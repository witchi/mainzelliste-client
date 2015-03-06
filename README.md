# Mainzelliste-Client

## General information
Mainzelliste (see <http://www.mainzelliste.de>) is a web-based patient list and pseudonymization service. It provides a RESTful interface for client applications like electronic data capture (EDC) systems. 

Mainzelliste-Client handles the HTTP calls necessary for using Mainzelliste from a Java application. It has been released as part of the Open Source Registry System for Rare Diseases in the EU (see <http://www.osse-register.de>).

## Release notes

### 1.0
Initial release.

## Build

Use maven to build the jar:

``` 
mvn clean package
```

Use it as a dependency:

```xml
<dependency>
	<groupId>de.pseudonymisierung</groupId>
	<artifactId>mainzelliste-client</artifactId>
	<version>1.0.0</version>
</dependency>
```

## Copyright and License
Copyright (C) 2015 Working Group on Joint Research, University Medical Center Mainz Contact: info@osse-register.de
 
 This program is free software; you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program; if not, see
<http://www.gnu.org/licenses>.