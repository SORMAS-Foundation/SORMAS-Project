<p align="center">
  <a href="https://sormas.org/">
    <img
      alt="SORMAS - Surveillance, Outbreak Response Management and Analysis System"
      src="logo.png"
      height="200"
    />
  </a>
  <br/>
  <a href="https://github.com/hzi-braunschweig/SORMAS-Project/blob/development/LICENSE"><img alt="License" src="https://img.shields.io/badge/license-GPL%20v3-blue"/></a> <a href="https://github.com/hzi-braunschweig/SORMAS-Project/releases/latest"><img alt="Latest Release" src="https://img.shields.io/github/v/release/hzi-braunschweig/SORMAS-Project"/></a><a href="https://github.com/hzi-braunschweig/SORMAS-Project/actions?query=workflow%3A%22Java+CI+with+Maven%22"><img alt="Development Build Status" src="https://github.com/hzi-braunschweig/SORMAS-Project/workflows/Java%20CI%20with%20Maven/badge.svg?branch=development"/></a> <a href="https://gitter.im/SORMAS-Project"><img alt="Gitter" src="https://badges.gitter.im/SORMAS-Project/dev-support.svg"/></a> <a href="https://twitter.com/SORMASDev"><img alt="Twitter" src="https://img.shields.io/twitter/follow/SORMASDev?label=%40SORMASDev&style=social"/></a>
</p>
<br/>

**SORMAS** (Surveillance Outbreak Response Management and Analysis System) is an open source eHealth system - consisting of separate web and mobile apps - that is geared towards optimizing the processes used in monitoring the spread of infectious diseases and responding to outbreak situations.

#### How Does it Work?
You can give SORMAS a try on our demo server at https://demoversion.sormas-oegd.de!

#### How Can I Get Involved?
Read through our [*Contributing Readme*](CONTRIBUTING.md) and contact us at sormas@helmholtz-hzi.de or join our [developer chat on Gitter](https://gitter.im/SORMAS-Project) to learn how you can help to drive the development of SORMAS forward and to get development support from our core developers. SORMAS is a community-driven project, and we'd love to have you on board! If you want to contribute to the code, please strictly adhere to the [*Development Environment*](DEVELOPMENT_ENVIRONMENT.md) guide to ensure that everything is set up correctly. Please also make sure that you've read the [*Development Contributing Guidelines*](CONTRIBUTING.md#development-contributing-guidelines) before you start to develop, and either follow or regularly check our Twitter account <a href="https://twitter.com/SORMASDev" target="_blank">@SORMASDev</a> to stay up to date with our schedule, new releases, guideline changes and other announcements.

#### How Can I Report a Bug or Request a Feature?
If you want to report a **security issue**, please follow our guideline for [*Responsible Disclosure*](SECURITY.md).
For bugs without security implications, change and feature requests, please [create a new issue](https://github.com/hzi-braunschweig/SORMAS-Project/issues/new/choose) and read the [*Submitting an Issue*](CONTRIBUTING.md#submitting-an-issue) guide for more detailed instructions. We appreciate your help!

#### Which Browsers and Android Versions Are Supported?
SORMAS officially supports and is tested on **Chromium-based browsers** (like Google Chrome) and **Mozilla Firefox**, and all Android versions starting from **Android 7.0** (Nougat). In principle, SORMAS should be usable with all web browsers that are supported by Vaadin 8 (Chrome, Firefox, Safari, Edge, Internet Explorer 11; see https://vaadin.com/faq).

#### Is there a ReST API documentation?
Yes! Please download the [latest release](https://github.com/hzi-braunschweig/SORMAS-Project/releases/latest) and copy the content of /deploy/openapi/sormas-rest.yaml to an editor that generates a visual API documentation (e.g. https://editor.swagger.io/).
<br/>
Runtime Swagger documentation of the External Visits Resource (used by external symptom journals such as CLIMEDO or PIA) is also available at ``<<host>>/sormas-rest/openapi.json`` or ``<<host>>/sormas-rest/openapi.yaml``

<p align="center"><img src="https://user-images.githubusercontent.com/23701005/74659600-ebb8fc00-5194-11ea-836b-a7ca9d682301.png"/></p>

## Project Structure
The project consists of the following modules:

- **sormas-api:** General business logic and definitions for data exchange between app and server
- **sormas-app:** The Android app
- **sormas-backend:** Server entity services, facades, etc.
- **sormas-base:** Base project that also contains build scripts
- **sormas-ear:** The ear needed to build the application
- **sormas-rest:** The REST interface; see [`sormas-rest/README.md`](sormas-rest/README.md)
- **sormas-ui:** The web application
- **sormas-base/dependencies:** dependencies to be deployed with the payara server
- **sormas-cargoserver:** setup for a local dev server using maven-cargo

## Server Management

* [Installing a SORMAS server](SERVER_SETUP.md)
* [Updating a SORMAS server](SERVER_UPDATE.md)
* [Customizing a SORMAS server](SERVER_CUSTOMIZATION.md)
