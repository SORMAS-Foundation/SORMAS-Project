/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.caze;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

@WireMockTest(httpPort = 8888)
public class CaseExternalSurveillanceToolTest extends AbstractBeanTest {

	private UserReferenceDto reportingUser;
	private TestDataCreator.RDCF rdcf;

	@Override
	public void init() {
		super.init();
		rdcf = creator.createRDCF();
		reportingUser = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_OFFICER).toReference();
	}

	@BeforeEach
	public void setup(WireMockRuntimeInfo wireMockRuntime) {
		configureExternalSurvToolUrlForWireMock(wireMockRuntime);
	}

	@AfterEach
	public void teardown() {
		clearExternalSurvToolUrlForWireMock();
	}

	@Test
	public void testArchiveCasesSharedWithExternalSurvTool() {
		CaseDataDto caseNotShared = creator.createCase(reportingUser, creator.createPerson().toReference(), rdcf);

		CaseDataDto caseShared = creator.createCase(reportingUser, creator.createPerson().toReference(), rdcf);
		creator.createExternalShareInfo(caseShared.toReference(), reportingUser, ExternalShareStatus.SHARED, null);

		CaseDataDto caseMarkedToNotShare =
			creator.createCase(reportingUser, creator.createPerson().toReference(), rdcf, c -> c.setDontShareWithReportingTool(true));

		CaseDataDto caseSharedThenMarkedToNotShare =
			creator.createCase(reportingUser, creator.createPerson().toReference(), rdcf, c -> c.setDontShareWithReportingTool(true));
		creator.createExternalShareInfo(caseSharedThenMarkedToNotShare.toReference(), reportingUser, ExternalShareStatus.SHARED, null);

		CaseDataDto caseSharedThenDeletedInExternalSurvTool = creator.createCase(reportingUser, creator.createPerson().toReference(), rdcf);
		creator.createExternalShareInfo(caseSharedThenDeletedInExternalSurvTool.toReference(), reportingUser, ExternalShareStatus.SHARED, null);
		creator.createExternalShareInfo(caseSharedThenDeletedInExternalSurvTool.toReference(), reportingUser, ExternalShareStatus.DELETED, null);

		CaseDataDto caseReShared = creator.createCase(reportingUser, creator.createPerson().toReference(), rdcf);
		creator.createExternalShareInfo(caseReShared.toReference(), reportingUser, ExternalShareStatus.SHARED, null);
		creator.createExternalShareInfo(caseReShared.toReference(), reportingUser, ExternalShareStatus.DELETED, null);
		creator.createExternalShareInfo(caseReShared.toReference(), reportingUser, ExternalShareStatus.SHARED, null);

		stubFor(
			post(urlEqualTo("/export"))
				.withRequestBody(containing("caseUuids").and(containing(caseShared.getUuid())).and(containing(caseReShared.getUuid())))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		List<ProcessedEntity> result = getCaseFacade().archive(
			Arrays.asList(
				caseNotShared.getUuid(),
				caseShared.getUuid(),
				caseMarkedToNotShare.getUuid(),
				caseSharedThenMarkedToNotShare.getUuid(),
				caseSharedThenDeletedInExternalSurvTool.getUuid(),
				caseReShared.getUuid()));

		assertThat(result, hasSize(6));
		assertThat(
			result.stream().map(ProcessedEntity::getProcessedEntityStatus).collect(Collectors.toSet()),
			containsInAnyOrder(ProcessedEntityStatus.SUCCESS));
	}

	private void configureExternalSurvToolUrlForWireMock(WireMockRuntimeInfo wireMockRuntime) {
		MockProducer.getProperties().setProperty("survnet.url", String.format("http://localhost:%s", wireMockRuntime.getHttpPort()));
	}

	private void clearExternalSurvToolUrlForWireMock() {
		MockProducer.getProperties().setProperty("survnet.url", "");
	}

}
