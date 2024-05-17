package de.symeda.sormas.app.pathogentest.edit;

import java.util.Date;

import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.databinding.FragmentPathogenTestEditLayoutBinding;

public class PathogenTestValidator {

	static void initializePathogenTestValidation(final FragmentPathogenTestEditLayoutBinding contentBinding) {
		PathogenTest pathogenTest = contentBinding.getData();

		contentBinding.pathogenTestTestDateTime.setValidationCallback(() -> {
			Date testDate = contentBinding.pathogenTestTestDateTime.getValue();
			final Date sampleDate;
			final String sampleDateCaption;
			if (pathogenTest.getSample() != null) {
				sampleDate = pathogenTest.getSample().getSampleDateTime();
				sampleDateCaption = I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME);
			} else {
				sampleDate = pathogenTest.getEnvironmentSample().getSampleDateTime();
				sampleDateCaption = I18nProperties.getPrefixCaption(EnvironmentSampleDto.I18N_PREFIX, EnvironmentSampleDto.SAMPLE_DATE_TIME);
			}

			// Must not be after sample date
			if (DateHelper.isDateBefore(testDate, sampleDate)) {
				contentBinding.pathogenTestTestDateTime.enableErrorState(
					I18nProperties.getValidationError(
						Validations.afterDateWithDate,
						contentBinding.pathogenTestTestDateTime.getCaption(),
						sampleDateCaption,
						DateFormatHelper.formatLocalDateTime(sampleDate)));
				return true;
			}

			return false;

		});
	}
}
