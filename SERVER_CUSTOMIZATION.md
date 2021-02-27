# Customizing a SORMAS Server

## Content
- [Customizing a SORMAS Server](#customizing-a-sormas-server)
  - [Content](#content)
  - [Server Configuration](#server-configuration)
    - [Custom login page](#custom-login-page)
    - [Custom download files in about section](#custom-download-files-in-about-section)
  - [Importing Infrastructure Data](#importing-infrastructure-data)
    - [Import](#import)
    - [Archive](#archive)
  - [Disease Configuration](#disease-configuration)
  - [Feature Configuration](#feature-configuration)
  - [Proxy Settings](#proxy-settings)

## Server Configuration
After installing a SORMAS servers, you can customize various configurations that define how SORMAS operates and is set up. This is done in the **sormas.properties** file that you can find in your domain folder. This file contains explanations for every property and also a default value in case you want to revert any changes that you've made.

Most of these properties are commented (indicated by a *#* in front of their name and value) because the default should work for most servers.
If you want to change these properties, you can remove the *#* and specify a valid value. We strongly suggest to add values to the properties that are not commented by default because these are specific to your server (e.g. the default locale/language, the center of the country or region you're using SORMAS in and the URL that leads to the mobile .apk file).

Please note that this file contains all the properties that existed in the SORMAS version that you initially installed on your server.
We strongly suggest to read the release notes of new versions in order to keep yourself updated about new properties or whether the expected values of existing ones have changed. In any of these cases, you need to manually update the properties file and either insert the new property or change the existing value to one that is compatible. The release notes will give you instructions on how to do this.

The following properties are currently configurable:

* **Default locale** `country.locale`: This is the locale your server is using as long as the user has not overwritten it in their settings. It impacts both the language that SORMAS is displayed in as well as e.g. date formats.
* **EPID prefix** `country.epidprefix`: SORMAS automatically generates EPID numbers for new cases. This is the prefix your country is using for all of these numbers. Most of the time it will be some sort of country code and should be three characters long.
* **Country name** `country.name`: Name of the country to pre-fill the country fields for locations and to activate the region, district, etc. fields when this country is selected. Needs to match the name of the country in the database.
* **Country center/zoom** `country.center.latitude`, `country.center.longitude` and `map.zoom`: These are the geo coordinates of the geographical center of the country or region you're using SORMAS in. Used to set the initial location of the maps used in dashboards and statistics.
* **App URL** `app.url`: The directory on your server where the mobile .apk file is stored that is used to automatically update the Android app after a new release. You should be able to copy the example given in the properties file and only have to replace the SERVER-NAME placeholder.
* **File paths** `documents.path`, `temp.path`, `generated.path` and `custom.path`: The folders that SORMAS stores files in, either temporarily during case export or permanently like import templates or documents. Files in temp.path are automatically deleted at midnight. Files in custom.path can be used to customize the login page, e.g. to provide default logins for demo servers or add additional contributors to the right sidebar.
* **Automatic case classification** `feature.automaticcaseclassification`: Determines whether SORMAS automatically classifies cases based on a number of criteria that are defined in the code.
* **Email settings** `email.sender.address` and `email.sender.name`: The email address and sender name that should be used when SORMAS is sending out emails, e.g. to notify users about specific events.
* **SMS settings** `sms.sender.name, sms.auth.key and sms.auth.secret`: Besides emails, SORMAS also supports sending automatic SMS to users at the same time (e.g. when a case has been classified as confirmed).
  The SMS provider SORMAS is using is the Vonage SMS API (<https://www.vonage.com/communications-apis/sms/>). If you have an account there, you can use your key and secret here to enable sending out SMS. Leaving these properties empty will disable this feature.
* **CSV separator** `csv.separator`: The separator that CSV files should use to separate columns. This is depending on your server locale. Most systems should be fine using the default (*,*), but e.g. German systems should be set to use *;*.
* **Name similarity threshold** `namesimilaritythreshold`: This is used when comparing cases or contacts to find duplicates in the system, either in retrospection or during creation or import.
  The higher the value, the more restrictive the algorithm, i.e. less potential duplicates will be found. It is suggested to play around with this setting to see which value works for your country and language.
* **Dev mode** `devmode`: Enabling developer mode will give you access to a tab in the Configuration menu that allows admins to create dummy cases and contacts to quickly fill the database.
  This is only meant to be used on development or demo systems and should be left disabled for production servers.
* **Infrastructure sync threshold** `infrastructuresyncthreshold`: Synchronizing infrastructure data to mobile apps (e.g. regions or health facilities) is done in chunks to avoid connection timeouts.
  If you expect your users to have very bad internet connection, lowering this threshold could make it easier for them to synchronize this data.
* **Archiving thresholds** `daysAfterCaseGetsArchived` and `daysAfterEventGetsArchived`: The number of days without any changes after which cases/events are automatically archived (i.e. they will no longer be displayed in the normal directories, but still count towards statistics or counts on the dashboard and can still be viewed by users with the respective user right).
  If set to 0, automatic archiving is disabled.
* **Rscript executable** `rscript.executable`: The location of the Rscript executable. If you've installed Rscript on your server and specify the path here (the default should work for Linux systems
  as long as you've used the default install path), network diagrams for transmission chains will be shown in the web app.
* **Symptom journal interface**: Properties used to connect to an external symptom journal service. `interface.symptomjournal.url` is the URL to the website that SORMAS should connect to;
  `interface.symptomjournal.authurl` is the URL used to authenticate SORMAS at the external service; `interface.symptomjournal.clientid` and `interface.symptomjournal.secret` are the credentials used for the
  authentication process. A default user can be created automatically at startup by using `interface.symptomjournal.defaultuser.username` and `interface.symptomjournal.defaultuser.password`. This user can be used
  by the Symptom Journal system to connect to SORMAS.
* **Patient diary interface** Properties used to connect to an external patient diary service. `interface.patientdiary.url` is the URL to the website that SORMAS should connect to;
  `interface.patientdiary.probandsurl` is the URL to the website that SORMAS can send notifications; `interface.patientdiary.authurl` is the URL trough which SORMAS can obtain an authorization to the external patient
  diary; `interface.patientdiary.email` and `interface.patientdiary.password` are the credentials used by SORMAS to authenticate in the external patient diary.
  A default user can be created automatically at startup by using `interface.patientdiary.defaultuser.username` and `interface.patientdiary.defaultuser.password`.
  This user can be used by the Patient Diary system to connect to SORMAS.
* **Custom branding**: Properties used to apply a custom branding to SORMAS that overrides its name and default logo. Using these properties also alters the sidebar and adds another customizable area to it.
  If you want to use this feature, set `custombranding` to true. `custombranding.name` is the name that you want to use, `custombranding.logo.path` is the path to the logo that should be used.
* **Geocoding** Properties used to integrate an external geocoding service for obtaining the geo coordinates of addresses.
  * `geocodingServiceUrlTemplate` is the url for searching for address details, `${street}`, `${houseNumber}`, `${postalCode}`, and `${city}` placeholders will be replaced with the actual address fields when searching;
  * `geocodingLongitudeJsonPath` and `geocodingLatitudeJsonPath` are used to obtain the longitude and latitude of the address in the result of the geocoding service request
* **Authentication Provider**: Allows the user to choose the way of authentication for SORMAS and all it's third party clients. Supported values `SORMAS` (default) and `KEYCLOAK`
* **Authentication Provider User Sync At Startup**: Enables async user sync when the system boots up. Since the User Sync is mainly needed for an initial sync only, it's recommended to
  disable/remove this property once an initial sync has be performed. The User Sync will work similarly to the manual user sync:
  * creates all the missing users in the External Authentication Provider
  * updates all existing users in the External Authentication Provider
  * keeps the user's password if the user doesn't exist in the External Authentication Provider
  * will not override the user's password if the user already in the External Authentication Provider (matching done by username case insensitive)
  * will only sync active users (inactive users are automatically synchronized when they are activated manually)
  * is enabled trough a property in sormas.properties `authentication.provider.userSyncAtStartup` (by default is disabled)


### Custom login page
When setting up the server a custom file directory is created (most likely `/opt/sormas/custom`). You can adjust the `login*.html` files in that directory to customize the login page.

### Custom download files in about section
You can create a sub-folder `aboutfiles` in the custom directory mentioned above (e.g. `/opt/sormas/custom/aboutfiles`). Any file in that directory will be made available in the about section of the frontend.

## Importing Infrastructure Data
When you start a SORMAS server for the first time, some default infrastructure data is generated to ensure that the server is usable and the default users can be created. It is recommended (and, unless you're working on a demo server, necessary) to archive this default data and import the official infrastructure data of the country or part of the country that you intend to use SORMAS in instead.

### Import
SORMAS by default splits infrastructure data into four categories. Starting from the highest administrative division, these are: *Regions*, *Districts*, *Communities*, and *Health Facilities*.
In addition, *Points of Entry* represent places like harbors and airports where people are frequently entering the country, while *Laboratories* are technically health facilities that are specifically used for sample testing purposes.

To import your data for one of these administrative divisions, log in as the default admin user and open the **Configuration** menu. Open any of the tabs for the infrastructure data you want to import and click on the **Import** button on the top right.
You can download an import guide from within the popup window that will be opened, containing detailed instructions about the import process and the steps you need to go through in order to successfully import your data.

Make sure that you always start with the highest administrative division when importing, i.e. regions, and work your way down to the lowest, because lower divisions typically contain references to higher divisions.

### Archive
After importing your infrastructure data, you need to archive the default data unless you want it to appear in your app.
To do that, again open the **Configuration** menu and the tab for the infrastructure data you want to archive. You can use the text filter on top of the screen to type in the name of the default data, then click on the edit icon to the right, and in the popup window that opens up, click on **Archive** and confirm your choice.

After archiving the default infrastructure data, you might want to edit the default users and assign them to administrative divisions that you've imported. To do so, go to the **User** menu and click on the edit icon beneath the user you want to re-assign.

## Disease Configuration
SORMAS supports a wide range of diseases, and not all of those might be relevant to any SORMAS instance or might be used in a different context. It is possible to adjust the following variables that define how the different diseases are handled:

* Whether the disease is **active**, i.e. it is used in this SORMAS instance
* Whether the disease is a **primary** disease, i.e. it is enabled for case surveillance; non-primary diseases can still be used for pathogen testing
* Whether the disease is **case-based**; if not, it is only enabled for aggregate case reporting
* Whether **contact follow-up is enabled**
* The **contact follow-up duration**

Right now, changing these variables unfortunately is not possible from within the user interface, but requires **direct database access**. If you have this access, you can edit the entries in the *diseaseconfiguration* table according to your needs.

**VERY IMPORTANT:** Whenever you edit an entry in this table, you also need to manually set the *changedate* to the current date and time. This is required in order for the mobile app to synchronize the changes and use the edited disease configuration.

## Feature Configuration
Some of the features in SORMAS can be enabled or disabled to further customize the system. Right now, changing these variables unfortunately is not possible from within the user interface, but requires **direct database access**. If you have this access, you can edit the entries in the *featureconfiguration* table.
There is one entry for every configurable feature in this table, and you can set the value of the *enabled* column to *true* to enable it and *false* to disable it. The *region*, *district*, *disease* and *enddate* columns are currently only applicable for the line listing feature and define the scope in which line listing is used.
Line listing is configurable from within the UI and does not need to be manually edited in the database.

**VERY IMPORTANT:** Whenever you edit an entry in this table, you also need to manually set the *changedate* to the current date and time. This is required in order for the mobile app to synchronize the changes and use the edited feature configuration.

The following features are currently configurable:

* **Case Surveillance** `CASE_SURVEILANCE`: The core module of SORMAS which allows the creation and management of suspect or confirmed disease cases.
* **Contact Tracing** `CONTACT_TRACING`: Management and follow-up of contacts of disease cases.
* **Sample Management** `SAMPLES_LAB`: Management of samples for cases, contacts or event participants and the documentation of pathogen tests performed on these samples.
* **Event Surveillance** `EVENT_SURVEILLANCE`: Creating and managing events and event participants to identify potential outbreaks or disease hotspots.
* **Aggregate Reporting** `AGGREGATE_REPORTING`: Allows collecting case numbers for a number of additional diseases for which case-based surveillance is not used. Commonly referred to as mSers in African countries.
* **Weekly Reporting** `WEEKLY_REPORTING`: Allows mobile users to confirm the number of cases they have collected on a weekly basis and web users to see an overview of whether or not mobile users have submitted their reports and how many cases they have reported.
* **Clinical Management** `CLINICAL_MANAGEMENT`: Enables the clinical management module of cases that allow collecting prescriptions and treatments as well as doctor's visits in a clinical context.
* **National Case Sharing** `NATIONAL_CASE_SHARING`: Allows users with the respective rights to make cases available to the whole country, i.e. other users will see these cases even if they don't belong to their jurisdiction.
* **Task Generation (Case Surveillance)** `TASK_GENERATION_CASE_SURVEILLANCE`: Enables or disables the automatic generation of tasks associated with case surveillance, especially the *Case Investigation* tasks that are usually generated when creating a new case.
* **Task Generation (Contact Tracing)** `TASK_GENERATION_CONTACT_TRACING`: Enables or disables the automatic generation of tasks associated with contact tracing, especially the *Contact Investigation* tasks that are usually generated when creating a new contact and the *Contact Follow-Up* tasks that are created once a day for every contact that is under follow-up.
* **Task Generation (Event Surveillance)** `TASK_GENERATION_EVENT_SURVEILLANCE`: Enables or disables the automatic generation of tasks associated with event surveillance.
* **Task Generation (General)** `TASK_GENERATION_GENERAL`: Enables or disables the automatic generation of tasks that aren't directly associated with one of the three other task types described above, e.g. the *Weekly Report Generation* task that asks mobile users to submit their weekly reports.
* **Campaigns** `CAMPAIGNS`: The campaigns module allows collecting flexible data which can be customized using the JSON format. Currently this is heavily geared towards vaccination campaigns in Afghanistan, but will be usable in a more generic way in the future for other countries as well.
* **Area Infrastructure** `INFRASTRUCTURE_TYPE_AREA`: Enables an additional infrastructure level above region that is called area by default. Currently only used in the campaigns module.
* **Case Follow-Up** `CASE_FOLLOWUP`: Enables the contact follow-up module for cases as well to allow a more detailed daily documentation of symptoms.
* **Line Listing** `LINE_LISTING`: Whether or not using line listing for case entry is enabled in the specified jurisdiction for the specified disease. Configurable from the UI, no database interaction needed.
* **Documents** `DOCUMENTS`: Enables document storage.

## Proxy Settings
Some SORMAS integrations support proxy settings:
* **Patient diary interface**
* **Geocoding**
* **SORMAS 2 SORMAS**

The proxy can be configured through the following system properties which can be passed as JVM arguments to the server:
* `org.jboss.resteasy.jaxrs.client.proxy.host`
* `org.jboss.resteasy.jaxrs.client.proxy.port`
* `org.jboss.resteasy.jaxrs.client.proxy.scheme`
