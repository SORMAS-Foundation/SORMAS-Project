/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.directory;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationCriteria;

public class AefiInvestigationFilterFormLayout extends VerticalLayout {

	private static final float PERCENTAGE_WIDTH = 100;

	private final AefiInvestigationFilterForm filterForm;

	public AefiInvestigationFilterFormLayout() {
		setSpacing(false);
		setMargin(false);
		setWidth(PERCENTAGE_WIDTH, Unit.PERCENTAGE);

		filterForm = new AefiInvestigationFilterForm();
		addComponent(filterForm);
	}

	public AefiInvestigationCriteria getValue() {
		return filterForm.getValue();
	}

	public void setValue(AefiInvestigationCriteria criteria) {
		filterForm.setValue(criteria);
	}

	public void addResetHandler(Button.ClickListener listener) {
		filterForm.addResetHandler(listener);
	}

	public void addApplyHandler(Button.ClickListener listener) {
		filterForm.addApplyHandler(listener);
	}
}
