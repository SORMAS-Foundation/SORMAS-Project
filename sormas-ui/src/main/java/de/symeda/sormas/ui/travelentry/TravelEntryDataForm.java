package de.symeda.sormas.ui.travelentry;

import static de.symeda.sormas.ui.utils.CssStyles.ERROR_COLOR_PRIMARY;
import static de.symeda.sormas.ui.utils.CssStyles.FORCE_CAPTION;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.SOFT_REQUIRED;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.style;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.travelentry.DeaContentEntry;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class TravelEntryDataForm extends AbstractEditForm<TravelEntryDto> {

	private static final String RESPONSIBLE_JURISDICTION_HEADING_LOC = "responsibleJurisdictionHeadingLoc";
	private static final String DIFFERENT_POINT_OF_ENTRY_JURISDICTION = "differentPointOfEntryJurisdiction";
	private static final String POINT_OF_ENTRY_HEADING_LOC = "pointOfEntryHeadingLoc";
	private static final String TRAVEL_ENTRY_HEADING_LOC = "travelEntryHeadingLoc";
	private static final String DEA_CONTENT_LOC = "DEAContentLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(TRAVEL_ENTRY_HEADING_LOC) +
					fluidRowLocs(4, TravelEntryDto.UUID, 3, TravelEntryDto.REPORT_DATE, 5, TravelEntryDto.REPORTING_USER) +
					fluidRowLocs(4, TravelEntryDto.EXTERNAL_ID)
			+ fluidRow(
			fluidColumnLoc(6, 0, TravelEntryDto.DISEASE),
			fluidColumnLoc(6, 0, TravelEntryDto.DISEASE_DETAILS),
			fluidColumnLoc(6, 0, TravelEntryDto.DISEASE_VARIANT)) +
					fluidRowLocs(TravelEntryDto.RECOVERED, TravelEntryDto.VACCINATED, TravelEntryDto.TESTED_NEGATIVE) +
			fluidRowLocs(RESPONSIBLE_JURISDICTION_HEADING_LOC)
			+ fluidRowLocs(TravelEntryDto.RESPONSIBLE_REGION, TravelEntryDto.RESPONSIBLE_DISTRICT, TravelEntryDto.RESPONSIBLE_COMMUNITY)
			+ fluidRowLocs(DIFFERENT_POINT_OF_ENTRY_JURISDICTION)
			+ fluidRowLocs(POINT_OF_ENTRY_HEADING_LOC)
			+ fluidRowLocs(TravelEntryDto.REGION, TravelEntryDto.DISTRICT)
			+ fluidRowLocs(TravelEntryDto.POINT_OF_ENTRY, TravelEntryDto.POINT_OF_ENTRY_DETAILS)

			 + loc(DEA_CONTENT_LOC) +

			fluidRowLocs(4, TravelEntryDto.QUARANTINE_HOME_POSSIBLE, 8, TravelEntryDto.QUARANTINE_HOME_POSSIBLE_COMMENT) +
			fluidRowLocs(4, TravelEntryDto.QUARANTINE_HOME_SUPPLY_ENSURED, 8, TravelEntryDto.QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT) +
			fluidRowLocs(6, TravelEntryDto.QUARANTINE, 3, TravelEntryDto.QUARANTINE_FROM, 3, TravelEntryDto.QUARANTINE_TO) +
			fluidRowLocs(TravelEntryDto.QUARANTINE_EXTENDED) +
			fluidRowLocs(TravelEntryDto.QUARANTINE_REDUCED) +
			fluidRowLocs(TravelEntryDto.QUARANTINE_TYPE_DETAILS) +
			fluidRowLocs(TravelEntryDto.QUARANTINE_ORDERED_VERBALLY, TravelEntryDto.QUARANTINE_ORDERED_VERBALLY_DATE) +
			fluidRowLocs(TravelEntryDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT, TravelEntryDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE) +
			fluidRowLocs(TravelEntryDto.QUARANTINE_OFFICIAL_ORDER_SENT, TravelEntryDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE) +
			fluidRowLocs(TravelEntryDto.QUARANTINE_HELP_NEEDED);

	//@formatter:on

	private final String travelEntryUuid;

	private ComboBox districtCombo;
	private ComboBox cbPointOfEntry;

	private Field<?> quarantine;
	private DateField quarantineFrom;
	private DateField quarantineTo;
	private CheckBox quarantineExtended;
	private CheckBox quarantineReduced;
	private CheckBox quarantineOrderedVerbally;
	private CheckBox quarantineOrderedOfficialDocument;
	private CheckBox differentPointOfEntryJurisdiction;
	private DEAFormBuilder deaFormBuilder;

	public TravelEntryDataForm(String travelEntryUuid, boolean isPseudonymized) {
		super(
			TravelEntryDto.class,
			TravelEntryDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			createFieldAccessCheckers(isPseudonymized, true));
		this.travelEntryUuid = travelEntryUuid;
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		Label travelEntryDataHeadingLabel = new Label(I18nProperties.getString(Strings.headingTravelEntryData));
		travelEntryDataHeadingLabel.addStyleName(H3);
		getContent().addComponent(travelEntryDataHeadingLabel, TRAVEL_ENTRY_HEADING_LOC);

		addField(TravelEntryDto.REPORT_DATE, DateField.class);

		addFields(TravelEntryDto.UUID, TravelEntryDto.REPORTING_USER);

		TextField externalIdField = addField(TravelEntryDto.EXTERNAL_ID, TextField.class);
		style(externalIdField, ERROR_COLOR_PRIMARY);

		ComboBox diseaseField = addDiseaseField(TravelEntryDto.DISEASE, false);
		ComboBox diseaseVariantField = addField(TravelEntryDto.DISEASE_VARIANT, ComboBox.class);
		diseaseVariantField.setNullSelectionAllowed(true);
		diseaseVariantField.setVisible(false);
		addField(TravelEntryDto.DISEASE_DETAILS, TextField.class);

		addField(TravelEntryDto.RECOVERED).addStyleNames(CssStyles.FORCE_CAPTION_CHECKBOX);
		addField(TravelEntryDto.VACCINATED).addStyleNames(CssStyles.FORCE_CAPTION_CHECKBOX);
		addField(TravelEntryDto.TESTED_NEGATIVE).addStyleNames(CssStyles.FORCE_CAPTION_CHECKBOX);

		Label jurisdictionHeadingLabel = new Label(I18nProperties.getString(Strings.headingResponsibleJurisdiction));
		jurisdictionHeadingLabel.addStyleName(H3);
		getContent().addComponent(jurisdictionHeadingLabel, RESPONSIBLE_JURISDICTION_HEADING_LOC);

		ComboBox responsibleRegion = addInfrastructureField(TravelEntryDto.RESPONSIBLE_REGION);
		responsibleRegion.setRequired(true);
		ComboBox responsibleDistrictCombo = addInfrastructureField(TravelEntryDto.RESPONSIBLE_DISTRICT);
		responsibleDistrictCombo.setRequired(true);
		ComboBox responsibleCommunityCombo = addInfrastructureField(TravelEntryDto.RESPONSIBLE_COMMUNITY);
		responsibleCommunityCombo.setNullSelectionAllowed(true);
		responsibleCommunityCombo.addStyleName(SOFT_REQUIRED);

		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegion, responsibleDistrictCombo, responsibleCommunityCombo);

		differentPointOfEntryJurisdiction = addCustomField(DIFFERENT_POINT_OF_ENTRY_JURISDICTION, Boolean.class, CheckBox.class);
		differentPointOfEntryJurisdiction.addStyleName(VSPACE_3);

		Label placeOfStayHeadingLabel = new Label(I18nProperties.getCaption(Captions.travelEntryPointOfEntry));
		placeOfStayHeadingLabel.addStyleName(H3);
		getContent().addComponent(placeOfStayHeadingLabel, POINT_OF_ENTRY_HEADING_LOC);

		ComboBox regionCombo = addInfrastructureField(TravelEntryDto.REGION);
		districtCombo = addInfrastructureField(TravelEntryDto.DISTRICT);

		differentPointOfEntryJurisdiction.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto;
			if (differentPointOfEntryJurisdiction.getValue().booleanValue()) {
				districtCombo.setValue(null);
				getPointsOfEntryForDistrict(null);
			} else {
				cbPointOfEntry.setValue(null);
				districtDto = (DistrictReferenceDto) responsibleDistrictCombo.getValue();
				getPointsOfEntryForDistrict(districtDto);
			}
		});

		cbPointOfEntry = addInfrastructureField(TravelEntryDto.POINT_OF_ENTRY);
		cbPointOfEntry.setImmediate(true);
		TextField tfPointOfEntryDetails = addField(TravelEntryDto.POINT_OF_ENTRY_DETAILS, TextField.class);
		tfPointOfEntryDetails.setVisible(false);

		regionCombo.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		regionCombo.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(districtCombo, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		districtCombo.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			if (differentPointOfEntryJurisdiction.getValue()) {
				getPointsOfEntryForDistrict(districtDto);
			} else {
				getPointsOfEntryForDistrict((DistrictReferenceDto) responsibleDistrictCombo.getValue());
			}
		});

		UserProvider currentUserProvider = UserProvider.getCurrent();
		JurisdictionLevel userJurisditionLevel =
			currentUserProvider != null ? UserRole.getJurisdictionLevel(currentUserProvider.getUserRoles()) : JurisdictionLevel.NONE;
		if (userJurisditionLevel == JurisdictionLevel.COMMUNITY
			|| userJurisditionLevel == JurisdictionLevel.HEALTH_FACILITY
			|| userJurisditionLevel == JurisdictionLevel.POINT_OF_ENTRY) {
			regionCombo.setReadOnly(true);
			districtCombo.setReadOnly(true);
		}

		quarantine = addField(TravelEntryDto.QUARANTINE);
		quarantine.addValueChangeListener(e -> onValueChange());
		quarantineFrom = addField(TravelEntryDto.QUARANTINE_FROM, DateField.class);
		quarantineTo = addDateField(TravelEntryDto.QUARANTINE_TO, DateField.class, -1);

		quarantineFrom.addValidator(
			new DateComparisonValidator(
				quarantineFrom,
				quarantineTo,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, quarantineFrom.getCaption(), quarantineTo.getCaption())));
		quarantineTo.addValidator(
			new DateComparisonValidator(
				quarantineTo,
				quarantineFrom,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, quarantineTo.getCaption(), quarantineFrom.getCaption())));

		quarantineOrderedVerbally = addField(TravelEntryDto.QUARANTINE_ORDERED_VERBALLY, CheckBox.class);
		CssStyles.style(quarantineOrderedVerbally, CssStyles.FORCE_CAPTION);
		addField(TravelEntryDto.QUARANTINE_ORDERED_VERBALLY_DATE, DateField.class);
		quarantineOrderedOfficialDocument = addField(TravelEntryDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT, CheckBox.class);
		CssStyles.style(quarantineOrderedOfficialDocument, CssStyles.FORCE_CAPTION);
		addField(TravelEntryDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE, DateField.class);

		CheckBox quarantineOfficialOrderSent = addField(TravelEntryDto.QUARANTINE_OFFICIAL_ORDER_SENT, CheckBox.class);
		CssStyles.style(quarantineOfficialOrderSent, FORCE_CAPTION);
		addField(TravelEntryDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE, DateField.class);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			TravelEntryDto.QUARANTINE_OFFICIAL_ORDER_SENT,
			TravelEntryDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT,
			Collections.singletonList(Boolean.TRUE),
			true);

		quarantineExtended = addField(TravelEntryDto.QUARANTINE_EXTENDED, CheckBox.class);
		quarantineExtended.setEnabled(false);
		quarantineExtended.setVisible(false);
		CssStyles.style(quarantineExtended, CssStyles.FORCE_CAPTION);

		quarantineReduced = addField(TravelEntryDto.QUARANTINE_REDUCED, CheckBox.class);
		quarantineReduced.setEnabled(false);
		quarantineReduced.setVisible(false);
		CssStyles.style(quarantineReduced, CssStyles.FORCE_CAPTION);

		TextField quarantineHelpNeeded = addField(TravelEntryDto.QUARANTINE_HELP_NEEDED, TextField.class);
		quarantineHelpNeeded.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));
		TextField quarantineTypeDetails = addField(TravelEntryDto.QUARANTINE_TYPE_DETAILS, TextField.class);
		quarantineTypeDetails.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));

		addField(TravelEntryDto.QUARANTINE_HOME_POSSIBLE, NullableOptionGroup.class);
		addField(TravelEntryDto.QUARANTINE_HOME_POSSIBLE_COMMENT, TextField.class);
		addField(TravelEntryDto.QUARANTINE_HOME_SUPPLY_ENSURED, NullableOptionGroup.class);
		addField(TravelEntryDto.QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT, TextField.class);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(TravelEntryDto.QUARANTINE_FROM, TravelEntryDto.QUARANTINE_TO, TravelEntryDto.QUARANTINE_HELP_NEEDED),
			TravelEntryDto.QUARANTINE,
			QuarantineType.QUARANTINE_IN_EFFECT,
			true);
		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY) || isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(TravelEntryDto.QUARANTINE_ORDERED_VERBALLY, TravelEntryDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT),
				TravelEntryDto.QUARANTINE,
				QuarantineType.QUARANTINE_IN_EFFECT,
				true);
		}
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			TravelEntryDto.QUARANTINE_HOME_POSSIBLE_COMMENT,
			TravelEntryDto.QUARANTINE_HOME_POSSIBLE,
			Arrays.asList(YesNoUnknown.NO),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			TravelEntryDto.QUARANTINE_HOME_SUPPLY_ENSURED,
			TravelEntryDto.QUARANTINE_HOME_POSSIBLE,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			TravelEntryDto.QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT,
			TravelEntryDto.QUARANTINE_HOME_SUPPLY_ENSURED,
			Arrays.asList(YesNoUnknown.NO),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			TravelEntryDto.QUARANTINE_TYPE_DETAILS,
			TravelEntryDto.QUARANTINE,
			Arrays.asList(QuarantineType.OTHER),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			TravelEntryDto.QUARANTINE_ORDERED_VERBALLY_DATE,
			TravelEntryDto.QUARANTINE_ORDERED_VERBALLY,
			Arrays.asList(Boolean.TRUE),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			TravelEntryDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE,
			TravelEntryDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT,
			Arrays.asList(Boolean.TRUE),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			TravelEntryDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE,
			TravelEntryDto.QUARANTINE_OFFICIAL_ORDER_SENT,
			Collections.singletonList(Boolean.TRUE),
			true);

		// Set initial visibilities & accesses
		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, TravelEntryDto.REPORT_DATE, TravelEntryDto.POINT_OF_ENTRY, TravelEntryDto.DISEASE);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(TravelEntryDto.DISEASE_DETAILS),
			TravelEntryDto.DISEASE,
			Collections.singletonList(Disease.OTHER),
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			TravelEntryDto.DISEASE,
			Collections.singletonList(TravelEntryDto.DISEASE_DETAILS),
			Collections.singletonList(Disease.OTHER));

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(TravelEntryDto.RECOVERED, TravelEntryDto.VACCINATED, TravelEntryDto.TESTED_NEGATIVE),
			TravelEntryDto.DISEASE,
			Disease.DISEASE_LIST,
			true);

		cbPointOfEntry.addValueChangeListener(e -> updatePointOfEntryFields(cbPointOfEntry, tfPointOfEntryDetails));

		FieldHelper.setVisibleWhen(
			differentPointOfEntryJurisdiction,
			Arrays.asList(regionCombo, districtCombo),
			Collections.singletonList(Boolean.TRUE),
			true);

		FieldHelper.setRequiredWhen(
			differentPointOfEntryJurisdiction,
			Arrays.asList(regionCombo, districtCombo),
			Collections.singletonList(Boolean.TRUE),
			false,
			null);

		setReadOnly(true, TravelEntryDto.UUID, TravelEntryDto.REPORTING_USER);

		responsibleDistrictCombo.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			getPointsOfEntryForDistrict(districtDto);
		});

		diseaseField.addValueChangeListener((ValueChangeListener) valueChangeEvent -> {
			Disease disease = (Disease) valueChangeEvent.getProperty().getValue();
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			diseaseVariantField
				.setVisible(disease != null && isVisibleAllowed(TravelEntryDto.DISEASE_VARIANT) && CollectionUtils.isNotEmpty(diseaseVariants));
		});

	}

	private void getPointsOfEntryForDistrict(DistrictReferenceDto districtDto) {
		FieldHelper.updateItems(
			cbPointOfEntry,
			districtDto != null ? FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(districtDto.getUuid(), true) : null);
	}

	private void updatePointOfEntryFields(ComboBox cbPointOfEntry, TextField tfPointOfEntryDetails) {

		if (cbPointOfEntry.getValue() != null) {
			boolean isOtherPointOfEntry = ((PointOfEntryReferenceDto) cbPointOfEntry.getValue()).isOtherPointOfEntry();
			setVisible(isOtherPointOfEntry, TravelEntryDto.POINT_OF_ENTRY_DETAILS);
			setRequired(isOtherPointOfEntry, TravelEntryDto.POINT_OF_ENTRY_DETAILS);
			if (!isOtherPointOfEntry) {
				tfPointOfEntryDetails.clear();
			}
		} else {
			tfPointOfEntryDetails.setVisible(false);
			tfPointOfEntryDetails.setRequired(false);
			tfPointOfEntryDetails.clear();
		}
	}

	private static UiFieldAccessCheckers createFieldAccessCheckers(boolean isPseudonymized, boolean withPersonalAndSensitive) {
		if (withPersonalAndSensitive) {
			return UiFieldAccessCheckers.getDefault(isPseudonymized);
		}

		return UiFieldAccessCheckers.getNoop();
	}

	private void onValueChange() {
		QuarantineType quarantineType = (QuarantineType) quarantine.getValue();
		if (QuarantineType.isQuarantineInEffect(quarantineType)) {
			TravelEntryDto travelEntryDto = FacadeProvider.getTravelEntryFacade().getByUuid(travelEntryUuid);
			if (travelEntryDto.getResultingCase() != null) {
				CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(travelEntryDto.getResultingCase().getUuid());
				if (caze != null) {
					quarantineFrom.setValue(caze.getQuarantineFrom());
					if (caze.getQuarantineTo() == null) {
						boolean caseFollowUpEnabled = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_FOLLOWUP);
						if (caseFollowUpEnabled) {
							quarantineTo.setValue(caze.getFollowUpUntil());
						}
					} else {
						quarantineTo.setValue(caze.getQuarantineTo());
					}
					if (caze.isQuarantineExtended()) {
						quarantineExtended.setValue(true);
						setVisible(true, TravelEntryDto.QUARANTINE_EXTENDED);
					}
					if (caze.isQuarantineReduced()) {
						quarantineReduced.setValue(true);
						setVisible(true, TravelEntryDto.QUARANTINE_REDUCED);
					}
				}
			}
		} else {
			quarantineFrom.clear();
			quarantineTo.clear();
			quarantineExtended.setValue(false);
			quarantineReduced.setValue(false);
			setVisible(false, TravelEntryDto.QUARANTINE_REDUCED, TravelEntryDto.QUARANTINE_EXTENDED);
		}
	}

	@Override
	public void setValue(TravelEntryDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		if ((newFieldValue.getPointOfEntryRegion() != null && !newFieldValue.getPointOfEntryRegion().equals(newFieldValue.getResponsibleRegion()))
			|| newFieldValue.getPointOfEntryDistrict() != null
				&& !newFieldValue.getPointOfEntryDistrict().equals(newFieldValue.getResponsibleDistrict())) {
			differentPointOfEntryJurisdiction.setValue(Boolean.TRUE);
		}
		super.setValue(newFieldValue);
		buildDeaContent(newFieldValue);
	}

	private void buildDeaContent(TravelEntryDto newFieldValue) {
		final List<DeaContentEntry> deaContent = newFieldValue.getDeaContent();
		if (CollectionUtils.isNotEmpty(deaContent)) {
			deaFormBuilder = new DEAFormBuilder(deaContent, false);
			deaFormBuilder.buildForm();
			getContent().addComponent(deaFormBuilder.getLayout(), DEA_CONTENT_LOC);
		}
	}

	@Override
	public TravelEntryDto getValue() {
		TravelEntryDto travelEntryDto = super.getValue();
		if (deaFormBuilder != null) {
			travelEntryDto.setDeaContent(deaFormBuilder.getDeaContentEntries());
		}
		return travelEntryDto;
	}
}
