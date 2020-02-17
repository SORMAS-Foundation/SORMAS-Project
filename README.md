<p align="center">
  <a href="https://sormas.org/">
    <img
      alt="SORMAS - Surveillance, Outbreak Response Management and Analysis System"
      src="logo.png"
      height="200"
    />
  </a>
  <br/>
  <a href="https://github.com/hzi-braunschweig/SORMAS-Project/blob/development/LICENSE"><img alt="License" src="https://img.shields.io/badge/license-GPL%20v3-blue"/></a> <a href="https://github.com/hzi-braunschweig/SORMAS-Project/releases/latest"><img alt="Latest Release" src="https://img.shields.io/github/v/release/hzi-braunschweig/SORMAS-Project"/></a> <img alt="Development Build Status" src="https://travis-ci.com/hzi-braunschweig/SORMAS-Project.svg?branch=development"/>
</p>

**SORMAS** (Surveillance Outbreak Response Management and Analysis System) is an open source eHealth system - consisting of separate web and mobile apps - that is geared towards optimizing the processes used in monitoring the spread of infectious diseases and responding to outbreak situations.

<p align="center"><img src="https://user-images.githubusercontent.com/23701005/74659600-ebb8fc00-5194-11ea-836b-a7ca9d682301.png"/></p>

## Table of Contents

* [Project Structure](#project-structure)
* [Server Management](#server-management)
* [Contributing](#contributing)
* [Implementation Guides](#implementation-guides)

## Project Structure
The project consists of the following modules:

- sormas-api: general business logic and definitions for data exchange between app and server
- sormas-app: the Android app
- sormas-backend: server entity services, facades, etc.
- sormas-base: base project that also contains build scripts
- sormas-ear: the ear needed to build the application
- sormas-rest: the REST interface
- sormas-ui: the web application

## Server Management

* [Setting up a SORMAS server](SERVER_SETUP.md)
* [Updating a SORMAS server](SERVER_UPDATE.md)

## Contributing
SORMAS is a community-driven project that is constantly evolving based on the needs of our users. If you want to get involved in contributing, there are a lot of ways for you to do so - no matter if you are a developer yourself, a user, involved in professional health care or just interested in the project. Please get in touch with us!

You can read more about possibilities to contribute to SORMAS in our [Contributing Readme](CONTRIBUTING.md).

## Implementation Guides

* [Adding license headers](ADDING_LICENSE.md)
* [How to add a new disease?](GUIDE_ADD_NEW_DISEASE.md)
* [How to add a new field?](GUIDE_ADD_NEW_FIELD.md)
