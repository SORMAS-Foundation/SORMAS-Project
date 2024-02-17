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

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.api.EditPermissionFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.immunization.ImmunizationDataView;
import de.symeda.sormas.ui.immunization.ImmunizationPersonView;
import de.symeda.sormas.ui.utils.AbstractEditAllowedDetailView;

public abstract class AbstractAefiView extends AbstractEditAllowedDetailView<AefiReferenceDto> {

	public static final String ROOT_VIEW_NAME = AefiView.VIEW_NAME;

	protected AbstractAefiView(String viewName) {
		super(viewName);
	}

	@Override
	protected AefiReferenceDto getReferenceByUuid(String uuid) {

		final AefiReferenceDto reference;

		boolean isCreateAction = ControllerProvider.getAefiController().isCreateAction(uuid);

		if (isCreateAction) {
			reference = new AefiReferenceDto();
			reference.setUuid(DataHelper.createUuid());
		} else {
			if (FacadeProvider.getAefiFacade().exists(uuid)) {
				reference = FacadeProvider.getAefiFacade().getReferenceByUuid(uuid);
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
		return FacadeProvider.getAefiFacade();
	}

	@Override
	public void enter(ViewChangeEvent event) {

		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {

		if (!findReferenceByParams(params)) {
			return;
		}

		boolean isCreateAction = ControllerProvider.getAefiController().isCreateAction(params);

		String immunizationUuid = "";
		if (isCreateAction) {
			immunizationUuid = ControllerProvider.getAefiController().getCreateActionImmunizationUuid(params);
		} else {
			AefiDto aefiDto = FacadeProvider.getAefiFacade().getByUuid(params);
			immunizationUuid = aefiDto.getImmunization().getUuid();
		}

		menu.removeAllViews();
		menu.addView(AefiView.VIEW_NAME, I18nProperties.getCaption(Captions.aefiAefiList));
		menu.addView(ImmunizationDataView.VIEW_NAME, I18nProperties.getCaption(ImmunizationDto.I18N_PREFIX), immunizationUuid);
		menu.addView(
			ImmunizationPersonView.VIEW_NAME,
			I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.PERSON),
			immunizationUuid);
		menu.addView(AefiDataView.VIEW_NAME, I18nProperties.getCaption(Captions.aefiAefiDataView), params);

		setMainHeaderComponent(ControllerProvider.getAefiController().getAefiViewTitleLayout(params, immunizationUuid, isCreateAction));
	}
}
