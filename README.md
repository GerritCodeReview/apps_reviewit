# ReviewIt - Android application for Gerrit

[Gerrit](https://www.gerritcodereview.com) provides web based code review and
repository management for the Git version control system. The ReviewIt app is
an Android app for Gerrit that allows sorting of incoming changes and review of
small/trivial changes.

The app suppports a 2-step review process:

1. Sort the incoming changes
2. Do review on the selected changes

Some users may just want to do reviews and skip the first step.

This is not an official Google product.

## Sorting of incoming changes

When working with Gerrit it is a common workflow that reviewers are not always
explicitly assigned, but that committers and contributors watch the incoming
changes and choose those changes for review that are interesting to them.

Watching incoming changes can be done in different ways, e.g:

* manually go through the list of open changes
* register project watches to get email notifications about new changes

These approaches have some problems in common when the number of incoming
changes gets too high:

* some changes of interest are not noticed (e.g. get lost in the mail inbox)
* at the time of notification there may be no time for immediate review, and at
  the moment when there is time to review, some of the interesting changes have
  been forgotten
* changes that are not interesting popup again and again whenever they are
  updated

The 'ReviewIt' Android app tries to solve these problems by offering a simple
way to sort incoming changes:

* mark interesting changes to review them later (by putting a labeled star on
  them)
* mark non-interesting changes as ignored

The actual review of the changes is done later, either by using the Gerrit web
UI or by using the review functionality of the ReviewIt app. The idea is that
the sorting of incoming changes allows users to stay updated about their project
while they are not in office (see new changes and sort them), but the code
review is done only later when there is time for it. Also some large changes may
not be reviewable on the small display of a phone.

The changes that have been starred are accessible by a change query or a Gerrit
dashboard, and hence they can be easily found when there is time to do reviews.
The app should configure an entry for this dashboard in the Gerrit menu
automatically (e.g. ‘My’ > ‘Review It’).

Updates for ignored changes are filtered out so that the noise is reduced.
In addition to starring/ignoring a change, the app also offers to skip changes.
Skipping a change means that the user is asked about this change once more when
it was updated.

Within the app the user can define a change query for the incoming changes. The
results are then presented one by one. For each change a decision (star, ignore
or skip) has to be made to go to the next change.

Starring/ignoring a change can be done by swiping the change to the right/left
side. In addition there are buttons to trigger starring, ignoring and skipping a
change.

The main app screen provides the information that is most relevant to deciding
whether a change is interesting:

* subject + commit message
* project, branch, topic
* age (when was the change last updated)
* owner
* patch set number, number of positive and negative Code-Review votes
* number of comments
* number of reviewers
* positive/negative Verified votes (green/red background)
* whether the change is mergeable (orange background)

This information is presented in a very compressed way and the focus is on the
subject and the commit message.

All actions (star, ignore and skip) are undoable by clicking on the undo action
in the menu, so that mistakes in sorting can be easily corrected.

Sometimes when watching a change one knows another person that is well suited to
review this change. This is why there is an add reviewer action in the menu that
allows to add another person as reviewer. Reviewer names are auto-completed on
typing.

There is also a menu entry to abandon the current change if it is no longer
needed.

By clicking on the bottom of the change one can go to a detailed change view
where more information is presented, e.g. the change number, the change link,
the approvals and the list of files. This detailed change view is zoomable.

The diff for all files can be seen in one unified diff view.

The file diffs are presented in a scroll view where the header with the file
name of the currently viewed file diff is sticky at the top of the screen.

Skipped context lines can be expanded.

## Reviewing (small) changes

This is not implemented yet, but it is envisioned to show a list of changes
(either those starred by the sorting step or all incoming changes) and then
allow the user to select a change for review. This leads to the view with the
unified diff of all changed files and at the end of it there will be voting
buttons to approve or reject the change.

## Contribute

Contributions are welcome!

Please read the [contribution
guidelines](https://gerrit.googlesource.com/apps/reviewit/+/master/CONTRIBUTING).

## Build-time dependencies

The app uses the following build-time dependencies provided under the given licenses:

* Apache License, Version 2.0
  * [gradle-versions-plugin](https://github.com/ben-manes/gradle-versions-plugin)
  * [sdk-manager-plugin](https://github.com/JakeWharton/sdk-manager-plugin)

* The MIT License
  * [jitpack.io](https://github.com/jitpack/jitpack.io)

Compile-time dependencies are listed in the app's help screen.
