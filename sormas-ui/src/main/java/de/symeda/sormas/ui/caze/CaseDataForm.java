package de.symeda.sormas.ui.caze;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
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
	public static final String NONE_HEALTH_FACILITY_DETAILS = "noneHealthFacilityDetails";
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE_3, "Case data")+
			
			LayoutUtil.divCss(CssStyles.VSPACE_4,
    				LayoutUtil.fluidRow(
    						LayoutUtil.loc(CaseDataDto.UUID), 
    						LayoutUtil.fluidRowLocs(CaseDataDto.REPORT_DATE, CaseDataDto.REPORTING_USER)) +
					LayoutUtil.fluidRowLocs(CaseDataDto.CASE_CLASSIFICATION) +
					LayoutUtil.fluidRowLocs(CaseDataDto.INVESTIGATION_STATUS) +
    				LayoutUtil.fluidRowLocs(CaseDataDto.EPID_NUMBER, CaseDataDto.DISEASE) +
    				LayoutUtil.fluidRowLocs("", CaseDataDto.DISEASE_DETAILS) +
    				LayoutUtil.fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT) +
		    		LayoutUtil.fluidRowLocs(CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY) +
    				LayoutUtil.fluidRowLocs("", CaseDataDto.HEALTH_FACILITY_DETAILS) +
    				LayoutUtil.fluidRowLocs(CaseDataDto.SURVEILLANCE_OFFICER, "")
		    )+
			LayoutUtil.loc(MEDICAL_INFORMATION_LOC) +
			LayoutUtil.fluidRow(
					LayoutUtil.fluidRowLocs(CaseDataDto.PREGNANT, "") +
					LayoutUtil.fluidRowLocs(CaseDataDto.MEASLES_VACCINATION, CaseDataDto.MEASLES_DOSES) +
					LayoutUtil.fluidRowLocs(CaseDataDto.MEASLES_VACCINATION_INFO_SOURCE, "") +
					LayoutUtil.fluidRowLocs(CaseDataDto.YELLOW_FEVER_VACCINATION, CaseDataDto.YELLOW_FEVER_VACCINATION_INFO_SOURCE)
			);
    	

    private final PersonDto person;
    private final Disease disease;

    public CaseDataForm(PersonDto person, Disease disease) {
        super(CaseDataDto.class, CaseDataDto.I18N_PREFIX);
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
    	
    	addField(CaseDataDto.CASE_CLASSIFICATION, OptionGroup.class);
    	addField(CaseDataDto.INVESTIGATION_STATUS, OptionGroup.class);
    	AbstractSelect diseaseField = addField(CaseDataDto.DISEASE, ComboBox.class);
    	TextField diseaseDetailsField = addField(CaseDataDto.DISEASE_DETAILS, TextField.class);
    	TextField healthFacilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
    	
    	addField(CaseDataDto.REGION, ComboBox.class);
    	addField(CaseDataDto.DISTRICT, ComboBox.class);
    	addField(CaseDataDto.COMMUNITY, ComboBox.class);
    	ComboBox facility = addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class);
    	
		ComboBox surveillanceOfficerField = addField(CaseDataDto.SURVEILLANCE_OFFICER, ComboBox.class);
		surveillanceOfficerField.setNullSelectionAllowed(true);
		
		addField(CaseDataDto.PREGNANT, OptionGroup.class);
		addField(CaseDataDto.MEASLES_VACCINATION, ComboBox.class);
		addField(CaseDataDto.MEASLES_DOSES, TextField.class);
		addField(CaseDataDto.MEASLES_VACCINATION_INFO_SOURCE, ComboBox.class);
		addField(CaseDataDto.YELLOW_FEVER_VACCINATION, ComboBox.class);
		addField(CaseDataDto.YELLOW_FEVER_VACCINATION_INFO_SOURCE, ComboBox.class);
    	
    	setRequired(true, CaseDataDto.CASE_CLASSIFICATION, CaseDataDto.INVESTIGATION_STATUS,
    			CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY);

    	setReadOnly(true, CaseDataDto.UUID, CaseDataDto.REPORT_DATE, CaseDataDto.REPORTING_USER,
    			CaseDataDto.INVESTIGATION_STATUS, CaseDataDto.REGION,
    			CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY);
    	if (!UserRole.isSupervisor(LoginHelper.getCurrentUserRoles())) {
    		setReadOnly(true, CaseDataDto.DISEASE);
    	}
    	
		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(CaseDataDto.class, (String)propertyId, disease);
			getFieldGroup().getField(propertyId).setVisible(visible);
		}
		
    	Sex personSex = person.getSex();
    	if (personSex != Sex.FEMALE) {
    		setVisible(false, CaseDataDto.PREGNANT);
    	}
		
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.MEASLES_DOSES, CaseDataDto.MEASLES_VACCINATION_INFO_SOURCE), 
				CaseDataDto.MEASLES_VACCINATION, Arrays.asList(Vaccination.VACCINATED), true);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.YELLOW_FEVER_VACCINATION_INFO_SOURCE),
				CaseDataDto.YELLOW_FEVER_VACCINATION, Arrays.asList(Vaccination.VACCINATED), true);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DISEASE_DETAILS), CaseDataDto.DISEASE, Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), CaseDataDto.DISEASE, Arrays.asList(CaseDataDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		
		List<String> medicalInformationFields = Arrays.asList(CaseDataDto.PREGNANT, CaseDataDto.MEASLES_VACCINATION, CaseDataDto.YELLOW_FEVER_VACCINATION);
		
		for (String medicalInformationField : medicalInformationFields) {
			if (getFieldGroup().getField(medicalInformationField).isVisible()) {
				String medicalInformationCaptionLayout = LayoutUtil.h3(CssStyles.VSPACE_3, "Additional medical information");
				Label medicalInformationCaptionLabel = new Label(medicalInformationCaptionLayout);
				medicalInformationCaptionLabel.setContentMode(ContentMode.HTML);
				getContent().addComponent(medicalInformationCaptionLabel, MEDICAL_INFORMATION_LOC);
				break;
			}
		}
		
		addValueChangeListener(e -> {
			diseaseField.addValueChangeListener(new DiseaseChangeListener(diseaseField, getValue().getDisease()));
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
					healthFacilityDetails.clear();
				}
			} else {
				healthFacilityDetails.setVisible(false);
				healthFacilityDetails.clear();
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
