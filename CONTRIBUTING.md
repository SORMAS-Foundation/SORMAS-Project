## Contributing

* [Translating SORMAS](I18N.md)
* [Defining new diseases](SOP_DISEASES.md)

### Developers

* [Setting up your local environment](DEVELOPMENT_ENVIRONMENT.md)
* [Performing load tests on a SORMAS server](LOAD_TESTING.md)

1. Use the eclipse code formatter (Ctrl+Shift+F) and the Android Studio code formatter for the **sormas-app** project.
2. Each commit should be related to a single issue on Github and have a reference to this issue as well as a short description on what has been done in this commit:
   > #61 - added model to define classification, apply automatic case classification whenever a field value changes
3. Each pull request should be related to a single issue (if possible). 

### SORMAS Sprint Board

The SORMAS sprint board is segmented into the following categories:

* **Backlog:** Issues that have been selected to be done in the current sprint, but for which work has not yet started.
* **In Progress:** Issues that have been assigned to a contributor and for which work has started.
* **Waiting:** Issues for which work has started and that have been put on hold, e.g. because action or feedback by an external contributor is required.
* **Review:** Issues that have been resolved, but not been reviewed by another contributor yet.
* **Done:** Issues that have been resolved, reviewed and satisfy the Definition of Done.

The general workflow is that whenever a contributor starts working on an issue, they **assign** themselves to it and manually **move the issue** from **Backlog** to **In Progress**.

The GitHub project has been configured to **automatically** move issues that are closed to **Review** and issues that are reopened back to **In Progress**.
