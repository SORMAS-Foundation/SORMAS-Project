package de.symeda.sormas.ui.clinicalcourse;

import java.util.Arrays;

import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class HealthConditionsForm extends AbstractEditForm<HealthConditionsDto> {
	
	private static final String HTML_LAYOUT =
			LayoutUtil.h3(I18nProperties.getString(Strings.headingHealthConditions)) +
			LayoutUtil.fluidRow(
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locs(
									HealthConditionsDto.TUBERCULOSIS, HealthConditionsDto.ASPLENIA,
									HealthConditionsDto.HEPATITIS, HealthConditionsDto.DIABETES,
									HealthConditionsDto.HIV, HealthConditionsDto.HIV_ART)),
					LayoutUtil.fluidColumn(6, 0, 
							LayoutUtil.locs(
									HealthConditionsDto.CHRONIC_LIVER_DISEASE, HealthConditionsDto.MALIGNANCY_CHEMOTHERAPY,
									HealthConditionsDto.CHRONIC_HEART_FAILURE, HealthConditionsDto.CHRONIC_PULMONARY_DISEASE, 
									HealthConditionsDto.CHRONIC_KIDNEY_DISEASE, HealthConditionsDto.CHRONIC_NEUROLOGIC_CONDITION))
					) +
			LayoutUtil.loc(HealthConditionsDto.OTHER_CONDITIONS);
	
	public HealthConditionsForm(UserRight editOrCreateUserRight) {
		super(HealthConditionsDto.class, HealthConditionsDto.I18N_PREFIX, editOrCreateUserRight);
	}
	
	@Override
	protected void addFields() {
		addFields(HealthConditionsDto.TUBERCULOSIS, HealthConditionsDto.ASPLENIA, HealthConditionsDto.HEPATITIS,
				HealthConditionsDto.DIABETES, HealthConditionsDto.HIV, HealthConditionsDto.HIV_ART,
				HealthConditionsDto.CHRONIC_LIVER_DISEASE, HealthConditionsDto.MALIGNANCY_CHEMOTHERAPY,
				HealthConditionsDto.CHRONIC_HEART_FAILURE, HealthConditionsDto.CHRONIC_PULMONARY_DISEASE,
				HealthConditionsDto.CHRONIC_KIDNEY_DISEASE, HealthConditionsDto.CHRONIC_NEUROLOGIC_CONDITION);
		addField(HealthConditionsDto.OTHER_CONDITIONS, TextArea.class).setRows(3);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), HealthConditionsDto.HIV_ART, HealthConditionsDto.HIV, Arrays.asList(YesNoUnknown.YES), true);
	}
    
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}

}
