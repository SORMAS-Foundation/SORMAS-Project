# Basics
In general data access & synchronisation is based on the following rules:

* hospital informant: all data associated to the facility (cases)
* community informant: all data associated to the community (cases)
* officers: all data associated to the district (cases, events)
* supervisors: all data associated to the region
* all data that is created by the user
* all data associated to the above data (contacts, tasks, persons, visits)
* special case: if an officer has access to a task or contact whose case/event/contact is not available, the association link should be inactive
* user rights (e.g. allowed to see cases) are documented in the [user rights table](sormas-api/src/main/resources/doc/SORMAS_User_Rights.xlsx)

# Case
* only users that are permitted to see cases at all
* whoever created the case or is assigned to it is allowed to access it
* access by region/district/community/health facility/point of entry of the case
* cases that are made public can be accessed by all users permitted to see cases (this feature can be disabled)

# Contact
* only users that are permitted to see contacts at all
* whoever created it or is assigned to it is allowed to access it
* users see all contacts of their cases
* access by region/district of the contact

# Visits
* users see all visits of the user's contacts

# Event
* only users that are permitted to see events at all
* whoever created the event or is assigned to it is allowed to access it
* access by region/district of the event

# EventParticipant
* users see all participants of all events they can access

# Tasks
* only users that are permitted to see tasks at all
* whoever created the task or is assigned to it is allowed to access it
* all tasks for the user's cases
* all tasks for the user's contacts
* all tasks for the user's events

# Samples
* only users that are permitted to see samples at all
* whoever created the sample or is assigned to it is allowed to access it
* users see all samples of their cases
* lab users see all samples of their laboratory

# Persons
* all persons of the cases the user can access
* all persons of the contacts the user can access
* all persons of the events the user can access

# WeeklyReports
* only users that are permitted to see weekly reports at all
* whoever created the weekly report
* national users see all weekly reports in the database
* supervisors see all weekly reports from facilities in their region
* officers see all weekly reports of their informants

# WeeklyReportEntries
* users can see all weekly report entries associated with weekly reports they can access
