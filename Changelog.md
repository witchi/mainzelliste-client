# Changelog
### Unreleased
#### New features
- It is now possible to add audittrail information to tokens
#### Refactor
- `MainzellisteNetworkException` now provide the mainzelliste error response, tha cause the exception
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
