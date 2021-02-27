# Load Testing Guide

## Introduction
This guide will help you set up an infrastructure to manually perform load tests on a SORMAS server (automatic load tests may become part of the release cycle at a later point in time).
Please note that these tests are intended to measure the time specific requests require to be performed, not to stress test the server. Make sure to set up a local development environment or a test server to execute these tests as running them on a productive system is not recommended.

## Install Gatling
To run the tests provided in this repository, you will need to install Gatling on your computer. Please follow the instructions on <https://gatling.io/docs/current/installation> ("Using the Bundle").

## Download Test Simulations
We have created simulations (which are basically test scenarios) that test the most performance-critical actions in the SORMAS apps:

* Open the surveillance dashboard
* Open the case directory
* Retrieve all cases for a mobile user via the REST interface
* Retrieve all persons for a mobile user via the REST interface
* Retrieve all infrastructure data via the REST interface

Download the latest [sormas_load_tests.zip](https://github.com/hzi-braunschweig/SORMAS-Project/blob/development/sormas_load_tests.zip) file and extract its contents into your Gatling directory.

## Adjust Simulation Configuration
Open the ``SimulationConfig.scala`` file in a text editor. Change the default value of the ``serverUrl`` variable to the URL of the server you want to test. If you're using a fresh SORMAS installation, you can leave the ``mobileUsername`` and ``mobilePassword``variables as they are. Otherwise, type in the username and password of a mobile user (most likely a Surveillance Officer) on your system that has access to as many cases as possible.
Finally, you can edit the ``numberOfUsers`` variable to determine the number of parallel requests performed when running the tests. Leaving these at 1 is a good idea to find out whether the tests are passing, but to actually load test your server, you want to increase this number to find out how its performing when multiple users are doing costly actions at the same time.

## Run the Load Tests
Please note that currently, to run the ``CaseDirectorySimulation`` and ``DashboardSimulation`` scenarios, you need to log in to the web app first because the actual login operation is not part of the simulations. The simulations will still execute successfully if you don't, but the results won't be meaningful in any regard.

Once everything is set up, navigate to the ``bin`` folder of your Gatling installation and run the ``gatling.bat`` file if you're using Windows or the ``gatling.sh`` file if you're using Linux.
A command window will be opened that loads all the simulations from your user-files directory. Choose the simulation you want to run by typing in its number. Once the simulation has been executed, you can find its results in the ``results`` folder.
