# Contribution Guidelines

If you want to contribute to SORMAS by any means - for example by submitting a bug report, requesting a new feature, translating the application into a new language, or actively contributing to the source code -, please make sure to read through and follow these guidelines. This allows us to consider and process your contribution as quickly and smoothly as possible. If there is anything unclear to you or you think that this guide is lacking coverage of a specific topic, please create a support issue or get in touch with us via [Gitter](https://gitter.im/SORMAS-Project).

## Table of Contents

* [Submitting an Issue](#submitting-an-issue)
* [Contributing to the Project](#contributing-to-the-project)
* [Contributing to the Code](#contributing-to-the-code)

## Submitting an Issue

**Before creating a new issue, please search the repository for similar issues first to avoid duplicates!** You can do this by using the search field in the menu bar. If you find an issue that already covers your request or seems very similar, please comment on that issue.

Please make sure to always use one of the templates that are automatically presented to you when [creating a new issue](https://github.com/hzi-braunschweig/SORMAS-Project/issues/new/choose) because it ensures that your issue is structured and ideally contains all the information that we need. Please add information to every mandatory section of the issue templates, and try to fill in the optional sections if possible. Please note that we might have to close issues that are not created this way.

If you want to report a **security issue**, please follow our guidelines for [*Responsible Disclosure*](SECURITY.md).

There are four types of issues based on our available templates:

* [Bug Report](#bug-report)
* [Change Request](#change-request)
* [Feature Request](#feature-request)
* [Support Request](#support-request)

### Bug Report

Bug reports cover everything that leads to the application behaving in an unintended way, including crashes, unexpected error messages, data loss, etc. If something seems to be working correctly but does not necessarily match your expectations of how it should be working, please consider opening a change request instead.

* **Bug Description:** A description of what exactly happened, where it happened and under which circumstances. Provide as many details as possible, even if they might seem irrelevant to you.
* **Steps to Reproduce:** If possible, provide a step-by-step instruction on the order of actions you performed before the bug occurred. This helps us to reproduce it on our system. Generally *optional*, but likely necessary for more complex bugs.
* **Expected Behavior:** Quickly describe what you believe should have happened instead of the error you got. *Optional* when the error is identified by a crash or an error message, but likely necessary if the bug is the result of a misbehavior of the application.
* **Screenshots:** If possible, make at least one screenshot of the bug and include it in your bug report. You can do this by simply dragging-and-dropping the image file into the template you're filling out. *Optional*, but very helpful for us.
* **System Details:** Tell us which device you were using, on which SORMAS version the error occurred and, depending on whether you were using the mobile app or the web app, your Android version or web browser.
* **Additional Information:** If there is anything else you want to add to your request, you can put it here. *Optional*.

### Change Request

Change requests cover features that are already part of SORMAS. This primarily includes aspects (or whole features) for which you would like to request an altered behavior, but also small extensions (e.g. additional values being added to a dropdown field).

* **Feature Description:** Please describe the feature you would like us to change like it is in SORMAS right now. If it is about a relatively big or a very general feature, namig it briefly might be enough. If it is about a very specific detail of said feature, please try to be more specific.
* **Problem Description:** Tell us why you want us to change the feature and what you believe is wrong or could be improved about the way it is currently designed.
* **Proposed Change:** Describe in as much detail as possible how you would like us to change the feature and what the expected outcome of your request should look like.
* **Possible Alternatives:** If possible, provide alternative solutions in case your proposed change can not be implemented for any reason. This is generally *optional* but might be requested at a later point in time.
* **Additional Information:** If there is anything else you want to add to your request, you can put it here. *Optional*.

### Feature Request

These types of issue cover everything that involves adding new features to SORMAS. This includes both very large additions like completely new app sections, but also smaller ones like adding a new field to an existing form.

* **Situation Description:** Please describe why your proposed feature is required, why SORMAS in its current state is not able to do what you want it to do, and in which way it would benefit from your feature being implemented.
* **Feature Description:** Describe in as much detail as possible how your proposed feature should look like and what it should do.
* **Possible Alternatives:** If possible, provide alternative solutions in case your proposed feature can not be implemented in the way you outlined it for any reason. This is generally *optional* but might be requested at a later point in time.
* **Additional Information:** If there is anything else you want to add to your request, you can put it here. *Optional*.

### Support Request

If you have problems setting up a SORMAS server or your development environment, don't know how a specific functionality in SORMAS is supposed to work, or have any other request that is not directly associated with a bug report, change or feature request, a support request is likely the correct type of issue for your concern. Alternatively, you can join our [Gitter channel](https://gitter.im/SORMAS-Project) and ask your question over there which might potentially give you a quicker and more comfortable way to talk about your request.

## Contributing to the Project

Even as a non-developer, there are a lot of things that you can help us with. If there is something you would like to do that you don't find instructions about here, make sure to contact us at sormas@helmholtz-hzi.de and let us know how we can assist you!

* [Translating SORMAS](I18N.md)
* [Defining new diseases](SOP_DISEASES.md)

## Contributing to the Code

If you're interested in participating in the development of SORMAS, you can use the following guides to get started. If you have problems setting up your development environment or don't know what you can work on, don't hesitate to contact us at sormas@helmholtz-hzi.de!

* [Setting up your local environment](DEVELOPMENT_ENVIRONMENT.md)
* [Adding license headers](ADDING_LICENSE.md)
* [How to add a new disease?](https://github.com/hzi-braunschweig/SORMAS-Project/wiki/Adding-a-New-Disease)
* [How to add a new field?](https://github.com/hzi-braunschweig/SORMAS-Project/wiki/Adding-a-New-Field-to-an-Entity)

### Development Contributing Guidelines

1. Use the eclipse code formatter (Ctrl+Shift+F) and the Android Studio code formatter for the **sormas-app** project. To not forget this, use save actions [for your IDE](DEVELOPMENT_ENVIRONMENT.md).
2. Rules for blank lines (which cannot be enforced by automatic formatting):
    - Use one blank line after method definition (but usually not for one liners like getters/setters or delegation).
    - Use one blank line to separate statements within a code block from each other when you start a new logical block.
    - Do not use blank lines after each statement.
    - No blank line after last statement of a block, but closing } with proper intendation in the next line.
    - No blank line between two closing }.
3. You can use ``//@formatter:off`` and ``//@formatter:on`` to encapsulate a code block where the automated formatting messes up the readability. Try to use this rarely and use proper intendation nevertheless.
4. Rules for code comments:
    - Separate code and comment: Set the comment before the statement(s) you want to explain.
5. Each commit should be related to a single issue on Github and have a reference to this issue as well as a short description on what has been done in this commit:
   > #61 - added model to define classification, apply automatic case classification whenever a field value changes
6. Each pull request should be related to a single issue (if possible).

### SORMAS Product Backlog

The board **Product Backlog** is used to plan, refine and prioritize the tickets for the upcoming sprints.
The sorting from top to bottom in every column reflects the priority for the product. The Product Owner is responsible to put tickets into the Backlog and keep the ticket information updated.

The Product Backlog contains the following columns:
* **Backlog:** Issues that have been identified by the Product Owner to be done in the next sprints. There can be a column for each Scrum Team if it fits the need.
* **Sprint n:** Contain tickets picked by the Product Owner to be done in the named sprint. Text notes or separate columns are used to separate issues between Scrum Teams.
  It gives a forecast what might come in the upcoming sprint and it is the starting point for the Sprint Planning. Every ticket the Development Team do not pick into their Sprint Backlog needs to be moved back to the Backlog column or one sprint further.
* **Done:** Tickets that are closed (usually resolved within the running sprint) are moved here **automatically**. The sorting does not represent the priority here any more.


### SORMAS Sprint Backlog

The board **Sprint Backlog** exists for each Scrum Team and is segmented into the following categories:

* **Backlog:** Issues that have been selected by the Development Team to be done in the current sprint, but for which work has not yet started. The sorting top to bottom on this column reflects the priority given by the Product Owner at the time of the Sprint Planning.
* **In Progress:** Issues that have been assigned to a contributor and for which work has started.
* **Waiting:** Issues for which work has started and that have been put on hold, e.g. because action or feedback by an external contributor is required.
* **Review:** Issues that have been resolved, but not been reviewed by another contributor yet. The ticket status is usually **Open**, but **Closed** is also allowed if no code change or merge is needed.
* **Testing:** Issues that have been reviewed and merged to **development** branch to be tested and verified on a central TEST instance. The ticket status is supposed to be **Closed**.
* **Done:** Issues that have been resolved, reviewed and satisfy the Definition of Done. The ticket status is supposed to be **Closed**.

The general workflow is that whenever a contributor starts working on an issue, they **assign** themselves to it and manually **move the issue** from **Backlog** to **In Progress**.
Transitions to **Waiting** and **Review** also need to be done manually. When the developer is done with all work (no code changes or merges needed, milestone is set), the ticket is supposed to be closed to go automatically to **Testing**.
Approved tickets are supposed to be moved manually from **Testing** to **Done*.

The GitHub project has been configured to **automatically** move issues that are closed to **Testing** and issues that are reopened back to **In Progress**.

The Development Team is responsible to keep the tickets up to date on this board and to assign the appropriate milestone in which the work is going to be released.


### Managing dependencies

For managing Java libraries as dependencies, they are managed by Maven and listed in *sormas-base/pom.xml*.  The purpose of a centralized management is to have an overview of the used libraries and adjust for new versions.

1. **Payara modules**: Provided by Payara in *{payara-home}/glassfish/modules* and used in that version by other libs.
2. **Domain libs**: Provided in Payara domain under *{payara-domain}/lib* to be usable by deployed artifacts (ear, war). They have to be listed in *sormas-base/dependencies/serverlibs.pom*. Usually for helper libraries that several artifacts need.
3. **Compile dependencies**: Bundled in respective artifacts who need the dependency explicitly. Usually for dependencies singularly needed in one artifact.
4. **Test libraries**: Libraries used in automated tests in one or more modules.

Due to the separate build management tool Gradle for *sormas-app*, there exists a redundant listing of compile dependencies in *sormas-app/app/build.gradle*.


### Eclipse Troubleshooting

Unfortunatley, when using eclipse together with the Payara Tools, there are a number of deployment problems that you might run into. Examples of these include:

* ClassDefNotFoundExceptions after deploying the artifacts and logging in to the web app
* Error messages in eclipse telling you that the deployment failed

There are a couple of things you can do to fix these problems:

* Do a Maven update for all projects
* Stop and restart the server
* Re-deploy the server artifacts

If the problem occurred right after you've pulled new code from GitHub, your safest bet is probably to start with the Maven update. For most other problems, a simple re-deployment or, if necessary, server restart should suffice.

When you have problems like this - `An internal error occurred during: "Polling news feeds".  javax/xml/bind/JAXBContext` - then disable setting `Window --> Preferences --> General --> News --> "Enable automatic news polling"` (may happen when running Eclipse with JDK 11).
