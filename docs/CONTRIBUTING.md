# Contributing Guidelines

If you want to contribute to SORMAS by any means - for example by submitting a bug report, requesting a new feature, translating the application into a new language, or actively contributing to the source code -, please make sure to read through and follow these guidelines.
This allows us to consider and process your contribution as quickly and smoothly as possible. If there is anything unclear to you or you think that this guide is lacking coverage of a specific topic, please create a support issue or get in touch with us via [Gitter](https://gitter.im/SORMAS-Project).

## Table of Contents

* [Submitting an Issue](#submitting-an-issue)
  * [Bug Report](#bug-report)
  * [Finding](#finding)
  * [Change Request](#change-request)
  * [Feature Request](#feature-request)
  * [Support Request](#support-request)
  * [Epic](#epic)
* [Contributing to the Project](#contributing-to-the-project)
* [Contributing to the Code](#contributing-to-the-code)
  * [Development Contributing Guidelines](#development-contributing-guidelines)
  * [Picking Issues for Development](#picking-issues-for-development)
  * [Submitting Pull Requests](#submitting-pull-requests)
* [Development Workflow](#development-workflow)
  * [Versioning](#versioning)
  * [Branches](#branches)

## Submitting an Issue

**Before creating a new issue, please search the repository for similar issues first to avoid duplicates!** You can do this by using the search field in the menu bar. If you find an issue that already covers your request or seems very similar, please comment on that issue.

We are currently distinguishing the following issue types: `bug`, `change`, `feature`, `task`, `epic` and `support`.
Please make sure to always use one of the templates that are automatically presented to you when [creating a new issue](https://github.com/hzi-braunschweig/SORMAS-Project/issues/new/choose) because it ensures that your issue is structured and ideally contains all the information that we need.
Please add information to every mandatory section of the issue templates, and try to fill in the optional sections if possible. Do not remove any section because they might be filled later by the development team.
Please note that we might have to close issues that are not created this way.
While moving forward in the development process, developers might extend or alter the description to better fit what should be changed by that issue.

If you want to report a **security issue**, please follow our guidelines for [*Responsible Disclosure*](SECURITY.md).

### Bug Report

Bug reports cover everything that leads to the application behaving in an unintended way, including crashes, unexpected error messages, data loss, etc.

* **Bug Description:** A description of what exactly happened, where it happened and under which circumstances. Provide as many details as possible, even if they might seem irrelevant to you. The following subheadings help to structure the needed information.
* **Steps to Reproduce:** If possible, provide a step-by-step instruction on the order of actions you performed before the problem occurred. This helps us to reproduce it on our system. Generally *optional*, but likely necessary for more complex bugs.
* **Screenshots:** If possible, make at least one screenshot of the actual behavior and include it in your report (without exposing protected data). You can do this by simply dragging-and-dropping the image file into the template you're filling out. *Optional*, but very helpful for us.
* **System Details:** Tell us which device you were using, on which SORMAS version the problem occurred and, depending on whether you were using the mobile app or the web app, your Android version or web browser.
* **Expected Behavior:** Quickly describe what you believe should have happened instead. *Optional* when the error is identified by a crash or an error message, but likely necessary if the problem is the result of a misbehavior of the application.
* **Additional Information:** If there is anything else you want to add to your request, you can put it here. *Optional*.

Before creating a bug report, please check the following rules:

1. If something seems to be working correctly but does not necessarily match your expectations of how it should be working, please consider opening a change request instead. This applies also for slow performing features.
2. If you have more than one possible bug - especially when you are tempted to create a list of independent findings in the **Bug Description** - then please document each bug as separate issue.
3. Bugs are only valid to create if you can point to a released version where the problem is occuring, not on the version currently under development.

The development team defines a **severity** for bugs to distinguish the consequences for affected users:

* **critical:** The system unusable as a whole or on critical functionality, no workaround is available.
* **major:** A functional requirement is incorrect or incomplete, there might be a workaround.
* **minor:** Text issues or grammatical mistakes, layouting or cosmetic problems. It does not affect the functionality, no workaround needed.

#### Why are bugs only valid on released versions?

1. If there is a problem newly introduced on the `development` branch, it means that an issue recently worked on is not yet finished (so to be reopened and continued to work on). It's relevant for reviewing (now, soon or far later) to keep changes logically together as good as possible.
2. For the Release Notes it is only relevant to mention changes from one version to the next, so listing fixed bugs that never existed from the end users point of view is pointless, confusing and noisy.

### Finding

Findings are used to document unexpected behaviour, mostly encountered within the development.

* **Problem Description:** A description of what exactly happened, where it happened and under which circumstances. Provide as many details as possible, even if they might seem irrelevant to you. The following subheadings help to structure the needed information.
* **Steps to Reproduce:** If possible, provide a step-by-step instruction on the order of actions you performed before the problem occurred. This helps us to reproduce it on our system. Generally *optional*, but likely necessary for more complex problem.
* **Screenshots:** If possible, make at least one screenshot of the actual behavior and include it in your report (without exposing protected data). You can do this by simply dragging-and-dropping the image file into the template you're filling out. *Optional*, but very helpful for us.
* **System Details:** Tell us which device you were using, on which SORMAS version the problem occurred and, depending on whether you were using the mobile app or the web app, your Android version or web browser.
* **Expected Behavior:** Quickly describe what you believe should have happened instead. *Optional* when the error is identified by a crash or an error message, but likely necessary if the problem is the result of a misbehavior of the application.
* **Additional Information:** If there is anything else you want to add to your request, you can put it here. *Optional*.

The development team will investigate the finding and add more details when needed. The goal is to either
1. convert it to a bug if it is considered to be a bug on a released version.
2. convert it to a change or other type if there is something to improve.
3. dismiss it as `duplicate` if the cause on the current development is found and reopened or fixed.
4. dismiss it as `discarded` if the finding is not an issue.

A **severity** as for bugs can also be used for findings.

### Change Request

Change requests cover features that are already part of SORMAS. This primarily includes aspects (or whole features) for which you would like to request an altered behavior, but also small extensions (e.g. additional values being added to a dropdown field).

* **Problem Description:** Tell us why you want us to change or extend an existing feature. Is there something working differently than you expect it? What can be improved about the way it is currently designed?
* **Proposed Change:** Describe in as much detail as possible how you would like us to change the feature and what the expected outcome of your request should look like. If possible, provide alternative solutions in case your proposed change can not be implemented in the way you outlined it for any reason.
* **Acceptance Criteria:** Describes what conditions should apply to the requested change. These can be functional or non-functional requirements, that are usually specified by the developers.
* **Implementation Details:** While refining an issue, a developer usually gives hints what to consider or change in the source code.
* **Additional Information:** If there is anything else you want to add to your request, you can put it here. *Optional*.

### Feature Request

These types of issue cover everything that involves adding new features to SORMAS. This includes both very large additions like completely new app sections, but also smaller ones like adding a new field to an existing form.

* **Feature Description:** Please describe why your proposed feature is required, why SORMAS in its current state is not able to do what you want it to do. How would you benefit from this feature being implemented?
* **Proposed Change:** Describe in as much detail as possible how your proposed feature should look like, what it should do and how it should be linked to existing features or processes. If possible, provide alternative solutions in case your proposed feature can not be implemented in the way you outlined it for any reason.
* **Acceptance Criteria:** Describes what conditions should apply to the requested change. These can be functional or non-functional requirements, that are usually specified by the developers.
* **Implementation Details:** While refining an issue, a developer usually gives hints what to consider or change in the source code.
* **Additional Information:** If there is anything else you want to add to your request, you can put it here. *Optional*.

### Task

A task that needs to be done but does not directly change anything about the product. This could for example be the preparation of an upcoming feature/change, optimization of processes on GitHub, working on automated tests, or update of one of the guides or Wiki articles in this repository.

* **Problem Description:** Please describe the requirement or problem that is supposed to be solved.
* **Proposed Solution:** Describe in as much detail how the proposed solution should look like and what should be done in order to achieve it.
* **Possible Alternatives:** If possible, provide alternative solutions in case your proposed change can not be realized for any reason. This is generally *optional* but might be requested at a later point in time.
* **Additional Information:** If there is anything else you want to add to the task, you can put it here. *Optional*.

### Support Request

If you have problems setting up a SORMAS server or your development environment, don't know how a specific functionality in SORMAS is supposed to work, or have any other request that is not directly associated with a bug report, change or feature request, a support request is likely the correct type of issue for your concern.
Alternatively, you can join our [Gitter channel](https://gitter.im/SORMAS-Project) and ask your question over there which might potentially give you a quicker and more comfortable way to talk about your request. If your request is about a specific problem, please make sure to provide as much information as possible.

### Epic

The development team uses an epic as umbrella for large change or feature streams that are linked together. Within the epic the included issues are linked in the **Tasks** section.

## Contributing to the Project

There are a lot of ways in which you can contribute to this project as a non-developer. If there is something you would like to do that you don't find instructions about here - or if you want to learn how you can get involved - please contact us at sormas@helmholtz-hzi.de and let us know how we can assist you!

Some possibilities to contribute to SORMAS are:

* [Helping with translation](I18N.md)
* [Defining new diseases](SOP_DISEASES.md)

## Contributing to the Code

If you're interested in participating in the development of SORMAS, please follow the [Development Environment Setup Instructions](DEVELOPMENT_ENVIRONMENT.md) before you start developing.
If you have problems setting up your development environment or need assistance in choosing the first issue to work on, please get in touch with us by joining our [Gitter channel](https://gitter.im/SORMAS-Project) or contacting us at sormas@helmholtz-hzi.de.
Additionally, our [Wiki](https://github.com/hzi-braunschweig/SORMAS-Project/wiki) contains some specific development guides that cover common issues like adding new fields to an entity that we suggest to check out before you start implementing something related to those topics.

* [Technical User Guides](https://github.com/hzi-braunschweig/SORMAS-Project/wiki#technical-user-guides)
* [Development Guides](https://github.com/hzi-braunschweig/SORMAS-Project/wiki#development-guides)

### Development Contributing Guidelines

In addition to the guidelines covered in the Development Environment Setup Instructions, please ensure to adhere to the following principles and procedures while developing code for SORMAS.

#### Rules for source code

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

#### Rules for commits

1. Commit messages of **every commit** have to be related to a specific issue on GitHub and reference its issue number as well as include a short description on what has been done in the commit. We will reject pull requests that violate this principle and ask you to re-submit it with proper commit messages. An acceptable commit message could look like this:
   > #1234 Added model to define classification
2. A common practice for Git commits is to keep the first line with 50 characters as meaninful short description, and to deliver additional details following in line 3 onwards. Most git viewers abbreviate the first line after 50 characters.
   > #1234 Added model to define classification
   >
   > - Apply automatic case classification whenever a field value changes
   > - Show classification in lists
3. Keep changes in bugfixes clean and compact to be able to easily review them and leave the possibility to cherry-pick the changes to another branch to fix a previous version. Don't clean dirty code along the way if not really needed to fix the problem.
4. If an issue requires a lot of code changes, consider breaking down these changes in logical steps. They are then easier to review, have more meaningful commit messages and deliver partly the intended value.
5. Don't mix refactoring with functional changes (new functionality, changes on features, bugfixes) within the same commit, since it makes reviewing the changes much harder. Usually the refactoring of existing code has to happen beforehand in at least one separate commits. This means refactoring (no functional change): Cleaner code, renaming, restructuring.
6. If it helps, it is okay to have several branches and pull requests for the same ticket (usually one after another, sometimes to work in parallel or to prepare changes in advance).
7. If you feel an issue consists out of several parts, feel free to document this in the **Implementation Details** as task list and check what you consider as finished. Do not break down an issue into sub-issues, unless this has been discussed with the development team.
8. If there is a finding concerning an issue which has been already closed, it will be reopened if that version is not yet released. You (or someone else) will have to quickly fix the finding before the version is released. If the version has been released already, the issue will stay closed and a new issue has to be filed.

### Picking Issues for Development

When picking tasks for development, you can either search the repository for existing issues that you would like to work on, or you can submit your own issues if you don't find any that cover your topic of interest (see the "Submitting an Issue" section of this guide). However, please note that issues need to fit our overall vision of the project; if you create an issue you want to work on yourself, it is recommended to first let a team member verify it to avoid submitting a pull request that requires a lot of functional changes or is rejected altogether.

### Submitting Pull Requests

Contributing to the SORMAS code requires you to submit pull requests that will be reviewed by one of our core developers. Once a pull request has been submitted to our repository, a developer will either assign themselves as its reviewer or we will get back to you in case we won't be able to review it in time.
This may e.g. happen if your pull request involves a lot of technical changes that we would like to merge together with other issues of the same nature or that could potentially break a lot of logic. Usually though, the general process looks like this:

1. A developer assigns themselves as the reviewer of your pull request, core developers assign each other. Please wait until the review is done; if you think that the review is taking too long, feel free to add a comment to the pull request as a reminder to the developer.
2. The developer might request changes to the code. If that's the case, please implement the requested changes or answer to their change request if you have questions or don't agree with a specific part of the request.
3. Once you've implemented all requested changes, please request another review by the assigned developer by clicking on the "Re-request review" icon next to their name.
4. As soon as the developer is happy with the submitted code (which might require multiple iterations of step 2 and 3, especially for larger pull requests), they will merge it into the development branch and close the pull request.

Please adhere to the following principles when submitting pull requests:

1. Only submit pull requests that are directly associated with ideally one specific issue in our GitHub repository. If there is no issue for the development work that you would like to do, create one before you start working on it.
2. Link your pull request to the issue(s) that they are associated with. This can be done either by using the "Linked issues" section at the right side when viewing a pull request, or by adding the keyword "Closes" or "Fixes" followed by the issue number to the pull request description (e.g. "Fixes #1234").
3. Make sure that your pull request has a meaningful title. By default, GitHub will use your commit message as the title which might not be appropriate. In general, using the same name as the linked issue is a good rule of thumb.
4. Try to not use force-push when updating an existing pull request (e.g. after changes have been requested or because you need to resolve merge conflicts).
5. Ideally, your pull request should pass the checks done by the automatic CI pipeline before it gets reviewed. If that's not the case, please make sure that your branch is up-to-date with the current development branch. If the checks also fail for the development branch, you're not required to do anything.
In any other case, please fix the issues (most likely failed unit tests) before requesting another review.

## Development Workflow

For SORMAS we use the **Gitflow** development workflow.

<img alt="General Gitflow Development Workflow" src="images/Gitflow.png"/>

### Versioning

For version numbers we use semantic versioning. Meaning of a given version number `X.Y.Z`:

* X: Major version: Major changes, severe changes in API or technical architecture.
* Y: Minor version: Usually a new release of a development iteration of a few weeks, containing new features and changes.
* Z: Micro version: Fixing problems in the last minor version to make it properly or better to use. Usually contains only bugfixes or small changes.

Versions are defined as [Git tags](https://github.com/hzi-braunschweig/SORMAS-Project/tags) with [release notes](https://github.com/hzi-braunschweig/SORMAS-Project/releases) attached to the tag.

An unstable version currently under development is denoted as `X.Y.Z-SNAPSHOT`.

### Branches

#### Permanent branches

* **development**: This is where the changes are commited/merged to by the developers.
* **master**: In regular intervals, the changes from `development` are merged to master with a identifiable version (tag). On top of this branch there is always the latest released version.
* **master-<version>**: In case an older version than the head version on `master` needs to be fixed (a new micro release), a dedicated `master-<version>` branch is split from `master` to manage the following micro releases (Example: `master-<version>` = `master-1.75.x`). Changes made on this branch are usually cherry-picked from a newer version (on `master` or `development`).
* **l10n_development**: Incoming changes on translation files from Crowdin, that are regularly merged into `development`.

#### Supporting branches

* **release-<version>**: To manage changes when merging from `development` to `master`. Once the new version is merged to `master`, the `release-<version>` branch is automatically removed.
* **hotfix-<version>**: To manage changes that are needed on an already released version (on any `master` branch) that need to be fixed with a new micro release.

Some branches contain the concerned version in its name, examples: `release-1.75.0`, `hotfix-1.75.1`. To manage new versions, tools are used to automatically merge between branches and tag the new version.
Once the new version is merged to `master`/`master-<version`, the `release-`/`hotfix-` branch are automatically deleted. There is only one `release-` and only `hotfix-` branch allowed at same time (enforced by the used Maven plugin).

#### Implementation branches

These kind of branches are manually created and maintained by developers who work on an issue. Such branches are used to create pull requests on to review the changes before merged into a permanent or supporting branch.
* **feature-1234_short_description**: Any branch that is supposed to contribute to `development` or a `hotfix` branch.
