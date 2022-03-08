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

package de.symeda.sormas.backend.common;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.util.IterableHelper;

public abstract class AbstractCoreAdoService<ADO extends CoreAdo> extends AbstractDeletableAdoService<ADO> {

	private static final int ARCHIVE_BATCH_SIZE = 1000;

	@EJB
	protected FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	protected AbstractCoreAdoService(Class<ADO> elementClass) {
		super(elementClass);
	}

	public boolean isArchived(String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ADO> from = cq.from(getElementClass());

		cq.where(cb.and(cb.equal(from.get(CoreAdo.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), uuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	protected <T extends ChangeDateBuilder<T>> T addChangeDates(T builder, From<?, ADO> adoPath, boolean includeExtendedChangeDateFilters) {
		return builder.add(adoPath);
	}

	public Map<String, Date> calculateEndOfProcessingDate(List<String> entityuuids) {

		if (entityuuids.isEmpty()) {
			return Collections.emptyMap();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<ADO> from = cq.from(getElementClass());

		Expression aggregatedChangeDateExpression = addChangeDates(new AggregatedChangeDateExpressionBuilder(cb), from, true).build();
		cq.multiselect(from.get(AbstractDomainObject.UUID), cb.max(aggregatedChangeDateExpression));
		cq.where(from.get(ADO.UUID).in(entityuuids));
		cq.groupBy(from.get(AbstractDomainObject.UUID));

		Map<String, Date> collect = em.createQuery(cq).getResultList().stream().collect(Collectors.toMap(r -> (String) r[0], r -> (Date) r[1]));
		return collect;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void archive(String entityUuid, Date endOfProcessingDate) {

		if (endOfProcessingDate == null) {
			endOfProcessingDate = calculateEndOfProcessingDate(Collections.singletonList(entityUuid)).get(entityUuid);
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<ADO> cu = cb.createCriteriaUpdate(getElementClass());
		Root<ADO> root = cu.from(getElementClass());

		cu.set(AbstractDomainObject.CHANGE_DATE, Timestamp.from(Instant.now()));
		cu.set(root.get(Case.ARCHIVED), true);
		cu.set(root.get(CoreAdo.END_OF_PROCESSING_DATE), endOfProcessingDate);

		cu.where(cb.equal(root.get(AbstractDomainObject.UUID), entityUuid));

		em.createQuery(cu).executeUpdate();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void archive(List<String> entityUuids) {

		IterableHelper.executeBatched(
			entityUuids,
			ARCHIVE_BATCH_SIZE,
			batchedUuids -> calculateEndOfProcessingDate(batchedUuids).forEach((entityUuid, finalEndOfProcessingDate) -> {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<ADO> cu = cb.createCriteriaUpdate(getElementClass());
				Root<ADO> root = cu.from(getElementClass());

				cu.set(AbstractDomainObject.CHANGE_DATE, Timestamp.from(Instant.now()));
				cu.set(root.get(Case.ARCHIVED), true);
				cu.set(root.get(CoreAdo.END_OF_PROCESSING_DATE), finalEndOfProcessingDate);

				cu.where(cb.equal(root.get(AbstractDomainObject.UUID), entityUuid));

				em.createQuery(cu).executeUpdate();
			}));
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void dearchive(List<String> entityUuids, String dearchiveReason) {

		IterableHelper.executeBatched(entityUuids, ARCHIVE_BATCH_SIZE, batchedUuids -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaUpdate<ADO> cu = cb.createCriteriaUpdate(getElementClass());
			Root<ADO> root = cu.from(getElementClass());

			cu.set(AbstractDomainObject.CHANGE_DATE, Timestamp.from(Instant.now()));
			cu.set(root.get(Case.ARCHIVED), false);
			cu.set(root.get(CoreAdo.END_OF_PROCESSING_DATE), (Date) null);
			cu.set(root.get(CoreAdo.ARCHIVE_UNDONE_REASON), dearchiveReason);

			cu.where(root.get(AbstractDomainObject.UUID).in(batchedUuids));

			em.createQuery(cu).executeUpdate();
		});
	}

	public boolean isEditAllowed(ADO entity, boolean withArchive) {
		if (withArchive) {
			return !entity.isArchived() || featureConfigurationFacade.isFeatureEnabled(FeatureType.EDIT_ARCHIVED_ENTITIES);
		}
		return true;
	}
}
