package de.symeda.sormas.ui.hospitalization;

import java.util.Arrays;
import java.util.Collection;

import com.vaadin.server.UserError;
import com.vaadin.ui.DateField;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.ViewMode;

@SuppressWarnings("serial")
public class CaseHospitalizationForm extends AbstractEditForm<HospitalizationDto> {
	
	private static final String HEALTH_FACILITY = "healthFacility";	
	private final CaseDataDto caze;
	private final ViewMode viewMode;
	
	private static final String HTML_LAYOUT = 
			LayoutUtil.h3("Hospitalization data") +
			LayoutUtil.fluidRowLocs(HEALTH_FACILITY, "") +
			LayoutUtil.fluidRowLocs(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY, "") +
			LayoutUtil.fluidRowLocs(HospitalizationDto.ADMISSION_DATE, HospitalizationDto.DISCHARGE_DATE) +
			LayoutUtil.fluidRowLocs(HospitalizationDto.ISOLATED, HospitalizationDto.ISOLATION_DATE) +
			LayoutUtil.fluidRowLocsCss(CssStyles.VSPACE_TOP_3, HospitalizationDto.HOSPITALIZED_PREVIOUSLY) +
			LayoutUtil.fluidRowLocs(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS)
	;		
	
	public CaseHospitalizationForm(CaseDataDto caze, UserRight editOrCreateUserRight, ViewMode viewMode) {
		super(HospitalizationDto.class, HospitalizationDto.I18N_PREFIX, editOrCreateUserRight);
		this.caze = caze;
		this.viewMode = viewMode;
		addFields();
	}
	
	@Override
	protected void addFields() {
		if (caze == null || viewMode == null) {
			return;
		}
		
		TextField facilityField = addCustomField(HEALTH_FACILITY, FacilityReferenceDto.class, TextField.class);
		facilityField.setValue(caze.getHealthFacility().toString());
		facilityField.setReadOnly(true);
		
		addField(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY, OptionGroup.class);
		DateField admissionDate = addField(HospitalizationDto.ADMISSION_DATE, DateField.class);
		admissionDate.setDateFormat(DateHelper.getShortDateFormat().toPattern());
		DateField dischargeDate = addField(HospitalizationDto.DISCHARGE_DATE, DateField.class);
		dischargeDate.setDateFormat(DateHelper.getShortDateFormat().toPattern());
		addField(HospitalizationDto.ISOLATED, OptionGroup.class);
		addField(HospitalizationDto.ISOLATION_DATE, DateField.class);
		OptionGroup hospitalizedPreviouslyField = addField(HospitalizationDto.HOSPITALIZED_PREVIOUSLY, OptionGroup.class);
		CssStyles.style(hospitalizedPreviouslyField, CssStyles.ERROR_COLOR_PRIMARY);
		PreviousHospitalizationsField previousHospitalizationsField = addField(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS, PreviousHospitalizationsField.class);

		initializeVisibilitiesAndAllowedVisibilities(null, viewMode);
		
		if (isVisibleAllowed(HospitalizationDto.ISOLATED)) {
			FieldHelper.setVisibleWhen(getFieldGroup(), HospitalizationDto.ISOLATED, HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY, Arrays.asList(YesNoUnknown.YES), true);
		}
		if (isVisibleAllowed(HospitalizationDto.DISCHARGE_DATE)) {
			FieldHelper.setVisibleWhen(getFieldGroup(), HospitalizationDto.DISCHARGE_DATE, HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY, Arrays.asList(YesNoUnknown.YES), true);
		}
		if (isVisibleAllowed(HospitalizationDto.ISOLATION_DATE)) {
			FieldHelper.setVisibleWhen(getFieldGroup(), HospitalizationDto.ISOLATION_DATE, HospitalizationDto.ISOLATED, Arrays.asList(YesNoUnknown.YES), true);
		}
		if (isVisibleAllowed(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS)) {
			FieldHelper.setVisibleWhen(getFieldGroup(), HospitalizationDto.PREVIOUS_HOSPITALIZATIONS, HospitalizationDto.HOSPITALIZED_PREVIOUSLY, Arrays.asList(YesNoUnknown.YES), true);
		}
		
		hospitalizedPreviouslyField.addValueChangeListener(e -> {
			updatePrevHospHint(hospitalizedPreviouslyField, previousHospitalizationsField);
		});
		previousHospitalizationsField.addValueChangeListener(e -> {
			updatePrevHospHint(hospitalizedPreviouslyField, previousHospitalizationsField);
		});
	}
	
	private void updatePrevHospHint(OptionGroup hospitalizedPreviouslyField, PreviousHospitalizationsField previousHospitalizationsField) {
		YesNoUnknown value = (YesNoUnknown) hospitalizedPreviouslyField.getValue();
		Collection<PreviousHospitalizationDto> previousHospitalizations = previousHospitalizationsField.getValue();
		if (LoginHelper.hasUserRight(UserRight.CASE_EDIT) && value == YesNoUnknown.YES && (previousHospitalizations == null || previousHospitalizations.size() == 0)) {
			hospitalizedPreviouslyField.setComponentError(new UserError("Please add an entry to the list below if there is any data available to you."));
		} else {
			hospitalizedPreviouslyField.setComponentError(null);
		}
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
