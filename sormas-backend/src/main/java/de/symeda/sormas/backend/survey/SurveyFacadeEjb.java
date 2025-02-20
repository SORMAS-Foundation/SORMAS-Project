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

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.PermitAll;
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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.externalemail.AttachmentException;
import de.symeda.sormas.api.externalemail.ExternalEmailException;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.survey.SurveyCriteria;
import de.symeda.sormas.api.survey.SurveyDocumentOptionsDto;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.survey.SurveyFacade;
import de.symeda.sormas.api.survey.SurveyIndexDto;
import de.symeda.sormas.api.survey.SurveyReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.docgeneration.DocGenerationHelper;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateEntitiesBuilder;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.DocumentTemplateFacadeEjbLocal;
import de.symeda.sormas.backend.docgeneration.RootEntities;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.externalemail.ExternalEmailFacadeEjb.ExternalEmailFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SurveyFacade")
@RightsAllowed(UserRight._SURVEY_VIEW)
public class SurveyFacadeEjb implements SurveyFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SurveyService surveyService;
	@EJB
	private UserService userService;
	@EJB
	private DocumentTemplateFacadeEjbLocal documentTemplateFacade;
	@EJB
	private DocumentTemplateEntitiesBuilder entitiesBuilder;
	@EJB
	private DocGenerationHelper docGenerationHelper;
	@EJB
	private SurveyTokenService surveyTokenService;
	@EJB
	private CaseService caseService;
	@EJB
	private DocumentService documentService;
	@EJB
	private ExternalEmailFacadeEjbLocal externalEmailFacade;

	@Override
	@RightsAllowed({
		UserRight._SURVEY_CREATE,
		UserRight._SURVEY_EDIT })
	public SurveyDto save(@Valid SurveyDto dto) {
		Survey existingSurvey = dto.getUuid() != null ? surveyService.getByUuid(dto.getUuid()) : null;

		FacadeHelper.checkCreateAndEditRights(existingSurvey, userService, UserRight.SURVEY_CREATE, UserRight.SURVEY_EDIT);

		validate(dto);

		Survey survey = fillOrBuildEntity(dto, existingSurvey);
		surveyService.ensurePersisted(survey);

		return toDto(survey);
	}

	@Override
	@RightsAllowed(UserRight._SURVEY_VIEW)
	public SurveyDto getByUuid(String uuid) {
		return toDto(surveyService.getByUuid(uuid));
	}

	@Override
	@RightsAllowed(UserRight._SURVEY_VIEW)
	public long count(SurveyCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Survey> root = cq.from(Survey.class);

		Predicate filter = CriteriaBuilderHelper.and(cb, surveyService.buildCriteriaFilter(criteria, cb, root));
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	@RightsAllowed(UserRight._SURVEY_VIEW)
	public List<SurveyIndexDto> getIndexList(SurveyCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		final Root<Survey> root = cq.from(Survey.class);

		cq.multiselect(
			Stream.concat(
				Stream.of(root.get(Survey.UUID), root.get(Survey.NAME), root.get(Survey.DISEASE)),
				// add sort properties to select
				sortBy(sortProperties, root, cb, cq).stream()).collect(Collectors.toList()));

		Predicate filter = CriteriaBuilderHelper.and(cb, surveyService.buildCriteriaFilter(criteria, cb, root));
		if (filter != null) {
			cq.where(filter);
		}

		return QueryHelper.getResultList(em, cq, new SurveyIndexDtoResultTransformer(), null, null);
	}

	private List<Selection<?>> sortBy(List<SortProperty> sortProperties, Root<Survey> root, CriteriaBuilder cb, CriteriaQuery<?> cq) {

		List<Selection<?>> selections = new ArrayList<>();

		if (isNotEmpty(sortProperties)) {
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
			Path<Object> changeDate = root.get(Survey.CHANGE_DATE);
			cq.orderBy(cb.desc(changeDate));
			selections.add(changeDate);
		}

		return selections;
	}

	@Override
	@RightsAllowed(UserRight._SURVEY_DELETE)
	public void deletePermanent(String uuid) {
		surveyService.deletePermanent(surveyService.getByUuid(uuid));
	}

	@Override
	public boolean exists(String uuid) {
		return surveyService.exists(uuid);
	}

	@Override
	public SurveyReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(surveyService.getByUuid(uuid));
	}

	@Override
	public Boolean isEditAllowed(String uuid) {
		Survey survey = surveyService.getByUuid(uuid);
		return surveyService.isEditAllowed(survey);
	}

	@Override
	public List<SurveyDto> getAllByDisease(Disease disease) {
		return surveyService.getByPredicate((cb, root, cq) -> cb.equal(root.get(Survey.DISEASE), disease))
			.stream()
			.map(this::toDto)
			.collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public void generateDocument(SurveyDocumentOptionsDto surveyOptions) throws DocumentTemplateException, ValidationException {
		SurveyDto survey = getByUuid(surveyOptions.getSurvey().getUuid());

		createSurveyDocument(survey, surveyOptions);
	}

	@Override
	@PermitAll
	public void sendDocument(SurveyDocumentOptionsDto surveyOptions)
		throws DocumentTemplateException, ValidationException, AttachmentException, IOException, ExternalEmailException {
		Survey survey = surveyService.getByReferenceDto(surveyOptions.getSurvey());
		SurveyDto surveyDto = toDto(survey);

		TokenAndDocument documentResult = createSurveyDocument(surveyDto, surveyOptions);

		ExternalEmailOptionsDto emailOptions = new ExternalEmailOptionsDto(
			survey.getEmailTemplate().getWorkflow(),
			surveyOptions.getRootEntityType(),
			surveyOptions.getRootEntityReference());
		emailOptions.setRecipientEmail(surveyOptions.getRecipientEmail());
		emailOptions.setTemplate(surveyDto.getEmailTemplate());
		emailOptions.setAttachedDocuments(Collections.singleton(documentResult.document.toReference()));

		externalEmailFacade.sendEmail(emailOptions);

		documentResult.token.setRecipientEmail(surveyOptions.getRecipientEmail());
		surveyTokenService.ensurePersisted(documentResult.token);
	}

	@Override
	public boolean hasUnassignedTokens(SurveyReferenceDto survey) {
		return surveyTokenService.count((cb, root) -> {
			SurveyTokenJoins joins = new SurveyTokenJoins(root);

			return cb.and(cb.equal(joins.getSurvey().get(Survey.UUID), survey.getUuid()), cb.isNull(root.get(SurveyToken.CASE_ASSIGNED_TO)));
		}) > 0;
	}

	private TokenAndDocument createSurveyDocument(SurveyDto survey, SurveyDocumentOptionsDto surveyOptions)
		throws DocumentTemplateException, ValidationException {
		RootEntityType rootEntityType = surveyOptions.getRootEntityType();
		ReferenceDto rootEntityReference = surveyOptions.getRootEntityReference();

		if (rootEntityType != RootEntityType.ROOT_CASE) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.surveyOnlyCasesSupported));
		}

		SurveyToken token = surveyTokenService.getFistUnusedToken(survey.toReference());
		if (token == null) {
			throw new ValidationException(I18nProperties.getString(Strings.errorSurveyTokenNotAvailable));
		}

		DocumentTemplateEntities entities = entitiesBuilder.resolveEntities(new RootEntities().addReference(rootEntityType, rootEntityReference));
		byte[] documentContent =
			documentTemplateFacade.generateDocumentDocxFromEntities(survey.getDocumentTemplate(), entities, surveyOptions.getTemplateProperties());

		DocumentDto documentDto = docGenerationHelper.saveDocument(
			docGenerationHelper.getDocumentFileName(rootEntityReference, survey.getDocumentTemplate()),
			DocumentDto.MIME_TYPE_DEFAULT,
			rootEntityReference,
			documentContent);

		token.setCaseAssignedTo(caseService.getByReferenceDto(((CaseDataDto) entities.getEntity(RootEntityType.ROOT_CASE)).toReference()));
		token.setAssignmentDate(new Date());
		token.setGeneratedDocument(documentService.getByReferenceDto(documentDto.toReference()));

		surveyTokenService.ensurePersisted(token);

		return new TokenAndDocument(token, documentDto);
	}

	private void validate(SurveyDto survey) {

	}

	private Survey fillOrBuildEntity(SurveyDto source, Survey target) {
		target = DtoHelper.fillOrBuildEntity(source, target, Survey::new, true);

		target.setName(source.getName());
		target.setDisease(source.getDisease());

		return target;
	}

	private SurveyDto toDto(Survey source) {
		if (source == null) {
			return null;
		}

		SurveyDto target = new SurveyDto();
		DtoHelper.fillDto(target, source);

		target.setName(source.getName());
		target.setDisease(source.getDisease());
		target.setDocumentTemplate(DocumentTemplateFacadeEjb.toReferenceDto(source.getDocumentTemplate()));
		target.setEmailTemplate(DocumentTemplateFacadeEjb.toReferenceDto(source.getEmailTemplate()));

		return target;
	}

	public static SurveyReferenceDto toReferenceDto(Survey entity) {

		if (entity == null) {
			return null;
		}

		return new SurveyReferenceDto(entity.getUuid(), entity.getName());
	}

	public SurveyReferenceDto convertToReferenceDto(Survey survey) {
		return toSurveyReferenceDto(survey);
	}

	public static SurveyReferenceDto toSurveyReferenceDto(Survey survey) {
		return new SurveyReferenceDto(survey.getUuid(), survey.getName());
	}

	@LocalBean
	@Stateless
	public static class SurveyFacadeEjbLocal extends SurveyFacadeEjb {

	}

	private static final class TokenAndDocument {

		private final SurveyToken token;
		private final DocumentDto document;

		public TokenAndDocument(SurveyToken token, DocumentDto document) {
			this.token = token;
			this.document = document;
		}
	}
}
