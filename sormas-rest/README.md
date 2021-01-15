### REST interface for SORMAS
#### Authentication
Access to the API is restricted using HTTP Basic Authentication.

#### OpenAPI / Swagger
The SORMAS REST API is automatically documented using the [`swagger-maven-plugin`](https://github.com/openapi-tools/swagger-maven-plugin) and the
 Swagger Annotation Framework<sup>[[1]]([SwaggerAnnotations])</sup>. Corresponding specification files in JSON and YAML format are produced
 automatically during the build process and can be found at `${Project Root}/sormas-rest/target/swagger.{json,yaml}` paths.
 
 If you are only interested in these OpenAPI specification files, you may either download a recent SORMAS release which has these files included
  in its `openapi` directory, or execute the following command inside the `sormas-base` module's directory to build them yourself:
 ```
 # Requires Maven to be installed!
 mvn package --projects ../sormas-rest --also-make -Dmaven.test.skip=true
 ```
 The specification files should then pop up at the paths specified above.

--- 

<a id="SwaggerAnnotations"></a>\[1] Swagger Annotations Guide on Github: <https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations>
