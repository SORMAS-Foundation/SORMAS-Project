package de.symeda.sormas.ui.campaign.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignFormDataSelectionField;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportErrorException;
import de.symeda.sormas.ui.importer.ImportLineResult;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CampaignFormDataImporter extends DataImporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignFormDataImporter.class);

	private final String campaignFormMetaUuid;
	private final CampaignReferenceDto campaignReferenceDto;
	private final UserFacade userFacade;
	private UI currentUI;

	public CampaignFormDataImporter(
		File inputFile,
		boolean hasEntityClassRow,
		UserDto currentUser,
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
		throws IOException, InterruptedException {

		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}
		CampaignFormDataDto campaignFormData = CampaignFormDataDto.build();
		campaignFormData.setCreatingUser(userFacade.getCurrentUserAsReference());

		try {
			insertImportRowIntoData(campaignFormData, values, entityProperties);
			campaignFormData.setCampaign(campaignReferenceDto);

			CampaignFormDataDto existingData = FacadeProvider.getCampaignFormDataFacade()
				.getExistingData(
					new CampaignFormDataCriteria().campaign(campaignFormData.getCampaign())
						.campaignFormMeta(campaignFormData.getCampaignFormMeta())
						.community(campaignFormData.getCommunity())
						.formDate(campaignFormData.getFormDate()));

			if (existingData != null) {
				final CampaignFormDataImportLock lock = new CampaignFormDataImportLock();
				synchronized (lock) {

					handleDetectedDuplicate(campaignFormData, existingData, lock);

					try {
						if (!lock.wasNotified) {
							lock.wait();
						}
					} catch (InterruptedException e) {
						logger.error("InterruptedException when trying to perform LOCK.wait() in campaign form data import: " + e.getMessage());
						throw e;
					}

					if (lock.similarityChoice == CampaignFormDataSimilarityChoice.CANCEL) {
						cancelImport();
						return ImportLineResult.SKIPPED;
					} else if (lock.similarityChoice == CampaignFormDataSimilarityChoice.SKIP) {
						return ImportLineResult.SKIPPED;
					} else {
						FacadeProvider.getCampaignFormDataFacade().overwriteCampaignFormData(existingData, campaignFormData);
					}
				}
			} else {
				FacadeProvider.getCampaignFormDataFacade().saveCampaignFormData(campaignFormData);
			}
		} catch (ImportErrorException | InvalidColumnException | ValidationRuntimeException e) {
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
			return Arrays.stream(CampaignFormElementType.YES_NO.getAllowedValues())
				.map(String::toLowerCase)
				.anyMatch(v -> v.equals(entry.getValue().toString().toLowerCase()));
		}
		return true;
	}

	private void insertImportRowIntoData(CampaignFormDataDto campaignFormData, String[] entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {

		CampaignFormMetaDto campaignMetaDto = FacadeProvider.getCampaignFormMetaFacade().getCampaignFormMetaByUuid(campaignFormMetaUuid);
		campaignFormData.setCampaignFormMeta(new CampaignFormMetaReferenceDto(campaignFormMetaUuid, campaignMetaDto.getFormName()));
		List<String> formDataDtoFields = Stream.of(campaignFormData.getClass().getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
		for (int i = 0; i < entry.length; i++) {
			final String propertyPath = entryHeaderPath[i];
			if (formDataDtoFields.contains(propertyPath)) {
				try {
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyPath, campaignFormData.getClass());
					Class<?> propertyType = propertyDescriptor.getPropertyType();
					if (!executeDefaultInvokings(
						propertyDescriptor,
						campaignFormData,
						entry[i],
						new String[] {
							propertyPath })) {
						final UserDto currentUserDto = userFacade.getByUuid(currentUser.getUuid());
						final JurisdictionLevel jurisdictionLevel = UserRole.getJurisdictionLevel(currentUserDto.getUserRoles());

						if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
							if (jurisdictionLevel == JurisdictionLevel.DISTRICT && !currentUserDto.getDistrict().getCaption().equals(entry[i])) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(Validations.importEntryDistrictNotInUsersJurisdiction, entry[i], propertyPath));
							}
							List<DistrictReferenceDto> district =
								FacadeProvider.getDistrictFacade().getByName(entry[i], campaignFormData.getRegion(), true);
							if (district.isEmpty()) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrRegion, entry[i], propertyPath));
							} else if (district.size() > 1) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(Validations.importDistrictNotUnique, entry[i], propertyPath));
							} else {
								propertyDescriptor.getWriteMethod().invoke(campaignFormData, district.get(0));
							}
						} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
							if (jurisdictionLevel == JurisdictionLevel.COMMUNITY && !currentUserDto.getCommunity().getCaption().equals(entry[i])) {
								throw new ImportErrorException(
									I18nProperties
										.getValidationError(Validations.importEntryCommunityNotInUsersJurisdiction, entry[i], propertyPath));
							}
							List<CommunityReferenceDto> community =
								FacadeProvider.getCommunityFacade().getByName(entry[i], campaignFormData.getDistrict(), true);
							if (community.isEmpty()) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrDistrict, entry[i], propertyPath));
							} else if (community.size() > 1) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(Validations.importCommunityNotUnique, entry[i], propertyPath));
							} else {
								propertyDescriptor.getWriteMethod().invoke(campaignFormData, community.get(0));
							}
						}
					}
				} catch (InvocationTargetException | IllegalAccessException e) {
					throw new ImportErrorException(I18nProperties.getValidationError(Validations.importErrorInColumn, propertyPath));
				} catch (IntrospectionException e) {
					// skip unknown fields
				} catch (ImportErrorException e) {
					throw e;
				} catch (Exception e) {
					LOGGER.error("Unexpected error when trying to import campaign form data: " + e.getMessage(), e);
					throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
				}
			} else {
				CampaignFormDataEntry formEntry = new CampaignFormDataEntry(propertyPath, entry[i]);

				Optional<CampaignFormElement> existingFormElement =
					campaignMetaDto.getCampaignFormElements().stream().filter(e -> e.getId().equals(formEntry.getId())).findFirst();
				if (!existingFormElement.isPresent()) {
					// skip unknown fields
					continue;
				} else if (Objects.nonNull(formEntry.getValue())
					&& StringUtils.isNotBlank(formEntry.getValue().toString())
					&& !isEntryValid(existingFormElement.get(), formEntry)) {
					throw new ImportErrorException(I18nProperties.getValidationError(Validations.importWrongDataTypeError, entry[i], propertyPath));
				}

				if (formEntry.getValue() == null || StringUtils.isBlank(formEntry.getValue().toString())) {
					continue;
				}

				// Convert yes/no values to true/false
				if (CampaignFormElementType.YES_NO.toString().equals(existingFormElement.get().getType())) {
					String value = formEntry.getValue().toString();
					if ("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
						formEntry.setValue(true);
					} else if ("no".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
						formEntry.setValue(false);
					}
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

	private void handleDetectedDuplicate(CampaignFormDataDto newData, CampaignFormDataDto existingData, CampaignFormDataImportLock lock) {

		Window popupWindow = VaadinUiUtil.createPopupWindow();

		currentUI.accessSynchronously(() -> {
			Runnable cancelCallback = () -> {
				synchronized (lock) {
					lock.setSimilarityChoice(CampaignFormDataSimilarityChoice.CANCEL);
					lock.notify();
					lock.wasNotified = true;
					popupWindow.close();
				}
			};
			Runnable skipCallback = () -> {
				synchronized (lock) {
					lock.setSimilarityChoice(CampaignFormDataSimilarityChoice.SKIP);
					lock.notify();
					lock.wasNotified = true;
					popupWindow.close();
				}
			};
			Runnable overwriteCallback = () -> {
				synchronized (lock) {
					lock.setSimilarityChoice(CampaignFormDataSimilarityChoice.OVERWRITE);
					lock.notify();
					lock.wasNotified = true;
					popupWindow.close();
				}
			};

			CampaignFormDataSelectionField selectionField = new CampaignFormDataSelectionField(
				newData,
				existingData,
				String.format(
					I18nProperties.getString(Strings.infoSkipOrOverrideDuplicateCampaignFormDataImport),
					newData.getCampaign().toString(),
					newData.getCampaignFormMeta().toString()),
				cancelCallback,
				skipCallback,
				overwriteCallback);

			popupWindow.setContent(selectionField);
			popupWindow.setCaption(I18nProperties.getString(Strings.headingCampaignFormDataAlreadyExisting));
			popupWindow.setWidth(960, Sizeable.Unit.PIXELS);

			currentUI.addWindow(popupWindow);
		});
	}

	private enum CampaignFormDataSimilarityChoice {

		CANCEL,
		SKIP,
		OVERWRITE;
	}

	private static class CampaignFormDataImportLock {

		protected boolean wasNotified = false;
		protected CampaignFormDataSimilarityChoice similarityChoice;

		public void setSimilarityChoice(CampaignFormDataSimilarityChoice similarityChoice) {
			this.similarityChoice = similarityChoice;
		}
	}
}
