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

package de.symeda.sormas.backend.infrastructure.country;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.infrastructure.country.CountryCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;

@Stateless
@LocalBean
public class CountryService extends AbstractInfrastructureAdoService<Country, CountryCriteria> {

	public CountryService() {
		super(Country.class);
	}

	public List<Country> getByDefaultName(String name, boolean includeArchivedEntities) {
		if (name == null) {
			return Collections.emptyList();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Country> cq = cb.createQuery(getElementClass());
		Root<Country> from = cq.from(getElementClass());

		Predicate filter = CriteriaBuilderHelper.unaccentedIlikePrecise(cb, from.get(Country.DEFAULT_NAME), name.trim());
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	/**
	 * All countries, archived or not, whose NUTS code equals {@code nutsCode}, ignoring case and surrounding spaces.
	 * {@code null} or an empty code → an empty list.
	 *
	 * <p>
	 * Example: {@code " lu"} → Luxembourg with NUTS code {@code "LU"}; {@code "XX"} → empty list.
	 */
	public List<Country> getByNutsCode(String nutsCode) {

		String trimmed = nutsCode != null ? nutsCode.trim() : null;
		if (trimmed == null || trimmed.isEmpty()) {
			return Collections.emptyList();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Country> cq = cb.createQuery(getElementClass());
		Root<Country> from = cq.from(getElementClass());

		cq.where(CriteriaBuilderHelper.ilikePrecise(cb, from.get(Country.NUTS_CODE), trimmed));

		return em.createQuery(cq).getResultList();
	}

	public Optional<Country> getByIsoCode(String isoCode, boolean includeArchivedEntities) {
		if (isoCode == null) {
			return Optional.empty();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Country> cq = cb.createQuery(getElementClass());
		Root<Country> from = cq.from(getElementClass());

		Predicate filter = CriteriaBuilderHelper.ilikePrecise(cb, from.get(Country.ISO_CODE), isoCode.trim());
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList().stream().findFirst();
	}

	public Optional<Country> getByUnoCode(String unoCode, boolean includeArchivedEntities) {
		if (unoCode == null) {
			return Optional.empty();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Country> cq = cb.createQuery(getElementClass());
		Root<Country> from = cq.from(getElementClass());

		Predicate filter = CriteriaBuilderHelper.ilikePrecise(cb, from.get(Country.UNO_CODE), unoCode.trim());
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList().stream().findFirst();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Country> from) {
		// no filter by user needed
		return null;
	}

	@Override
	public Predicate buildCriteriaFilter(CountryCriteria criteria, CriteriaBuilder cb, Root<Country> from) {

		Predicate filter = null;
		if (criteria.getSubcontinent() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(Country.SUBCONTINENT, JoinType.LEFT).get(Subcontinent.UUID), criteria.getSubcontinent().getUuid()));
		}
		if (criteria.getNameCodeLike() != null) {
			String[] textFilters = criteria.getNameCodeLike().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				// e.g. "GR", "GRC", "300" or "EL" all find Greece
				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, from.get(Country.ISO_CODE), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Country.ISO2_CODE), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Country.ISO_NUMERIC_CODE), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Country.UNO_CODE), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Country.EPIPULSE_LOCATION_CODE), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Country.DEFAULT_NAME), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter =
					CriteriaBuilderHelper.and(cb, filter, cb.or(cb.equal(from.get(Country.ARCHIVED), false), cb.isNull(from.get(Country.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Country.ARCHIVED), true));
			}
		}
		return filter;
	}

	public List<Country> getByExternalId(String externalId, boolean includeArchived) {
		return getByExternalId(externalId, Country.EXTERNAL_ID, includeArchived);
	}
}
