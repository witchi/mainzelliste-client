/*
 * Copyright (c) 2010 René Brüntrup, Martin Lablans, Frank Ückert
 * Licensed under the MIT X11 License (see LICENSE.txt).
 */
 
$(document).ready(function(){
	var reso = tempIdResolverConstructor({ defaultResolveFunctionName: conf['baseUrlTempIdResolver'] });
	reso.resolve();
});