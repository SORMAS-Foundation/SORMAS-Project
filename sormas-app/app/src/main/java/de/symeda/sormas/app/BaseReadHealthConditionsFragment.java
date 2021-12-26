/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app;

import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.app.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.databinding.FragmentReadHealthConditionsLayoutBinding;

public abstract class BaseReadHealthConditionsFragment<TActivityRootData extends AbstractDomainObject>
	extends BaseReadFragment<FragmentReadHealthConditionsLayoutBinding, HealthConditions, TActivityRootData> {

	protected HealthConditions record;

	@Override
	public void onLayoutBinding(FragmentReadHealthConditionsLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	public void onAfterLayoutBinding(FragmentReadHealthConditionsLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(HealthConditionsDto.class, contentBinding.mainContent);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_case_health_conditions);
	}

	@Override
	public HealthConditions getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_read_health_conditions_layout;
	}
}
