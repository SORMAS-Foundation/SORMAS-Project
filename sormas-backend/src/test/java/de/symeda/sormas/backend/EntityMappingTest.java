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

package de.symeda.sormas.backend;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.hibernate.annotations.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.MappingException;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueDto;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventGroupDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogDto;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.report.WeeklyReportEntryDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.share.ExternalShareInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.share.ShareRequestDetailsDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.backend.action.Action;
import de.symeda.sormas.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.backend.campaign.diagram.CampaignDiagramDefinition;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.maternalhistory.MaternalHistory;
import de.symeda.sormas.backend.caze.porthealthinfo.PortHealthInfo;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.NotExposedToApi;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumValue;
import de.symeda.sormas.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.backend.document.Document;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventGroup;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.exposure.Exposure;
import de.symeda.sormas.backend.externalmessage.ExternalMessage;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReport;
import de.symeda.sormas.backend.externalmessage.labmessage.TestReport;
import de.symeda.sormas.backend.feature.FeatureConfiguration;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.importexport.ExportConfiguration;
import de.symeda.sormas.backend.infrastructure.PopulationData;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.manualmessagelog.ManualMessageLog;
import de.symeda.sormas.backend.outbreak.Outbreak;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonContactDetail;
import de.symeda.sormas.backend.report.AggregateReport;
import de.symeda.sormas.backend.report.WeeklyReport;
import de.symeda.sormas.backend.report.WeeklyReportEntry;
import de.symeda.sormas.backend.sample.AdditionalTest;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.share.ExternalShareInfo;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.share.incoming.SormasToSormasShareRequest;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.systemevent.SystemEvent;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.therapy.Prescription;
import de.symeda.sormas.backend.therapy.Therapy;
import de.symeda.sormas.backend.therapy.Treatment;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserRole;
import de.symeda.sormas.backend.vaccination.Vaccination;
import de.symeda.sormas.backend.visit.Visit;

public class EntityMappingTest {

	private static final Map<Class<? extends AbstractDomainObject>, Class<? extends EntityDto>> mappings = new HashMap<>();
	private static final List<Class<?>> mustMatchTypes = Arrays.asList(String.class, Integer.class, Long.class);

	static {
		mappings.put(Action.class, ActionDto.class);
		mappings.put(ActivityAsCase.class, ActivityAsCaseDto.class);
		mappings.put(AdditionalTest.class, AdditionalTestDto.class);
		mappings.put(AggregateReport.class, AggregateReportDto.class);
		mappings.put(Area.class, AreaDto.class);
		mappings.put(Campaign.class, CampaignDto.class);
		mappings.put(CampaignDiagramDefinition.class, CampaignDiagramDefinitionDto.class);
		mappings.put(CampaignFormData.class, CampaignFormDataDto.class);
		mappings.put(CampaignFormMeta.class, CampaignFormMetaDto.class);
		mappings.put(Case.class, CaseDataDto.class);
		mappings.put(ClinicalCourse.class, ClinicalCourseDto.class);
		mappings.put(ClinicalVisit.class, ClinicalVisitDto.class);
		mappings.put(Community.class, CommunityDto.class);
		mappings.put(Contact.class, ContactDto.class);
		mappings.put(Continent.class, ContinentDto.class);
		mappings.put(Country.class, CountryDto.class);
		mappings.put(CustomizableEnumValue.class, CustomizableEnumValueDto.class);
		mappings.put(DiseaseConfiguration.class, DiseaseConfigurationDto.class);
		mappings.put(District.class, DistrictDto.class);
		mappings.put(Document.class, DocumentDto.class);
		mappings.put(EpiData.class, EpiDataDto.class);
		mappings.put(Event.class, EventDto.class);
		mappings.put(EventGroup.class, EventGroupDto.class);
		mappings.put(EventParticipant.class, EventParticipantDto.class);
		mappings.put(ExportConfiguration.class, ExportConfigurationDto.class);
		mappings.put(Exposure.class, ExposureDto.class);
		mappings.put(ExternalMessage.class, ExternalMessageDto.class);
		mappings.put(ExternalShareInfo.class, ExternalShareInfoDto.class);
		mappings.put(Facility.class, FacilityDto.class);
		mappings.put(FeatureConfiguration.class, FeatureConfigurationDto.class);
		mappings.put(HealthConditions.class, HealthConditionsDto.class);
		mappings.put(Hospitalization.class, HospitalizationDto.class);
		mappings.put(Immunization.class, ImmunizationDto.class);
		mappings.put(Location.class, LocationDto.class);
		mappings.put(ManualMessageLog.class, ManualMessageLogDto.class);
		mappings.put(MaternalHistory.class, MaternalHistoryDto.class);
		mappings.put(Outbreak.class, OutbreakDto.class);
		mappings.put(PathogenTest.class, PathogenTestDto.class);
		mappings.put(Person.class, PersonDto.class);
		mappings.put(PersonContactDetail.class, PersonContactDetailDto.class);
		mappings.put(PointOfEntry.class, PointOfEntryDto.class);
		mappings.put(PopulationData.class, PopulationDataDto.class);
		mappings.put(PortHealthInfo.class, PortHealthInfoDto.class);
		mappings.put(Prescription.class, PrescriptionDto.class);
		mappings.put(PreviousHospitalization.class, PreviousHospitalizationDto.class);
		mappings.put(Region.class, RegionDto.class);
		mappings.put(Sample.class, SampleDto.class);
		mappings.put(SampleReport.class, SampleReportDto.class);
		mappings.put(ShareRequestInfo.class, ShareRequestDetailsDto.class);
		mappings.put(SormasToSormasOriginInfo.class, SormasToSormasOriginInfoDto.class);
		mappings.put(SormasToSormasShareInfo.class, SormasToSormasShareInfoDto.class);
		mappings.put(SormasToSormasShareRequest.class, SormasToSormasShareRequestDto.class);
		mappings.put(Subcontinent.class, SubcontinentDto.class);
		mappings.put(SurveillanceReport.class, SurveillanceReportDto.class);
		mappings.put(Symptoms.class, SymptomsDto.class);
		mappings.put(SystemEvent.class, SystemEventDto.class);
		mappings.put(Task.class, TaskDto.class);
		mappings.put(TestReport.class, TestReportDto.class);
		mappings.put(Therapy.class, TherapyDto.class);
		mappings.put(TravelEntry.class, TravelEntryDto.class);
		mappings.put(Treatment.class, TreatmentDto.class);
		mappings.put(User.class, UserDto.class);
		mappings.put(UserRole.class, UserRoleDto.class);
		mappings.put(Vaccination.class, VaccinationDto.class);
		mappings.put(Visit.class, VisitDto.class);
		mappings.put(WeeklyReport.class, WeeklyReportDto.class);
		mappings.put(WeeklyReportEntry.class, WeeklyReportEntryDto.class);

	}

	@Test
	@Disabled("Ignored until all fields have been fixed or marked with exception annotation!")
	public void testDtoEntityFieldMatching() {

		final StringBuilder stringBuilder = new StringBuilder("\n");
		AtomicBoolean differencesFound = new AtomicBoolean(false);

		mappings.forEach((entityClass, dtoClass) -> {
			final String entityName = entityClass.getSimpleName();
			final String dtoName = dtoClass.getSimpleName();

			final ClassComparisonResult entityVsDto = compareClassFields(entityClass, dtoClass);
			final ClassComparisonResult dtoVsEntity = compareClassFields(dtoClass, entityClass);

			if (entityVsDto.differencesFound() || dtoVsEntity.differencesFound()) {
				stringBuilder.append("\n" + entityName + " <-> " + dtoName + "\n");
			}

			final List<String> missingFieldsInDto = entityVsDto.getMissingFields();
			if (!missingFieldsInDto.isEmpty()) {
				stringBuilder.append("\tMissing fields in DTO:\n");
				missingFieldsInDto.forEach(s -> stringBuilder.append("\t - " + s + "\n"));
				differencesFound.set(true);
			}

			final List<String> missingFieldsInEntity = dtoVsEntity.getMissingFields();
			if (!missingFieldsInEntity.isEmpty()) {
				stringBuilder.append("\tMissing fields in entity:\n");
				missingFieldsInEntity.forEach(s -> stringBuilder.append("\t - " + s + "\n"));
				differencesFound.set(true);
			}

			final List<String> fieldsHavingDifferentTypes = dtoVsEntity.getFieldsHavingDifferentTypes();
			final List<String> fieldsHavingDifferentTypesFromEntity = entityVsDto.getFieldsHavingDifferentTypes();
			if (!fieldsHavingDifferentTypes.isEmpty() || !fieldsHavingDifferentTypesFromEntity.isEmpty()) {
				stringBuilder.append("\tFields having different types:\n");
				fieldsHavingDifferentTypes.forEach(s -> stringBuilder.append("\t - " + s + "\n"));
				fieldsHavingDifferentTypesFromEntity.stream()
					.filter(s -> !fieldsHavingDifferentTypes.contains(s))
					.forEach(s -> stringBuilder.append("\t - " + s + "\n"));
				differencesFound.set(true);
			}
		});

		if (differencesFound.get()) {
			Assertions.fail(stringBuilder.toString());
		}
	}

	private ClassComparisonResult compareClassFields(Class<?> leadClass, Class<?> comparisonClass) {

		final ClassComparisonResult classComparisonResult = new ClassComparisonResult();

		final Predicate<Field> excludeConstantFields = field -> !(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()));
		final Predicate<Field> excludeFieldsNotExposedToApi = field -> !(field.isAnnotationPresent(NotExposedToApi.class));
		final Predicate<Field> excludeFieldsFromApi = field -> !(field.isAnnotationPresent(MappingException.class));
		final List<Field> leadClassDeclaredFields = FieldUtils.getAllFieldsList(leadClass)
			.stream()
			.filter(excludeConstantFields.and(excludeFieldsFromApi).and(excludeFieldsNotExposedToApi))
			.collect(Collectors.toList());
		for (final Field field : leadClassDeclaredFields) {
			final String fieldName = field.getName();
			final Class<?> fieldType = field.getType();

			Field comparisonField = FieldUtils.getField(comparisonClass, fieldName, true);
			if (comparisonField == null) {
				if (!Collection.class.isAssignableFrom(fieldType)) {
					classComparisonResult.addMissingField(fieldName);
				}
			} else {
				final Class<?> comparisonFieldType = comparisonField.getType();
				final Method getter = MethodUtils.getAccessibleMethod(leadClass, "get" + fieldName);
				if (((mustMatchTypes.contains(fieldType) && (getter != null && !getter.isAnnotationPresent(Type.class))) || fieldType.isEnum())
					&& !fieldType.equals(comparisonFieldType)) {
					classComparisonResult.addDifferentTypeField(fieldName);
				}
			}
		}
		return classComparisonResult;
	}

	public static class ClassComparisonResult {

		final List<String> missingFields = new ArrayList<>();
		final List<String> fieldsHavingDifferentTypes = new ArrayList<>();

		public void addMissingField(String fieldName) {
			missingFields.add(fieldName);
		}

		public void addDifferentTypeField(String fieldName) {
			fieldsHavingDifferentTypes.add(fieldName);
		}

		public List<String> getMissingFields() {
			return missingFields;
		}

		public List<String> getFieldsHavingDifferentTypes() {
			return fieldsHavingDifferentTypes;
		}

		public boolean differencesFound() {
			return !missingFields.isEmpty() || !fieldsHavingDifferentTypes.isEmpty();
		}
	}

}
