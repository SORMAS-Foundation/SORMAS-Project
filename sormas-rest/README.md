# REST interface for SORMAS

This is a one-stop-shop for all systems that need access to the SORMAS data:

* Synchronization of data with the SORMAS Android app
* Data access for the SORMAS Angular web app
* Exchanging data with other SORMAS instances
* External services like symptom diaries or citizen applications
* Synchronization of data with other surveillance or to data analysis systems

## Authentication
Access to the API is by default restricted by HTTP Basic authentication. Using OIDC/OAUTH2/Bearer authentication is also possible depending on how keycloak is setup. See , it can als use Bearer authentication. See [Authentication & Authorization](https://github.com/hzi-braunschweig/SORMAS-Project/wiki/Authentication-&-Authorization#keycloak). 

For basic auth use the username and password as credentials for your HTTP requests.
The user needs to have a user role having the SORMAS_REST user right.

## API Documentation
The SORMAS REST API is documented automatically. The OpenAPI specification files are generated during the build process
and can be found at `${Project Root}/sormas-rest/target/swagger.{json,yaml}`.

You can render the OpenAPI specification with tools like [editor.swagger.io](https://editor.swagger.io/). This allows
you to inspect endpoints and example payloads, generate a matching API client for many languages, and to easily interact
with the API of a live instance.

## OpenAPI / Swagger
The OpenAPI files are generated with the [`swagger-maven-plugin`](https://github.com/openapi-tools/swagger-maven-plugin)
and the Swagger Annotation Framework<sup>[[1]]([SwaggerAnnotations])</sup>.

If you are only interested in the OpenAPI specification files, you may either download a recent SORMAS
[release](https://github.com/hzi-braunschweig/SORMAS-Project/releases/) where the files reside in the `openapi`
directory, or execute the following command inside the `sormas-base` module's directory to build them for yourself:

```bash
# Requires Maven to be installed!
mvn package --projects ../sormas-rest --also-make -Dmaven.test.skip=true
```

The specification files are created at the path specified above.

---

<a id="SwaggerAnnotations"></a>\[1] Swagger Annotations Guide on Github: <https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations>
