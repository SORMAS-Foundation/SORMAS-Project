/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples.pathogentestlink;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;
import de.symeda.sormas.ui.utils.components.sidecomponent.event.SideComponentEditEvent;

@SuppressWarnings("serial")
public class PathogenTestListComponent extends SideComponent {

	private final PathogenTestList pathogenTestList;

	public PathogenTestListComponent(SampleReferenceDto sampleRef) {
		super(I18nProperties.getString(Strings.headingTests));

		addCreateButton(I18nProperties.getCaption(Captions.pathogenTestNewTest), UserRight.PATHOGEN_TEST_CREATE);

		pathogenTestList = new PathogenTestList(sampleRef);
		pathogenTestList.addSideComponentFieldEditEventListener(e -> {
			PathogenTestListEntry listEntry = (PathogenTestListEntry) e.getComponent();
			fireEvent(new SideComponentEditEvent(this, listEntry.getPathogenTest().getUuid()));
		});
		addComponent(pathogenTestList);
		pathogenTestList.reload();
	}

	public void reload() {
		this.pathogenTestList.reload();
	}
}
