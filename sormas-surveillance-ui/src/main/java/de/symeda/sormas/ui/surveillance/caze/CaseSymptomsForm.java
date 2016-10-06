package de.symeda.sormas.ui.surveillance.caze;

import com.vaadin.ui.NativeSelect;

import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseSymptomsForm extends AbstractEditForm<SymptomsDto> {

	private static final String HTML_LAYOUT = LayoutUtil.h3(CssStyles.VSPACE3, "Case symptoms")
			+ LayoutUtil.divCss(CssStyles.VSPACE3,
				LayoutUtil.fluidRowLocs(SymptomsDto.ONSET_DATE, "", SymptomsDto.TEMPERATURE, SymptomsDto.TEMPERATURE_SOURCE))
			+ LayoutUtil.fluidRow(
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locs(SymptomsDto.FEVER, SymptomsDto.VOMITING_NAUSEA, SymptomsDto.DIARRHEA,
									SymptomsDto.INTENSE_FATIGUE_WEAKNESS, SymptomsDto.ANOREXIA_APPETITE_LOSS,
									SymptomsDto.ABDOMINAL_PAIN, SymptomsDto.CHEST_PAIN, SymptomsDto.MUSCLE_PAIN,
									SymptomsDto.JOINT_PAIN, SymptomsDto.HEADACHE, SymptomsDto.COUGH,
									SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.DIFFICULTY_SWALLOWING,
									SymptomsDto.SORE_THROAT, SymptomsDto.JAUNDICE, SymptomsDto.CONJUNCTIVITIS,
									SymptomsDto.SKIN_RASH, SymptomsDto.HICCUPS, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE,
									SymptomsDto.COMA_UNCONSCIOUS, SymptomsDto.CONFUSED_DISORIENTED)),
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locs(SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.GUMS_BLEEDING,
									SymptomsDto.INJECTION_SITE_BLEEDING, SymptomsDto.EPISTAXIS, SymptomsDto.MELENA,
									SymptomsDto.HEMATEMESIS, SymptomsDto.DIGESTED_BLOOD_VOMIT, SymptomsDto.HEMOPTYSIS,
									SymptomsDto.BLEEDING_VAGINA, SymptomsDto.PETECHIAE, SymptomsDto.HEMATURIA,
									SymptomsDto.OTHER_HEMORRHAGIC, SymptomsDto.OTHER_HEMORRHAGIC_TEXT,
									SymptomsDto.OTHER_NON_HEMORRHAGIC, SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS)));

	public CaseSymptomsForm() {
		super(SymptomsDto.class, SymptomsDto.I18N_PREFIX);
	}

	@Override
	protected void addFields() {

		addField(SymptomsDto.ONSET_DATE);
		addField(SymptomsDto.TEMPERATURE, NativeSelect.class);
		addField(SymptomsDto.TEMPERATURE_SOURCE);

		addFields(SymptomsDto.FEVER, SymptomsDto.VOMITING_NAUSEA, SymptomsDto.DIARRHEA,
				SymptomsDto.INTENSE_FATIGUE_WEAKNESS, SymptomsDto.ANOREXIA_APPETITE_LOSS, SymptomsDto.ABDOMINAL_PAIN,
				SymptomsDto.CHEST_PAIN, SymptomsDto.MUSCLE_PAIN, SymptomsDto.JOINT_PAIN, SymptomsDto.HEADACHE,
				SymptomsDto.COUGH, SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.DIFFICULTY_SWALLOWING,
				SymptomsDto.SORE_THROAT, SymptomsDto.JAUNDICE, SymptomsDto.CONJUNCTIVITIS, SymptomsDto.SKIN_RASH,
				SymptomsDto.HICCUPS, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE, SymptomsDto.COMA_UNCONSCIOUS,
				SymptomsDto.CONFUSED_DISORIENTED);
		addFields(SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.GUMS_BLEEDING, SymptomsDto.INJECTION_SITE_BLEEDING,
				SymptomsDto.EPISTAXIS, SymptomsDto.MELENA, SymptomsDto.HEMATEMESIS, SymptomsDto.DIGESTED_BLOOD_VOMIT,
				SymptomsDto.HEMOPTYSIS, SymptomsDto.BLEEDING_VAGINA, SymptomsDto.PETECHIAE, SymptomsDto.HEMATURIA,
				SymptomsDto.OTHER_HEMORRHAGIC, SymptomsDto.OTHER_HEMORRHAGIC_TEXT, SymptomsDto.OTHER_NON_HEMORRHAGIC,
				SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS);

		setRequired(true, SymptomsDto.ONSET_DATE);
		// setReadOnly(true, );
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
