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

package de.symeda.sormas.backend.epipulse;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.epipulse.EpipulseExportCriteria;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

@Stateless
@LocalBean
public class EpipulseExportService extends BaseAdoService<EpipulseExport> {

	private static final Logger logger = Logger.getLogger(EpipulseExportService.class.getName());

	public EpipulseExportService() {
		super(EpipulseExport.class);
	}

	public Predicate buildCriteriaFilter(EpipulseExportCriteria criteria, EpipulseExportQueryContext queryContext) {

		final CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		final EpipulseExportJoins joins = queryContext.getJoins();
		final From<?, EpipulseExport> from = queryContext.getRoot();

		Predicate filter = null;

		if (criteria.getSubjectCode() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(EpipulseExport.SUBJECT_CODE), criteria.getSubjectCode()));
		}
		if (criteria.getStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(EpipulseExport.STATUS), criteria.getStatus()));
		}
		if (criteria.getReportDateFrom() != null && criteria.getReportDateTo() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(from.get(EpipulseExport.START_DATE), criteria.getReportDateFrom()));
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThan(from.get(EpipulseExport.END_DATE), criteria.getReportDateTo()));
		}
		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(from.get(EpipulseExport.ARCHIVED), false), cb.isNull(from.get(EpipulseExport.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(EpipulseExport.ARCHIVED), true));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.DELETED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(EpipulseExport.DELETED), true));
			}
		}
		if (criteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(EpipulseExport.DELETED)));
		}

		return filter;
	}

	public boolean configured() {
		try {
			//@formatter:off
            List<String> requiredTables =
                    Arrays.asList(
                            EpipulseDatasourceConfiguration.TABLE_NAME,
                            EpipulseLocationConfiguration.TABLE_NAME,
                            EpipulseSubjectcodeConfiguration.TABLE_NAME
                    );

            String query = "SELECT COUNT(*) " +
                    "FROM information_schema.tables " +
                    "WHERE table_schema = 'public' " +
                    "AND table_name IN (" +
                    String.join(",", requiredTables.stream()
                            .map(table -> "'" + table + "'")
                            .collect(Collectors.toList())) +
                    ")";
            //@formatter:on

			Number count = (Number) em.createNativeQuery(query).getSingleResult();
			return count.intValue() == requiredTables.size();
		} catch (Exception e) {
			logger.warning("Error checking epipulse configuration tables: " + e.getMessage());
			return false;
		}
	}

	public boolean isArchived(String uuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EpipulseExport> from = cq.from(getElementClass());

		cq.where(cb.and(cb.equal(from.get(EpipulseExport.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), uuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}
}
