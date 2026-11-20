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

package de.symeda.sormas.backend.epipulse;

import java.util.logging.Logger;

import javax.ejb.EJB;
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
import de.symeda.sormas.backend.epipulse.export.EpipulseConfigurationLookupService;

@Stateless
@LocalBean
public class EpipulseExportService extends BaseAdoService<EpipulseExport> {

	private static final Logger logger = Logger.getLogger(EpipulseExportService.class.getName());

	@EJB
	private EpipulseConfigurationLookupService configurationLookupService;

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

	/**
	 * Whether EpiPulse exports can be created: an EpiPulse data source is configured, either as system configuration value
	 * {@code EPIPULSE_DATASOURCE} (entered by an admin, e.g. {@code "LU-SYSTEM1"}) or in the disease configuration of at
	 * least one disease.
	 */
	public boolean configured() {
		try {
			return configurationLookupService.isDatasourceConfigured();
		} catch (Exception e) {
			logger.warning("Error checking the EpiPulse configuration: " + e.getMessage());
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
