# SORMAS Keycloak integration

## How to debug

How to obtain an access token to impersonate a certain client:

```bash
curl --location --request POST 'http://localhost:8081/keycloak/realms/SORMAS/protocol/openid-connect/token' --header 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'client_id=sormas-stats' --data-urlencode 'client_secret=changeit' --data-urlencode 'grant_type=password' -d "username=admin" -d "password=1234abdcefHAH\!asd"
{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJOMzZ3ZFFHWVdDWnRvMEhsYUIzUnlNbllRVzJ5UFo1WVNVZFhOcy1tVDBZIn0.eyJleHAiOjE2NjI0NzMwNzMsImlhdCI6MTY2MjQ3Mjc3MywianRpIjoiNmEwMjNhMTItZDVkOC00OWNmLWIyODYtNzlkNDE0MmQyMmNlIiwiaXNzIjoiaHR0cDovL3Nvcm1hcy1kb2NrZXItdGVzdC5jb20va2V5Y2xvYWsvcmVhbG1zL1NPUk1BUyIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIzOTljYWM2Mi0xYjA1LTQ1YWEtYjAyYy03ZWEwYTI0MGYxNDQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJzb3JtYXMtc3RhdHMiLCJzZXNzaW9uX3N0YXRlIjoiOTA0NzE0NDAtZjJlNC00ZWZiLTk1NzEtYzVjYTgxMDY3ZGEyIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1zb3JtYXMiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InNvcm1hcy1zdGF0cyI6eyJyb2xlcyI6WyJzb3JtYXMtc3RhdHMtYWNjZXNzIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiI5MDQ3MTQ0MC1mMmU0LTRlZmItOTU3MS1jNWNhODEwNjdkYTIiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJhZCBtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbiIsImdpdmVuX25hbWUiOiJhZCIsImZhbWlseV9uYW1lIjoibWluIn0.LIqT7G9hNo5_fSDg2MB1g534Bvwqw5_ElqnfUbQr6yfV4OLpKoEuLfNXAHF7QU2qsmtjhrU01qV7OINqy2wNJyBl9mI4m-H4ns0jMu6RcAHJMwSBlFp9LfEMZIUxTgXAjbSLSorAHR7LT0SsbWxwSjoGgZMZbK5q9W0NCP_6qjcfPl7ka5BE9lrRfCv8cF91BbgJ0Wj4ZyG3I1JnxpDm0Orqj5mIVRaaTXPSQTeWcb7q_BErWbLP6_ywjMUpT565rgU2XnkLhhvnTUQSfUyjCWvmwvpgF5uQ7Fvs-OLjx0yDlbl37d5YNhdvHt20YdbiCGd1H6LKbH028Zo5vPmkrQ","expires_in":300,"refresh_expires_in":1800,"refresh_token":"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIyZGVmMmY3ZS1kNDg3LTRmYjItYTFkNy02MzJiYjQxMWNhYTEifQ.eyJleHAiOjE2NjI0NzQ1NzMsImlhdCI6MTY2MjQ3Mjc3MywianRpIjoiOWY1YmYxY2ItOGJlZi00MDA1LTgwYzctNjA5OTJlOGUwNzM1IiwiaXNzIjoiaHR0cDovL3Nvcm1hcy1kb2NrZXItdGVzdC5jb20va2V5Y2xvYWsvcmVhbG1zL1NPUk1BUyIsImF1ZCI6Imh0dHA6Ly9zb3JtYXMtZG9ja2VyLXRlc3QuY29tL2tleWNsb2FrL3JlYWxtcy9TT1JNQVMiLCJzdWIiOiIzOTljYWM2Mi0xYjA1LTQ1YWEtYjAyYy03ZWEwYTI0MGYxNDQiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoic29ybWFzLXN0YXRzIiwic2Vzc2lvbl9zdGF0ZSI6IjkwNDcxNDQwLWYyZTQtNGVmYi05NTcxLWM1Y2E4MTA2N2RhMiIsInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6IjkwNDcxNDQwLWYyZTQtNGVmYi05NTcxLWM1Y2E4MTA2N2RhMiJ9.M30sqDyn5BaplIIqEx9Lv7kbVOlycF6oaEe58zbQpcg","token_type":"Bearer","not-before-policy":0,"session_state":"90471440-f2e4-4efb-9571-c5ca81067da2","scope":"profile email"}%                                                                                                            
~
```

Use this token to access a certain endpoint:

```bash
curl -X POST http://localhost:8081/keycloak/realms/SORMAS/protocol/openid-connect/userinfo   -H 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJOMzZ3ZFFHWVdDWnRvMEhsYUIzUnlNbllRVzJ5UFo1WVNVZFhOcy1tVDBZIn0.eyJleHAiOjE2NjI0NzMwNzMsImlhdCI6MTY2MjQ3Mjc3MywianRpIjoiNmEwMjNhMTItZDVkOC00OWNmLWIyODYtNzlkNDE0MmQyMmNlIiwiaXNzIjoiaHR0cDovL3Nvcm1hcy1kb2NrZXItdGVzdC5jb20va2V5Y2xvYWsvcmVhbG1zL1NPUk1BUyIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIzOTljYWM2Mi0xYjA1LTQ1YWEtYjAyYy03ZWEwYTI0MGYxNDQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJzb3JtYXMtc3RhdHMiLCJzZXNzaW9uX3N0YXRlIjoiOTA0NzE0NDAtZjJlNC00ZWZiLTk1NzEtYzVjYTgxMDY3ZGEyIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1zb3JtYXMiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InNvcm1hcy1zdGF0cyI6eyJyb2xlcyI6WyJzb3JtYXMtc3RhdHMtYWNjZXNzIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiI5MDQ3MTQ0MC1mMmU0LTRlZmItOTU3MS1jNWNhODEwNjdkYTIiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJhZCBtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbiIsImdpdmVuX25hbWUiOiJhZCIsImZhbWlseV9uYW1lIjoibWluIn0.LIqT7G9hNo5_fSDg2MB1g534Bvwqw5_ElqnfUbQr6yfV4OLpKoEuLfNXAHF7QU2qsmtjhrU01qV7OINqy2wNJyBl9mI4m-H4ns0jMu6RcAHJMwSBlFp9LfEMZIUxTgXAjbSLSorAHR7LT0SsbWxwSjoGgZMZbK5q9W0NCP_6qjcfPl7ka5BE9lrRfCv8cF91BbgJ0Wj4ZyG3I1JnxpDm0Orqj5mIVRaaTXPSQTeWcb7q_BErWbLP6_ywjMUpT565rgU2XnkLhhvnTUQSfUyjCWvmwvpgF5uQ7Fvs-OLjx0yDlbl37d5YNhdvHt20YdbiCGd1H6LKbH028Zo5vPmkrQ'   -H 'Content-Type: application/x-www-form-urlencoded'
{"sub":"399cac62-1b05-45aa-b02c-7ea0a240f144","resource_access":{"sormas-stats":{"roles":["sormas-stats-access"]},"account":{"roles":["manage-account","manage-account-links","view-profile"]}},"email_verified":false,"name":"ad min","preferred_username":"admin","given_name":"ad","family_name":"min"}%               
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
