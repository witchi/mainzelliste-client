/*
 * Copyright (c) 2010 René Brüntrup, Martin Lablans, Frank Ückert 
 * Licensed under the MIT X11 License (see LICENSE.txt).
 */

var authorizationConstructor = function(spec, my) {
	var that = {};
	var my = my || {};

	if (typeof(spec.monitorStatus) == 'undefined')
		spec.monitorStatus = false;
	if (spec.monitorStatus && !spec.monitorStatusIntervalMs)
		spec.monitorStatusIntervalMs = 60000;

	my.status = null;
	my.timeoutDate = null;

	var getStatus = function() {
		return my.status;
	};

	var getTimeoutDate = function() {
		return my.timeoutDate;
	};

	var login = function(username, password, callback) {
		var loginResult = function(result, textStatus) {
			if (textStatus === 'success' && result) {
				updateStatus(function() {
					if (callback) callback(true);
					$(that).trigger('loggedIn');
				});
			}
			else {
				spec.connection.ajax({
					url: 'auth/logout',
					complete: function() {
						updateStatus(function() {
							if (callback) callback(false, "serverError");
							$(that).trigger('loginFailed');
						});
					}
				});
			}
		};

		spec.connection.ajax({
			url: 'auth/login',
			dataType: 'json',
			data: {username: username, password: password},
			success: function(sessionId, textStatus) {
				if (!sessionId) {
					if (callback) callback(false, "wrongPassword");
					$(that).trigger('loginFailed');
					updateStatus();
					return;
				}

				spec.connection.addParam('sessionId', sessionId);

				if (typeof(spec.mdat) !== 'undefined') {
					spec.mdat.addParam('sessionId', sessionId);
					spec.mdat.ajax({
						url: 'auth/login',
						dataType: 'json',
						data: {sessionId: sessionId},
						success: loginResult,
						error: function(xhr, textStatus, errorThrown) {
							loginResult(false, 'success');
						}
					});
				} else
					loginResult(true, 'success');
			},
			error: function(xhr, textStatus, errorThrown) {
				loginResult(false, 'success');
			}
		});
	};

	var logout = function(callback) {
		spec.connection.ajax({
			url: 'auth/logout',
			dataType: 'json',
			success: function(result, textStatus) {
				spec.connection.removeParam('sessionId');
				if (spec.mdat)
					spec.mdat.removeParam('sessionId');
				if (callback) callback(result);
				$(that).trigger('loggedOut', result);
				updateStatus();
			},
			error: function(xhr, textStatus, errorThrown) {
				if (callback) callback(false);
				$(that).trigger('loggedOut', false);
				updateStatus();
			}
		});
	};

	var keepAlive = function() {
		if (my.keepAliveQueued)
			return;

		if (!spec.monitorStatus || (my.timeoutDate &&
				my.timeoutDate - new Date().getTime() < spec.monitorStatusIntervalMs))
			updateStatus(null, true);
		else
			my.keepAliveQueued = true;
	};

	var updateStatus = function(callback, sendKeepAlive) {
		spec.connection.ajax({
			url: 'auth/status',
			dataType: 'json',
			data: (sendKeepAlive || my.keepAliveQueued ? { keepAlive: true } : {}),
			success: function(data, textStatus, xhr) {
				my.status = data;
				if (data && data.timeoutInS)
					my.timeoutDate = new Date(new Date().getTime() + (data.timeoutInS * 1000));
				else
					my.timeoutDate = null;

				if (spec.monitorStatus && callback) callback(data);
				$(that).trigger('statusUpdate', data);
			},
			error: function(xhr, textStatus, errorThrown) {
				my.status = null; 
				if (spec.monitorStatus && callback) callback(null);
				$(that).trigger('statusUpdate', null);
			}
		});

		my.keepAliveQueued = false;
	};

	if (spec.monitorStatus) {
		setInterval(function() { updateStatus(); }, spec.monitorStatusIntervalMs);
		updateStatus();
	}

	// Build that.

	that.getStatus = getStatus;
	that.getTimeoutDate = getTimeoutDate;
	that.keepAlive = keepAlive;
	that.login = login;
	that.logout = logout;
	that.updateStatus = updateStatus;

	return that;
};
