# Sormas2Sormas Setup

## Introduction

Sormas2Sormas (or S2S for short) is a feature that allows sharing of entities (e.g., cases, contacts, events, etc.)
between two or more SORMAS instances.

## Components

### SORMAS Instance

A SORMAS instance is a running instance of the SORMAS application. A SORMAS instance which is configured to participate
in S2S is obviously the main component of the Sormas2Sormas feature. It is the one that initiates the sharing process
and the one that receives the shared entities. Each instance has a unique S2S identifier which is an arbitrary string.
We do not enforce a certain scheme, but it is strongly encouraged to use a meaningful and consistent identifiers
(e.g., `2.sormas.id.sormas_a` in Germany which is the dedicated SurvNet ID for the public health office).

#### Server Descriptors

A server descriptor is a struct which contains the information needed to connect to a SORMAS instance (see the
`SormasServerDescriptor` class). It consists of the S2S ID, a human-readable name used in the UI, and the hostname.
Descriptors centrally distributed via a dedicated `etcd` keyspace (e.g., `/s2s/`).

```json
{
  "id":"2.sormas.id.sormas_a",
  "name":"sormas_a_org_name",
  "hostName":"sormas_a:6080"
}
```

#### Keystore

Each instance receives a dedicated x509 certificate and private key pair which is used to encrypt all S2S communication.
The certificate is signed by a common S2S CA. The certificate and private key are stored in a `pkcs12` keystore under the
S2S ID of the instance. See [here](https://github.com/sormas-foundation/S2S-Testbed/blob/main/docker/certs/sormas_a/generate-cert-a.sh)
for an example of how to generate a keystore.

#### Truststore

Each instance receives a truststore which contains the S2S CA certificate under the `sormas2sormas.rootCaAlias`.
See [here](https://github.com/sormas-foundation/S2S-Testbed/blob/main/docker/certs/ca/generate_ca.sh) for an example of
how to generate a truststore.

#### Properties

The following properties are required to be set in the `sormas.properties` file:

| Property                                         | Description                                                                                                                                            |
|--------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| `central.oidc.url`                               | URL of the OIDC server authenticating instances and authorizing S2S requests.                                                                          |
| `central.etcd.host=`                             | The hostname of the etcd instance providing data like server descriptors.                                                                              |
| `central.etcd.clientName`                        | The client name of the instance used in authentication towards etcd.                                                                                   |
| `central.etcd.clientPassword`                    | The client password of the instance used in authentication towards etcd.                                                                               |
| `central.etcd.caPath`                            | The full filesystem path to the CA trusted by etcd clients when creating a TLS connection to the etcd server.                                          |
| `central.location.sync`                          | If set to true, all infrastructure data from the central server will be synchronized into the local SORMAS database at (re)start and on a daily basis. |
| `sormas2sormas.path`                             | Filesystem path to a directory where certificates and files related to SORMAS2SORMAS are stored.                                                       |
| `sormas2sormas.id=`                              | The S2S ID of this instance. The identifier can be arbitrary, but should be meaningful.                                                                |
| `sormas2sormas.keystoreName`                     | Name of the key store file relative to `sormas2sormas.path`.                                                                                           |
| `sormas2sormas.keystorePass`                     | Password of the key store file.                                                                                                                        |
| `sormas2sormas.rootCaAlias`                      | The alias of the trusted root CA which secures the S2S communication. This is NOT used for TLS.                                                        |
| `sormas2sormas.truststoreName`                   | Name of the truststore file relative to `sormas2sormas.path`.                                                                                          |
| `sormas2sormas.truststorePass`                   | Password of the truststore.                                                                                                                            |
| `sormas2sormas.oidc.realm`                       | Name of our S2S authorization realm provided by the OIDC server.                                                                                       |
| `sormas2sormas.oidc.clientId`                    | The client ID used in authentication of the instance towards the OIDC server.                                                                          |
| `sormas2sormas.oidc.clientSecret`                | The client secret used in authorization of the instance towards the OIDC server.                                                                       |
| `sormas2sormas.etcd.keyPrefix`                   | The etcd key space prefix which is used to store s2s related information.                                                                              |
| `sormas2sormas.ignoreProperty.additionalDetails` | Controls whether the value in `additionalDetails` field should be sent or not to the other instances. Possible values: true/false(default).            |
| `sormas2sormas.ignoreProperty.externalId`        | Controls whether the value in `externalId` field should be sent or not to the other instances. Possible values: true/false(default).                   |
| `sormas2sormas.ignoreProperty.externalToken`     | Controls whether the value in `externalToken` field should be sent or not to the other instances.  Possible values: true/false(default).               |
| `sormas2sormas.ignoreProperty.internalToken`     | Controls whether the value in `internalToken` field should be sent or not to the other instances.  Possible values: true/false(default).               |
| `#sormas2sormas.districtExternalId`              | External ID of the district to which the cases/contacts to be assigned when accepting a share request                                                  |


### Keycloak

Keycloak is an open source identity and access management solution. It is used to manage the users and their permissions.
In our case, we use the [OIDC client credentials flow](https://auth0.com/docs/get-started/authentication-and-authorization-flow/client-credentials-flow)
to authenticate the S2S enabled instances and to authorize the S2S share operations. It consists of a dedicated S2S
realm which contains all the necessary configurations, namely dedicated clients and client scopes. Connections to Keycloak
must be protected by TLS.

#### Clients

Each S2S instance receives a dedicated client in the S2S realm. An S2S instance is acting as client and is configured to
use the OIDC client credentials.

#### Client Scopes

For each client, a similar client scope is created. Each S2S instance `A` which should be able to send data to another
instance `B` must be assigned the client scope of instance `B`. This way, instance `A` is allowed to send data to instance `B`.
The client scope _must_ be prefixed with `s2s-` and _must_ contain the S2S ID of the instance it is assigned to (e.g.,
`s2s-2.sormas.id.sormas_b`).


### etcd

#### Server Descriptors stored in etcd

etcd is a distributed key-value store. It is used to store the server descriptors of all S2S enabled instances. They are
all stored under a dedicated S2S key space prefix (e.g., `/s2s/`) at a path like `/s2s/2.sormas.id.sormas_b`.
The etcd server is expected to be secured by TLS with a trusted CA announced to instances via `central.etcd.caPath`.
The etcd client name and password are used to authenticate the S2S instances towards the etcd server.

#### Infrastructure Data

etcd is also used to store the infrastructure data of the central server. This data is used to synchronize the local DB
to have a consistent view of the infrastructure data. The infrastructure data is stored under a dedicated key space of
the form `/central/location/${entity}` where `entity` is one of continent, subcontinent, country, region, district,
and community. A complete key could look like `/central/location/continent/3fab2b69-31cd-5765-91e3-d68826a6dd48` where
the uuid is the uuid assigned to the continent in the central server. The values are expected to mirror the JSON values
of the DTOs (e.g., `ContinentDto`, etc.).

## Secure Communication

The S2S communication is expected to be secured by TLS. For additional security, the S2S communication is also
encrypted. Each instance receives a dedicated keystore and truststore. The keystore contains the instance's private and
public key. The truststore contains a root CA certificate (i.e., `sormas2sormas.rootCaAlias`) which is used to sign all
instance certificates. The connection to Keycloak must be secured by TLS. The connection to etcd must be secured by
TLS. The CA trusted by etcd clients when creating a TLS connection to the server is set via `central.etcd.caPath`.

## Deactivation

1. Stop the instance and create backup
2. The instance on which S2S will be deactivated has to revoke all outgoing pending requests AND has to accept or reject
all incoming pending request. Please note that as of now, users won't be able to see the whole sharebox or share directory.
Therefore, they cannot track if the case/contact was shared with/by another instance or not.
3. Deactivate the S2S feature by commenting all relevant properties in `sormas.properties`:
   - `sormas2sormas.*`
   - `central.oidc.url`
   - Optional if GA wants to disable central infra sync: `central.*`. Strongly discouraged.
4. Remove service descriptor of the deactivated instance from central `etcd` to prevent further discovery.
5. Reset feature configuration table.
6. Remove client and client scope of the instance from central Keycloak.
7. `shred` the instance's key material.
8. Restart the instance.
