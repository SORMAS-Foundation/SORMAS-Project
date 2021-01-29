package de.symeda.sormas.backend.campaign.form;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.safety.Whitelist;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaFacade;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "CampaignFormMetaFacade")
public class CampaignFormMetaFacadeEjb implements CampaignFormMetaFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CampaignFormMetaService service;
	@EJB
	private UserService userService;

	public CampaignFormMeta fromDto(@NotNull CampaignFormMetaDto source, boolean checkChangeDate) {
		CampaignFormMeta target = DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), CampaignFormMeta::new, checkChangeDate);

		target.setFormId(source.getFormId());
		target.setFormName(source.getFormName());
		target.setLanguageCode(source.getLanguageCode());
		target.setCampaignFormElementsList(source.getCampaignFormElements());
		target.setCampaignFormTranslationsList(source.getCampaignFormTranslations());

		return target;
	}

	public CampaignFormMetaDto toDto(CampaignFormMeta source) {
		if (source == null) {
			return null;
		}

		CampaignFormMetaDto target = new CampaignFormMetaDto();
		DtoHelper.fillDto(target, source);

		target.setFormId(source.getFormId());
		target.setFormName(source.getFormName());
		target.setLanguageCode(source.getLanguageCode());
		target.setCampaignFormElements(source.getCampaignFormElementsList());
		target.setCampaignFormTranslations(source.getCampaignFormTranslationsList());

		return target;
	}

	@Override
	public CampaignFormMetaDto saveCampaignFormMeta(CampaignFormMetaDto campaignFormMetaDto) throws ValidationRuntimeException {
		validateAndClean(campaignFormMetaDto);

		CampaignFormMeta campaignFormMeta = fromDto(campaignFormMetaDto, true);
		service.ensurePersisted(campaignFormMeta);
		return toDto(campaignFormMeta);
	}

	@Override
	public CampaignFormMetaDto buildCampaignFormMetaFromJson(String formId, String languageCode, String schemaDefinitionJson, String translationsJson)
		throws IOException {
		CampaignFormMetaDto campaignForm = new CampaignFormMetaDto();
		campaignForm.setFormId(formId);
		campaignForm.setLanguageCode(languageCode);
		ObjectMapper mapper = new ObjectMapper();
		if (StringUtils.isNotBlank(schemaDefinitionJson)) {
			campaignForm.setCampaignFormElements(Arrays.asList(mapper.readValue(schemaDefinitionJson, CampaignFormElement[].class)));
		}
		if (StringUtils.isNotBlank(translationsJson)) {
			campaignForm.setCampaignFormTranslations(Arrays.asList(mapper.readValue(translationsJson, CampaignFormTranslations[].class)));
		}

		return campaignForm;
	}

	@Override
	public List<CampaignFormMetaReferenceDto> getAllCampaignFormMetasAsReferences() {
		return service.getAll()
			.stream()
			.map(CampaignFormMetaFacadeEjb::toReferenceDto)
			.sorted(Comparator.comparing(ReferenceDto::toString))
			.collect(Collectors.toList());
	}

	@Override
	public CampaignFormMetaDto getCampaignFormMetaByUuid(String campaignFormUuid) {
		return toDto(service.getByUuid(campaignFormUuid));
	}

	@Override
	public List<CampaignFormMetaDto> getAllAfter(Date date) {
		final List<CampaignFormMeta> allAfter = service.getAllAfter(date, userService.getCurrentUser());
		return allAfter.stream().map(campaignFormMeta -> toDto(campaignFormMeta)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return service.getAllUuids();
	}

	@Override
	public List<CampaignFormMetaDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(campaignFormMeta -> toDto(campaignFormMeta)).collect(Collectors.toList());
	}

	@Override
	public List<CampaignFormMetaReferenceDto> getCampaignFormMetasAsReferencesByCampaign(String uuid) {
		return service.getCampaignFormMetasAsReferencesByCampaign(uuid);
	}

	@Override
	public void validateAllFormMetas() {
		List<CampaignFormMeta> forms = service.getAll();

		for (CampaignFormMeta form : forms) {
			try {
				CampaignFormMetaDto formDto = toDto(form);
				validateAndClean(formDto);
			} catch (ValidationRuntimeException e) {
				throw new ValidationRuntimeException(form.getFormId() + ": " + e.getMessage());
			} catch (Exception e) {
				throw new ValidationRuntimeException(
					form.getFormId() + ": "
						+ I18nProperties.getValidationError(Validations.campaignFormMetaValidationUnexpectedError, e.getMessage()));
			}
		}
	}

	@Override
	public void validateAndClean(CampaignFormMetaDto campaignFormMetaDto) throws ValidationRuntimeException {
		if (CollectionUtils.isEmpty(campaignFormMetaDto.getCampaignFormElements())) {
			return;
		}

		// Throw an exception when the schema definition contains an element without an ID or type
		campaignFormMetaDto.getCampaignFormElements()
			.stream()
			.filter(e -> StringUtils.isBlank(e.getId()) || StringUtils.isBlank(e.getType()))
			.findFirst()
			.ifPresent(e -> {
				if (StringUtils.isBlank(e.getId())) {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.campaignFormElementIdRequired));
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.campaignFormElementTypeRequired, e.getId()));
				}
			});

		// Throw an exception when the schema definition contains the same ID more than once
		campaignFormMetaDto.getCampaignFormElements().forEach(e -> {
			if (Collections.frequency(campaignFormMetaDto.getCampaignFormElements(), e) > 1) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.campaignFormElementDuplicateId, e.getId()));
			}
		});

		// Throw an error if any translation does not have a language code or contains an element without an ID or caption
		if (CollectionUtils.isNotEmpty(campaignFormMetaDto.getCampaignFormTranslations())) {
			campaignFormMetaDto.getCampaignFormTranslations().forEach(cft -> {
				if (StringUtils.isBlank(cft.getLanguageCode())) {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.campaignFormTranslationLanguageCodeRequired));
				}

				cft.getTranslations()
					.stream()
					.filter(t -> StringUtils.isBlank(t.getElementId()) || StringUtils.isBlank(t.getCaption()))
					.findFirst()
					.ifPresent(e -> {
						if (StringUtils.isBlank(e.getElementId())) {
							throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.campaignFormTranslationIdRequired));
						} else {
							throw new ValidationRuntimeException(
								I18nProperties
									.getValidationError(Validations.campaignFormTranslationCaptionRequired, e.getElementId(), cft.getLanguageCode()));
						}
					});
			});
		}

		Map<String, String> idsAndTypes = campaignFormMetaDto.getCampaignFormElements()
			.stream()
			.collect(Collectors.toMap(CampaignFormElement::getId, CampaignFormElement::getType));

		for (CampaignFormElement element : campaignFormMetaDto.getCampaignFormElements()) {
			// Clean the element caption from all HTML tags that are not explicitly allowed
			if (StringUtils.isNotBlank(element.getCaption())) {
				Whitelist whitelist = Whitelist.none();
				whitelist.addTags(CampaignFormElement.ALLOWED_HTML_TAGS);
				element.setCaption(HtmlHelper.cleanHtml(element.getCaption(), whitelist));
			}

			// Validate form elements
			validateCampaignFormElementType(element.getId(), element.getType());
			validateCampaignFormElementStyles(element.getId(), element.getStyles());
			if (StringUtils.isNotBlank(element.getDependingOn()) && ArrayUtils.isEmpty(element.getDependingOnValues())) {
				throw new ValidationRuntimeException(
					I18nProperties.getValidationError(Validations.campaignFormDependingOnValuesMissing, element.getId()));
			}
			validateCampaignFormMetaDependency(element.getId(), element.getDependingOn(), element.getDependingOnValues(), idsAndTypes);
		}

		// Validate element IDs used in translations and clean HTML used in translation captions
		if (CollectionUtils.isNotEmpty(campaignFormMetaDto.getCampaignFormTranslations())) {
			for (CampaignFormTranslations translations : campaignFormMetaDto.getCampaignFormTranslations()) {
				translations.getTranslations().forEach(e -> {
					if (idsAndTypes.get(e.getElementId()) == null) {
						throw new ValidationRuntimeException(
							I18nProperties
								.getValidationError(Validations.campaignFormTranslationIdInvalid, e.getElementId(), translations.getLanguageCode()));
					}

					if (StringUtils.isNotBlank(e.getCaption())) {
						Whitelist whitelist = Whitelist.none();
						whitelist.addTags(CampaignFormElement.ALLOWED_HTML_TAGS);
						e.setCaption(HtmlHelper.cleanHtml(e.getCaption(), whitelist));
					}
				});
			}
		}
	}

	private void validateCampaignFormElementType(String id, String type) throws ValidationRuntimeException {
		if (!StringUtils.equalsAny(type, CampaignFormElement.VALID_TYPES)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.campaignFormUnsupportedType, type, id));
		}
	}

	private void validateCampaignFormElementStyles(String id, String[] styles) throws ValidationRuntimeException {
		if (ArrayUtils.isEmpty(styles)) {
			return;
		}

		for (String style : styles) {
			if (!StringUtils.equalsAny(style, CampaignFormElement.VALID_STYLES)) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.campaignFormUnsupportedStyle, style, id));
			}
		}
	}

	private void validateCampaignFormMetaDependency(String id, String dependingOn, String[] dependingOnValues, Map<String, String> otherElements)
		throws ValidationRuntimeException {
		if (StringUtils.isBlank(dependingOn)) {
			return;
		}

		// Schema must contain an element with an ID matching the dependingOn attribute
		if (!otherElements.containsKey(dependingOn)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.campaignFormDependingOnNotFound, dependingOn, id));
		}

		// The element referenced by the dependingOn attribute must be of a type that is compatible with the dependingOnValues.
		for (String dependingOnValue : dependingOnValues) {
			if (!isValueValidForType(otherElements.get(dependingOn), dependingOnValue)) {
				throw new ValidationRuntimeException(
					I18nProperties.getValidationError(
						Validations.campaignFormUnsupportedDependingOnValue,
						dependingOnValue,
						id,
						otherElements.get(dependingOn),
						dependingOn));
			}
		}
	}

	private boolean isValueValidForType(String type, String value) {
		if (type.equals(CampaignFormElementType.NUMBER.toString())) {
			try {
				Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return false;
			}
		}

		if (type.equals(CampaignFormElementType.YES_NO.toString())) {
			return StringUtils.equalsAnyIgnoreCase(value, CampaignFormElementType.YES_NO.getAllowedValues());
		}

		return true;
	}

	public static CampaignFormMetaReferenceDto toReferenceDto(CampaignFormMeta entity) {
		if (entity == null) {
			return null;
		}

		return new CampaignFormMetaReferenceDto(entity.getUuid(), entity.toString());
	}

	@LocalBean
	@Stateless
	public static class CampaignFormMetaFacadeEjbLocal extends CampaignFormMetaFacadeEjb {
	}

}
