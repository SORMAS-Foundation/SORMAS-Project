# SORMAS Keycloak integration

## How to debug

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

## sormas-backend client in Keycloak

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

## sormas-stats client in Keycloak

This client defines a `sormas-stats-access` client role. Once assigned to a user, it can access `sormas-stats`.
The client scope `client roles` mapper adds the roles to the `userinfo` endpoint, which is queried by the Apache2
OIDC module to determine the user's roles.
