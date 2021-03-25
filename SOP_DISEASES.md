# SOP for Adding New Diseases to SORMAS

This file defines the SOP (Standard Operating Procedure) that should be followed when requesting new diseases to be added to the system by the core development team. Answering all the questions asked in this guide will make sure that we will be able to integrate new diseases into SORMAS as quickly as possible.

## Content
- [SOP for Adding New Diseases to SORMAS](#sop-for-adding-new-diseases-to-sormas)
  - [Content](#content)
  - [Guide](#guide)
    - [Step 1: Download the Data Dictionary](#step-1-download-the-data-dictionary)
    - [Step 2: Define Basic Disease Details](#step-2-define-basic-disease-details)
    - [Step 3: Define Existing Case Fields](#step-3-define-existing-case-fields)
    - [Step 4: Define Existing Person Fields](#step-4-define-existing-person-fields)
    - [Step 5: Define the Relevant Symptoms](#step-5-define-the-relevant-symptoms)
    - [Step 6: Define the Relevant Epidemiological Data](#step-6-define-the-relevant-epidemiological-data)
    - [Step 7: Define Health Conditions](#step-7-define-health-conditions)
    - [Step 8: Define New Fields in Other Areas](#step-8-define-new-fields-in-other-areas)
    - [Step 9: Provide Case Classification Criteria](#step-9-provide-case-classification-criteria)
    - [Step 10: Provide Additional Information](#step-10-provide-additional-information)
    - [Step 11: Send Your Disease Definition to the SORMAS Team](#step-11-send-your-disease-definition-to-the-sormas-team)

## Guide

### Step 1: Download the Data Dictionary

Download the latest [Data Dictionary](https://github.com/hzi-braunschweig/SORMAS-Project/raw/development/sormas-api/src/main/resources/doc/SORMAS_Data_Dictionary.xlsx) from this repository and open it. Please never use a version of the Data Dictionary that you downloaded earlier as it is very likely that its contents have changed in the meantime.

You will use the Data Dictionary to define all the details of the new disease. Please make sure to mark every addition or change (e.g. by colorizing the text or background of the row in a subtle red) so we don't miss any of the information you have provided.

### Step 2: Define Basic Disease Details

Open the **Case** tab of the Data Dictionary and scroll down to the tables that have a blue background. These tables define *enumerations*, which are basically data types with fixed values.
Examples include the different case classifications, the gender of a person or the diseases that are used in SORMAS. Find the **Disease** enumeration table (refer to the *Type* column) and add a new row to it. Enter the following details:

* The **name of the disease** in the *Caption* column
* Optionally, if the disease has a long name, a **short name or abbreviation** in the *Short* column

Use the *Description* column to answer the following question(s):

* Does the disease have **contact follow-up**?
  * If yes, for **how many days** should contact follow-up be done?

### Step 3: Define Existing Case Fields

Look through the rows in the first table of the **Case** tab (which has a grey background). This table defines all the fields that are displayed in the *Case Information* tab in the SORMAS application.
The *Caption* column defines the name of the field as it is displayed in the user interface, while the *Diseases* column specifies which diseases use this field. Please add the name (or, if available, short name) of your new disease to the "New disease" column of every row that represents a field that is relevant for it and colorize it.

### Step 4: Define Existing Person Fields
Open the **Person** tab and repeat step 3 for the first table containing the fields that define the details of a person in SORMAS.

### Step 5: Define the Relevant Symptoms
Open the **Symptoms** tab which lists all the symptoms that are currently used in SORMAS. This is a very long list and you will have to go through every single row and define whether this symptom should be tracked for your new disease or not.

It's possible that your new disease uses one or more symptoms that are currently not part of SORMAS. In that case, you need to add a new row for each of these symptoms to the bottom of the table and provide the **name of the symptom** in the *Caption* column.

Most symptoms in SORMAS are simple *Yes/No/Unknown* fields where *Yes* means that the symptom is present, *No* that the symptom is not present and *Unknown* that there is no information about whether the symptom is present or not. If your symptom can simply be defined by this pattern, you don't have to specify anything else.
However, if your symptom is more complex (e.g. there are a number of pre-defined values that the user should choose from), please provide all the necessary details about how the symptom should be specified by users in the *Description* column.

### Step 6: Define the Relevant Epidemiological Data

Open the **Epidemiological data** tab which lists all fields that are used to collect information about the epidemiological background of the case, e.g. whether they visited burials, had contact with a confirmed case or animals. Repeat step 3 for all rows in the first table, and add new rows if your new disease requires information that is not currently collected within SORMAS.
As new fields in this tab are likely to be more complex than basic symptoms, make sure to define as much information about how they should function in the *Description* column.

### Step 7: Define Health Conditions
Open the **Health conditions** tab which contains a list of pre-existing conditions that are not symptoms of the disease, but are still relevant especially for case management purposes in a hospital. Repeat step 3 for all rows in the first table, and add new rows if there are health conditions relevant for your new disease that are not part of SORMAS yet.
As always with new fields, make sure to provide all relevant details in the *Description* column.

### Step 8: Define New Fields in Other Areas
It is possible that your disease requires further information to be collected that is not supported by SORMAS yet, e.g. new details about the person, specific information about its hospitalization, or even very important fields that should directly go into the case information.
You can use the same process you used to define new symptoms, health conditions or epidemiological data fields by opening the tab in question and adding new rows to the topmost table.

---

At this point, you have finished all the necessary definitions in the Data Dictionary. Save your work and prepare an email with the Data Dictionary file attached to it. Don't send this email before working through the remaining steps though, as there are still a few details that are needed in order to finish the specification of your new disease.

---

### Step 9: Provide Case Classification Criteria
Optimally, when defining a new disease, you should also specify the criteria SORMAS should use to automatically classify the case as suspect, probable or confirmed. In order to do this in a way that is compatible with the system we use, you will need access to a running SORMAS system (e.g. the play server you can find at <https://sormas.org>).
Log in as any user (e.g. the default user on the play server), open the *About* section from the main menu, and open the *Case Classification Rules (HTML)* document. Please define the classification criteria in a way that is similar to the system used in this document. If available, you can also send us an official document by WHO or your national CDC that specifies the classification criteria.

### Step 10: Provide Additional Information
If there are still things that are necessary in order to properly implement the new disease in SORMAS (you might require us to create a whole new area for cases or there might be very complex mechanics that need a lot more specification), please give us as many details about them as possible. Just put all this information into your email.

### Step 11: Send Your Disease Definition to the SORMAS Team
Send your email containing the updated Data Dictionary file, the case classification criteria and your additional notes to sormas@helmholtz-hzi.de. Congratulations, your work is done! We should now have all the information we need in order to integrate your disease into SORMAS.
If there is anything that is unclear or if we need additional details, we will get in touch with you as soon as possible. Thank you so much for contributing to SORMAS and helping us to fight the spread of as many diseases as possible!
