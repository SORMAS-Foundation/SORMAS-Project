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

package de.symeda.sormas.ui.vaccination.list;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class VaccinationListComponent extends SideComponent {

	public VaccinationListComponent(
		VaccinationListCriteria criteria,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		boolean showCreateButton) {
		super(I18nProperties.getString(Strings.entityVaccinations));

		VaccinationList vaccinationList = new VaccinationList(criteria.getPerson().getUuid(), criteria.getDisease());

		UserProvider currentUser = UserProvider.getCurrent();
		if (showCreateButton && currentUser != null && currentUser.hasUserRight(UserRight.IMMUNIZATION_CREATE)) {
			Button createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.vaccinationNewVaccination));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(
				e -> ControllerProvider.getVaccinationController()
					.create(
						region,
						district,
						criteria.getPerson(),
						criteria.getDisease(),
						UiFieldAccessCheckers.getNoop(),
						v -> vaccinationList.reload()));
			addCreateButton(createButton);
		}

		addComponent(vaccinationList);
		vaccinationList.reload();
	}

}
