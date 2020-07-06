package de.symeda.sormas.backend.campaign.form;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormDto;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.campaign.form.CampaignFormFacade;
import de.symeda.sormas.api.campaign.form.CampaignFormReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless(name = "CampaignFormFacade")
public class CampaignFormFacadeEjb implements CampaignFormFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CampaignFormService service;

	public CampaignForm fromDto(@NotNull CampaignFormDto source) {
		CampaignForm target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new CampaignForm();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setFormId(source.getFormId());
		target.setLanguageCode(source.getLanguageCode());
		target.setCampaignFormElementsList(source.getCampaignFormElements());
		target.setCampaignFormTranslationsList(source.getCampaignFormTranslations());

		return target;
	}

	public CampaignFormDto toDto(CampaignForm source) {
		if (source == null) {
			return null;
		}

		CampaignFormDto target = new CampaignFormDto();
		DtoHelper.fillDto(target, source);

		target.setFormId(source.getFormId());
		target.setLanguageCode(source.getLanguageCode());
		target.setCampaignFormElements(source.getCampaignFormElementsList());
		target.setCampaignFormTranslations(source.getCampaignFormTranslationsList());

		return target;
	}

	@Override
	public CampaignFormDto saveCampaignForm(CampaignFormDto campaignFormDto) throws ValidationRuntimeException {
		validateAndClean(campaignFormDto);

		CampaignForm campaignForm = fromDto(campaignFormDto);
		service.ensurePersisted(campaignForm);
		return toDto(campaignForm);
	}

	@Override
	public CampaignFormDto buildCampaignFormFromJson(String formId, String languageCode, String schemaDefinitionJson, String translationsJson)
		throws IOException {
		CampaignFormDto campaignForm = new CampaignFormDto();
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
	public List<CampaignFormReferenceDto> getAllCampaignFormsAsReferences() {
		return service.getAll()
			.stream()
			.map(CampaignFormFacadeEjb::toReferenceDto)
			.sorted(Comparator.comparing(ReferenceDto::toString))
			.collect(Collectors.toList());
	}

	@Override
	public CampaignFormDto getCampaignFormByUuid(String campaignFormUuid) {
		return toDto(service.getByUuid(campaignFormUuid));
	}

	@Override
	public void validateAndClean(CampaignFormDto campaignFormDto) throws ValidationRuntimeException {
		if (CollectionUtils.isEmpty(campaignFormDto.getCampaignFormElements())) {
			return;
		}

		// Throw an exception when the schema definition contains an element without an ID or type
		campaignFormDto.getCampaignFormElements()
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
		campaignFormDto.getCampaignFormElements().forEach(e -> {
			if (Collections.frequency(campaignFormDto.getCampaignFormElements(), e) > 1) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.campaignFormElementDuplicateId, e.getId()));
			}
		});

		// Throw an error if any translation does not have a language code or contains an element without an ID or caption
		if (CollectionUtils.isNotEmpty(campaignFormDto.getCampaignFormTranslations())) {
			campaignFormDto.getCampaignFormTranslations().forEach(cft -> {
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

		Map<String, String> idsAndTypes =
			campaignFormDto.getCampaignFormElements().stream().collect(Collectors.toMap(CampaignFormElement::getId, CampaignFormElement::getType));

		for (CampaignFormElement element : campaignFormDto.getCampaignFormElements()) {
			// Clean the element caption from all HTML tags that are not explicitly allowed
			if (StringUtils.isNotBlank(element.getCaption())) {
				Whitelist whitelist = Whitelist.none();
				whitelist.addTags(CampaignFormElement.ALLOWED_HTML_TAGS);
				element.setCaption(Jsoup.clean(element.getCaption(), whitelist));
			}

			// Validate form elements
			validateCampaignFormElementType(element.getId(), element.getType());
			validateCampaignFormElementStyles(element.getId(), element.getStyles());
			if (StringUtils.isNotBlank(element.getDependingOn()) && ArrayUtils.isEmpty(element.getDependingOnValues())) {
				throw new ValidationRuntimeException(
					I18nProperties.getValidationError(Validations.campaignFormDependingOnValuesMissing, element.getId()));
			}
			validateCampaignFormDependency(element.getId(), element.getDependingOn(), element.getDependingOnValues(), idsAndTypes);
		}

		// Validate element IDs used in translations and clean HTML used in translation captions
		if (CollectionUtils.isNotEmpty(campaignFormDto.getCampaignFormTranslations())) {
			for (CampaignFormTranslations translations : campaignFormDto.getCampaignFormTranslations()) {
				translations.getTranslations().forEach(e -> {
					if (idsAndTypes.get(e.getElementId()) == null) {
						throw new ValidationRuntimeException(
							I18nProperties
								.getValidationError(Validations.campaignFormTranslationIdInvalid, e.getElementId(), translations.getLanguageCode()));
					}

					if (StringUtils.isNotBlank(e.getCaption())) {
						Whitelist whitelist = Whitelist.none();
						whitelist.addTags(CampaignFormElement.ALLOWED_HTML_TAGS);
						e.setCaption(Jsoup.clean(e.getCaption(), whitelist));
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

	private void validateCampaignFormDependency(String id, String dependingOn, String[] dependingOnValues, Map<String, String> otherElements)
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
			return StringUtils.equalsAny(value, CampaignFormElementType.YES_NO.getAllowedValues());
		}

		return true;
	}

	public static CampaignFormReferenceDto toReferenceDto(CampaignForm entity) {
		if (entity == null) {
			return null;
		}

		return new CampaignFormReferenceDto(entity.getUuid(), entity.toString());
	}

	@LocalBean
	@Stateless
	public static class CampaignFormFacadeEjbLocal extends CampaignFormFacadeEjb {
	}

}
