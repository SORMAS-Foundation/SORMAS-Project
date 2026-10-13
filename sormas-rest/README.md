# REST interface for SORMAS

This is a one-stop-shop for all systems that need access to the SORMAS data:

* Synchronization of data with the SORMAS Android app
* Data access for the SORMAS Angular web app
* Exchanging data with other SORMAS instances
* External services like symptom diaries or citizen applications
* Synchronization of data with other surveillance or to data analysis systems

## Authentication
Access to the API is by default restricted by HTTP Basic authentication. Using OIDC/OAUTH2/Bearer authentication is
also possible depending on how keycloak is set up.
See [Authentication & Authorization](https://github.com/sormas-foundation/SORMAS-Project/wiki/Authentication-&-Authorization#keycloak).

For basic auth use the username and password as credentials for your HTTP requests.
The user needs to have a user role having the SORMAS_REST user right.

## API Documentation
The SORMAS REST API is documented automatically. The OpenAPI specification files are generated during the build process
and can be found at `${Project Root}/sormas-rest/target/swagger.{json,yaml}`.

You can render the OpenAPI specification with tools like
[editor.swagger.io](https://editor.swagger.io/?url=https://raw.githubusercontent.com/sormas-foundation/SORMAS-Project/development/sormas-rest/swagger.yaml).
This allows you to inspect endpoints and example payloads, generate a matching API client for many languages, and to easily interact with the API of a live instance.

## OpenAPI / Swagger
The OpenAPI files are generated with the [`swagger-maven-plugin`](https://github.com/swagger-api/swagger-core/tree/master/modules/swagger-maven-plugin)
and the Swagger Annotation Framework<sup>[[1]]([SwaggerAnnotations])</sup>.

If you are only interested in the OpenAPI specification files, you may either download a recent SORMAS
[release](https://github.com/sormas-foundation/SORMAS-Project/releases/) where the files reside in the `openapi`
directory, take a look at the files in `sormas-rest`, or execute the following command inside the `sormas-base` module's
directory to build them for yourself:

```bash
# Requires Maven to be installed!
mvn package --projects ../sormas-rest --also-make -Dmaven.test.skip=true
```

The specification files are created at the path specified above.

---

<a id="SwaggerAnnotations"></a>\[1] Swagger Annotations Guide on GitHub:
<https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations>


## External Visit API

The purpose of this API is to enable communication between SORMAS and other symptom journals.
Only users with the role `REST_EXTERNAL_VISITS_USER` are authorized to use the endpoints. Authentication is done using
basic auth, with the user and password. For technical details please contact the dev team on
[GitHub Discussions](https://github.com/sormas-foundation/SORMAS-Project/discussions).


### Workflow Description

#### About external follow up

Follow up in SORMAS is done via visits. Visits hold information about symptoms at a specific time point.
Visits can be created for cases and contacts, either via the SORMAS-UI or the external visits journal API.


#### About persons, cases and contacts

This is a very basic concept that one needs to understand when working with visits. In SORMAS, a person entity
represents a physically existing person. Cases and contacts represent epidemiological occurrences. When a person was
in contact with an infectious environment, the person has a contact in SORMAS.
When the person was in contact with such environments twice, it gets two contacts.
When a person falls ill, it gets a case with the according disease. This means: In SORMAS, each contact and case relates
to exactly one person. One person can have several contacts and cases, though. Follow up is done for a contact
(or a case, which can be enabled in the SORMAS feature configuration). Contacts (or cases) initiate follow up. A person,
though, either shows symptoms or not. It can not show symptoms for just one contact and not for the other. Thus, visits
are related to all active contacts (and cases) of a person. Also, the communication with external symptom journals is
**PERSON BASED**. Only the person uuid is used, visits are uploaded to each active case and contact of a person.


#### Person status variables

It is important to understand the meaning of two variables: the follow-up status and the symptom journal status.

The follow-up status describes the follow-up for a contact or a case. Possible values are defined in
[the FollowUpStatus enum](https://github.com/sormas-foundation/SORMAS-Project/blob/development/sormas-api/src/main/java/de/symeda/sormas/api/contact/FollowUpStatus.java)

Follow up can be done with, or without an external journal, the follow-up status makes no distinction there. Because the
follow-up status is contact and case specific, but the communication with external journals is person based, SORMAS
determines the most important follow-up status of all contacts and cases related to the person in question when
communicating with external journals. Whenever there is follow-up ongoing for any of the persons contacts (and cases if
the case follow-up feature is enabled in SORMAS), SORMAS will state the `FollowUpStatus.FOLLOW_UP` for that person
towards external journals.

The [SymptomJournalStatus](https://github.com/sormas-foundation/SORMAS-Project/blob/development/sormas-api/src/main/java/de/symeda/sormas/api/person/SymptomJournalStatus.java)
describes the state of a person related to external symptom journals. It is not contact or case specific.


#### Configuration in SORMAS

In the domain folder, there is a sormas.properties. it holds the following values relevant for an external journal:

* `interface.patientdiary.authurl`: used to fetch an authentication token (see 1. below).

* `interface.patientdiary.frontendAuthurl`: URL used to retrieve tokens for frontend requests. If not specified, no tokens will be fetched for such.

* `interface.patientdiary.tokenLifetime`: Lifetime of tokens fetched via the authurl or the frontendAuthurl. To be specified in seconds. Can be set to 0 for no token caching. Defaults to 21600 (6 hrs.).

* `interface.patientdiary.probandsurl`: used to register new persons in the external journal (see 2. below).

* `interface.patientdiary.url`: used to open a person in the external journal (see 6. below).

* `interface.patientdiary.email`: used to authenticate at the external journal (see 1. below).

* `interface.patientdiary.password`: used to authenticate at the external journal.

* `interface.patientdiary.defaultuser.username`: This user will be created in SORMAS and can be used by the external journal to authenticate.

* `interface.patientdiary.defaultuser.password`: The above user's password.

* `interface.patientdiary.acceptPhoneContact`: used to configure whether the phone number is considered relevant for registering a person in the external journal. It affects the validation of persons in SORMAS (see 2. below). Defaults to true


#### Workflows

##### SORMAS fetching an authentication token from the external journal

POST to the `interface.patientdiary.authurl`.

Request body:
```json lines
{
    "email" : [patientdiary.email],
    "password" : [patientdiary.password]
}
```
where `[patientdiary.email]` is replaced with `interface.patientdiary.email` and `[patientdiary.password]` with
`interface.patientdiary.password specified` in the `sormas.properties`.

Expected response body:
```json lines
{
    "success" : true,
    "userId" : [some-user-id],
    "token" : [token]
}
```
The `token` returned will be used to authenticate in other requests. Its lifetime can be configured via the
`interface.patientdiary.tokenLifetime` property.

One special scenario is fetching a token for frontend calls (see 6.): When the `interface.patientdiary.frontendAuthurl`
is configured, it is used instead of the `interface.patientdiary.authurl` here. If it is not configured, no token will
be used.



##### Registration of a new person in the external journal

This process involves several steps that are triggered via the `REGISTER` button a privileged user can see in the top
right corner when having opened a case or a contact.

To be able to see this button, the user must have at least one of the following user roles: national user, contact
supervisor, contact officer, community officer, surveillance officer, surveillance supervisor, or admin supervisor.


First comes a SORMAS-internal validation of contact details.
The person to be registered needs to have at least an email address (or a phone number if that is accepted for
registration, see `interface.patientdiary.acceptPhoneContact`) to pass this validation. Also, when there are several
email addresses or phone numbers, one of them has to be marked primary contact detail, so that it is clear which contact
detail shall be used.



Then comes an external validation of the contact details. For this, SORMAS fetches an authentication token as in 1.
Then it sends a GET request to the following URL for each contact detail to be used in  the external journal:

`GET [interface.patientdiary.probandsurl]/probands?q=[URL-encoded-query-parameter-and-value]`, with a header like
`x-access-token: [token]`.

The `[URL-encoded-query-parameter-and-value]` consists of a parameter-value-pair. The parameter is either `Email` or
`Mobile phone`. The value holds the contact detail to be validated.

An unencoded example for this is `"Email" = "example@example.de"`, the URL-encoded version is
`%22Email%22%20%3D%20%22example%40example.de%22`.

`[token]` is replaced with the token fetched for authorization.

The CURL equivalent for an exemplary call is \
`curl --request GET 'https://probands-URL.com//probands?q=%22Email%22%20%3D%20%22example%40example.de%22' --header 'x-access-token: my-access-token'`.


Expected result is a `PatientDiaryQueryResponse` which information about any person already registered in the external
journal and using the same contact detail.

It needs to be structured as follows:
```json lines
PatientDiaryQueryResponse {
    total: integer,
    count: integer,
    results: List<PatientDiaryPersonData>
}
```

- `total` should state how many persons are registered in the external journal (this information is currently never used in SORMAS).

- `count` should state how many registered persons using the same contact detail were found.

- `results` need to contain a PatientDiaryPersonData for each match:

```json lines
PatientDiaryPersonData {
    _id: string,
    idatId: PatientDiaryIdatId
}
```

- `_id` should be a unique identifier for the person this data is about (this information is currently never used in SORMAS)

The `PatientDiaryIdatId` needs to be structured as follows:

```json lines
PatientDiaryIdatId{
    idat: PatientDiaryPersonDto
}
```

The `PatientDiaryPersonDto` holds the actual person data:

```json lines
PatientDiaryPersonDto{
    personUUID: string,
    firstName: string,
    lastName: string,
    gender: string,
    birthday: string,
    contactInformation: PatientDiaryContactInformation,
    endDate: string
}

```
- `personUUID` should be the UUID of the person in SORMAS. This UUID is used to sync with external journals (this information is currently never used in SORMAS).
- `firstName` and `lastName` need to hold the first and last name of the person.
- `gender` should hold the persons gender (this information is currently never used in SORMAS).
- `birthday` should hold the person's birthday (this information is currently never used in SORMAS).
- `contactInformation` should hold the contact information of that person, which should for logical reasons always contain (at least) the contact detail provided by SORMAS in the query.
- `endDate` should hold the date after which follow up is supposed to be stopped by the external journal.

```json lines
PatientDiaryIdatId {
    email: string,
    phone: PatientDiaryPhone
}
```

- `email` should hold the email address for the person
- `phone` should hold the phone number of that person:

```json lines
PatientDiaryPhone {
    number: string,
    internationalNumber: string,
    nationalNumber: string,
    countryCode: string,
    dialCode: string,
}
```

To put this all together, here is an example `PatientDiaryQueryResponse` with one person using the same contact detail:
```json
{
    "total" : 100,
    "count" : 1,
    "results" : [{
        "_id" : "60586691d4c30700119515c8",
        "idatId" : {
            "idat" : {
            "contactInformation" : {
                "phone" : null,
                "email" : "example@example.de"
            },
            "personUUID" : "RMTEF2-UZXCXE-7YBJK6-KUMSSEME",
              "firstName" : "Maria",
              "lastName" : "Muster",
              "gender" : "female",
              "birthday" : null
            }
        }
    }]
}
```

SORMAS allows to continue with the registration of a new person only when the person has a unique first name, so when
all persons of the response have a different one (or if the response does not contain any matches, which needs to show
in `PatientDiaryQueryResponse.count == 0`). This validation is necessary to avoid confusion of person related data in
some external journals.


When there are no validation errors in the process described above, SORMAS fetches an authentication token as described
in 1. and then uses it to request the external journal to register the person:

`POST [interface.patientdiary.probandsurl]/external-data/[personUUID]`.

The `[personUUID]` is replaced with the UUID of the person, which is later used to sync data between the external
journal and SORMAS.

Expected response body:
```json lines
{
    "success" : [boolean],
    "message" : [messageString]
}

```
`[boolean]` is expected to be true in case of successful registration. SORMAS then sets the symptom journal status to
`REGISTERED` and displays the message to the user.

When `[boolean]` is false, the message is shown as an error to the user.

To fetch data relevant for the registration, the external journal can use the `/visits-external/person/{personUuid}`
API endpoint described below.

##### Synchronization of person data changed in SORMAS

It may happen that person data (like a contact detail) gets changed after a person is registered in an external journal.
SORMAS notifies external journals about such a change with first fetching an authentication token as descriced in 1.,
and the using this token for this request:

`PUT [interface.patientdiary.probandsurl]/external-data/[personUUID]`

The external journal is expected to refetch the person data via the `/visits-external/person/{personUuid}` API endpoint
described below and save the changes.

After re-fetching the person data, the symptom journal does its own internal validation and responds to SORMAS with the
synchronization result, containing eventual validation errors.

The expected response body:
```json lines
{
    "success" : [boolean],
    "message" : [messageString],
    "errors" : [{
        "errorKey": [errorString]
      }]
}
```

If the changes were done manually by a user from the person edit form, the synchronization result is shown to the user
in a popup window, so that the user can fix eventual errors and resynchronize the person data.

##### Upload of symptom journal data to SORMAS

For this, the `/visits-external` API endpoint has to be used. This is described below.

##### Upload of a symptom journal status to SORMAS

For this, the `/visits-external/person/{personUuid}/status` API endpoint is to be used. This is described below.


##### Opening a person in the external journal from within SORMAS

Once the symptom journal status of a person is set to `REGISTERED` or `ACCEPTED`, the external journal button in the
SORMAS-UI changes. It does not provide a registration anymore, but the options to open the person in the external
journal and to cancel external follow up. This button can be found when having opened a contact (or a case if the case
follow-up feature is enabled in SORMAS) in the top right corner. If the user chooses to open the person in the external
journal, SORMAS opens a new browser tab with the following URL:

`[interface.patientdiary.url]/data?q=[personUuid]&queryKey=sicFieldIdentifier`

SORMAS expects the external journal to present a view of the person there.

If the `interface.patientdiary.frontendAuthurl` is configured, SORMAS fetches an authentication token as described in 1,
and appends it to the URL:

`[interface.patientdiary.url]/data?q=[personUuid]&queryKey=sicFieldIdentifier&token=[token]`


##### Deletion of a person from an external journal

As described above, the journal button can offer the option to cancel external follow up. If a user choses this option,
SORMAS fetches an authentication token as described in 1., and uses it to request:

`DELETE [interface.patientdiary.probandsurl]/external-data/[personUUID]`

Expected response body:
```json lines
{
    "success" : [boolean],
    "message" : [messageString]
}
```

`[boolean]` is expected to be true in case of successful deletion. SORMAS then sets the symptom journal status to
`DELETED` and displays the message to the user.

When `[boolean]` is false, the message is shown as an error to the user.

Please note that this does not affect any follow-up status. Cancelling follow up of a contact or case is independent
of cancelling external follow up of a person.

