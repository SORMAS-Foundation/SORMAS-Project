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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization;

import java.util.ArrayList;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.information.AefiImmunizationInfo;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.information.AefiInfo;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.information.AefiPersonInfo;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

public class AefiInvestigationDataView extends AbstractAefiInvestigationDataView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String ADVERSE_EVENT_LOC = "adverseEventLoc";
	public static final String PERSON_LOC = "personLoc";
	public static final String IMMUNIZATION_LOC = "immunizationLoc";

	private CommitDiscardWrapperComponent<AefiInvestigationDataForm> editComponent;

	public AefiInvestigationDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected String getRootViewName() {
		return super.getRootViewName();
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		AefiInvestigationDto aefiInvestigationDto;
		AefiDto aefiReport;
		ImmunizationDto immunization;

		boolean isCreateAction = ControllerProvider.getAefiInvestigationController().isCreateAction(params);
		if (isCreateAction) {
			aefiInvestigationDto = AefiInvestigationDto.build(getReference());

			String aefiReportUuid = ControllerProvider.getAefiInvestigationController().getCreateActionAefiReportUuid(params);
			aefiReport = FacadeProvider.getAefiFacade().getByUuid(aefiReportUuid);
			immunization = FacadeProvider.getImmunizationFacade().getByUuid(aefiReport.getImmunization().getUuid());

			aefiInvestigationDto.setAefiReport(aefiReport.toReference());
			aefiInvestigationDto.setVaccinations(new ArrayList<>(immunization.getVaccinations()));
			aefiInvestigationDto.setReportingUser(UserProvider.getCurrent().getUserReference());
		} else {
			aefiInvestigationDto = FacadeProvider.getAefiInvestigationFacade().getByUuid(getReference().getUuid());
			aefiReport = FacadeProvider.getAefiFacade().getByUuid(aefiInvestigationDto.getAefiReport().getUuid());
			immunization = FacadeProvider.getImmunizationFacade().getByUuid(aefiReport.getImmunization().getUuid());
		}

		editComponent = ControllerProvider.getAefiInvestigationController()
			.getAefiInvestigationDataEditComponent(isCreateAction, aefiInvestigationDto, this::showUnsavedChangesPopup);

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent, PERSON_LOC, ADVERSE_EVENT_LOC, IMMUNIZATION_LOC);

		container.addComponent(layout);

		UserProvider currentUser = UserProvider.getCurrent();
		if (currentUser.hasAllUserRights(UserRight.PERSON_VIEW)) {

			PersonDto personDto = FacadeProvider.getPersonFacade().getByUuid(immunization.getPerson().getUuid());
			Disease disease = immunization.getDisease();

			AefiPersonInfo aefiPersonInfo = new AefiPersonInfo(personDto, disease);
			CssStyles.style(aefiPersonInfo, CssStyles.VIEW_SECTION);

			layout.addSidePanelComponent(aefiPersonInfo, PERSON_LOC);
		}

		if (currentUser.hasUserRight(UserRight.IMMUNIZATION_VIEW)) {

			AefiImmunizationInfo aefiImmunizationInfo = new AefiImmunizationInfo(immunization, (r) -> {
			});
			CssStyles.style(aefiImmunizationInfo, CssStyles.VIEW_SECTION, CssStyles.VSPACE_TOP_3);

			layout.addSidePanelComponent(aefiImmunizationInfo, IMMUNIZATION_LOC);
		}

		if (currentUser.hasUserRight(UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_VIEW)) {

			AefiInfo aefiInfo = new AefiInfo(aefiReport, (r) -> {
			});
			CssStyles.style(aefiInfo, CssStyles.VIEW_SECTION, CssStyles.VSPACE_TOP_3);

			layout.addSidePanelComponent(aefiInfo, ADVERSE_EVENT_LOC);
		}

		if (!isCreateAction) {
			final String uuid = aefiInvestigationDto.getUuid();
			final EditPermissionType aefiInvestigationEditAllowed = FacadeProvider.getAefiInvestigationFacade().getEditPermissionType(uuid);
			final boolean deleted = FacadeProvider.getAefiInvestigationFacade().isDeleted(uuid);
			layout.disableIfNecessary(deleted, aefiInvestigationEditAllowed);
		}
	}
}
