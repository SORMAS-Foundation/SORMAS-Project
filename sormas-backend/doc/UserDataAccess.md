# User data access

## Basics
In general data access & synchronisation is based on the following rules:

* hospital informant: all data associated to the facility (cases)
* community informant: all data associated to the community (cases)
* officers: all data associated to the district (cases, events)
* supervisors: all data associated to the region
* observers: all data associated to their jurisdiction (nation, region or district)
* port health users: - all data associated to their jurisdiction (nation, region or district), that is seeing data associated to their point of entry, only national port health users see data from multiple points of entry
                     - can only see port health cases, no matter the user jurisdiction
* all data that is created by the user
* all data associated to the data created by the user (contacts, tasks, persons, visits)
* special case: if an officer has access to a task or contact whose case/event/contact is not available, the association link should be inactive
* user rights (e.g. allow seeing cases) and mapping of user roles to a jurisdiction level is documented in the [user rights table](https://github.com/hzi-braunschweig/SORMAS-Project/tree/development/sormas-api/src/main/resources/doc/SORMAS_User_Rights.xlsx)

## Case
* only users that are permitted to see cases at all
* whoever created the case or is assigned to it is allowed to access it
* all cases are accessible for national users
* access by jurisdiction, that is based on the region/district/community/health facility/point of entry of the case
* cases that are made public can be accessed by all users permitted to see cases (this feature can be disabled)
* editing is possible only for cases that are part of the user's jurisdiction
* viewing personal and sensitive data is possible only for cases that are part of the user's jurisdiction

## Contact
* only users that are permitted to see contacts at all
* whoever created it or is assigned to it is allowed to access it
* all contacts are accessible for national users
* users see all contacts of their cases
* access by jurisdiction that is using the region/district of the contact or, if not available, the jurisdiction of the source case of the contact
* editing is possible only for contacts that are part of the user's jurisdiction
* viewing personal and sensitive data is possible only for contacts that are part of the user's jurisdiction

## Visits
* users see all visits of the user's contacts
* viewing sensitive data is possible only for contacts that are part of the user's jurisdiction

## Event
* only users that are permitted to see events at all
* whoever created the event or is assigned to it is allowed to access it
* all events are accessible for national users
* access by region/district of the event

## EventParticipant
* users see all participants of all events they can access
* viewing personal and sensitive data is possible only for events that are part of the user's jurisdiction

## Tasks
* only users that are permitted to see tasks at all
* whoever created the task or is assigned to it is allowed to access it
* all tasks are accessible for national users
* all tasks for the user's cases except the ones assigned to users in other jurisdiction
* all tasks for the user's contacts except the ones assigned to users in other jurisdiction
* all tasks for the user's events except the ones assigned to users in other jurisdiction

## Actions
* only users that are permitted to see action at all
* whoever created the action is allowed to access it
* all actions for the user's events

## Samples
* only users that are permitted to see samples at all
* all samples are accessible for national users
* whoever created the sample or is assigned to it is allowed to access it
* users see all samples of their cases
* lab users see all samples of their laboratory
* access by jurisdiction of the case or contact the sample belongs to
* editing possible only for samples of cases/contacts/event participants in the user's jurisdiction
* viewing sensitive data is possible only for samples that are part of the user's jurisdiction

## Persons
* all persons of the cases the user can access
* all persons of the contacts the user can access
* all persons of the events the user can access
* editing possible only for persons that have their corresponding contact, case or event in the user's jurisdiction
* viewing personal and sensitive data is possible only for persons that have their corresponding contact, case or event in the user's jurisdiction

## WeeklyReports
* only users that are permitted to see weekly reports at all
* whoever created the weekly report
* national users see all weekly reports in the database
* supervisors see all weekly reports from facilities in their region
* officers see all weekly reports of their informants

## WeeklyReportEntries
* users can see all weekly report entries associated with weekly reports they can access

# User notifications

## Case changed notification
* Types: email, sms
* To: all responsible supervisors within the case region
* When: case classification changed or disease of unspecified VHF changed

## Case investigation status done notification
* Types: email, sms
* To: all responsible supervisors within the case region
* When: case investigation status gets updated to done

## Pathogen test result notification
* Types: email, sms
* To: all responsible supervisors within the region of corresponding case or contact
* When: 
    * create of pathogen test with test result different from PENDING
    * update of pathogen test result from PENDING to any other value
    
## Contact follow-up task done notification
* Types: email, sms
* To: all responsible supervisors within the region of the task
* When: task of type contact follow-up set to status done

## New and pending task notification
* Types: email, sms
* To: [assigned user](#task-generation-and-user-assignment) of each new or pending task
* When: every 10 minutes 

## Visit changed notification
* Types: email, sms
* To: surveillance and contact supervisor of visit's contact's region
* When: visit having contacts becomes symptomatic

# Task generation and user assignment

## Submit weekly reports
* generated every hour if WEEKLY_REPORT_GENERATION feature enabled for each hospital informant and if no weekly report has yet been sent

## Contact follow-up
* generated every hour if CONTACT_FOLLOW_UP feature enabled 
* the task assignee is set to the contact officer if specified, otherwise to a random officer in the contact district

## Case related tasks
* the task assignee is set to the surveillance officer of the case if specified, otherwise to a random surveillance officer in the contact district


