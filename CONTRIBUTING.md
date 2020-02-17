# Contributing

## Submitting an Issue

Please read adhere to the following guidelines when submitting new issues. This allows us to process your request as quickly as possible.

* [Bug Report](#bug-report)
* [Change Request](#change-request)
* [Feature Request](#feature-request)

### Bug Report

When submitting a bug report, providing us with as much information as possible helps us to identify the root of the problem and fix it as fast as possible. Ideally, we would need the following details:

* **Bug Description:** A description of what exactly happened, where it happened and under which circumstances. Provide as many details as possible.
* **Steps to Reproduce:** If possible, provide a step-by-step instruction on the order of actions you performed before the bug occurred. This helps us to reproduce it on our system.
* **Expected Behavior:** Quickly describe what you believe should've happened instead of the error you got.
* **Screenshots:** If possible, make at least one screenshot of the bug and include it in your bug report. You can do this by simply dragging-and-dropping the image file into the template you're filling out.
* **System Details:** Tell us which device you were using, on which SORMAS version the error occurred and, depending on whether you were using the mobile app or the web app, your Android version or the web browser.
* **Additional Information:** If there is anything else you want to add to your request, you can put it here.

### Change Request

When submitting a change request or a feature request, describing the feature you would like us to add or change in detail will make sure that we have an easier time to discuss it internally and get back to you as quickly as possible.

* **Feature Description:** Please describe the feature you would like us to change as it is in SORMAS right now so we know the subject of the issue.
* **Problem Description:** Tell us why you want us to change the feature and what you believe is wrong with the way it's currently designed.
* **Proposed Change:** Describe in as much detail as possible how you would like us to change the feature.
* **Possible Alternatives:** If possible, provide alternative solutions in case your proposed change can not be implemented for any reason.
* **Additional Information:** If there is anything else you want to add to your request, you can put it here.

### Feature Request

When submitting a change request or a feature request, describing the feature you would like us to add or change in detail will make sure that we have an easier time to discuss it internally and get back to you as quickly as possible.

* **Situation Description:** Please describe why your proposed feature is required and how exactly SORMAS is not able to do what you want it to do right now.
* **Feature Description:** Describe in as much detail as possible how your proposed feature should look like and what it should do.
* **Possible Alternatives:** If possible, provide alternative solutions in case your proposed feature can not be implemented in the way you outlined it for any reason.
* **Additional Information:** If there is anything else you want to add to your request, you can put it here.

## Getting Involved

* [Translating SORMAS](I18N.md)
* [Defining new diseases](SOP_DISEASES.md)

## SORMAS Development

* [Setting up your local environment](DEVELOPMENT_ENVIRONMENT.md)
* [Performing load tests on a SORMAS server](LOAD_TESTING.md)

1. Use the eclipse code formatter (Ctrl+Shift+F) and the Android Studio code formatter for the **sormas-app** project.
2. Each commit should be related to a single issue on Github and have a reference to this issue as well as a short description on what has been done in this commit:
   > #61 - added model to define classification, apply automatic case classification whenever a field value changes
3. Each pull request should be related to a single issue (if possible). 

## SORMAS Sprint Board

The SORMAS sprint board is segmented into the following categories:

* **Backlog:** Issues that have been selected to be done in the current sprint, but for which work has not yet started.
* **In Progress:** Issues that have been assigned to a contributor and for which work has started.
* **Waiting:** Issues for which work has started and that have been put on hold, e.g. because action or feedback by an external contributor is required.
* **Review:** Issues that have been resolved, but not been reviewed by another contributor yet.
* **Done:** Issues that have been resolved, reviewed and satisfy the Definition of Done.

The general workflow is that whenever a contributor starts working on an issue, they **assign** themselves to it and manually **move the issue** from **Backlog** to **In Progress**.

The GitHub project has been configured to **automatically** move issues that are closed to **Review** and issues that are reopened back to **In Progress**.
