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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
import javax.validation.Validation;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateCriteria;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.survey.SurveyCriteria;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.survey.SurveyFacade;
import de.symeda.sormas.api.survey.SurveyIndexDto;
import de.symeda.sormas.api.survey.SurveyReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.docgeneration.DocumentTemplate;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateService;
import de.symeda.sormas.backend.docgeneration.TemplateEngine;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.FieldExtractor;
import fr.opensagres.xdocreport.template.FieldsExtractor;

@Stateless(name = "SurveyFacade")
@RightsAllowed(UserRight._SURVEY_VIEW)
public class SurveyFacadeEjb implements SurveyFacade {

	private static final String SURVEY_TOKEN_DOCUMENT_PLACEHOLDER = "surveyToken";

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SurveyService surveyService;
	@EJB
	private UserService userService;
	@EJB
	private DocumentTemplateFacadeEjb.DocumentTemplateFacadeEjbLocal documentTemplateFacade;
	@EJB
	private DocumentTemplateService documentTemplateService;

	private TemplateEngine templateEngine = new TemplateEngine();

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
	@RightsAllowed(UserRight._SURVEY_EDIT)
	public void uploadDocumentTemplate(@NotNull SurveyReferenceDto surveyRef, DocumentTemplateDto uploadedDocumentTemplate, byte[] fileContent)
		throws DocumentTemplateException {
		Survey existingSurvey = surveyService.getByReferenceDto(surveyRef);

		boolean validated = validateSurveyDocumentTemplate(fileContent);
		if (!validated) {
			throw new DocumentTemplateException(
				I18nProperties.getValidationError(Validations.surveyDocumentTemplateMissingTokenVariable, SURVEY_TOKEN_DOCUMENT_PLACEHOLDER));
		}

		List<DocumentTemplateDto> availableTemplates =
			documentTemplateFacade.getAvailableTemplates(new DocumentTemplateCriteria(DocumentWorkflow.SURVEY_DOCUMENT, null, surveyRef));
		if (!availableTemplates.isEmpty()) {
			availableTemplates.forEach(docTemplate -> {
				DocumentTemplate existingDocumentTemplate = documentTemplateService.getByUuid(docTemplate.getUuid());
				documentTemplateService.deletePermanent(existingDocumentTemplate);
			});
		}

		DocumentTemplateDto savedDocumentTemplate = documentTemplateFacade.saveDocumentTemplate(uploadedDocumentTemplate, fileContent);
		DocumentTemplate savedDocumentTemplateEntity = documentTemplateService.getByReferenceDto(savedDocumentTemplate.toReference());
		existingSurvey.setDocumentTemplate(savedDocumentTemplateEntity);
		surveyService.ensurePersisted(existingSurvey);
	}

	private boolean validateSurveyDocumentTemplate(byte[] fileContent) throws DocumentTemplateException {
		IXDocReport report = templateEngine.readXDocReport(new ByteArrayInputStream(fileContent));
		FieldsExtractor<FieldExtractor> extractor = FieldsExtractor.create();
		DocumentVariables documentVariables = null;
		try {
			report.extractFields(extractor);
			documentVariables = templateEngine.filterExtractedVariables(extractor);
		} catch (XDocReportException | IOException e) {
			throw new RuntimeException(e);
		}

		AtomicBoolean validUploadedFile = new AtomicBoolean(false);
		if (documentVariables != null && !documentVariables.getVariables().isEmpty()) {
			documentVariables.getVariables().forEach(docTemplateVariable -> {

				if (docTemplateVariable.contains(SURVEY_TOKEN_DOCUMENT_PLACEHOLDER)) {
					validUploadedFile.set(true);
				} ;
			});
		}

		return validUploadedFile.get();
	}

	@Override
	@RightsAllowed(UserRight._SURVEY_EDIT)
	public void uploadEmailTemplate(@NotNull SurveyReferenceDto surveyReference, DocumentTemplateDto uploadedEmailTemplateDto, byte[] fileContent)
		throws DocumentTemplateException {

		Survey existingSurvey = surveyService.getByUuid(surveyReference.getUuid());

		List<DocumentTemplateDto> availableTemplates =
			documentTemplateFacade.getAvailableTemplates(new DocumentTemplateCriteria(DocumentWorkflow.SURVEY_EMAIL, null, surveyReference));
		if (!availableTemplates.isEmpty()) {
			availableTemplates.forEach(docTemplate -> {
				DocumentTemplate existingDocumentTemplate = documentTemplateService.getByUuid(docTemplate.getUuid());
				documentTemplateService.deletePermanent(existingDocumentTemplate);
			});
		}

		DocumentTemplateDto savedDocumentTemplate = documentTemplateFacade.saveDocumentTemplate(uploadedEmailTemplateDto, fileContent);
		DocumentTemplate byReferenceDto = documentTemplateService.getByReferenceDto(savedDocumentTemplate.toReference());
		existingSurvey.setEmailTemplate(byReferenceDto);
		surveyService.ensurePersisted(existingSurvey);
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
}
