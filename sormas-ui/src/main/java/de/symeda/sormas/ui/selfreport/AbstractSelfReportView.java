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

package de.symeda.sormas.ui.selfreport;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.EditPermissionFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractEditAllowedDetailView;

public abstract class AbstractSelfReportView extends AbstractEditAllowedDetailView<SelfReportReferenceDto> {

	private static final long serialVersionUID = 8423930944045255379L;

	public static final String ROOT_VIEW_NAME = SelfReportsView.VIEW_NAME;

	public AbstractSelfReportView(String viewName) {
		super(viewName);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected SelfReportReferenceDto getReferenceByUuid(String uuid) {
		final SelfReportReferenceDto reference;
		if (FacadeProvider.getSelfReportFacade().exists(uuid)) {
			reference = FacadeProvider.getSelfReportFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
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
		return FacadeProvider.getSelfReportFacade();
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}

		menu.removeAllViews();
		menu.addView(SelfReportsView.VIEW_NAME, I18nProperties.getCaption(Captions.selfReportSelfReportsList));
		menu.addView(SelfReportDataView.VIEW_NAME, I18nProperties.getCaption(SelfReportDto.I18N_PREFIX), params);

		setMainHeaderComponent(ControllerProvider.getSelfReportController().getSelfReporViewTitleLayout(getReference().getUuid()));
	}
}
