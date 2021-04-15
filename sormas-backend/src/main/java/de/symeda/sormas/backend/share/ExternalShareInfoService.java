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

package de.symeda.sormas.backend.share;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class ExternalShareInfoService extends AdoServiceWithUserFilter<ExternalShareInfo> {

	@EJB
	private UserService userService;

	public ExternalShareInfoService() {
		super(ExternalShareInfo.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, ExternalShareInfo> from) {
		return null;
	}

	public Predicate buildCriteriaFilter(ExternalShareInfoCriteria criteria, CriteriaBuilder cb, Root<ExternalShareInfo> shareInfo) {
		Predicate filter = null;

		if (criteria.getCaze() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(shareInfo.join(ExternalShareInfo.CAZE, JoinType.LEFT).get(Case.UUID), criteria.getCaze().getUuid()));
		}

		if (criteria.getEvent() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(shareInfo.join(ExternalShareInfo.EVENT, JoinType.LEFT).get(Case.UUID), criteria.getEvent().getUuid()));
		}

		return filter;
	}

	public void createAndPersistShareInfo(Case caze, ExternalShareStatus status) {
		createAndPersistShareInfo(status, caze, ExternalShareInfo::setCaze);
	}

	public void createAndPersistShareInfo(Event event, ExternalShareStatus status) {
		createAndPersistShareInfo(status, event, ExternalShareInfo::setEvent);
	}

	public boolean isCaseShared(Long caseId) {
		return exists((cb, root) -> cb.equal(root.get(ExternalShareInfo.CAZE).get(Case.ID), caseId));
	}

	public boolean isEventShared(Long eventId) {
		return exists((cb, root) -> cb.equal(root.get(ExternalShareInfo.EVENT).get(Event.ID), eventId));
	}

	private <T> void createAndPersistShareInfo(ExternalShareStatus status, T associatedEntity, BiConsumer<ExternalShareInfo, T> setAssociatedEntity) {
		ExternalShareInfo shareInfo = new ExternalShareInfo();

		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setCreationDate(new Timestamp(new Date().getTime()));

		setAssociatedEntity.accept(shareInfo, associatedEntity);

		shareInfo.setSender(userService.getCurrentUser());
		shareInfo.setStatus(status);

		ensurePersisted(shareInfo);
	}

	public List<ExternalShareInfoCountAndLatestDate> getCaseShareCountAndLatestDate(List<Long> caseIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExternalShareInfoCountAndLatestDate> cq = cb.createQuery(ExternalShareInfoCountAndLatestDate.class);
		Root<ExternalShareInfo> root = cq.from(ExternalShareInfo.class);
		Path<String> cazeId = root.join(ExternalShareInfo.CAZE, JoinType.LEFT).get(Case.ID);
		Path<String> creationDate = root.get(ExternalShareInfo.CREATION_DATE);

		Subquery<String> latestShareInfoSubQuery = cq.subquery(String.class);
		Root<ExternalShareInfo> latestShareInfoRoot = latestShareInfoSubQuery.from(ExternalShareInfo.class);

		latestShareInfoSubQuery.select(
			cb.function(
				ExtendedPostgreSQL94Dialect.CONCAT_FUNCTION,
				String.class,
				latestShareInfoRoot.get(ExternalShareInfo.CAZE),
				cb.max(latestShareInfoRoot.get(ExternalShareInfo.CREATION_DATE))));
		latestShareInfoSubQuery.groupBy(latestShareInfoRoot.get(ExternalShareInfo.CAZE));

		Subquery<Long> countSubQuery = cq.subquery(Long.class);
		Root<ExternalShareInfo> countRoot = countSubQuery.from(ExternalShareInfo.class);
		countSubQuery.select(cb.count(countRoot.get(ExternalShareInfo.ID)));
		countSubQuery.where(cb.equal(countRoot.get(ExternalShareInfo.CAZE), cazeId));
		countSubQuery.groupBy(countRoot.get(ExternalShareInfo.CAZE));

		cq.multiselect(cazeId, countSubQuery, creationDate, root.get(ExternalShareInfo.STATUS));
		cq.where(
			cb.function(ExtendedPostgreSQL94Dialect.CONCAT_FUNCTION, String.class, cazeId, creationDate).in(latestShareInfoSubQuery),
			cazeId.in(caseIds));

		return em.createQuery(cq).getResultList();
	}
}
