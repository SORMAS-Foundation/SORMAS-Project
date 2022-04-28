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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.share.ExternalShareCriteria;
import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
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
		return exists((cb, root, cq) -> cb.equal(root.get(ExternalShareInfo.CAZE).get(Case.ID), caseId));
	}

	public boolean isEventShared(Long eventId) {
		return exists((cb, root, cq) -> cb.equal(root.get(ExternalShareInfo.EVENT).get(Event.ID), eventId));
	}

	public List<ExternalShareInfo> getShareInfoByCase(String caseUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExternalShareInfo> cq = cb.createQuery(ExternalShareInfo.class);
		Root<ExternalShareInfo> root = cq.from(ExternalShareInfo.class);

		cq.where(buildCriteriaFilter(new ExternalShareInfoCriteria().caze(new CaseReferenceDto(caseUuid)), cb, root));
		return em.createQuery(cq).getResultList();
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
		return getShareCountAndLatestDate(caseIds, ExternalShareInfo.CAZE);
	}

	public List<ExternalShareInfoCountAndLatestDate> getEventShareCountAndLatestDate(List<Long> eventIds) {
		return getShareCountAndLatestDate(eventIds, ExternalShareInfo.EVENT);
	}

	public List<ExternalShareInfoCountAndLatestDate> getShareCountAndLatestDate(List<Long> ids, String associatedObjectName) {
		if (ids.size() == 0) {
			return Collections.emptyList();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExternalShareInfoCountAndLatestDate> cq = cb.createQuery(ExternalShareInfoCountAndLatestDate.class);
		Root<ExternalShareInfo> root = cq.from(ExternalShareInfo.class);
		Path<String> associatedObjectId = root.join(associatedObjectName, JoinType.LEFT).get(AbstractDomainObject.ID);
		Path<String> associatedObjectUuid = root.join(associatedObjectName, JoinType.LEFT).get(AbstractDomainObject.UUID);
		Path<String> creationDate = root.get(ExternalShareInfo.CREATION_DATE);

		Subquery<String> latestShareInfoSubQuery = cq.subquery(String.class);
		Root<ExternalShareInfo> latestShareInfoRoot = latestShareInfoSubQuery.from(ExternalShareInfo.class);
		Path<Object> latestShareInfoAssociatedObject = latestShareInfoRoot.get(associatedObjectName);
		latestShareInfoSubQuery.select(
			cb.function(
				ExtendedPostgreSQL94Dialect.CONCAT_FUNCTION,
				String.class,
				latestShareInfoAssociatedObject.get(AbstractDomainObject.ID),
				cb.max(latestShareInfoRoot.get(ExternalShareInfo.CREATION_DATE))));
		latestShareInfoSubQuery.where(latestShareInfoAssociatedObject.get(AbstractDomainObject.ID).in(ids));
		latestShareInfoSubQuery.groupBy(latestShareInfoAssociatedObject);

		Subquery<Long> countSubQuery = cq.subquery(Long.class);
		Root<ExternalShareInfo> countRoot = countSubQuery.from(ExternalShareInfo.class);
		countSubQuery.select(cb.count(countRoot.get(ExternalShareInfo.ID)));
		Path<Object> countAssociatedObject = countRoot.get(associatedObjectName);
		countSubQuery.where(cb.equal(countAssociatedObject.get(AbstractDomainObject.ID), associatedObjectId));
		countSubQuery.groupBy(countAssociatedObject);

		cq.multiselect(associatedObjectUuid, countSubQuery, creationDate, root.get(ExternalShareInfo.STATUS));
		cq.distinct(true);
		cq.where(
			cb.function(ExtendedPostgreSQL94Dialect.CONCAT_FUNCTION, String.class, associatedObjectId, creationDate).in(latestShareInfoSubQuery),
			associatedObjectId.in(ids));

		return em.createQuery(cq).getResultList();
	}

	public Predicate buildShareCriteriaFilter(
		ExternalShareCriteria criteria,
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		From<?, ?> from,
		String associatedObjectName,
		Function<Expression<Date>, Predicate> changeDatePredicateBuilder) {

		Predicate filter = null;

		if (Boolean.TRUE.equals(criteria.getOnlyEntitiesNotSharedWithExternalSurvTool())) {
			Subquery<Long> survToolShareSubQuery = cq.subquery(Long.class);
			Root<ExternalShareInfo> survToolShareRoot = survToolShareSubQuery.from(ExternalShareInfo.class);
			survToolShareSubQuery.select(survToolShareRoot.get(ExternalShareInfo.ID));
			survToolShareSubQuery.where(cb.equal(survToolShareRoot.join(associatedObjectName), from.get(AbstractDomainObject.ID)));

			filter = CriteriaBuilderHelper.and(cb, filter, cb.not(cb.exists(survToolShareSubQuery)));
		}

		if (Boolean.TRUE.equals(criteria.getOnlyEntitiesSharedWithExternalSurvTool())) {
			Subquery<Long> survToolShareSubQuery = cq.subquery(Long.class);
			Root<ExternalShareInfo> survToolShareRoot = survToolShareSubQuery.from(ExternalShareInfo.class);
			survToolShareSubQuery.select(survToolShareRoot.get(ExternalShareInfo.ID));
			survToolShareSubQuery.where(cb.equal(survToolShareRoot.join(associatedObjectName), from.get(AbstractDomainObject.ID)));

			filter = CriteriaBuilderHelper.and(cb, filter, cb.exists(survToolShareSubQuery));
		}

		if (Boolean.TRUE.equals(criteria.getOnlyEntitiesChangedSinceLastSharedWithExternalSurvTool())) {
			Predicate changedSinceLastShareFilter =
				buildLatestSurvToolShareDateFilter(cq, cb, from, associatedObjectName, changeDatePredicateBuilder);

			filter = CriteriaBuilderHelper.and(cb, filter, changedSinceLastShareFilter);
		}

		// Exclude all entities which are not supposed to be shared with the reportingtool
		if (filter != null && from.getJavaType().isAssignableFrom(Case.class)) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(Case.DONT_SHARE_WITH_REPORTING_TOOL)));
		}

		return filter;
	}

	public Predicate buildLatestSurvToolShareDateFilter(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		From<?, ?> from,
		String associatedObjectName,
		Function<Expression<Date>, Predicate> shareDatePredicateBuilder) {

		Subquery<Timestamp> survToolShareSubQuery = cq.subquery(Timestamp.class);
		Root<ExternalShareInfo> survToolShareRoot = survToolShareSubQuery.from(ExternalShareInfo.class);
		Join<ExternalShareInfo, ?> associatedObject = survToolShareRoot.join(associatedObjectName, JoinType.LEFT);
		@SuppressWarnings({
			"unchecked",
			"rawtypes" })
		Expression<Date> latestShareDate =
			// double conversion because hibernate doesn't know the `max` function for timestamps
			(Expression<Date>) ((Expression) cb.max(survToolShareRoot.get(ExternalShareInfo.CREATION_DATE)));

		Path<Timestamp> associatedObjectId = associatedObject.get(AbstractDomainObject.ID);

		survToolShareSubQuery.select(associatedObjectId);
		survToolShareSubQuery.where(cb.equal(associatedObject, from.get(AbstractDomainObject.ID)));
		survToolShareSubQuery.groupBy(associatedObjectId);
		survToolShareSubQuery.having(shareDatePredicateBuilder.apply(latestShareDate));

		return cb.exists(survToolShareSubQuery);
	}
}
