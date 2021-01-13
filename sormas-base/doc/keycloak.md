# Keycloak

Open Source Identity and Access Management.
In SORMAS Keycloak is available as an alternative authentication provider to the default authentication method.

Current version is: Keycloak 12

## Setup

To set up Keycloak check the guide here [Keycloak Setup](../../SERVER_SETUP.md#keycloak-server)

## SORMAS Realm

The SORMAS Realm in Keycloak contains all the configuration which are specific to the SORMAS Project.
All the configuration is part of the [SORMAS.json](../setup/keycloak/SORMAS.json) file.

### Configuration summary

#### Login & Authentication

* **Duplicate emails** are allowed in order to support the same requirement as SORMAS which in some installations require 
admin support for some users, in which case the admin will use her own email address
* **No login with emails** due to the previous point
* **Password Policy** comes predefined since version 1.54 of the SORMAS-Project with the following default settings
  * Length of minimum 12 characters
  * At least 1 upper case letter
  * At least 1 lower case letter
  * At least 1 digit
  * At least 1 special character
* **OTP** is supported by default trough the *Google Authenticator* or *Free OTP* by has to be activated from the 
  Keycloak Admin console
* **Forgot Password** is enabled by default
* **sormas-sha256** is an encryption algorithm which comes packaged with Keycloak to support transition of existing 
  environments towards the Keycloak Authentication Provider
  
#### Clients

The SORMAS Realm relies on 4 clients:

* **sormas-ui** - handles access to the SORMAS wen UI
* **sormas-app** - handles access to the SORMAS Android App
* **sormas-rest** - handles access to the SORMAS API
* **sormas-backend** - handles SORMAS server requests

#### Roles

The role management is handled by SORMAS however as a pre-validation Keycloak is also configured with a few roles which 
are required for certain API access:
* **USER** - required by default for any API access
* **REST_USER** - required for most API endpoints (main purpose is for the SurvNet converter)
* **REST_EXTERNAL_VISITS_USER** - required by Symptom Journals which are connected to SORMAS
* **SORMAS_TO_SORMAS_CLIENT** - required by other SORMAS instance to access the current SORMAS instance

#### Email

Email configurations are optional and are not part of the default configuration.

In case the system relies on users activating their own accounts it's required to configure these settings.

#### Custom Configuration

The configuration provided by default is the minimum required configuration for Keycloak to work together with SORMAS.

The Keycloak configuration can be adjusted by any user with admin rights, however **beware** that any change to the default 
configuration might rend the system unusable.

The following configurations are most likely to be environment specific:

* **[Email Settings](https://www.keycloak.org/docs/12.0/server_admin/#_email)**  
  * make sure to set an email for the admin user, so the **Test connection** feature works
* **[Password Policies](https://www.keycloak.org/docs/latest/server_admin/#_password-policies)**
  * The **Password Blacklist** policy can only be configured with access to the host machine
* **[OTP Policies](https://www.keycloak.org/docs/latest/server_admin/#otp-policies)**
  *  Can be activated by default for all user by marking **Basic Auth Password+OTP** as required in the 
     **Authentication>Flows** section, then mark it as default in the **Authentication>Required** section
