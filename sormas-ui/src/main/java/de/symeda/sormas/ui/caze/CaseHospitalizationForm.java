package de.symeda.sormas.ui.caze;

import java.util.Arrays;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.OptionGroup;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.HospitalizationDto;
import de.symeda.sormas.api.caze.YesNoUnknown;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.ui.fields.PreviousHospitalizationsField;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseHospitalizationForm extends AbstractEditForm<HospitalizationDto> {
	
	private static final String HEALTH_FACILITY = "healthFacility";	
	private final CaseDataDto caze;
	
	private static final String HTML_LAYOUT = 
			LayoutUtil.h3(CssStyles.VSPACE3, "Hospitalization data") +
			LayoutUtil.fluidRowLocs(HEALTH_FACILITY, "") +
			LayoutUtil.fluidRowLocs(HospitalizationDto.ADMISSION_DATE, HospitalizationDto.DISCHARGE_DATE) +
			LayoutUtil.fluidRowLocsCss(CssStyles.VSPACE3, HospitalizationDto.ISOLATED, HospitalizationDto.ISOLATION_DATE) +
			LayoutUtil.fluidRowLocs(HospitalizationDto.HOSPITALIZED_PREVIOUSLY) +
			LayoutUtil.fluidRowLocs(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS)
	;		
	
	public CaseHospitalizationForm(CaseDataDto caze) {
		super(HospitalizationDto.class, HospitalizationDto.I18N_PREFIX);
		this.caze = caze;
		addFields();
	}
	
	@Override
	protected void addFields() {
		if (caze == null) {
			return;
		}
		
		ComboBox facilityField = addCustomField(HEALTH_FACILITY, FacilityReferenceDto.class, ComboBox.class);
		facilityField.addItem(caze.getHealthFacility());
		facilityField.setValue(caze.getHealthFacility());
		facilityField.setReadOnly(true);
		
		addField(HospitalizationDto.ADMISSION_DATE, DateField.class);
		addField(HospitalizationDto.DISCHARGE_DATE, DateField.class);
		addField(HospitalizationDto.ISOLATED, OptionGroup.class);
		addField(HospitalizationDto.ISOLATION_DATE, DateField.class);
		addField(HospitalizationDto.HOSPITALIZED_PREVIOUSLY, OptionGroup.class);
		addField(HospitalizationDto.PREVIOUS_HOSPITALIZATIONS, PreviousHospitalizationsField.class);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), HospitalizationDto.ISOLATION_DATE, HospitalizationDto.ISOLATED, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), HospitalizationDto.PREVIOUS_HOSPITALIZATIONS, HospitalizationDto.HOSPITALIZED_PREVIOUSLY, Arrays.asList(YesNoUnknown.YES), true);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
