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

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SelfReportsView extends AbstractView {

	private static final long serialVersionUID = -6229689625299341177L;

	public static final String VIEW_NAME = "selfreports";

	private final ViewConfiguration viewConfiguration;

	private final SelfReportCriteria gridCriteria;

	private final SelfReportGridComponent gridComponent;

	public SelfReportsView() {
		super(VIEW_NAME);

		setSizeFull();

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		gridCriteria = ViewModelProviders.of(getClass())
			.getOrDefault(SelfReportCriteria.class, () -> new SelfReportCriteria().relevanceStatus(EntityRelevanceStatus.ACTIVE));

		gridComponent = new SelfReportGridComponent(gridCriteria, viewConfiguration, () -> navigateTo(gridCriteria, true), () -> {
			ViewModelProviders.of(getClass()).remove(SelfReportCriteria.class);
			navigateTo(null, true);
		});
		addComponent(gridComponent);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			gridCriteria.fromUrlParams(params);
		}

		setApplyingCriteria(true);
		gridComponent.updateFilterComponents(gridCriteria);
		setApplyingCriteria(false);

		gridComponent.reload();
	}
}
