/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.ui.adverseeventsfollowingimmunization;

import java.util.ArrayList;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.form.AefiDataForm;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.information.AefiImmunizationInfo;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.information.AefiPersonInfo;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

public class AefiDataView extends AbstractAefiView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String ADVERSE_EVENT_LOC = "adverseEventLoc";
	public static final String PERSON_LOC = "personLoc";
	public static final String IMMUNIZATION_LOC = "immunizationLoc";
	public static final String INVESTIGATIONS_LOC = "investigationsLoc";

	private CommitDiscardWrapperComponent<AefiDataForm> editComponent;

	public AefiDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected String getRootViewName() {
		return super.getRootViewName();
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		AefiDto aefi;
		ImmunizationDto immunization;

		boolean isCreateAction = ControllerProvider.getAefiController().isCreateAction(params);
		if (isCreateAction) {
			aefi = AefiDto.build(getReference());

			String immunizationUuid = ControllerProvider.getAefiController().getCreateActionImmunizationUuid(params);
			immunization = FacadeProvider.getImmunizationFacade().getByUuid(immunizationUuid);

			aefi.setImmunization(immunization.toReference());
			aefi.setVaccinations(new ArrayList<>(immunization.getVaccinations()));
			aefi.setReportingUser(UserProvider.getCurrent().getUserReference());
		} else {
			aefi = FacadeProvider.getAefiFacade().getByUuid(getReference().getUuid());
			immunization = FacadeProvider.getImmunizationFacade().getByUuid(aefi.getImmunization().getUuid());
		}

		editComponent = ControllerProvider.getAefiController().getAefiDataEditComponent(isCreateAction, aefi, this::showUnsavedChangesPopup);

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent, PERSON_LOC, IMMUNIZATION_LOC, INVESTIGATIONS_LOC);

		container.addComponent(layout);

		UserProvider currentUser = UserProvider.getCurrent();
		if (currentUser.hasAllUserRights(UserRight.PERSON_VIEW)) {
			PersonDto personDto = FacadeProvider.getPersonFacade().getByUuid(immunization.getPerson().getUuid());
			Disease disease = immunization.getDisease();

			AefiPersonInfo aefiPersonInfo = new AefiPersonInfo(personDto, disease);
			CssStyles.style(aefiPersonInfo, CssStyles.VIEW_SECTION);

			layout.addSidePanelComponent(aefiPersonInfo, PERSON_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.IMMUNIZATION_MANAGEMENT)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.IMMUNIZATION_VIEW)) {
			if (!FacadeProvider.getFeatureConfigurationFacade()
				.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {

				AefiImmunizationInfo aefiImmunizationInfo = new AefiImmunizationInfo(immunization, (r) -> {
				});
				CssStyles.style(aefiImmunizationInfo, CssStyles.VIEW_SECTION, CssStyles.VSPACE_TOP_3);

				layout.addSidePanelComponent(aefiImmunizationInfo, IMMUNIZATION_LOC);
			}
		}

		if (!isCreateAction) {
			final String uuid = aefi.getUuid();
			final EditPermissionType aefiEditAllowed = FacadeProvider.getAefiFacade().getEditPermissionType(uuid);
			final boolean deleted = FacadeProvider.getAefiFacade().isDeleted(uuid);
			layout.disableIfNecessary(deleted, aefiEditAllowed);
		}
	}
}
