package de.symeda.sormas.ui.clinicalcourse;

import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class ClinicalCourseForm extends AbstractEditForm<ClinicalCourseDto> {

	private static final String HTML_LAYOUT =
			LayoutUtil.fluidRowLocs(ClinicalCourseDto.HEALTH_CONDITIONS);
	
	public ClinicalCourseForm(UserRight editOrCreateUserRight) {
		super(ClinicalCourseDto.class, ClinicalCourseDto.I18N_PREFIX, editOrCreateUserRight);
	}
	
	@Override
	protected void addFields() {
		addField(ClinicalCourseDto.HEALTH_CONDITIONS, HealthConditionsForm.class).setCaption(null);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
