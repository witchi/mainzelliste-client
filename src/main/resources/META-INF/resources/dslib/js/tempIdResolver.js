/*
 * Copyright (c) 2010 René Brüntrup, Martin Lablans, Frank Ückert 
 * Licensed under the MIT X11 License (see LICENSE.txt).
 */

var tempIdResolverConstructor = function(spec, my) {
	var that = {};
	var my = my || {};
	var spec = spec || {};

	my.resolvers = my.resolvers || [];

	if (typeof(spec.useCache) === 'undefined')
		spec.useCache = true;
	if (spec.useCache) {
		// use HTML5 web storage if available
		if(typeof(Storage)!=="undefined") {
			my.cache = webStorageTempIdCacheConstructor({
				maxCacheAgeSecs: spec.maxCacheAgeSecs,
				maxCacheItems: spec.maxCacheItems
		});
		} else {
			my.cache = tempIdCacheConstructor({
				maxCacheAgeSecs: spec.maxCacheAgeSecs,
				maxCacheItems: spec.maxCacheItems
			});			
		}
	}

	spec.defaultResolveFunctionName = spec.defaultResolveFunctionName || 'resolveTempIds';
	if (spec.useDefaultResolver === 'mainzellisteResolver') 
	{
		spec.useDefaultResover = true;
		my.defaultResolver = mainzellisteResolverConstructor({
			mainzellisteURL: spec.mainzellisteURL});
	} else if (typeof(spec.useDefaultResolver) === 'undefined')
	{
		spec.useDefaultResolver = true;
		my.defaultResolver = defaultResolverConstructor({
			resolveFunctionName: spec.defaultResolveFunctionName});
	} 

	addSubjectResolver = function(subject, resolveFunction) {
		my.resolvers[subject] = resolveFunction;
	};

	defaultResolver = function(subject, tempId, callback) {
		$.get(spec.defaultResolveFunctionName, {subject: subject, tempId: tempId},
			function(data, textStatus, xhr) {
				if (callback) callback(data);
			}
		);
	};

	// success: Function, which will be called after all items have been resolved
	resolve = function(success) {		
		my.nToResolve = 0; // Reset counter of elements still to resolve		
		my.success = success;
	
		my.defaultResolver.startInput(); // Initialisierung
		// Durchlaufe alle Elemente mit data-subject-Attribut
		$('*[data-subject]').each(function(index, element) {
			var subject = element.getAttribute('data-subject');
			var tempId= element.getAttribute('data-tempid');

			// Falls der Wert im Cache ist, schreibe ihn direkt in das Element
			var value = my.cache.getValue(subject, tempId);
			if (value !== null) {
				insertResolvedValue(element, value);
				return;
			}

			my.nToResolve++; // Increment number of elements still to resolve

			// Ansonsten: Resolver bestimmen
			var resolver = my.resolvers[subject];
			if (typeof(resolver) === 'undefined' && spec.useDefaultResolver)
				resolver = my.defaultResolver;
			/*
			 * Der Aufruf sorgt dafür, dass das subject-tempid-Paar an eine Liste
			 * zu "erledigender" Werte angehängt wird. Mit übergeben wird eine 
			 * Callback-Funktion, die den aufgelösten Wert in den Cache und 
			 * in  das HTML-Element schreibt.
			 */
			resolver.resolve(subject, tempId,
				{subject: subject, tempId: tempId, element: element},
				function(value, data) {
					if (my.cache)
						my.cache.setValue(data.subject, data.tempId, value);
					insertResolvedValue(data.element, value);
					// decrement number of elements to resolve
					my.nToResolve--;
					// Call success function if done
					if (my.nToResolve == 0 && typeof(my.success) === 'function')
						my.success();
				});
		});
		
		// Für data-formatted hier weiter
		$('*[data-formatted]').each(function(index, element) {
			
			// Formatstring parsen: Array subject->tempid (oder andersherum) erzeugen
			// Callback muss sich merken, was er braucht
			// Dazu Cache erforderlich. Interner Cache möglich? Evtl. so etwas wie Liste zum Abhaken
			
		});
		
		// If number of elements to resolve is still 0, everything is done.
		// This works because the code above that decrements the counter
		// is not executed until endInput() is called.
		if (my.nToResolve == 0 && typeof(my.success) === 'function')
			my.success();
		
		/* Mit folgendem Aufruf wird die Liste der aufzulösenden subject-tempid-Paare
		 * abgearbeitet, was den Aufruf der oben definierten Callback-Funktion
		 * beinhaltet.
		 */
		my.defaultResolver.endInput();
	};

	insertResolvedValue = function(element, value) {
		// Gegebenenfalls benutzerdefinierte Postprocessing-Funktion aufrufen
		var postProcess = element.getAttribute('data-postProcess');
		if (typeof window[postProcess] == 'function')
			value = window[postProcess](value);
		$(element).filter(':input').val(value); 
		$(element).filter(':not(:input)').html(value);
	}
	
	/**
	 * Invalididates cached subject.
	 */
	invalidate = function(subject, tempId) {
        my.cache.clearValue(subject, tempId);
    }


	cachesize = function() {
		console.log(my.cache.itemCount());
	}

	// Building that.
	that.addSubjectResolver = addSubjectResolver;
	that.defaultResolver = defaultResolver;
	that.resolve = resolve;
	that.invalidate = invalidate;
	that.cachesize = cachesize;

	return that;
};
