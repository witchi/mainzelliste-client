# Mainzelliste.Client

## General information
Mainzelliste (see <http://www.mainzelliste.de>) is a web-based patient list and pseudonymization service. It provides a RESTful interface for client applications like electronic data capture (EDC) systems. 

Mainzelliste-Client handles the HTTP calls necessary for using Mainzelliste from a Java application. It has been released as part of the Open Source Registry System for Rare Diseases in the EU (see <http://osse-register.de>).

## Release notes

### 2.0.0

This release uses Mainzelliste API 3.0. Because the semantics of the editPatient token have changed, this release is not
generally backwards compatible. Specifically, this affects cases where the editPatient token is used without specifying
the fields that should be editable: In API 2.x and below, this means that all fields are editable. Starting with API
3.0, it means that no field at all is editable, i.e. all editable fields have to be explicitely defined in data item
`fields`.

#### New features

- AddPatientToken and EditPatientToken can handle externally generated IDs in accordance with Mainzelliste API 3.0.

#### Bug fixes

- `EditPatientToken#setFieldsToEdit()` and `EditPatientToken#redirect()` failed when being called with null argument.

### 1.1.0

- Added JavaScript code for loading identifying data into the user's web browser.

### 1.0.2

####Bug fix

- Fields added by AddPatientToken#addField did not appear in output of AddPatientToken#toJSON.

####Other changes

- Removed debug messages.
- Removed obsolete test class.

### 1.0.1

Bug fix: Header for API version was spelled incorrectly.

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
	<version>1.1.0</version>
</dependency>
```

## Copyright and License
Mainzelliste-Client has been released as part of OSSE and is thus licensed under the same conditions:


```
#!text
Copyright (C) 2015 Working Group on Joint Research,
Division of Medical Informatics,
Institute of Medical Biometrics, Epidemiology and Informatics,
University Medical Center of the Johannes Gutenberg University Mainz

Contact: info@osse-register.de

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU Affero General Public License as published by the Free 
Software Foundation; either version 3 of the License, or (at your option) any
later version.

This program is distributed in the hope that it will be useful, but WITHOUT 
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
details.

You should have received a copy of the GNU Affero General Public License 
along with this program; if not, see <http://www.gnu.org/licenses>.

Additional permission under GNU GPL version 3 section 7:

If you modify this Program, or any covered work, by linking or combining it 
with Jersey (https://jersey.java.net) (or a modified version of that 
library), containing parts covered by the terms of the General Public 
License, version 2.0, the licensors of this Program grant you additional 
permission to convey the resulting work.
 
```