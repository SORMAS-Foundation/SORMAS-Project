/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.util;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.jurisdiction.JurisdictionValidator;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.user.User;

public abstract class PredicateJurisdictionValidator extends JurisdictionValidator<Predicate> {

	protected final CriteriaBuilder cb;
	protected final User user;
	protected final Path userPath;

	public PredicateJurisdictionValidator(CriteriaBuilder cb, User user, Path userPath, List<PredicateJurisdictionValidator> jurisdictionValidators) {
		super(jurisdictionValidators);
		this.cb = cb;
		this.user = user;
		this.userPath = userPath;
	}

	@Override
	protected Predicate or(List<Predicate> jurisdictionTypes) {
		return CriteriaBuilderHelper.or(cb, jurisdictionTypes.toArray(new Predicate[jurisdictionTypes.size()]));
	}

	@Override
	public Predicate isRootInJurisdiction() {
		return user != null
			? isInJurisdictionByJurisdictionLevel(user.getJurisdictionLevel())
			: isInJurisdictionByJurisdictionLevelPath(userPath.get(User.JURISDICTION_LEVEL));
	}

	protected Predicate isInJurisdictionByJurisdictionLevelPath(Path jLP) {
		return cb.selectCase()
			.when(cb.equal(jLP, JurisdictionLevel.NATION), cb.selectCase().when(whenNationalLevel(), true).otherwise(false))
			.when(cb.equal(jLP, JurisdictionLevel.REGION), cb.selectCase().when(whenRegionalLevel(), true).otherwise(false))
			.when(cb.equal(jLP, JurisdictionLevel.DISTRICT), cb.selectCase().when(whenDistrictLevel(), true).otherwise(false))
			.when(cb.equal(jLP, JurisdictionLevel.COMMUNITY), cb.selectCase().when(whenCommunityLevel(), true).otherwise(false))
			.when(cb.equal(jLP, JurisdictionLevel.HEALTH_FACILITY), cb.selectCase().when(whenFacilityLevel(), true).otherwise(false))
			.when(cb.equal(jLP, JurisdictionLevel.LABORATORY), cb.selectCase().when(whenLaboratoryLevel(), true).otherwise(false))
			.when(cb.equal(jLP, JurisdictionLevel.POINT_OF_ENTRY), cb.selectCase().when(whenPointOfEntryLevel(), true).otherwise(false))
			.otherwise(false)
			.in(true);
	}

	public Predicate hasUserLimitedDisease() {
		if (user != null) {
			return getLimitedDiseasePredicate();
		} else {
			return null;
		}
	}

	protected Predicate getLimitedDiseasePredicate() {
		return null;
	}

	@Override
	protected Predicate and(Predicate condition1, Predicate condition2) {
		return CriteriaBuilderHelper.and(cb, condition1, condition2);
	}
}
