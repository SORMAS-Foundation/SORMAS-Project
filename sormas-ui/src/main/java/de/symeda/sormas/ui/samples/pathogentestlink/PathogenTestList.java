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

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class PathogenTestList extends PaginationList<PathogenTestDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private SampleReferenceDto sampleRef;
	private EnvironmentSampleReferenceDto environmentSampleRef;
	private final Consumer<Runnable> actionCallback;
	private final boolean isEditable;

	public PathogenTestList(SampleReferenceDto sampleRef, Consumer<Runnable> actionCallback, boolean isEditAllowed) {
		super(MAX_DISPLAYED_ENTRIES);

		this.sampleRef = sampleRef;
		this.actionCallback = actionCallback;
		this.isEditable = isEditAllowed;
	}

	public PathogenTestList(EnvironmentSampleReferenceDto environmentSampleRef, Consumer<Runnable> actionCallback, boolean isEditAllowed) {
		super(MAX_DISPLAYED_ENTRIES);

		this.environmentSampleRef = environmentSampleRef;
		this.actionCallback = actionCallback;
		this.isEditable = isEditAllowed;
	}

	@Override
	public void reload() {
		List<PathogenTestDto> pathogenTests = null;
		if (sampleRef != null) {
			pathogenTests = ControllerProvider.getPathogenTestController().getPathogenTestsBySample(sampleRef);
		} else if (environmentSampleRef != null) {
			pathogenTests = ControllerProvider.getPathogenTestController().getPathogenTestsByEnvironmentSample(environmentSampleRef);
		}

		setEntries(pathogenTests);
		if (!pathogenTests.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			Label noPathogenTestsLabel = new Label(I18nProperties.getString(Strings.infoNoPathogenTests));
			listLayout.addComponent(noPathogenTestsLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		List<PathogenTestDto> displayedEntries = getDisplayedEntries();
		for (PathogenTestDto pathogenTest : displayedEntries) {
			PathogenTestListEntry listEntry = new PathogenTestListEntry(pathogenTest, true);
			String pathogenTestUuid = pathogenTest.getUuid();
			boolean isEditableAndHasEditRight =
				UserProvider.getCurrent().hasAllUserRightsWithEditAllowedFlag(isEditable, UserRight.SAMPLE_EDIT, UserRight.PATHOGEN_TEST_EDIT);
			boolean isEditableAndHasDeleteRight =
				UserProvider.getCurrent().hasAllUserRightsWithEditAllowedFlag(isEditable, UserRight.PATHOGEN_TEST_DELETE);

			listEntry.addActionButton(
				pathogenTestUuid,
				e -> actionCallback.accept(
					() -> ControllerProvider.getPathogenTestController()
						.edit(pathogenTestUuid, SormasUI::refreshView, isEditableAndHasEditRight, isEditableAndHasDeleteRight)),
				isEditableAndHasEditRight);
			listEntry.setEnabled(isEditable);
			listLayout.addComponent(listEntry);
		}
	}
}
