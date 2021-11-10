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
package de.symeda.sormas.ui.samples;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class PathogenTestList extends PaginationList<PathogenTestDto> {

	private final SampleReferenceDto sampleRef;
	private final BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest;
	private final Supplier<Boolean> createOrEditAllowedCallback;

	public PathogenTestList(
		SampleReferenceDto sampleRef,
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest,
		Supplier<Boolean> createOrEditAllowedCallback) {
		super(5);

		this.sampleRef = sampleRef;
		this.onSavedPathogenTest = onSavedPathogenTest;
		this.createOrEditAllowedCallback = createOrEditAllowedCallback;
	}

	@Override
	public void reload() {
		List<PathogenTestDto> pathogenTests = ControllerProvider.getPathogenTestController().getPathogenTestsBySample(sampleRef);

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
			PathogenTestListEntry listEntry = new PathogenTestListEntry(pathogenTest);
			addEditButton(pathogenTest, listEntry);
			listLayout.addComponent(listEntry);
		}
	}

	private void addEditButton(PathogenTestDto pathogenTest, PathogenTestListEntry listEntry) {
		if (UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_EDIT)) {
			listEntry.addEditListener((ClickListener) event -> {
				if (createOrEditAllowedCallback.get()) {
					ControllerProvider.getPathogenTestController().edit(pathogenTest, 0, PathogenTestList.this::reload, onSavedPathogenTest);
				} else {
					Notification.show(null, I18nProperties.getString(Strings.messageFormHasErrorsPathogenTest), Type.ERROR_MESSAGE);
				}
			});
		}
	}

}
