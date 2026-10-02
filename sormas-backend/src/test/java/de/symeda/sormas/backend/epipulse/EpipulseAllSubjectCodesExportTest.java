/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package de.symeda.sormas.backend.epipulse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;

/**
 * Smoke tests all subject-code exports against an empty database.
 */
public class EpipulseAllSubjectCodesExportTest extends AbstractEpipulseExportTest {

	private Date periodStart;
	private Date periodEnd;

	@BeforeEach
	public void setUpAllSubjectCodes() {

		configureLuxembourgFor(EpipulseSubjectCode.values());

		Date today = new Date();
		periodStart = DateHelper.subtractDays(today, 30);
		periodEnd = DateHelper.addDays(today, 1);
	}

	@Test
	@DisplayName("every subject code assembles a query PostgreSQL accepts")
	public void everySubjectCodeAssemblesAValidQuery() {

		for (EpipulseSubjectCode subjectCode : EpipulseSubjectCode.values()) {

			ExportedEntries export = runExport(subjectCode, periodStart, periodEnd);

			assertThat(subjectCode + " selected cases from an empty database", export.all(), is(empty()));
		}
	}

	/**
	 * Verifies all exports also run when incubation periods are disabled.
	 */
	@Test
	@DisplayName("every subject code exports when its disease has no incubation period configured")
	public void everySubjectCodeExportsWithoutAnIncubationPeriod() {

		Set<Disease> diseases = Arrays.stream(EpipulseSubjectCode.values()).map(EpipulseSubjectCode::getDisease).collect(Collectors.toSet());

		setIncubationPeriodEnabled(diseases, false);
		try {
			for (EpipulseSubjectCode subjectCode : EpipulseSubjectCode.values()) {

				ExportedEntries export = runExport(subjectCode, periodStart, periodEnd);

				assertThat(subjectCode + " selected cases from an empty database", export.all(), is(empty()));
			}
		} finally {
			setIncubationPeriodEnabled(diseases, true);
		}
	}

	private void setIncubationPeriodEnabled(Set<Disease> diseases, boolean enabled) {

		for (Disease disease : diseases) {
			DiseaseConfigurationDto configuration =
				DiseaseConfigurationFacadeEjb.toDto(getDiseaseConfigurationService().getDiseaseConfiguration(disease));
			configuration.setIncubationPeriodEnabled(enabled);
			getDiseaseConfigurationFacade().saveDiseaseConfiguration(configuration);
		}

		getBean(DiseaseConfigurationFacadeEjbLocal.class).loadData();
	}
}
