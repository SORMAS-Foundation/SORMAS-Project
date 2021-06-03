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
* Clone the project and open the *sormas-e2e-performance-tests* subproject


## Test execution

Non-GUI execution of the performance tests is recommended:

> Execution from a terminal window:
* open the console and navigate to the Jmeter bin folder
* type the following in the command line:  jmeter -n -t 'PathToTheJMXFile'\SormasPoc.jmx 
  -l 'PathWhereTheResultsWillBeSaved'\Results.csv -e -o 'PathWhereTheHtmlReportWillBeSaved'\reports

> Example:
jmeter -n -t C:\projects\sormas\performance\SormasPoc.jmx -l C:\projects\sormas\performance\results\10threads\Results3.csv
-e -o C:\projects\sormas\performance\results\10threads\reports


## Reporting

* After running the tests with the above mentioned command, you will be able to find the html report in the `reports` folder
that you mentioned at execution. Double-click on `index.html` file, and the generated report should open in the default browser.
