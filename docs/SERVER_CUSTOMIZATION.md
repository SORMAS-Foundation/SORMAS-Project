# Configuring and Customizing a SORMAS Server

SORMAS has been created and is developed as an international system that can potentially be used everywhere in the world. However, every country naturally has its own requirements and processes, and in addition, there is a set of information that needs to be specified before a SORMAS instance can function properly.
For this reason, there are a number of ways in which such a SORMAS instance can be configured and customized:

* **[Server Configuration](#server-configuration):** The basic server configuration is available as a .properties file, needs to be adjusted for every SORMAS instance, and is relevant for both production and development. This should be edited directly after installing the server.
* **[Feature Configuration](#feature-configuration):** Most SORMAS features are optional and can be turned on or off directly in the database.
* **[Disease Configuration](#disease-configuration):** SORMAS supports a large number of infectious diseases which can be enabled or disabled and further customized directly in the database.
* **[Deletion Configuration](#deletion-configuration):** SORMAS can be configured to automatically delete entities in the database.
* **[Infrastructure Data](#infrastructure-data):** Most infrastructure data (except countries and continents) are not shipped with SORMAS because they are country-specific. Importing the infrastructure data of your country (or creating some dummy data) is one of the first things you should do after setting up a SORMAS server.

Beyond that, the Wiki contains even more customization options:

* [Adding Disease Variants to a SORMAS Server](https://github.com/hzi-braunschweig/SORMAS-Project/wiki/Adding-Disease-Variants-to-a-SORMAS-Server)
* [Customizing the Login and About Pages](https://github.com/hzi-braunschweig/SORMAS-Project/wiki/Customizing-the-Login-and-About-Pages)
* [Customizing the Name and Icon of the Android App](https://github.com/hzi-braunschweig/SORMAS-Project/wiki/Customizing-the-Name-and-Icon-of-the-Android-App)
* [Creating a SORMAS2SORMAS Certificate](https://github.com/hzi-braunschweig/SORMAS-Project/wiki/Creating-a-SORMAS2SORMAS-Certificate)

## Server Configuration
The general SORMAS configuration is stored in the **sormas.properties** file that you can find in your payara domain folder. When you set up a new SORMAS server, please make sure to go over all entries in that file and adjust their values if necessary.
Each property has an explanation telling you its purpose, and some of them also have a default value that you can use to revert the changes you've made.

Most of these properties are commented (indicated by a *#* in front of their name and value), which means that the default value will automatically be used (e.g. the path to temporary files on the server) or the associated feature will not be used at all (e.g. the custom branding properties or the configuration of an external symptom journal).
Some properties however are not commented, but also don't have a default value. **It is strongly recommended to enter values for these properties** because they are required for some parts of SORMAS to work correctly, or because they are very specific to your individual SORMAS instance.
This especially applies to the **country.locale** and **country.name** properties, which will cause serious problems while using the application if left empty.

**Important: The sormas.properties file contains all properties that existed in the SORMAS version that you initially installed on your server. New properties added in more recent SORMAS versions are not automatically added!** If you're operating a SORMAS server, we strongly suggest to read the release notes of new versions.
If properties have been added to this file, they will be communicated in these notes so that you can add them to your properties file.

[This Wiki page](https://github.com/hzi-braunschweig/SORMAS-Project/wiki/Server-Configuration-Options) contains a list and explanation of all currently configurable properties.

## Feature Configuration
Some of the features of SORMAS can be enabled or disabled to further customize the system. Right now, this is only possible directly in the `featureconfiguration` table in the database. This table contains one entry for every configurable feature and is automatically populated during server startup. Setting the `enabled` to `true` or `false` will enable or disable the feature, respectively.
Changes are immediately applied to the running system and don't require a server restart.

The columns `region`, `district`, `disease` and `enddate` are currently only applicable for the line listing feature and define the scope in which the line listing is used. Line listing is configurable from the user interface and does not need to be manually edited in the database.

**Important: If you're using the mobile app, you also need to update the `changedate` to the current date and time whenever you enable or disable a feature!** Otherwise the mobile applications will not be notified about the change.

[This Wiki page](https://github.com/hzi-braunschweig/SORMAS-Project/wiki/Feature-Configuration-Options) contains a list and explanation of all currently configurable features.

## Disease Configuration
SORMAS supports a wide range of diseases, and not all of those might be relevant to every SORMAS instance or might be used in a different context. As with features, configuring diseases is currently only possible directly in the database via the `diseaseconfiguration` table. All diseases have a default value for each of their properties that is applied when the respective database entry is empty.
Changing these entries overrides that default value. Unlike with features, disease configurations are cached and therefore require you to restart the server before they are applied.

**Important: If you're using the mobile app, you also need to update the `changedate` to the current date and time whenever you change a disease configuration!** Otherwise the mobile applications will not be notified about the change.

It is possible to adjust the following properties that define how the diseases are handled:

* **`active`:** Whether this disease is used in this SORMAS instance. The concrete type of usage is specified by the other properties.
* **`primaryDisease`:** Primary diseases are enabled for case surveillance while non-primary diseases can only be used for pathogen testing.
* **`caseBased`:** Case-based diseases can be used to create cases while non-case-based diseases can be used for aggregate reporting.
* **`followUpEnabled`:** Whether follow-up is enabled for this disease, i.e. the follow-up status can be managed and visits can be created.
* **`followUpDuration`:** The minimum duration of follow-up for contacts of this disease.
* **`caseFollowUpDuration`:** The minimum duration of follow-up for cases of this disease.
* **`eventParticipantFollowUpDuration`:** The minimum duration of follow-up for event participants of this disease. Please note that event participant follow-up is not yet implemented.
* **`extendedClassification`:** Whether this disease uses an extended case classification system that allows users to specify whether a case has been clinically, epidemiologically or laboratory-diagnostically confirmed.
* **`extendedClassificationMulti`:** Whether the three confirmation properties used for extended classification can be specified individually, i.e. users can enter multiple sources of confirmation.

## Deletion Configuration
SORMAS can be set up to automatically delete entities after a specific time period. There are seven core entities for which automatic deletion can be enabled and configured: *Case, Contact, Event, Event Participant, Immunization, Travel Entry, and Campaign.* This configuration is currently only possible directly in the database via the `deleteconfiguration` table, which already contains rows for each of these entities. The table consists of the following columns:

* **`entityType`:** The name of the entity that supports automatic deletion.
* **`deletionReference`:** The reference date for the calculation of the date on which deletion takes place (see below).
* **`deletionPeriod`:** The number of days after which an entity is deleted, starting with the deletion reference. The minimum is 7.

Both `deletionReference` and `deletionPeriod` need to be filled in order for the automatic deletion to take place. Entities for which at least one of these fields is left empty will not be automatically deleted. Deletion is executed via a nightly cron job and might therefore not happen immediately when the deletion date has been reached.

### Deletion Reference
The `deletionReference` field has four possible values which define the date that is used to calculate whether an entity needs to be deleted (i.e., when the date calculated by subtracting the deletion period from the current date is before the deletion reference date, the entity is deleted). A `MANUAL_DELETION` entry can exist in parallel to one of the other entries, and if both entries are configured, deletion is executed as soon as the threshold of one of these entries is met.

* **`CREATION`**: The creation date of the entity will be used.
* **`END`**: The latest change date of the entity itself and any of its depending entities will be used. E.g. for cases, this includes but is not limited to its epi data, symptoms, or hospitalization.
* **`ORIGIN`**: This is currently only implemented for travel entries and means that the report date of the entity will be used. If this is specified for any other entity, the deletion job will be stopped and throw an error.
* **`MANUAL_DELETION`**: The date on which the entity was manually deleted by a user.

## Infrastructure Data
When you start a SORMAS server for the first time and the `createDefaultEntities` property is enabled, some default infrastructure data is generated to ensure that the server is usable and the default users can be created.
It is recommended (and, unless you're working on a demo server, necessary) to archive this default data and import the official infrastructure data of the country or part of the country that you intend to use SORMAS in instead.

SORMAS by default splits infrastructure data into four mandatory categories. Starting from the highest administrative division, these are *Regions*, *Districts*, *Communities*, and *Health Facilities*.
In addition, *Points of Entry* represent places like harbors and airports where people are frequently entering the country, while *Laboratories* are technically health facilities that are specifically used for sample testing purposes. The *Area* infrastructure type can be enabled in the feature configuration and adds another optional layer of infrastructure above Regions.
Finally, it is possible to add *Countries*, *Subcontinents* and *Continents* to your system if you also want to collect data from outside the country SORMAS is used in (e.g. because you want to record travels or events).

### Importing Infrastructure
To import your data for one of the administrative divisions, log in as the default admin user (which is created even when `createDefaultEntities` is disabled) and open the **Configuration** menu. Open any of the tabs for the infrastructure data you want to import and click on the **Import** button on the top right.
You can download an import guide from within the popup window that will be opened, containing detailed instructions about the import process and the steps you need to go through in order to successfully import your data.

Make sure that you always start with the highest administrative division when importing (i.e. at least *Countries* if you want to collect data from other countries as well, *Areas* if enabled, or *Regions* otherwise) and work your way down to the lowest, because lower divisions typically contain mandatory references to higher divisions.

For *Countries*, *Subcontinents* and *Continents*, SORMAS provides a default import that allows you to automatically add a complete set of data to your system. For *Countries*, this default data equals to the [official list of countries provided by the WHO](https://www.who.int/countries). For *Subcontinents* and *Continents*, the list is based on the data used by the [Robert Koch Institut](https://www.rki.de/DE/Home/homepage_node.html).
