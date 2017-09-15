/*
 * Copyright (c) 2010 René Brüntrup, Martin Lablans, Frank Ückert 
 * Licensed under the MIT X11 License (see LICENSE.txt).
 */

var defaultResolverConstructor = function(spec, my) {
	var that = {};
	var my = my || {};
	var spec = spec || {};

	spec.maxIdsInRequest = spec.maxIdsInRequest || 20; 
	
	resolve = function(subject, tempId, data, callback) {
		if (!my.subjects[subject])
			my.subjects[subject] = {};
		if (!my.subjects[subject][tempId])
			my.subjects[subject][tempId] = [];
		my.subjects[subject][tempId].push({ callback: callback, data:data });
	};

	endInput = function() {
		/* Collect subjects and Temp-IDs to resolve. The data is split into
		 * blocks of at most spec.maxIdsInRequest Temp-IDs in order to avoid
		 * too long URLs (requests are made with GET).
		 */
		var tempIds = 0; // total number of Temp-IDs
		var tempIdsInBlock = 0; // number of Temp-IDs in the current block (=request)
		var block = 0; // counts current block 
		var callParamBlocks = []; // array of request objects (=blocks)
		var callParam;
		for (var subjectName in my.subjects) {
			var subject = my.subjects[subjectName];
			for (var tempId in subject) {
				// if block if full, save to array and start a new one
				if (tempIdsInBlock >= spec.maxIdsInRequest) {
					callParamBlocks.push(callParam);
					++block;
					tempIdsInBlock = 0;
				}
				// initialize request data for each new block
				if (tempIdsInBlock == 0) {
					callParam = { subjects: {} };
				}
				if (!callParam.subjects[subjectName])
					callParam.subjects[subjectName] = [];
				callParam.subjects[subjectName].push(tempId);
				++tempIds;
				++tempIdsInBlock;
			}
		}
		// save block of remaining Temp-IDs
		if (tempIdsInBlock > 0)
			callParamBlocks.push(callParam);

		if (tempIds === 0)
			return;
		// loop over blocks and make request for each of them
		for (var callParamIndex in callParamBlocks) {
			$.getJSON(spec.resolveFunctionName, {data: JSON.stringify(callParamBlocks[callParamIndex])}, function(data, textStatus, xhr) {
				for (var subjectName in my.subjects) {
					var subject = my.subjects[subjectName];
					for (var tempId in subject) {
						if (typeof(data[subjectName]) !== 'undefined' && typeof(data[subjectName][tempId]) !== 'undefined')							
							for (var i = 0; i < subject[tempId].length; ++i)
								subject[tempId][i].callback(data[subjectName][tempId], subject[tempId][i].data);
					}
				}
			});
		};
	}
	startInput = function() {
		my.subjects = {};
	};

	// Building that.
	that.endInput = endInput;
	that.resolve= resolve;
	that.startInput = startInput;

	return that;
};
