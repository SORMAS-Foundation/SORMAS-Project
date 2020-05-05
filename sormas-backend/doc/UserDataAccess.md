# Basics
In general data access & synchronisation is based on the following rules:

* hospital informant: all data associated to the facility (cases)
* community informant: all data associated to the community (cases)
* officers: all data associated to the district (cases, events)
* supervisors: all data associated to the region
* all data that is created by the user
* all data associated to the above data (contacts, tasks, persons, visits)
* special case: if an officer has access to a task or contact whose case/event/contact is not available, the association link should be inactive

# Case
* whoever created the case or is assigned to it is allowed to access it
* access by region/district/community/health facility/point of entry of the case

# Contact
* whoever created it or is assigned to it is allowed to access it
* users see all contacts of their cases
* access by region/district of the contact

# Visits
* uses see all visits of the user's contact's persons

# Event
* whoever created the event or is assigned to it is allowed to access it
* access by region/district of the event
* informants dont see events

# EventParticipant
* users see all participants of all events they can access

# Tasks
* whoever created the task or is assigned to it is allowed to access it
* all tasks for the user's cases
* all tasks for the user's contacts
* all tasks for the user's events

# Samples
* whoever created the sample or is assigned to it is allowed to access it
* users see all samples of their cases
* lab users see all samples of their laboratory

# Persons
* all persons of the cases the user can access
* all persons of the contacts the user can access
* all persons of the events the user can access

# WeeklyReports
* whoever created the weekly report
* national users see all weekly reports in the database
* supervisors see all weekly reports from facilities in their region
* officers see all weekly reports of their informants

# WeeklyReportEntries
* users can see all weekly report entries associated with weekly reports they can access
