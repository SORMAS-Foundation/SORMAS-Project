<p align="center">
  <a href="https://sormas.org/">
    <img
      alt="SORMAS - Surveillance, Outbreak Response Management and Analysis System"
      src="logo.png"
      height="200"
    />
  </a>
</p>

**SORMAS** (Surveillance Outbreak Response Management and Analysis System) is an open source eHealth system - consisting of separate web and mobile apps - that is geared towards optimizing the processes used in monitoring the spread of infectious diseases and responding to outbreak situations.

<img alt="Development Build Status" src="https://travis-ci.org/MateStrysewskeSym/SORMAS-Open.svg?branch=development"/>

# Table of Contents

* [Project Structure](#project-structure)
* [Releases & Server Setup](#releases-and-server-setup)
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

## Releases and Server Setup

* [Installing a SORMAS server](SERVER_SETUP.md)
* [Updating a SORMAS server](SERVER_UPDATE.md)
* [Latest release](https://github.com/hzi-braunschweig/SORMAS-Project/releases/latest)
* [Creating a release](RELEASE.md)

## Contributing

* [Translating SORMAS](I18N.md)
* [Defining new diseases](SOP_DISEASES.md)
* TODO: Reporting an issue
* TODO: Code of conduct
* [Setting up your local environment](DEVELOPMENT_ENVIRONMENT.md)
* [Performing load tests on a SORMAS server](LOAD_TESTING.md)

### Developers
1. Use the Eclipse code formatter (Ctrl+Shift+F).  
   Use the Android Studio code formatter for the **sormas-app** project
2. Each commit should be related to a single issue on Github and have a reference to this issue. Separate subject from body with a blank line.   
   > Automatic case classification for existing SORMAS diseases #61
   >
   > build model to define classification  
   > apply automatic case classification whenever a field value changes
3. Each pull request should be related to a single issue as-well (if possible). 

## Implementation Guides

* [Adding license headers](ADDING_LICENSE.md)
* [How to add a new disease?](GUIDE_ADD_NEW_DISEASE.md)
* [How to add a new field?](GUIDE_ADD_NEW_FIELD.md)
