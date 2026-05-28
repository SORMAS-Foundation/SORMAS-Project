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
package de.symeda.sormas.ui.epidata;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldGroup;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldVisibilityContext;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.epidata.ClusterType;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.exposure.InfectionSource;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.ui.ActivityAsCase.ActivityAsCaseField;
import de.symeda.sormas.ui.exposure.ExposuresField;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.components.CustomizableFieldsGroup;
import de.symeda.sormas.ui.utils.components.MultilineLabel;

@SuppressWarnings({
	"java:S110", // suppress sonar too many parents warning
	"java:S2160" // suppress missing equals not relevant for Vaadin components
})
public class EpiDataForm extends AbstractEditForm<EpiDataDto> {

	private static final long serialVersionUID = 1L;

	private static final String LOC_EXPOSURE_INVESTIGATION_HEADING = "locExposureInvestigationHeading";
	private static final String LOC_EXPOSURE_PERIOD_CONSIDER_HEADING = "locExposurePeriodConsiderHeading";
	private static final String LOC_CONCLUSION_HEADING = "locConclusionHeading";
	private static final String LOC_CLUSTER_TYPE_HEADING = "locClusterTypeHeading";
	private static final String LOC_ACTIVITY_AS_CASE_INVESTIGATION_HEADING = "locActivityAsCaseInvestigationHeading";
	private static final String LOC_SOURCE_CASE_CONTACTS_HEADING = "locSourceCaseContactsHeading";
	private static final String LOC_EPI_DATA_FIELDS_HINT = "locEpiDataFieldsHint";
	private static final String LOC_EXP_PERIOD_HEADING = "locExpPeriodHeading";

	private static final String LOC_CUSTOMIZABLE_FIELDS_EXPOSURE_INVESTIGATION = CustomizableFieldGroup.EPIDATA_EXPOSURE_INVESTIGATION.getKey();
	private static final String LOC_CUSTOMIZABLE_FIELDS_ACTIVITY_AS_CASE = CustomizableFieldGroup.EPIDATA_ACTIVITY_AS_CASE.getKey();
	private static final String LOC_CUSTOMIZABLE_FIELDS_CONTACT_WITH_SOURCE_CASE = CustomizableFieldGroup.EPIDATA_CONTACT_WITH_SOURCE_CASE.getKey();
	private static final String EXPOSURE_DATES_LAYOUT =
		fluidRowLocs(3, "EXPOSURE_START_DATE_LABEL", 3, "EXPOSURE_START_DATE_VALUE", 3, "EXPOSURE_END_DATE_LABEL", 3, "EXPOSURE_END_DATE_VALUE");
	private static final String LOC_OTHER_INFORMATION_HEADING = "locOtherInformationHeading";

	private static final List<Disease> CONCLUSION_ALLOWED_DISEASES =
		Collections.unmodifiableList(Arrays.asList(Disease.CRYPTOSPORIDIOSIS, Disease.GIARDIASIS, Disease.MALARIA, Disease.DENGUE));

	//@formatter:off
	private static final String MAIN_HTML_LAYOUT =
			loc(LOC_EXPOSURE_PERIOD_CONSIDER_HEADING) +
			fluidRowLocs("EXP_DATES_LAYOUT") +
			loc(LOC_EXPOSURE_INVESTIGATION_HEADING) +
			fluidRowLocs(6,EpiDataDto.CASE_IMPORTED_STATUS,6,"") +
			fluidRowLocs(6, EpiDataDto.EXPOSURE_INVESTIGATION_FROM_DATE, 6, EpiDataDto.EXPOSURE_INVESTIGATION_TO_DATE) +
			loc(LOC_EXP_PERIOD_HEADING) +
			loc(EpiDataDto.EXPOSURE_DETAILS_KNOWN) +
			loc(EpiDataDto.EXPOSURES) +
			loc(LOC_CUSTOMIZABLE_FIELDS_EXPOSURE_INVESTIGATION) +
			loc(LOC_CONCLUSION_HEADING) +
			fluidRowLocs(6,EpiDataDto.CASE_IMPORTED_STATUS,6,"") +
			fluidRowLocs(6, EpiDataDto.IMPORTED_CASE, 6, EpiDataDto.COUNTRY)+
			fluidRowLocs(EpiDataDto.MODE_OF_TRANSMISSION, EpiDataDto.MODE_OF_TRANSMISSION_TYPE) +
			fluidRowLocs(EpiDataDto.INFECTION_SOURCE, EpiDataDto.INFECTION_SOURCE_TEXT) +
			fluidRowLocs(EpiDataDto.PLACE_OF_INFECTION, EpiDataDto.RESIDENCE_AT_ONSET) +
			loc(LOC_ACTIVITY_AS_CASE_INVESTIGATION_HEADING) +
			fluidRowLocs(6, EpiDataDto.ACTIVITY_AS_CASE_FROM_DATE, 6, EpiDataDto.ACTIVITY_AS_CASE_TO_DATE) +
			loc(EpiDataDto.ACTIVITY_AS_CASE_DETAILS_KNOWN)+
			loc(EpiDataDto.ACTIVITIES_AS_CASE) +
			loc(LOC_CUSTOMIZABLE_FIELDS_ACTIVITY_AS_CASE) +
			loc(LOC_CLUSTER_TYPE_HEADING)+
			fluidRowLocs(3, EpiDataDto.CLUSTER_RELATED,5,EpiDataDto.CLUSTER_TYPE,4,EpiDataDto.CLUSTER_TYPE_TEXT) +
			locCss(VSPACE_TOP_3, LOC_EPI_DATA_FIELDS_HINT) +
			loc(EpiDataDto.HIGH_TRANSMISSION_RISK_AREA) +
			loc(EpiDataDto.LARGE_OUTBREAKS_AREA) +
			loc(EpiDataDto.AIRPORT_WORKER) +
			loc(EpiDataDto.HEALTHCARE_PROFESSIONAL) +
			loc(EpiDataDto.AREA_INFECTED_ANIMALS);

	private static final String SOURCE_CONTACTS_HTML_LAYOUT =
			locCss(VSPACE_TOP_3, LOC_SOURCE_CASE_CONTACTS_HEADING) +
			loc(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN) +
			loc(LOC_CUSTOMIZABLE_FIELDS_CONTACT_WITH_SOURCE_CASE);

	private static final String OTHER_INFORMATION_HTML_LAYOUT =
			loc(LOC_OTHER_INFORMATION_HEADING) + fluidRowLocs(EpiDataDto.OTHER_DETAILS);
	//@formatter:on

	private final Disease disease;
	private final Class<? extends EntityDto> parentClass;
	private final transient Consumer<Boolean> sourceContactsToggleCallback;
	private final boolean isPseudonymized;
	private final Date symptomOnsetDate;
	private final boolean caseFollowUpEnabled;

	private CustomizableFieldsGroup exposureInvestigationPanel;
	private CustomizableFieldsGroup activityAsCasePanel;
	private CustomizableFieldsGroup contactWithSourceCasePanel;

	public EpiDataForm(
		Disease disease,
		Class<? extends EntityDto> parentClass,
		boolean isPseudonymized,
		boolean inJurisdiction,
		Consumer<Boolean> sourceContactsToggleCallback,
		boolean isEditAllowed,
		Date date,
		List<CustomizableFieldMetadataDto> customizableFieldsMetadata,
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> customizableFieldsValues) {
		super(
			EpiDataDto.class,
			EpiDataDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized),
			isEditAllowed);
		this.disease = disease;
		this.parentClass = parentClass;
		this.sourceContactsToggleCallback = sourceContactsToggleCallback;
		this.isPseudonymized = isPseudonymized;
		this.symptomOnsetDate = date;
		this.caseFollowUpEnabled = disease != null && FacadeProvider.getDiseaseConfigurationFacade().hasFollowUp(disease);
		setCustomizableFieldsMetadata(customizableFieldsMetadata);
		setCustomizableFieldsValues(customizableFieldsValues);
		addFields();
	}

	@Override
	protected void addFields() {
		if (disease == null) {
			return;
		}

		addHeadingsAndInfoTexts();

		exposureInvestigationPanel = new CustomizableFieldsGroup(CustomizableFieldGroup.EPIDATA_EXPOSURE_INVESTIGATION);
		exposureInvestigationPanel.setVisibilityContext(new CustomizableFieldVisibilityContext().withDisease(disease));
		exposureInvestigationPanel.setFieldsMetadata(getCustomizableFieldsMetadata());
		exposureInvestigationPanel.setFieldsValues(getCustomizableFieldsValues());
		exposureInvestigationPanel.updateFieldsDisplay();
		getContent().addComponent(exposureInvestigationPanel, LOC_CUSTOMIZABLE_FIELDS_EXPOSURE_INVESTIGATION);

		NullableOptionGroup ogExposureDetailsKnown = addField(EpiDataDto.EXPOSURE_DETAILS_KNOWN, NullableOptionGroup.class);
		ExposuresField exposuresField = addField(
			EpiDataDto.EXPOSURES,
			new ExposuresField(
				disease,
				FieldVisibilityCheckers.withDisease(disease)
					.add(new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale())),
				UiFieldAccessCheckers.getDefault(false, FacadeProvider.getConfigFacade().getCountryLocale()),
				true));

		exposuresField.setEpiDataParentClass(parentClass);
		exposuresField.setWidthFull();
		exposuresField.setPseudonymized(isPseudonymized);

		if (parentClass == CaseDataDto.class) {
			addActivityAsCaseFields();
			addField(EpiDataDto.ACTIVITY_AS_CASE_FROM_DATE, DateField.class);
			addField(EpiDataDto.ACTIVITY_AS_CASE_TO_DATE, DateField.class);
		}

		activityAsCasePanel = new CustomizableFieldsGroup(CustomizableFieldGroup.EPIDATA_ACTIVITY_AS_CASE);
		activityAsCasePanel.setVisibilityContext(new CustomizableFieldVisibilityContext().withDisease(disease));
		activityAsCasePanel.setFieldsMetadata(getCustomizableFieldsMetadata());
		activityAsCasePanel.setFieldsValues(getCustomizableFieldsValues());
		activityAsCasePanel.updateFieldsDisplay();
		getContent().addComponent(activityAsCasePanel, LOC_CUSTOMIZABLE_FIELDS_ACTIVITY_AS_CASE);

		addField(EpiDataDto.HIGH_TRANSMISSION_RISK_AREA, NullableOptionGroup.class);
		addField(EpiDataDto.LARGE_OUTBREAKS_AREA, NullableOptionGroup.class);
		addField(EpiDataDto.AREA_INFECTED_ANIMALS, NullableOptionGroup.class);
		NullableOptionGroup ogContactWithSourceCaseKnown = addField(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN, NullableOptionGroup.class);

		if (sourceContactsToggleCallback != null) {
			ogContactWithSourceCaseKnown.addValueChangeListener(e -> {
				YesNoUnknown sourceContactsKnown = (YesNoUnknown) FieldHelper.getNullableSourceFieldValue((Field<?>) e.getProperty());
				sourceContactsToggleCallback.accept(YesNoUnknown.YES == sourceContactsKnown);
			});
		}

		addField(EpiDataDto.CASE_IMPORTED_STATUS);
		addField(EpiDataDto.CLUSTER_TYPE);
		addField(EpiDataDto.CLUSTER_RELATED);

		addField(EpiDataDto.MODE_OF_TRANSMISSION);
		addField(EpiDataDto.MODE_OF_TRANSMISSION_TYPE);
		addField(EpiDataDto.INFECTION_SOURCE);
		addField(EpiDataDto.INFECTION_SOURCE_TEXT);
		addField(EpiDataDto.IMPORTED_CASE, NullableOptionGroup.class);
		List<CountryReferenceDto> countries = FacadeProvider.getCountryFacade().getAllActiveAsReference();
		ComboBox country = addInfrastructureField(EpiDataDto.COUNTRY);
		country.addItems(countries);

		addField(EpiDataDto.EXPOSURE_INVESTIGATION_FROM_DATE, DateField.class);
		addField(EpiDataDto.EXPOSURE_INVESTIGATION_TO_DATE, DateField.class);

		includeExposureDates(symptomOnsetDate, disease);
		addField(EpiDataDto.AIRPORT_WORKER, NullableOptionGroup.class);
		addField(EpiDataDto.HEALTHCARE_PROFESSIONAL, NullableOptionGroup.class);
		addField(EpiDataDto.PLACE_OF_INFECTION);
		addField(EpiDataDto.RESIDENCE_AT_ONSET);

		TextField clusterTypeTF = addField(EpiDataDto.CLUSTER_TYPE_TEXT);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), EpiDataDto.CLUSTER_TYPE, EpiDataDto.CLUSTER_RELATED, Collections.singletonList(Boolean.TRUE), true);
		FieldHelper.setVisibleWhen(getField(EpiDataDto.CLUSTER_TYPE), Arrays.asList(clusterTypeTF), Arrays.asList(ClusterType.OTHER), true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.EXPOSURES,
			EpiDataDto.EXPOSURE_DETAILS_KNOWN,
			Collections.singletonList(YesNoUnknown.YES),
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), EpiDataDto.MODE_OF_TRANSMISSION_TYPE, EpiDataDto.MODE_OF_TRANSMISSION, ModeOfTransmission.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.INFECTION_SOURCE_TEXT, EpiDataDto.INFECTION_SOURCE, InfectionSource.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.COUNTRY, EpiDataDto.IMPORTED_CASE, YesNoUnknown.YES, true);

		contactWithSourceCasePanel = new CustomizableFieldsGroup(CustomizableFieldGroup.EPIDATA_CONTACT_WITH_SOURCE_CASE);
		contactWithSourceCasePanel.setVisibilityContext(new CustomizableFieldVisibilityContext().withDisease(disease));
		contactWithSourceCasePanel.setFieldsMetadata(getCustomizableFieldsMetadata());
		contactWithSourceCasePanel.setFieldsValues(getCustomizableFieldsValues());
		contactWithSourceCasePanel.updateFieldsDisplay();
		getContent().addComponent(contactWithSourceCasePanel, LOC_CUSTOMIZABLE_FIELDS_CONTACT_WITH_SOURCE_CASE);

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		exposuresField.addValueChangeListener(e -> ogExposureDetailsKnown.setEnabled(CollectionUtils.isEmpty(exposuresField.getValue())));

		TextArea additionalDetails = addField(EpiDataDto.OTHER_DETAILS, TextArea.class);
		additionalDetails.setRows(6);
		additionalDetails.setDescription(
			I18nProperties.getPrefixDescription(EpiDataDto.I18N_PREFIX, EpiDataDto.OTHER_DETAILS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));
	}

	/**
	 * Include the exposure start and dates when symptomOnsetDate is present.
	 * Disease incubation period is enabled with valid values.
	 *
	 * @param symptomOnsetDate
	 * @param disease
	 */
	private void includeExposureDates(Date symptomOnsetDate, Disease disease) {
		// By default, hiding the exposure period to consider heading,
		// it will be visible only when all the conditions are met to show the exposure start and end dates.
		getContent().getComponent(LOC_EXPOSURE_PERIOD_CONSIDER_HEADING).setVisible(false);
		//  if symptomOnsetDate is null, return;
		if (symptomOnsetDate == null) {
			return;
		}
		DiseaseConfigurationDto diseaseConfigurationDto = FacadeProvider.getDiseaseConfigurationFacade().getDiseaseConfiguration(disease);
		if (diseaseConfigurationDto == null) {
			return;
		}
		if (diseaseConfigurationDto.getIncubationPeriodEnabled() == null || !diseaseConfigurationDto.getIncubationPeriodEnabled()) {
			return;
		}
		if (diseaseConfigurationDto.getMaxIncubationPeriod() == null || diseaseConfigurationDto.getMaxIncubationPeriod() == 0) {
			return;
		}
		if (diseaseConfigurationDto.getMinIncubationPeriod() == null) {
			return;
		}

		CustomLayout exposureDatesLayout = new CustomLayout();
		exposureDatesLayout.setTemplateContents(EXPOSURE_DATES_LAYOUT);
		Label exposureStartLabel = new Label(I18nProperties.getString(Strings.exposureStartDate));
		exposureStartLabel.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		exposureDatesLayout.addComponent(exposureStartLabel, "EXPOSURE_START_DATE_LABEL");

		DateField exposureStartDateValue = new DateField();
		exposureStartDateValue.setValue(DateHelper.subtractDays(symptomOnsetDate, diseaseConfigurationDto.getMaxIncubationPeriod()));
		exposureStartDateValue.setReadOnly(true);
		exposureDatesLayout.addComponent(exposureStartDateValue, "EXPOSURE_START_DATE_VALUE");

		Label exposureEndLabel = new Label(I18nProperties.getString(Strings.exposureEndDate));
		exposureEndLabel.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		exposureDatesLayout.addComponent(exposureEndLabel, "EXPOSURE_END_DATE_LABEL");
		DateField exposureEndDateValue = new DateField();
		exposureEndDateValue.setValue(DateHelper.subtractDays(symptomOnsetDate, diseaseConfigurationDto.getMinIncubationPeriod()));
		exposureEndDateValue.setReadOnly(true);
		exposureDatesLayout.addComponent(exposureEndDateValue, "EXPOSURE_END_DATE_VALUE");

		getContent().addComponent(exposureDatesLayout, "EXP_DATES_LAYOUT");
		getContent().getComponent(LOC_EXPOSURE_PERIOD_CONSIDER_HEADING).setVisible(true);
	}

	private void addActivityAsCaseFields() {

		getContent().addComponent(
			new MultilineLabel(
				h3(I18nProperties.getString(Strings.headingActivityAsCase))
					+ divsCss(VSPACE_3, I18nProperties.getString(Strings.infoActivityAsCaseInvestigation)),
				ContentMode.HTML),
			LOC_ACTIVITY_AS_CASE_INVESTIGATION_HEADING);

		NullableOptionGroup ogActivityAsCaseDetailsKnown = addField(EpiDataDto.ACTIVITY_AS_CASE_DETAILS_KNOWN, NullableOptionGroup.class);
		ActivityAsCaseField activityAsCaseField = addField(EpiDataDto.ACTIVITIES_AS_CASE, ActivityAsCaseField.class);
		activityAsCaseField.setWidthFull();
		activityAsCaseField.setPseudonymized(isPseudonymized);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.ACTIVITIES_AS_CASE,
			EpiDataDto.ACTIVITY_AS_CASE_DETAILS_KNOWN,
			Collections.singletonList(YesNoUnknown.YES),
			true);

		activityAsCaseField
			.addValueChangeListener(e -> ogActivityAsCaseDetailsKnown.setEnabled(CollectionUtils.isEmpty(activityAsCaseField.getValue())));
	}

	private void addHeadingsAndInfoTexts() {
		getContent().addComponent(
			new MultilineLabel(
				h3(I18nProperties.getString(Strings.headingExposureInvestigation))
					+ divsCss(
						VSPACE_3,
						I18nProperties.getString(
							parentClass == ContactDto.class ? Strings.infoExposureInvestigationContacts : Strings.infoExposureInvestigation),
						disease == Disease.GIARDIASIS ? I18nProperties.getString(Strings.giardiaInfoExposureInvestigation) : StringUtils.EMPTY),
				ContentMode.HTML),
			LOC_EXPOSURE_INVESTIGATION_HEADING);

		getContent().addComponent(
			new MultilineLabel(divsCss(VSPACE_3, I18nProperties.getString(Strings.infoEpiDataFieldsHint)), ContentMode.HTML),
			LOC_EPI_DATA_FIELDS_HINT);

		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_LUXEMBOURG) && Disease.MEASLES == disease) {
			getContent().addComponent(
				new MultilineLabel(h3(I18nProperties.getString(Strings.headingClusterType)) + divsCss(VSPACE_3), ContentMode.HTML),
				LOC_CLUSTER_TYPE_HEADING);
		}

		getContent().addComponent(
			new MultilineLabel(h3(I18nProperties.getString(Strings.headingExposurePeriodConsider)) + divsCss(VSPACE_3), ContentMode.HTML),
			LOC_EXPOSURE_PERIOD_CONSIDER_HEADING);

		// Conclusion heading should be visible for all countries Giardiasis & Cryptosporidiosis specific fields
		getContent().addComponent(
			new MultilineLabel(h3(I18nProperties.getString(Strings.headingEpiConclusion)) + divsCss(VSPACE_3), ContentMode.HTML),
			LOC_CONCLUSION_HEADING);
		getContent().getComponent(LOC_CONCLUSION_HEADING).setVisible(CONCLUSION_ALLOWED_DISEASES.contains(disease));

		getContent().addComponent(
			new MultilineLabel(
				h3(I18nProperties.getString(Strings.headingEpiDataSourceCaseContacts))
					+ divsCss(VSPACE_3, I18nProperties.getString(Strings.infoEpiDataSourceCaseContacts)),
				ContentMode.HTML),
			LOC_SOURCE_CASE_CONTACTS_HEADING);

		Label otherInformationLabel = new Label(I18nProperties.getString(Strings.headingEpiDataOtherInformation));
		otherInformationLabel.addStyleName(H3);
		getContent().addComponent(otherInformationLabel, LOC_OTHER_INFORMATION_HEADING);
	}

	/**
	 * Collects the current values from all customizable field panels.
	 *
	 * @return map of metadata DTO to value DTO, suitable for
	 *         {@link de.symeda.sormas.api.customizablefield.CustomizableFieldValueFacade#saveEntityCustomFields}
	 */
	public Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> collectCurrentFieldValues() {
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> result = new HashMap<>();
		for (CustomizableFieldsGroup panel : new CustomizableFieldsGroup[] {
			exposureInvestigationPanel,
			activityAsCasePanel,
			contactWithSourceCasePanel }) {
			if (panel != null) {
				panel.getFieldsValues().forEach((metadata, valueDto) -> {
					if (valueDto != null) {
						result.put(metadata, valueDto);
					}
				});
			}
		}
		return result;
	}

	/**
	 * Registers a listener that fires whenever any customizable field in any of this form's
	 * groups changes its value. Used by the controller to drive
	 * {@link de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent#setDirty(boolean)}.
	 *
	 * @param listener
	 *            the listener to register on all panels
	 */
	public void addCustomizableFieldValueChangeListener(com.vaadin.data.HasValue.ValueChangeListener<?> listener) {
		for (CustomizableFieldsGroup panel : new CustomizableFieldsGroup[] {
			exposureInvestigationPanel,
			activityAsCasePanel,
			contactWithSourceCasePanel }) {
			if (panel != null) {
				panel.addValueChangeListener(listener);
			}
		}
	}

	/**
	 * Resets all customizable field panels to the original values that were loaded when the form
	 * was opened. Call this from a
	 * {@link de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener} to keep
	 * customizable fields in sync with the regular field discard.
	 */
	public void resetCustomizableFieldValues() {
		for (CustomizableFieldsGroup panel : new CustomizableFieldsGroup[] {
			exposureInvestigationPanel,
			activityAsCasePanel,
			contactWithSourceCasePanel }) {
			if (panel != null) {
				panel.setFieldsValues(getCustomizableFieldsValues());
				panel.updateFieldsDisplay();
			}
		}
	}

	public void disableContactWithSourceCaseKnownField() {
		setEnabled(false, EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN);
	}

	public void setGetSourceContactsCallback(Supplier<List<ContactReferenceDto>> callback) {
		((ExposuresField) getField(EpiDataDto.EXPOSURES)).setGetSourceContactsCallback(callback);
	}

	public Map<String, Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto>> collectExposureCustomizableFieldValues() {
		return ((ExposuresField) getField(EpiDataDto.EXPOSURES)).collectCustomizableFieldValues();
	}

	@Override
	protected String createHtmlLayout() {
		// Source contacts YESNOUnknown field should be visible only the diseases which are follow-up enabled,
		// else normal layout without source contacts fields should be visible.
		String layout;
		if (parentClass == CaseDataDto.class && caseFollowUpEnabled) {
			layout = MAIN_HTML_LAYOUT + SOURCE_CONTACTS_HTML_LAYOUT;
		} else {
			layout = MAIN_HTML_LAYOUT;
		}
		return layout + OTHER_INFORMATION_HTML_LAYOUT;
	}
}
