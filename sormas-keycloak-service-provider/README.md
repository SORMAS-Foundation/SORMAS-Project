# Custom SORMAS Keycloak Service Provider

For more information about the Service Provider Interface see: <https://www.keycloak.org/docs/11.0/server_development/#_providers>

## SORMAS Password Hash Provider

This Service Provider is implementing in Keycloak a Hash Mechanism for passwords similar
to the one used in SORMAS to allow migrating of credentials for already existing users.

Since SORMAS and Keycloak are using different hashing techniques, the `SormasPasswordHashProvider` replicates the
SORMAS technique by importing the `sormas-api` dependency where the technique is defined.

### SORMAS Hashing Technique
*For more info about the SORMAS hashing technique see [PasswordHelper](../sormas-api/src/main/java/de/symeda/sormas/api/utils/PasswordHelper.java).*

In Keycloak this algorithm will be identifiable by the id `sormas-sha256`.

### Keycloak Hashing Technique

Keycloak supports a more configurable approach to password policy which can be customized for each system.
See [Password Policies](https://www.keycloak.org/docs/11.0/server_admin/#_password-policies).

### SORMAS User Password Sync

There are only 2 ways of synchronizing the user's password from SORMAS into Keycloak:
* whenever a user is created for the first time in Keycloak - being triggered from SORMAS
* whenever a user's password is updated in SORMAS, and the user doesn't have an email address setup

For any of the events about the user's credentials in Keycloak are overwritten by those from SORMAS, and the hashing
algorithm will be changed to `sormas-sha256`.

However once a user chooses to change their password in Keycloak (trough the *Forgot Password* mechanism or by the admin),
their credentials will be updated using the default or configured Password Policies from Keycloak.

## Deployment of the SPI

To deploy the Custom SPI, make sure to build this project and then follow the steps described in
[Register an SPI Using the Keycloak Deployer](https://www.keycloak.org/docs/11.0/server_development/#using-the-keycloak-deployer)
