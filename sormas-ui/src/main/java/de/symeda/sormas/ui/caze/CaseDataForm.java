package de.symeda.sormas.ui.caze;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DoneListener;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class CaseDataForm extends AbstractEditForm<CaseDataDto> {

	private static final String MEDICAL_INFORMATION_LOC = "medicalInformationLoc";
	private static final String SMALLPOX_VACCINATION_SCAR_IMG = "smallpoxVaccinationScarImg";

	public static final String NONE_HEALTH_FACILITY_DETAILS = "noneHealthFacilityDetails";

	private static final String HTML_LAYOUT = 
			LayoutUtil.h3("Case data")+
			LayoutUtil.fluidRow(
					LayoutUtil.oneOfTwoCol(CaseDataDto.UUID), 
					LayoutUtil.oneOfFourCol(CaseDataDto.REPORT_DATE),
					LayoutUtil.oneOfFourCol(CaseDataDto.REPORTING_USER)) +
			LayoutUtil.fluidRowLocs(CaseDataDto.CASE_CLASSIFICATION) +
			LayoutUtil.fluidRow(
					LayoutUtil.threeOfFourCol(CaseDataDto.INVESTIGATION_STATUS),
					LayoutUtil.oneOfFourCol(CaseDataDto.INVESTIGATED_DATE)
			) +
			LayoutUtil.fluidRow(
					LayoutUtil.threeOfFourCol(CaseDataDto.OUTCOME),
					LayoutUtil.oneOfFourCol(CaseDataDto.OUTCOME_DATE)
			) +
			LayoutUtil.fluidRowLocs(CaseDataDto.EPID_NUMBER, CaseDataDto.DISEASE) +
			// one or the other
			LayoutUtil.fluidRow("", LayoutUtil.locs(CaseDataDto.DISEASE_DETAILS, CaseDataDto.PLAGUE_TYPE)) +
			LayoutUtil.fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT) +
			LayoutUtil.fluidRowLocs(CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY) +
			LayoutUtil.fluidRowLocs("", CaseDataDto.HEALTH_FACILITY_DETAILS) +
			LayoutUtil.loc(MEDICAL_INFORMATION_LOC) +
			LayoutUtil.fluidRowLocs(CaseDataDto.PREGNANT, "") +
			LayoutUtil.fluidRowLocs(CaseDataDto.VACCINATION, CaseDataDto.VACCINATION_DOSES) +
			LayoutUtil.fluidRowLocs(CaseDataDto.VACCINATION_INFO_SOURCE, "") +
			LayoutUtil.fluidRowLocs(CaseDataDto.SMALLPOX_VACCINATION_RECEIVED, CaseDataDto.SMALLPOX_VACCINATION_DATE) +
			LayoutUtil.fluidRowLocs(CaseDataDto.SMALLPOX_VACCINATION_SCAR) +
			LayoutUtil.fluidRowLocs(SMALLPOX_VACCINATION_SCAR_IMG) +
			LayoutUtil.fluidRowLocs("", CaseDataDto.SURVEILLANCE_OFFICER)
			;


	private final PersonDto person;
	private final Disease disease;

	public CaseDataForm(PersonDto person, Disease disease, UserRight editOrCreateUserRight) {
		super(CaseDataDto.class, CaseDataDto.I18N_PREFIX, editOrCreateUserRight);
		this.person = person;
		this.disease = disease;
		addFields();
	}

	@Override
	protected void addFields() {
		if (person == null || disease == null) {
			return;
		}

		addFields(CaseDataDto.UUID, CaseDataDto.REPORT_DATE, CaseDataDto.REPORTING_USER);

		TextField epidField = addField(CaseDataDto.EPID_NUMBER, TextField.class);
		epidField.addValidator(new RegexpValidator(DataHelper.getEpidNumberRegexp(), true, 
				"The EPID number does not match the required pattern. You may still save the case and enter the correct number later."));
		epidField.addValidator(new StringLengthValidator("An EPID number has to be provided. You may still save the case and enter the correct number later.", 1, null, false));
		epidField.setInvalidCommitted(true);
		CssStyles.style(epidField, CssStyles.ERROR_COLOR_PRIMARY);

		addField(CaseDataDto.CASE_CLASSIFICATION, OptionGroup.class);
		addField(CaseDataDto.INVESTIGATION_STATUS, OptionGroup.class);
		DateField investigatedDate = addField(CaseDataDto.INVESTIGATED_DATE, DateField.class);
		investigatedDate.setDateFormat(DateHelper.getDateFormat().toPattern());
		addField(CaseDataDto.OUTCOME, OptionGroup.class);
		DateField outcomeDate = addField(CaseDataDto.OUTCOME_DATE, DateField.class);
		outcomeDate.setDateFormat(DateHelper.getDateFormat().toPattern());
		ComboBox diseaseField = addField(CaseDataDto.DISEASE, ComboBox.class);
		addField(CaseDataDto.DISEASE_DETAILS, TextField.class);
		OptionGroup plagueType = addField(CaseDataDto.PLAGUE_TYPE, OptionGroup.class);
		TextField healthFacilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);

		addField(CaseDataDto.REGION, ComboBox.class);
		ComboBox district = addField(CaseDataDto.DISTRICT, ComboBox.class);
		ComboBox community = addField(CaseDataDto.COMMUNITY, ComboBox.class);
		ComboBox facility = addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class);

		ComboBox surveillanceOfficerField = addField(CaseDataDto.SURVEILLANCE_OFFICER, ComboBox.class);
		surveillanceOfficerField.setNullSelectionAllowed(true);

		addField(CaseDataDto.PREGNANT, OptionGroup.class);
		ComboBox vaccination = addField(CaseDataDto.VACCINATION, ComboBox.class);
		TextField vaccinationDoses = addField(CaseDataDto.VACCINATION_DOSES, TextField.class);
		ComboBox vaccinationInfoSource = addField(CaseDataDto.VACCINATION_INFO_SOURCE, ComboBox.class);
		addField(CaseDataDto.SMALLPOX_VACCINATION_SCAR, OptionGroup.class);
		addField(CaseDataDto.SMALLPOX_VACCINATION_RECEIVED, OptionGroup.class);
		addField(CaseDataDto.SMALLPOX_VACCINATION_DATE, DateField.class);

		setRequired(true, CaseDataDto.CASE_CLASSIFICATION, CaseDataDto.INVESTIGATION_STATUS, CaseDataDto.OUTCOME, CaseDataDto.DISEASE, CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.HEALTH_FACILITY);
		FieldHelper.addSoftRequiredStyle(investigatedDate, outcomeDate, plagueType, community, surveillanceOfficerField);

		setReadOnly(true, CaseDataDto.UUID, CaseDataDto.REPORT_DATE, CaseDataDto.REPORTING_USER, CaseDataDto.REGION,
				CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY);

		setReadOnly(!LoginHelper.hasUserRight(UserRight.CASE_CHANGE_DISEASE), CaseDataDto.DISEASE);
		setReadOnly(!LoginHelper.hasUserRight(UserRight.CASE_INVESTIGATE), CaseDataDto.INVESTIGATION_STATUS, CaseDataDto.INVESTIGATED_DATE);
		setReadOnly(!LoginHelper.hasUserRight(UserRight.CASE_CLASSIFY), CaseDataDto.CASE_CLASSIFICATION, CaseDataDto.OUTCOME, CaseDataDto.OUTCOME_DATE);

		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(CaseDataDto.class, (String)propertyId, disease);
			getFieldGroup().getField(propertyId).setVisible(visible);
		}

		setVisible(person.getSex() == Sex.FEMALE, CaseDataDto.PREGNANT);

		if (vaccination.getValue() != Vaccination.VACCINATED) {
			vaccinationDoses.setVisible(false);
			vaccinationInfoSource.setVisible(false);
		}
		
		vaccination.addValueChangeListener(e -> {
			if (e.getProperty().getValue() == Vaccination.VACCINATED) {
				vaccinationDoses.setVisible(DiseasesConfiguration.isDefinedOrMissing(CaseDataDto.class, CaseDataDto.VACCINATION_DOSES, disease));
				vaccinationInfoSource.setVisible(DiseasesConfiguration.isDefinedOrMissing(CaseDataDto.class, CaseDataDto.VACCINATION_INFO_SOURCE, disease));
			} else {
				vaccinationDoses.setVisible(false);
				vaccinationDoses.clear();
				vaccinationInfoSource.setVisible(false);
				vaccinationInfoSource.clear();
			}
		});

		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DISEASE_DETAILS), CaseDataDto.DISEASE, Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), CaseDataDto.DISEASE, Arrays.asList(CaseDataDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.PLAGUE_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.PLAGUE), true);

		if (disease == Disease.MONKEYPOX) {
			Image smallpoxVaccinationScarImg = new Image(null, new ThemeResource("img/smallpox-vaccination-scar.jpg"));
			CssStyles.style(smallpoxVaccinationScarImg, CssStyles.VSPACE_3);
			getContent().addComponent(smallpoxVaccinationScarImg, SMALLPOX_VACCINATION_SCAR_IMG);
			// Set up initial image visibility
			getContent().getComponent(SMALLPOX_VACCINATION_SCAR_IMG).setVisible(
					getFieldGroup().getField(CaseDataDto.SMALLPOX_VACCINATION_RECEIVED).getValue() == YesNoUnknown.YES);

			// Set up image visibility listener
			getFieldGroup().getField(CaseDataDto.SMALLPOX_VACCINATION_RECEIVED).addValueChangeListener(e -> {
				getContent().getComponent(SMALLPOX_VACCINATION_SCAR_IMG).setVisible(e.getProperty().getValue() == YesNoUnknown.YES);
			});
		}

		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.SMALLPOX_VACCINATION_SCAR, CaseDataDto.SMALLPOX_VACCINATION_DATE), CaseDataDto.SMALLPOX_VACCINATION_RECEIVED, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), CaseDataDto.OUTCOME_DATE, CaseDataDto.OUTCOME, Arrays.asList(CaseOutcome.DECEASED, CaseOutcome.RECOVERED, CaseOutcome.UNKNOWN), true);
		
		List<String> medicalInformationFields = Arrays.asList(CaseDataDto.PREGNANT, CaseDataDto.VACCINATION, CaseDataDto.SMALLPOX_VACCINATION_RECEIVED);

		for (String medicalInformationField : medicalInformationFields) {
			if (getFieldGroup().getField(medicalInformationField).isVisible()) {
				String medicalInformationCaptionLayout = LayoutUtil.h3("Additional medical information");
				Label medicalInformationCaptionLabel = new Label(medicalInformationCaptionLayout);
				medicalInformationCaptionLabel.setContentMode(ContentMode.HTML);
				getContent().addComponent(medicalInformationCaptionLabel, MEDICAL_INFORMATION_LOC);
				break;
			}
		}

		addValueChangeListener(e -> {
			diseaseField.addValueChangeListener(new DiseaseChangeListener(diseaseField, getValue().getDisease()));
		});
		
		district.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			List<UserReferenceDto> assignableSurveillanceOfficers = FacadeProvider.getUserFacade().getAssignableUsersByDistrict(districtDto, false, UserRole.SURVEILLANCE_OFFICER);
			FieldHelper.updateItems(surveillanceOfficerField, assignableSurveillanceOfficers);
		});

		facility.addValueChangeListener(e -> {
			if (facility.getValue() != null) {
				boolean otherHealthFacility = ((FacilityReferenceDto) facility.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
				boolean noneHealthFacility = ((FacilityReferenceDto) facility.getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
				boolean detailsVisible = otherHealthFacility || noneHealthFacility;

				healthFacilityDetails.setVisible(detailsVisible);

				if (otherHealthFacility) {
					healthFacilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
				}
				if (noneHealthFacility) {
					healthFacilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, NONE_HEALTH_FACILITY_DETAILS));
				}
				if (!detailsVisible) {
					if (!healthFacilityDetails.isReadOnly()) {
						healthFacilityDetails.clear();
					}
				}
			} else {
				healthFacilityDetails.setVisible(false);
				if (!healthFacilityDetails.isReadOnly()) {
					healthFacilityDetails.clear();
				}
			}			
		});		
	}

	@Override 
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	private static class DiseaseChangeListener implements ValueChangeListener {

		private AbstractSelect diseaseField;
		private Disease currentDisease;

		DiseaseChangeListener(AbstractSelect diseaseField, Disease currentDisease) {
			this.diseaseField = diseaseField;
			this.currentDisease = currentDisease;
		}

		@Override
		public void valueChange(Property.ValueChangeEvent e) {

			if (diseaseField.getValue() != currentDisease) {
				ConfirmationComponent confirmDiseaseChangeComponent = new ConfirmationComponent(false) {
					private static final long serialVersionUID = 1L;
					@Override
					protected void onConfirm() {
						diseaseField.removeValueChangeListener(DiseaseChangeListener.this);
					}
					@Override
					protected void onCancel() {
						diseaseField.setValue(currentDisease);
					}
				};
				confirmDiseaseChangeComponent.getConfirmButton().setCaption("Really change case disease?");
				confirmDiseaseChangeComponent.getCancelButton().setCaption("Cancel");
				confirmDiseaseChangeComponent.setMargin(true);

				Window popupWindow = VaadinUiUtil.showPopupWindow(confirmDiseaseChangeComponent);
				CloseListener closeListener = new CloseListener() {
					@Override
					public void windowClose(CloseEvent e) {
						diseaseField.setValue(currentDisease);
					}
				};
				popupWindow.addCloseListener(closeListener);
				confirmDiseaseChangeComponent.addDoneListener(new DoneListener() {
					public void onDone() {
						popupWindow.removeCloseListener(closeListener);
						popupWindow.close();
					}
				});
				popupWindow.setCaption("Change case disease");       
			}
		}
	}
}
