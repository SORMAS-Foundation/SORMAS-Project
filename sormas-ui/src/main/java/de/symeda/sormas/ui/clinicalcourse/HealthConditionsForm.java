package de.symeda.sormas.ui.clinicalcourse;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Arrays;

import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class HealthConditionsForm extends AbstractEditForm<HealthConditionsDto> {
		
		private static final long serialVersionUID = 1L;
		
	
	private static final String HTML_LAYOUT =
			h3(I18nProperties.getString(Strings.headingHealthConditions)) +
			fluidRow(
					fluidColumn(6, 0, locs(
							HealthConditionsDto.TUBERCULOSIS, HealthConditionsDto.ASPLENIA,
							HealthConditionsDto.HEPATITIS, HealthConditionsDto.DIABETES,
							HealthConditionsDto.IMMUNODEFICIENCY_OTHER_THAN_HIV,
							HealthConditionsDto.HIV, HealthConditionsDto.HIV_ART, 
							HealthConditionsDto.CONGENITAL_SYPHILIS, HealthConditionsDto.DOWN_SYNDROME)),
					fluidColumn(6, 0, locs(
							HealthConditionsDto.CHRONIC_LIVER_DISEASE, HealthConditionsDto.MALIGNANCY_CHEMOTHERAPY,
							HealthConditionsDto.CHRONIC_HEART_FAILURE, HealthConditionsDto.CHRONIC_PULMONARY_DISEASE, 
							HealthConditionsDto.CHRONIC_KIDNEY_DISEASE, HealthConditionsDto.CHRONIC_NEUROLOGIC_CONDITION,
							HealthConditionsDto.CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION))
					) +
			loc(HealthConditionsDto.OTHER_CONDITIONS);
	
	public HealthConditionsForm(UserRight editOrCreateUserRight) {
		super(HealthConditionsDto.class, HealthConditionsDto.I18N_PREFIX, editOrCreateUserRight);
	}
	
	@Override
	protected void addFields() {
		addFields(HealthConditionsDto.TUBERCULOSIS, HealthConditionsDto.ASPLENIA, HealthConditionsDto.HEPATITIS,
				HealthConditionsDto.DIABETES, HealthConditionsDto.HIV, HealthConditionsDto.HIV_ART,
				HealthConditionsDto.CHRONIC_LIVER_DISEASE, HealthConditionsDto.MALIGNANCY_CHEMOTHERAPY,
				HealthConditionsDto.CHRONIC_HEART_FAILURE, HealthConditionsDto.CHRONIC_PULMONARY_DISEASE,
				HealthConditionsDto.CHRONIC_KIDNEY_DISEASE, HealthConditionsDto.CHRONIC_NEUROLOGIC_CONDITION,
				HealthConditionsDto.DOWN_SYNDROME, HealthConditionsDto.CONGENITAL_SYPHILIS,
				HealthConditionsDto.IMMUNODEFICIENCY_OTHER_THAN_HIV, HealthConditionsDto.CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION);
		addField(HealthConditionsDto.OTHER_CONDITIONS, TextArea.class).setRows(3);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), HealthConditionsDto.HIV_ART, HealthConditionsDto.HIV, Arrays.asList(YesNoUnknown.YES), true);
	}
    
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}

}
