# Synchronization process between SORMAS App and Server

## Synchronisation of modified and created data

![alt text](sormas_sync_process.png "Synchronization process between SORMAS app and server")

1. Make local changes

   The user changes some data with the SORMAS mobile app. E.g. the hospitalization of a case.

2. Save and create snapshot

   When saving local changes, the changed entity is marked as "modified". In addition a snapshot of the original data is created. This snapshot is later needed to compare local changes and server changes when merging.

3. Push new local entities to server

   Push new entities that have been created on the mobile app to the server.

4. Pull changes from server

   Get all data that was changed on the server since the last synchronization (e.g. pushed by another SORMAS app user).

5. Merge with local changes

   The changed data from the server is merged with local changes made by the app user. The snapshot that was created in step 2 is used as reference for the comparison.

   If the exact same field of an entity was modified on both server and app since the last synchronization, the server will "win". This means the local change is overridden and a log entry is added to the synchronization conflict log. The same goes for removed list items that were modified.

   The snapshot of changed entities is updated with the latestet server version

6. Push local changes to server

   After the merge is done the data of the app is "up to date". Now it is possible to send the local changes to the server. If pull and merge is not done before pushing, the server will reject all data that conflicts with changes made by other users.

7. "Accept" local changes

   After all local changes have been successfully pushed, we can "accept" them. This means the snapshot is deleted and the entity is no longer marked as modified.

8. Pull results from server

   Based on the data send the server same changes will have been made that need to be pulled. This will always include the change date based on the server time. In addition some entities may have been changed based on automatic processes (e.g. case classification).

9. "Merge" / save

   As in step 4 the pulled data from the server will be merged. Since there are no local changes, this means effectively that the data is just saved.

## Synchronisation modes

### "Changes"

This is the default mode for synchronisation.

1. Pull infrastructure data (based on change date)
2. Once every 24 hours: Pull uuids of entities that have been deleted on the server -> delete
3. Once every 24 hours: Pull uuids of entities that have been archived on the server -> delete
4. Synchronize core & support data, as explained above (based on change date)

### "Complete"

This mode is used, if a synchronisation in "Changes" mode fails (except for connection related fails) OR completes but still leaves unsynchronized data on the device.

1. Pull infrastructure data (based on change date)
2. **Pull all infrastructure uuids to delete invalid data and pull missing**
3. **Push new entities**
4. **Pull all core & support uuids to delete invalid data and pull missing**
5. Synchronize core & support data, as explained above (based on change date)

### "Complete and Repull"

This mode is used, if a synchronisation in "Complete" mode fails (except for connection related fails).

1. Pull infrastructure data (based on change date)
2. Pull all infrastructure uuids to delete invalid data and pull missing
3. **Re-pull all core & support data and part of the infrastructure data**
4. Push new entities
5. Pull all core & support uuids to delete invalid data and pull missing
6. Synchronize core & support data, as explained above (based on change date)
