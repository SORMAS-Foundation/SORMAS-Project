# Customizing a SORMAS Server

## Content
* [Server Configuration](#server-configuration)
* [Importing Infrastructure Data](#importing-infrastructure-data)
* [Disease Configuration](#disease-configuration)
* [Feature Confguration](#feature-configuration)

## Server Configuration
After installing a SORMAS servers, you can customize various configurations that define how SORMAS operates and is set up. This is done in the **sormas.properties** file that you can find in your domain folder. This file contains explanations for every property and also a default value in case you want to revert any changes that you've made.

Most of these properties are commented (indicated by a *#* in front of their name and value) because the default should work for most servers. If you want to change these properties, you can remove the *#* and specify a valid value. We strongly suggest to add values to the properties that are not commented by default because these are specific to your server (e.g. the default locale/language, the center of the country or region you're using SORMAS in and the URL that leads to the mobile .apk file).

Please note that this file contains all the properties that existed in the SORMAS version that you initially installed on your server. We strongly suggest to read the release notes of new versions in order to keep yourself updated about new properties or whether the expected values of existing ones have changed. In any of these cases, you need to manually update the properties file and either insert the new property or change the existing value to one that is compatible. The release notes will give you instructions on how to do this.

The following properties are currently configurable:

* **Default locale** *(country.locale)*: This is the locale your server is using as long as the user has not overwritten it in their settings. It impacts both the language that SORMAS is displayed in as well as e.g. date formats.
* **EPID prefix** *(country.epidprefix)*: SORMAS automatically generates EPID numbers for new cases. This is the prefix your country is using for all of these numbers. Most of the time it will be some sort of country code and should be three characters long.
* **Country center/zoom** *(country.center.latitude, country.center.longitude and map.zoom)*: These are the geo coordinates of the geographical center of the country or region you're using SORMAS in. Used to set the initial location of the maps used in dashboards and statistics.
* **App URL** *(app.url)*: The directory on your server where the mobile .apk file is stored that is used to automatically update the Android app after a new release. You should be able to copy the example given in the properties file and only have to replace the SERVER-NAME placeholder.
* **File paths** *(temp.path, generated.path and custom.path)*: The folders that SORMAS stores files in, either temporarily during case export or permanently like import templates. Files in temp.path are automatically deleted at midnight. Files in custom.path can be used to customize the login page, e.g. to provide default logins for demo servers or add additional contributors to the right sidebar.
* **Automatic case classification** *(feature.automaticcaseclassification)*: Determines whether SORMAS automatically classifies cases based on a number of criteria that are defined in the code.
* **Email settings** *(email.sender.address and email.sender.name)*: The email address and sender name that should be used when SORMAS is sending out emails, e.g. to notify users about specific events.
* **SMS settings** *(sms.sender.name, sms.auth.key and sms.auth.secret)*: Besides emails, SORMAS also supports sending automatic SMS to users at the same time (e.g. when a case has been classified as confirmed). The SMS provider SORMAS is using is the Vonage SMS API (https://www.vonage.com/communications-apis/sms/). If you have an account there, you can use your key and secret here to enable sending out SMS. Leaving these properties empty will disable this feature.
* **CSV separator** *(csv.separator)*: The separator that CSV files should use to separate columns. This is depending on your server locale. Most systems should be fine using the default (*,*), but e.g. German systems should be set to use *;*.
* **Name similarity threshold** *(namesimilaritythreshold)*: This is used when comparing cases or contacts to find duplicates in the system, either in retrospection or during creation or import. The higher the value, the more restrictive the algorithm, i.e. less potential duplicates will be found. It is suggested to play around with this setting to see which value works for your country and language.
* **Dev mode** *(devmode)*: Enabling developer mode will give you access to a tab in the Configuration menu that allows admins to create dummy cases and contacts to quickly fill the database. This is only meant to be used on development or demo systems and should be left disabled for production servers.
* **Infrastructure sync threshold** *(infrastructuresyncthreshold)*: Synchronizing infrastructure data to mobile apps (e.g. regions or health facilities) is done in chunks to avoid connection timeouts. If you expect your users to have very bad internet connection, lowering this threshold could make it easier for them to synchronize this data.
* **Archiving thresholds** *(daysAfterCaseGetsArchived and daysAfterEventGetsArchived)*: The number of days without any changes after which cases/events are automatically archived (i.e. they will no longer be displayed in the normal directories, but still count towards statistics or counts on the dashboard and can still be viewed by users with the respective user right).
* **Rscript executable** *(rscript.executable)*: The location of the Rscript executable. If you've installed Rscript on your server and specify the path here (the default should work for Linux systems as long as you've used the default install path), network diagrams for transmission chains will be shown in the web app.

## Importing Infrastructure Data
When you start a SORMAS server for the first time, some default infrastructure data is generated to ensure that the server is usable and the default users can be created. It is recommended (and, unless you're working on a demo server, necessary) to archive this default data and import the official infrastructure data of the country or part of the country that you intend to use SORMAS in instead.

### Import
SORMAS by default splits infrastructure data into four categories. Starting from the highest administrative division, these are: *Regions*, *Districts*, *Communities*, and *Health Facilities*. In addition, *Points of Entry* represent places like harbors and airports where people are frequently entering the country, while *Laboratories* are technically health facilities that are specifically used for sample testing purposes.

To import your data for one of these administrative divisions, log in as the default admin user and open the **Configuration** menu. Open any of the tabs for the infrastructure data you want to import and click on the **Import** button on the top right. You can download an import guide from within the popup window that will be opened, containing detailed instructions about the import process and the steps you need to go through in order to successfully import your data.

Make sure that you always start with the highest administrative division when importing, i.e. regions, and work your way down to the lowest, because lower divisions typically contain references to higher divisions.

### Archive
After importing your infrastructure data, you need to archive the default data unless you want it to appear in your app. To do that, again open the **Configuration** menu and the tab for the infrastructure data you want to archive. You can use the text filter on top of the screen to type in the name of the default data, then click on the edit icon to the right, and in the popup window that opens up, click on **Archive** and confirm your choice.

After archiving the default infrastructure data, you might want to edit the default users and assign them to administrative divisions that you've imported. To do so, go to the **User** menu and click on the edit icon beneath the user you want to re-assign.

## Disease Configuration
SORMAS supports a wide range of diseases, and not all of those might be relevant to any SORMAS instance or might be used in a different context. It is possible to adjust the following variables that define how the different diseases are handled:

* Whether the disease is **active**, i.e. it is used in this SORMAS instance
* Whether the disease is a **primary** disease, i.e. it is enabled for case surveillance; non-primary diseases can still be used for pathogen testing
* Whether the disease is **case-based**; if not, it is only enabled for aggregate case reporting
* Whether **contact follow-up is enabled**
* The **contact follow-up duration**

Right now, changing these variables unfortunately is not possible from within the user interface, but requires **direct database access**. If you have this access, you can edit the entries in the *diseaseconfiguration* table according to your needs. 

**IMPORTANT:** Whenever you edit an entry in this table, you also need to manually set the *changedate* to the current date and time. This is required in order for the mobile app to synchronize the changes and use the edited disease configuration.

## Feature Configuration
Some of the features in SORMAS can be enabled or disabled for the system.
Examples for this are aggregated reporting, event surveillance, national case sharing and more.

Right now, changing these variables unfortunately is not possible from within the user interface, but requires **direct database access**. If you have this access, you can edit the entries in the *featureconfiguration* table. 

* There will be an entry in the database table for each feature that is available in SORMAS
* Set the "enabled" value of the feature to true or false to enable or disable it
* The region, district, disease and enddate columns are currently only appicable for the line listing feature. The line listing feature is the only feature that can currently be configured using the UI.

**IMPORTANT:** Whenever you edit an entry in this table, you also need to manually set the *changedate* to the current date and time. This is required in order for the mobile app to synchronize the changes and use the edited disease configuration.
