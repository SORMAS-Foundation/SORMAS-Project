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

package de.symeda.sormas.api.externalmessage.processing.labmessage;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.AbstractMessageProcessingFlowBase;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus;

/**
 * Abstract class defining the flow of processing a lab message allowing to choose between multiple options like create or select a
 * case/contact/event participant and then create or update a sample with pathogen tests
 * The flow is coded in the `run` method.
 */
public abstract class AbstractLabMessageProcessingFlow extends AbstractMessageProcessingFlowBase {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final AbstractRelatedLabMessageHandler relatedLabMessageHandler;

	public AbstractLabMessageProcessingFlow(
		ExternalMessageDto externalMessage,
		UserDto user,
		ExternalMessageMapper mapper,
		ExternalMessageProcessingFacade processingFacade,
		AbstractRelatedLabMessageHandler relatedLabMessageHandler,
		Boolean forceSampleCreation) {
		super(user, externalMessage, mapper, processingFacade, forceSampleCreation);
		this.relatedLabMessageHandler = relatedLabMessageHandler;
	}

	public AbstractLabMessageProcessingFlow(
		ExternalMessageDto externalMessage,
		UserDto user,
		ExternalMessageMapper mapper,
		ExternalMessageProcessingFacade processingFacade,
		AbstractRelatedLabMessageHandler relatedLabMessageHandler) {
		this(externalMessage, user, mapper, processingFacade, relatedLabMessageHandler, false);
	}

	protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> doInitialSetup(
		ProcessingResult<ExternalMessageProcessingResult> previousResult) {
		if (relatedLabMessageHandler == null) {
			return super.doInitialSetup(previousResult);
		}
		return handleRelatedLabMessages(relatedLabMessageHandler, previousResult);
	}

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> handleRelatedLabMessages(
		AbstractRelatedLabMessageHandler relatedLabMessageHandler,
		ProcessingResult<ExternalMessageProcessingResult> previousResult) {

		if (relatedLabMessageHandler == null) {
			return previousResult.asCompletedFuture();
		}

		// TODO currently, related messages handling is just done if one sample report exists. That's why this works.
		SampleReportDto firstSampleReport = getExternalMessage().getSampleReportsNullSafe().get(0);
		return relatedLabMessageHandler.handle(getExternalMessage()).thenCompose(result -> {
			AbstractRelatedLabMessageHandler.HandlerResultStatus status = result.getStatus();
			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CANCELED) {
				logger.debug("[MESSAGE PROCESSING] Canceled while handling as a related message.");
				return ProcessingResult.withStatus(ProcessingResultStatus.CANCELED, previousResult.getData()).asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CANCELED_WITH_UPDATES) {
				logger.debug("[MESSAGE PROCESSING] Canceled while handling as a related message. But some updates were made.");
				return ProcessingResult.withStatus(ProcessingResultStatus.CANCELED_WITH_CORRECTIONS, previousResult.getData()).asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.HANDLED) {
				logger.debug("[MESSAGE PROCESSING] Processing done as a related message to another one.");
				SampleDto relatedSample = result.getSample();

				return ProcessingResult
					.of(
						ProcessingResultStatus.DONE,
						setPersonAssociationsOnResult(
							relatedSample,
							previousResult.getData()
								.withPerson(result.getPerson(), false)
								.andWithSampleAndPathogenTests(
									result.getSample(),
									getExternalMessageProcessingFacade().getPathogenTestsBySample(relatedSample.toReference()),
									firstSampleReport,
									false)))
					.asCompletedFuture();
			}

			if (status == AbstractRelatedLabMessageHandler.HandlerResultStatus.CONTINUE) {
				logger.debug("[MESSAGE PROCESSING] Processing done as a related message to another one and continue with the normal flow.");
				return ProcessingResult.continueWith(previousResult.getData()).asCompletedFuture();
			}

			return ProcessingResult.continueWith(previousResult.getData()).asCompletedFuture();
		});
	}

	private ExternalMessageProcessingResult setPersonAssociationsOnResult(SampleDto sample, ExternalMessageProcessingResult result) {
		if (sample.getAssociatedCase() != null) {
			return result.withSelectedCase(getExternalMessageProcessingFacade().getCaseDataByUuid(sample.getAssociatedCase().getUuid()));
		} else if (sample.getAssociatedContact() != null) {
			return result.withSelectedContact(getExternalMessageProcessingFacade().getContactByUuid(sample.getAssociatedContact().getUuid()));
		} else if (sample.getAssociatedEventParticipant() != null) {
			return result.withSelectedEventParticipant(
				getExternalMessageProcessingFacade().getEventParticipantByUuid(sample.getAssociatedEventParticipant().getUuid()));
		}

		return result;
	}

	protected void markExternalMessageAsProcessed(
		ExternalMessageDto externalMessage,
		ProcessingResult<ExternalMessageProcessingResult> result,
		SurveillanceReportDto surveillanceReport) {

		List<ExternalMessageProcessingResult.SampleSelection> relatedSampleReports = result.getData().getSamples();
		if (relatedSampleReports != null && !relatedSampleReports.isEmpty()) {
			relatedSampleReports.forEach(e -> e.getSampleReport().setSample(e.getEntity().toReference()));
		}
		if (surveillanceReport != null) {
			externalMessage.setSurveillanceReport(surveillanceReport.toReference());
		}
		externalMessage.setStatus(ExternalMessageStatus.PROCESSED);
		externalMessage.setChangeDate(new Date());
		getExternalMessageProcessingFacade().saveExternalMessage(externalMessage);
	}

	/**
	 * Post-processes a case after it has been created or updated based on the external message.
	 * This method has a special case for Pertusis in case of Luxembourg.
	 * 
	 * @param caseDto
	 *            The case data transfer object that has been created or updated.
	 * @param externalMessageDto
	 *            The external message data transfer object containing the lab message details.
	 */
	@Override
	protected void postBuildCase(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {

		if (getExternalMessageProcessingFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {

			// if any of the positive test reports from any sample is a CULTURE or PCR_RT_PCR test, set the case classification to CONFIRMED
			if (externalMessageDto.getSampleReports() != null && !externalMessageDto.getSampleReports().isEmpty()) {
				externalMessageDto.getSampleReports()
					.stream()
					.filter(Objects::nonNull)
					.map(SampleReportDto::getTestReports)
					.flatMap(List::stream)
					.filter(testReport -> testReport != null && PathogenTestResultType.POSITIVE.equals(testReport.getTestResult()))
					.map(TestReportDto::getTestType)
					.filter(testType -> PathogenTestType.CULTURE.equals(testType) || PathogenTestType.PCR_RT_PCR.equals(testType))
					.findAny()
					.ifPresent(testType -> caseDto.setCaseClassification(CaseClassification.CONFIRMED));
			}

			caseDto.setInvestigationStatus(InvestigationStatus.PENDING);
			caseDto.setOutcome(CaseOutcome.NO_OUTCOME);
		}
	}

	@Override
	protected void postBuildPerson(PersonDto personDto, ExternalMessageDto externalMessageDto) {
		// No specific post-processing for person data in this flow
	}

}
