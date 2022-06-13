# Contributing Guidelines

If you want to contribute to SORMAS by any means - for example by submitting a bug report, requesting a new feature, translating the application into a new language, or actively contributing to the source code -, please make sure to read through and follow these guidelines.
This allows us to consider and process your contribution as quickly and smoothly as possible. If there is anything unclear to you or you think that this guide is lacking coverage of a specific topic, please create a support issue or get in touch with us via [Gitter](https://gitter.im/SORMAS-Project).

## Table of Contents

* [Submitting an Issue](#submitting-an-issue)
  * [Bug Report](#bug-report)
  * [Change Request](#change-request)
  * [Feature Request](#feature-request)
  * [Support Request](#support-request)
* [Contributing to the Project](#contributing-to-the-project)
* [Contributing to the Code](#contributing-to-the-code)
  * [Development Contributing Guidelines](#development-contributing-guidelines)
  * [Picking Issues for Development](#picking-issues-for-development)
  * [Submitting Pull Requests](#submitting-pull-requests)

## Submitting an Issue

**Before creating a new issue, please search the repository for similar issues first to avoid duplicates!** You can do this by using the search field in the menu bar. If you find an issue that already covers your request or seems very similar, please comment on that issue.

Please make sure to always use one of the templates that are automatically presented to you when [creating a new issue](https://github.com/hzi-braunschweig/SORMAS-Project/issues/new/choose) because it ensures that your issue is structured and ideally contains all the information that we need.
Please add information to every mandatory section of the issue templates, and try to fill in the optional sections if possible. Do not remove any section because they might be filled later by the development team.
Please note that we might have to close issues that are not created this way.

If you want to report a **security issue**, please follow our guidelines for [*Responsible Disclosure*](SECURITY.md).

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

* **Feature Description:** Please describe the feature you would like us to change like it is in SORMAS right now. If it is about a relatively big or a very general feature, naming it briefly might be enough. If it is about a very specific detail of said feature, please try to be more specific.
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

### Task

A task that needs to be done but does not directly change anything about the product. This could for example be the preparation of an upcoming feature/change, optimization of processes on GitHub, or update of one of the guides or Wiki articles in this repository.

* **Problem Description:** Please describe the requirement or problem that is supposed to be solved.
* **Proposed Solution:** Describe in as much detail how the proposed solution should look like and what should be done in order to achieve it.
* **Possible Alternatives:** If possible, provide alternative solutions in case your proposed change can not be realized for any reason. This is generally *optional* but might be requested at a later point in time.
* **Additional Information:** If there is anything else you want to add to the task, you can put it here. *Optional*.

### Support Request

If you have problems setting up a SORMAS server or your development environment, don't know how a specific functionality in SORMAS is supposed to work, or have any other request that is not directly associated with a bug report, change or feature request, a support request is likely the correct type of issue for your concern.
Alternatively, you can join our [Gitter channel](https://gitter.im/SORMAS-Project) and ask your question over there which might potentially give you a quicker and more comfortable way to talk about your request. If your request is about a specific problem, please make sure to provide as much information as possible.

## Contributing to the Project

There are a lot of ways in which you can contribute to this project as a non-developer. If there is something you would like to do that you don't find instructions about here - or if you want to learn how you can get involved - please contact us at sormas@helmholtz-hzi.de and let us know how we can assist you!

Some possibilities to contribute to SORMAS are:

* [Helping with translation](I18N.md)
* [Defining new diseases](SOP_DISEASES.md)

## Contributing to the Code

If you're interested in participating in the development of SORMAS, please follow the [Development Environment Setup Instructions](DEVELOPMENT_ENVIRONMENT.md) before you start developing.
If you have problems setting up your development environment or need assistance in choosing the first issue to work on, please get in touch with us by joining our [Gitter channel](https://gitter.im/SORMAS-Project) or contacting us at sormas@helmholtz-hzi.de.
Additionally, our [Wiki](https://github.com/hzi-braunschweig/SORMAS-Project/wiki) contains some specific development guides that cover common issues like adding new fields to an entity that we suggest to check out before you start implementing something related to those topics.

### Development Contributing Guidelines

In addition to the guidelines covered in the Development Environment Setup Instructions, please ensure to adhere to the following principles and procedures while developing code for SORMAS:

1. Remember to always apply code formatting and import reordering for all classes you work on; we recommend to use the Save Actions plugin as described in the setup instructions instead of manually executing these steps.
2. Some code formatting rules can't be enforced by the code formatter. Please make sure to write your code in accordance to the following rules:
    - When defining a method, enter a blank line before starting to write its body (except for methods with only one line of code, e.g. most getters and setters).
    - Use a blank line to separate logical blocks of code within a method.
    - Apart from those, don't use blank lines where they are not necessarily needed to keep the code compact and readable.
    - Don't use blank lines after the last statement of a block, but a closing `}` with proper indentation in the next line instead.
    - Don't use blank lines between two closing `}`.
3. You can use `//@formatter:off` and `//@formatter:on` to encapsulate code blocks that you don't want automatic code formatting to be applied to, e.g. because it would mess up readability. Please only use this if really needed and try to use proper indentation nonetheless.
4. Separate code and comments, i.e. write the comment in a separate line before the statement that you want to explain.
5. When you create new classes, please add license headers to them according to the [Adding License Headers guide](ADDING_LICENSE.md)
6. Commit messages have to be related to a specific issue on GitHub and reference its issue number as well as include a short description on what has been done in the commit. We might reject pull requests that violate this principle and ask you to re-submit it with proper commit messages. An acceptable commit message could look like this:
   > #61 - added model to define classification, apply automatic case classification whenever a field value changes

### Picking Issues for Development

When picking tasks for development, you can either search the repository for existing issues that you would like to work on, or you can submit your own issues if you don't find any that cover your topic of interest (see the "Submitting an Issue" section of this guide).
While it is not mandatory, we heavily suggest to mark issues that you want to work on with the `approval requested` label and wait for one of the core developers to respond to the issue in one of the following ways. If you didn't create the issue yourself but picked an existing one, please also add a comment to indicate that you would like to work on it.

* The developer might request clarification concerning the specifications of the issue; please try to to answer their questions or extend the issue description and wait for their feedback.
* The developer might suggest changes to the functional or technical specifications to ensure that it fits the overall vision of the application; please confirm these suggestions or discuss them with the developer if you don't agree with them.
* The developer might approve the issue by adding an approval comment and removing the `approval requested` label. This means that we believe that the specifications are complete and the purpose of the issue serves the overall vision of SORMAS. At this point you can start development on the issue.
* The developer might ask you to pick another issue for development. This will usually only happen if the issue you have requested approval for is already planned to be developed by the core development team for the current or next milestone.
* The developer might reject the issue altogether. This will usually only happen if it doesn't fit the overall vision of the application at all or is a duplicate, and most of the time we will discuss this with you before closing the issue.

The advantage of adhering to this process is that you can be sure that there won't be any major change requests in terms of functional or technical specifications once you've submitted a pull request containing your work, and that discussions concerning these topics can be led and finished before starting development.
If you have labeled an issue with `approval requested` but don't receive any feedback on it for a few days, please add a comment that directly tags [@MateStrysewske](https://github.com/MateStrysewske), [@MartinWahnschaffe](https://github.com/MartinWahnschaffe) or [@markusmann-vg](https://github.com/markusmann-vg).

### Submitting Pull Requests

Contributing to the SORMAS code requires you to submit pull requests that will be reviewed by one of our core developers. Once a pull request has been submitted to our repository, a developer will either assign themselves as its reviewer or we will get back to you in case we won't be able to review it in time.
This may e.g. happen if your pull request involves a lot of technical changes that we would like to merge together with other issues of the same nature or that could potentially break a lot of logic. Usually though, the general process looks like this:

1. A developer assigns themselves as the reviewer of your pull request. Please wait until the review is done; if you think that the review is taking too long, feel free to add a comment to the pull request as a reminder to the developer.
2. The developer might request changes to the code. If that's the case, please implement the requested changes or answer to their change request if you have questions or don't agree with a specific part of the request.
3. Once you've implemented all requested changes, please request another review by the assigned developer by clicking on the "Re-request review" icon next to their name.
4. As soon as the developer is happy with the submitted code (which might require multiple iterations of step 2 and 3, especially for larger pull requests), they will merge it into the development branch and close the pull request.

Please adhere to the following principles when submitting pull requests:

1. Only submit pull requests that are directly associated with ideally one specific issue in our GitHub repository. If there is no issue for the development work that you would like to do, create one before you start working on it.
2. Link your pull request to the issue(s) that they are associated with. This can be done either by using the "Linked issues" section at the right side when viewing a pull request, or by adding the keyword "Closes" or "Fixes" followed by the issue number to the pull request description (e.g. "Closes #61").
3. Make sure that your pull request has a meaningful title. By default, GitHub will use your commit message as the title which might not be appropriate. In general, using the same name as the linked issue is a good rule of thumb.
4. Try to not use force-push when updating an existing pull request (e.g. after changes have been requested or because you need to resolve merge conflicts).
5. Ideally, your pull request should pass the checks done by the automatic CI pipeline before it gets reviewed. If that's not the case, please make sure that your branch is up-to-date with the current development branch. If the checks also fail for the development branch, you're not required to do anything.
In any other case, please fix the issues (most likely failed unit tests) before requesting another review.
