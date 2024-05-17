# SORMAS Android App

The SORMAS Android app is designed for use by users at the level of hospitals, communities and districts.
These have only limited access to the overall data of the system, so it is possible to transfer the relevant data set and make it available offline.

The **offline** functionality was also one of the core requirements in the development of the app, as it is also to be used in areas where no or hardly any mobile data network is available.
This makes it possible for users to use the app freely and, for example, to synchronise the recorded data with the server in the evening.

The app covers **all essential functionalities of SORMAS** and the complete data collection in the areas of
cases, contacts, events, samples, messages, aggregate reports and tasks.

The app is predominantly used on **7 or 10 inch** tablets and requires Android 8.0 or newer.
To manage the large number of devices used within a country and to simplify support, it is possible to use a mobile device management (MDM) solution.

## Data Synchronization

For synchronisation, the Android app accesses the **SORMAS ReST** interface.

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

[Synchronization in more detail](https://github.com/sormas-foundation/SORMAS-Project/blob/development/sormas-base/doc/SormasSyncProcess.md)

## Versions & Update

The app comes with an integrated update mechanic that checks the server for new versions and can **automatically download and install the latest one**.

The synchronization is highly dependent on the version of the SORMAS API.
When the version of the app is not compatible with the server version, the app has to be updated before a sync can be done again
(e.g. new field added to the data model that would go lost when data is synced back and forth).

During the update **existing data is automatically migrated** using the commands in DatabaseHelper.


## Device Encryption

Each Android device using SORMAS should ahve device encryption enabled. This is currently not enforced.

## Data Storage

The data relevant to the user is stored on the device in an SQLite database.
The version of SQLite included in Android 8 does not allow operations like renaming of database columns (needs SQLite 3.25.0, thus Android 11), which makes it quite inconvenient to work with.

## Firebase Crashlytics & Performance Monitoring

The SORMAS app is using Google Firebase to track crashes and performance.

Based on this [list](https://firebase.google.com/support/privacy) the following data listed in the details is collected.

<details>

* An RFC-4122 UUID which permits us to deduplicate crashes
* The timestamp of when the crash occurred
* The app's bundle identifier and full version number
* The device's operating system name and version number
* A boolean indicating whether the device was jailbroken/rooted
* The device's model name, CPU architecture, amount of RAM and disk space
* The uint64 instruction pointer of every frame of every currently running thread
* If available in the runtime, the plain-text method or function name containing each instruction pointer.
* If an exception was thrown, the plain-text class name and message value of the exception
* If a fatal signal was raised, its name and integer code
* For each binary image loaded into the application, its name, UUID, byte size, and the uint64 base address at which it was loaded into RAM
* A boolean indicating whether or not the app was in the background at the time it crashed
* An integer value indicating the rotation of the screen at the time of crash
* A boolean indicating whether the device's proximity sensor was triggered

Data within the framework of Firebase Performance Monitoring:

* General device information, such as model, OS, and orientation
* RAM and disk size
* CPU usage
* Carrier (based on Mobile Country and Network Code)
* Radio/Network information (for example, Wi-Fi, LTE, 3G)
* Country (based on IP address)
* Locale/language
* App version
* App foreground or background state
* App package name
* Firebase installation IDs
* Duration times for automated traces
* Network URLs (not including URL parameters or payload content) and the following corresponding information:
* Response codes (for example, 403, 200)
* Payload size in bytes
* Response times

Data that Firebase basically collects:

| User dimension                                          | Type   | Description                                                                                          |
|---------------------------------------------------------|--------|------------------------------------------------------------------------------------------------------|
| Age                                                     | Text   | Identifies users by six categories: 18-24, 25-34, 35-44, 45-54, 55-64, and 65+.                      |
| App Store                                               | Text   | The store from which the app was downloaded and installed.                                           |
| App Version                                             | Text   | The versionName (Android) or the Bundle version (iOS).                                               |
| Country                                                 | Text   | The country the user resides in.                                                                     |
| Device Brand                                            | Text   | The brand name of the mobile device (e.g., Motorola, LG, or Samsung).                                |
| Device Category                                         | Text   | The category of the mobile device (e.g., mobile or tablet).                                          |
| Device Model                                            | Text   | The mobile device model name (e.g., iPhone 5s or SM-J500M).                                          |
| First Open Time                                         | Number | The time (in milliseconds, UTC) at which the user first opened the app, rounded up to the next hour. |
| Gender                                                  | Text   | Identifies users as either male or female.                                                           |
| Interests                                               | Text   | Lists the interests of the user (e.g., "Arts & Entertainment, Games, Sports").                       |
| Language                                                | Text   | The language setting of the device OS (e.g., en-us or pt-br).                                        |
| New/Established                                         | N/A    | New: First opened the app within the last 7 days.                                                    |
| Established: First opened the app more than 7 days ago. |
| OS Version                                              | Text   | The version of the device OS (e.g., 9.3.2 or 5.1.1).                                                 |

Plus: UUID of the SORMAS users.
</details>
