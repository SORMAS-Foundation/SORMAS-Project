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

package de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization.components;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.CssLayout;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.ui.utils.CssStyles;

public class AefiTypeStatisticsGroupComponent extends CssLayout {

	Map<Vaccine, AefiTypeStatisticsComponent> componentMap = new HashMap<>();

	public AefiTypeStatisticsGroupComponent() {

		setWidthFull();
	}

	public void update(Map<Vaccine, Map<AefiType, Long>> countsByVaccineData) {

		//temporary fix: re-use stored components and hide/unhide if not in new update
		removeAllComponents();
		componentMap.clear();

		AefiTypeStatisticsComponent statisticsComponent;

		for (Map.Entry<Vaccine, Map<AefiType, Long>> entry : countsByVaccineData.entrySet()) {
			if (componentMap.containsKey(entry.getKey())) {
				componentMap.get(entry.getKey()).update(entry.getValue());
			} else {
				statisticsComponent = new AefiTypeStatisticsComponent("", null, entry.getKey().toString(), false);
				//statisticsComponent.getHeading().getTitleLabel().setValue(entry.getKey().toString());
				statisticsComponent.hideHeading();
				statisticsComponent.update(entry.getValue());
				statisticsComponent
					.addStyleNames(CssStyles.VIEW_SECTION_WIDTH_AUTO, CssStyles.PADDING_X_8, CssStyles.HSPACE_RIGHT_3, CssStyles.VSPACE_4);
				componentMap.put(entry.getKey(), statisticsComponent);

				addComponent(statisticsComponent);
			}
		}
	}
}
