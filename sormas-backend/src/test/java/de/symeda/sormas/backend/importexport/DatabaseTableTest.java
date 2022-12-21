/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.importexport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.importexport.DatabaseTableType;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class DatabaseTableTest {

	@Test
	public void testCasesAndVisitsEnabled() {

		List<FeatureConfigurationDto> caseFeatureConfigurations =
			Arrays.asList(getFeatureConfiguration(FeatureType.CASE_SURVEILANCE, true), getFeatureConfiguration(FeatureType.CASE_FOLLOWUP, true));

		assertThat(DatabaseTable.CASES.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.HOSPITALIZATIONS.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.PREVIOUSHOSPITALIZATIONS.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.PORT_HEALTH_INFO.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.MATERNAL_HISTORIES.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.EPIDATA.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.EXPOSURES.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.ACTIVITIES_AS_CASE.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.HEALTH_CONDITIONS.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.SYMPTOMS.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.VISITS.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(true));

		assertThat(DatabaseTable.CONTACTS.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(false));
		assertThat(DatabaseTable.IMMUNIZATIONS.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(false));
		assertThat(DatabaseTable.SAMPLES.isEnabled(caseFeatureConfigurations, new ConfigFacadeEjb()), is(false));
	}

	@Test
	public void testPersonsEnabled() {

		List<FeatureConfigurationDto> personFeatureConfigurations = Arrays.asList(
			getFeatureConfiguration(FeatureType.CASE_SURVEILANCE, true),
			getFeatureConfiguration(FeatureType.EVENT_SURVEILLANCE, true),
			getFeatureConfiguration(FeatureType.CONTACT_TRACING, true));

		assertThat(DatabaseTable.PERSONS.isEnabled(personFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.PERSON_CONTACT_DETAILS.isEnabled(personFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.PERSON_LOCATIONS.isEnabled(personFeatureConfigurations, new ConfigFacadeEjb()), is(true));
	}

	@Test
	public void testCampaignsEnabled() {

		List<FeatureConfigurationDto> campaignFeatureConfigurations = Collections.singletonList(getFeatureConfiguration(FeatureType.CAMPAIGNS, true));

		assertThat(DatabaseTable.CAMPAIGNS.isEnabled(campaignFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.CAMPAIGN_FORM_META.isEnabled(campaignFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.CAMPAIGN_FORM_DATA.isEnabled(campaignFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.CAMPAIGN_DIAGRAM_DEFINITIONS.isEnabled(campaignFeatureConfigurations, new ConfigFacadeEjb()), is(true));

	}

	@Test
	public void testTasksEnabled() {

		List<FeatureConfigurationDto> taskFeatureConfigurations =
			Collections.singletonList(getFeatureConfiguration(FeatureType.TASK_MANAGEMENT, true));

		assertThat(DatabaseTable.TASKS.isEnabled(taskFeatureConfigurations, new ConfigFacadeEjb()), is(true));
		assertThat(DatabaseTable.TASK_OBSERVER.isEnabled(taskFeatureConfigurations, new ConfigFacadeEjb()), is(true));
	}

	@Test
	public void testInfrastructureTablesEnabled() {

		Arrays.stream(DatabaseTable.values()).filter(t -> t.getDatabaseTableType() == DatabaseTableType.INFRASTRUCTURE).forEach(table -> {
			if (table != DatabaseTable.AREAS) {
				assertThat(
					table.name() + " should be enabled without any feature configuration",
					table.isEnabled(Collections.emptyList(), new ConfigFacadeEjb()),
					is(true));
			} else {
				assertThat(
					table.name() + " should not be enabled without feature configuration",
					table.isEnabled(Collections.emptyList(), new ConfigFacadeEjb()),
					is(false));
			}
		});
	}

	@Test
	public void testS2sTablesEnabled() {

		ConfigFacadeEjb configFacadeMock = Mockito.mock(ConfigFacadeEjb.class);
		Mockito.when(configFacadeMock.isS2SConfigured()).thenReturn(true);
		Mockito.when(configFacadeMock.isExternalSurveillanceToolGatewayConfigured()).thenReturn(false);

		assertThat(DatabaseTable.SORMAS_TO_SORMAS_SHARE_REQUESTS.isEnabled(Collections.emptyList(), configFacadeMock), is(true));
		assertThat(DatabaseTable.SORMAS_TO_SORMAS_ORIGIN_INFO.isEnabled(Collections.emptyList(), configFacadeMock), is(true));
		assertThat(DatabaseTable.SORMAS_TO_SORMAS_SHARE_INFO.isEnabled(Collections.emptyList(), configFacadeMock), is(true));
		assertThat(DatabaseTable.SHARE_REQUEST_INFO.isEnabled(Collections.emptyList(), configFacadeMock), is(true));
		assertThat(DatabaseTable.EXTERNAL_SHARE_INFO.isEnabled(Collections.emptyList(), configFacadeMock), is(false));
	}

	private FeatureConfigurationDto getFeatureConfiguration(FeatureType featureType, boolean enabled) {
		FeatureConfigurationDto featureConfiguration = new FeatureConfigurationDto();
		featureConfiguration.setFeatureType(featureType);
		featureConfiguration.setEnabled(enabled);

		return featureConfiguration;
	}
}
