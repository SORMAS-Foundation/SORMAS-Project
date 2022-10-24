## SORMAS Android App

The SORMAS Android app is designed for use by users at the level of hospitals, communities and districts. 
These have only limited access to the overall data of the system, so it is possible to transfer the relevant data set and make it available offline.

The offline functionality was also one of the core requirements in the development of the app, as it is also to be used in areas where no or hardly any mobile data network is available. 
This makes it possible for users to use the app freely and, for example, to synchronise the recorded data with the server in the evening.

The app covers all essential functionalities of SORMAS and the complete data collection in the areas of 
cases, contacts, events, samples, messages, mSERS and tasks.

The app is predominantly used on 7 or 10 inch tablets and requires Android 8.0 or newer. 
To manage the large number of devices used within a country and to simplify support, it is possible to use a mobile device management (MDM) solution. 

### Data Synchronization

For synchronisation, the Android app accesses the SORMAS ReST interface.

First, all data that has changed since the last synchronisation is retrieved from the server. 
In the app, a three-way merge takes place for all data that the user has changed himself in the meantime. 
For each individual field (e.g. gender of a person), it is checked whether the value on the server has changed compared to the original status on the device. 
If so, this change is adopted, otherwise any existing change of the user is retained. 
In this way, the data can be merged as automatically as possible. 
In the rare cases of an actual conflict, the users are informed accordingly about the discarded data.

In the second step, all changes made on the device are now sent to the server and confirmed by it. 
All communication between the app and the server takes place via encrypted HTTPS connections.

For data exchange with the server, the version of the SORMAS Android app used must be compatible with the version of the server. 
If this is not the case, the app offers the option of an automatic update to the corresponding version.

Known limitation: The system always calculates on the fly what data the user can access (e.g. cases based on jurisdiction). 
There is no active process of removing data the user has no longer access to from the mobile device (e.g. when the jurisdiction of a case changes). 
When the user makes changes to such an entity and tries to sync, the system will detect that the user has no access and trigger an exchange of all UUIDs the user has access to, to remove not accessible entities.

[Synchronization in more detail](https://github.com/hzi-braunschweig/SORMAS-Project/blob/development/sormas-base/doc/SormasSyncProcess.md)

### Versions & Update

The app comes with an integrated update mechanic that checks the server for new versions and can automatically download and install the latest one.

The synchronization is highly dependent on the version of the SORMAS API. 
When the version of the app is not compatible with the server version, the app has to be updated before a sync can be done again 
(e.g. new field added to the data model that would go lost when data is synced back and forth).

During the update existing data is automatically migrated using the commands in DatabaseHelper.


### Device Encryption

Each Android device using SORMAS should ahve device encryption enabled. This is currently not enforced.

### Data Storage

The data relevant to the user is stored on the device in an SQLite database. 
The version of SQLite included in Android 8 does not allow operations like renaming of database columns (needs SQLite 3.25.0, thus Android 11), which makes it quite inconvenient to work with.
