# Basics
In general synchronization is based on the following rules:

* informant: all data associated to the facility (cases)
* officers: all data associated to the LGA/district (cases, events)
* all data that is created by the user
* all data associated to the above data (contacts, tasks, persons, visits)
* special case: if an officer has access to a task or contact whose case/event/contact is not available, the association link should be inactive

# Case
* whoever created the case or is assigned to it is allowed to access it
* supervisors see all cases of their region
* officers see all cases of their district
* informants see all cases of their facility

# Contact
* whoever created it or is assigned to it is allowed to access it
* users see all contacts of their cases

# Visits
* uses see all visits of the user's contact's persons

# Event
* whoever created the event or is assigned to it is allowed to access it
* supervisors see all events of their region
* officers see all events of their district
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