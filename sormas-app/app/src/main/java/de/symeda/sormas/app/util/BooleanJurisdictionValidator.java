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

package de.symeda.sormas.app.util;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.jurisdiction.JurisdictionValidator;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.UserDtoHelper;

public abstract class BooleanJurisdictionValidator extends JurisdictionValidator<Boolean> {

	private final UserJurisdiction userJurisdiction;

	public BooleanJurisdictionValidator(List<BooleanJurisdictionValidator> associatedJurisdictionValidators, UserJurisdiction userJurisdiction) {
		super(associatedJurisdictionValidators, UserDtoHelper.isRestrictedToAssignEntities(ConfigProvider.getUser()));
		this.userJurisdiction = userJurisdiction;
	}

    @Override
    protected Boolean or(List<Boolean> jurisdictionTypes) {
        for (Boolean b : jurisdictionTypes) {
            if (b) {
                return b;
            }
        }
        return false;
    }

	@Override
	protected Boolean and(Boolean condition1, Boolean condition2) {
		return condition1 && condition2;
	}

	@Override
	public Boolean hasUserLimitedDisease() {
		if (getDisease() == null) {
			return true;
		}
		if (userJurisdiction != null && CollectionUtils.isNotEmpty(userJurisdiction.getLimitedDiseases())) {
			return  userJurisdiction.getLimitedDiseases().contains(getDisease());
		} else {
			return true;
		}
	}

	protected abstract Disease getDisease();
}
