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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.aefiinvestigationlink;

import java.util.function.Consumer;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationListCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

@SuppressWarnings("serial")
public class AefiInvestigationListComponent extends SideComponent {

	public AefiInvestigationListComponent(
		AefiInvestigationListCriteria listCriteria,
		Consumer<Runnable> actionCallback,
		boolean isEditAllowed,
		boolean isCreateAction) {
		super(I18nProperties.getString(Strings.headingAefiReportInvestigations), actionCallback);

		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);

		if (isEditAllowed) {
			addCreateButton(
				I18nProperties.getCaption(Captions.aefiNewAefiInvestigation),
				() -> ControllerProvider.getAefiInvestigationController()
					.navigateToAefiInvestigation("adverseevent/" + listCriteria.getAefiReport().getUuid() + "/investigation/create"),
				UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_CREATE);

			if (isCreateAction) {
				createButton.setEnabled(false);
			}
		}

		AefiInvestigationList aefiInvestigationList = new AefiInvestigationList(listCriteria, actionCallback, isEditAllowed);
		addComponent(aefiInvestigationList);
		aefiInvestigationList.reload();
	}
}
