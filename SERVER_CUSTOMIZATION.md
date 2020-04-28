# Customizing a SORMAS Server

## Content
* [Importing Infrastructure Data](#importing-infrastructure-data)
* [Disease Configuration](#disease-configuration)
* [Feature Confguration](#feature-configuration)

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
