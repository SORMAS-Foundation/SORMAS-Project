/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.entities.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.entities.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.entities.immunization.SormasToSormasImmunizationDto;
import de.symeda.sormas.api.sormastosormas.entities.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.entities.surveillancereport.SormasToSormasSurveillanceReportDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.sormastosormas.ValidationHelper;
import de.symeda.sormas.backend.sormastosormas.entities.caze.ReceivedCaseProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.contact.ReceivedContactProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.event.ReceivedEventProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.eventparticipant.ReceivedEventParticipantProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.immunization.ReceivedImmunizationProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.sample.ReceivedSampleProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.surveillancereport.ReceivedSurveillanceReportProcessor;
import de.symeda.sormas.backend.sormastosormas.share.ShareRequestData;

@Stateless
@LocalBean
public class ReceivedEntitiesProcessor {

	@EJB
	private ReceivedCaseProcessor caseProcessor;
	@EJB
	private ReceivedContactProcessor contactProcessor;
	@EJB
	private ReceivedSampleProcessor sampleProcessor;
	@EJB
	private ReceivedEventProcessor eventProcessor;
	@EJB
	private ReceivedEventParticipantProcessor eventParticipantProcessor;
	@EJB
	private ReceivedImmunizationProcessor immunizationProcessor;
	@EJB
	private ReceivedSurveillanceReportProcessor surveillanceReportProcessor;

	public List<ValidationErrors> processReceivedData(SormasToSormasDto receivedData, ShareDataExistingEntities existingEntities) {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		SormasToSormasOriginInfoDto originInfo = receivedData.getOriginInfo();

		originInfo.setUuid(DataHelper.createUuid());
		originInfo.setChangeDate(new Date());

		ValidationErrors originInfoErrors = validateOriginInfo(originInfo, Captions.sormasToSormasOriginInfo);
		if (originInfoErrors.hasError()) {
			validationErrors.add(originInfoErrors);
		}

		List<SormasToSormasCaseDto> cases = receivedData.getCases();
		if (CollectionUtils.isNotEmpty(cases)) {
			cases.forEach(c -> {
				ValidationErrors caseErrors =
					caseProcessor.processReceivedData(c, existingEntities.getCases().get(c.getEntity().getUuid()), originInfo);

				if (caseErrors.hasError()) {
					validationErrors.add(new ValidationErrors(ValidationHelper.buildCaseValidationGroupName(c.getEntity()), caseErrors));
				}
			});
		}

		List<SormasToSormasContactDto> contacts = receivedData.getContacts();
		if (CollectionUtils.isNotEmpty(contacts)) {
			contacts.forEach(c -> {
				ValidationErrors contactErrors =
					contactProcessor.processReceivedData(c, existingEntities.getContacts().get(c.getEntity().getUuid()), originInfo);

				if (contactErrors.hasError()) {
					validationErrors.add(new ValidationErrors(ValidationHelper.buildContactValidationGroupName(c.getEntity()), contactErrors));
				}
			});
		}

		List<SormasToSormasEventDto> events = receivedData.getEvents();
		if (CollectionUtils.isNotEmpty(events)) {
			events.forEach(e -> {
				ValidationErrors eventErrors =
					eventProcessor.processReceivedData(e, existingEntities.getEvents().get(e.getEntity().getUuid()), originInfo);

				if (eventErrors.hasError()) {
					validationErrors.add(new ValidationErrors(ValidationHelper.buildEventValidationGroupName(e.getEntity()), eventErrors));
				}
			});
		}

		List<SormasToSormasEventParticipantDto> eventParticipants = receivedData.getEventParticipants();
		if (CollectionUtils.isNotEmpty(eventParticipants)) {
			eventParticipants.forEach(ep -> {
				ValidationErrors eventParticipantErrors = eventParticipantProcessor
					.processReceivedData(ep, existingEntities.getEventParticipants().get(ep.getEntity().getUuid()), originInfo);

				if (eventParticipantErrors.hasError()) {
					validationErrors
						.add(new ValidationErrors(ValidationHelper.buildEventParticipantValidationGroupName(ep.getEntity()), eventParticipantErrors));
				}
			});
		}

		List<SormasToSormasSampleDto> samples = receivedData.getSamples();
		if (CollectionUtils.isNotEmpty(samples)) {
			samples.forEach(s -> {
				ValidationErrors contactErrors =
					sampleProcessor.processReceivedData(s, existingEntities.getSamples().get(s.getEntity().getUuid()), originInfo);

				if (contactErrors.hasError()) {
					validationErrors.add(new ValidationErrors(ValidationHelper.buildSampleValidationGroupName(s.getEntity()), contactErrors));
				}
			});
		}

		List<SormasToSormasImmunizationDto> immunizations = receivedData.getImmunizations();
		if (CollectionUtils.isNotEmpty(immunizations)) {
			immunizations.forEach(s -> {
				ValidationErrors immunizationErrors =
					immunizationProcessor.processReceivedData(s, existingEntities.getImmunizations().get(s.getEntity().getUuid()), originInfo);

				if (immunizationErrors.hasError()) {
					validationErrors
						.add(new ValidationErrors(ValidationHelper.buildImmunizationValidationGroupName(s.getEntity()), immunizationErrors));
				}
			});
		}

		List<SormasToSormasSurveillanceReportDto> reports = receivedData.getSurveillanceReports();
		if (CollectionUtils.isNotEmpty(reports)) {
			reports.forEach(r -> {
				ValidationErrors immunizationErrors = surveillanceReportProcessor
					.processReceivedData(r, existingEntities.getSurveillanceReports().get(r.getEntity().getUuid()), originInfo);

				if (immunizationErrors.hasError()) {
					validationErrors
						.add(new ValidationErrors(ValidationHelper.buildSurveillanceReportValidationGroupName(r.getEntity()), immunizationErrors));
				}
			});
		}

		return validationErrors;
	}

	public List<ValidationErrors> processReceivedRequest(ShareRequestData shareData) {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		SormasToSormasOriginInfoDto originInfo = shareData.getOriginInfo();

		originInfo.setUuid(DataHelper.createUuid());
		originInfo.setChangeDate(new Date());

		ValidationErrors originInfoErrors = validateOriginInfo(originInfo, Captions.sormasToSormasOriginInfo);
		if (originInfoErrors.hasError()) {
			validationErrors.add(new ValidationErrors(new ValidationErrorGroup(Captions.sormasToSormasOriginInfo), originInfoErrors));
		}

		shareData.getPreviews().getCases().forEach(c -> {
			ValidationErrors caseErrors = caseProcessor.processReceivedPreview(c);

			if (caseErrors.hasError()) {
				validationErrors.add(new ValidationErrors(ValidationHelper.buildCaseValidationGroupName(c), caseErrors));
			}
		});
		shareData.getPreviews().getContacts().forEach(c -> {
			ValidationErrors contactErrors = contactProcessor.processReceivedPreview(c);

			if (contactErrors.hasError()) {
				validationErrors.add(new ValidationErrors(ValidationHelper.buildContactValidationGroupName(c), contactErrors));
			}
		});
		shareData.getPreviews().getEvents().forEach(e -> {
			ValidationErrors eventErrors = eventProcessor.processReceivedPreview(e);

			if (eventErrors.hasError()) {
				validationErrors.add(new ValidationErrors(ValidationHelper.buildEventValidationGroupName(e), eventErrors));
			}
		});
		shareData.getPreviews().getEventParticipants().forEach(ep -> {
			ValidationErrors eventParticipantErrors = eventParticipantProcessor.processReceivedPreview(ep);

			if (eventParticipantErrors.hasError()) {
				validationErrors.add(new ValidationErrors(ValidationHelper.buildEventParticipantValidationGroupName(ep), eventParticipantErrors));
			}
		});

		return validationErrors;
	}

	private ValidationErrors validateOriginInfo(SormasToSormasOriginInfoDto originInfo, String validationGroupCaption) {
		if (originInfo == null) {
			return ValidationErrors
				.create(new ValidationErrorGroup(validationGroupCaption), new ValidationErrorMessage(Validations.sormasToSormasShareInfoMissing));
		}

		ValidationErrors validationErrors = new ValidationErrors();

		if (originInfo.getOrganizationId() == null) {
			validationErrors.add(
				new ValidationErrorGroup(Captions.CaseData_sormasToSormasOriginInfo),
				new ValidationErrorMessage(Validations.sormasToSormasOrganizationIdMissing));
		}

		if (DataHelper.isNullOrEmpty(originInfo.getSenderName())) {
			validationErrors.add(
				new ValidationErrorGroup(Captions.CaseData_sormasToSormasOriginInfo),
				new ValidationErrorMessage(Validations.sormasToSormasSenderNameMissing));
		}

		if (DataHelper.isNullOrEmpty(originInfo.getComment())) {
			validationErrors.add(
				new ValidationErrorGroup(Captions.CaseData_sormasToSormasOriginInfo),
				new ValidationErrorMessage(Validations.sormasToSormasCommentMissing));
		}

		return validationErrors;
	}
}
