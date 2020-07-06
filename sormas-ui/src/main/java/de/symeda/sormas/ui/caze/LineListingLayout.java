package de.symeda.sormas.ui.caze;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateHelper8;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.FieldVisibleAndNotEmptyValidator;

public class LineListingLayout extends VerticalLayout {

	private static final long serialVersionUID = -5565485322654993085L;

	public static final float DEFAULT_WIDTH = 1696;
	public static final float WITDH_WITHOUT_EPID_NUMBER = 1536;

	private ComboBox<Disease> disease;
	private TextField diseaseDetails;

	private ComboBox<RegionReferenceDto> region;
	private ComboBox<DistrictReferenceDto> district;
	private List<CaseLineLayout> caseLines;
	private Button cancelButton;
	private Button saveButton;

	private Window window;

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
		HorizontalLayout sharedInformationBar = new HorizontalLayout();
		sharedInformationBar.addStyleName(CssStyles.SPACING_SMALL);
		disease = new ComboBox<>(I18nProperties.getCaption(Captions.disease));
		disease.setId("lineListingDisease");
		disease.setItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		sharedInformationBar.addComponent(disease);
		diseaseDetails = new TextField(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE_DETAILS));
		diseaseDetails.setId("lineListingDiseaseDetails");
		diseaseDetails.setVisible(false);
		sharedInformationBar.addComponent(diseaseDetails);
		disease.addValueChangeListener(event -> diseaseDetails.setVisible(Disease.OTHER.equals(disease.getValue())));

		region = new ComboBox<>(I18nProperties.getCaption(Captions.region));
		region.setId("lineListingRegion");
		sharedInformationBar.addComponent(region);

		district = new ComboBox<>(I18nProperties.getCaption(Captions.district));
		district.setId("lineListingDistrict");
		district.addValueChangeListener(e -> setEpidNumberPrefixes());
		sharedInformationBar.addComponent(district);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = e.getValue();
			updateDistricts(regionDto);
		});
		district.addValueChangeListener(e -> {
			removeFacilitiesIfCommunityIsNotPresent();
			removeCommunities();
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getValue();
			updateCommunitiesAndFacilities(districtDto);
		});

		sharedInformationComponent.addComponent(sharedInformationBar);

		addComponent(sharedInformationComponent);

		caseLines = new ArrayList<CaseLineLayout>();
		VerticalLayout lineComponent = new VerticalLayout();
		lineComponent.setMargin(false);

		Label lineComponentLabel = new Label();
		lineComponentLabel.setValue(I18nProperties.getCaption(Captions.lineListingNewCasesList));
		lineComponentLabel.addStyleName(CssStyles.H3);
		lineComponent.addComponent(lineComponentLabel);

		CaseLineLayout line = new CaseLineLayout(lineComponent, 0);
		line.setBean(new CaseLineDto());
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
			region.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		}

		HorizontalLayout actionBar = new HorizontalLayout();
		Button addLine = ButtonHelper.createIconButton(Captions.lineListingAddLine, VaadinIcons.PLUS, e -> {
			CaseLineLayout newLine = new CaseLineLayout(lineComponent, caseLines.size() + 1);
			DistrictReferenceDto districtReferenceDto = (DistrictReferenceDto) district.getValue();
			updateCommunityAndFacility(districtReferenceDto, newLine);
			CaseLineDto lastLineDto = caseLines.get(caseLines.size() - 1).getBean();
			CaseLineDto newLineDto = new CaseLineDto();
			newLineDto.setDisease(lastLineDto.getDisease());
			newLineDto.setDiseaseDetails(lastLineDto.getDiseaseDetails());
			newLineDto.setRegion(lastLineDto.getRegion());
			newLineDto.setDistrict(lastLineDto.getDistrict());
			newLineDto.setDateOfReport(lastLineDto.getDateOfReport());
			newLineDto.setCommunity(lastLineDto.getCommunity());
			newLineDto.setFacility(lastLineDto.getFacility());
			newLineDto.setFacilityDetails(lastLineDto.getFacilityDetails());
			newLine.setBean(newLineDto);
			caseLines.add(newLine);
			lineComponent.addComponent(newLine);

			setEpidNumberPrefix(newLine, lastLineDto.getDateOfReport());

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

		saveButton = ButtonHelper.createButton(Captions.actionSave, event -> save(), ValoTheme.BUTTON_PRIMARY);

		buttonsPanel.addComponent(saveButton);
		buttonsPanel.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(saveButton, 0);

		addComponent(buttonsPanel);
		setComponentAlignment(buttonsPanel, Alignment.BOTTOM_RIGHT);

	}

	private void updateDistricts(RegionReferenceDto regionDto) {
		FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
	}

	private void setEpidNumberPrefixes() {

		for (CaseLineLayout layout : caseLines) {
			LocalDate dateOfReport = layout.dateOfReport.getValue();
			setEpidNumberPrefix(layout, dateOfReport);
		}
	}

	private void setEpidNumberPrefix(CaseLineLayout layout, LocalDate date) {

		if (district.getValue() != null) {
			if (date == null) {
				layout.epidNumber.setValue(getEpidNumberPrefix(null));
			} else {
				String year = String.valueOf(date.getYear()).substring(2);
				layout.epidNumber.setValue(getEpidNumberPrefix(year));
			}
		}
	}

	private String getEpidNumberPrefix(String year) {

		String fullEpidCode = FacadeProvider.getDistrictFacade().getFullEpidCodeForDistrict(district.getValue().getUuid());
		if (year == null) {
			return fullEpidCode + "-";
		} else {
			return fullEpidCode + "-" + year + "-";
		}
	}

	private void closeWindow() {
		window.close();
	}

	private void save() {
		try {
			validate();
		} catch (ValidationRuntimeException e) {
			Notification.show(I18nProperties.getString(Strings.errorFieldValidationFailed), "", Type.ERROR_MESSAGE);
			return;
		}

		for (CaseLineDto caseLineDto : getCaseLineDtos()) {

			CaseDataDto newCase = CaseDataDto.build(PersonDto.build().toReference(), caseLineDto.getDisease());

			newCase.setDiseaseDetails(caseLineDto.getDiseaseDetails());
			newCase.setRegion(caseLineDto.getRegion());
			newCase.setDistrict(caseLineDto.getDistrict());
			newCase.setReportDate(DateHelper8.toDate(caseLineDto.getDateOfReport()));
			newCase.setEpidNumber(caseLineDto.getEpidNumber());
			newCase.setCommunity((CommunityReferenceDto) caseLineDto.getCommunity());
			newCase.setHealthFacility((FacilityReferenceDto) caseLineDto.getFacility());
			newCase.setHealthFacilityDetails(caseLineDto.getFacilityDetails());

			if (caseLineDto.getDateOfOnset() != null) {
				newCase.getSymptoms().setOnsetDate(DateHelper8.toDate(caseLineDto.getDateOfOnset()));
			}

			newCase.setReportingUser(UserProvider.getCurrent().getUserReference());

			final PersonDto newPerson = PersonDto.build();
			newPerson.setFirstName(caseLineDto.getFirstName());
			newPerson.setLastName(caseLineDto.getLastName());
			newPerson.setBirthdateYYYY(caseLineDto.getDateOfBirthYYYY());
			newPerson.setBirthdateMM(caseLineDto.getDateOfBirthMM());
			newPerson.setBirthdateDD(caseLineDto.getDateOfBirthDD());
			newPerson.setSex(caseLineDto.getSex());

			ControllerProvider.getCaseController().selectOrCreateCase(newCase, newPerson, uuid -> {
				if (uuid == null) {

					FacadeProvider.getPersonFacade().savePerson(newPerson);

					newCase.setPerson(newPerson.toReference());

					FacadeProvider.getCaseFacade().saveCase(newCase);
					Notification.show(I18nProperties.getString(Strings.messageCaseCreated), Type.ASSISTIVE_NOTIFICATION);
				}
			});
		}
		closeWindow();
		ControllerProvider.getCaseController().navigateToIndex();
	}

	private void updateCommunitiesAndFacilities(DistrictReferenceDto districtDto) {

		for (CaseLineLayout line : caseLines) {
			updateCommunityAndFacility(districtDto, line);
		}
	}

	private void updateCommunityAndFacility(DistrictReferenceDto districtDto, CaseLineLayout line) {
		FieldHelper.updateItems(
			line.getCommunity(),
			districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
		FieldHelper.updateItems(
			line.getFacility(),
			districtDto != null ? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict(districtDto, true) : null);
	}

	private void removeCommunities() {

		for (CaseLineLayout line : caseLines) {
			FieldHelper.removeItems(line.getCommunity());
		}
	}

	private void removeFacilitiesIfCommunityIsNotPresent() {

		for (CaseLineLayout line : caseLines) {
			if (line.getCommunity().getValue() == null) {
				FieldHelper.removeItems(line.getFacility());
			}
		}
	}

	public void validate() throws ValidationRuntimeException {
		boolean validationFailed = false;
		for (CaseLineLayout caseLine : caseLines) {
			BinderValidationStatus<CaseLineDto> validationStatus = caseLine.validate();
			if (validationStatus.hasErrors()) {
				validationFailed = true;
			}
		}
		if (validationFailed) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorFieldValidationFailed));
		}
	}

	public List<CaseLineDto> getCaseLineDtos() {
		return caseLines.stream().map(caseLine -> caseLine.getBean()).collect(Collectors.toList());
	}

	class CaseLineLayout extends HorizontalLayout {

		private static final long serialVersionUID = 4159615474757272630L;

		private Binder<CaseLineDto> binder = new Binder<>(CaseLineDto.class);

		private DateField dateOfReport;
		private TextField epidNumber;
		private ComboBox<CommunityReferenceDto> community;
		private ComboBox<FacilityReferenceDto> facility;
		private TextField facilityDetails;
		private TextField firstname;
		private TextField lastname;
		private ComboBox<Integer> dateOfBirthYear;
		private ComboBox<Integer> dateOfBirthMonth;
		private ComboBox<Integer> dateOfBirthDay;
		private ComboBox<Sex> sex;
		private DateField dateOfOnset;

		private Button delete;

		public CaseLineLayout(VerticalLayout lineComponent, int lineIndex) {

			addStyleName(CssStyles.SPACING_SMALL);
			setMargin(false);

			binder.forField(disease).asRequired().bind(CaseLineDto.DISEASE);
			binder.forField(diseaseDetails)
				.asRequired(new FieldVisibleAndNotEmptyValidator<String>(I18nProperties.getString(Strings.errorFieldValidationFailed)))
				.bind(CaseLineDto.DISEASE_DETAILS);
			binder.forField(region).asRequired().bind(CaseLineDto.REGION);
			binder.forField(district).asRequired().bind(CaseLineDto.DISTRICT);

			dateOfReport = new DateField();
			dateOfReport.setId("lineListingDateOfReport_" + lineIndex);
			dateOfReport.setWidth(100, Unit.PIXELS);
			binder.forField(dateOfReport).asRequired().bind(CaseLineDto.DATE_OF_REPORT);
			dateOfReport.addValueChangeListener(e -> setEpidNumberPrefix(this, dateOfReport.getValue()));
			epidNumber = new TextField();
			epidNumber.setId("lineListingEpidNumber_" + lineIndex);
			epidNumber.setWidth(160, Unit.PIXELS);
			binder.forField(epidNumber).bind(CaseLineDto.EPID_NUMBER);
			community = new ComboBox<>();
			community.setId("lineListingCommunity_" + lineIndex);
			community.addStyleName(CssStyles.SOFT_REQUIRED);
			community.addValueChangeListener(e -> {
				FieldHelper.removeItems(facility);
				CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getValue();
				FieldHelper.updateItems(
					facility,
					communityDto != null
						? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByCommunity(communityDto, true)
						: district.getValue() != null
							? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict((DistrictReferenceDto) district.getValue(), true)
							: null);
			});
			binder.forField(community).bind(CaseLineDto.COMMUNITY);
			facility = new ComboBox<>();
			facility.setId("lineListingFacility_" + lineIndex);
			facility.setWidth(364, Unit.PIXELS);
			facility.addValueChangeListener(e -> {
				updateFacilityFields(facility, facilityDetails);
			});
			binder.forField(facility).asRequired().bind(CaseLineDto.FACILITY);
			facilityDetails = new TextField();
			facilityDetails.setId("lineListingFacilityDetails_" + lineIndex);
			facilityDetails.setVisible(false);
			updateFacilityFields(facility, facilityDetails);
			binder.forField(facilityDetails)
				.asRequired(new FieldVisibleAndNotEmptyValidator<String>(I18nProperties.getString(Strings.errorFieldValidationFailed)))
				.bind(CaseLineDto.FACILITIY_DETAILS);

			firstname = new TextField();
			firstname.setId("lineListingFirstName_" + lineIndex);
			binder.forField(firstname).asRequired().bind(CaseLineDto.FIRST_NAME);
			lastname = new TextField();
			firstname.setId("lineListingLastName_" + lineIndex);
			binder.forField(lastname).asRequired().bind(CaseLineDto.LAST_NAME);

			dateOfBirthYear = new ComboBox<>();
			dateOfBirthYear.setId("lineListingDateOfBirthYear_" + lineIndex);
			dateOfBirthYear.setEmptySelectionAllowed(true);
			dateOfBirthYear.setItems(DateHelper.getYearsToNow());
			dateOfBirthYear.setWidth(80, Unit.PIXELS);
			dateOfBirthYear.addStyleName(CssStyles.CAPTION_OVERFLOW);
			binder.forField(dateOfBirthYear).bind(CaseLineDto.DATE_OF_BIRTH_YYYY);
			dateOfBirthMonth = new ComboBox<>();
			dateOfBirthMonth.setId("lineListingDateOfBirthMonth_" + lineIndex);
			dateOfBirthMonth.setEmptySelectionAllowed(true);
			dateOfBirthMonth.setItems(DateHelper.getMonthsInYear());
			dateOfBirthMonth.setPageLength(12);
			setItemCaptionsForMonths(dateOfBirthMonth);
			dateOfBirthMonth.setWidth(120, Unit.PIXELS);
			binder.forField(dateOfBirthMonth).bind(CaseLineDto.DATE_OF_BIRTH_MM);
			dateOfBirthDay = new ComboBox<>();
			dateOfBirthDay.setId("lineListingDateOfBirthDay_" + lineIndex);
			dateOfBirthDay.setEmptySelectionAllowed(true);
			dateOfBirthDay.setWidth(80, Unit.PIXELS);
			binder.forField(dateOfBirthDay).bind(CaseLineDto.DATE_OF_BIRTH_DD);

			// Update the list of days according to the selected month and year
			dateOfBirthYear.addValueChangeListener(e -> {
				updateListOfDays((Integer) e.getValue(), (Integer) dateOfBirthMonth.getValue(), dateOfBirthDay);
			});
			dateOfBirthMonth.addValueChangeListener(e -> {
				updateListOfDays((Integer) dateOfBirthYear.getValue(), (Integer) e.getValue(), dateOfBirthDay);
			});

			sex = new ComboBox<>();
			sex.setId("lineListingSex_" + lineIndex);
			sex.setItems(Sex.values());
			sex.setWidth(100, Unit.PIXELS);
			binder.forField(sex).bind(CaseLineDto.SEX);
			dateOfOnset = new DateField();
			dateOfOnset.setId("lineListingDateOfOnSet_" + lineIndex);
			dateOfOnset.setWidth(100, Unit.PIXELS);
			dateOfOnset.addStyleName(CssStyles.CAPTION_FIXED_WIDTH_100);
			binder.forField(dateOfOnset).bind(CaseLineDto.DATE_OF_ONSET);
			delete = ButtonHelper.createIconButtonWithCaption("delete_" + lineIndex, null, VaadinIcons.TRASH, event -> {
				lineComponent.removeComponent(this);
				caseLines.remove(this);
				caseLines.get(0).formatAsFirstLine();
				if (caseLines.size() > 1) {
					caseLines.get(0).getDelete().setEnabled(true);
				}
			});

			addComponent(dateOfReport);
			if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_CHANGE_EPID_NUMBER)) {
				addComponent(epidNumber);
			}
			addComponents(
				community,
				facility,
				facilityDetails,
				firstname,
				lastname,
				dateOfBirthYear,
				dateOfBirthMonth,
				dateOfBirthDay,
				sex,
				dateOfOnset,
				delete);

			if (lineIndex == 0) {
				formatAsFirstLine();
			} else {
				formatAsOtherLine();
			}

			setComponentAlignment(dateOfBirthMonth, Alignment.BOTTOM_LEFT);
			setComponentAlignment(dateOfBirthDay, Alignment.BOTTOM_LEFT);
		}

		public void setBean(CaseLineDto bean) {
			binder.setBean(bean);
		}

		public CaseLineDto getBean() {
			return binder.getBean();
		}

		public BinderValidationStatus<CaseLineDto> validate() {
			return binder.validate();
		}

		private void formatAsFirstLine() {

			setRequiredInicatorsVisibility(true);

			formatAsOtherLine();

			dateOfReport.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE));
			dateOfReport.removeStyleName(CssStyles.CAPTION_HIDDEN);
			epidNumber.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.EPID_NUMBER));
			community.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.COMMUNITY));
			facility.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY));
			facility.removeStyleName(CssStyles.CAPTION_HIDDEN);
			// no caption due to limited space and dependence on type of facility (other or home)
//			facilityDetails.setCaption(I18nProperties.getCaption(Captions.caseHealthFacilityDetailsShort));
//			facilityDetails.removeStyleName(CssStyles.CAPTION_HIDDEN);
			CssStyles.style(facilityDetails, CssStyles.FORCE_CAPTION);
			firstname.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
			firstname.removeStyleName(CssStyles.CAPTION_HIDDEN);
			lastname.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
			lastname.removeStyleName(CssStyles.CAPTION_HIDDEN);
			dateOfBirthYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
			sex.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
			dateOfOnset.setCaption(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
			dateOfOnset.setDescription(I18nProperties.getPrefixDescription(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
			delete.setEnabled(false);
			setComponentAlignment(delete, Alignment.MIDDLE_LEFT);
		}

		private void formatAsOtherLine() {

			setRequiredInicatorsVisibility(false);

			CssStyles.style(dateOfReport, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
			CssStyles.style(facility, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
			CssStyles.style(facilityDetails, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
			CssStyles.style(firstname, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
			CssStyles.style(lastname, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
		}

		private void setRequiredInicatorsVisibility(boolean visible) {

			dateOfReport.setRequiredIndicatorVisible(visible);
			facility.setRequiredIndicatorVisible(visible);
			firstname.setRequiredIndicatorVisible(visible);
			lastname.setRequiredIndicatorVisible(visible);
		}

		private void setItemCaptionsForMonths(ComboBox<Integer> comboBox) {
			comboBox.setItemCaptionGenerator(item -> I18nProperties.getEnumCaption(Month.of(item)));
		}

		private void updateListOfDays(Integer selectedYear, Integer selectedMonth, ComboBox<Integer> dateOfBirthDay) {
			Integer currentlySelected = (Integer) dateOfBirthDay.getValue();
			List<Integer> daysInMonth = DateHelper.getDaysInMonth(selectedMonth, selectedYear);
			dateOfBirthDay.setItems(daysInMonth);
			if (daysInMonth.contains(currentlySelected)) {
				dateOfBirthDay.setValue(currentlySelected);
			}
		}

		private void updateFacilityFields(ComboBox<FacilityReferenceDto> cbFacility, TextField tfFacilityDetails) {
			if (cbFacility.getValue() != null) {
				boolean otherHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
				boolean noneHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
				boolean visibleEnabledAndRequired = otherHealthFacility || noneHealthFacility;

				tfFacilityDetails.setVisible(visibleEnabledAndRequired);
				tfFacilityDetails.setEnabled(visibleEnabledAndRequired);

				if (otherHealthFacility) {
					tfFacilityDetails.setPlaceholder(I18nProperties.getCaption(Captions.caseHealthFacilityDetailsShort));

				}
				if (noneHealthFacility) {
					tfFacilityDetails.setPlaceholder(I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails));
				}
				if (visibleEnabledAndRequired) {
					cbFacility.setWidthUndefined();
				} else {
					tfFacilityDetails.clear();
					cbFacility.setWidth(324, Unit.PIXELS);
				}
			} else {
				tfFacilityDetails.setVisible(false);
				tfFacilityDetails.setEnabled(false);
				tfFacilityDetails.clear();
				cbFacility.setWidth(324, Unit.PIXELS);
			}
		}

		public ComboBox<CommunityReferenceDto> getCommunity() {
			return community;
		}

		public ComboBox<FacilityReferenceDto> getFacility() {
			return facility;
		}

		public TextField getFacilityDetails() {
			return facilityDetails;
		}

		public Button getDelete() {
			return delete;
		}
	}

	public static class CaseLineDto implements Serializable {

		private static final long serialVersionUID = -6638490209881568126L;

		public static final String DISEASE = "disease";
		public static final String DISEASE_DETAILS = "diseaseDetails";
		public static final String REGION = "region";
		public static final String DISTRICT = "district";
		public static final String DATE_OF_REPORT = "dateOfReport";
		public static final String EPID_NUMBER = "epidNumber";
		public static final String COMMUNITY = "community";
		public static final String FACILITY = "facility";
		public static final String FACILITIY_DETAILS = "facilityDetails";
		public static final String FIRST_NAME = "firstName";
		public static final String LAST_NAME = "lastName";
		public static final String DATE_OF_BIRTH_YYYY = "dateOfBirthYYYY";
		public static final String DATE_OF_BIRTH_MM = "dateOfBirthMM";
		public static final String DATE_OF_BIRTH_DD = "dateOfBirthDD";
		public static final String SEX = "sex";
		public static final String DATE_OF_ONSET = "dateOfOnset";

		private Disease disease;
		private String diseaseDetails;
		private RegionReferenceDto region;
		private DistrictReferenceDto district;
		private LocalDate dateOfReport;
		private String epidNumber;
		private CommunityReferenceDto community;
		private FacilityReferenceDto facility;
		private String facilityDetails;
		private String firstName;
		private String lastName;
		private Integer dateOfBirthYYYY;
		private Integer dateOfBirthMM;
		private Integer dateOfBirthDD;
		private Sex sex;
		private LocalDate dateOfOnset;

		public CaseLineDto(
			Disease disease,
			String diseaseDetails,
			RegionReferenceDto region,
			DistrictReferenceDto district,
			LocalDate dateOfReport,
			String epidNumber,
			CommunityReferenceDto community,
			FacilityReferenceDto facility,
			String facilityDetails,
			String firstname,
			String lastname,
			Integer dateOfBirthYear,
			Integer dateOfBirthMonth,
			Integer dateOfBirthDay,
			Sex sex,
			LocalDate dateOfOnset) {

			this.disease = disease;
			this.diseaseDetails = diseaseDetails;
			this.region = region;
			this.district = district;
			this.dateOfReport = dateOfReport;
			this.epidNumber = epidNumber;
			this.community = community;
			this.facility = facility;
			this.facilityDetails = facilityDetails;
			this.firstName = firstname;
			this.lastName = lastname;
			this.dateOfBirthYYYY = dateOfBirthYear;
			this.dateOfBirthMM = dateOfBirthMonth;
			this.dateOfBirthDD = dateOfBirthDay;
			this.sex = sex;
			this.dateOfOnset = dateOfOnset;
		}

		public CaseLineDto() {
		}

		public Disease getDisease() {
			return disease;
		}

		public void setDisease(Disease disease) {
			this.disease = disease;
		}

		public String getDiseaseDetails() {
			return diseaseDetails;
		}

		public void setDiseaseDetails(String diseaseDetails) {
			this.diseaseDetails = diseaseDetails;
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

		public String getEpidNumber() {
			return epidNumber;
		}

		public void setEpidNumber(String epidNumber) {
			this.epidNumber = epidNumber;
		}

		public CommunityReferenceDto getCommunity() {
			return community;
		}

		public void setCommunity(CommunityReferenceDto community) {
			this.community = community;
		}

		public FacilityReferenceDto getFacility() {
			return facility;
		}

		public void setFacility(FacilityReferenceDto facility) {
			this.facility = facility;
		}

		public String getFacilityDetails() {
			return facilityDetails;
		}

		public void setFacilityDetails(String facilityDetails) {
			this.facilityDetails = facilityDetails;
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

		public LocalDate getDateOfOnset() {
			return dateOfOnset;
		}

		public void setDateOfOnset(LocalDate dateOfOnset) {
			this.dateOfOnset = dateOfOnset;
		}
	}
}
