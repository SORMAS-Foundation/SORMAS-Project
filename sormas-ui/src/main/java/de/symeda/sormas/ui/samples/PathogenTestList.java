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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import java.util.List;
import java.util.function.BiConsumer;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class PathogenTestList extends PaginationList<PathogenTestDto> {

	private SampleReferenceDto sampleRef;
	private int caseSampleCount;
	private BiConsumer<PathogenTestResultType, Runnable> testChangedCallback;

	public PathogenTestList(SampleReferenceDto sampleRef, BiConsumer<PathogenTestResultType, Runnable> testChangedCallback) {
		super(5);

		this.sampleRef = sampleRef;
		this.testChangedCallback = testChangedCallback;
	}

	@Override
	public void reload() {
		List<PathogenTestDto> pathogenTests = ControllerProvider.getPathogenTestController()
				.getPathogenTestsBySample(sampleRef);
		

		setEntries(pathogenTests);
		if (!pathogenTests.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noPathogenTestsLabel = new Label(I18nProperties.getString(Strings.infoNoPathogenTests));
			listLayout.addComponent(noPathogenTestsLabel);
		}
	}
	
	@Override
	protected void drawDisplayedEntries() {
		for (PathogenTestDto pathogenTest : getDisplayedEntries()) {
			PathogenTestListEntry listEntry = new PathogenTestListEntry(pathogenTest);
			if (UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_EDIT)) {
				listEntry.addEditListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						ControllerProvider.getPathogenTestController().edit(pathogenTest, caseSampleCount,
								PathogenTestList.this::reload, testChangedCallback);
					}
				});
			}
			listLayout.addComponent(listEntry);
		}
	}
}
