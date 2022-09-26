# Keycloak

Open Source Identity and Access Management.
In SORMAS Keycloak is available as an alternative authentication provider to the default authentication method.

Current version is: Keycloak 16.1.0

## Setup

To set up Keycloak check the guide here [Keycloak Setup](../../docs/SERVER_SETUP.md#keycloak-server)

## SORMAS Realm

The SORMAS Realm in Keycloak contains all the configuration which are specific to the SORMAS Project.
All the configuration is part of the `setup/keycloak/SORMAS.json` file.

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
* **OTP** is supported by default through the *Google Authenticator* or *Free OTP* by has to be activated from the
  Keycloak Admin console
* **Forgot Password** is enabled by default
* **sormas-sha256** is an encryption algorithm which comes packaged with Keycloak to support transition of existing
  environments towards the Keycloak Authentication Provider

#### Clients

The SORMAS Realm relies on the following clients:

* **sormas-ui** - handles access to the SORMAS wen UI
* **sormas-app** - handles access to the SORMAS Android App
* **sormas-rest** - handles access to the SORMAS API
* **sormas-backend** - handles SORMAS server requests
* **sormas-stats** - handles access to the external SORMAS-Stats App

##### How to debug

How to obtain an access token to impersonate a certain client:

```bash
curl --location --request POST 'http://localhost:8081/keycloak/realms/SORMAS/protocol/openid-connect/token' 
--header 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'client_id=sormas-stats' 
--data-urlencode 'client_secret=changeit' --data-urlencode 'grant_type=password' -d "username=admin" 
-d "password=1234abdcefHAH\!asd"
{"access_token":"SOME_TOKEN",...}
```

Use this token to access a certain endpoint:

```bash
curl -X POST http://localhost:8081/keycloak/realms/SORMAS/protocol/openid-connect/userinfo 
-H 'Authorization: Bearer SOME_TOKEN'
{"sub":"399cac62-1b05-45aa-b02c-7ea0a240f144","resource_access":{"sormas-stats":{"roles":["sormas-stats-access"]},
"account":{"roles":["manage-account","manage-account-links","view-profile"]}},"email_verified":false,"name":"ad min",
"preferred_username":"admin","given_name":"ad","family_name":"min"}
```

##### sormas-backend client in Keycloak

This client is used to allow the SORMAS backend to access the Keycloak server.
It is configured as a confidential client, which means that it has a secret that is used to authenticate the client
to the OIDC server. This is based on the client credentials grant type, which is configured via the associated
**Service Account**. Currently, the following roles are assigned to the service account:
```json
{
  "clientRoles": {
    "realm-management": [
      "manage-realm",
      "manage-users",
      "manage-clients"
    ]
  }
}
```

##### sormas-stats client in Keycloak

This client defines a `sormas-stats-access` client role. Once assigned to a user, it can access `sormas-stats`.
The client scope `client roles` mapper adds the roles to the `userinfo` endpoint, which is queried by the Apache2
OIDC module to determine the user's roles.


#### Roles

The role management is handled solely by SORMAS starting with 1.70.

#### Email

Email configurations are optional and are not part of the default configuration.

In case the system relies on users activating their own accounts it's required to configure these settings.

#### Audit Logging
Audit logging of all login activity can be done by setting `org.keycloak.events` to `DEBUG`. To enable this, please copy
the files from `sormas-base/setup/keycloak/audit-logging/` to `/opt/jboss/startup-scripts/` inside your Keycloak server
and restart the server.

#### Custom Configuration

The configuration provided by default is the minimum required configuration for Keycloak to work together with SORMAS.

The Keycloak configuration can be adjusted by any user with admin rights, however **beware** that any change to the default
configuration might render the system unusable.

The following configurations are most likely to be environment specific:

* **[Email Settings](https://www.keycloak.org/docs/16.1/server_admin/#_email)**
  * make sure to set an email for the admin user, so the **Test connection** feature works
* **[Password Policies](https://www.keycloak.org/docs/16.1/server_admin/#_password-policies)**
  * The **Password Blacklist** policy can only be configured with access to the host machine
* **[OTP Policies](https://www.keycloak.org/docs/16.1/server_admin/#otp-policies)**
  * Can be activated by default for all user by marking **Basic Auth Password+OTP** as required in the
    **Authentication>Flows** section, then mark it as default in the **Authentication>Required** section
