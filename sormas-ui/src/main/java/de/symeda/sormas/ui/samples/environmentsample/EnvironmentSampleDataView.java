/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.samples.environmentsample;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.environment.EnvironmentDataView;
import de.symeda.sormas.ui.samples.SamplesView;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

public class EnvironmentSampleDataView extends AbstractDetailView<EnvironmentSampleReferenceDto> {

	public static final String ROOT_VIEW_NAME = SamplesView.VIEW_NAME;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/environment";
	private CommitDiscardWrapperComponent<EnvironmentSampleEditForm> editComponent;

	public EnvironmentSampleDataView() {
		super(VIEW_NAME);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected EnvironmentSampleReferenceDto getReferenceByUuid(String uuid) {
		final EnvironmentSampleReferenceDto reference;
		if (FacadeProvider.getEnvironmentSampleFacade().exists(uuid)) {
			reference = FacadeProvider.getEnvironmentSampleFacade().getReferenceByUuid(uuid);
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
		setHeightUndefined();

		String sampleUuid = getReference().getUuid();
		EnvironmentSampleDto sample = FacadeProvider.getEnvironmentSampleFacade().getByUuid(sampleUuid);
		EditPermissionType editPermission = FacadeProvider.getEnvironmentSampleFacade().getEditPermissionType(sampleUuid);
		editComponent = ControllerProvider.getEnvironmentSampleController().getEditComponent(sample);

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent);
		container.addComponent(layout);

		setSubComponent(container);

		layout.disableIfNecessary(sample.isDeleted(), editPermission);
		editComponent.setEnabled(isEditAllowed());
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}
		EnvironmentSampleDto sample = FacadeProvider.getEnvironmentSampleFacade().getByUuid(params);

		menu.removeAllViews();
		menu.addView(SamplesView.VIEW_NAME, I18nProperties.getCaption(Captions.sampleSamplesList));
		menu.addView(EnvironmentDataView.VIEW_NAME, I18nProperties.getString(Strings.entityEnvironment), sample.getEnvironment().getUuid(), true);

		menu.addView(VIEW_NAME, I18nProperties.getCaption(EnvironmentSampleDto.I18N_PREFIX), params);

		setMainHeaderComponent(ControllerProvider.getEnvironmentSampleController().getEditViewTitleLayout(getReference().getUuid()));
	}

	private boolean isEditAllowed() {
		return FacadeProvider.getEnvironmentSampleFacade().isEditAllowed(getReference().getUuid());
	}

}
