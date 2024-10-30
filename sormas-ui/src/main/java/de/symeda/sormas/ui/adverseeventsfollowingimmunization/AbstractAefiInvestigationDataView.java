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

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.EditPermissionFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.immunization.ImmunizationDataView;
import de.symeda.sormas.ui.immunization.ImmunizationPersonView;
import de.symeda.sormas.ui.utils.AbstractEditAllowedDetailView;

public class AbstractAefiInvestigationDataView extends AbstractEditAllowedDetailView<AefiInvestigationReferenceDto> {

	public static final String ROOT_VIEW_NAME = AefiInvestigationView.VIEW_NAME;

	protected AbstractAefiInvestigationDataView(String viewName) {
		super(viewName);
	}

	@Override
	protected AefiInvestigationReferenceDto getReferenceByUuid(String uuid) {

		final AefiInvestigationReferenceDto reference;

		boolean isCreateAction = ControllerProvider.getAefiInvestigationController().isCreateAction(uuid);

		if (isCreateAction) {
			reference = new AefiInvestigationReferenceDto();
			reference.setUuid(DataHelper.createUuid());
		} else {
			if (FacadeProvider.getAefiInvestigationFacade().exists(uuid)) {
				reference = FacadeProvider.getAefiInvestigationFacade().getReferenceByUuid(uuid);
			} else {
				reference = null;
			}
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return ROOT_VIEW_NAME;
	}

	@Override
	protected void initView(String params) {

	}

	@Override
	protected EditPermissionFacade getEditPermissionFacade() {
		return FacadeProvider.getAefiInvestigationFacade();
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {

		if (!findReferenceByParams(params)) {
			return;
		}

		boolean isCreateAction = ControllerProvider.getAefiInvestigationController().isCreateAction(params);

		String aefiReportUuid = "";
		String immunizationUuid = "";
		if (isCreateAction) {
			aefiReportUuid = ControllerProvider.getAefiInvestigationController().getCreateActionAefiReportUuid(params);
			AefiDto aefiDto = FacadeProvider.getAefiFacade().getByUuid(aefiReportUuid);
			immunizationUuid = aefiDto.getImmunization().getUuid();
		} else {
			AefiInvestigationDto aefiInvestigationDto = FacadeProvider.getAefiInvestigationFacade().getByUuid(params);
			aefiReportUuid = aefiInvestigationDto.getAefiReport().getUuid();

			AefiDto aefiDto = FacadeProvider.getAefiFacade().getByUuid(aefiReportUuid);
			immunizationUuid = aefiDto.getImmunization().getUuid();
		}

		menu.removeAllViews();
		menu.addView(AefiView.VIEW_NAME, I18nProperties.getCaption(Captions.aefiAefiList));
		menu.addView(ImmunizationDataView.VIEW_NAME, I18nProperties.getCaption(ImmunizationDto.I18N_PREFIX), immunizationUuid);
		menu.addView(
			ImmunizationPersonView.VIEW_NAME,
			I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.PERSON),
			immunizationUuid);
		menu.addView(AefiDataView.VIEW_NAME, I18nProperties.getCaption(Captions.aefiAefiDataView), aefiReportUuid);
		menu.addView(AefiInvestigationDataView.VIEW_NAME, I18nProperties.getCaption(Captions.aefiAefiInvestigationDataView), params);

		setMainHeaderComponent(
			ControllerProvider.getAefiInvestigationController()
				.getAefiInvestigationViewTitleLayout(getReference().getUuid(), aefiReportUuid, immunizationUuid, isCreateAction));
	}
}
