/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.environment;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.climate.EnvironmentClimateLayout;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

/**
 * The environment's Climate data tab: the weather at the site compared to normal, with its samples, events, site
 * readings and the cases near it on the same time axis. Nothing on it is edited in place, so it is never dirty.
 */
public class EnvironmentClimateView extends AbstractEnvironmentView {

	private static final long serialVersionUID = 4937512238006917251L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/climate";

	public EnvironmentClimateView() {
		super(VIEW_NAME);
	}

	/**
	 * @return true when the tab is shown: climate data switched on and the climate view right (the environment views
	 *         already need environment management)
	 */
	public static boolean isPermitted() {
		return UiUtil.permitted(FeatureType.CLIMATE_MANAGEMENT, UserRight.CLIMATE_VIEW);
	}

	@Override
	protected void initView(String params) {

		setHeightUndefined();
		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> null);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);

		if (!isPermitted()) {
			container.addComponent(new Label(I18nProperties.getString(Strings.errorForbidden)));
			return;
		}
		String uuid = getReference().getUuid();
		boolean editAllowed = FacadeProvider.getEnvironmentFacade().isEditAllowed(uuid) && !FacadeProvider.getEnvironmentFacade().isDeleted(uuid);
		container.addComponent(new EnvironmentClimateLayout(getReference(), editAllowed));
	}
}
