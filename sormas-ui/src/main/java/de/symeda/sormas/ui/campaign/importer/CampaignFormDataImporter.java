package de.symeda.sormas.ui.campaign.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportErrorException;
import de.symeda.sormas.ui.importer.ImportLineResult;

public class CampaignFormDataImporter extends DataImporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignFormDataImporter.class);

	private UI currentUI;
	private String campaignFormMetaUUID;
	private CampaignReferenceDto campaignReferenceDto;

	private UserFacade userFacade;

	public CampaignFormDataImporter(
		File inputFile,
		boolean hasEntityClassRow,
		UserReferenceDto currentUser,
		String campaignUUID,
		CampaignReferenceDto campaignFormDataDto) {
		super(inputFile, hasEntityClassRow, currentUser);
		this.campaignFormMetaUUID = campaignUUID;
		this.campaignReferenceDto = campaignFormDataDto;

		this.userFacade = FacadeProvider.getUserFacade();
	}

	@Override
	public void startImport(Consumer<StreamResource> addErrorReportToLayoutCallback, UI currentUI, boolean duplicatesPossible)
		throws IOException, CsvValidationException {

		this.currentUI = currentUI;
		super.startImport(addErrorReportToLayoutCallback, currentUI, duplicatesPossible);
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean firstLine)
		throws IOException {
		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}
		CampaignFormDataDto campaignFormData = CampaignFormDataDto.build();

		try {
			campaignFormData = insertColumnEntryIntoData(campaignFormData, values, entityProperties);
			CampaignFormMetaDto campaginMetaDto = FacadeProvider.getCampaignFormMetaFacade().getCampaignFormMetaByUuid(campaignFormMetaUUID);
			campaignFormData.setCampaign(campaignReferenceDto);
			campaignFormData.setCampaignFormMeta(new CampaignFormMetaReferenceDto(campaignFormMetaUUID, campaginMetaDto.getFormName()));
			Map<String, String> invalidEntries = validateFormValues(campaginMetaDto, campaignFormData);
			if (!invalidEntries.isEmpty()) {
				for (String e : invalidEntries.keySet()) {
					writeImportError(values, I18nProperties.getValidationError(Validations.importWrongDataTypeError, invalidEntries.get(e), e));
				}
				return ImportLineResult.ERROR;
			} else {
				FacadeProvider.getCampaignFormDataFacade().saveCampaignFormData(campaignFormData);
			}
		} catch (ImportErrorException e) {
			writeImportError(values, e.getMessage());
			return ImportLineResult.ERROR;
		}

		return ImportLineResult.SUCCESS;
	}

	private Map<String, String> validateFormValues(CampaignFormMetaDto campaginMetaDto, CampaignFormDataDto campaignFormData) {
		Map<String, String> wrongEntries = new LinkedHashMap<>();
		List<CampaignFormElement> formElements = campaginMetaDto.getCampaignFormElements();
		Optional<CampaignFormElement> formElementOptional;
		for (CampaignFormDataEntry formDataEntry : campaignFormData.getFormValues()) {
			formElementOptional = formElements.stream().filter(formElement -> formElement.getId().equals(formDataEntry.getId())).findFirst();
			if (formElementOptional.isPresent()) {
				if (!isEntryValid(formElementOptional.get(), formDataEntry)) {
					wrongEntries.put(formElementOptional.get().getId(), formDataEntry.getValue().toString());

				}
			}
		}
		return wrongEntries;
	}

	private boolean isEntryValid(CampaignFormElement definition, CampaignFormDataEntry entry) {
		if (definition.getType().equalsIgnoreCase(CampaignFormElementType.NUMBER.toString())) {
			if (!NumberUtils.isParsable(entry.getValue().toString())) {
				return false;
			}
		} else if (definition.getType().equalsIgnoreCase(CampaignFormElementType.TEXT.toString())) {
			if (entry.getValue().toString().matches("[0-9]+")) {
				return false;
			}
		} else if (definition.getType().equalsIgnoreCase(CampaignFormElementType.YES_NO.toString())) {
			if (!Arrays.asList(CampaignFormElementType.YES_NO.getAllowedValues()).contains(entry.getValue().toString())) {
				return false;
			}
		}
		return true;
	}

	private CampaignFormDataDto insertColumnEntryIntoData(CampaignFormDataDto campaignFormData, String[] entry, String[] entryHeaderPath)
		throws ImportErrorException {
		CampaignFormDataDto currentElement = campaignFormData;
		for (int i = 0; i < entry.length; i++) {
			if (Objects.isNull(currentElement.getCommunity())) {
				try {
					PropertyDescriptor propertyDescriptor = null;
					propertyDescriptor = new PropertyDescriptor(entryHeaderPath[i], currentElement.getClass());
					Class<?> propertyType = propertyDescriptor.getPropertyType();
					if (!executeDefaultInvokings(propertyDescriptor, currentElement, entry[i], entryHeaderPath)) {
						final UserDto currentUserDto = userFacade.getByUuid(currentUser.getUuid());
						final JurisdictionLevel jurisdictionLevel = UserRole.getJurisdictionLevel(currentUserDto.getUserRoles());
						/*
						 * if (propertyType.isAssignableFrom(CampaignReferenceDto.class)) {
						 * CampaignDto campaign = FacadeProvider.getCampaignFacade().getByUuid(entry[i]);
						 * if (Objects.nonNull(campaign)) {
						 * propertyDescriptor.getWriteMethod().invoke(currentElement, new CampaignReferenceDto(campaign.getUuid(),
						 * campaign.getName()));
						 * }
						 * } else
						 */
						if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
							if (jurisdictionLevel == JurisdictionLevel.DISTRICT && !currentUserDto.getDistrict().getCaption().equals(entry[i])) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDistrictNotInJurisdiction,
										entry,
										buildEntityProperty(entryHeaderPath)));
							}
							List<DistrictReferenceDto> district =
								FacadeProvider.getDistrictFacade().getByName(entry[i], currentElement.getRegion(), true);
							if (district.isEmpty()) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDoesNotExistDbOrRegion,
										entry,
										buildEntityProperty(entryHeaderPath)));
							} else if (district.size() > 1) {
								throw new ImportErrorException(
									I18nProperties
										.getValidationError(Validations.importDistrictNotUnique, entry, buildEntityProperty(entryHeaderPath)));
							} else {
								propertyDescriptor.getWriteMethod().invoke(currentElement, district.get(0));
							}
						} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
							if (jurisdictionLevel == JurisdictionLevel.COMMUNITY && !currentUserDto.getCommunity().getCaption().equals(entry[i])) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryCommunityNotInJurisdiction,
										entry,
										buildEntityProperty(entryHeaderPath)));
							}
							List<CommunityReferenceDto> community =
								FacadeProvider.getCommunityFacade().getByName(entry[i], currentElement.getDistrict(), true);
							if (community.isEmpty()) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDoesNotExistDbOrDistrict,
										entry,
										buildEntityProperty(entryHeaderPath)));
							} else if (community.size() > 1) {
								throw new ImportErrorException(
									I18nProperties
										.getValidationError(Validations.importCommunityNotUnique, entry, buildEntityProperty(entryHeaderPath)));
							} else {
								propertyDescriptor.getWriteMethod().invoke(currentElement, community.get(0));
							}
						}
					}
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (IntrospectionException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				CampaignFormDataEntry formEntry = new CampaignFormDataEntry(entryHeaderPath[i], entry[i]);
				if (Objects.nonNull(currentElement.getFormValues())) {
					List<CampaignFormDataEntry> currentElementFormValues = currentElement.getFormValues();
					currentElementFormValues.add(formEntry);
					currentElement.setFormValues(currentElementFormValues);
				} else {
					List formValues = new LinkedList();
					formValues.add(formEntry);
					currentElement.setFormValues(formValues);
				}
			}
		}
		return currentElement;
	}

	@Override
	protected boolean executeDefaultInvokings(PropertyDescriptor pd, Object element, String entry, String[] entryHeaderPath)
		throws InvocationTargetException, IllegalAccessException, ParseException, ImportErrorException {
		final boolean returnBoolean = super.executeDefaultInvokings(pd, element, entry, entryHeaderPath);
		final Class<?> propertyType = pd.getPropertyType();
		if (propertyType.isAssignableFrom(RegionReferenceDto.class)) {
			final UserDto currentUserDto = userFacade.getByUuid(currentUser.getUuid());
			final JurisdictionLevel jurisdictionLevel = UserRole.getJurisdictionLevel(currentUserDto.getUserRoles());
			if (jurisdictionLevel == JurisdictionLevel.REGION && !currentUserDto.getRegion().getCaption().equals(entry)) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryRegionNotInJurisdiction, entry, buildEntityProperty(entryHeaderPath)));
			}
		}
		return returnBoolean;
	}
}
