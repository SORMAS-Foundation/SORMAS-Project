# User data access

## Basics
In general data access & synchronisation is based on the following rules:

* hospital informant: all data associated to the facility (cases)
* community informant: all data associated to the community (cases)
* officers: all data associated to the district (cases, events)
* supervisors: all data associated to the region
* observers: all data associated to their jurisdiction (nation, region or district)
* port of entry users: all data associated to their jurisdiction (nation, region or district)
* all data that is created by the user
* all data associated to the data created by the user (contacts, tasks, persons, visits)
* port health users can only see port health cases, no matter the user jurisdiction
* special case: if an officer has access to a task or contact whose case/event/contact is not available, the association link should be inactive
* user rights (e.g. allow seeing cases) and mapping of user roles to a jurisdiction level is documented in the [user rights table](https://github.com/hzi-braunschweig/SORMAS-Project/tree/development/sormas-api/src/main/resources/doc/SORMAS_User_Rights.xlsx)

## Case
* only users that are permitted to see cases at all
* whoever created the case or is assigned to it is allowed to access it
* access by jurisdiction, that is based region/district/community/health facility/point of entry of the case
* cases that are made public can be accessed by all users permitted to see cases (this feature can be disabled)
* edit is possible only for cases that are part of the users jurisdiction
* view of personal and sensitive data is possible only for cases that are part of the user's jurisdiction

## Contact
* only users that are permitted to see contacts at all
* whoever created it or is assigned to it is allowed to access it
* users see all contacts of their cases
* access by jurisdiction, that is using region/district of the contact and jurisdiction of the case the contact is assigned to
* edit is possible only for contacts that are part of the users jurisdiction
* view of personal and sensitive data is possible only for contacts that are part of the user's jurisdiction

## Visits
* users see all visits of the user's contacts
* view of sensitive data is possible only for contacts that are part of the user's jurisdiction

## Event
* only users that are permitted to see events at all
* whoever created the event or is assigned to it is allowed to access it
* access by region/district of the event

## EventParticipant
* users see all participants of all events they can access
* view of personal and sensitive data is possible only for events that are part of the user's jurisdiction

## Tasks
* only users that are permitted to see tasks at all
* whoever created the task or is assigned to it is allowed to access it
* all tasks for the user's cases except the ones assigned to users in other jurisdiction
* all tasks for the user's contacts except the ones assigned to users in other jurisdiction
* all tasks for the user's events except the ones assigned to users in other jurisdiction
 
## Samples
* only users that are permitted to see samples at all
* whoever created the sample or is assigned to it is allowed to access it
* users see all samples of their cases
* lab users see all samples of their laboratory
* access by jurisdiction of the case or contact the sample belongs to
* edit is possible only for samples of cases and/or contacts in the user's jurisdiction
* view of sensitive data is possible only for samples that are part of the user's jurisdiction

## Persons
* all persons of the cases the user can access
* all persons of the contacts the user can access
* all persons of the events the user can access
* edit is possible only only for persons that have their corresponding contact, case or event in the users jurisdiction
* view of personal and sensitive data is possible only for persons that have their corresponding contact, case or event in the users jurisdiction

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
* To: surveillance and contact supervisor of visits contacts region
* When: visit having contacts becomes symptomatic

# Task generation and user assignment

## Submit weekly reports
* generated every hour if WEEKLY_REPORT_GENERATION feature enabled for each hospital informant

## Contact follow-up
* generated every hour if CONTACT_FOLLOW_UP feature enabled 
* assigned user becomes the contact officer if exists, if not a random officer of the contact district

## Case related tasks
* assigned user becomes the surveillance officer of the case if exists, if not a random surveillance officer of the district


