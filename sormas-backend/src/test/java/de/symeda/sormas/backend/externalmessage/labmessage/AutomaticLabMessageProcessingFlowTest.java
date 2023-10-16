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

package de.symeda.sormas.backend.externalmessage.labmessage;

import static de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus.CANCELED;
import static de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus.DONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.RelatedSamplesReportsAndPathogenTests;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class AutomaticLabMessageProcessingFlowTest extends AbstractBeanTest {

	private AutomaticLabMessageProcessingFlow flow;

	private TestDataCreator.RDCF rdcf;

	@Override
	public void init() {
		super.init();

		flow = getAutomaticLabMessageProcessingFlow();
		rdcf = creator.createRDCF();
	}

	@Test
	public void testProcessCanceledForEmptyMessage() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = new ExternalMessageDto();

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(externalMessage);

		assertThat(result.getStatus(), is(CANCELED));
	}

	@Test
	public void testProcessWithNewData() throws ExecutionException, InterruptedException {
		ExternalMessageDto externalMessage = new ExternalMessageDto();
		externalMessage.setDisease(Disease.CORONAVIRUS);
		externalMessage.setPersonFirstName("John");
		externalMessage.setPersonLastName("Doe");
		externalMessage.setPersonSex(Sex.MALE);
		externalMessage.setPersonFacility(rdcf.facility);

		ProcessingResult<RelatedSamplesReportsAndPathogenTests> result = runFlow(externalMessage);

		assertThat(result.getStatus(), is(DONE));
	}

	private ProcessingResult<RelatedSamplesReportsAndPathogenTests> runFlow(ExternalMessageDto labMessage)
		throws ExecutionException, InterruptedException {

		return flow.run(labMessage).toCompletableFuture().get();
	}
}
