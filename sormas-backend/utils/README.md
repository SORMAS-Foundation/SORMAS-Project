# Script Usage

## Import scripts

To prepare data for importing into SORMAS with the import scripts, one can use the import templates downloaded from the UI.

Note that the headers starting with `#` must be removed from them.

Use the scripts by editing the csv path and running it as super user on the SORMAS database. Also make sure the user has access to the prepared CSV on the host system.

## Delete scripts

The delete scripts remove entries for good, it is no 'soft delete'. To be executed with caution by sys-admins only!

## Check scripts

Check scripts examine the database, but do not alter the state of the database. They are used to find, e.g., missing history tables or columns.