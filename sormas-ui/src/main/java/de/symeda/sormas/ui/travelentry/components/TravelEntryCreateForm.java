package de.symeda.sormas.ui.travelentry.components;

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

import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.vaadin.v7.data.util.converter.Converter;
import de.symeda.sormas.api.travelentry.DeaContentEntry;
import de.symeda.sormas.ui.travelentry.DEAFormBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.travelentry.DEAFormBuilder;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;

public class TravelEntryCreateForm extends AbstractEditForm<TravelEntryDto> {

	private static final long serialVersionUID = 2160497736783946091L;

	private static final String RESPONSIBLE_JURISDICTION_HEADING_LOC = "responsibleJurisdictionHeadingLoc";
	private static final String DIFFERENT_POINT_OF_ENTRY_JURISDICTION = "differentPointOfEntryJurisdiction";
	private static final String POINT_OF_ENTRY_HEADING_LOC = "pointOfEntryHeadingLoc";
	private static final String DEA_CONTENT_LOC = "DEAContentLoc";

	//@formatter:off
	private static final String HTML_LAYOUT = fluidRowLocs(TravelEntryDto.REPORT_DATE, TravelEntryDto.EXTERNAL_ID)
		+ fluidRow(
		fluidColumnLoc(6, 0, TravelEntryDto.DISEASE),
		fluidColumnLoc(6, 0, TravelEntryDto.DISEASE_DETAILS),
		fluidColumnLoc(6, 0, TravelEntryDto.DISEASE_VARIANT)) +
		fluidRowLocs(RESPONSIBLE_JURISDICTION_HEADING_LOC)
		+ fluidRowLocs(TravelEntryDto.RESPONSIBLE_REGION, TravelEntryDto.RESPONSIBLE_DISTRICT, TravelEntryDto.RESPONSIBLE_COMMUNITY)
		+ fluidRowLocs(DIFFERENT_POINT_OF_ENTRY_JURISDICTION)
		+ fluidRowLocs(POINT_OF_ENTRY_HEADING_LOC)
		+ fluidRowLocs(TravelEntryDto.REGION, TravelEntryDto.DISTRICT)
		+ fluidRowLocs(TravelEntryDto.POINT_OF_ENTRY, TravelEntryDto.POINT_OF_ENTRY_DETAILS)
			+ loc(DEA_CONTENT_LOC)
		+ fluidRowLocs(PersonDto.FIRST_NAME, PersonDto.LAST_NAME)
		+ fluidRow(fluidRowLocs(PersonDto.BIRTH_DATE_YYYY, PersonDto.BIRTH_DATE_MM, PersonDto.BIRTH_DATE_DD),
		fluidRowLocs(PersonDto.SEX))
		+ fluidRowLocs(PersonDto.NATIONAL_HEALTH_ID, PersonDto.PASSPORT_NUMBER)
		+ fluidRowLocs(PersonDto.PRESENT_CONDITION, SymptomsDto.ONSET_DATE)
		+ fluidRowLocs(PersonDto.PHONE, PersonDto.EMAIL_ADDRESS);
	//@formatter:on

	private ComboBox districtCombo;
	private ComboBox cbPointOfEntry;
	private ComboBox birthDateDay;
	private DEAFormBuilder deaFormBuilder;

	public TravelEntryCreateForm() {
		super(
			TravelEntryDto.class,
			TravelEntryDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()));
		setWidth(720, Unit.PIXELS);
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(TravelEntryDto.REPORT_DATE, DateField.class);

		TextField externalIdField = addField(TravelEntryDto.EXTERNAL_ID, TextField.class);
		style(externalIdField, ERROR_COLOR_PRIMARY);

		ComboBox diseaseField = addDiseaseField(TravelEntryDto.DISEASE, false);
		ComboBox diseaseVariantField = addField(TravelEntryDto.DISEASE_VARIANT, ComboBox.class);
		diseaseVariantField.setNullSelectionAllowed(true);
		diseaseVariantField.setVisible(false);
		addField(TravelEntryDto.DISEASE_DETAILS, TextField.class);

		Label jurisdictionHeadingLabel = new Label(I18nProperties.getString(Strings.headingResponsibleJurisdiction));
		jurisdictionHeadingLabel.addStyleName(H3);
		getContent().addComponent(jurisdictionHeadingLabel, RESPONSIBLE_JURISDICTION_HEADING_LOC);

		ComboBox responsibleRegion = addField(TravelEntryDto.RESPONSIBLE_REGION);
		responsibleRegion.setRequired(true);
		ComboBox responsibleDistrictCombo = addField(TravelEntryDto.RESPONSIBLE_DISTRICT);
		responsibleDistrictCombo.setRequired(true);
		ComboBox responsibleCommunityCombo = addField(TravelEntryDto.RESPONSIBLE_COMMUNITY);
		responsibleCommunityCombo.setNullSelectionAllowed(true);
		responsibleCommunityCombo.addStyleName(SOFT_REQUIRED);

		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegion, responsibleDistrictCombo, responsibleCommunityCombo);

		CheckBox differentPointOfEntryJurisdiction = addCustomField(DIFFERENT_POINT_OF_ENTRY_JURISDICTION, Boolean.class, CheckBox.class);
		differentPointOfEntryJurisdiction.addStyleName(VSPACE_3);

		Label placeOfStayHeadingLabel = new Label(I18nProperties.getCaption(Captions.travelEntryPointOfEntry));
		placeOfStayHeadingLabel.addStyleName(H3);
		getContent().addComponent(placeOfStayHeadingLabel, POINT_OF_ENTRY_HEADING_LOC);

		ComboBox regionCombo = addInfrastructureField(TravelEntryDto.REGION);
		districtCombo = addInfrastructureField(TravelEntryDto.DISTRICT);

		cbPointOfEntry = addInfrastructureField(TravelEntryDto.POINT_OF_ENTRY);
		cbPointOfEntry.setImmediate(true);
		TextField tfPointOfEntryDetails = addField(TravelEntryDto.POINT_OF_ENTRY_DETAILS, TextField.class);
		tfPointOfEntryDetails.setVisible(false);

		addCustomField(PersonDto.FIRST_NAME, String.class, TextField.class);
		addCustomField(PersonDto.LAST_NAME, String.class, TextField.class);
		addCustomField(PersonDto.NATIONAL_HEALTH_ID, String.class, TextField.class);
		addCustomField(PersonDto.PASSPORT_NUMBER, String.class, TextField.class);

		birthDateDay = addCustomField(PersonDto.BIRTH_DATE_DD, Integer.class, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateDay.setNullSelectionAllowed(true);
		birthDateDay.addStyleName(FORCE_CAPTION);
		birthDateDay.setInputPrompt(I18nProperties.getString(Strings.day));
		ComboBox birthDateMonth = addCustomField(PersonDto.BIRTH_DATE_MM, Integer.class, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateMonth.setNullSelectionAllowed(true);
		birthDateMonth.addItems(DateHelper.getMonthsInYear());
		birthDateMonth.setPageLength(12);
		birthDateMonth.addStyleName(FORCE_CAPTION);
		birthDateMonth.setInputPrompt(I18nProperties.getString(Strings.month));
		setItemCaptionsForMonths(birthDateMonth);
		ComboBox birthDateYear = addCustomField(PersonDto.BIRTH_DATE_YYYY, Integer.class, ComboBox.class);
		birthDateYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateYear.setNullSelectionAllowed(true);
		birthDateYear.addItems(DateHelper.getYearsToNow());
		birthDateYear.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID_TOSTRING);
		birthDateYear.setInputPrompt(I18nProperties.getString(Strings.year));
		birthDateDay.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) birthDateYear.getValue(), (Integer) birthDateMonth.getValue(), (Integer) e));
		birthDateMonth.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) birthDateYear.getValue(), (Integer) e, (Integer) birthDateDay.getValue()));
		birthDateYear.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) e, (Integer) birthDateMonth.getValue(), (Integer) birthDateDay.getValue()));

		// Update the list of days according to the selected month and year
		birthDateYear.addValueChangeListener(e -> {
			updateListOfDays((Integer) e.getProperty().getValue(), (Integer) birthDateMonth.getValue());
			birthDateMonth.markAsDirty();
			birthDateDay.markAsDirty();
		});
		birthDateMonth.addValueChangeListener(e -> {
			updateListOfDays((Integer) birthDateYear.getValue(), (Integer) e.getProperty().getValue());
			birthDateYear.markAsDirty();
			birthDateDay.markAsDirty();
		});
		birthDateDay.addValueChangeListener(e -> {
			birthDateYear.markAsDirty();
			birthDateMonth.markAsDirty();
		});

		ComboBox sex = addCustomField(PersonDto.SEX, Sex.class, ComboBox.class);
		sex.setCaption(I18nProperties.getCaption(Captions.Person_sex));
		ComboBox presentCondition = addCustomField(PersonDto.PRESENT_CONDITION, PresentCondition.class, ComboBox.class);
		presentCondition.setCaption(I18nProperties.getCaption(Captions.Person_presentCondition));

		addCustomField(
			SymptomsDto.ONSET_DATE,
			Date.class,
			DateField.class,
			I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));

		TextField phone = addCustomField(PersonDto.PHONE, String.class, TextField.class);
		phone.setCaption(I18nProperties.getCaption(Captions.Person_phone));
		TextField email = addCustomField(PersonDto.EMAIL_ADDRESS, String.class, TextField.class);
		email.setCaption(I18nProperties.getCaption(Captions.Person_emailAddress));

		phone.addValidator(new PhoneNumberValidator(I18nProperties.getValidationError(Validations.validPhoneNumber, phone.getCaption())));
		email.addValidator(new EmailValidator(I18nProperties.getValidationError(Validations.validEmailAddress, email.getCaption())));

		regionCombo.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		regionCombo.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(districtCombo, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		districtCombo.addValueChangeListener(e -> {
			if (differentPointOfEntryJurisdiction.getValue()) {
				DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
				getPointsOfEntryForDistrict(districtDto);
			}
		});

		differentPointOfEntryJurisdiction.addValueChangeListener(v -> {
			if (differentPointOfEntryJurisdiction.getValue()) {
				cbPointOfEntry.removeAllItems();
			} else {
				getPointsOfEntryForDistrict((DistrictReferenceDto) responsibleDistrictCombo.getValue());
			}
		});

		UserProvider currentUserProvider = UserProvider.getCurrent();
		JurisdictionLevel userJurisditionLevel =
			currentUserProvider != null ? UserRole.getJurisdictionLevel(currentUserProvider.getUserRoles()) : JurisdictionLevel.NONE;
		if (userJurisditionLevel == JurisdictionLevel.HEALTH_FACILITY) {
			regionCombo.setReadOnly(true);
			districtCombo.setReadOnly(true);
		}

		// Set initial visibilities & accesses
		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(
			true,
			TravelEntryDto.REPORT_DATE,
			TravelEntryDto.POINT_OF_ENTRY,
			PersonDto.FIRST_NAME,
			PersonDto.LAST_NAME,
			TravelEntryDto.DISEASE,
			PersonDto.SEX);

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

	public PersonDto getPerson() {
		PersonDto person = PersonDto.build();

		person.setFirstName((String) getField(PersonDto.FIRST_NAME).getValue());
		person.setLastName((String) getField(PersonDto.LAST_NAME).getValue());
		person.setBirthdateDD((Integer) getField(PersonDto.BIRTH_DATE_DD).getValue());
		person.setBirthdateMM((Integer) getField(PersonDto.BIRTH_DATE_MM).getValue());
		person.setBirthdateYYYY((Integer) getField(PersonDto.BIRTH_DATE_YYYY).getValue());
		person.setSex((Sex) getField(PersonDto.SEX).getValue());
		person.setPresentCondition((PresentCondition) getField(PersonDto.PRESENT_CONDITION).getValue());

		String phone = (String) getField(PersonDto.PHONE).getValue();
		if (StringUtils.isNotEmpty(phone)) {
			person.setPhone(phone);
		}

		String emailAddress = (String) getField(PersonDto.EMAIL_ADDRESS).getValue();
		if (StringUtils.isNotEmpty(emailAddress)) {
			person.setEmailAddress(emailAddress);
		}

		person.setNationalHealthId((String) getField(PersonDto.NATIONAL_HEALTH_ID).getValue());
		person.setPassportNumber((String) getField(PersonDto.PASSPORT_NUMBER).getValue());

		return person;
	}

	public void setPerson(PersonDto person) {

		if (person != null) {
			((TextField) getField(PersonDto.FIRST_NAME)).setValue(person.getFirstName());
			((TextField) getField(PersonDto.LAST_NAME)).setValue(person.getLastName());
			((ComboBox) getField(PersonDto.BIRTH_DATE_YYYY)).setValue(person.getBirthdateYYYY());
			((ComboBox) getField(PersonDto.BIRTH_DATE_MM)).setValue(person.getBirthdateMM());
			((ComboBox) getField(PersonDto.BIRTH_DATE_DD)).setValue(person.getBirthdateDD());
			((ComboBox) getField(PersonDto.SEX)).setValue(person.getSex());
			((ComboBox) getField(PersonDto.PRESENT_CONDITION)).setValue(person.getPresentCondition());
			((TextField) getField(PersonDto.PHONE)).setValue(person.getPhone());
			((TextField) getField(PersonDto.EMAIL_ADDRESS)).setValue(person.getEmailAddress());
		} else {
			getField(PersonDto.FIRST_NAME).clear();
			getField(PersonDto.LAST_NAME).clear();
			getField(PersonDto.BIRTH_DATE_DD).clear();
			getField(PersonDto.BIRTH_DATE_MM).clear();
			getField(PersonDto.BIRTH_DATE_YYYY).clear();
			getField(PersonDto.SEX).clear();
			getField(PersonDto.PRESENT_CONDITION).clear();
			getField(PersonDto.PHONE).clear();
			getField(PersonDto.EMAIL_ADDRESS).clear();
		}
	}

	public void setPersonalDetailsReadOnlyIfNotEmpty(boolean readOnly) {

		getField(PersonDto.FIRST_NAME).setEnabled(!readOnly);
		getField(PersonDto.LAST_NAME).setEnabled(!readOnly);
		if (getField(PersonDto.SEX).getValue() != null) {
			getField(PersonDto.SEX).setEnabled(!readOnly);
		}
		if (getField(PersonDto.BIRTH_DATE_YYYY).getValue() != null) {
			getField(PersonDto.BIRTH_DATE_YYYY).setEnabled(!readOnly);
		}
		if (getField(PersonDto.BIRTH_DATE_MM).getValue() != null) {
			getField(PersonDto.BIRTH_DATE_MM).setEnabled(!readOnly);
		}
		if (getField(PersonDto.BIRTH_DATE_DD).getValue() != null) {
			getField(PersonDto.BIRTH_DATE_DD).setEnabled(!readOnly);
		}
	}

	public void setDiseaseReadOnly(boolean readOnly) {
		getField(CaseDataDto.DISEASE).setEnabled(!readOnly);
	}

	private void getPointsOfEntryForDistrict(DistrictReferenceDto districtDto) {
		FieldHelper.updateItems(
			cbPointOfEntry,
			districtDto != null ? FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(districtDto.getUuid(), true) : null);
	}

	private void setItemCaptionsForMonths(AbstractSelect months) {

		months.setItemCaption(1, I18nProperties.getEnumCaption(Month.JANUARY));
		months.setItemCaption(2, I18nProperties.getEnumCaption(Month.FEBRUARY));
		months.setItemCaption(3, I18nProperties.getEnumCaption(Month.MARCH));
		months.setItemCaption(4, I18nProperties.getEnumCaption(Month.APRIL));
		months.setItemCaption(5, I18nProperties.getEnumCaption(Month.MAY));
		months.setItemCaption(6, I18nProperties.getEnumCaption(Month.JUNE));
		months.setItemCaption(7, I18nProperties.getEnumCaption(Month.JULY));
		months.setItemCaption(8, I18nProperties.getEnumCaption(Month.AUGUST));
		months.setItemCaption(9, I18nProperties.getEnumCaption(Month.SEPTEMBER));
		months.setItemCaption(10, I18nProperties.getEnumCaption(Month.OCTOBER));
		months.setItemCaption(11, I18nProperties.getEnumCaption(Month.NOVEMBER));
		months.setItemCaption(12, I18nProperties.getEnumCaption(Month.DECEMBER));
	}

	private void updateListOfDays(Integer selectedYear, Integer selectedMonth) {

		Integer currentlySelected = (Integer) birthDateDay.getValue();
		birthDateDay.removeAllItems();
		birthDateDay.addItems(DateHelper.getDaysInMonth(selectedMonth, selectedYear));
		if (birthDateDay.containsId(currentlySelected)) {
			birthDateDay.setValue(currentlySelected);
		}
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

	private void buildDeaContent(TravelEntryDto newFieldValue) {
		final List<DeaContentEntry> deaContent = newFieldValue.getDeaContent();
		if (CollectionUtils.isNotEmpty(deaContent)) {
			deaFormBuilder = new DEAFormBuilder(deaContent, true);
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

	@Override
	public void setValue(TravelEntryDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		buildDeaContent(newFieldValue);
	}
}
