# Sormas2Sormas Setup

## Introduction

### What is Sormas2Sormas?

### Components

## Setup

### Properties

| Property                                         | Description                                                                                                                                          |
|--------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| `central.oidc.url`                               | URL of the OIDC server (e.g., Keycloak) authenticating 3rd party requests.                                                                           |
| `central.etcd.host=`                             | The hostname of the etcd instance providing data.                                                                                                    |
| `central.etcd.clientName`                        | The client name used in etcd authentication.                                                                                                         |
| `central.etcd.clientPassword`                    | The client password used in etcd authentication.                                                                                                     |
| `central.etcd.caPath`                            | The path to the CA cert trusted by etcd clients.                                                                                                     |
| `central.location.sync`                          | If set to true, all infrastructure data from the central server will be synchronized into the local SORMAS database at startup and on a daily basis. |
| `sormas2sormas.path`                             | Path on the server where certificates and files related to SORMAS2SORMAS are stored.                                                                 |
| `sormas2sormas.id=`                              | The S2S ID of this instance.                                                                                                                         |
| `sormas2sormas.keystoreName`                     | Name of the key store file.                                                                                                                          |
| `sormas2sormas.keystorePass`                     | Password of the key store.                                                                                                                           |
| `sormas2sormas.rootCaAlias`                      | The alias of the trusted root CA.                                                                                                                    |
| `sormas2sormas.truststoreName`                   | Name of the trust store file.                                                                                                                        |
| `sormas2sormas.truststorePass`                   | Password of the trust store.                                                                                                                         |
| `sormas2sormas.oidc.realm`                       | Name of our authorization realm.                                                                                                                     |
| `sormas2sormas.oidc.clientId`                    | The client ID used in OIDC.                                                                                                                          |
| `sormas2sormas.oidc.clientSecret`                | The client secret used in OIDC.                                                                                                                      |
| `sormas2sormas.etcd.keyPrefix`                   | The etcd key prefix which is used to store s2s related information.                                                                                  |
| `sormas2sormas.ignoreProperty.additionalDetails` | Control if the value is ignored when shared / overwritten trough S2S.                                                                                |
| `sormas2sormas.ignoreProperty.externalId`        | Control if the value is ignored when shared / overwritten trough S2S.                                                                                |
| `sormas2sormas.ignoreProperty.externalToken`     | Control if the value is ignored when shared / overwritten trough S2S.                                                                                |
| `sormas2sormas.ignoreProperty.internalToken`     | Control if the value is ignored when shared / overwritten trough S2S.                                                                                |
| `#sormas2sormas.districtExternalId`              | External id of the district to which the Cases/Contacts to be assigned when accepting a share request                                                |

