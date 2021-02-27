# How to add a new field?

This guide explains how to add a new plain field to the SORMAS data schema.
It does **not** explain how to add list fields, new sections or concepts to SORMAS.

**Important:** This is the first version of this guide. Please get in contact if anything is not clear or you have suggestions on how to improve the guide, the source code or the underlying architecture.

### Example use cases

* A symptom is needed for a specific disease (e.g. headache)
* A field with additional epidemiological details on a case (e.g. contact with a special type of animal)

## 0. Preparation (!)

1. Make sure the field is not already in the system.
   SORMAS has a lot of data fields and many of them are only used for a few diseases and hidden for other ones.
   The best way to make sure is to open the data dictionary and go through the existing fields of all related data sections: <https://github.com/hzi-braunschweig/SORMAS-Project/blob/development/sormas-api/src/main/resources/doc/SORMAS_Data_Dictionary.xlsx>
2. Clearly define the field:
   * Name and description
   * Field type: plain text, pre-defined values (enum), date, time, number
   * Example values
   * Who is supposed to enter the field?
   * Who is supposed to read the field?
3. [Set up your local development environment](DEVELOPMENT_ENVIRONMENT.md)

## I. Adding the field to the SORMAS API

The SORMAS API is the heart of the data schema. Accordingly, this is where you have to get started.

1. Identify the class where the field needs to be added. For most scenarios you will only need to have a look at CaseDataDto.java and all the fields used in there, e.g. SymptomsDto.
2. Add the field as a private member of the class with a get- and set-method. In addition a static final String to be used as a constant to identify the field.
3. If the field has pre-defined values add a enum in the package of the class. Have a look at one of the existing enums for reference.
4. Add the caption to captions.properties and description to description.properties in the project resources. For enums add all values to enum.properties.
   ```.properties
   Symptoms.soreThroat = Sore throat/pharyngitis
   ```
5. When you made additions/changes on keys in ``captions.properties``, ``strings.properties`` or ``validations.properties`` you have to run ``I18nConstantGenerator`` (run as ... Java Application) to update the corresponding Constants classes.
6. *Very important*: We have now officially made a change to the API which likely means that old versions are no longer fully compatible.
   When data with the new field is sent to a mobile device with an old version, it will not know about the field and the data is lost on the device.
   When the data is send back to the server the empty field may override any existing data and it's now also lost on the server itself.
   To avoid this the following has to be done:
   * Open the InfoProvider.getMinimumRequiredVersion method.
   * Set the version to the current development version (without the -SNAPSHOT). You can find the current version in the maven pom.xml configuration file.
   
## II. Adding the field to the SORMAS backend

The SORMAS backend is responsible for persisting all data into the servers database and to make this data accessible.
Accordingly it's necessary to extend the persistence logic with the new field.

1. Identify the entity class that matches the API class where the field was added (e.g. Case.java).
2. Add the field as a private member of the entity class with a get- and set-method.
3. Add the correct JPA annotation to the get-method (see other fields for examples).
   ```java
    @Enumerated(EnumType.STRING)
    public SymptomState getSoreThroat() {
        return soreThroat;
    }
   ```

In addition to this the sormas_schema.sql file in sormas-base/sql has to be extended:

4. Scroll to the bottom and add a new schema_version block. 
   It starts with a comment that contains the date and a short info on the changes and the github issue id and ends with and "INSERT INTO schema_version..." where the version has to be incremented.
   ```
   -- 2019-02-20 Additional signs and symptoms #938
   
   INSERT INTO schema_version (version_number, comment) VALUES (131, 'Additional signs and symptoms #938');
   ```
5. Within this block add a new column to the table that matches the entity where the new field was added in sormas-backend.
   You can scroll up to see examples of this for all the different field types. Note that the column name is all lower case.
   ```
   ALTER TABLE symptoms ADD COLUMN sorethroat varchar(255);
   ```
6. Make sure to also add the column to the corresponding history table in the database.
7. Update default values if needed.
8. Try to execute the SQL on your system!

Now we need to make sure data in the new field is exchanged between the backend entity classes and the API data transfer objects.

9. Identify the *FacadeEjb class for the entity (e.g. CaseFacadeEjb).
10. Extend the toDto and fromDto/fillOrBuildEntity methods to exchange data between the API class and the backend entity class that is persisted.
    ```
    target.setSoreThroat(source.getSoreThroat());
    ```
Now we need to make sure data in the new field is exported by the detailed export.

11. Identify corresponding *ExportDto (e.g. CaseExportDto)
12. Add the field as a private member of the dto class with a get- and set-method.
13. Add the @Order annotation on the getter method of the new field
    ```
    @Order(33)
    public SymptomState getSoreThroat() {
        return soreThroat;
    }
    ```
    > **NOTE**: The @Order numbers should be unique so please increase the order of the getters below if there are any.
14. Initialize the new field in the constructor
15. Add the new field in the selection list in the `getExportList` method of the *FacadeEJB
    ```
    cq.multiselect(
        ...,
        caseRoot.get(Case.SORE_THROAT),
        ...
    )
    ``` 
    > **NOTE**: Make sure the order of the fields in the selection list corresponds the order of arguments in the constructor of *ExportDto class  
### III. Adding the field to the SORMAS UI

The SORMAS UI is the web application that is used by supervisors, laboratory users, national instances and others.
Here we have to extend the form where the field is supposed to be shown and edited. Note that the web application uses the same form for read and write mode, so the field only needs to be added once.

1. Identify the Form class where the new field is supposed to be shown. Examples of this are SymptomsForm.class, CaseDataForm.class or EpiDataForm.class.
2. Add the new field to the HTML layout definition in the top of the form class. The forms use column layouts based on the bootstrap CSS library.
   ```
   LayoutUtil.fluidRowLocs(SymptomsDto.TEMPERATURE, SymptomsDto.TEMPERATURE_SOURCE) +
   ```
3. Go to the addFields method of the form and add the field. 
   You can add it without defining a UI field type - this will use a default UI field type based on the type of the data field (see SormasFieldGroupFieldFactory):
   ```
   addFields(EpiDataDto.WATER_BODY, EpiDataDto.WATER_BODY_DETAILS, EpiDataDto.WATER_SOURCE);
   ```
   Or you can define the type of UI field that should be used and provide additional initialization for the field:
   ```
   ComboBox region = addField(CaseDataDto.REGION, ComboBox.class);
   region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
   ```   
4. The FieldHelper class provides methods to conditionally make the field visible, required or read-only, based on the values of other fields.
5. Finally have a try in the web application to check if everything is working as expected.

### IV. Adding the field to the SORMAS Android app

The SORMAS Android app synchronizes with the server using the SORMAS ReST interface. The app does have it's own database to persist all data of the user for offline usage. Thus it's necessary to extend the entity classes used by the app.

1. Identify the entity class in the sormas-app backend sub-package.
2. Add the field as a private member of the entity class with a get- and set-method.
3. Add the correct JPA or ORM-lite annotation to the private member (see other fields for examples).
   Note: In the future this may be replaced by using Android Room.
4. Identify the *DtoHelper class for the entity (e.g. CaseDtoHelper).
5. Extend the fillInnerFromAdo and fillInnerFromDto methods to exchange data between the API class and the app entity class that is persisted.

SORMAS allows users to upgrade from old app versions. Thus it's necessary to add the needed SQL to the onUpgrade method in the DatabaseHelper class.

6. Increment the DATABASE_VERSION variable in the DatabaseHelper class.
7. Go to the end of the onUpgrade method and add a new case block that defines how to upgrade to the new version.
8. Execute the needed SQL using the DAO (database access object) of the entity class. 
   You can mostly use the same SQL used for adding the field to the SORMAS backend. The column name has to match the field name in the entity class (not all lower case).
   ```   
    getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN soreThroat varchar(255);");
   ```   
                    
The SORMAS app has separate fragments used for read and edit activities. Each fragment is split into the xml layout file and the java class containing it's logic.

9. Identify the edit fragment layout xml file where the field needs to be added. E.g. /res/layout/fragment_symptoms_edit_layout.xml
10. Add the field to the layout. See the existing fields for reference. Our custom Android components automatically add captions and descriptions to the field.
    ```
    <de.symeda.sormas.app.component.controls.ControlSwitchField
        android:id="@+id/symptoms_soreThroat"
        app:enumClass="@{symptomStateClass}"
        app:slim="true"
        app:value="@={data.soreThroat}"
        style="@style/ControlSingleColumnStyle" />
    ```
    Note that this comes with automatic data-binding. Use "@={...}" for edit fields and "@{...}" for read fields.
11. Identify the edit fragment java class. E.g. SymptomsEditFragment.java
12. Add needed field initializations to the existing onAfterLayoutBinding method. If necessary you can prepare any data in the prepareFragmentData method, while the fragment is still loading.
   Since there are many use cases please have a look at the existing classes.
13. Do the same for the read fragment and possibly also create fragment.
14. Finally have a try in the app to check if everything is working as expected. Make sure data entered on the Android device is properly synchronized and also appears on the server.

Now you are done :-)
