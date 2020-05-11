package de.symeda.sormas.ui.clinicalcourse;

import static de.symeda.sormas.api.clinicalcourse.HealthConditionsDto.*;
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
									TUBERCULOSIS, ASPLENIA, HEPATITIS, DIABETES, IMMUNODEFICIENCY_OTHER_THAN_HIV, HIV,
									HIV_ART, CONGENITAL_SYPHILIS, DOWN_SYNDROME, CHRONIC_LIVER_DISEASE,
									MALIGNANCY_CHEMOTHERAPY)),
							fluidColumn(6, 0, locs(
									CHRONIC_HEART_FAILURE, CHRONIC_PULMONARY_DISEASE, CHRONIC_KIDNEY_DISEASE,
									CHRONIC_NEUROLOGIC_CONDITION, CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION,
									OBESITY, CURRENT_SMOKER, FORMER_SMOKER, ASTHMA, SICKLE_CELL_DISEASE))
					) +
					loc(OTHER_CONDITIONS);
	
	public HealthConditionsForm(UserRight editOrCreateUserRight) {
		super(HealthConditionsDto.class, I18N_PREFIX, editOrCreateUserRight);
	}
	
	@Override
	protected void addFields() {
		addFields(TUBERCULOSIS, ASPLENIA, HEPATITIS, DIABETES, HIV, HIV_ART, CHRONIC_LIVER_DISEASE,
				MALIGNANCY_CHEMOTHERAPY, CHRONIC_HEART_FAILURE, CHRONIC_PULMONARY_DISEASE,
				CHRONIC_KIDNEY_DISEASE, CHRONIC_NEUROLOGIC_CONDITION, DOWN_SYNDROME, CONGENITAL_SYPHILIS,
				IMMUNODEFICIENCY_OTHER_THAN_HIV, CARDIOVASCULAR_DISEASE_INCLUDING_HYPERTENSION, OBESITY,
				CURRENT_SMOKER, FORMER_SMOKER, ASTHMA, SICKLE_CELL_DISEASE);
		addField(OTHER_CONDITIONS, TextArea.class).setRows(3);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), HIV_ART, HIV, Arrays.asList(YesNoUnknown.YES), true);
	}
    
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}

}
