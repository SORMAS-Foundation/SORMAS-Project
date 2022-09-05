```bash
curl --location --request POST \
'http://localhost:8081/keycloak/realms/SORMAS/protocol/openid-connect/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=sormas-stats' \
--data-urlencode 'client_secret=changeit'
--data-urlencode 'grant_type=password'
-d "username=admin"
-d password=$ADMIN_PWD

curl -X POST \
http://localhost:8081/keycloak/realms/SORMAS/protocol/openid-connect/userinfo \
-H 'Authorization: Bearer $RECEIVED_TOKEN'\
-H 'Content-Type: application/x-www-form-urlencoded'
```