# Performance Test Plan


## Background

Sormas System has increased in usage and performance has been noticed to change. This implies for some changes but first, there is the need to measure somehow the performance through some Key Performance Indicators.

## 1. Purpose

The purpose of this section is to provide a high-level overview of the performance testing approach that should be followed for the SORMAS project.

This must be presented to all the relevant stakeholders and should be discussed in order to gain consensus.

## 2. Introduction

As part of the delivery of the  SORMAS, it is required that the solution meets the acceptance criteria, both in terms of functional and non-functional areas. The purpose of this Page is to provide an outline for non-functional testing of the SORMAS solution.

This Page covers the following:

- Entry and Exit Criteria
- Environmental Requirements
- Volume and Performance Testing Approach
- Performance Testing Activities

## 3. Entry Criteria

The following work items should be completed/agreed beforehand in order to proceed with the actual performance testing activities:

- Non-functional test requirements document created based on Reported Issues with quantified NFRs(Non-Functional Requirements)
- The critical use-cases should be functionally tested and without any critical bugs outstanding
- Key use-cases have been defined and scoped (Preferably together with the CLIENT
- Performance test types agreed
- Performance environment setup
- Any data setup needed - e.g. Appropriate number of users created on the Performance environment; Appropriate/sufficient number of Cases/Interactions in order to be able to create meaningful data setup

## 4. Exit Criteria

The performance testing activity will be completed when:

- The NFR targets have been met and performance test results have been presented to the team and approved
- The Performance of the project has reached a maturity where the ROI from creating more Performance tests is lower than the execution of older tests
- Performance increases are so small that the end users would not observe any changes

## 5. Environmental Requirements

The performance tests will be run against a stable version of the SORMAS solution (which has already passed the functional tests) and performed on a dedicated production-like environment (Performance-Environment) assigned for performance testing with no deployments on that environment during the course of the performance testing.

### **5.1 Load Injectors**

There will be one or more dedicated &quot;load injectors&quot; (Data creation scripts) set up to initiate the required load for performance testing (Cases, Events, and Contacts).

Possibilities for the load Injectors:

- The load injector could be a VM or multiple VMs that have an instance of JMeter running, initiating the requests
- Katalon Studio scripts executed in order to create all the necessary test data: Cases, Events, Contacts, Persons

### **5.2 Test Tools**

Test tools used for Volume and Performance testing will be:

- Jmeter
- Katalon Studio

#### **5.2.1 JMeter**

An open-source load testing tool. Predominantly used for volume and performance testing.

#### **5.2.2 Katalon Studio**

An open-source wrapper tool for Selenium Webdriver. Automation tests have already been created using this tool.

#### **5.2.3 Gatlink**

Gatlink was used in the past for some basic scenarios. Could be used further on or, the same test cases can be moved to Katalon/Jmeter.

For more information please check the [Gatlink Github page](https://github.com/hzi-braunschweig/SORMAS-Project/blob/2e6d9bf1379d2fd03e6762dd447414e3a27d7f87/LOAD_TESTING.md).

## 6. Volume and Performance Testing Approach

The `SORMAS` solution should be performant enough to manage the following load criteria.

N.B. The numbers in the following table are for sample only - real values should be inserted once finalized by CLIENT NFR document.

### **6.1 Target Service Volumes**

Response times need to be taken into consideration based on previous versions: 1.48, 1.49 and 1.50

Since user logins peak values are not high, they will be taken as the target for fixed load test. Scaling factor is TBD.

### **6.2 Number of Cases,Contacts,Events,Tasks**

Performance testing will run for a maximum of 1 million cases, 10 million Contacts, 1million Events,100 million Tasks. All this Test Data will be created in the DB beforehand and should be accessible via `SORMAS` API.

These Numbers need to be approved by the Client + be in accordance with the suggested NFRs.

The Scaling Model is presented on the Section 6.6

### **6.3 Assertions**

In the case where JMeter tool will be used to execute performance testing scripts, within the scripts, there will be assertions stated to check for the following metrics as well as some basic functional checks to ensure correct responses are received for each request.

The same test cases can be implemented using Katalon Studio ( Integration with Jmeter needs to be researched further TBD).

In the case of the Katalon Studio tool selection, logic needs to be implemented in order to be able to run the scripts in a Performance-oriented way(run the Case creation script multiple times with incremented data). Assertions can be easily included in the test cases either by calling other test cases( tests that verify the newly created cases) or just add the functionality to the creation test case.

### **6.4 Metrics**

Following metrics to be taken into consideration:

- Page load times on different pages
- REST calls response times TBD
  - Retrieve all cases for a mobile user
  - Retrieve all cases for a normal web user
  - Retrieve all infrastructure data
  - Retrieve a high number of contacts
  - Retrieve all the cases
  - Retrieve all Events
  - Retrieve all Contacts assigned to a specific case ( TBD the number of contacts and the scaling model for the number of contacts)
  - Retrieve all Contacts/Cases assigned to a Person (TBD )
  - Import Different files ( Incrementing the import size should also monitor the response times)
- Import times for different files (TBD what these files contain)
- Export times for different scenarios (TBD after scaling model will be defined)

### **6.5 Load Profiles**

The load profiles should be designed to mimic a typical average day&#39;s traffic to `SORMAS` site. Number of Cases/Contacts/Events/Tasks that get created during one normal working day should be provided as NRF from the Client. These numbers can then furthermore be used as BaseLine KPI&#39;s for the load of the system.

- Number of Logins(per day/Hour/User)
- Open Cases, Events, Contacts, Tasks list (Loading times should be measured)
- Open one specific Case and edit it (In parallel with a higher number of Connections or multiple times in a row)
- Create Contact and add it to a Case
- Create multiple Tasks for one Case
- Create one Event and add multiple Cases to that event

### **6.6 Scaling Model**

The scaling model describes how the DataBase for the Test Environment should be populated.

Suggested model:

- Start by creating 10 Cases each Case with one Contact and One Task
- Scenario 1: Scale the number of Cases to 100, 200,500,1000,1500,3000,5000,10000,20K,50K,100K,150K,300K,500K and 1Million Cases
- Scenario 2: Create 1 Case with 10 Contacts and scale up the number of Contacts for the same Case: 100 Contacts, 200, 500, 1K , 2.5K 5K, 10K contacts for one Case
- Scenario 3: Once the scale of Scenario2 is completed, Scale-up by creating multiple Cases of the Type from Scenario2 : 10,50,100,200,300,500,800,1K cases like in the Scenario2 fully scaled up.
- Scenario 4: Create One Event with multiple Contacts and increase the number of contacts: 10,100,200,300,500,1K,2K,5K,10K,25K,50K
- Scenario 5: Link the Scenario2 to Scenario4
- Scenario 6: Create One Case with Multiple Events of the type from Scenario 4 linked to it and scale up the number of events linked to that Case
- Scenario 7: Create One Case with a lot of Contacts, a lot of Events and a lot of Tasks. Scale up by increasing the number of Tasks, Contacts, Events and Multiplying the Case with all linked elements.
- TBD

#### **6.6.1 Baselining**

The first course of action is to find a baseline.

Response times will be measured for Scenario 1 The Baseline then Will be saved into a Performance Report document.

Each Scenario will be measured and will have the results saved in the same Performance Report

#### **6.6.2 Load Testing**

After the baseline metrics are gathered, then the same simulation (Scenario), which simulates a load profile, is run with an increased number of users to test against the target volumes (TBD).

The idea of this load test is to test the system against a typical day&#39;s load, simulating the ramp-ups, day&#39;s peaks, and ramp-downs.

More Statistical data is needed in order to have the baselines for peaks, and ramp-ups+ ramp-downs. TBD

#### **6.6.3 Stress Testing**

The aim of stress testing is to find the breaking point of the system, i.e. at what point does the system becomes unresponsive. If auto-scaling is in place, the stress test will also be a good indicator at which point the system scales and new resources are added. For stress testing, the same simulation used for load testing is used but with a higher than expected load.

#### **6.6.4 Spike Testing**

Spike testing introduces a significant load on the system in a relatively short period of time (For Example doing a large Import). The aim of this test is to simulate a possible bottleneck on the server-side and checking when the normal Users perceive a decrease in Performance.

#### **6.6.5 Soak Testing**

Soak testing will run a load test for an extended period of time. The aim is to reveal any memory leaks and unresponsiveness or errors during the course of the soak test. We would typically use 80% of the load (used for load testing) for 24hrs, and/or 60% of the load for 48hrs.

## 7. Performance testing activities

The following activities are suggested to take place in order, to complete Performance Testing:

### **7.1 Performance Test Environment Build**

- Each new release should be deployed on the Performance environment.
- The load injectors should have enough capacity and should be managed remotely. Also, the location of the injectors should be agreed (different virtual Machines or just one machine with the Performance testing setup)
- Real-time monitoring and alerting mechanism should be in place and should cover the application, the servers as well as the load injectors. (TBD, in more technical details)
- Application logs should be accessible and verified after each Performance testing cycle.
- Performance Test Suite will be included in the Continous Integration system and will be executed after each release. Reporting of these Test Cycles should happen during first half of each sprint so that corrective actions can be assured by the development team so that performance decrease is kept to a minimum from sprint to sprint.

### **7.2 Use-Case Scripting**

- The performance testing tool which will be used is Katalon Studio + Jmeter plugin
- Any data requirements will be discussed for the use-cases to be scripted

### **7.3 Test Scenario Build**

- The type of the test to be executed (Load/Stress etc)
- The load profile/load model should be agreed for each test type (ramp-up/down, steps etc.)
- Incorporate think time into the scenarios this may vary for each scenario

### **7.4 Test Execution and Analysis**

The following tests should be executed in the following order:

- Baselining Test
- Load Test
- Stress Test
- Spike Test
- Soak Test
- Saturation Point Test

**Performance Test Process** :

1. Release New Sormas Version
2. Deploy new version to the Performance Environment
3. Execute Smoke tests in order to check if the deploy has been successful
4. Start the Performance Test Suite (CI/CD setup )
5. Analyze the results of the first execution
6. Restart the server + Clean the DataBase
7. Run the Performance Test Suite Again
8. Compare the results with the previous Test Run
9. If necessary, present the results to the Development team and create the issues for fixing the discovered problems
10. After the next release or HotFix start with step 2.

Ideally, 2 Test runs of each test type will be performed. After each test run the application might be fine-tuned in order to increase its performance and then another test cycle will commence. This Process should happen ideally during each sprint

### **7.5 Post-Test Analysis and Reporting**

- Capture and back-up all the relevant data reports and save the information in trackable
- Determine the success or failure by comparing the test results to the performance targets. If the targets are not met then the appropriate changes should be made and then another test execution cycle will commence. It is unknown how many execution cycles will be needed in order to meet the agreed targets.
- Document and present the test results to the team on a Sprint basis.

## 9 Test Scenarios

The following test scenarios will be suggested for implementation:

### **9.1 Scenario 1**

This scenario will be implemented first in order to decide the baseline for the Scaling Model paragraph 6.6 scenario2: 1 case with a scalable number of contacts

1. Make sure the environment has a medium number of Cases + contacts already in the Database
2. Create one Case with 10 Contacts assigned to it
3. Increase the number of Contacts assigned to that case from 10 to 50 incrementally
4. Repeat steps 2 and 3 until the number of cases increases gradually from 1-2(newly created cases to 10,15,20,30,50,80,100 new cases each of them with a maximum of 50 Contacts assigned

Create an export file after each incremental change for further uses.

This scenario can be scaled up for 2 Variables, the number of Contacts and the number of cases, hence, it is advisable to scale the number of contacts for the first Case to the maximum number agreed upon and afterwards either duplicate the Case or create new ones with the same logic.

Response times should be measured at Key moments in this process and documented for further comparison.

Suggested measurement moments:

- Get all cases before the process begins and save this as t1
- Create the first Case and read the times with each increment of the number of cases (t2,t3,t4,...)
- Get all cases and save the retrieve time t1.1
- Create/Duplicate a new/first Case and measure the times with each increment (t2.2,t3.2,t4.2...)
- Continue this measurement process until changes are present in the response times.

### **9.2 Scenario 2**

This scenario focuses on finding bottlenecks at the import process

1. Make sure the environment has a medium number of Cases and Contacts already in the Database
2. Have an import file with 1 Case and multiple Contacts ( can use the same logic as from Scenario1 step 2)
3. Measure the response time for the import of this file and save it as t1
4. Add more relevant data (Contacts) to the import file ( Scenario 1 step 3, already exported files can be used for this step)
5. Measure the response times after each incremental import (t1.1,t1.2,t1.3,...)
6. Continue this process until the import file has an import of one Case and the maximum agreed-upon number of Contacts
7. Duplicate/Create a new Case and increment the number of contacts for the new Case in the import file
8. Measure the response times after each incremental import (t2.1,t2.2,t2.3,...)
9. Increase the number of Cases+ Contacts added to the import file and measure the times. For each added Case save the times with the naming convention: t3 for 3 Cases, t4 for 4 Cases  ... tn for n Cases.

At the end of this Test Scenario, plot all the measured times on a Chart so that changes are easily trackable.

### **9.3 Scenario 3**

While Scenario 2 is being executed, another possible bottleneck might appear on the UI. Hence, measuring the response times of normal UI interactions needs to be tested.

1. Make sure the environment has a medium number of Cases and Contacts already in the Database
2. Start an Import file with 1 Case and multiple Contacts
3. Measure the response times of this import (t1)
4. Login on the same environment on the UI with another user (should try with the same user as well) and open the Case directory
5. Measure the time it takes to load the Case Directory on the web as (t1.w1)
6. Start another import with a bigger import file (the file from step 7 from Scenario 2)
7. Repeat step 4
8. Measure the response times on the UI while uploading the larger file
9. Repeat these steps until the response times start to decrease significantly

All these response times need to be plotted on the same graphic in order to have relevant information.

The graphic should contain response times of the API call for the import and the response times on the UI while the import is being processed on one axis and on the other the size of the import file. If the size is something standardized, the name of the file can be used on the graphic.

### **9.4 Scenario 4**

This Test Scenario will focus on benchmarking the initial synchronization with different sizes of the Database

### **9.5 Scenario 5**

This Test Scenario will focus on Loading and measuring on the web all detected duplicate cases

### **9.6 Scenario 6**

TBD
