package de.symeda.sormas.ui.campaign.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
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
import de.symeda.sormas.api.importexport.InvalidColumnException;
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

	private final String campaignFormMetaUuid;
	private final CampaignReferenceDto campaignReferenceDto;
	private final UserFacade userFacade;

	public CampaignFormDataImporter(
		File inputFile,
		boolean hasEntityClassRow,
		UserReferenceDto currentUser,
		String campaignFormMetaUuid,
		CampaignReferenceDto campaignReferenceDto) {
		super(inputFile, hasEntityClassRow, currentUser);
		this.campaignFormMetaUuid = campaignFormMetaUuid;
		this.campaignReferenceDto = campaignReferenceDto;

		this.userFacade = FacadeProvider.getUserFacade();
	}

	@Override
	public void startImport(Consumer<StreamResource> addErrorReportToLayoutCallback, UI currentUI, boolean duplicatesPossible)
		throws IOException, CsvValidationException {

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
			insertImportRowIntoData(campaignFormData, values, entityProperties);
			campaignFormData.setCampaign(campaignReferenceDto);
			FacadeProvider.getCampaignFormDataFacade().saveCampaignFormData(campaignFormData);
		} catch (ImportErrorException | InvalidColumnException e) {
			writeImportError(values, e.getLocalizedMessage());
			return ImportLineResult.ERROR;
		}

		return ImportLineResult.SUCCESS;
	}

	private boolean isEntryValid(CampaignFormElement definition, CampaignFormDataEntry entry) {
		if (definition.getType().equalsIgnoreCase(CampaignFormElementType.NUMBER.toString())) {
			return NumberUtils.isParsable(entry.getValue().toString());
		} else if (definition.getType().equalsIgnoreCase(CampaignFormElementType.TEXT.toString())) {
			return !entry.getValue().toString().matches("[0-9]+");
		} else if (definition.getType().equalsIgnoreCase(CampaignFormElementType.YES_NO.toString())) {
			return Arrays.asList(CampaignFormElementType.YES_NO.getAllowedValues()).contains(entry.getValue().toString());
		}
		return true;
	}

	private void insertImportRowIntoData(CampaignFormDataDto campaignFormData, String[] entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {

		CampaignFormMetaDto campaignMetaDto = FacadeProvider.getCampaignFormMetaFacade().getCampaignFormMetaByUuid(campaignFormMetaUuid);
		campaignFormData.setCampaignFormMeta(new CampaignFormMetaReferenceDto(campaignFormMetaUuid, campaignMetaDto.getFormName()));
		for (int i = 0; i < entry.length; i++) {
			// Every element after community is a form value that's part of the JSON definition
			if (Objects.isNull(campaignFormData.getCommunity())) {
				try {
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(entryHeaderPath[i], campaignFormData.getClass());
					Class<?> propertyType = propertyDescriptor.getPropertyType();
					if (!executeDefaultInvokings(propertyDescriptor, campaignFormData, entry[i], entryHeaderPath)) {
						final UserDto currentUserDto = userFacade.getByUuid(currentUser.getUuid());
						final JurisdictionLevel jurisdictionLevel = UserRole.getJurisdictionLevel(currentUserDto.getUserRoles());

						if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
							if (jurisdictionLevel == JurisdictionLevel.DISTRICT && !currentUserDto.getDistrict().getCaption().equals(entry[i])) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDistrictNotInUsersJurisdiction,
										entry,
										buildEntityProperty(entryHeaderPath)));
							}
							List<DistrictReferenceDto> district =
								FacadeProvider.getDistrictFacade().getByName(entry[i], campaignFormData.getRegion(), true);
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
								propertyDescriptor.getWriteMethod().invoke(campaignFormData, district.get(0));
							}
						} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
							if (jurisdictionLevel == JurisdictionLevel.COMMUNITY && !currentUserDto.getCommunity().getCaption().equals(entry[i])) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryCommunityNotInUsersJurisdiction,
										entry,
										buildEntityProperty(entryHeaderPath)));
							}
							List<CommunityReferenceDto> community =
								FacadeProvider.getCommunityFacade().getByName(entry[i], campaignFormData.getDistrict(), true);
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
								propertyDescriptor.getWriteMethod().invoke(campaignFormData, community.get(0));
							}
						}
					}
				} catch (InvocationTargetException | IllegalAccessException e) {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entryHeaderPath)));
				} catch (IntrospectionException e) {
					throw new InvalidColumnException(buildEntityProperty(entryHeaderPath));
				} catch (Exception e) {
					LOGGER.error("Unexpected error when trying to import campaign form data: " + e.getMessage(), e);
					throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
				}
			} else {
				CampaignFormDataEntry formEntry = new CampaignFormDataEntry(entryHeaderPath[i], entry[i]);

				Optional<CampaignFormElement> formElementOptional =
					campaignMetaDto.getCampaignFormElements().stream().filter(e -> e.getId().equals(formEntry.getId())).findFirst();
				if (!formElementOptional.isPresent()) {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.campaignFormDataImportMissingColumnError, entryHeaderPath[i]));
				} else if (Objects.nonNull(formEntry.getValue())
					&& StringUtils.isNotBlank(formEntry.getValue().toString())
					&& !isEntryValid(formElementOptional.get(), formEntry)) {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importWrongDataTypeError, entry[i], entryHeaderPath[i]));
				}

				if (formEntry.getValue() == null || StringUtils.isBlank(formEntry.getValue().toString())) {
					continue;
				}

				if (Objects.nonNull(campaignFormData.getFormValues())) {
					List<CampaignFormDataEntry> currentElementFormValues = campaignFormData.getFormValues();
					currentElementFormValues.add(formEntry);
					campaignFormData.setFormValues(currentElementFormValues);
				} else {
					List<CampaignFormDataEntry> formValues = new LinkedList<>();
					formValues.add(formEntry);
					campaignFormData.setFormValues(formValues);
				}
			}
		}
	}

	@Override
	protected boolean executeDefaultInvokings(PropertyDescriptor pd, Object element, String entry, String[] entryHeaderPath)
		throws InvocationTargetException, IllegalAccessException, ImportErrorException {

		final boolean invokingSuccessful = super.executeDefaultInvokings(pd, element, entry, entryHeaderPath);
		final Class<?> propertyType = pd.getPropertyType();
		if (propertyType.isAssignableFrom(RegionReferenceDto.class)) {
			final UserDto currentUserDto = userFacade.getByUuid(currentUser.getUuid());
			final JurisdictionLevel jurisdictionLevel = UserRole.getJurisdictionLevel(currentUserDto.getUserRoles());
			if (jurisdictionLevel == JurisdictionLevel.REGION && !currentUserDto.getRegion().getCaption().equals(entry)) {
				throw new ImportErrorException(
					I18nProperties
						.getValidationError(Validations.importEntryRegionNotInUsersJurisdiction, entry, buildEntityProperty(entryHeaderPath)));
			}
		}
		return invokingSuccessful;
	}

	@Override
	protected String getErrorReportFileName() {
		return "campaign_data_import_error_report.csv";
	}
}
