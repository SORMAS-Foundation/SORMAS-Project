<p align="center">
  <a href="https://sormas.org/">
    <img
      alt="SORMAS - Surveillance, Outbreak Response Management and Analysis System"
      src="logo.png"
      height="200"
    />
  </a>
</p>

# Table of Contents

* [Project Structure](#project-structure)
* [Releases & Server Setup](#releases-and-server-setup)
* [Contributing](#contributing)

## Project Structure
The project consists of the following modules:

- sormas-api: general business logic and definitions for data exchange between app and server
- sormas-app: the android app
- sormas-backend: server entity services, facades, etc.
- sormas-base: base project that also contains build scripts
- sormas-ear: the ear needed to build the application
- sormas-rest: the rest interface
- sormas-ui: the web application

## Releases and Server Setup

* [Installing a SORMAS server](SERVER_SETUP.md)
* [Updating a SORMAS server](SERVER_UPDATE.md)
* [Latest release](https://github.com/hzi-braunschweig/SORMAS-Project/releases/latest)
* [Creating a release](RELEASE.md)

## Contributing

* TODO: Reporting an issue
* TODO: Code of conduct
* [Setting up your local environment](DEVELOPMENT_ENVIRONMENT.md)

### Most Important
1. Use the Eclipse code formatter (Ctrl+Shift+F).

   Use the Android Studio code formatter for the **sormas-app** project
2. Each commit should be related to a single issue on Github and have a reference to this issue. Example (5a8d101):
   
   > #460 - added contacts dashboard (without content yet), added abstract components for dashboard view, filter layout and statistics component
3. Each pull request should be related to a single issue as-well (if possible). 
