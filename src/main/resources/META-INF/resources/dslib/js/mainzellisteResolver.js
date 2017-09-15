/*
 * Temp-ID-Resolver for use with Mainzelliste. 
 * Written by Andreas Borg based on defaultResolver.js
 * (Original copyright note below) 
 *  
 * Copyright (c) 2010 René Brüntrup, Martin Lablans, Frank Ückert 
 * Licensed under the MIT X11 License (see LICENSE.txt).
 * 
 */

var mainzellisteResolverConstructor = function(spec, my) {
	var that = {};
	var my = my || {};
	var spec = spec || {};

	resolve = function(subject, tempId, data, callback) {
		if (!my.tempIds[tempId])
			my.tempIds[tempId] = {};
		if (!my.tempIds[tempId][subject])
			my.tempIds[tempId][subject] = [];
		my.tempIds[tempId][subject].push({ callback: callback, data:data });
	};

	/* 
	 * Encapsulated in a function so that tempId is a local variable
	 * and not altered by the surrounding loop.
	 * See http://stackoverflow.com/questions/2687679/jquery-ajax-inside-a-loop-problem
	 */
	doMainzellisteRequest = function(tempId) {
		//Detect browser support for CORS
		if ('withCredentials' in new XMLHttpRequest()) {
			$.ajax({
				url: spec.mainzellisteURL + "/patients",
				dataType: 'json',
				data: {tokenId: tempId},
				success: function(data) {
					for (var subject in my.tempIds[tempId]) {
						var subjectData = my.tempIds[tempId][subject];
						for (var i = 0; i < subjectData.length; i++) {
							// Check if subject means a field
							if (subject in data[0].fields) {
								subjectData[i].callback(data[0].fields[subject], subjectData[i].data);
							} else {
								// Otherwise check if there is an ID type that matches the subject
								var idArray = data[0].ids;
								for (var j = 0; j < idArray.length; j++) {
									var id = idArray[j];
									if (id.idType == subject) {
										subjectData[i].callback(id.idString, subjectData[i].data);
									}
								}
							}
						}
					}
				},
				error: function(request, textStatus, errorThrown) {
					// TODO: Fehlerprotokoll o.ä., so dass der Benutzer einen
					// Hinweis hat, aber nicht mit Meldungen bombardiert wird.
				}
			});
		// Use proprietary XDomainRequest in IE 8 and 9
		} else if(typeof XDomainRequest !== "undefined"){
			var xdr = new XDomainRequest();
			xdr.open("get", spec.mainzellisteURL + "/patients/tokenId/" + tempId);
			
			xdr.onload = function() {
				var data = jQuery.parseJSON(xdr.responseText);
				for (var subject in my.tempIds[tempId]) {
					var subjectData = my.tempIds[tempId][subject];
					for (var i = 0; i < subjectData.length; i++) {
						// Check if subject means a field
						if (subject in data[0].fields) {
							subjectData[i].callback(data[0].fields[subject], subjectData[i].data);
						} else {
							// Otherwise check if there is an ID type that matches the subject
							var idArray = data[0].ids;
							for (var j = 0; j < idArray.length; j++) {
								var id = idArray[j];
								if (id.idType == subject) {
									subjectData[i].callback(id.idString, subjectData[i].data);
								}
							}
						}
					}
				}
			};
			
			/* 
			 * Setting empty event handlers and wrapping the send() call in setTimeout
			 * supposedly prevents an IE bug 
			 * (see cypressnorth.com/programming/internet-explorer-aborting-ajax-requests-fixed/) 
			 */ 
			xdr.onprogress = function () { };
			xdr.ontimeout = function () { };
			xdr.onerror = function () { };			 
			setTimeout(function () {
				xdr.send("tokenId=" + tempId);
			}, 0);			
		}
	}

	endInput = function() {
		for (var tempId in my.tempIds) {
			doMainzellisteRequest(tempId);
		}
	};

	startInput = function() {
		my.tempIds = {};
	};

	// Building that.
	that.endInput = endInput;
	that.resolve= resolve;
	that.startInput = startInput;

	return that;
};
