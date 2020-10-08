package de.symeda.sormas.ui.clinicalcourse;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class ClinicalCourseForm extends AbstractEditForm<ClinicalCourseDto> {

	private static final long serialVersionUID = 1L;

	private static final String HTML_LAYOUT = fluidRowLocs(ClinicalCourseDto.HEALTH_CONDITIONS);

	public ClinicalCourseForm(boolean isPseudonymized) {
		super(
			ClinicalCourseDto.class,
			ClinicalCourseDto.I18N_PREFIX,
			true,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.forSensitiveData(isPseudonymized));
	}

	@Override
	protected void addFields() {
		addField(ClinicalCourseDto.HEALTH_CONDITIONS, HealthConditionsForm.class).setCaption(null);

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
