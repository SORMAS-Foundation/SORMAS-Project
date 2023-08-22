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

package de.symeda.sormas.backend.environment.environmentsample;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleCriteria;
import de.symeda.sormas.backend.common.AbstractDeletableAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class EnvironmentSampleService extends AbstractDeletableAdoService<EnvironmentSample> {

	@EJB
	private UserService userService;

	public EnvironmentSampleService() {
		super(EnvironmentSample.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, EnvironmentSample> from) {
		return inJurisdictionOrOwned(cb, cq, from);
	}

	@Override
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, EnvironmentSample> from) {
		return CriteriaBuilderHelper.and(cb, super.createRelevantDataFilter(cb, cq, from), createDefaultFilter(cb, from));
	}

	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, EnvironmentSample> from) {
		return cb.isFalse(from.get(EnvironmentSample.DELETED));
	}

	public boolean isEditAllowed(EnvironmentSample sample) {
		return inJurisdictionOrOwned(sample);
	}

	@Override
	public boolean inJurisdictionOrOwned(EnvironmentSample sample) {
		return fulfillsCondition(sample, this::inJurisdictionOrOwned);
	}

	@Override
	public List<Long> getInJurisdictionIds(List<EnvironmentSample> samples) {
		return getIdList(samples, this::inJurisdictionOrOwned);
	}

	public Predicate buildCriteriaFilter(EnvironmentSampleCriteria criteria, EnvironmentSampleQueryContext queryContext) {
		return null;
	}

	private Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> cq, From<?, EnvironmentSample> from) {
		return EnvironmentSampleJurisdictionValidator
			.of(new EnvironmentSampleQueryContext(cb, cq, from, new EnvironmentSampleJoins(from)), userService.getCurrentUser())
			.isRootInJurisdictionOrOwned();
	}
}
