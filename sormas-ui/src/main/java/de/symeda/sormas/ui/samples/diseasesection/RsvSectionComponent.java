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
package de.symeda.sormas.ui.samples.diseasesection;

import com.vaadin.ui.ComboBox;

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.RsvSubtype;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

/**
 * RSV section: reveals the RSV subtype (A or B) selector on a positive {@link PathogenTestType#RSV_SUBTYPING}
 * result (#14024). The subtype is captured in {@link PathogenTestDto#getRsvSubtype()}.
 */
public class RsvSectionComponent extends AbstractDiseaseSectionComponent {

	private ComboBox<RsvSubtype> rsvSubtypeField;

	private PathogenTestType currentTestType;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {
		rsvSubtypeField = createComboBox(PathogenTestDto.RSV_SUBTYPE);
		rsvSubtypeField.setItemCaptionGenerator(RsvSubtype::toString);
		rsvSubtypeField.setVisible(false);
		updateRsvSubtypeItems();
		addRow(rsvSubtypeField);

		binder.forField(rsvSubtypeField).bind(PathogenTestDto::getRsvSubtype, PathogenTestDto::setRsvSubtype);
	}

	@Override
	protected void wireVisibility() {
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateVisibility();
			updateRsvSubtypeItems();
		}));

		track(eventBus.on(TestResultChangedEvent.class, event -> {
			currentResult = event.getTestResult();
			updateVisibility();
		}));
	}

	private void updateVisibility() {
		boolean visible = currentTestType == PathogenTestType.RSV_SUBTYPING && currentResult == PathogenTestResultType.POSITIVE;
		rsvSubtypeField.setVisible(visible);
		if (!visible) {
			rsvSubtypeField.clear();
		}
		updateRowAndSelfVisibility();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setRsvSubtype(null);
	}

	private void updateRsvSubtypeItems() {
		updateComboBoxByDisease(rsvSubtypeField, RsvSubtype.class, disease);
	}
}
