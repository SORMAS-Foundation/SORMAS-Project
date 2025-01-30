/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.survey.SurveyTokenCriteria;
import de.symeda.sormas.api.survey.SurveyTokenDto;
import de.symeda.sormas.api.survey.SurveyTokenFacade;
import de.symeda.sormas.api.survey.SurveyTokenIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.document.DocumentFacadeEjb;
import de.symeda.sormas.backend.document.DocumentFacadeEjb.DocumentFacadeEjbLocal;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SurveyTokenFacade")
@RightsAllowed(UserRight._SURVEY_TOKEN_VIEW)
public class SurveyTokenFacadeEjb implements SurveyTokenFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SurveyTokenService surveyTokenService;
	@EJB
	private UserService userService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private DocumentFacadeEjbLocal documentFacade;

	@Override
	@RightsAllowed({
		UserRight._SURVEY_TOKEN_CREATE,
		UserRight._SURVEY_TOKEN_EDIT })
	public SurveyTokenDto save(@Valid SurveyTokenDto dto) {
		SurveyToken existingSurveyToken = dto.getUuid() != null ? surveyTokenService.getByUuid(dto.getUuid()) : null;

		FacadeHelper.checkCreateAndEditRights(existingSurveyToken, userService, UserRight.SURVEY_TOKEN_CREATE, UserRight.SURVEY_TOKEN_EDIT);

		validate(dto);

		SurveyToken surveyToken = fillOrBuildEntity(dto, existingSurveyToken);
		surveyTokenService.ensurePersisted(surveyToken);

		return toDto(surveyToken);
	}

	@Override
	public SurveyTokenDto getByUuid(String uuid) {
		return toDto(surveyTokenService.getByUuid(uuid));
	}

	@Override
	public long count(SurveyTokenCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<SurveyToken> root = cq.from(SurveyToken.class);

		Predicate filter = CriteriaBuilderHelper.and(cb, surveyTokenService.buildCriteriaFilter(criteria, cb, root, new SurveyTokenJoins(root)));
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<SurveyTokenIndexDto> getIndexList(SurveyTokenCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		final Root<SurveyToken> root = cq.from(SurveyToken.class);
		final SurveyTokenJoins joins = new SurveyTokenJoins(root);

		cq.multiselect(
			Stream
				.concat(
					Stream.of(
						root.get(SurveyToken.UUID),
						root.get(SurveyToken.TOKEN),
						joins.getCaseAssignedTo().get(Case.UUID),
						joins.getCaseAssignedToJoins().getPerson().get(Person.FIRST_NAME),
						joins.getCaseAssignedToJoins().getPerson().get(Person.LAST_NAME),
						root.get(SurveyToken.CASE_ASSIGNED_TO),
						root.get(SurveyToken.ASSIGNMENT_DATE)),
					// add sort properties to select
					sortBy(sortProperties, root, cb, cq).stream())
				.collect(Collectors.toList()));

		Predicate filter = CriteriaBuilderHelper.and(cb, surveyTokenService.buildCriteriaFilter(criteria, cb, root, joins));
		if (filter != null) {
			cq.where(filter);
		}

		return QueryHelper.getResultList(em, cq, new SurveyTokenIndexDtoResultTransformer(), null, null);
	}

	@Override
	public void deletePermanent(String uuid) {
		surveyTokenService.deletePermanent(surveyTokenService.getByUuid(uuid));
	}

	private List<Selection<?>> sortBy(List<SortProperty> sortProperties, Root<SurveyToken> root, CriteriaBuilder cb, CriteriaQuery<?> cq) {

		List<Selection<?>> selections = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				CriteriaBuilderHelper.OrderBuilder orderBuilder = CriteriaBuilderHelper.createOrderBuilder(cb, sortProperty.ascending);
				final List<Order> orderList;
				switch (sortProperty.propertyName) {
				case Survey.UUID:
				case Survey.DISEASE:
					orderList = orderBuilder.build(root.get(sortProperty.propertyName));
					break;
				case Survey.NAME:
					orderList = orderBuilder.build(cb.lower(root.get(sortProperty.propertyName)));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}

				order.addAll(orderList);
				selections.addAll(orderList.stream().map(Order::getExpression).collect(Collectors.toList()));
			}
			cq.orderBy(order);
		} else {
			Path<Object> changeDate = root.get(SurveyToken.CHANGE_DATE);
			cq.orderBy(cb.desc(changeDate));
			selections.add(changeDate);
		}

		return selections;
	}

	private void validate(SurveyTokenDto dto) {
	}

	private SurveyToken fillOrBuildEntity(SurveyTokenDto source, SurveyToken target) {
		target = DtoHelper.fillOrBuildEntity(source, target, SurveyToken::new, true);

		target.setToken(source.getToken());
		target.setCaseAssignedTo(caseService.getByReferenceDto(source.getCaseAssignedTo()));
		target.setAssignmentDate(source.getAssignmentDate());
		target.setRecipientEmail(source.getRecipientEmail());
		target.setResponseReceived(source.isResponseReceived());

		return target;
	}

	private SurveyTokenDto toDto(SurveyToken source) {
		if (source == null) {
			return null;
		}

		SurveyTokenDto target = new SurveyTokenDto();
		DtoHelper.fillDto(target, source);

		target.setToken(source.getToken());
		target.setCaseAssignedTo(caseFacade.convertToReferenceDto(source.getCaseAssignedTo()));
		target.setAssignmentDate(source.getAssignmentDate());
		target.setRecipientEmail(source.getRecipientEmail());
		target.setGeneratedDocument(DocumentFacadeEjb.toReferenceDto(source.getGeneratedDocument()));
		target.setResponseReceived(source.isResponseReceived());

		return target;
	}

	@LocalBean
	@Stateless
	public static class SurveyTokenFacadeEjbLocal extends SurveyTokenFacadeEjb {

	}
}
