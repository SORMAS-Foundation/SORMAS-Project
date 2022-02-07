/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.person;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.SimilarPersonDto;
import org.apache.commons.lang3.StringUtils;

public class PersonSearchField extends PersonSelectionField {

	public PersonSearchField(PersonDto referencePerson, String infoText) {
		super(referencePerson, infoText);
	}

	@Override
	protected Component initContent() {
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(new MarginInfo(false, false, true, false));
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);

		addInfoComponent();
		addFilterForm();
		filterForm.setVisible(true);
		setValue(null);
		mainLayout.addComponent(personGrid);

		return mainLayout;
	}

	@Override
	protected void doSetValue(SimilarPersonDto newValue) {
		personGrid.select(newValue);
	}

	@Override
	protected void initializeGrid() {
		personGrid = new PersonSelectionGrid();

		personGrid.addSelectionListener(e -> {
			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
	}
}
