package de.symeda.sormas.ui.contact.components.linelisting;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.ValidationResult;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;

public class LineListingLayout extends VerticalLayout {

	public static final float DEFAULT_WIDTH = 1696;

	private final CaseSelector caseSelector;
	private final ComboBox<Disease> disease;
	private final ComboBox<RegionReferenceDto> region;
	private final ComboBox<DistrictReferenceDto> district;

	private List<ContactLineLayout> caseLines;

	private Button cancelButton;
	private Button saveButton;

	private final Window window;
	private Consumer<List<ContactLineDto>> saveCallback;

	public LineListingLayout(Window window) {

		this.window = window;

		setSpacing(false);

		VerticalLayout sharedInformationComponent = new VerticalLayout();
		sharedInformationComponent.setMargin(false);
		sharedInformationComponent.setSpacing(false);
		Label sharedInformationLabel = new Label();
		sharedInformationLabel.setValue(I18nProperties.getCaption(Captions.lineListingSharedInformation));
		sharedInformationLabel.addStyleName(CssStyles.H3);
		sharedInformationComponent.addComponent(sharedInformationLabel);

		caseSelector = new CaseSelector();
		caseSelector.setId("lineListingCase");
		sharedInformationComponent.addComponent(caseSelector);

		HorizontalLayout sharedInformationBar = new HorizontalLayout();
		sharedInformationBar.addStyleName(CssStyles.SPACING_SMALL);

		disease = new ComboBox<>(I18nProperties.getCaption(Captions.disease));
		disease.setId("lineListingDisease");
		disease.setItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		sharedInformationBar.addComponent(disease);

		region = new ComboBox<>(I18nProperties.getCaption(Captions.region));
		region.setId("lineListingRegion");
		sharedInformationBar.addComponent(region);

		district = new ComboBox<>(I18nProperties.getCaption(Captions.district));
		district.setId("lineListingDistrict");
		sharedInformationBar.addComponent(district);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = e.getValue();
			updateDistricts(regionDto);
		});

		sharedInformationComponent.addComponent(sharedInformationBar);

		addComponent(sharedInformationComponent);

		caseLines = new ArrayList<>();
		VerticalLayout lineComponent = new VerticalLayout();
		lineComponent.setMargin(false);

		Label lineComponentLabel = new Label();
		lineComponentLabel.setValue(I18nProperties.getCaption(Captions.lineListingNewCasesList));
		lineComponentLabel.addStyleName(CssStyles.H3);
		lineComponent.addComponent(lineComponentLabel);

		ContactLineLayout line = new ContactLineLayout(lineComponent, 0);
		line.setBean(new ContactLineDto());
		caseLines.add(line);
		lineComponent.addComponent(line);
		lineComponent.setSpacing(false);
		addComponent(lineComponent);

		if (UserRole.isSupervisor(UserProvider.getCurrent().getUserRoles())) {
			RegionReferenceDto userRegion = UserProvider.getCurrent().getUser().getRegion();
			region.setValue(userRegion);
			region.setVisible(false);
			updateDistricts(userRegion);
		} else {
			region.setItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		HorizontalLayout actionBar = new HorizontalLayout();
		Button addLine = ButtonHelper.createIconButton(Captions.lineListingAddLine, VaadinIcons.PLUS, e -> {
			ContactLineLayout newLine = new ContactLineLayout(lineComponent, caseLines.size() + 1);
			ContactLineDto lastLineDto = caseLines.get(caseLines.size() - 1).getBean();
			ContactLineDto newLineDto = new ContactLineDto();
			newLineDto.setCaze(lastLineDto.getCaze());
			newLineDto.setDisease(lastLineDto.getDisease());
			newLineDto.setRegion(lastLineDto.getRegion());
			newLineDto.setDistrict(lastLineDto.getDistrict());
			newLineDto.setDateOfReport(lastLineDto.getDateOfReport());
			newLineDto.setDateOfLastContact(lastLineDto.getDateOfLastContact());
			newLineDto.setTypeOfContact(lastLineDto.getTypeOfContact());
			newLineDto.setRelationToCase(lastLineDto.getRelationToCase());
			newLine.setBean(newLineDto);
			caseLines.add(newLine);
			lineComponent.addComponent(newLine);

			if (caseLines.size() > 1) {
				caseLines.get(0).getDelete().setEnabled(true);
			}
		}, ValoTheme.BUTTON_PRIMARY);

		actionBar.addComponent(addLine);
		actionBar.setComponentAlignment(addLine, Alignment.MIDDLE_LEFT);

		addComponent(actionBar);

		HorizontalLayout buttonsPanel = new HorizontalLayout();
		buttonsPanel.setMargin(false);
		buttonsPanel.setSpacing(true);
		buttonsPanel.setWidth(100, Unit.PERCENTAGE);

		cancelButton = ButtonHelper.createButton(Captions.actionDiscard, event -> closeWindow());

		buttonsPanel.addComponent(cancelButton);
		buttonsPanel.setComponentAlignment(cancelButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(cancelButton, 1);

		saveButton = ButtonHelper.createButton(Captions.actionSave, event -> saveCallback.accept(getCaseLineDtos()), ValoTheme.BUTTON_PRIMARY);

		buttonsPanel.addComponent(saveButton);
		buttonsPanel.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(saveButton, 0);

		addComponent(buttonsPanel);
		setComponentAlignment(buttonsPanel, Alignment.BOTTOM_RIGHT);
	}

	private void updateDistricts(RegionReferenceDto regionDto) {
		FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
	}

	public void closeWindow() {
		window.close();
	}

	public void validate() throws ValidationRuntimeException {
		boolean validationFailed = false;
		for (ContactLineLayout caseLine : caseLines) {
			BinderValidationStatus<ContactLineDto> validationStatus = caseLine.validate();
			if (validationStatus.hasErrors()) {
				validationFailed = true;
			}
		}
		if (validationFailed) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorFieldValidationFailed));
		}
	}

	public List<ContactLineDto> getCaseLineDtos() {
		return caseLines.stream().map(caseLine -> caseLine.getBean()).collect(Collectors.toList());
	}

	public void setSaveCallback(Consumer<List<ContactLineDto>> saveCallback) {
		this.saveCallback = saveCallback;
	}

	class ContactLineLayout extends HorizontalLayout {

		private final Binder<ContactLineDto> binder = new Binder<>(ContactLineDto.class);

		private final DateField dateOfReport;
		private final DateField dateOfLastContact;

		private final ComboBox<ContactProximity> typeOfContact;
		private final ComboBox<ContactRelation> relationToCase;
		private final TextField firstname;
		private final TextField lastname;
		private final ComboBox<Integer> dateOfBirthYear;
		private ComboBox<Integer> dateOfBirthMonth;
		private ComboBox<Integer> dateOfBirthDay;
		private final ComboBox<Sex> sex;

		private final Button delete;

		public ContactLineLayout(VerticalLayout lineComponent, int lineIndex) {

			addStyleName(CssStyles.SPACING_SMALL);
			setMargin(false);

			binder.forField(caseSelector).bind(ContactLineDto.CAZE);
			binder.forField(disease).asRequired().bind(ContactLineDto.DISEASE);
			binder.forField(region).asRequired().bind(ContactLineDto.REGION);
			binder.forField(district).asRequired().bind(ContactLineDto.DISTRICT);

			dateOfReport = new DateField();
			dateOfReport.setId("lineListingDateOfReport_" + lineIndex);
			dateOfReport.setWidth(100, Unit.PIXELS);
			binder.forField(dateOfReport).asRequired().bind(ContactLineDto.DATE_OF_REPORT);
			dateOfReport.setRangeEnd(LocalDate.now());

			dateOfLastContact = new DateField();
			dateOfLastContact.setId("lineListingDateOfLastContact_" + lineIndex);
			dateOfLastContact.setWidth(150, Unit.PIXELS);
			binder.forField(dateOfLastContact).bind(ContactLineDto.DATE_OF_LAST_CONTACT);
			dateOfLastContact.setRangeEnd(LocalDate.now());

			typeOfContact = new ComboBox<>();
			typeOfContact.setId("lineListingContactProximity_" + lineIndex);
			typeOfContact.setWidth(200, Unit.PIXELS);
			typeOfContact.addStyleName(CssStyles.CAPTION_OVERFLOW);
			binder.forField(typeOfContact).bind(ContactLineDto.TYPE_OF_CONTACT);

			relationToCase = new ComboBox<>();
			relationToCase.setId("lineListingRelationToCase_" + lineIndex);
			relationToCase.setWidth(200, Unit.PIXELS);
			relationToCase.addStyleName(CssStyles.CAPTION_OVERFLOW);
			binder.forField(relationToCase).bind(ContactLineDto.RELATION_TO_CASE);

			firstname = new TextField();
			firstname.setId("lineListingFirstName_" + lineIndex);
			binder.forField(firstname).asRequired().bind(ContactLineDto.FIRST_NAME);
			lastname = new TextField();
			firstname.setId("lineListingLastName_" + lineIndex);
			binder.forField(lastname).asRequired().bind(ContactLineDto.LAST_NAME);

			dateOfBirthYear = new ComboBox<>();
			dateOfBirthYear.setId("lineListingDateOfBirthYear_" + lineIndex);
			dateOfBirthYear.setEmptySelectionAllowed(true);
			dateOfBirthYear.setItems(DateHelper.getYearsToNow());
			dateOfBirthYear.setWidth(80, Unit.PIXELS);
			dateOfBirthYear.addStyleName(CssStyles.CAPTION_OVERFLOW);
			binder.forField(dateOfBirthYear).withValidator((e, context) -> {
				try {
					ControllerProvider.getPersonController().validateBirthDate(e, dateOfBirthMonth.getValue(), dateOfBirthDay.getValue());
					return ValidationResult.ok();
				} catch (Validator.InvalidValueException ex) {
					return ValidationResult.error(ex.getMessage());
				}
			}).bind(ContactLineDto.DATE_OF_BIRTH_YYYY);
			dateOfBirthMonth = new ComboBox<>();
			dateOfBirthMonth.setId("lineListingDateOfBirthMonth_" + lineIndex);
			dateOfBirthMonth.setEmptySelectionAllowed(true);
			dateOfBirthMonth.setItems(DateHelper.getMonthsInYear());
			dateOfBirthMonth.setPageLength(12);
			setItemCaptionsForMonths(dateOfBirthMonth);
			dateOfBirthMonth.setWidth(120, Unit.PIXELS);
			binder.forField(dateOfBirthMonth).withValidator((e, context) -> {
				try {
					ControllerProvider.getPersonController().validateBirthDate(dateOfBirthYear.getValue(), e, dateOfBirthDay.getValue());
					return ValidationResult.ok();
				} catch (Validator.InvalidValueException ex) {
					return ValidationResult.error(ex.getMessage());
				}
			}).bind(ContactLineDto.DATE_OF_BIRTH_MM);
			dateOfBirthDay = new ComboBox<>();
			dateOfBirthDay.setId("lineListingDateOfBirthDay_" + lineIndex);
			dateOfBirthDay.setEmptySelectionAllowed(true);
			dateOfBirthDay.setWidth(80, Unit.PIXELS);
			binder.forField(dateOfBirthDay).withValidator((e, context) -> {
				try {
					ControllerProvider.getPersonController().validateBirthDate(dateOfBirthYear.getValue(), dateOfBirthMonth.getValue(), e);
					return ValidationResult.ok();
				} catch (Validator.InvalidValueException ex) {
					return ValidationResult.error(ex.getMessage());
				}
			}).bind(ContactLineDto.DATE_OF_BIRTH_DD);

			// Update the list of days according to the selected month and year
			dateOfBirthYear.addValueChangeListener(e -> {
				updateListOfDays(e.getValue(), dateOfBirthMonth.getValue(), dateOfBirthDay);
				dateOfBirthMonth.markAsDirty();
				dateOfBirthDay.markAsDirty();
			});
			dateOfBirthMonth.addValueChangeListener(e -> {
				updateListOfDays(dateOfBirthYear.getValue(), e.getValue(), dateOfBirthDay);
				dateOfBirthYear.markAsDirty();
				dateOfBirthDay.markAsDirty();
			});
			dateOfBirthDay.addValueChangeListener(e -> {
				dateOfBirthYear.markAsDirty();
				dateOfBirthMonth.markAsDirty();
			});

			sex = new ComboBox<>();
			sex.setId("lineListingSex_" + lineIndex);
			sex.setItems(Sex.values());
			sex.setWidth(100, Unit.PIXELS);
			binder.forField(sex).asRequired().bind(ContactLineDto.SEX);
			delete = ButtonHelper.createIconButtonWithCaption("delete_" + lineIndex, null, VaadinIcons.TRASH, event -> {
				lineComponent.removeComponent(this);
				caseLines.remove(this);
				caseLines.get(0).formatAsFirstLine();
				if (caseLines.size() > 1) {
					caseLines.get(0).getDelete().setEnabled(true);
				}
			});

			addComponents(
				dateOfReport,
				dateOfLastContact,
				typeOfContact,
				relationToCase,
				firstname,
				lastname,
				dateOfBirthYear,
				dateOfBirthMonth,
				dateOfBirthDay,
				sex,
				delete);

			if (lineIndex == 0) {
				formatAsFirstLine();
			} else {
				formatAsOtherLine();
			}

			setComponentAlignment(dateOfBirthMonth, Alignment.BOTTOM_LEFT);
			setComponentAlignment(dateOfBirthDay, Alignment.BOTTOM_LEFT);
		}

		public void setBean(ContactLineDto bean) {
			binder.setBean(bean);
		}

		public ContactLineDto getBean() {
			return binder.getBean();
		}

		public BinderValidationStatus<ContactLineDto> validate() {
			return binder.validate();
		}

		private void formatAsFirstLine() {

			formatAsOtherLine();

			dateOfReport.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE));
			dateOfReport.removeStyleName(CssStyles.CAPTION_HIDDEN);
			dateOfLastContact.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.LAST_CONTACT_DATE));
			dateOfLastContact.removeStyleName(CssStyles.CAPTION_HIDDEN);
			typeOfContact.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.CONTACT_PROXIMITY));
			typeOfContact.removeStyleName(CssStyles.CAPTION_HIDDEN);
			relationToCase.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.RELATION_TO_CASE));
			relationToCase.removeStyleName(CssStyles.CAPTION_HIDDEN);
			firstname.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
			firstname.removeStyleName(CssStyles.CAPTION_HIDDEN);
			lastname.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
			lastname.removeStyleName(CssStyles.CAPTION_HIDDEN);
			dateOfBirthYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
			sex.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
			sex.removeStyleName(CssStyles.CAPTION_HIDDEN);
			delete.setEnabled(false);
			setComponentAlignment(delete, Alignment.MIDDLE_LEFT);
		}

		private void formatAsOtherLine() {

			CssStyles.style(dateOfReport, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
			CssStyles.style(firstname, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
			CssStyles.style(lastname, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
			CssStyles.style(sex, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
		}

		private void setItemCaptionsForMonths(ComboBox<Integer> comboBox) {
			comboBox.setItemCaptionGenerator(item -> I18nProperties.getEnumCaption(Month.of(item)));
		}

		private void updateListOfDays(Integer selectedYear, Integer selectedMonth, ComboBox<Integer> dateOfBirthDay) {
			Integer currentlySelected = dateOfBirthDay.getValue();
			List<Integer> daysInMonth = DateHelper.getDaysInMonth(selectedMonth, selectedYear);
			dateOfBirthDay.setItems(daysInMonth);
			if (daysInMonth.contains(currentlySelected)) {
				dateOfBirthDay.setValue(currentlySelected);
			}
		}

		public Button getDelete() {
			return delete;
		}
	}

	public static class ContactLineDto implements Serializable {

		public static final String CAZE = "caze";
		public static final String DISEASE = "disease";
		public static final String REGION = "region";
		public static final String DISTRICT = "district";
		public static final String DATE_OF_REPORT = "dateOfReport";
		public static final String DATE_OF_LAST_CONTACT = "dateOfLastContact";
		public static final String TYPE_OF_CONTACT = "typeOfContact";
		public static final String RELATION_TO_CASE = "relationToCase";
		public static final String FIRST_NAME = "firstName";
		public static final String LAST_NAME = "lastName";
		public static final String DATE_OF_BIRTH_YYYY = "dateOfBirthYYYY";
		public static final String DATE_OF_BIRTH_MM = "dateOfBirthMM";
		public static final String DATE_OF_BIRTH_DD = "dateOfBirthDD";
		public static final String SEX = "sex";

		private CaseReferenceDto caze;
		private Disease disease;
		private RegionReferenceDto region;
		private DistrictReferenceDto district;
		private LocalDate dateOfReport;
		private LocalDate dateOfLastContact;
		private ContactProximity typeOfContact;
		private ContactRelation relationToCase;
		private String firstName;
		private String lastName;
		private Integer dateOfBirthYYYY;
		private Integer dateOfBirthMM;
		private Integer dateOfBirthDD;
		private Sex sex;

		public CaseReferenceDto getCaze() {
			return caze;
		}

		public void setCaze(CaseReferenceDto caze) {
			this.caze = caze;
		}

		public Disease getDisease() {
			return disease;
		}

		public void setDisease(Disease disease) {
			this.disease = disease;
		}

		public RegionReferenceDto getRegion() {
			return region;
		}

		public void setRegion(RegionReferenceDto region) {
			this.region = region;
		}

		public DistrictReferenceDto getDistrict() {
			return district;
		}

		public void setDistrict(DistrictReferenceDto district) {
			this.district = district;
		}

		public LocalDate getDateOfReport() {
			return dateOfReport;
		}

		public void setDateOfReport(LocalDate dateOfReport) {
			this.dateOfReport = dateOfReport;
		}

		public LocalDate getDateOfLastContact() {
			return dateOfLastContact;
		}

		public void setDateOfLastContact(LocalDate dateOfLastContact) {
			this.dateOfLastContact = dateOfLastContact;
		}

		public ContactProximity getTypeOfContact() {
			return typeOfContact;
		}

		public void setTypeOfContact(ContactProximity typeOfContact) {
			this.typeOfContact = typeOfContact;
		}

		public ContactRelation getRelationToCase() {
			return relationToCase;
		}

		public void setRelationToCase(ContactRelation relationToCase) {
			this.relationToCase = relationToCase;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public Integer getDateOfBirthYYYY() {
			return dateOfBirthYYYY;
		}

		public void setDateOfBirthYYYY(Integer dateOfBirthYYYY) {
			this.dateOfBirthYYYY = dateOfBirthYYYY;
		}

		public Integer getDateOfBirthMM() {
			return dateOfBirthMM;
		}

		public void setDateOfBirthMM(Integer dateOfBirthMM) {
			this.dateOfBirthMM = dateOfBirthMM;
		}

		public Integer getDateOfBirthDD() {
			return dateOfBirthDD;
		}

		public void setDateOfBirthDD(Integer dateOfBirthDD) {
			this.dateOfBirthDD = dateOfBirthDD;
		}

		public Sex getSex() {
			return sex;
		}

		public void setSex(Sex sex) {
			this.sex = sex;
		}
	}
}
