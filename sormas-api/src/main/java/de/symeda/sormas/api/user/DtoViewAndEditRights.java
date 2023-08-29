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

package de.symeda.sormas.api.user;

import java.util.HashMap;
import java.util.Map;

import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.visit.VisitDto;

public class DtoViewAndEditRights {

	private static Map<String, UserRight> viewRights = new HashMap<>();
	private static Map<String, UserRight> editRights = new HashMap<>();

	static {
		viewRights.put(AdditionalTestDto.class.getSimpleName(), UserRight.ADDITIONAL_TEST_VIEW);
		editRights.put(AdditionalTestDto.class.getSimpleName(), UserRight.ADDITIONAL_TEST_EDIT);

		viewRights.put(AggregateReportDto.class.getSimpleName(), UserRight.AGGREGATE_REPORT_VIEW);
		editRights.put(AggregateReportDto.class.getSimpleName(), UserRight.AGGREGATE_REPORT_EDIT);

		viewRights.put(CaseDataDto.class.getSimpleName(), UserRight.CASE_VIEW);
		editRights.put(CaseDataDto.class.getSimpleName(), UserRight.CASE_EDIT);

		viewRights.put(ClinicalVisitDto.class.getSimpleName(), UserRight.CLINICAL_COURSE_VIEW);
		editRights.put(ClinicalVisitDto.class.getSimpleName(), UserRight.CLINICAL_VISIT_EDIT);

		viewRights.put(ContactDto.class.getSimpleName(), UserRight.CONTACT_VIEW);
		editRights.put(ContactDto.class.getSimpleName(), UserRight.CONTACT_EDIT);

		viewRights.put(EnvironmentDto.class.getSimpleName(), UserRight.ENVIRONMENT_VIEW);
		editRights.put(EnvironmentDto.class.getSimpleName(), UserRight.ENVIRONMENT_EDIT);

		viewRights.put(EnvironmentSampleDto.class.getSimpleName(), UserRight.ENVIRONMENT_SAMPLE_VIEW);
		editRights.put(EnvironmentSampleDto.class.getSimpleName(), UserRight.ENVIRONMENT_SAMPLE_EDIT);

		viewRights.put(EventDto.class.getSimpleName(), UserRight.EVENT_VIEW);
		editRights.put(EventDto.class.getSimpleName(), UserRight.EVENT_EDIT);

		viewRights.put(EventParticipantDto.class.getSimpleName(), UserRight.EVENTPARTICIPANT_VIEW);
		editRights.put(EventParticipantDto.class.getSimpleName(), UserRight.EVENTPARTICIPANT_EDIT);

		viewRights.put(ImmunizationDto.class.getSimpleName(), UserRight.IMMUNIZATION_VIEW);
		editRights.put(ImmunizationDto.class.getSimpleName(), UserRight.IMMUNIZATION_EDIT);

		viewRights.put(OutbreakDto.class.getSimpleName(), UserRight.OUTBREAK_VIEW);
		editRights.put(OutbreakDto.class.getSimpleName(), UserRight.OUTBREAK_EDIT);

		// no explicit UserRight to view PathogenTestDto
		editRights.put(PathogenTestDto.class.getSimpleName(), UserRight.PATHOGEN_TEST_EDIT);

		viewRights.put(PersonDto.class.getSimpleName(), UserRight.PERSON_VIEW);
		editRights.put(PersonDto.class.getSimpleName(), UserRight.PERSON_EDIT);

		viewRights.put(PrescriptionDto.class.getSimpleName(), UserRight.CASE_VIEW);
		editRights.put(PrescriptionDto.class.getSimpleName(), UserRight.PRESCRIPTION_EDIT);

		viewRights.put(SampleDto.class.getSimpleName(), UserRight.SAMPLE_VIEW);
		editRights.put(SampleDto.class.getSimpleName(), UserRight.SAMPLE_EDIT);

		viewRights.put(TaskDto.class.getSimpleName(), UserRight.TASK_VIEW);
		editRights.put(TaskDto.class.getSimpleName(), UserRight.TASK_EDIT);

		viewRights.put(TreatmentDto.class.getSimpleName(), UserRight.CASE_VIEW);
		editRights.put(TreatmentDto.class.getSimpleName(), UserRight.TREATMENT_EDIT);

		// can be with CONTACT_VIEW, too. Currently all user roles that can view
		// cases can also view contacts.
		viewRights.put(VisitDto.class.getSimpleName(), UserRight.CASE_VIEW);
		editRights.put(VisitDto.class.getSimpleName(), UserRight.VISIT_EDIT);

		viewRights.put(WeeklyReportDto.class.getSimpleName(), UserRight.WEEKLYREPORT_VIEW);
		// no explicit UserRight to edit WeeklyReportDto

		viewRights.put(CampaignFormMetaDto.class.getSimpleName(), UserRight.CAMPAIGN_VIEW);
		editRights.put(CampaignFormMetaDto.class.getSimpleName(), UserRight.CAMPAIGN_EDIT);

		viewRights.put(CampaignDto.class.getSimpleName(), UserRight.CAMPAIGN_VIEW);
		editRights.put(CampaignDto.class.getSimpleName(), UserRight.CAMPAIGN_EDIT);

		viewRights.put(CampaignFormDataDto.class.getSimpleName(), UserRight.CAMPAIGN_FORM_DATA_VIEW);
		editRights.put(CampaignFormDataDto.class.getSimpleName(), UserRight.CAMPAIGN_FORM_DATA_EDIT);
	}

	public static UserRight getUserRightView(Class clazz) {
		return viewRights.get(clazz.getSimpleName());
	}

	public static UserRight getUserRightEdit(Class clazz) {
		return editRights.get(clazz.getSimpleName());
	}
}
