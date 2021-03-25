# How to add a new disease?

This guide explains how to add a new disease to SORMAS and how to configure the existing fields to be either available or hidden in the case forms.

## 0. Preparation

Define which of the available case and contact data fields should be available for the new disease.
The best way to do this is to follow these steps:

1. Download the latest data dictionary <https://github.com/hzi-braunschweig/SORMAS-Project/blob/development/sormas-api/src/main/resources/doc/SORMAS_Data_Dictionary.xlsx>
2. Go through all the fields of person, case, contact and their sub entities symptoms, epidemiological data, etc.
   The [SOP for adding new diseases to SORMAS](SOP_DISEASES.md) explains this in detail.
3. If any field that is needed for your disease is missing, please have a look at the guide [How to add a new field?](GUIDE_ADD_NEW_FIELD.md).

## I. Adding the new disease

1. Open the Disease enum class in the API project and add the new disease. Put it in alphabetical order (with the exception of "OTHER").
2. Add the name of the disease to the enum.properties translation resource file. You may also want to add a short name.
3. **Very important**: We have now made a change to the API. Old versions are no longer compatible!
   When data with the new disease is send to a mobile device with an old version, it will not know about the disease type and lead to exceptions on the device.
   To avoid this the following has to be done:
   * Open the InfoProvider.getMinimumRequiredVersion method.
   * Set the version to the current development version (without the -SNAPSHOT). You can find the current version in the maven pom.xml configuration file.
4. Add a color for the disease to the disease.scss styling file and the CssStyles.getDisease color method in sormas-ui
5. SORMAS supports a simplified data entry mode that can be used during outbreaks.
   Its purpose is to reduce the number of fields that have to be entered by users to a minimum to reduce workload.
   If you want the disease to support this mode you need to include the disease in the Disease.isSupportingOutbreakMode method.

## II. Configuring the fields for the new disease

By default a case of the new disease will only use fields that are marked with "All" in the data dictionary.
Your disease will likely need to show additional fields. Do the following steps for all entities affected:

1. Open the entity's API class (e.g. SymptomsDto) and go through all the member fields.
2. Add the disease to the existing Diseases annotation.

That's it. The forms in the web application and Android app will automatically evaluate the annotation to decide which fields to show.
