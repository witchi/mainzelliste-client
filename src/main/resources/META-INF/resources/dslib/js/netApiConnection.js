/*
 * Copyright (c) 2010 René Brüntrup, Martin Lablans, Frank Ückert 
 * Licensed under the MIT X11 License (see LICENSE.txt).
 */

var netApiConnectionConstructor = function(spec, my) {
	var that = {};
	var my = my || {};
	my.options = my.options || {};
	my.isAvailable = (typeof(spec.isAvailable) !== 'undefined' ? spec.isAvailable : true);

	if (typeof(spec.monitorConnection) == 'undefined')
		spec.monitorConnection = false;
	if (typeof(spec.requestType) == 'undefined')
		spec.requestType = 'auto';
	my.requestTypeUsed = spec.requestType;

	my.options.timeout = spec.timeout || 10000;

	// Core ajax functions.

	var ajax = function(options) {
		var error_xhr = null;
		var error_textStatus = null;

		var myUrl = (options.url ? (spec.baseUrl + options.url) : (spec.baseUrl + my.options.url)); 
		if (my.params)
			myUrl = appendParams(myUrl, my.params);

		var myOptions = {
			url: myUrl,
			contentType: options.contentType || null,
			dataType: options.dataType || my.options.dataType,
			data: options.data || my.options.data,
			headers: options.headers || my.options.headers,
			timeout: options.timeout || my.options.timeout,
			type: options.type || 'GET',
			xhrFields: { withCredentials: true },
			success: function(data, textStatus, xhr) {
				if (typeof(options.checkConnection) === 'undefined' || options.checkConnection === true)
					checkConnection(true);
				if (data && data.__HTTP_ERROR__) {
					error_xhr = {
						byScriptTag: true,
						responseText: data.__HTTP_ERROR__.msg,
						status: data.__HTTP_ERROR__.httpStatusCode,
						statusText: data.__HTTP_ERROR__.httpStatusMsg
					};
					error_textStatus = 'error';
					if (options.error)
						options.error(error_xhr, error_textStatus, null);
				}
				else if (options.success)
					options.success(data, textStatus, xhr);
			},
			error: function(xhr, textStatus, errorThrown) {
				if (typeof(options.checkConnection) === 'undefined' || options.checkConnection === true)
					checkConnection(false);
				if (options.error)
					options.error(xhr, textStatus, errorThrown);
			},
			complete: function(xhr, textStatus) {
				if (options.complete)
					options.complete(error_xhr || xhr, error_textStatus || textStatus);
			}
		};

		my.requestTypeUsed = 'auto';
		switch (my.requestTypeUsed) {
			case 'auto':
				var originalErrorFnc = myOptions.error;
				var originalSuccessFnc = myOptions.success;

				var xhrFailed = function(xhr, textStatus, errorThrown) {
					if (!jQuery.browser.msie || !window.XDomainRequest) {
						originalErrorFnc(xhr, textStatus, errorThrown);
						return;
					}

					var xdr = new XDomainRequest();
					if (myOptions.type === 'GET') {
						if (typeof myOptions.data == 'string')
							throw 'String data for get request is not supported.';
						else
							myOptions.url = appendParams(myOptions.url, myOptions.data);
					}
					if (myOptions.type !== 'GET' && myOptions.type !== 'POST')
						myOptions.url = appendParams(myOptions.url, { 'XDomainRequestMethod': myOptions.type });
					var xdrType = (myOptions.type === 'GET' ? 'GET' : 'POST');
					xdr.open(xdrType, myOptions.url);
					xdr.onload = function() {
						var dataType = myOptions.dataType;
						if (!dataType)
							switch (this.contentType) {
								case 'application/json': dataType = 'json'; break;
								case 'text/html': dataType = 'html'; break;
								case 'text/plain': dataType = 'text'; break;
								case 'application/xml': dataType = 'xml'; break;
							}

						var response = this.responseText;
						switch (dataType) {
							case 'json': response = JSON.parse(response); break;
							case 'html': break;
							case 'text': break;
							case 'xml': response = jQuery.parseXML(response); break;
						}
						originalSuccessFnc(response, 'success');
					};
					xdr.onerror = function() { originalErrorFnc(); };
					xdr.ontimeout = function() { originalErrorFnc(); };
					try {
						if (xdrType === 'GET')
							xdr.send();
						else {
							var body = (typeof myOptions.data == 'string' ? 
								myOptions.data : JSON.stringify(myOptions.data));
							xdr.send(body);
						}
					} catch (e) {
						originalErrorFnc();
					};
				};

				myOptions.error = xhrFailed;

				try {
					$.ajax(myOptions);
				} catch (e) {
					xhrFailed();
				}

				break;
			default:
				throw "Unknown request type '" + my.requestTypeUsed + "'";
		}
	};

	var ajaxSetup = function(options) {
		my.defaultOptions = options;
	};

	// Request functions.
	
	var getJSON = function(url, param1, param2) {
		var callback = null, data = null;
		if (typeof(param1) == 'function') {
			callback = param1;
			if (typeof(param2) != 'undefined')
				throw new 'No parameters expected after callback.';
		}
		else
			data = param1;
		if (typeof(param2) == 'function')
			callback = param2;

		ajax({
			url: url,
			dataType: 'json',
			data: data,
			success: callback
		});
	};

	var load = function(dest, url, param1, param2) {
		var callback = null, data = null;
		if (typeof(param1) == 'function') {
			callback = param1;
			if (typeof(param2) != 'undefined')
				throw new 'No parameters expected after callback.';
		}
		else
			data = param1;
		if (typeof(param2) == 'function')
			callback = param2;

		ajax({
			url: url,
			dataType: 'json',
			data: data,
			success: function(data, textStatus, xhr) {
				$(dest).html(data);
			},
			complete: function(xhr, textStatus) {
				if (callback)
					callback(xhr, textStatus);
			}
		});
	};

	// Status handling.

	function checkConnection(requestResult) {
		if ((spec.monitorConnection !== true) ||
			(typeof(requestResult) !== 'undefined' && my.isAvailable === requestResult))
			return;

		// We always double check, if the state seems to have changed.
		var options = {
			checkConnection: false,
			url: 'ping',
			dataType: 'text',
			timeout: 10000,
			success: function(data, textStatus, xhr) {
				if (my.isAvailable != true) {
					my.isAvailable = true;
					$(that).trigger('available');
				}
			},
			error: function(xhr, textStatus, errorThrown) {
				if (my.isAvailable != false) {
					my.isAvailable = false;
					$(that).trigger('unavailable');
				}
			}
		};

		ajax(options);
	}

	var isAvailable = function() {
		return my.isAvailable;
	};

	if (spec.monitorConnection) {
		setInterval(function() { checkConnection(); }, 60000);
		checkConnection();
	}

	// Misc functions

	var addParam = function(key, value) {
		if (!my.params)
			my.params = {};
		my.params[key] = value;
	};

	var removeParam = function(key) {
		if (!my.params || typeof my.params[key] === 'undefined')
			return false;
		delete my.params[key];
		return true;
	};

	// Building that.
	that.ajax = ajax;
	that.ajaxSetup = ajaxSetup;
	that.getJSON = getJSON;
	that.isAvailable = isAvailable;
	that.load = load;
	that.addParam = addParam;
	that.removeParam = removeParam;

	var appendParams = function(url, params) {
		if (!params)
			return url;

		var firstPart = url + '?';
		var lastPart = '';

		var qmPos = url.lastIndexOf('?');
		if (qmPos !== -1) {
			firstPart = url.substr(0, qmPos + 1);
			lastPart = url.substr(qmPos + 1);
		}

		var paramsString = $.param(params);
		return firstPart + paramsString + (lastPart ? '&' + lastPart : '');
	};

	return that;
};
