# Adding License Headers

## License Header
Use the following header for all newly created source files:

```
SORMAS® - Surveillance Outbreak Response Management & Analysis System
Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
```

## Eclipse
- Use eclipse's Releng tool to automatically add license headers to all relevant source files (see https://www.codejava.net/ides/eclipse/how-to-add-copyright-license-header-for-java-source-files-in-eclipse for a usage guide)
- After installing the tool from the marketplace, open Window > Preferences > Copyright Tool and paste the license header from above into the template text area
- Make sure to select "Replace all existing copyright comments with this copyright template" and especially "Skip over XML files" (to make sure that headers don't get added to e.g. build files)
- Whenever you create a new source file: Right click on the file and select "Fix Copyrights"

## Android Studio/IntelliJ
- Open File > Settings > Editor > Copyright > Copyright Profiles
- Create a new profile and paste the license header from above into the Copyright text area
- Head back to the general Copyright settings and select the new copyright profile as the "Default project copyright"
- (Optional: If the year has changed, right click on all projects containing manual code and select "Update Copyright...", select "Custom Scope" and in the dropdown, select "Project Source Files"; Click "Ok" and wait until the copyright has been added to/changed for all files)
- Android Studio automatically adds the copyright to newly created files afterwards
