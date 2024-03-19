package de.symeda.sormas.ui.caze.components.linelisting;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.FieldVisibleAndNotEmptyValidator;
import de.symeda.sormas.ui.utils.components.linelisting.line.DeleteLineEvent;
import de.symeda.sormas.ui.utils.components.linelisting.line.LineLayout;
import de.symeda.sormas.ui.utils.components.linelisting.model.LineDto;
import de.symeda.sormas.ui.utils.components.linelisting.person.PersonField;
import de.symeda.sormas.ui.utils.components.linelisting.person.PersonFieldDto;
import de.symeda.sormas.ui.utils.components.linelisting.section.LineListingSection;

public class LineListingLayout extends VerticalLayout {

	private static final long serialVersionUID = -5565485322654993085L;

	public static final float DEFAULT_WIDTH = 1696;
	public static final float WITDH_WITHOUT_EPID_NUMBER = 1536;

	private final ComboBox<Disease> disease;
	private final TextField diseaseDetails;

	private final ComboBox<RegionReferenceDto> region;
	private final ComboBox<DistrictReferenceDto> district;
	private final ComboBox<FacilityTypeGroup> typeGroup;
	private final ComboBox<FacilityType> type;

	private final List<CaseLineLayout> caseLines;

	private final Window window;
	private Consumer<LinkedList<LineDto<CaseDataDto>>> saveCallback;

	public LineListingLayout(Window window) {

		this.window = window;

		setSpacing(false);

		LineListingSection sharedInformationComponent = new LineListingSection(Captions.lineListingSharedInformation);

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

		region = new ComboBox<>(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_REGION));
		region.setItemCaptionGenerator(item -> item.buildCaption());
		region.setId("lineListingRegion");
		sharedInformationBar.addComponent(region);

		district = new ComboBox<>(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_DISTRICT));
		district.setItemCaptionGenerator(item -> item.buildCaption());
		district.setId("lineListingDistrict");
		sharedInformationBar.addComponent(district);

		typeGroup = new ComboBox<>(I18nProperties.getCaption(Captions.Facility_typeGroup));
		typeGroup.setId("typeGroup");
		typeGroup.setWidth(200, Unit.PIXELS);
		typeGroup.setItems(FacilityTypeGroup.getAccomodationGroups());
		sharedInformationBar.addComponent(typeGroup);

		type = new ComboBox<>(I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.TYPE));
		type.setId("type");
		type.setWidth(200, Unit.PIXELS);
		sharedInformationBar.addComponent(type);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = e.getValue();
			updateDistricts(regionDto);
		});
		district.addValueChangeListener(e -> {
			removeFacilitiesIfCommunityIsNotPresent();
			removeCommunities();
			DistrictReferenceDto districtDto = e.getValue();
			updateCommunitiesAndFacilities(districtDto);
		});
		typeGroup.addValueChangeListener(e -> {
			removeFacilities();
			type.setItems(typeGroup.getValue() != null ? FacilityType.getAccommodationTypes(typeGroup.getValue()) : new ArrayList<>());
			type.setValue(null);
		});
		type.addValueChangeListener(e -> {
			removeFacilities();
			if (type.getValue() != null && district.getValue() != null) {
				updateFacilities(type.getValue(), district.getValue());
			}
		});

		sharedInformationComponent.addComponent(sharedInformationBar);

		addComponent(sharedInformationComponent);

		LineListingSection lineComponent = new LineListingSection(Captions.lineListingNewCasesList);

		caseLines = new ArrayList<>();
		CaseLineLayout line = buildNewLine(lineComponent);
		caseLines.add(line);
		lineComponent.addComponent(line);
		lineComponent.setSpacing(false);
		addComponent(lineComponent);

		if (UiUtil.hasRegionJurisdictionLevel()) {
			RegionReferenceDto userRegion = UiUtil.getUser().getRegion();
			region.setValue(userRegion);
			region.setVisible(false);
			updateDistricts(userRegion);
		} else {
			region.setItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			region.setVisible(false);
			region.setValue(FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
			district.setVisible(false);
			district.setValue(FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
		}

		HorizontalLayout actionBar = new HorizontalLayout();
		Button addLine = ButtonHelper.createIconButton(Captions.lineListingAddLine, VaadinIcons.PLUS, e -> {
			CaseLineLayout newLine = buildNewLine(lineComponent);
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

		Button cancelButton = ButtonHelper.createButton(Captions.actionDiscard, event -> closeWindow());

		buttonsPanel.addComponent(cancelButton);
		buttonsPanel.setComponentAlignment(cancelButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(cancelButton, 1);

		Button saveButton = ButtonHelper.createButton(Captions.actionSave, event -> saveCallback.accept(getCaseLineDtos()), ValoTheme.BUTTON_PRIMARY);

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

	public void closeWindow() {
		window.close();
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
		if (type.getValue() != null) {
			updateFacility(type.getValue(), districtDto, line);
		}
	}

	private void updateFacilities(FacilityType type, DistrictReferenceDto districtDto) {
		for (CaseLineLayout line : caseLines) {
			if (line.getCommunity().getValue() != null) {
				updateFacility(type, line.getCommunity().getValue(), line);
			} else {
				updateFacility(type, districtDto, line);
			}
		}
	}

	private void updateFacility(FacilityType type, DistrictReferenceDto districtDto, CaseLineLayout line) {
		FieldHelper.updateItems(
			line.getFacility(),
			districtDto != null ? FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(districtDto, type, true, false) : null);
	}

	private void updateFacility(FacilityType type, CommunityReferenceDto communityDto, CaseLineLayout line) {
		FieldHelper.updateItems(
			line.getFacility(),
			communityDto != null ? FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(communityDto, type, true, false) : null);
	}

	private void removeCommunities() {

		for (CaseLineLayout line : caseLines) {
			FieldHelper.removeItems(line.getCommunity());
		}
	}

	private void removeFacilities() {

		for (CaseLineLayout line : caseLines) {
			FieldHelper.removeItems(line.getFacility());
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
		for (CaseLineLayout line : caseLines) {
			if (line.hasErrors()) {
				validationFailed = true;
			}
		}
		if (validationFailed) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorFieldValidationFailed));
		}
	}

	private LinkedList<LineDto<CaseDataDto>> getCaseLineDtos() {
		return caseLines.stream().map(line -> {
			CaseLineDto caseLineDto = line.getBean();
			LineDto<CaseDataDto> result = new LineDto<>();

			CaseDataDto caze = CaseDataDto.build(PersonDto.build().toReference(), caseLineDto.getDisease());

			caze.setDiseaseDetails(caseLineDto.getDiseaseDetails());
			caze.setResponsibleRegion(caseLineDto.getRegion());
			caze.setResponsibleDistrict(caseLineDto.getDistrict());
			caze.setReportDate(UtilDate.from(caseLineDto.getDateOfReport()));
			caze.setEpidNumber(caseLineDto.getEpidNumber());
			caze.setResponsibleCommunity(caseLineDto.getCommunity());
			caze.setFacilityType(caseLineDto.getFacilityType());
			caze.setHealthFacility(caseLineDto.getFacility());
			caze.setHealthFacilityDetails(caseLineDto.getFacilityDetails());
			if (caseLineDto.getDateOfOnset() != null) {
				caze.getSymptoms().setOnsetDate(UtilDate.from(caseLineDto.getDateOfOnset()));
			}
			if (UserProvider.getCurrent() != null) {
				caze.setReportingUser(UiUtil.getUserReference());
			}
			result.setEntity(caze);

			final PersonDto person = PersonDto.build();
			person.setFirstName(caseLineDto.getPerson().getFirstName());
			person.setLastName(caseLineDto.getPerson().getLastName());
			if (caseLineDto.getPerson().getBirthDate() != null) {
				person.setBirthdateYYYY(caseLineDto.getPerson().getBirthDate().getDateOfBirthYYYY());
				person.setBirthdateMM(caseLineDto.getPerson().getBirthDate().getDateOfBirthMM());
				person.setBirthdateDD(caseLineDto.getPerson().getBirthDate().getDateOfBirthDD());
			}
			person.setSex(caseLineDto.getPerson().getSex());
			result.setPerson(person);

			return result;
		}).collect(Collectors.toCollection(LinkedList::new));
	}

	public void setSaveCallback(Consumer<LinkedList<LineDto<CaseDataDto>>> saveCallback) {
		this.saveCallback = saveCallback;
	}

	private CaseLineLayout buildNewLine(VerticalLayout lineComponent) {
		CaseLineLayout newLine = new CaseLineLayout(caseLines.size());
		DistrictReferenceDto districtReferenceDto = district.getValue();
		updateCommunityAndFacility(districtReferenceDto, newLine);

		CaseLineDto newLineDto = new CaseLineDto();

		if (!caseLines.isEmpty()) {
			CaseLineDto lastLineDto = caseLines.get(caseLines.size() - 1).getBean();
			newLineDto.setDisease(lastLineDto.getDisease());
			newLineDto.setDiseaseDetails(lastLineDto.getDiseaseDetails());
			newLineDto.setRegion(lastLineDto.getRegion());
			newLineDto.setDistrict(lastLineDto.getDistrict());
			newLineDto.setDateOfReport(lastLineDto.getDateOfReport());
			newLineDto.setCommunity(lastLineDto.getCommunity());
			newLineDto.setFacilityTypeGroup(lastLineDto.getFacilityTypeGroup());
			newLineDto.setFacilityType(lastLineDto.getFacilityType());
			newLineDto.setFacility(lastLineDto.getFacility());
			newLineDto.setFacilityDetails(lastLineDto.getFacilityDetails());

			setEpidNumberPrefix(newLine, lastLineDto.getDateOfReport());
		} else {
			Disease defaultDisease = FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease();
			if (defaultDisease != null) {
				newLineDto.setDisease(defaultDisease);
			}
		}

		newLine.setBean(newLineDto);
		newLine.addDeleteLineListener(e -> {
			CaseLineLayout selectedLine = (CaseLineLayout) e.getComponent();
			lineComponent.removeComponent(selectedLine);
			caseLines.remove(selectedLine);
			caseLines.get(0).formatAsFirstLine();
			if (caseLines.size() > 1) {
				caseLines.get(0).getDelete().setEnabled(true);
			}
		});

		return newLine;
	}

	class CaseLineLayout extends LineLayout {

		private static final long serialVersionUID = 4159615474757272630L;

		private final Binder<CaseLineDto> binder = new Binder<>(CaseLineDto.class);

		private final DateField dateOfReport;
		private final TextField epidNumber;
		private final ComboBox<CommunityReferenceDto> community;
		private ComboBox<FacilityReferenceDto> facility;
		private TextField facilityDetails;
		private final PersonField person;
		private final DateField dateOfOnset;

		private final Button delete;

		public CaseLineLayout(int lineIndex) {

			addStyleName(CssStyles.SPACING_SMALL);
			setMargin(false);

			binder.forField(disease).asRequired().bind(CaseLineDto.DISEASE);
			binder.forField(diseaseDetails)
				.asRequired(new FieldVisibleAndNotEmptyValidator<>(I18nProperties.getString(Strings.errorFieldValidationFailed)))
				.bind(CaseLineDto.DISEASE_DETAILS);
			binder.forField(region).asRequired().bind(CaseLineDto.REGION);
			binder.forField(district).asRequired().bind(CaseLineDto.DISTRICT);
			binder.forField(typeGroup).asRequired().bind(CaseLineDto.FACILITY_TYPE_GROUP);
			binder.forField(type).asRequired().bind(CaseLineDto.FACILITY_TYPE);

			dateOfReport = new DateField();
			dateOfReport.setId("lineListingDateOfReport_" + lineIndex);
			dateOfReport.setWidth(100, Unit.PIXELS);
			binder.forField(dateOfReport).asRequired().bind(CaseLineDto.DATE_OF_REPORT);
			dateOfReport.setRangeEnd(LocalDate.now());
			dateOfReport.addValueChangeListener(e -> setEpidNumberPrefix(this, dateOfReport.getValue()));
			epidNumber = new TextField();
			epidNumber.setId("lineListingEpidNumber_" + lineIndex);
			epidNumber.setWidth(160, Unit.PIXELS);
			binder.forField(epidNumber).bind(CaseLineDto.EPID_NUMBER);
			community = new ComboBox<>();
			community.setItemCaptionGenerator(item -> item.buildCaption());
			community.setId("lineListingCommunity_" + lineIndex);
			community.addStyleName(CssStyles.SOFT_REQUIRED);
			community.addValueChangeListener(e -> {
				FieldHelper.removeItems(facility);
				CommunityReferenceDto communityDto = e.getValue();
				if (type.getValue() != null) {
					FieldHelper.updateItems(
						facility,
						communityDto != null
							? FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(communityDto, type.getValue(), true, false)
							: district.getValue() != null
								? FacadeProvider.getFacilityFacade()
									.getActiveFacilitiesByDistrictAndType(district.getValue(), type.getValue(), true, false)
								: null);
				}
			});
			binder.forField(community).bind(CaseLineDto.COMMUNITY);
			facility = new ComboBox<>();
			facility.setItemCaptionGenerator(item -> item.buildCaption());
			facility.setId("lineListingFacility_" + lineIndex);
			facility.setWidth(364, Unit.PIXELS);
			facility.addValueChangeListener(e -> updateFacilityFields(facility, facilityDetails));
			binder.forField(facility).asRequired().bind(CaseLineDto.FACILITY);
			facilityDetails = new TextField();
			facilityDetails.setId("lineListingFacilityDetails_" + lineIndex);
			CssStyles.style(facilityDetails, CssStyles.SOFT_REQUIRED);
			facilityDetails.setVisible(false);
			updateFacilityFields(facility, facilityDetails);
			binder.forField(facilityDetails).bind(CaseLineDto.FACILITY_DETAILS);

			person = new PersonField();
			person.setId("lineListingPerson_" + lineIndex);
			binder.forField(person).bind(CaseLineDto.PERSON);

			dateOfOnset = new DateField();
			dateOfOnset.setId("lineListingDateOfOnSet_" + lineIndex);
			dateOfOnset.setWidth(100, Unit.PIXELS);
			dateOfOnset.addStyleName(CssStyles.CAPTION_FIXED_WIDTH_100);
			binder.forField(dateOfOnset).bind(CaseLineDto.DATE_OF_ONSET);
			delete = ButtonHelper
				.createIconButtonWithCaption("delete_" + lineIndex, null, VaadinIcons.TRASH, event -> fireEvent(new DeleteLineEvent(this)));

			addComponent(dateOfReport);
			if (shouldShowEpidNumber()) {
				addComponent(epidNumber);
			}
			addComponents(community, facility, facilityDetails, person, dateOfOnset, delete);

			if (lineIndex == 0) {
				formatAsFirstLine();
			} else {
				formatAsOtherLine();
			}

			if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
				community.setVisible(false);
				community.setValue(FacadeProvider.getCommunityFacade().getDefaultInfrastructureReference());
			}
		}

		public void setBean(CaseLineDto bean) {
			binder.setBean(bean);
		}

		public CaseLineDto getBean() {
			return binder.getBean();
		}

		public boolean hasErrors() {
			BinderValidationStatus<PersonFieldDto> personValidationStatus = person.validate();
			BinderValidationStatus<CaseLineDto> lineValidationStatus = binder.validate();
			return personValidationStatus.hasErrors() || lineValidationStatus.hasErrors();
		}

		private void formatAsFirstLine() {

			formatAsOtherLine();

			dateOfReport.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE));
			dateOfReport.removeStyleName(CssStyles.CAPTION_HIDDEN);
			epidNumber.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.EPID_NUMBER));
			community.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_COMMUNITY));
			facility.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY));
			facility.removeStyleName(CssStyles.CAPTION_HIDDEN);
			CssStyles.style(facilityDetails, CssStyles.FORCE_CAPTION);
			person.showCaptions();
			dateOfOnset.setCaption(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
			dateOfOnset.setDescription(I18nProperties.getPrefixDescription(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
			delete.setEnabled(false);
			setComponentAlignment(delete, Alignment.MIDDLE_LEFT);
		}

		private void formatAsOtherLine() {

			CssStyles.style(dateOfReport, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
			CssStyles.style(facility, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
			CssStyles.style(facilityDetails, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
			person.hideCaptions();
		}

		private void updateFacilityFields(ComboBox<FacilityReferenceDto> cbFacility, TextField tfFacilityDetails) {
			if (cbFacility.getValue() != null) {
				boolean otherHealthFacility = cbFacility.getValue().getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
				boolean noneHealthFacility = cbFacility.getValue().getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
				boolean visibleEnabledAndRequired = otherHealthFacility || noneHealthFacility;

				tfFacilityDetails.setVisible(visibleEnabledAndRequired);
				tfFacilityDetails.setEnabled(visibleEnabledAndRequired);

				if (otherHealthFacility) {
					tfFacilityDetails.setPlaceholder(I18nProperties.getCaption(Captions.caseFacilityDetailsShort));

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

		private boolean shouldShowEpidNumber() {
			ConfigFacade configFacade = FacadeProvider.getConfigFacade();
			return UiUtil.permitted(UserRight.CASE_CHANGE_EPID_NUMBER)
				&& !configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)
				&& !configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_SWITZERLAND);
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
		public static final String FACILITY_DETAILS = "facilityDetails";
		public static final String PERSON = "person";
		public static final String DATE_OF_ONSET = "dateOfOnset";
		public static final String FACILITY_TYPE_GROUP = "facilityTypeGroup";
		public static final String FACILITY_TYPE = "facilityType";

		private Disease disease;
		private String diseaseDetails;
		private RegionReferenceDto region;
		private DistrictReferenceDto district;
		private LocalDate dateOfReport;
		private String epidNumber;
		private CommunityReferenceDto community;
		private FacilityTypeGroup facilityTypeGroup;
		private FacilityType facilityType;
		private FacilityReferenceDto facility;
		private String facilityDetails;
		private PersonFieldDto person;
		private LocalDate dateOfOnset;

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

		public PersonFieldDto getPerson() {
			return person;
		}

		public void setPerson(PersonFieldDto person) {
			this.person = person;
		}

		public LocalDate getDateOfOnset() {
			return dateOfOnset;
		}

		public void setDateOfOnset(LocalDate dateOfOnset) {
			this.dateOfOnset = dateOfOnset;
		}

		public FacilityTypeGroup getFacilityTypeGroup() {
			return facilityTypeGroup;
		}

		public void setFacilityTypeGroup(FacilityTypeGroup facilityTypeGroup) {
			this.facilityTypeGroup = facilityTypeGroup;
		}

		public FacilityType getFacilityType() {
			return facilityType;
		}

		public void setFacilityType(FacilityType facilityType) {
			this.facilityType = facilityType;
		}
	}
}
