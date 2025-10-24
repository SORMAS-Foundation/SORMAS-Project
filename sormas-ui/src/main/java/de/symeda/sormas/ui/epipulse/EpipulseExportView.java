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

package de.symeda.sormas.ui.epipulse;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.epipulse.EpipulseExportCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.statistics.AbstractStatisticsView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class EpipulseExportView extends AbstractStatisticsView {

	private static final long serialVersionUID = -5549414867103771784L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/epipulse-export";

	private final EpipulseExportGridComponent epipulseExportGridComponent;
	private ViewConfiguration viewConfiguration;

	public EpipulseExportView() {

		super(VIEW_NAME);

		if (!ViewModelProviders.of(EpipulseExportView.class).has(EpipulseExportCriteria.class)) {
			// init default filter
			EpipulseExportCriteria criteria = new EpipulseExportCriteria();
			ViewModelProviders.of(EpipulseExportView.class).get(EpipulseExportCriteria.class, criteria);
		}

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		epipulseExportGridComponent = new EpipulseExportGridComponent(getViewTitleLabel(), this);
		addComponent(epipulseExportGridComponent);

		if (UiUtil.permitted(UserRight.EPIPULSE_EXPORT_CREATE)) {
			Button createButton = ButtonHelper.createIconButton(
				Captions.epipulseNewExport,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getEpipulseExportController().create(epipulseExportGridComponent.getGrid()::reload),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		epipulseExportGridComponent.reload(event);
	}

	public ViewConfiguration getViewConfiguration() {
		return viewConfiguration;
	}
}
