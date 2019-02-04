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

import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class SampleTestList extends PaginationList<SampleTestDto> {

	private SampleReferenceDto sampleRef;
	private int caseSampleCount;

	public SampleTestList(SampleReferenceDto sampleRef) {
		super(5);

		this.sampleRef = sampleRef;
	}

	@Override
	public void reload() {
		List<SampleTestDto> sampleTests = ControllerProvider.getSampleTestController()
				.getSampleTestsBySample(sampleRef);
		

		setEntries(sampleTests);
		if (!sampleTests.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noSampleTestsLabel = new Label("There are no tests for this Sample.");
			listLayout.addComponent(noSampleTestsLabel);
		}
	}
	
	@Override
	protected void drawDisplayedEntries() {
		for (SampleTestDto sampleTest : getDisplayedEntries()) {
			SampleTestListEntry listEntry = new SampleTestListEntry(sampleTest);
			if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EDIT)) {
				listEntry.addEditListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						ControllerProvider.getSampleTestController().edit(sampleTest, caseSampleCount,
								SampleTestList.this::reload);
					}
				});
			}
			listLayout.addComponent(listEntry);
		}
	}
}
