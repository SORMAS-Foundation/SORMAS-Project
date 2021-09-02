# Table of contents

* [General info](#general-info)
* [Setup](#setup)
* [Test execution](#test-execution)
* [Reporting](#reporting)

## General info

This project aims to help identify performance issues on the Sormas rest api's.

## Setup

* Install Java JDK 11 on your local machine
* Install apache Jmeter
* Download the property file reader external library from:
  [tag-jmeter-extn-1.1.zip](#https://www.vinsguru.com/download/87/?_ga=2.188716968.1652712557.1623012940-501989637.1623012940)
* Place the downloaded jar in the JMETER_HOME/lib/ext foler.
* Clone the project and open the *sormas-e2e-performance-tests* subproject

## Test execution

Non-GUI execution of the performance tests is recommended:

> Execution from a terminal window:

* open the console and navigate to the Jmeter bin folder
* type the following in the command line:  
  jmeter -n -t 'PathToTheJMXFile'\SormasPoc.jmx -Jmodule='moduleName'
  -l 'PathWhereTheResultsWillBeSaved'\Results.csv -e -o 'PathWhereTheHtmlReportWillBeSaved'\reports
  
-Jmodule=daily-build-scenario
-Jmodule=load-scenario

> Example:
jmeter -n -t C:\projects\sormas\performance\SormasPoc.jmx -Jmodule=daily-build-scenario
-l C:\projects\sormas\performance\results\Results3.csv -e -o C:\projects\sormas\performance\reports

## Using Variables

The variables are preconfigured to run the tests against a specific test instance.
If you wish to run the tests against another system (e.g. localhost), you can exchange the arguments as needed.
Please make sure the database is populated with accessible data before trying to access it (e.g. create cases before trying to GET them).

## Reporting

* After running the tests with the above mentioned command, you will be able to find the html report
  in the `reports` folder that you mentioned at execution. Double-click on `index.html` file, and
  the generated report should open in the default browser.
