/*
 * Copyright (c) 2010 René Brüntrup, Martin Lablans, Frank Ückert 
 * Licensed under the MIT X11 License (see LICENSE.txt).
 */

var webStorageTempIdCacheConstructor = function(spec, my) {
	var that = {};
	var my = my || {};
	var spec = spec || {};
	
	var storageKeyPath = "de.unimainz.imbei.dslib";
	
	spec.maxCacheAgeSecs = spec.maxCacheAgeSecs || (12 * 60),
	spec.maxCacheItems = spec.maxCacheItems || 1000;

	if (!readSession()) createSession();
	
	function readSession() {
		if (window.sessionStorage == null)
			return false;
		try {
			my.itemCount = Number(window.sessionStorage.getItem(storageKeyPath + ".itemCount"));
			my.subjects = JSON.parse(window.sessionStorage.getItem(storageKeyPath + ".subjects"));
			if (my.subjects == null)
				return false;
			return true;
		} catch (e) {// JSON parsing failed
			return false;
		}
	}
		
	function saveSession() {
		if (window.sessionStorage != null) {
			window.sessionStorage.setItem(storageKeyPath + ".subjects", JSON.stringify(my.subjects));
			window.sessionStorage.setItem(storageKeyPath + ".itemCount", my.itemCount);
		}
	}
	
	function createSession() {
		my.itemCount = 0;
		my.subjects = new Object();
	}
	
	clear = function() {
		my.subjects = new Object();
		my.itemCount = 0;
	}

	clearOld = function() {
		
		var now = new Date().getTime();
		var newItemCount = 0;
		// Subjects are intentionally not cleared. This would just be wasted time.
		for (var subjectName in my.subjects) {
			var newIds = new Object();
			var subject = my.subjects[subjectName];
			for (var itemName in subject) {
				if (subject[itemName].time < (now - (spec.maxCacheAgeSecs * 1000)))
					delete subject[itemName];
				else
					++newItemCount;
			}
		}
	}

	itemCount = function() {
	}

	getValue = function(subject, tempId) {
		
		if (typeof(my.subjects[subject]) === 'undefined')
			return null;
		if (typeof(my.subjects[subject][tempId]) === 'undefined')
			return null;

		return my.subjects[subject][tempId].value;
	};

	setValue = function(subject, tempId, value) {
		
		
		if (my.itemCount >= spec.maxCacheItems)
			clearOld();
		if (my.itemCount >= spec.maxCacheItems)
			clear();

		if (typeof(my.subjects[subject]) === 'undefined')
			my.subjects[subject] = new Object();
		if (typeof(my.subjects[subject][tempId]) === 'undefined')
			my.itemCount++;
		my.subjects[subject][tempId] = {
			time: new Date().getTime(),
			value: value
		};
		
		saveSession();
	};
	
	/**
	 * Needed to invalidate a cache item.
	 */
	clearValue = function(subject, tempId) {
		// if no subject is given, delete all items for this Temp-ID
		if (subject === null) {
			for (var subjectName in my.subjects) {
				delete my.subjects[subjectName][tempId];
			}
		} else {
			delete my.subjects[subject][tempId];
		}
		saveSession();
	};


	// Building that.
	that.getValue = getValue;
	that.setValue = setValue;
	that.clearValue = clearValue;

	return that;
};
