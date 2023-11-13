/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.event;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class EventGroupService extends AdoServiceWithUserFilterAndJurisdiction<EventGroup> {

	@EJB
	private CaseService caseService;

	public EventGroupService() {
		super(EventGroup.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, EventGroup> eventGroupPath) {
		return createUserFilter(cb, cq, eventGroupPath, null);
	}

	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(
		CriteriaBuilder cb,
		CriteriaQuery cq,
		From<?, EventGroup> eventGroupPath,
		EventUserFilterCriteria eventUserFilterCriteria) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel == JurisdictionLevel.NATION) {
			return null;
		}

		Join<EventGroup, Event> eventPath = eventGroupPath.join(EventGroup.EVENTS, JoinType.LEFT);
		Predicate filter = null;

		switch (jurisdictionLevel) {
		case REGION:
			if (currentUser.getRegion() != null) {
				filter = CriteriaBuilderHelper
					.or(cb, filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.REGION), currentUser.getRegion()));
			}
			break;
		case DISTRICT:
			if (currentUser.getDistrict() != null) {
				filter = CriteriaBuilderHelper
					.or(cb, filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.DISTRICT), currentUser.getDistrict()));
			}
			break;
		default:
		}

		if (filter != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				CriteriaBuilderHelper
					.limitedDiseasePredicate(cb, currentUser, eventPath.get(Event.DISEASE), cb.isNull(eventPath.get(Event.DISEASE))));
		}

		Predicate filterResponsible = cb.equal(eventPath.join(Event.REPORTING_USER, JoinType.LEFT), currentUser);
		filterResponsible = cb.or(filterResponsible, cb.equal(eventPath.join(Event.RESPONSIBLE_USER, JoinType.LEFT), currentUser));

		if (eventUserFilterCriteria != null && eventUserFilterCriteria.isIncludeUserCaseAndEventParticipantFilter()) {
			filter = CriteriaBuilderHelper.or(cb, filter, createCaseAndEventParticipantFilter(cb, cq, eventPath));
		}

		if (eventUserFilterCriteria != null && eventUserFilterCriteria.isForceRegionJurisdiction()) {
			filter = CriteriaBuilderHelper
				.or(cb, filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.REGION), currentUser.getRegion()));
		}

		if (filter != null) {
			filter = CriteriaBuilderHelper.or(cb, filter, filterResponsible);
		} else {
			filter = filterResponsible;
		}

		return filter;
	}

	public Predicate createCaseAndEventParticipantFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Event> eventPath) {

		Join<Event, EventParticipant> eventParticipantJoin = eventPath.join(Event.EVENT_PARTICIPANTS, JoinType.LEFT);
		Join<EventParticipant, Case> caseJoin = eventParticipantJoin.join(EventParticipant.RESULTING_CASE, JoinType.LEFT);

		Subquery<Long> caseSubquery = cq.subquery(Long.class);
		Root<Case> caseRoot = caseSubquery.from(Case.class);
		caseSubquery.where(caseService.createUserFilter(new CaseQueryContext(cb, cq, new CaseJoins(caseRoot))));
		caseSubquery.select(caseRoot.get(Case.ID));

		Predicate filter = cb.in(caseJoin.get(Case.ID)).value(caseSubquery);

		final User currentUser = getCurrentUser();
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel == JurisdictionLevel.REGION || jurisdictionLevel == JurisdictionLevel.DISTRICT) {
			Subquery<Long> eventParticipantSubquery = cq.subquery(Long.class);
			Root<EventParticipant> epRoot = eventParticipantSubquery.from(EventParticipant.class);

			switch (jurisdictionLevel) {
			case REGION:
				if (currentUser.getRegion() != null) {
					eventParticipantSubquery.where(
						cb.and(
							cb.equal(epRoot.get(EventParticipant.EVENT).get(Event.ID), eventPath.get(Event.ID)),
							cb.equal(epRoot.get(EventParticipant.REGION).get(Region.ID), currentUser.getRegion().getId())));
				}
				break;
			case DISTRICT:
				if (currentUser.getDistrict() != null) {
					eventParticipantSubquery.where(
						cb.and(
							cb.equal(epRoot.get(EventParticipant.EVENT).get(Event.ID), eventPath.get(Event.ID)),
							cb.equal(epRoot.get(EventParticipant.DISTRICT).get(District.ID), currentUser.getDistrict().getId())));
				}
				break;
			default:
			}

			eventParticipantSubquery.select(epRoot.get(EventParticipant.ID));

			filter = CriteriaBuilderHelper.or(cb, filter, cb.in(eventParticipantJoin.get(EventParticipant.ID)).value(eventParticipantSubquery));
		}

		return filter;

	}

	public Predicate buildCriteriaFilter(EventGroupCriteria eventGroupCriteria, CriteriaBuilder cb, From<?, EventGroup> from) {

		Predicate filter = null;

		if (eventGroupCriteria.getRelevanceStatus() != null) {
			if (eventGroupCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(from.get(EventGroup.ARCHIVED), false), cb.isNull(from.get(EventGroup.ARCHIVED))));
			} else if (eventGroupCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(EventGroup.ARCHIVED), true));
			}
		}

		Join<EventGroup, Event> eventJoin = from.join(EventGroup.EVENTS, JoinType.LEFT);
		if (eventGroupCriteria.getEvent() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(eventJoin.get(Event.UUID), eventGroupCriteria.getEvent().getUuid()));
		}
		if (eventGroupCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					eventJoin.join(Event.EVENT_LOCATION, JoinType.LEFT).join(Location.REGION, JoinType.LEFT).get(Region.UUID),
					eventGroupCriteria.getRegion().getUuid()));
		}
		if (eventGroupCriteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					eventJoin.join(Event.EVENT_LOCATION, JoinType.LEFT).join(Location.DISTRICT, JoinType.LEFT).get(District.UUID),
					eventGroupCriteria.getDistrict().getUuid()));
		}
		if (eventGroupCriteria.getCommunity() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					eventJoin.join(Event.EVENT_LOCATION, JoinType.LEFT).join(Location.COMMUNITY, JoinType.LEFT).get(Community.UUID),
					eventGroupCriteria.getCommunity().getUuid()));
		}
		if (eventGroupCriteria.getEventDateFrom() != null && eventGroupCriteria.getEventDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.between(eventJoin.get(Event.START_DATE), eventGroupCriteria.getEventDateFrom(), eventGroupCriteria.getEventDateTo()),
					cb.and(
						cb.isNotNull(eventJoin.get(Event.END_DATE)),
						cb.lessThan(eventJoin.get(Event.START_DATE), eventGroupCriteria.getEventDateFrom()),
						cb.greaterThanOrEqualTo(eventJoin.get(Event.END_DATE), eventGroupCriteria.getEventDateFrom()))));
		} else if (eventGroupCriteria.getEventDateFrom() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.greaterThanOrEqualTo(eventJoin.get(Event.START_DATE), eventGroupCriteria.getEventDateFrom()),
					cb.and(
						cb.isNotNull(eventJoin.get(Event.END_DATE)),
						cb.lessThan(eventJoin.get(Event.START_DATE), eventGroupCriteria.getEventDateFrom()),
						cb.greaterThanOrEqualTo(eventJoin.get(Event.END_DATE), eventGroupCriteria.getEventDateFrom()))));
		} else if (eventGroupCriteria.getEventDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.and(
						cb.isNull(eventJoin.get(Event.END_DATE)),
						cb.lessThanOrEqualTo(eventJoin.get(Event.START_DATE), eventGroupCriteria.getEventDateTo())),
					cb.lessThanOrEqualTo(eventJoin.get(Event.END_DATE), eventGroupCriteria.getEventDateTo())));
		}
		if (StringUtils.isNotEmpty(eventGroupCriteria.getFreeText())) {
			String[] textFilters = eventGroupCriteria.getFreeText().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, from.get(EventGroup.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(EventGroup.NAME), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (StringUtils.isNotEmpty(eventGroupCriteria.getFreeTextEvent())) {
			String[] textFilters = eventGroupCriteria.getFreeTextEvent().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, eventJoin.get(Event.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, eventJoin.get(Event.EVENT_TITLE), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, eventJoin.get(Event.EVENT_DESC), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, eventJoin.get(Event.SRC_FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, eventJoin.get(Event.SRC_LAST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, eventJoin.get(Event.SRC_EMAIL), textFilter),
					CriteriaBuilderHelper.ilike(cb, eventJoin.get(Event.SRC_TEL_NO), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, eventJoin.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.CITY), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}

		if (CollectionUtils.isNotEmpty(eventGroupCriteria.getExcludedUuids())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.not(from.get(AbstractDomainObject.UUID).in(eventGroupCriteria.getExcludedUuids())));
		}

		return filter;
	}
}
