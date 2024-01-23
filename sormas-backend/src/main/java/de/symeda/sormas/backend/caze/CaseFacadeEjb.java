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

package de.symeda.sormas.backend.caze;

import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.and;
import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.createOrderBuilder;
import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.or;
import static de.symeda.sormas.backend.visit.VisitLogic.getVisitResult;
import static java.util.Objects.isNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.BurialInfoDto;
import de.symeda.sormas.api.caze.CaseBulkEditData;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseFollowUpCriteria;
import de.symeda.sormas.api.caze.CaseFollowUpDto;
import de.symeda.sormas.api.caze.CaseIndexDetailedDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseListEntryDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.CaseMergeIndexDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CasePersonDto;
import de.symeda.sormas.api.caze.CaseReferenceDefinition;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.caze.CoreAndPersonDto;
import de.symeda.sormas.api.caze.EmbeddedSampleExportDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.PreviousCaseDto;
import de.symeda.sormas.api.caze.ReinfectionDetail;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseReferenceDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataHelper;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.followup.FollowUpDto;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.followup.FollowUpPeriodDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.location.LocationReferenceDto;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogDto;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasRuntimeException;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskHelper;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.DtoCopyHelper;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.checkers.UserRightFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitResultDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.classification.CaseClassificationFacadeEjb.CaseClassificationFacadeEjbLocal;
import de.symeda.sormas.backend.caze.maternalhistory.MaternalHistoryFacadeEjb;
import de.symeda.sormas.backend.caze.maternalhistory.MaternalHistoryFacadeEjb.MaternalHistoryFacadeEjbLocal;
import de.symeda.sormas.backend.caze.porthealthinfo.PortHealthInfoFacadeEjb;
import de.symeda.sormas.backend.caze.porthealthinfo.PortHealthInfoFacadeEjb.PortHealthInfoFacadeEjbLocal;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportService;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb.ClinicalCourseFacadeEjbLocal;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitFacadeEjb.ClinicalVisitFacadeEjbLocal;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitService;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.clinicalcourse.HealthConditionsMapper;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper.OrderBuilder;
import de.symeda.sormas.backend.common.NotificationService;
import de.symeda.sormas.backend.common.messaging.MessageContents;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.MessagingService;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.contact.VisitSummaryExportDetails;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumFacadeEjb.CustomizableEnumFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.document.Document;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.event.EventSummaryDetails;
import de.symeda.sormas.backend.exposure.Exposure;
import de.symeda.sormas.backend.exposure.ExposureService;
import de.symeda.sormas.backend.externaljournal.ExternalJournalService;
import de.symeda.sormas.backend.externalsurveillancetool.ExternalSurveillanceToolGatewayFacadeEjb.ExternalSurveillanceToolGatewayFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb.HospitalizationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.backend.immunization.ImmunizationEntityHelper;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.importexport.ExportHelper;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb.PopulationDataFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.manualmessagelog.ManualMessageLogService;
import de.symeda.sormas.backend.outbreak.Outbreak;
import de.symeda.sormas.backend.outbreak.OutbreakService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.sample.AdditionalTest;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.share.ExternalShareInfoCountAndLatestDate;
import de.symeda.sormas.backend.share.ExternalShareInfoService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.caze.SormasToSormasCaseFacadeEjb.SormasToSormasCaseFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareInfoHelper;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.therapy.Prescription;
import de.symeda.sormas.backend.therapy.PrescriptionFacadeEjb;
import de.symeda.sormas.backend.therapy.PrescriptionFacadeEjb.PrescriptionFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.PrescriptionService;
import de.symeda.sormas.backend.therapy.Therapy;
import de.symeda.sormas.backend.therapy.TherapyFacadeEjb;
import de.symeda.sormas.backend.therapy.TherapyFacadeEjb.TherapyFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.Treatment;
import de.symeda.sormas.backend.therapy.TreatmentFacadeEjb;
import de.symeda.sormas.backend.therapy.TreatmentFacadeEjb.TreatmentFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.TreatmentService;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.services.TravelEntryService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserHelper;
import de.symeda.sormas.backend.user.UserReference;
import de.symeda.sormas.backend.user.UserRoleFacadeEjb;
import de.symeda.sormas.backend.user.UserRoleService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.PatchHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;
import de.symeda.sormas.backend.vaccination.Vaccination;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb;
import de.symeda.sormas.backend.vaccination.VaccinationService;
import de.symeda.sormas.backend.visit.Visit;
import de.symeda.sormas.backend.visit.VisitFacadeEjb;
import de.symeda.sormas.backend.visit.VisitFacadeEjb.VisitFacadeEjbLocal;
import de.symeda.sormas.backend.visit.VisitService;

@Stateless(name = "CaseFacade")
@RightsAllowed(UserRight._CASE_VIEW)
public class CaseFacadeEjb extends AbstractCoreFacadeEjb<Case, CaseDataDto, CaseIndexDto, CaseReferenceDto, CaseService, CaseCriteria>
	implements CaseFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private CaseClassificationFacadeEjbLocal caseClassificationFacade;
	@EJB
	private CaseListCriteriaBuilder listQueryBuilder;
	@EJB
	private PersonService personService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private VisitService visitService;
	@EJB
	private VisitFacadeEjbLocal visitFacade;
	@EJB
	private SymptomsFacadeEjbLocal symptomsFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private TaskService taskService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private EventService eventService;
	@EJB
	private SampleService sampleService;
	@EJB
	private PathogenTestService pathogenTestService;
	@EJB
	private PathogenTestFacadeEjbLocal sampleTestFacade;
	@EJB
	private HospitalizationFacadeEjbLocal hospitalizationFacade;
	@EJB
	private EpiDataFacadeEjbLocal epiDataFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private TreatmentFacadeEjbLocal treatmentFacade;
	@EJB
	private PrescriptionFacadeEjbLocal prescriptionFacade;
	@EJB
	private ClinicalVisitFacadeEjbLocal clinicalVisitFacade;
	@EJB
	private MessagingService messagingService;
	@EJB
	private NotificationService notificationService;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private TherapyFacadeEjbLocal therapyFacade;
	@EJB
	private ClinicalCourseFacadeEjbLocal clinicalCourseFacade;
	@EJB
	private PrescriptionService prescriptionService;
	@EJB
	private TreatmentService treatmentService;
	@EJB
	private ClinicalVisitService clinicalVisitService;
	@EJB
	private ExposureService exposureService;
	@EJB
	private OutbreakService outbreakService;
	@EJB
	private MaternalHistoryFacadeEjbLocal maternalHistoryFacade;
	@EJB
	private PointOfEntryService pointOfEntryService;
	@EJB
	private PortHealthInfoFacadeEjbLocal portHealthInfoFacade;
	@EJB
	private PopulationDataFacadeEjbLocal populationDataFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private UserRoleFacadeEjb.UserRoleFacadeEjbLocal userRoleFacade;
	@EJB
	private SormasToSormasOriginInfoService originInfoService;
	@EJB
	private ManualMessageLogService manualMessageLogService;
	@EJB
	private AdditionalTestFacadeEjbLocal additionalTestFacade;
	@EJB
	private ExternalJournalService externalJournalService;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private ExternalSurveillanceToolGatewayFacadeEjbLocal externalSurveillanceToolGatewayFacade;
	@EJB
	private ExternalShareInfoService externalShareInfoService;
	@EJB
	private DocumentService documentService;
	@EJB
	private SurveillanceReportService surveillanceReportService;
	@EJB
	private EpiDataService epiDataService;
	@EJB
	private SurveillanceReportFacadeEjb.SurveillanceReportFacadeEjbLocal surveillanceReportFacade;
	@EJB
	private SormasToSormasFacadeEjbLocal sormasToSormasFacade;
	@EJB
	private SormasToSormasCaseFacadeEjbLocal sormasToSormasCaseFacade;
	@EJB
	private ShareRequestInfoService shareRequestInfoService;
	@EJB
	private VaccinationFacadeEjb.VaccinationFacadeEjbLocal vaccinationFacade;
	@EJB
	private HealthConditionsMapper healthConditionsMapper;
	@EJB
	private TravelEntryService travelEntryService;
	@EJB
	private VaccinationService vaccinationService;
	@EJB
	private CaseService caseService;
	@EJB
	private UserRoleService userRoleService;
	@EJB
	private CustomizableEnumFacadeEjbLocal customizableEnumFacade;

	@Resource
	private ManagedScheduledExecutorService executorService;

	public CaseFacadeEjb() {
	}

	@Inject
	public CaseFacadeEjb(CaseService service) {
		super(Case.class, CaseDataDto.class, service);
	}

	@Override
	protected Pseudonymizer createPseudonymizer() {
		return getPseudonymizerForDtoWithClinician("");
	}

	private Pseudonymizer getPseudonymizerForDtoWithClinician(@Nullable String pseudonymizedValue) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, pseudonymizedValue);

		UserRightFieldAccessChecker clinicianViewRightChecker =
			new UserRightFieldAccessChecker(UserRight.CASE_CLINICIAN_VIEW, userService.hasRight(UserRight.CASE_CLINICIAN_VIEW));
		pseudonymizer.addFieldAccessChecker(clinicianViewRightChecker, clinicianViewRightChecker);

		return pseudonymizer;
	}

	public Page<CaseIndexDto> getIndexPage(CaseCriteria caseCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<CaseIndexDto> caseIndexList = getIndexList(caseCriteria, offset, size, sortProperties);
		long totalElementCount = count(caseCriteria);
		return new Page<>(caseIndexList, offset, size, totalElementCount);
	}

	@Override
	public String getUuidByUuidEpidNumberOrExternalId(String searchTerm, CaseCriteria caseCriteria) {
		return service.getUuidByUuidEpidNumberOrExternalId(searchTerm, caseCriteria);
	}

	@Override
	public long count(CaseCriteria caseCriteria) {

		return count(caseCriteria, false);
	}

	@Override
	public long count(CaseCriteria caseCriteria, boolean ignoreUserFilter) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> root = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, root);

		Predicate filter = null;

		if (!ignoreUserFilter) {
			CaseUserFilterCriteria caseUserFilterCriteria = new CaseUserFilterCriteria();
			if (caseCriteria != null) {
				caseUserFilterCriteria.setIncludeCasesFromOtherJurisdictions(caseCriteria.getIncludeCasesFromOtherJurisdictions());
			}
			filter = service.createUserFilter(caseQueryContext, caseUserFilterCriteria);
		}

		if (caseCriteria != null) {
			Predicate criteriaFilter = service.createCriteriaFilter(caseCriteria, caseQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(root));
		return em.createQuery(cq).getSingleResult();
	}

	public Page<CaseIndexDetailedDto> getIndexDetailedPage(
		CaseCriteria caseCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {
		List<CaseIndexDetailedDto> caseIndexDetailedList = getIndexDetailedList(caseCriteria, offset * size, size, sortProperties);
		long totalElementCount = count(caseCriteria);
		return new Page<>(caseIndexDetailedList, offset, size, totalElementCount);
	}

	@Override
	public List<CaseIndexDto> getIndexList(CaseCriteria caseCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaQuery<Tuple> cqIds = listQueryBuilder.buildIndexCriteriaPrefetchIds(caseCriteria, sortProperties);
		List<Long> indexListIds =
			QueryHelper.getResultList(em, cqIds, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());

		List<CaseIndexDto> cases = new ArrayList<>();
		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			CriteriaQuery<Tuple> cq = listQueryBuilder.buildIndexCriteria(caseCriteria, sortProperties, batchedIds);
			cases.addAll(QueryHelper.getResultList(em, cq, new CaseIndexDtoResultTransformer(), null, null));
		});

		List<Long> caseIds = cases.stream().map(CaseIndexDto::getId).collect(Collectors.toList());

		Map<String, ExternalShareInfoCountAndLatestDate> survToolShareCountAndDates = null;
		if (externalSurveillanceToolGatewayFacade.isFeatureEnabled()) {
			survToolShareCountAndDates = externalShareInfoService.getCaseShareCountAndLatestDate(caseIds)
				.stream()
				.collect(Collectors.toMap(ExternalShareInfoCountAndLatestDate::getAssociatedObjectUuid, Function.identity()));
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		for (CaseIndexDto caze : cases) {
			if (survToolShareCountAndDates != null) {
				ExternalShareInfoCountAndLatestDate survToolShareCountAndDate = survToolShareCountAndDates.get(caze.getUuid());

				if (survToolShareCountAndDate != null) {
					caze.setSurveillanceToolShareCount(survToolShareCountAndDate.getCount());
					caze.setSurveillanceToolLastShareDate(survToolShareCountAndDate.getLatestDate());
					caze.setSurveillanceToolStatus(survToolShareCountAndDate.getLatestStatus());
				}
			}

			Boolean isInJurisdiction = caze.getInJurisdiction();
			pseudonymizer.pseudonymizeDto(
				CaseIndexDto.class,
				caze,
				isInJurisdiction,
				c -> pseudonymizer.pseudonymizeDto(AgeAndBirthDateDto.class, caze.getAgeAndBirthDate(), isInJurisdiction, null));

			if (diseaseConfigurationFacade.hasFollowUp(caze.getDisease())) {
				int numberOfMissedVisits =
					FollowUpLogic.getNumberOfRequiredVisitsSoFar(caze.getReportDate(), caze.getFollowUpUntil()) - caze.getVisitCount();
				if (numberOfMissedVisits < 0) {
					numberOfMissedVisits = 0;
				}
				caze.setMissedVisitsCount(numberOfMissedVisits);
			}
		}

		return cases;
	}

	@Override
	public List<CaseIndexDetailedDto> getIndexDetailedList(CaseCriteria caseCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaQuery<Tuple> cqIds = listQueryBuilder.buildIndexDetailedCriteriaPrefetchIds(caseCriteria, sortProperties);
		List<Long> indexListIds =
			QueryHelper.getResultList(em, cqIds, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());

		List<CaseIndexDetailedDto> cases = new ArrayList<>();
		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			CriteriaQuery<Tuple> cq = listQueryBuilder.buildIndexDetailedCriteria(caseCriteria, sortProperties, batchedIds);
			cases.addAll(QueryHelper.getResultList(em, cq, new CaseIndexDetailedDtoResultTransformer(), null, null));
		});

		// Load latest events info
		// Adding a second query here is not perfect, but selecting the last event with a criteria query
		// doesn't seem to be possible and using a native query is not an option because of user filters
		List<EventSummaryDetails> eventSummaries =
			eventService.getEventSummaryDetailsByCases(cases.stream().map(CaseIndexDetailedDto::getId).collect(Collectors.toList()));

		Map<String, ExternalShareInfoCountAndLatestDate> survToolShareCountAndDates = null;
		if (externalSurveillanceToolGatewayFacade.isFeatureEnabled()) {
			survToolShareCountAndDates =
				externalShareInfoService.getCaseShareCountAndLatestDate(cases.stream().map(CaseIndexDto::getId).collect(Collectors.toList()))
					.stream()
					.collect(Collectors.toMap(ExternalShareInfoCountAndLatestDate::getAssociatedObjectUuid, Function.identity()));
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		for (CaseIndexDetailedDto caze : cases) {
			if (survToolShareCountAndDates != null) {
				ExternalShareInfoCountAndLatestDate survToolShareCountAndDate = survToolShareCountAndDates.get(caze.getUuid());

				if (survToolShareCountAndDate != null) {
					caze.setSurveillanceToolShareCount(survToolShareCountAndDate.getCount());
					caze.setSurveillanceToolLastShareDate(survToolShareCountAndDate.getLatestDate());
					caze.setSurveillanceToolStatus(survToolShareCountAndDate.getLatestStatus());
				}
			}

			if (caze.getEventCount() > 0) {
				eventSummaries.stream()
					.filter(v -> v.getCaseId() == caze.getId())
					.max(Comparator.comparing(EventSummaryDetails::getEventDate))
					.ifPresent(eventSummary -> {
						caze.setLatestEventId(eventSummary.getEventUuid());
						caze.setLatestEventStatus(eventSummary.getEventStatus());
						caze.setLatestEventTitle(eventSummary.getEventTitle());
					});
			}

			Boolean isInJurisdiction = caze.getInJurisdiction();
			pseudonymizer.pseudonymizeDto(CaseIndexDetailedDto.class, caze, isInJurisdiction, c -> {
				pseudonymizer.pseudonymizeDto(AgeAndBirthDateDto.class, caze.getAgeAndBirthDate(), isInJurisdiction, null);
				pseudonymizer
					.pseudonymizeUser(userService.getByUuid(caze.getReportingUser().getUuid()), userService.getCurrentUser(), caze::setReportingUser);
			});

			if (diseaseConfigurationFacade.hasFollowUp(caze.getDisease())) {
				int numberOfMissedVisits =
					FollowUpLogic.getNumberOfRequiredVisitsSoFar(caze.getReportDate(), caze.getFollowUpUntil()) - caze.getVisitCount();
				if (numberOfMissedVisits < 0) {
					numberOfMissedVisits = 0;
				}
				caze.setMissedVisitsCount(numberOfMissedVisits);
			}
		}

		return cases;
	}

	@Override
	public List<CaseSelectionDto> getCaseSelectionList(CaseCriteria caseCriteria) {
		List<CaseSelectionDto> entries = service.getCaseSelectionList(caseCriteria);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(CaseSelectionDto.class, entries, CaseSelectionDto::isInJurisdiction, null);

		return entries;
	}

	@Override
	public List<CaseListEntryDto> getEntriesList(String personUuid, Integer first, Integer max) {

		Long personId = personFacade.getPersonIdByUuid(personUuid);
		List<CaseListEntryDto> entries = service.getEntriesList(personId, first, max);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(CaseListEntryDto.class, entries, CaseListEntryDto::isInJurisdiction, null);

		return entries;
	}

	@RightsAllowed({
		UserRight._CASE_EDIT })
	public CaseDataDto postUpdate(String uuid, JsonNode caseDataDtoJson) {
		CaseDataDto existingCaseDto = getCaseDataWithoutPseudonyimization(uuid);
		PatchHelper.postUpdate(caseDataDtoJson, existingCaseDto);

		return this.save(existingCaseDto);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@RightsAllowed(UserRight._CASE_EXPORT)
	public List<CaseExportDto> getExportList(
		CaseCriteria caseCriteria,
		Collection<String> selectedRows,
		CaseExportType exportType,
		int first,
		int max,
		ExportConfigurationDto exportConfiguration,
		Language userLanguage) {

		Boolean previousCaseManagementDataCriteria = caseCriteria.getMustHaveCaseManagementData();
		if (CaseExportType.CASE_MANAGEMENT == exportType) {
			caseCriteria.setMustHaveCaseManagementData(Boolean.TRUE);
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CaseExportDto> cq = cb.createQuery(CaseExportDto.class);
		Root<Case> caseRoot = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caseRoot);
		final CaseJoins joins = caseQueryContext.getJoins();

		// Events count subquery
		Subquery<Long> eventCountSq = cq.subquery(Long.class);
		Root<EventParticipant> eventCountRoot = eventCountSq.from(EventParticipant.class);
		Join<EventParticipant, Event> event = eventCountRoot.join(EventParticipant.EVENT, JoinType.INNER);
		Join<EventParticipant, Case> resultingCase = eventCountRoot.join(EventParticipant.RESULTING_CASE, JoinType.INNER);
		eventCountSq.where(
			cb.and(
				cb.equal(resultingCase.get(Case.ID), caseRoot.get(Case.ID)),
				cb.isFalse(event.get(Event.DELETED)),
				cb.isFalse(eventCountRoot.get(EventParticipant.DELETED))));
		eventCountSq.select(cb.countDistinct(event.get(Event.ID)));

		Subquery<Long> prescriptionCountSq = cq.subquery(Long.class);
		Root<Prescription> prescriptionCountRoot = prescriptionCountSq.from(Prescription.class);
		Join<Prescription, Therapy> prescriptionTherapyJoin = prescriptionCountRoot.join(Prescription.THERAPY, JoinType.LEFT);
		prescriptionCountSq.where(cb.and(cb.equal(prescriptionTherapyJoin.get(Therapy.ID), caseRoot.get(Case.THERAPY).get(Therapy.ID))));
		prescriptionCountSq.select(cb.countDistinct(prescriptionCountRoot.get(Prescription.ID)));

		Subquery<Long> treatmentCountSq = cq.subquery(Long.class);
		Root<Treatment> treatmentCountRoot = treatmentCountSq.from(Treatment.class);
		Join<Treatment, Therapy> treatmentTherapyJoin = treatmentCountRoot.join(Treatment.THERAPY, JoinType.LEFT);
		treatmentCountSq.where(cb.and(cb.equal(treatmentTherapyJoin.get(Therapy.ID), caseRoot.get(Case.THERAPY).get(Therapy.ID))));
		treatmentCountSq.select(cb.countDistinct(treatmentCountRoot.get(Treatment.ID)));

		boolean exportGpsCoordinates = ExportHelper.shouldExportFields(exportConfiguration, PersonDto.ADDRESS, CaseExportDto.ADDRESS_GPS_COORDINATES);
		boolean exportPrescriptionNumber = (exportType == null || exportType == CaseExportType.CASE_MANAGEMENT)
			&& ExportHelper.shouldExportFields(exportConfiguration, CaseExportDto.NUMBER_OF_PRESCRIPTIONS);
		boolean exportTreatmentNumber = (exportType == null || exportType == CaseExportType.CASE_MANAGEMENT)
			&& ExportHelper.shouldExportFields(exportConfiguration, CaseExportDto.NUMBER_OF_TREATMENTS);
		boolean exportClinicalVisitNumber = (exportType == null || exportType == CaseExportType.CASE_MANAGEMENT)
			&& ExportHelper.shouldExportFields(exportConfiguration, CaseExportDto.NUMBER_OF_CLINICAL_VISITS);
		boolean exportOutbreakInfo = ExportHelper.shouldExportFields(exportConfiguration, CaseExportDto.ASSOCIATED_WITH_OUTBREAK);

		//@formatter:off
		cq.multiselect(caseRoot.get(Case.ID), joins.getPerson().get(Person.ID),
				exportGpsCoordinates ? joins.getPersonAddress().get(Location.LATITUDE) : cb.nullLiteral(Double.class),
				exportGpsCoordinates ? joins.getPersonAddress().get(Location.LONGITUDE) : cb.nullLiteral(Double.class),
				exportGpsCoordinates ? joins.getPersonAddress().get(Location.LATLONACCURACY) : cb.nullLiteral(Float.class),
				joins.getEpiData().get(EpiData.ID),
				joins.getRoot().get(Case.SYMPTOMS).get(Symptoms.ID),
				joins.getHospitalization().get(Hospitalization.ID),
				joins.getRoot().get(Case.HEALTH_CONDITIONS).get(HealthConditions.ID),
				caseRoot.get(Case.UUID),
				caseRoot.get(Case.EPID_NUMBER), caseRoot.get(Case.DISEASE), caseRoot.get(Case.DISEASE_VARIANT), caseRoot.get(Case.DISEASE_DETAILS),
				caseRoot.get(Case.DISEASE_VARIANT_DETAILS), joins.getPerson().get(Person.UUID), joins.getPerson().get(Person.FIRST_NAME), joins.getPerson().get(Person.LAST_NAME),
				joins.getPerson().get(Person.SALUTATION), joins.getPerson().get(Person.OTHER_SALUTATION), joins.getPerson().get(Person.SEX),
				caseRoot.get(Case.PREGNANT), joins.getPerson().get(Person.APPROXIMATE_AGE),
				joins.getPerson().get(Person.APPROXIMATE_AGE_TYPE), joins.getPerson().get(Person.BIRTHDATE_DD),
				joins.getPerson().get(Person.BIRTHDATE_MM), joins.getPerson().get(Person.BIRTHDATE_YYYY),
				caseRoot.get(Case.REPORT_DATE), joins.getRegion().get(Region.NAME),
				joins.getDistrict().get(District.NAME), joins.getCommunity().get(Community.NAME),
				caseRoot.get(Case.FACILITY_TYPE),
				joins.getFacility().get(Facility.NAME), joins.getFacility().get(Facility.UUID), caseRoot.get(Case.HEALTH_FACILITY_DETAILS),
				joins.getPointOfEntry().get(PointOfEntry.NAME), joins.getPointOfEntry().get(PointOfEntry.UUID), caseRoot.get(Case.POINT_OF_ENTRY_DETAILS),
				caseRoot.get(Case.CASE_CLASSIFICATION),
				caseRoot.get(Case.CLINICAL_CONFIRMATION), caseRoot.get(Case.EPIDEMIOLOGICAL_CONFIRMATION), caseRoot.get(Case.LABORATORY_DIAGNOSTIC_CONFIRMATION),
				caseRoot.get(Case.NOT_A_CASE_REASON_NEGATIVE_TEST),
				caseRoot.get(Case.NOT_A_CASE_REASON_PHYSICIAN_INFORMATION), caseRoot.get(Case.NOT_A_CASE_REASON_DIFFERENT_PATHOGEN),
				caseRoot.get(Case.NOT_A_CASE_REASON_OTHER), caseRoot.get(Case.NOT_A_CASE_REASON_DETAILS),
				caseRoot.get(Case.INVESTIGATION_STATUS), caseRoot.get(Case.INVESTIGATED_DATE),
				caseRoot.get(Case.OUTCOME), caseRoot.get(Case.OUTCOME_DATE),
				caseRoot.get(Case.SEQUELAE), caseRoot.get(Case.SEQUELAE_DETAILS),
				caseRoot.get(Case.BLOOD_ORGAN_OR_TISSUE_DONATED),
				caseRoot.get(Case.FOLLOW_UP_STATUS), caseRoot.get(Case.FOLLOW_UP_UNTIL),
				caseRoot.get(Case.NOSOCOMIAL_OUTBREAK), caseRoot.get(Case.INFECTION_SETTING),
				caseRoot.get(Case.PROHIBITION_TO_WORK), caseRoot.get(Case.PROHIBITION_TO_WORK_FROM), caseRoot.get(Case.PROHIBITION_TO_WORK_UNTIL),
				caseRoot.get(Case.RE_INFECTION), caseRoot.get(Case.PREVIOUS_INFECTION_DATE), caseRoot.get(Case.REINFECTION_STATUS), caseRoot.get(Case.REINFECTION_DETAILS),
				// quarantine
				caseRoot.get(Case.QUARANTINE), caseRoot.get(Case.QUARANTINE_TYPE_DETAILS), caseRoot.get(Case.QUARANTINE_FROM), caseRoot.get(Case.QUARANTINE_TO),
				caseRoot.get(Case.QUARANTINE_HELP_NEEDED),
				caseRoot.get(Case.QUARANTINE_ORDERED_VERBALLY),
				caseRoot.get(Case.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT),
				caseRoot.get(Case.QUARANTINE_ORDERED_VERBALLY_DATE),
				caseRoot.get(Case.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE),
				caseRoot.get(Case.QUARANTINE_EXTENDED),
				caseRoot.get(Case.QUARANTINE_REDUCED),
				caseRoot.get(Case.QUARANTINE_OFFICIAL_ORDER_SENT),
				caseRoot.get(Case.QUARANTINE_OFFICIAL_ORDER_SENT_DATE),

				joins.getHospitalization().get(Hospitalization.ADMITTED_TO_HEALTH_FACILITY), joins.getHospitalization().get(Hospitalization.ADMISSION_DATE),
				joins.getHospitalization().get(Hospitalization.DISCHARGE_DATE), joins.getHospitalization().get(Hospitalization.LEFT_AGAINST_ADVICE),
				joins.getPerson().get(Person.PRESENT_CONDITION), joins.getPerson().get(Person.DEATH_DATE), joins.getPerson().get(Person.BURIAL_DATE),
				joins.getPerson().get(Person.BURIAL_CONDUCTOR), joins.getPerson().get(Person.BURIAL_PLACE_DESCRIPTION),
				// address
				joins.getPersonAddressRegion().get(Region.NAME), joins.getPersonAddressDistrict().get(District.NAME), joins.getPersonAddressCommunity().get(Community.NAME),
				joins.getPersonAddress().get(Location.CITY), joins.getPersonAddress().get(Location.STREET), joins.getPersonAddress().get(Location.HOUSE_NUMBER),
				joins.getPersonAddress().get(Location.ADDITIONAL_INFORMATION), joins.getPersonAddress().get(Location.POSTAL_CODE),
				joins.getPersonAddressFacility().get(Facility.NAME), joins.getPersonAddressFacility().get(Facility.UUID), joins.getPersonAddress().get(Location.FACILITY_DETAILS),
				// phone
				caseQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_PHONE_SUBQUERY),
				caseQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_PHONE_OWNER_SUBQUERY),
				caseQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_EMAIL_SUBQUERY),
				caseQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_OTHER_CONTACT_DETAILS_SUBQUERY),
				joins.getPerson().get(Person.EDUCATION_TYPE),
				joins.getPerson().get(Person.EDUCATION_DETAILS), joins.getPerson().get(Person.OCCUPATION_TYPE),
				joins.getPerson().get(Person.OCCUPATION_DETAILS), joins.getPerson().get(Person.ARMED_FORCES_RELATION_TYPE), joins.getEpiData().get(EpiData.CONTACT_WITH_SOURCE_CASE_KNOWN),
				caseRoot.get(Case.VACCINATION_STATUS), caseRoot.get(Case.POSTPARTUM), caseRoot.get(Case.TRIMESTER),
				eventCountSq,
				exportPrescriptionNumber ? prescriptionCountSq : cb.nullLiteral(Long.class),
				exportTreatmentNumber ? treatmentCountSq : cb.nullLiteral(Long.class),
				exportClinicalVisitNumber ? clinicalVisitSq(cb, cq, caseRoot) : cb.nullLiteral(Long.class),
				caseRoot.get(Case.EXTERNAL_ID),
				caseRoot.get(Case.EXTERNAL_TOKEN),
				caseRoot.get(Case.INTERNAL_TOKEN),
				joins.getPerson().get(Person.BIRTH_NAME),
				joins.getPersonBirthCountry().get(Country.ISO_CODE),
				joins.getPersonBirthCountry().get(Country.DEFAULT_NAME),
				joins.getPersonCitizenship().get(Country.ISO_CODE),
				joins.getPersonCitizenship().get(Country.DEFAULT_NAME),
				caseRoot.get(Case.CASE_IDENTIFICATION_SOURCE),
				caseRoot.get(Case.SCREENING_TYPE),
				// responsible jurisdiction
				joins.getResponsibleRegion().get(Region.NAME),
				joins.getResponsibleDistrict().get(District.NAME),
				joins.getResponsibleCommunity().get(Community.NAME),
				caseRoot.get(Case.CLINICIAN_NAME),
				caseRoot.get(Case.CLINICIAN_PHONE),
				caseRoot.get(Case.CLINICIAN_EMAIL),
				caseRoot.get(Case.REPORTING_USER).get(User.ID),
				caseRoot.get(Case.FOLLOW_UP_STATUS_CHANGE_USER).get(User.ID),
				caseRoot.get(Case.PREVIOUS_QUARANTINE_TO),
				caseRoot.get(Case.QUARANTINE_CHANGE_COMMENT),
				exportOutbreakInfo ? cb.selectCase().when(cb.exists(outbreakSq(caseQueryContext)), cb.literal(I18nProperties.getString(Strings.yes)))
						.otherwise(cb.literal(I18nProperties.getString(Strings.no))) : cb.nullLiteral(String.class),
				JurisdictionHelper.booleanSelector(cb, service.inJurisdictionOrOwned(caseQueryContext)),
				caseRoot.get(Case.INVESTIGATED_DATE),
				caseRoot.get(Case.OUTCOME_DATE));
		//@formatter:on

		cq.distinct(true);

		Predicate filter = service.createUserFilter(caseQueryContext);

		if (caseCriteria != null) {
			Predicate criteriaFilter = service.createCriteriaFilter(caseCriteria, caseQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}
		filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, caseRoot.get(Case.UUID));

		if (filter != null) {
			cq.where(filter);
		}

		/*
		 * Sort by report date DESC, but also by id for stable Sorting in case of equal report dates.
		 * Since this method supports paging, values might jump between pages when sorting is unstable.
		 */
		cq.orderBy(cb.desc(caseRoot.get(Case.REPORT_DATE)), cb.desc(caseRoot.get(Case.ID)));

		List<CaseExportDto> resultList = QueryHelper.getResultList(em, cq, first, max);

		List<Long> resultCaseIds = resultList.stream().map(CaseExportDto::getId).collect(Collectors.toList());
		if (!resultList.isEmpty()) {
			List<Symptoms> symptomsList = null;
			CriteriaQuery<Symptoms> symptomsCq = cb.createQuery(Symptoms.class);
			Root<Symptoms> symptomsRoot = symptomsCq.from(Symptoms.class);
			Expression<String> symptomsIdsExpr = symptomsRoot.get(Symptoms.ID);
			symptomsCq.where(symptomsIdsExpr.in(resultList.stream().map(CaseExportDto::getSymptomsId).collect(Collectors.toList())));
			symptomsList = em.createQuery(symptomsCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
			Map<Long, Symptoms> symptoms = symptomsList.stream().collect(Collectors.toMap(Symptoms::getId, Function.identity()));

			Map<Long, HealthConditions> healthConditions = null;
			if (exportType == null || exportType == CaseExportType.CASE_MANAGEMENT) {
				if (ExportHelper.shouldExportFields(exportConfiguration, CaseDataDto.HEALTH_CONDITIONS)) {
					List<HealthConditions> healthConditionsList = null;
					CriteriaQuery<HealthConditions> healthConditionsCq = cb.createQuery(HealthConditions.class);
					Root<HealthConditions> healthConditionsRoot = healthConditionsCq.from(HealthConditions.class);
					Expression<String> healthConditionsIdsExpr = healthConditionsRoot.get(HealthConditions.ID);
					healthConditionsCq.where(
						healthConditionsIdsExpr.in(resultList.stream().map(CaseExportDto::getHealthConditionsId).collect(Collectors.toList())));
					healthConditionsList = em.createQuery(healthConditionsCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
					healthConditions = healthConditionsList.stream().collect(Collectors.toMap(HealthConditions::getId, Function.identity()));
				}
			}

			Map<Long, PreviousHospitalization> firstPreviousHospitalizations = null;
			if (ExportHelper.shouldExportFields(exportConfiguration, CaseExportDto.INITIAL_DETECTION_PLACE)) {
				List<PreviousHospitalization> prevHospsList = null;
				CriteriaQuery<PreviousHospitalization> prevHospsCq = cb.createQuery(PreviousHospitalization.class);
				Root<PreviousHospitalization> prevHospsRoot = prevHospsCq.from(PreviousHospitalization.class);
				Join<PreviousHospitalization, Hospitalization> prevHospsHospitalizationJoin =
					prevHospsRoot.join(PreviousHospitalization.HOSPITALIZATION, JoinType.LEFT);
				Expression<String> hospitalizationIdsExpr = prevHospsHospitalizationJoin.get(Hospitalization.ID);
				prevHospsCq
					.where(hospitalizationIdsExpr.in(resultList.stream().map(CaseExportDto::getHospitalizationId).collect(Collectors.toList())));
				prevHospsCq.orderBy(cb.asc(prevHospsRoot.get(PreviousHospitalization.ADMISSION_DATE)));
				prevHospsList = em.createQuery(prevHospsCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
				firstPreviousHospitalizations =
					prevHospsList.stream().collect(Collectors.toMap(p -> p.getHospitalization().getId(), Function.identity(), (id1, id2) -> id1));
			}

			Map<Long, CaseClassification> sourceCaseClassifications = null;
			if (ExportHelper.shouldExportFields(exportConfiguration, CaseExportDto.MAX_SOURCE_CASE_CLASSIFICATION)) {
				sourceCaseClassifications = contactService.getSourceCaseClassifications(resultCaseIds)
					.stream()
					.collect(
						Collectors
							.toMap(e -> (Long) e[0], e -> (CaseClassification) e[1], (c1, c2) -> c1.getSeverity() >= c2.getSeverity() ? c1 : c2));
			}

			Map<Long, List<Exposure>> exposures = null;
			if ((exportType == null || exportType == CaseExportType.CASE_SURVEILLANCE)
				&& ExportHelper
					.shouldExportFields(exportConfiguration, CaseExportDto.TRAVELED, CaseExportDto.TRAVEL_HISTORY, CaseExportDto.BURIAL_ATTENDED)) {
				CriteriaQuery<Exposure> exposuresCq = cb.createQuery(Exposure.class);
				Root<Exposure> exposuresRoot = exposuresCq.from(Exposure.class);
				Join<Exposure, EpiData> exposuresEpiDataJoin = exposuresRoot.join(Exposure.EPI_DATA, JoinType.LEFT);
				Expression<String> epiDataIdsExpr = exposuresEpiDataJoin.get(EpiData.ID);
				Predicate exposuresPredicate = cb.and(
					epiDataIdsExpr.in(resultList.stream().map(CaseExportDto::getEpiDataId).collect(Collectors.toList())),
					cb.or(
						cb.equal(exposuresRoot.get(Exposure.EXPOSURE_TYPE), ExposureType.TRAVEL),
						cb.equal(exposuresRoot.get(Exposure.EXPOSURE_TYPE), ExposureType.BURIAL)));
				exposuresCq.where(exposuresPredicate);
				exposuresCq.orderBy(cb.asc(exposuresEpiDataJoin.get(EpiData.ID)));
				List<Exposure> exposureList = em.createQuery(exposuresCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
				exposures = exposureList.stream().collect(Collectors.groupingBy(e -> e.getEpiData().getId()));
			}

			Map<Long, List<EmbeddedSampleExportDto>> samples = null;
			if ((exportType == null || exportType == CaseExportType.CASE_SURVEILLANCE)
				&& ExportHelper.shouldExportFields(exportConfiguration, CaseExportDto.SAMPLE_INFORMATION)) {
				List<EmbeddedSampleExportDto> samplesList = null;
				CriteriaQuery<EmbeddedSampleExportDto> samplesCq = cb.createQuery(EmbeddedSampleExportDto.class);
				Root<Sample> samplesRoot = samplesCq.from(Sample.class);
				Join<Sample, Case> samplesCaseJoin = samplesRoot.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
				Expression<String> caseIdsExpr = samplesCaseJoin.get(Case.ID);
				samplesCq.multiselect(
					samplesRoot.get(Sample.UUID),
					samplesRoot.get(Sample.SAMPLE_DATE_TIME),
					samplesRoot.get(Sample.LAB).get(Facility.UUID),
					samplesRoot.get(Sample.LAB).get(Facility.NAME),
					samplesRoot.get(Sample.LAB_DETAILS),
					samplesRoot.get(Sample.PATHOGEN_TEST_RESULT),
					caseIdsExpr);

				Predicate eliminateDeletedSamplesFilter = cb.equal(samplesRoot.get(Sample.DELETED), false);
				samplesCq.where(caseIdsExpr.in(resultCaseIds), eliminateDeletedSamplesFilter);
				samplesList = em.createQuery(samplesCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
				samples = samplesList.stream().collect(Collectors.groupingBy(s -> s.getCaseId()));
			}

			List<VisitSummaryExportDetails> visitSummaries = null;
			if (featureConfigurationFacade.isFeatureEnabled(FeatureType.CASE_FOLLOWUP)
				&& ExportHelper.shouldExportFields(
					exportConfiguration,
					CaseExportDto.NUMBER_OF_VISITS,
					CaseExportDto.LAST_COOPERATIVE_VISIT_DATE,
					CaseExportDto.LAST_COOPERATIVE_VISIT_SYMPTOMATIC,
					CaseExportDto.LAST_COOPERATIVE_VISIT_SYMPTOMS)) {
				CriteriaQuery<VisitSummaryExportDetails> visitsCq = cb.createQuery(VisitSummaryExportDetails.class);
				Root<Case> visitsCqRoot = visitsCq.from(Case.class);
				Join<Case, Visit> visitsJoin = visitsCqRoot.join(Case.VISITS, JoinType.LEFT);
				Join<Visit, Symptoms> visitSymptomsJoin = visitsJoin.join(Visit.SYMPTOMS, JoinType.LEFT);

				visitsCq.where(
					CriteriaBuilderHelper
						.and(cb, visitsCqRoot.get(AbstractDomainObject.ID).in(resultCaseIds), cb.isNotEmpty(visitsCqRoot.get(Case.VISITS))));
				visitsCq.multiselect(
					visitsCqRoot.get(AbstractDomainObject.ID),
					visitsJoin.get(Visit.VISIT_DATE_TIME),
					visitsJoin.get(Visit.VISIT_STATUS),
					visitSymptomsJoin);

				visitSummaries = em.createQuery(visitsCq).getResultList();
			}

			Map<Long, List<Immunization>> immunizations = null;
			if ((exportType == null || exportType == CaseExportType.CASE_SURVEILLANCE)
				&& (exportConfiguration == null
					|| exportConfiguration.getProperties()
						.stream()
						.anyMatch(p -> StringUtils.equalsAny(p, ExportHelper.getVaccinationExportProperties())))) {
				List<Immunization> immunizationList;
				CriteriaQuery<Immunization> immunizationsCq = cb.createQuery(Immunization.class);
				Root<Immunization> immunizationsCqRoot = immunizationsCq.from(Immunization.class);
				Join<Immunization, Person> personJoin = immunizationsCqRoot.join(Immunization.PERSON, JoinType.LEFT);
				Expression<String> personIdsExpr = personJoin.get(Person.ID);
				immunizationsCq.where(
					CriteriaBuilderHelper.and(
						cb,
						cb.or(
							cb.equal(immunizationsCqRoot.get(Immunization.MEANS_OF_IMMUNIZATION), MeansOfImmunization.VACCINATION),
							cb.equal(immunizationsCqRoot.get(Immunization.MEANS_OF_IMMUNIZATION), MeansOfImmunization.VACCINATION_RECOVERY)),
						personIdsExpr.in(resultList.stream().map(CaseExportDto::getPersonId).collect(Collectors.toList()))));
				immunizationsCq.select(immunizationsCqRoot);
				immunizationList = em.createQuery(immunizationsCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
				immunizations = immunizationList.stream().collect(Collectors.groupingBy(i -> i.getPerson().getId()));
			}

			// Load latest events info
			// Adding a second query here is not perfect, but selecting the last event with a criteria query
			// doesn't seem to be possible and using a native query is not an option because of user filters
			List<EventSummaryDetails> eventSummaries = null;
			if (ExportHelper.shouldExportFields(
				exportConfiguration,
				CaseExportDto.LATEST_EVENT_ID,
				CaseExportDto.LATEST_EVENT_STATUS,
				CaseExportDto.LATEST_EVENT_TITLE)) {

				eventSummaries = eventService.getEventSummaryDetailsByCases(resultCaseIds);
			}

			Map<Long, UserReference> caseUsers = getCaseUsersForExport(resultList, exportConfiguration);

			Pseudonymizer pseudonymizer = getPseudonymizerForDtoWithClinician(I18nProperties.getCaption(Captions.inaccessibleValue));

			for (CaseExportDto exportDto : resultList) {
				final boolean inJurisdiction = exportDto.getInJurisdiction();

				if (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseExportDto.COUNTRY)) {
					exportDto.setCountry(configFacade.getEpidPrefix());
				}
				if (ExportHelper.shouldExportFields(exportConfiguration, CaseDataDto.SYMPTOMS)) {
					Optional.ofNullable(symptoms.get(exportDto.getSymptomsId()))
						.ifPresent(symptom -> exportDto.setSymptoms(SymptomsFacadeEjb.toSymptomsDto(symptom)));
				}
				if (healthConditions != null) {
					Optional.ofNullable(healthConditions.get(exportDto.getHealthConditionsId()))
						.ifPresent(healthCondition -> exportDto.setHealthConditions(HealthConditionsMapper.toDto(healthCondition)));
				}
				if (firstPreviousHospitalizations != null) {
					Optional.ofNullable(firstPreviousHospitalizations.get(exportDto.getHospitalizationId()))
						.ifPresent(firstPreviousHospitalization -> {
							if (firstPreviousHospitalization.getHealthFacility() != null) {
								exportDto.setInitialDetectionPlace(
									FacilityHelper.buildFacilityString(
										firstPreviousHospitalization.getHealthFacility().getUuid(),
										firstPreviousHospitalization.getHealthFacility().getName(),
										firstPreviousHospitalization.getHealthFacilityDetails()));
							} else {
								exportDto.setInitialDetectionPlace(I18nProperties.getCaption(Captions.unknown));
							}
						});
					if (StringUtils.isEmpty(exportDto.getInitialDetectionPlace())) {
						if (!StringUtils.isEmpty(exportDto.getHealthFacility())) {
							exportDto.setInitialDetectionPlace(exportDto.getHealthFacility());
						} else {
							exportDto.setInitialDetectionPlace(exportDto.getPointOfEntry());
						}
					}
				}
				if (sourceCaseClassifications != null) {
					Optional.ofNullable(sourceCaseClassifications.get(exportDto.getId()))
						.ifPresent(sourceCaseClassification -> exportDto.setMaxSourceCaseClassification(sourceCaseClassification));
				}
				if (exposures != null) {
					Optional.ofNullable(exposures.get(exportDto.getEpiDataId())).ifPresent(caseExposures -> {
						StringBuilder travelHistoryBuilder = new StringBuilder();
						if (caseExposures.stream().anyMatch(e -> ExposureType.BURIAL.equals(e.getExposureType()))) {
							exportDto.setBurialAttended(true);
						}
						caseExposures.stream().filter(e -> ExposureType.TRAVEL.equals(e.getExposureType())).forEach(exposure -> {
							Location location = exposure.getLocation();
							travelHistoryBuilder.append(
								EpiDataHelper.buildDetailedTravelString(
									LocationReferenceDto.buildCaption(
										location.getRegion() != null ? location.getRegion().getName() : null,
										location.getDistrict() != null ? location.getDistrict().getName() : null,
										location.getCommunity() != null ? location.getCommunity().getName() : null,
										location.getCity(),
										location.getStreet(),
										location.getHouseNumber(),
										location.getAdditionalInformation()),
									exposure.getDescription(),
									exposure.getStartDate(),
									exposure.getEndDate(),
									userLanguage))
								.append(", ");
						});
						if (travelHistoryBuilder.length() > 0) {
							exportDto.setTraveled(true);
							travelHistoryBuilder.delete(travelHistoryBuilder.lastIndexOf(", "), travelHistoryBuilder.length() - 1);
						}
						exportDto.setTravelHistory(travelHistoryBuilder.toString());
					});
				}
				if (samples != null) {
					Optional.ofNullable(samples.get(exportDto.getId())).ifPresent(caseSamples -> {
						int count = 0;
						caseSamples.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
						for (EmbeddedSampleExportDto sampleDto : caseSamples) {

							switch (++count) {
							case 1:
								exportDto.setSample1(sampleDto);
								break;
							case 2:
								exportDto.setSample2(sampleDto);
								break;
							case 3:
								exportDto.setSample3(sampleDto);
								break;
							default:
								exportDto.addOtherSample(sampleDto);
							}
						}
					});
				}
				if (immunizations != null) {
					Optional.ofNullable(immunizations.get(exportDto.getPersonId())).ifPresent(caseImmunizations -> {
						List<Immunization> filteredImmunizations =
							caseImmunizations.stream().filter(i -> i.getDisease() == exportDto.getDisease()).collect(Collectors.toList());
						if (!filteredImmunizations.isEmpty()) {
							filteredImmunizations.sort(Comparator.comparing(i -> ImmunizationEntityHelper.getDateForComparison(i, false)));
							Immunization mostRecentImmunization = filteredImmunizations.get(filteredImmunizations.size() - 1);
							Integer numberOfDoses = mostRecentImmunization.getNumberOfDoses();
							Date onsetDate = Optional.ofNullable(symptoms.get(exportDto.getSymptomsId())).map(Symptoms::getOnsetDate).orElse(null);

							List<Vaccination> relevantSortedVaccinations = vaccinationService.getRelevantSortedVaccinations(
								filteredImmunizations.stream().flatMap(i -> i.getVaccinations().stream()).collect(Collectors.toList()),
								onsetDate,
								exportDto.getReportDate());
							Vaccination firstVaccination = null;
							Vaccination lastVaccination = null;

							if (CollectionUtils.isNotEmpty(relevantSortedVaccinations)) {
								firstVaccination = relevantSortedVaccinations.get(0);
								lastVaccination = relevantSortedVaccinations.get(relevantSortedVaccinations.size() - 1);
								exportDto.setFirstVaccinationDate(firstVaccination.getVaccinationDate());
								exportDto.setLastVaccinationDate(lastVaccination.getVaccinationDate());
								exportDto.setVaccineName(lastVaccination.getVaccineName());
								exportDto.setOtherVaccineName(lastVaccination.getOtherVaccineName());
								exportDto.setVaccineManufacturer(lastVaccination.getVaccineManufacturer());
								exportDto.setOtherVaccineManufacturer(lastVaccination.getOtherVaccineManufacturer());
								exportDto.setVaccinationInfoSource(lastVaccination.getVaccinationInfoSource());
								exportDto.setVaccineAtcCode(lastVaccination.getVaccineAtcCode());
								exportDto.setVaccineBatchNumber(lastVaccination.getVaccineBatchNumber());
								exportDto.setVaccineUniiCode(lastVaccination.getVaccineUniiCode());
								exportDto.setVaccineInn(lastVaccination.getVaccineInn());
							}

							exportDto.setNumberOfDoses(
								numberOfDoses != null ? String.valueOf(numberOfDoses) : getNumberOfDosesFromVaccinations(lastVaccination));
						}
					});
				}
				if (visitSummaries != null) {
					List<VisitSummaryExportDetails> visits =
						visitSummaries.stream().filter(v -> v.getContactId() == exportDto.getId()).collect(Collectors.toList());

					VisitSummaryExportDetails lastCooperativeVisit = visits.stream()
						.filter(v -> v.getVisitStatus() == VisitStatus.COOPERATIVE)
						.max(Comparator.comparing(VisitSummaryExportDetails::getVisitDateTime))
						.orElse(null);

					exportDto.setNumberOfVisits(visits.size());
					if (lastCooperativeVisit != null) {
						exportDto.setLastCooperativeVisitDate(lastCooperativeVisit.getVisitDateTime());

						SymptomsDto visitSymptoms = SymptomsFacadeEjb.toSymptomsDto(lastCooperativeVisit.getSymptoms());
						pseudonymizer.pseudonymizeDto(SymptomsDto.class, visitSymptoms, inJurisdiction, null);

						exportDto.setLastCooperativeVisitSymptoms(SymptomsHelper.buildSymptomsHumanString(visitSymptoms, true, userLanguage));
						exportDto.setLastCooperativeVisitSymptomatic(
							visitSymptoms.getSymptomatic() == null
								? YesNoUnknown.UNKNOWN
								: (visitSymptoms.getSymptomatic() ? YesNoUnknown.YES : YesNoUnknown.NO));
					}
				}

				if (eventSummaries != null && exportDto.getEventCount() != 0) {
					eventSummaries.stream()
						.filter(v -> v.getCaseId() == exportDto.getId())
						.max(Comparator.comparing(EventSummaryDetails::getEventDate))
						.ifPresent(eventSummary -> {
							exportDto.setLatestEventId(eventSummary.getEventUuid());
							exportDto.setLatestEventStatus(eventSummary.getEventStatus());
							exportDto.setLatestEventTitle(eventSummary.getEventTitle());
						});
				}

				if (!caseUsers.isEmpty()) {
					if (exportDto.getReportingUserId() != null) {
						UserReference user = caseUsers.get(exportDto.getReportingUserId());

						exportDto.setReportingUserName(user.getName());
						exportDto.setReportingUserRoles(
							user.getUserRoles().stream().map(userRole -> UserRoleFacadeEjb.toReferenceDto(userRole)).collect(Collectors.toSet()));
					}

					if (exportDto.getFollowUpStatusChangeUserId() != null) {
						UserReference user = caseUsers.get(exportDto.getFollowUpStatusChangeUserId());

						exportDto.setFollowUpStatusChangeUserName(user.getName());
						exportDto.setFollowUpStatusChangeUserRoles(
							user.getUserRoles().stream().map(userRole -> UserRoleFacadeEjb.toReferenceDto(userRole)).collect(Collectors.toSet()));
					}
				}

				pseudonymizer.pseudonymizeDto(CaseExportDto.class, exportDto, inJurisdiction, c -> {
					pseudonymizer.pseudonymizeDto(BirthDateDto.class, c.getBirthdate(), inJurisdiction, null);
					pseudonymizer.pseudonymizeDto(EmbeddedSampleExportDto.class, c.getSample1(), inJurisdiction, null);
					pseudonymizer.pseudonymizeDto(EmbeddedSampleExportDto.class, c.getSample2(), inJurisdiction, null);
					pseudonymizer.pseudonymizeDto(EmbeddedSampleExportDto.class, c.getSample3(), inJurisdiction, null);
					pseudonymizer.pseudonymizeDtoCollection(EmbeddedSampleExportDto.class, c.getOtherSamples(), s -> inJurisdiction, null);
					pseudonymizer.pseudonymizeDto(BurialInfoDto.class, c.getBurialInfo(), inJurisdiction, null);
					pseudonymizer.pseudonymizeDto(SymptomsDto.class, c.getSymptoms(), inJurisdiction, null);
				});
			}
		}

		caseCriteria.setMustHaveCaseManagementData(previousCaseManagementDataCriteria);

		return resultList;
	}

	private Subquery<Boolean> outbreakSq(CaseQueryContext caseQueryContext) {

		final CriteriaBuilder cb = caseQueryContext.getCriteriaBuilder();
		final CaseJoins joins = caseQueryContext.getJoins();
		final CriteriaQuery<?> cq = caseQueryContext.getQuery();
		final From<?, Case> caseRoot = caseQueryContext.getRoot();

		final Subquery<Boolean> outbreakSubquery = cq.subquery(Boolean.class);
		final Root<Outbreak> outbreakRoot = outbreakSubquery.from(Outbreak.class);
		final Join<Outbreak, District> districtJoin = outbreakRoot.join(Outbreak.DISTRICT, JoinType.LEFT);
		outbreakSubquery.select(outbreakRoot.get(Outbreak.ID));
		outbreakSubquery.where(cb.and(cb.equal(districtJoin.get(District.ID), joins.getDistrict().

			get(District.ID)),
			cb.equal(outbreakRoot.get(Outbreak.DISEASE), caseRoot.get(Case.DISEASE)),
			cb.lessThanOrEqualTo(outbreakRoot.get(Outbreak.START_DATE), caseRoot.get(Case.REPORT_DATE)),
			cb.or(
				cb.isNull(outbreakRoot.get(Outbreak.END_DATE)),
				cb.greaterThanOrEqualTo(outbreakRoot.get(Outbreak.END_DATE), caseRoot.get(Case.REPORT_DATE)))));
		return outbreakSubquery;
	}

	private Subquery<Long> clinicalVisitSq(CriteriaBuilder cb, CriteriaQuery<CaseExportDto> cq, Root<Case> caseRoot) {
		Subquery<Long> clinicalVisitCountSq = cq.subquery(Long.class);
		Root<ClinicalVisit> clinicalVisitRoot = clinicalVisitCountSq.from(ClinicalVisit.class);
		Join<ClinicalVisit, ClinicalCourse> clinicalVisitClinicalCourseJoin = clinicalVisitRoot.join(ClinicalVisit.CLINICAL_COURSE, JoinType.LEFT);
		clinicalVisitCountSq.where(
			cb.and(cb.equal(clinicalVisitClinicalCourseJoin.get(ClinicalCourse.ID), caseRoot.get(Case.CLINICAL_COURSE).get(ClinicalCourse.ID))));
		clinicalVisitCountSq.select(cb.countDistinct(clinicalVisitRoot.get(ClinicalVisit.ID)));
		return clinicalVisitCountSq;
	}

	private Map<Long, UserReference> getCaseUsersForExport(List<CaseExportDto> resultList, ExportConfigurationDto exportConfiguration) {
		Map<Long, UserReference> caseUsers = Collections.emptyMap();
		if (exportConfiguration == null
			|| exportConfiguration.getProperties().contains(CaseDataDto.REPORTING_USER)
			|| exportConfiguration.getProperties().contains(CaseDataDto.FOLLOW_UP_STATUS_CHANGE_USER)) {
			Set<Long> userIds = resultList.stream()
				.map((c -> Arrays.asList(c.getReportingUserId(), c.getFollowUpStatusChangeUserId())))
				.flatMap(Collection::stream)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
			caseUsers = userService.getUserReferencesByIds(userIds).stream().collect(Collectors.toMap(UserReference::getId, Function.identity()));
		}

		return caseUsers;
	}

	private String getNumberOfDosesFromVaccinations(Vaccination vaccination) {
		return vaccination != null ? vaccination.getVaccineDose() : "";
	}

	@Override
	public List<String> getAllActiveUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveUuids();
	}

	@RightsAllowed({
		UserRight._DASHBOARD_SURVEILLANCE_VIEW, })
	public Long countCasesForMap(
		RegionReferenceDto regionRef,
		DistrictReferenceDto districtRef,
		Disease disease,
		Date from,
		Date to,
		NewCaseDateType dateType) {
		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);

		return service.countCasesForMap(region, district, disease, from, to, dateType);
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_SURVEILLANCE_VIEW, })
	public List<MapCaseDto> getCasesForMap(
		RegionReferenceDto regionRef,
		DistrictReferenceDto districtRef,
		Disease disease,
		Date from,
		Date to,
		NewCaseDateType dateType) {

		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);

		List<MapCaseDto> cases = service.getCasesForMap(region, district, disease, from, to, dateType);
		// todo shouldn't this also use the overridden createPseudonymizer method?
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		pseudonymizer.pseudonymizeDtoCollection(
			MapCaseDto.class,
			cases,
			MapCaseDto::getInJurisdiction,
			(c, isInJurisdiction) -> pseudonymizer.pseudonymizeDto(PersonReferenceDto.class, c.getPerson(), isInJurisdiction, null));

		return cases;
	}

	@Override
	public List<CaseDataDto> getAllCasesOfPerson(String personUuid) {

		List<Case> entities = service.findBy(new CaseCriteria().person(new PersonReferenceDto(personUuid)), false);
		return toPseudonymizedDtos(entities);
	}

	@Override
	public List<CaseReferenceDto> getRandomCaseReferences(CaseCriteria criteria, int count, Random randomGenerator) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);

		Predicate filter = service.createUserFilter(caseQueryContext, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
		filter = CriteriaBuilderHelper.and(cb, filter, service.createCriteriaFilter(criteria, caseQueryContext));
		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(caze.get(Case.UUID)));
		cq.select(caze.get(Case.UUID));

		List<String> uuids = em.createQuery(cq).getResultList();
		if (uuids.isEmpty()) {
			return null;
		}

		return randomGenerator.ints(count, 0, uuids.size()).mapToObj(i -> new CaseReferenceDto(uuids.get(i))).collect(Collectors.toList());
	}

	@Override
	public List<CaseSelectionDto> getSimilarCases(CaseSimilarityCriteria criteria) {

		List<CaseSelectionDto> entries = service.getSimilarCases(criteria);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(CaseSelectionDto.class, entries, CaseSelectionDto::isInJurisdiction, null);

		return entries;
	}

	@Override
	public List<CaseDataDto> getRelevantCasesForVaccination(VaccinationDto vaccinationDto) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Case> cq = cb.createQuery(Case.class);
		final Root<Case> caze = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		final CaseJoins joins = caseQueryContext.getJoins();

		Vaccination vaccination = vaccinationService.getByUuid(vaccinationDto.getUuid());
		Join<Case, Person> person = joins.getPerson();
		Join<Person, Immunization> immunizationJoin = person.join(Person.IMMUNIZATIONS, JoinType.LEFT);
		Join<Immunization, Vaccination> vaccinationsJoin = immunizationJoin.join(Immunization.VACCINATIONS, JoinType.LEFT);

		Predicate predicate = cb.in(vaccinationsJoin).value(vaccination);
		cq.where(predicate);
		cq.select(caze);

		List<Case> cases = em.createQuery(cq).getResultList();
		return toDtos(cases.stream().filter(c -> vaccinationService.isVaccinationRelevant(c, vaccination)));
	}

	@Override
	public boolean hasOtherValidVaccination(CaseDataDto caze, String vaccinationUuid) {
		List<VaccinationDto> relevantVaccinationsForCase = vaccinationFacade.getRelevantVaccinationsForCase(caze);
		//checking if the vaccination selected for delete is in the relevant vaccinations of the case
		return relevantVaccinationsForCase.stream().anyMatch(v -> !v.getUuid().equals(vaccinationUuid));
	}

	@Override
	public Pair<RegionReferenceDto, DistrictReferenceDto> getRegionAndDistrictRefsOf(CaseReferenceDto caze) {
		return caseService.getRegionAndDistrictRefsOf(caze);
	}

	@Override
	public List<CaseMergeIndexDto[]> getCasesForDuplicateMerging(
		CaseCriteria criteria,
		@Min(1) Integer limit,
		boolean showDuplicatesWithDifferentRegion) {
		List<CaseMergeIndexDto[]> cases =
			service.getCasesForDuplicateMerging(criteria, limit, showDuplicatesWithDifferentRegion, configFacade.getNameSimilarityThreshold());

		for (CaseMergeIndexDto[] caze : cases) {
			pseudonymizeCasePairs(caze);
		}

		return cases;
	}

	public void pseudonymizeCasePairs(CaseMergeIndexDto[] cazePair) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));

		Arrays.stream(cazePair).forEach(caze -> {
			Boolean isInJurisdiction = caze.getInJurisdiction();
			pseudonymizer.pseudonymizeDto(
				CaseMergeIndexDto.class,
				caze,
				isInJurisdiction,
				c -> pseudonymizer.pseudonymizeDto(AgeAndBirthDateDto.class, caze.getAgeAndBirthDate(), isInJurisdiction, null));
		});
	}

	@RightsAllowed(UserRight._CASE_EDIT)
	public void updateCompleteness(String caseUuid) {
		service.updateCompleteness(caseUuid);
	}

	@Override
	public CaseDataDto getCaseDataByUuid(String uuid) {
		// todo this plainly duplicates getByUuid from AbstractCoreFacade
		return toPseudonymizedDto(service.getByUuid(uuid, true));
	}

	private CaseDataDto getCaseDataWithoutPseudonyimization(String uuid) {
		return toDto(service.getByUuid(uuid, true));
	}

	@Override
	public CaseReferenceDto getReferenceByUuid(String uuid) {
		return convertToReferenceDto(service.getByUuid(uuid));
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_CREATE,
		UserRight._CASE_EDIT })
	public CaseDataDto save(@Valid @NotNull CaseDataDto dto) throws ValidationRuntimeException {
		return save(dto, true, true, true);
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_CREATE,
		UserRight._CASE_EDIT })
	public CoreAndPersonDto<CaseDataDto> save(@Valid @NotNull CoreAndPersonDto<CaseDataDto> coreAndPersonDto) throws ValidationRuntimeException {
		CaseDataDto caseDto = coreAndPersonDto.getCoreData();
		CoreAndPersonDto savedCoreAndPersonDto = new CoreAndPersonDto();
		if (coreAndPersonDto.getPerson() != null) {
			PersonDto newlyCreatedPersonDto = personFacade.save(coreAndPersonDto.getPerson());
			caseDto.setPerson(newlyCreatedPersonDto.toReference());
			savedCoreAndPersonDto.setPerson(newlyCreatedPersonDto);
		}
		CaseDataDto savedCaseData = save(caseDto, true, true, true);
		savedCoreAndPersonDto.setCoreData(savedCaseData);
		return savedCoreAndPersonDto;
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_EDIT })
	public List<ProcessedEntity> saveBulkCase(
		List<String> caseUuidList,
		@Valid CaseBulkEditData updatedCaseBulkEditData,
		boolean diseaseChange,
		boolean diseaseVariantChange,
		boolean classificationChange,
		boolean investigationStatusChange,
		boolean outcomeChange,
		boolean surveillanceOfficerChange)
		throws ValidationRuntimeException {

		List<ProcessedEntity> processedCases = new ArrayList<>();
		for (String caseUuid : caseUuidList) {
			Case caze = service.getByUuid(caseUuid);
			try {
				if (service.isEditAllowed(caze)) {
					CaseDataDto existingCaseDto = toDto(caze);
					updateCaseWithBulkData(
						updatedCaseBulkEditData,
						caze,
						diseaseChange,
						diseaseVariantChange,
						classificationChange,
						investigationStatusChange,
						outcomeChange,
						surveillanceOfficerChange);
					doSave(caze, true, existingCaseDto, true);
					processedCases.add(new ProcessedEntity(caseUuid, ProcessedEntityStatus.SUCCESS));
				} else {
					processedCases.add(new ProcessedEntity(caseUuid, ProcessedEntityStatus.NOT_ELIGIBLE));
				}
			} catch (Exception e) {
				processedCases.add(new ProcessedEntity(caseUuid, ProcessedEntityStatus.INTERNAL_FAILURE));
				logger.error("The case with uuid {} could not be saved due to an Exception", caseUuid, e);
			}
		}

		return processedCases;
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_EDIT })
	public List<ProcessedEntity> saveBulkEditWithFacilities(
		List<String> caseUuidList,
		@Valid CaseBulkEditData updatedCaseBulkEditData,
		boolean diseaseChange,
		boolean diseaseVariantChange,
		boolean classificationChange,
		boolean investigationStatusChange,
		boolean outcomeChange,
		boolean surveillanceOfficerChange,
		Boolean doTransfer) {

		List<ProcessedEntity> processedCases = new ArrayList<>();

		Region newRegion = regionService.getByUuid(updatedCaseBulkEditData.getRegion().getUuid());
		District newDistrict = districtService.getByUuid(updatedCaseBulkEditData.getDistrict().getUuid());
		Community newCommunity =
			updatedCaseBulkEditData.getCommunity() != null ? communityService.getByUuid(updatedCaseBulkEditData.getCommunity().getUuid()) : null;
		Facility newFacility = facilityService.getByUuid(updatedCaseBulkEditData.getHealthFacility().getUuid());

		for (String caseUuid : caseUuidList) {
			Case caze = service.getByUuid(caseUuid);

			try {
				if (service.isEditAllowed(caze)) {
					CaseDataDto existingCaseDto = toDto(caze);
					updateCaseWithBulkData(
						updatedCaseBulkEditData,
						caze,
						diseaseChange,
						diseaseVariantChange,
						classificationChange,
						investigationStatusChange,
						outcomeChange,
						surveillanceOfficerChange);

					caze.setRegion(newRegion);
					caze.setDistrict(newDistrict);
					caze.setCommunity(newCommunity);
					caze.setFacilityType(updatedCaseBulkEditData.getFacilityType());
					caze.setHealthFacility(newFacility);
					caze.setHealthFacilityDetails(updatedCaseBulkEditData.getHealthFacilityDetails());

					CaseLogic.handleHospitalization(toDto(caze), existingCaseDto, doTransfer);
					doSave(caze, true, existingCaseDto, true);
					processedCases.add(new ProcessedEntity(caseUuid, ProcessedEntityStatus.SUCCESS));
				} else {
					processedCases.add(new ProcessedEntity(caseUuid, ProcessedEntityStatus.NOT_ELIGIBLE));
				}
			} catch (Exception e) {
				processedCases.add(new ProcessedEntity(caseUuid, ProcessedEntityStatus.INTERNAL_FAILURE));
				logger.error("The case with uuid {} could not be saved", caseUuid, e);
			}
		}

		return processedCases;
	}

	private void updateCaseWithBulkData(
		CaseBulkEditData updatedCaseBulkEditData,
		Case existingCase,
		boolean diseaseChange,
		boolean diseaseVariantChange,
		boolean classificationChange,
		boolean investigationStatusChange,
		boolean outcomeChange,
		boolean surveillanceOfficerChange) {

		if (diseaseChange) {
			Disease newDisease = updatedCaseBulkEditData.getDisease();
			existingCase.setDisease(newDisease);

			if (!diseaseVariantChange
				&& existingCase.getDiseaseVariant() != null
				&& !customizableEnumFacade
					.existsEnumValue(CustomizableEnumType.DISEASE_VARIANT, existingCase.getDiseaseVariant().getValue(), newDisease)) {
				existingCase.setDiseaseVariant(null);
				existingCase.setDiseaseVariantDetails(null);
			} else if (diseaseVariantChange) {
				DiseaseVariant diseaseVariant = updatedCaseBulkEditData.getDiseaseVariant();
				existingCase.setDiseaseVariant(diseaseVariant);

				existingCase.setDiseaseVariantDetails(diseaseVariant == null ? null : updatedCaseBulkEditData.getDiseaseVariantDetails());
			}

			existingCase.setDiseaseDetails(updatedCaseBulkEditData.getDiseaseDetails());
			existingCase.setPlagueType(updatedCaseBulkEditData.getPlagueType());
			existingCase.setDengueFeverType(updatedCaseBulkEditData.getDengueFeverType());
			existingCase.setRabiesType(updatedCaseBulkEditData.getRabiesType());
		}
		if (classificationChange) {
			existingCase.setCaseClassification(updatedCaseBulkEditData.getCaseClassification());
		}
		if (investigationStatusChange) {
			existingCase.setInvestigationStatus(updatedCaseBulkEditData.getInvestigationStatus());
		}
		if (outcomeChange) {
			existingCase.setOutcome(updatedCaseBulkEditData.getOutcome());
		}
		// Setting the surveillance officer is only allowed if all selected cases are in
		// the same district
		if (surveillanceOfficerChange) {
			UserReferenceDto surveillanceOfficer = updatedCaseBulkEditData.getSurveillanceOfficer();
			existingCase.setSurveillanceOfficer(surveillanceOfficer != null ? userService.getByUuid(surveillanceOfficer.getUuid()) : null);
		}

		if (Objects.nonNull(updatedCaseBulkEditData.getHealthFacilityDetails())) {
			existingCase.setHealthFacilityDetails(updatedCaseBulkEditData.getHealthFacilityDetails());
		}

		if (updatedCaseBulkEditData.getDontShareWithReportingTool() != null) {
			existingCase.setDontShareWithReportingTool(updatedCaseBulkEditData.getDontShareWithReportingTool());
		}
	}

	@RightsAllowed({
		UserRight._CASE_CREATE,
		UserRight._CASE_EDIT })
	public CaseDataDto save(@Valid CaseDataDto dto, boolean handleChanges, boolean checkChangeDate, boolean internal)
		throws ValidationRuntimeException {

		Case existingCase = service.getByUuid(dto.getUuid(), true);
		FacadeHelper.checkCreateAndEditRights(existingCase, userService, UserRight.CASE_CREATE, UserRight.CASE_EDIT);

		if (existingCase != null && internal) {
			EditPermissionType editPermission = service.getEditPermissionType(existingCase);
			if (editPermission == EditPermissionType.OUTSIDE_JURISDICTION) {
				throw new AccessDeniedException(I18nProperties.getString(Strings.errorCaseNotEditableOutsideJurisdiction));
			} else if (editPermission != EditPermissionType.ALLOWED) {
				throw new AccessDeniedException(I18nProperties.getString(Strings.errorCaseNotEditable));
			}
		}

		return caseSave(dto, handleChanges, existingCase, toDto(existingCase), checkChangeDate, internal);
	}

	@RightsAllowed({
		UserRight._CASE_EDIT })
	public CaseDataDto updateFollowUpComment(@Valid @NotNull CaseDataDto dto) throws ValidationRuntimeException {

		Case caze = service.getByUuid(dto.getUuid());
		caze.setFollowUpComment(dto.getFollowUpComment());
		service.ensurePersisted(caze);
		return toPseudonymizedDto(caze);
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_EDIT,
		UserRight._IMMUNIZATION_CREATE,
		UserRight._IMMUNIZATION_EDIT,
		UserRight._IMMUNIZATION_DELETE })
	public void updateVaccinationStatus(CaseReferenceDto caseRef, VaccinationStatus status) {
		Case caze = service.getByReferenceDto(caseRef);
		caze.setVaccinationStatus(status);

		service.ensurePersisted(caze);
	}

	private CaseDataDto caseSave(
		@Valid CaseDataDto dto,
		boolean handleChanges,
		Case existingCaze,
		CaseDataDto existingCaseDto,
		boolean checkChangeDate,
		boolean syncShares)
		throws ValidationRuntimeException {
		SymptomsHelper.updateIsSymptomatic(dto.getSymptoms());

		Pseudonymizer pseudonymizer = createPseudonymizer();

		restorePseudonymizedDto(dto, existingCaseDto, existingCaze, pseudonymizer);

		validateUserRights(dto, existingCaseDto);
		validate(dto);

		externalJournalService.handleExternalJournalPersonUpdateAsync(dto.getPerson());

		Case caze = fillOrBuildEntity(dto, existingCaze, checkChangeDate);

		// Set version number on a new case
		if (caze.getCreationDate() == null && StringUtils.isEmpty(dto.getCreationVersion())) {
			caze.setCreationVersion(InfoProvider.get().getVersion());
		}

		doSave(caze, handleChanges, existingCaseDto, syncShares);

		return toPseudonymizedDto(caze, pseudonymizer);
	}

	@RightsAllowed(UserRight._CASE_EDIT)
	public void syncSharesAsync(ShareTreeCriteria criteria) {
		executorService.schedule(() -> sormasToSormasCaseFacade.syncShares(criteria), 5, TimeUnit.SECONDS);
	}

	private void doSave(Case caze, boolean handleChanges, CaseDataDto existingCaseDto, boolean syncShares) {
		service.ensurePersisted(caze);
		if (handleChanges) {
			updateCaseVisitAssociations(existingCaseDto, caze);
			onCaseChanged(existingCaseDto, caze, syncShares);
		}
	}

	private void updateCaseVisitAssociations(CaseDataDto existingCase, Case caze) {

		if (existingCase != null
			&& Objects.equals(existingCase.getReportDate(), caze.getReportDate())
			&& Objects.equals(existingCase.getFollowUpUntil(), caze.getFollowUpUntil())
			&& existingCase.getDisease() == caze.getDisease()) {
			return;
		}

		if (existingCase != null) {
			for (Visit visit : caze.getVisits()) {
				visit.setCaze(null);
			}
		}

		Set<Visit> allRelevantVisits = visitService.getAllRelevantVisits(
			caze.getPerson(),
			caze.getDisease(),
			CaseLogic.getStartDate(caze.getSymptoms().getOnsetDate(), caze.getSymptoms().getOnsetDate()),
			CaseLogic.getEndDate(caze.getSymptoms().getOnsetDate(), caze.getReportDate(), caze.getFollowUpUntil()));

		for (Visit visit : allRelevantVisits) {
			caze.getVisits().add(visit); // Necessary for further logic during the case save process
			visit.setCaze(caze);
		}
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_CREATE,
		UserRight._CASE_EDIT })
	public void setSampleAssociations(ContactReferenceDto sourceContact, CaseReferenceDto cazeRef) {

		if (sourceContact != null) {
			final Contact contact = contactService.getByUuid(sourceContact.getUuid());
			final Case caze = service.getByUuid(cazeRef.getUuid());
			List<Sample> samples = contact.getSamples().stream().filter(sample -> !sample.isDeleted()).collect(Collectors.toList());

			if (samples.size() > 0) {
				samples.forEach(sample -> {
					if (contact.getDisease() == caze.getDisease() && sample.getAssociatedCase() == null) {
						sample.setAssociatedCase(caze);
						sampleService.ensurePersisted(sample);
					} else if (!DataHelper.isSame(sample.getAssociatedCase(), cazeRef)) {
						sampleFacade.cloneSampleForCase(sample, caze);
					}
				});

				onCaseChanged(toDto(caze), caze);
			}

			// The samples for case are not persisted yet, so use the samples from contact since they are the same
			caze.setFollowUpUntil(service.computeFollowUpuntilDate(caze, contact.getSamples()));
		}
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_CREATE,
		UserRight._CASE_EDIT })
	public void setSampleAssociations(EventParticipantReferenceDto sourceEventParticipant, CaseReferenceDto cazeRef) {

		if (sourceEventParticipant != null) {
			final EventParticipant eventParticipant = eventParticipantService.getByUuid(sourceEventParticipant.getUuid());
			final Case caze = service.getByUuid(cazeRef.getUuid());
			List<Sample> samples = eventParticipant.getSamples().stream().filter(sample -> !sample.isDeleted()).collect(Collectors.toList());

			if (samples.size() > 0) {
				samples.forEach(sample -> {
					if (eventParticipant.getEvent().getDisease() == caze.getDisease() && sample.getAssociatedCase() == null) {
						sample.setAssociatedCase(caze);
						sampleService.ensurePersisted(sample);
					} else if (!sample.getAssociatedCase().getUuid().equals(cazeRef.getUuid())) {
						sampleFacade.cloneSampleForCase(sample, caze);
					}
				});

				onCaseChanged(toDto(caze), caze);
			}

			// The samples for case are not persisted yet, so use the samples from event participant since they are the same
			caze.setFollowUpUntil(service.computeFollowUpuntilDate(caze, eventParticipant.getSamples()));
		}
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_CREATE,
		UserRight._CASE_EDIT })
	public void setSampleAssociationsUnrelatedDisease(EventParticipantReferenceDto sourceEventParticipant, CaseReferenceDto cazeRef) {
		final EventParticipant eventParticipant = eventParticipantService.getByUuid(sourceEventParticipant.getUuid());
		final Case caze = service.getByUuid(cazeRef.getUuid());
		final Disease disease = caze.getDisease();
		eventParticipant.getSamples().stream().filter(sample -> sampleContainsTestForDisease(sample, disease)).forEach(sample -> {
			if (eventParticipant.getEvent().getDisease() == disease && sample.getAssociatedCase() == null) {
				sample.setAssociatedCase(caze);
			} else {
				sampleFacade.cloneSampleForCase(sample, caze);
			}

			// The samples for case are not persisted yet, so use the samples from event participant since they are the same
			caze.setFollowUpUntil(service.computeFollowUpuntilDate(caze, eventParticipant.getSamples()));
		});

	}

	private boolean sampleContainsTestForDisease(Sample sample, Disease disease) {
		return sample.getPathogenTests().stream().anyMatch(test -> test.getTestedDisease().equals(disease));
	}

	@Override
	public void validate(@Valid CaseDataDto caze) throws ValidationRuntimeException {

		// Check whether any required field that does not have a not null constraint in
		// the database is empty
		if (caze.getReportingUser() == null && !caze.isPseudonymized()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportingUser));
		}

		if (caze.getResponsibleRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validResponsibleRegion));
		}
		if (caze.getResponsibleDistrict() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validResponsibleDistrict));
		}
		if (caze.getResponsibleCommunity() != null
			&& !communityFacade.getByUuid(caze.getResponsibleCommunity().getUuid()).getDistrict().equals(caze.getResponsibleDistrict())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noResponsibleCommunityInResponsibleDistrict));
		}
		if ((caze.getCaseOrigin() == null || caze.getCaseOrigin() == CaseOrigin.IN_COUNTRY) && caze.getHealthFacility() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validFacility));
		}
		if (CaseOrigin.POINT_OF_ENTRY.equals(caze.getCaseOrigin()) && caze.getPointOfEntry() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validPointOfEntry));
		}
		if (caze.getDisease() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDisease));
		}
		// Check whether there are any infrastructure errors
		if (!districtFacade.getByUuid(caze.getResponsibleDistrict().getUuid()).getRegion().equals(caze.getResponsibleRegion())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noResponsibleDistrictInResponsibleRegion));
		}
		if (caze.getResponsibleCommunity() != null
			&& !communityFacade.getByUuid(caze.getResponsibleCommunity().getUuid()).getDistrict().equals(caze.getResponsibleDistrict())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noResponsibleCommunityInResponsibleDistrict));
		}
		if (caze.getRegion() != null
			&& caze.getDistrict() != null
			&& !districtFacade.getByUuid(caze.getDistrict().getUuid()).getRegion().equals(caze.getRegion())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noDistrictInRegion));
		}
		if (caze.getDistrict() != null
			&& caze.getCommunity() != null
			&& !communityFacade.getByUuid(caze.getCommunity().getUuid()).getDistrict().equals(caze.getDistrict())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noCommunityInDistrict));
		}
		if (caze.getHealthFacility() != null) {
			FacilityDto healthFacility = facilityFacade.getByUuid(caze.getHealthFacility().getUuid());

			if (caze.getFacilityType() == null) {
				if (!FacilityDto.NONE_FACILITY_UUID.equals(caze.getHealthFacility().getUuid())) {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityType));
				}
			} else if (!caze.getFacilityType().isAccommodation()) {
				throw new ValidationRuntimeException(
					I18nProperties.getValidationError(Validations.notAccomodationFacilityType, caze.getFacilityType()));
			}

			if (caze.getRegion() == null) {
				if (caze.getResponsibleCommunity() == null
					&& healthFacility.getDistrict() != null
					&& !healthFacility.getDistrict().equals(caze.getResponsibleDistrict())) {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInResponsibleDistrict));
				}
				if (caze.getResponsibleCommunity() != null
					&& healthFacility.getCommunity() != null
					&& !caze.getResponsibleCommunity().equals(healthFacility.getCommunity())) {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInResponsibleCommunity));
				}
				if (healthFacility.getRegion() != null && !caze.getResponsibleRegion().equals(healthFacility.getRegion())) {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInResponsibleRegion));
				}
			} else {
				if (caze.getCommunity() == null && healthFacility.getDistrict() != null && !healthFacility.getDistrict().equals(caze.getDistrict())) {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInDistrict));
				}
				if (caze.getCommunity() != null
					&& healthFacility.getCommunity() != null
					&& !caze.getCommunity().equals(healthFacility.getCommunity())) {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInCommunity));
				}
				if (healthFacility.getRegion() != null && !caze.getRegion().equals(healthFacility.getRegion())) {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInRegion));
				}
			}
		}
	}

	public void validateUserRights(CaseDataDto caze, CaseDataDto existingCaze) {
		if (existingCaze != null) {
			if (!DataHelper.isSame(caze.getHealthFacility(), existingCaze.getHealthFacility())) {

				if (existingCaze.getPointOfEntry() != null
					&& caze.getHealthFacility() != null
					&& !userService.hasRight(UserRight.CASE_REFER_FROM_POE)) {
					throw new AccessDeniedException(
						String.format(
							I18nProperties.getString(Strings.errorNoRightsForChangingField),
							I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY)));
				}

				if (existingCaze.getHealthFacility() != null && !userService.hasRight(UserRight.CASE_TRANSFER)) {
					throw new AccessDeniedException(
						String.format(
							I18nProperties.getString(Strings.errorNoRightsForChangingField),
							I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY)));
				}
			}

			if (!userService.hasRight(UserRight.CASE_INVESTIGATE)
				&& (!DataHelper.equal(caze.getInvestigationStatus(), existingCaze.getInvestigationStatus())
					|| !DataHelper.equal(caze.getInvestigatedDate(), existingCaze.getInvestigatedDate()))) {
				throw new AccessDeniedException(
					String.format(
						I18nProperties.getString(Strings.errorNoRightsForChangingMultipleFields),
						I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.INVESTIGATION_STATUS)));
			}

			if (!userService.hasRight(UserRight.CASE_CLASSIFY)
				&& (!DataHelper.equal(caze.getCaseClassification(), existingCaze.getCaseClassification())
					|| !DataHelper.equal(caze.getClassificationComment(), existingCaze.getClassificationComment())
					|| !DataHelper.equal(caze.getClassificationDate(), existingCaze.getClassificationDate())
					|| !DataHelper.equal(caze.getClassificationUser(), existingCaze.getClassificationUser())
					|| !DataHelper.equal(caze.getOutcome(), existingCaze.getOutcome())
					|| !DataHelper.equal(caze.getOutcomeDate(), existingCaze.getOutcomeDate()))) {
				throw new AccessDeniedException(
					String.format(
						I18nProperties.getString(Strings.errorNoRightsForChangingMultipleFields),
						I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION)));
			}

			if (!userService.hasRight(UserRight.CASE_CHANGE_DISEASE)
				&& (!DataHelper.equal(caze.getDisease(), existingCaze.getDisease())
					|| !DataHelper.equal(caze.getDiseaseDetails(), existingCaze.getDiseaseDetails()))) {
				throw new AccessDeniedException(
					String.format(
						I18nProperties.getString(Strings.errorNoRightsForChangingField),
						I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE)));
			}

			if (!userService.hasRight(UserRight.CASE_CHANGE_EPID_NUMBER) && (!DataHelper.equal(caze.getEpidNumber(), existingCaze.getEpidNumber()))) {
				throw new AccessDeniedException(
					String.format(
						I18nProperties.getString(Strings.errorNoRightsForChangingField),
						I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.EPID_NUMBER)));
			}

			if (!userService.hasRight(UserRight.CASE_CLINICIAN_VIEW)
				&& (!DataHelper.equal(caze.getClinicianName(), existingCaze.getClinicianName())
					|| !DataHelper.equal(caze.getClinicianEmail(), existingCaze.getClinicianEmail())
					|| !DataHelper.equal(caze.getClinicianPhone(), existingCaze.getClinicianPhone()))) {
				throw new AccessDeniedException(
					String.format(
						I18nProperties.getString(Strings.errorNoRightsForChangingMultipleFields),
						I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CLINICIAN_NAME)));
			}
		}
	}

	/**
	 * Handles potential changes of related tasks that needs to be done after
	 * a case has been created/saved
	 */
	private void updateTasksOnCaseChanged(Case newCase, CaseDataDto existingCase) {
		// In case that *any* jurisdiction of the case has been changed, we need to see if we need to reassign related tasks.
		// Tasks can be assigned to various user roles and users, therefore it is crucial to make sure
		// that no tasks related to cases are assigned to officers lacking jurisdiction on the case.

		if (existingCase != null) {
			boolean responsibleRegionChanged = !DataHelper.isSame(existingCase.getResponsibleRegion(), newCase.getResponsibleRegion());
			boolean regionChanged = !DataHelper.isSame(existingCase.getRegion(), newCase.getRegion());

			boolean responsibleDistrictChanged = !DataHelper.isSame(existingCase.getResponsibleDistrict(), newCase.getResponsibleDistrict());
			boolean districtChanged = !DataHelper.isSame(existingCase.getDistrict(), newCase.getDistrict());

			// check if infrastructure was changed, added, or removed from the case

			boolean responsibleCommunityChanged = !DataHelper.isSame(existingCase.getResponsibleCommunity(), newCase.getResponsibleCommunity());
			boolean communityChanged = !DataHelper.isSame(existingCase.getCommunity(), newCase.getCommunity());

			boolean facilityChanged = !DataHelper.isSame(existingCase.getHealthFacility(), newCase.getHealthFacility());

			if (responsibleRegionChanged
				|| responsibleDistrictChanged
				|| responsibleCommunityChanged
				|| regionChanged
				|| districtChanged
				|| communityChanged
				|| facilityChanged) {
				reassignTasksOfCase(newCase, false);
			}

		}

		// Create a task to search for other cases for new Plague cases
		if (existingCase == null
			&& newCase.getDisease() == Disease.PLAGUE
			&& featureConfigurationFacade.isTaskGenerationFeatureEnabled(TaskType.ACTIVE_SEARCH_FOR_OTHER_CASES)) {
			createActiveSearchForOtherCasesTask(newCase);
		}
	}

	@PermitAll
	public void onCaseSampleChanged(Case associatedCase) {
		// Update case classification if the feature is enabled
		if (configFacade.isFeatureAutomaticCaseClassification()) {
			if (associatedCase.getCaseClassification() != CaseClassification.NO_CASE) {
				Long pathogenTestsCount = pathogenTestService.countByCase(associatedCase);
				if (pathogenTestsCount == 0) {
					return;
				}
				// calculate classification
				CaseDataDto newCaseDto = toDto(associatedCase);

				CaseClassification classification = caseClassificationFacade.getClassification(newCaseDto);

				// only update when classification by system changes - user may overwrite this
				if (classification != associatedCase.getSystemCaseClassification()) {
					associatedCase.setSystemCaseClassification(classification);

					// really a change? (user may have already set it)
					if (classification != associatedCase.getCaseClassification()) {
						associatedCase.setCaseClassification(classification);
						associatedCase.setClassificationUser(null);
						associatedCase.setClassificationDate(new Date());
					}
				}
			}
		}
	}

	/**
	 * Handles potential changes, processes and backend logic that needs to be done
	 * after a case has been created/saved
	 */
	@PermitAll
	public void onCaseChanged(CaseDataDto existingCase, Case newCase) {
		onCaseChanged(existingCase, newCase, true);
	}

	@PermitAll
	public void onCaseChanged(CaseDataDto existingCase, Case newCase, boolean syncShares) {

		// If its a new case and the case is new and the geo coordinates of the case's
		// health facility are null, set its coordinates to the case's report
		// coordinates, if available. Else if case report coordinates are null set them
		// to the facility's coordinates
		Facility facility = newCase.getHealthFacility();
		if (existingCase == null && facility != null && !FacilityHelper.isOtherOrNoneHealthFacility(facility.getUuid())) {
			if ((facility.getLatitude() == null || facility.getLongitude() == null)
				&& newCase.getReportLat() != null
				&& newCase.getReportLon() != null) {
				facility.setLatitude(newCase.getReportLat());
				facility.setLongitude(newCase.getReportLon());
				facilityService.ensurePersisted(facility);
			} else if (newCase.getReportLat() == null && newCase.getReportLon() == null && newCase.getReportLatLonAccuracy() == null) {
				newCase.setReportLat(facility.getLatitude());
				newCase.setReportLon(facility.getLongitude());
			}
		}

		// Clear facility type if no facility or home was selected
		if (newCase.getHealthFacility() == null || FacilityDto.NONE_FACILITY_UUID.equals(newCase.getHealthFacility().getUuid())) {
			newCase.setFacilityType(null);
		}

		// Generate epid number if missing or incomplete
		FieldVisibilityCheckers fieldVisibilityCheckers = FieldVisibilityCheckers.withCountry(configFacade.getCountryLocale());
		if (fieldVisibilityCheckers.isVisible(CaseDataDto.class, CaseDataDto.EPID_NUMBER)
			&& !CaseLogic.isCompleteEpidNumber(newCase.getEpidNumber())) {
			newCase.setEpidNumber(
				generateEpidNumber(
					newCase.getEpidNumber(),
					newCase.getUuid(),
					newCase.getDisease(),
					newCase.getReportDate(),
					newCase.getResponsibleDistrict().getUuid()));
		}

		// update the plague type based on symptoms
		if (newCase.getDisease() == Disease.PLAGUE) {
			PlagueType plagueType = DiseaseHelper.getPlagueTypeForSymptoms(SymptomsFacadeEjb.toSymptomsDto(newCase.getSymptoms()));
			if (plagueType != newCase.getPlagueType() && plagueType != null) {
				newCase.setPlagueType(plagueType);
			}
		}

		District survOffDistrict = newCase.getSurveillanceOfficer() != null ? newCase.getSurveillanceOfficer().getDistrict() : null;
		Region survOffRegion = newCase.getSurveillanceOfficer() != null ? newCase.getSurveillanceOfficer().getRegion() : null;

		boolean missingSurvOffDistrict =
			survOffDistrict == null || (!survOffDistrict.equals(newCase.getResponsibleDistrict()) && !survOffDistrict.equals(newCase.getDistrict()));
		boolean missingSurvOffRegion =
			survOffRegion == null || (!survOffRegion.equals(newCase.getResponsibleRegion()) && !survOffRegion.equals(newCase.getRegion()));

		if (missingSurvOffDistrict && missingSurvOffRegion) {
			setCaseResponsible(newCase);
		}

		updateInvestigationByStatus(existingCase, newCase);

		updatePersonAndCaseByOutcome(existingCase, newCase);

		updateCaseAge(existingCase, newCase);

		// Change the disease of all contacts if the case disease or disease details have changed
		if (existingCase != null
			&& (newCase.getDisease() != existingCase.getDisease()
				|| !StringUtils.equals(newCase.getDiseaseDetails(), existingCase.getDiseaseDetails()))) {
			for (Contact contact : contactService.findBy(new ContactCriteria().caze(newCase.toReference()), null)) {
				if (contact.getDisease() != newCase.getDisease() || !StringUtils.equals(contact.getDiseaseDetails(), newCase.getDiseaseDetails())) {
					// Only do the change if it hasn't been done in the mobile app before
					contact.setDisease(newCase.getDisease());
					contact.setDiseaseDetails(newCase.getDiseaseDetails());
					contactService.ensurePersisted(contact);
				}
			}
		}

		if (existingCase != null
			&& (newCase.getDisease() != existingCase.getDisease()
				|| !Objects.equals(newCase.getReportDate(), existingCase.getReportDate())
				|| !Objects.equals(newCase.getSymptoms().getOnsetDate(), existingCase.getSymptoms().getOnsetDate()))) {

			// Update follow-up until and status of all contacts
			for (Contact contact : contactService.findBy(new ContactCriteria().caze(newCase.toReference()), null)) {
				contactService.updateFollowUpDetails(contact, false);
				contactService.udpateContactStatus(contact);
			}
			for (Contact contact : contactService.getAllByResultingCase(newCase)) {
				contactService.updateFollowUpDetails(contact, false);
				contactService.udpateContactStatus(contact);
			}
		}

		// Update follow-up
		service.updateFollowUpDetails(newCase, existingCase != null && newCase.getFollowUpStatus() != existingCase.getFollowUpStatus());

		updateTasksOnCaseChanged(newCase, existingCase);

		// Update case classification if the feature is enabled
		CaseClassification classification = null;
		boolean setClassificationInfo = true;
		if (configFacade.isFeatureAutomaticCaseClassification()) {
			if (newCase.getCaseClassification() != CaseClassification.NO_CASE) {
				// calculate classification
				CaseDataDto newCaseDto = toDto(newCase);

				classification = caseClassificationFacade.getClassification(newCaseDto);

				// only update when classification by system changes - user may overwrite this
				if (classification != newCase.getSystemCaseClassification()) {
					newCase.setSystemCaseClassification(classification);

					// really a change? (user may have already set it)
					if (classification != newCase.getCaseClassification()) {
						newCase.setCaseClassification(classification);
						newCase.setClassificationUser(null);
						newCase.setClassificationDate(new Date());
						setClassificationInfo = false;
					}
				}
			}
		}

		if (setClassificationInfo
			&& ((existingCase == null && newCase.getCaseClassification() != CaseClassification.NOT_CLASSIFIED)
				|| (existingCase != null && newCase.getCaseClassification() != existingCase.getCaseClassification()))) {
			newCase.setClassificationUser(userService.getCurrentUser());
			newCase.setClassificationDate(new Date());
		}

		// calculate reference definition for cases
		if (configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			boolean fulfilled = evaluateFulfilledCondition(toDto(newCase), classification);
			newCase.setCaseReferenceDefinition(fulfilled ? CaseReferenceDefinition.FULFILLED : CaseReferenceDefinition.NOT_FULFILLED);
		}

		// Set Yes/No/Unknown fields associated with embedded lists to Yes if the lists
		// are not empty
		if (!newCase.getHospitalization().getPreviousHospitalizations().isEmpty()
			&& YesNoUnknown.YES != newCase.getHospitalization().getHospitalizedPreviously()) {
			newCase.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
		}
		if (!newCase.getEpiData().getExposures().isEmpty() && !YesNoUnknown.YES.equals(newCase.getEpiData().getExposureDetailsKnown())) {
			newCase.getEpiData().setExposureDetailsKnown(YesNoUnknown.YES);
		}

		// Update completeness value
		service.clearCompleteness(newCase);

		// Send an email to all responsible supervisors when the case classification has
		// changed
		if (existingCase != null && existingCase.getCaseClassification() != newCase.getCaseClassification()) {

			try {
				String message = String.format(
					I18nProperties.getString(MessageContents.CONTENT_CASE_CLASSIFICATION_CHANGED),
					DataHelper.getShortUuid(newCase.getUuid()),
					newCase.getCaseClassification().toString());
				notificationService.sendNotifications(
					NotificationType.CASE_CLASSIFICATION_CHANGED,
					JurisdictionHelper.getCaseRegions(newCase),
					null,
					MessageSubject.CASE_CLASSIFICATION_CHANGED,
					message);
			} catch (NotificationDeliveryFailedException e) {
				logger.error("NotificationDeliveryFailedException when trying to notify supervisors about the change of a case classification. ");
			}
		}

		// Send an email to all responsible supervisors when the disease of an
		// Unspecified VHF case has changed
		if (existingCase != null && existingCase.getDisease() == Disease.UNSPECIFIED_VHF && existingCase.getDisease() != newCase.getDisease()) {

			try {
				String message = String.format(
					I18nProperties.getString(MessageContents.CONTENT_DISEASE_CHANGED),
					DataHelper.getShortUuid(newCase.getUuid()),
					existingCase.getDisease().toString(),
					newCase.getDisease().toString());

				notificationService.sendNotifications(
					NotificationType.CASE_DISEASE_CHANGED,
					JurisdictionHelper.getCaseRegions(newCase),
					null,
					MessageSubject.DISEASE_CHANGED,
					message);
			} catch (NotificationDeliveryFailedException e) {
				logger.error("NotificationDeliveryFailedException when trying to notify supervisors about the change of a case disease.");
			}
		}

		// If the case is a newly created case or if it was not in a CONFIRMED status
		// and now the case is in a CONFIRMED status, notify related surveillance officers
		Set<CaseClassification> confirmedClassifications = CaseClassification.getConfirmedClassifications();
		if ((existingCase == null || !confirmedClassifications.contains(existingCase.getCaseClassification()))
			&& confirmedClassifications.contains(newCase.getCaseClassification())) {
			sendConfirmedCaseNotificationsForEvents(newCase);
		}

		if (existingCase != null && syncShares && sormasToSormasFacade.isFeatureConfigured()) {
			syncSharesAsync(new ShareTreeCriteria(existingCase.getUuid()));
		}

		// This logic should be consistent with CaseDataForm.onQuarantineEndChange
		if (existingCase != null && existingCase.getQuarantineTo() != null && !existingCase.getQuarantineTo().equals(newCase.getQuarantineTo())) {
			newCase.setPreviousQuarantineTo(existingCase.getQuarantineTo());
		}

		if (existingCase == null) {
			vaccinationFacade.updateVaccinationStatuses(newCase);
		}

		// On German systems, correct and clean up reinfection data
		if (configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			newCase.setReinfectionDetails(cleanupReinfectionDetails(newCase.getReinfectionDetails()));
			newCase.setReinfectionStatus(CaseLogic.calculateReinfectionStatus(newCase.getReinfectionDetails()));
		}
	}

	private boolean evaluateFulfilledCondition(CaseDataDto newCase, CaseClassification caseClassification) {

		if (newCase.getCaseClassification() != CaseClassification.NO_CASE) {
			List<CaseClassification> fulfilledCaseClassificationOptions =
				Arrays.asList(CaseClassification.CONFIRMED, CaseClassification.CONFIRMED_NO_SYMPTOMS, CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS);

			if (caseClassification == null) {
				caseClassification = caseClassificationFacade.getClassification(newCase);
			}

			List<PathogenTest> casePathogenTests = null;
			if (fulfilledCaseClassificationOptions.contains(caseClassification)) {
				casePathogenTests = pathogenTestService.getAllByCase(newCase.getUuid());
				casePathogenTests = casePathogenTests.stream()
					.filter(
						pathogenTest -> (Arrays.asList(PathogenTestType.PCR_RT_PCR, PathogenTestType.ISOLATION, PathogenTestType.SEQUENCING)
							.contains(pathogenTest.getTestType())
							&& PathogenTestResultType.POSITIVE.equals(pathogenTest.getTestResult())))
					.collect(Collectors.toList());
			}
			return casePathogenTests != null && !casePathogenTests.isEmpty();
		} else {
			return false;
		}
	}

	private void sendConfirmedCaseNotificationsForEvents(Case caze) {

		try {
			notificationService.sendNotifications(
				NotificationType.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
				MessageSubject.EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
				new Object[] {
					caze.getDisease().getName() },
				() -> {
					final Date fromDate = Date.from(Instant.now().minus(Duration.ofDays(30)));
					Map<String, User> eventResponsibleUsers =
						eventService.getAllEventUuidWithResponsibleUserByCaseAfterDateForNotification(caze, fromDate);

					return eventResponsibleUsers.keySet()
						.stream()
						.collect(
							Collectors.toMap(
								eventResponsibleUsers::get,
								eventUuid -> String.format(
									I18nProperties.getString(MessageContents.CONTENT_EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED),
									DataHelper.getShortUuid(eventUuid),
									caze.getDisease().getName(),
									DataHelper.getShortUuid(caze.getUuid()))));
				});
		} catch (NotificationDeliveryFailedException e) {
			logger.error("NotificationDeliveryFailedException when trying to notify event responsible user about a newly confirmed case.");
		}
	}

	@RightsAllowed(UserRight._CASE_EDIT)
	public void setCaseResponsible(Case caze) {
		setCaseResponsible(caze, false, null, null, null);
	}

	@RightsAllowed(UserRight._CASE_EDIT)
	public void setCaseResponsible(
		Case caze,
		boolean neededFeatureAlreadyChecked,
		List<User> possibleUsersForReplacementSurvOfficerBasedOnResponsibleDistrict,
		List<User> possibleUsersForReplacementSurvOfficerBasedOnDistrict,
		Set<User> possibleUsersForReplacementFacilityUsers) {
		if (neededFeatureAlreadyChecked
			|| featureConfigurationFacade
				.isPropertyValueTrue(FeatureType.CASE_SURVEILANCE, FeatureTypeProperty.AUTOMATIC_RESPONSIBILITY_ASSIGNMENT)) {
			District reportingUserDistrict = caze.getReportingUser().getDistrict();

			if (userRoleService.hasUserRight(caze.getReportingUser().getUserRoles(), UserRight.CASE_RESPONSIBLE)
				&& (reportingUserDistrict == null
					|| reportingUserDistrict.equals(caze.getResponsibleDistrict())
					|| reportingUserDistrict.equals(caze.getDistrict()))) {
				caze.setSurveillanceOfficer(caze.getReportingUser());
			} else {
				List<User> hospitalUsers;
				if (possibleUsersForReplacementFacilityUsers == null) {
					hospitalUsers = caze.getHealthFacility() != null && FacilityType.HOSPITAL.equals(caze.getHealthFacility().getType())
						? userService.getFacilityUsersOfHospital(caze.getHealthFacility())
						: new ArrayList<>();
				} else {
					hospitalUsers = possibleUsersForReplacementFacilityUsers.stream()
						.filter(
							user -> user.getHealthFacility().equals(caze.getHealthFacility())
								&& user.getJurisdictionLevel().equals(JurisdictionLevel.HEALTH_FACILITY))
						.collect(Collectors.toList());
				}
				Random rand = new Random();

				if (!hospitalUsers.isEmpty()) {
					caze.setSurveillanceOfficer(
						hospitalUsers.stream()
							.filter(user -> !UserHelper.isRestrictedToAssignEntities(user))
							.collect(Collectors.toList())
							.get(rand.nextInt(hospitalUsers.size()))
							.getAssociatedOfficer());
				}

				else {
					User survOff = null;
					if (caze.getResponsibleDistrict() != null) {
						if (possibleUsersForReplacementSurvOfficerBasedOnResponsibleDistrict == null) {
							survOff = getRandomDistrictCaseResponsible(caze.getResponsibleDistrict());
						} else if (!possibleUsersForReplacementSurvOfficerBasedOnResponsibleDistrict.isEmpty()) {
							List<User> collect = possibleUsersForReplacementSurvOfficerBasedOnResponsibleDistrict.stream()
								.filter(user -> caze.getResponsibleDistrict().equals(user.getDistrict()))
								.collect(Collectors.toList());
							survOff = collect.size() > 0 ? collect.get(new Random().nextInt(collect.size())) : null;
						}
					}

					if (survOff == null && caze.getDistrict() != null) {
						if (possibleUsersForReplacementSurvOfficerBasedOnDistrict == null) {
							survOff = getRandomDistrictCaseResponsible(caze.getDistrict());
						} else if (!possibleUsersForReplacementSurvOfficerBasedOnDistrict.isEmpty()) {
							List<User> collect = possibleUsersForReplacementSurvOfficerBasedOnDistrict.stream()
								.filter(user -> caze.getDistrict().equals(user.getDistrict()))
								.collect(Collectors.toList());
							survOff = collect.size() > 0 ? collect.get(new Random().nextInt(collect.size())) : null;
						}
					}

					caze.setSurveillanceOfficer(survOff);
				}
			}
		}
	}

	/**
	 * Reassigns tasks related to `caze`. With `forceReassignment` beeing false, the function will only reassign
	 * the tasks if the assignees lack jurisdiction on the case. When forced, all tasks will be reassigned.
	 *
	 * @param caze
	 *            the case which related tasks are reassigned.
	 * @param forceReassignment
	 *            force reassignment of case tasks.
	 */
	@RightsAllowed({
		UserRight._CASE_CREATE,
		UserRight._CASE_EDIT })
	public void reassignTasksOfCase(Case caze, boolean forceReassignment) {
		// for each task that is related to the case, the task assignee must match the jurisdiction of the case
		// otherwise we will reassign the task
		for (Task task : caze.getTasks()) {
			if (task.getTaskStatus() != TaskStatus.PENDING) {
				continue;
			}

			User taskAssignee = task.getAssigneeUser();

			if (forceReassignment || taskAssignee == null || !service.inJurisdiction(caze, taskAssignee)) {
				// if there is any mismatch between the jurisdiction of the case and the assigned user,
				// we need to reassign the tasks
				assignOfficerOrSupervisorToTask(caze, task);
				taskService.ensurePersisted(task);
			}

		}

	}

	@Override
	@RightsAllowed(UserRight._SYSTEM)
	public int updateCompleteness() {
		List<String> getCompletenessCheckCaseList = getCompletenessCheckNeededCaseList();

		IterableHelper.executeBatched(getCompletenessCheckCaseList, 10, caseCompletionBatch -> service.updateCompleteness(caseCompletionBatch));

		return getCompletenessCheckCaseList.size();
	}

	@Override
	public PreviousCaseDto getMostRecentPreviousCase(PersonReferenceDto person, Disease disease, Date startDate) {

		return service.getMostRecentPreviousCase(person.getUuid(), disease, startDate);
	}

	private List<String> getCompletenessCheckNeededCaseList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);

		cq.where(cb.isNull(caze.get(Case.COMPLETENESS)));

		cq.orderBy(cb.desc(caze.get(Case.CHANGE_DATE)));
		cq.select(caze.get(Case.UUID));

		return em.createQuery(cq).getResultList();
	}

	@Override
	public String getGenerateEpidNumber(CaseDataDto caze) {
		return generateEpidNumber(
			caze.getEpidNumber(),
			caze.getUuid(),
			caze.getDisease(),
			caze.getReportDate(),
			caze.getResponsibleDistrict().getUuid());
	}

	private String generateEpidNumber(String newEpidNumber, String caseUuid, Disease disease, Date reportDate, String districtUuid) {

		if (!CaseLogic.isEpidNumberPrefix(newEpidNumber)) {
			// Generate a completely new epid number if the prefix is not complete or doesn't match the pattern
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(reportDate);
			String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
			newEpidNumber = districtFacade.getFullEpidCodeForDistrict(districtUuid) + "-" + year + "-";
		}

		// Generate a suffix number
		String highestEpidNumber = service.getHighestEpidNumber(newEpidNumber, caseUuid, disease);
		if (highestEpidNumber == null || highestEpidNumber.endsWith("-")) {
			// If there is not yet a case with a suffix for this epid number in the database, use 001
			newEpidNumber = newEpidNumber + "001";
		} else {
			// Otherwise, extract the suffix from the highest existing epid number and increase it by 1
			String suffixString = highestEpidNumber.substring(highestEpidNumber.lastIndexOf('-'));
			// Remove all non-digits from the suffix to ignore earlier input errors
			suffixString = suffixString.replaceAll("[^\\d]", "");
			if (suffixString.isEmpty()) {
				// If the suffix is empty now, that means there is not yet an epid number with a
				// suffix containing numbers
				newEpidNumber = newEpidNumber + "001";
			} else {
				int suffix = Integer.parseInt(suffixString) + 1;
				newEpidNumber += String.format("%03d", suffix);
			}
		}

		return newEpidNumber;
	}

	private void updatePersonAndCaseByOutcome(CaseDataDto existingCase, Case newCase) {

		if (existingCase != null && newCase.getOutcome() != existingCase.getOutcome()) {

			if (newCase.getOutcome() == null || newCase.getOutcome() == CaseOutcome.NO_OUTCOME) {
				newCase.setOutcomeDate(null);
			}

			if (newCase.getOutcome() == CaseOutcome.DECEASED) {
				if (newCase.getPerson().getPresentCondition() != PresentCondition.DEAD
					&& newCase.getPerson().getPresentCondition() != PresentCondition.BURIED) {
					PersonDto existingPerson = PersonFacadeEjb.toPersonDto(newCase.getPerson());
					newCase.getPerson().setPresentCondition(PresentCondition.DEAD);
					newCase.getPerson().setDeathDate(newCase.getOutcomeDate());
					newCase.getPerson().setCauseOfDeath(CauseOfDeath.EPIDEMIC_DISEASE);
					newCase.getPerson().setCauseOfDeathDisease(newCase.getDisease());
					// attention: this may lead to infinite recursion when not properly implemented
					personFacade.onPersonChanged(existingPerson, newCase.getPerson());
				}
			} else if (newCase.getOutcome() == CaseOutcome.UNKNOWN || newCase.getOutcome() == CaseOutcome.RECOVERED) {

				PersonDto existingPerson = PersonFacadeEjb.toPersonDto(newCase.getPerson());

				if (existingPerson.getCauseOfDeath() == CauseOfDeath.EPIDEMIC_DISEASE
					&& existingPerson.getCauseOfDeathDisease() == newCase.getDisease()) {
					// Make sure no other case associated with the person has Outcome=DECEASED
					CaseCriteria caseCriteria = new CaseCriteria();
					caseCriteria.setPerson(existingPerson.toReference());
					caseCriteria.setOutcome(CaseOutcome.DECEASED);
					if (count(caseCriteria, true) == 0) {
						newCase.getPerson()
							.setPresentCondition(newCase.getOutcome() == CaseOutcome.UNKNOWN ? PresentCondition.UNKNOWN : PresentCondition.ALIVE);
						newCase.getPerson().setBurialDate(null);
						newCase.getPerson().setDeathDate(null);
						newCase.getPerson().setDeathPlaceDescription(null);
						newCase.getPerson().setDeathPlaceType(null);
						newCase.getPerson().setCauseOfDeath(null);
						newCase.getPerson().setCauseOfDeathDetails(null);
						newCase.getPerson().setCauseOfDeathDisease(null);
						personFacade.onPersonChanged(existingPerson, newCase.getPerson());
					}
				}
			}
		} else if (existingCase != null
			&& newCase.getOutcome() == CaseOutcome.DECEASED
			&& (newCase.getPerson().getPresentCondition() == PresentCondition.DEAD
				|| newCase.getPerson().getPresentCondition() == PresentCondition.BURIED)
			&& !Objects.equals(existingCase.getOutcomeDate(), newCase.getOutcomeDate())
			&& newCase.getOutcomeDate() != null
			&& newCase.getPerson().getCauseOfDeath() == CauseOfDeath.EPIDEMIC_DISEASE
			&& newCase.getPerson().getCauseOfDeathDisease() == existingCase.getDisease()) {
			// outcomeDate of a deceased case was changed, but person is already considered dead
			// update the deathdate of the person
			PersonDto existingPerson = PersonFacadeEjb.toPersonDto(newCase.getPerson());
			newCase.getPerson().setDeathDate(newCase.getOutcomeDate());
			personFacade.onPersonChanged(existingPerson, newCase.getPerson());
		} else if (existingCase == null) {
			// new Case; Still compare persons Condition and caseOutcome
			if (newCase.getOutcome() == CaseOutcome.DECEASED
				&& newCase.getPerson().getPresentCondition() != PresentCondition.BURIED
				&& newCase.getPerson().getPresentCondition() != PresentCondition.DEAD) {
				// person is alive but case has outcome deceased
				PersonDto existingPerson = PersonFacadeEjb.toPersonDto(newCase.getPerson());
				newCase.getPerson().setDeathDate(newCase.getOutcomeDate());
				newCase.getPerson().setPresentCondition(PresentCondition.DEAD);
				newCase.getPerson().setCauseOfDeath(CauseOfDeath.EPIDEMIC_DISEASE);
				newCase.getPerson().setCauseOfDeathDisease(newCase.getDisease());
				personFacade.onPersonChanged(existingPerson, newCase.getPerson());
			} else if (newCase.getOutcome() == CaseOutcome.UNKNOWN || newCase.getOutcome() == CaseOutcome.RECOVERED) {

				PersonDto existingPerson = PersonFacadeEjb.toPersonDto(newCase.getPerson());

				if (existingPerson.getCauseOfDeath() == CauseOfDeath.EPIDEMIC_DISEASE
					&& existingPerson.getCauseOfDeathDisease() == newCase.getDisease()) {
					// Make sure no other case associated with the person has Outcome=DECEASED
					CaseCriteria caseCriteria = new CaseCriteria();
					caseCriteria.setPerson(existingPerson.toReference());
					caseCriteria.setOutcome(CaseOutcome.DECEASED);
					if (count(caseCriteria, true) == 0) {
						newCase.getPerson()
							.setPresentCondition(newCase.getOutcome() == CaseOutcome.UNKNOWN ? PresentCondition.UNKNOWN : PresentCondition.ALIVE);
						newCase.getPerson().setBurialDate(null);
						newCase.getPerson().setDeathDate(null);
						newCase.getPerson().setDeathPlaceDescription(null);
						newCase.getPerson().setDeathPlaceType(null);
						newCase.getPerson().setCauseOfDeath(null);
						newCase.getPerson().setCauseOfDeathDetails(null);
						newCase.getPerson().setCauseOfDeathDisease(null);
						personFacade.onPersonChanged(existingPerson, newCase.getPerson());
					}
				}
			}
		}
	}

	private void updateCaseAge(CaseDataDto existingCase, Case newCase) {

		if (newCase.getPerson().getApproximateAge() != null) {
			Date newCaseStartDate = CaseLogic.getStartDate(newCase.getSymptoms().getOnsetDate(), newCase.getReportDate());
			if (existingCase == null || !CaseLogic.getStartDate(existingCase).equals(newCaseStartDate)) {
				if (newCase.getPerson().getApproximateAgeType() == ApproximateAgeType.MONTHS) {
					newCase.setCaseAge(0);
				} else {
					Date personChangeDate = newCase.getPerson().getChangeDate();
					Date referenceDate = newCaseStartDate;
					newCase.setCaseAge(newCase.getPerson().getApproximateAge() - DateHelper.getYearsBetween(referenceDate, personChangeDate));
					if (newCase.getCaseAge() < 0) {
						newCase.setCaseAge(0);
					}
				}

			}
		}
	}

	@Override
	@RightsAllowed(UserRight._CASE_DELETE)
	public void delete(String caseUuid, DeletionDetails deletionDetails)
		throws ExternalSurveillanceToolRuntimeException, SormasToSormasRuntimeException {
		Case caze = service.getByUuid(caseUuid);
		deleteCase(caze, deletionDetails);
	}

	@Override
	@RightsAllowed(UserRight._CASE_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		List<ProcessedEntity> processedCases = new ArrayList<>();
		List<Case> casesToBeDeleted = service.getByUuids(uuids);

		if (casesToBeDeleted != null) {
			casesToBeDeleted.forEach(caseToBeDeleted -> {

				try {
					if (!caseToBeDeleted.isDeleted()) {
						deleteCase(caseToBeDeleted, deletionDetails);
						processedCases.add(new ProcessedEntity(caseToBeDeleted.getUuid(), ProcessedEntityStatus.SUCCESS));
					} else {
						processedCases.add(new ProcessedEntity(caseToBeDeleted.getUuid(), ProcessedEntityStatus.NOT_ELIGIBLE));
					}
				} catch (ExternalSurveillanceToolRuntimeException e) {
					processedCases.add(new ProcessedEntity(caseToBeDeleted.getUuid(), ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE));
					logger.error(
						"The case with uuid {} could not be deleted due to a ExternalSurveillanceToolRuntimeException",
						caseToBeDeleted.getUuid(),
						e);
				} catch (SormasToSormasRuntimeException e) {
					processedCases.add(new ProcessedEntity(caseToBeDeleted.getUuid(), ProcessedEntityStatus.SORMAS_TO_SORMAS_FAILURE));
					logger.error("The case with uuid {} could not be deleted due to a SormasToSormasRuntimeException", caseToBeDeleted.getUuid(), e);
				} catch (AccessDeniedException e) {
					processedCases.add(new ProcessedEntity(caseToBeDeleted.getUuid(), ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
					logger.error("The case with uuid {} could not be deleted due to a AccessDeniedException", caseToBeDeleted.getUuid(), e);
				} catch (Exception e) {
					processedCases.add(new ProcessedEntity(caseToBeDeleted.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error("The case with uuid {} could not be deleted due to an Exception", caseToBeDeleted.getUuid(), e);
				}
			});
		}

		return processedCases;
	}

	@Override
	@RightsAllowed(UserRight._CASE_DELETE)
	public void restore(String uuid) {
		super.restore(uuid);
	}

	@Override
	@RightsAllowed(UserRight._CASE_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		List<ProcessedEntity> processedCases = new ArrayList<>();
		List<Case> casesToBeRestored = caseService.getByUuids(uuids);

		if (casesToBeRestored != null) {
			casesToBeRestored.forEach(caseToBeRestored -> {
				try {
					restore(caseToBeRestored.getUuid());
					processedCases.add(new ProcessedEntity(caseToBeRestored.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (Exception e) {
					processedCases.add(new ProcessedEntity(caseToBeRestored.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error("The case with uuid {} could not be restored due to an Exception", caseToBeRestored.getUuid(), e);
				}
			});
		}
		return processedCases;
	}

	@Override
	@RightsAllowed(UserRight._CASE_DELETE)
	public void deleteWithContacts(String caseUuid, DeletionDetails deletionDetails) {

		Case caze = service.getByUuid(caseUuid);
		deleteCase(caze, deletionDetails);

		Optional.of(caze.getContacts()).ifPresent(cl -> cl.forEach(c -> contactService.delete(c, deletionDetails)));
	}

	private void deleteCase(Case caze, DeletionDetails deletionDetails)
		throws ExternalSurveillanceToolRuntimeException, SormasToSormasRuntimeException, AccessDeniedException {

		if (!caseService.inJurisdictionOrOwned(caze)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageCaseOutsideJurisdictionDeletionDenied));
		}
		externalJournalService.handleExternalJournalPersonUpdateAsync(caze.getPerson().toReference());

		try {
			sormasToSormasFacade.revokePendingShareRequests(caze.getSormasToSormasShares(), true);
		} catch (SormasToSormasException e) {
			throw new SormasToSormasRuntimeException(e);
		}

		service.delete(caze, deletionDetails);
	}

	@Override
	@RightsAllowed(UserRight._CASE_MERGE)
	public void deleteAsDuplicate(String caseUuid, String duplicateOfCaseUuid) {

		Case caze = service.getByUuid(caseUuid);
		Case duplicateOfCase = service.getByUuid(duplicateOfCaseUuid);
		caze.setDuplicateOf(duplicateOfCase);
		service.ensurePersisted(caze);

		delete(caseUuid, new DeletionDetails(DeletionReason.DUPLICATE_ENTRIES, null));
	}

	@RightsAllowed({
		UserRight._CASE_DELETE,
		UserRight._CASE_MERGE,
		UserRight._SYSTEM })
	public void deleteCaseInExternalSurveillanceTool(Case caze) throws ExternalSurveillanceToolException {

		if (externalSurveillanceToolGatewayFacade.isFeatureEnabled() && caze.getExternalID() != null && !caze.getExternalID().isEmpty()) {
			// getByExternalId(caze) throws NPE (see #10820) so we use the service directly.
			// Can potentially be changed back once 10844 is done.
			List<Case> casesWithSameExternalId = service.getByExternalId(caze.getExternalID());
			if (casesWithSameExternalId != null && casesWithSameExternalId.size() == 1 && externalShareInfoService.isCaseShared(caze.getId())) {
				externalSurveillanceToolGatewayFacade.deleteCasesInternal(Collections.singletonList(toDto(caze)));
			}
		}
	}

	@Override
	@RightsAllowed(UserRight._CASE_ARCHIVE)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ProcessedEntity archive(String entityUuid, Date endOfProcessingDate, boolean includeContacts) {
		ProcessedEntity processedEntity = super.archive(entityUuid, endOfProcessingDate);
		if (includeContacts) {
			List<String> caseContacts = contactService.getAllUuidsByCaseUuids(Collections.singletonList(entityUuid));
			contactService.archive(caseContacts);
		}

		return processedEntity;
	}

	@Override
	@RightsAllowed(UserRight._CASE_ARCHIVE)
	public List<ProcessedEntity> archive(List<String> entityUuids, boolean includeContacts) {
		List<ProcessedEntity> processedEntities = super.archive(entityUuids);
		if (includeContacts) {
			List<String> caseContacts = contactService.getAllUuidsByCaseUuids(entityUuids);
			contactService.archive(caseContacts);
		}

		return processedEntities;
	}

	@Override
	@RightsAllowed(UserRight._CASE_ARCHIVE)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ProcessedEntity dearchive(String entityUuid, String dearchiveReason, boolean includeContacts) {
		ProcessedEntity processedEntity = dearchive(Collections.singletonList(entityUuid), dearchiveReason, includeContacts).get(0);

		return processedEntity;
	}

	@Override
	@RightsAllowed(UserRight._CASE_ARCHIVE)
	public List<ProcessedEntity> dearchive(List<String> entityUuids, String dearchiveReason, boolean includeContacts) {
		List<ProcessedEntity> processedEntities = super.dearchive(entityUuids, dearchiveReason);

		if (includeContacts) {
			List<String> caseContacts = contactService.getAllUuidsByCaseUuids(entityUuids);
			contactService.dearchive(caseContacts, dearchiveReason);
		}

		return processedEntities;
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_CREATE,
		UserRight._CASE_EDIT })
	public void setResultingCase(EventParticipantReferenceDto eventParticipantReferenceDto, CaseReferenceDto caseReferenceDto) {
		final EventParticipant eventParticipant = eventParticipantService.getByUuid(eventParticipantReferenceDto.getUuid());
		if (eventParticipant != null) {
			eventParticipant.setResultingCase(caseService.getByUuid(caseReferenceDto.getUuid()));
			eventParticipantService.ensurePersisted(eventParticipant);
		}
	}

	@Override
	public EditPermissionType isEditContactAllowed(String uuid) {
		Case ado = service.getByUuid(uuid);
		return service.isAddContactAllowed(ado);
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return service.getArchivedUuidsSince(since);
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}
		return service.getDeletedUuidsSince(since);
	}

	public static CaseReferenceDto toReferenceDto(Case entity) {

		if (entity == null) {
			return null;
		}

		return entity.toReference();
	}

	@Override
	public void pseudonymizeDto(Case source, CaseDataDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {

		if (dto != null) {
			pseudonymizer.pseudonymizeDto(CaseDataDto.class, dto, inJurisdiction, c -> {
				User currentUser = userService.getCurrentUser();
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), currentUser, dto::setReportingUser);
				pseudonymizer.pseudonymizeUser(source.getClassificationUser(), currentUser, dto::setClassificationUser);

				pseudonymizer.pseudonymizeDto(
					EpiDataDto.class,
					dto.getEpiData(),
					inJurisdiction,
					e -> pseudonymizer.pseudonymizeDtoCollection(
						ExposureDto.class,
						e.getExposures(),
						exp -> inJurisdiction,
						(exp, expInJurisdiction) -> pseudonymizer.pseudonymizeDto(LocationDto.class, exp.getLocation(), expInJurisdiction, null)));

				pseudonymizer.pseudonymizeDto(HealthConditionsDto.class, c.getHealthConditions(), inJurisdiction, null);

				pseudonymizer.pseudonymizeDtoCollection(
					PreviousHospitalizationDto.class,
					c.getHospitalization().getPreviousHospitalizations(),
					h -> inJurisdiction,
					null);

				pseudonymizer.pseudonymizeDto(SymptomsDto.class, dto.getSymptoms(), inJurisdiction, null);
				pseudonymizer.pseudonymizeDto(MaternalHistoryDto.class, dto.getMaternalHistory(), inJurisdiction, null);
			});
		}
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_EDIT,
		UserRight._CASE_EDIT })
	public void restorePseudonymizedDto(CaseDataDto dto, CaseDataDto existingCaseDto, Case caze, Pseudonymizer pseudonymizer) {
		if (existingCaseDto != null) {
			boolean inJurisdiction = service.inJurisdictionOrOwned(caze);

			User currentUser = userService.getCurrentUser();

			pseudonymizer.restoreUser(caze.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restoreUser(caze.getClassificationUser(), currentUser, dto, dto::setClassificationUser);

			pseudonymizer.restorePseudonymizedValues(CaseDataDto.class, dto, existingCaseDto, inJurisdiction);

			EpiDataDto epiData = dto.getEpiData();
			EpiDataDto existingEpiData = existingCaseDto.getEpiData();

			pseudonymizer.restorePseudonymizedValues(EpiDataDto.class, epiData, existingEpiData, inJurisdiction);

			epiData.getExposures().forEach(exposure -> {
				ExposureDto existingExposure =
					existingEpiData.getExposures().stream().filter(exp -> DataHelper.isSame(exposure, exp)).findFirst().orElse(null);

				if (existingExposure != null) {
					pseudonymizer.restorePseudonymizedValues(ExposureDto.class, exposure, existingExposure, inJurisdiction);
					pseudonymizer
						.restorePseudonymizedValues(LocationDto.class, exposure.getLocation(), existingExposure.getLocation(), inJurisdiction);
				}
			});

			pseudonymizer.restorePseudonymizedValues(
				HealthConditionsDto.class,
				dto.getHealthConditions(),
				existingCaseDto.getHealthConditions(),
				inJurisdiction);

			dto.getHospitalization()
				.getPreviousHospitalizations()
				.forEach(
					previousHospitalization -> existingCaseDto.getHospitalization()
						.getPreviousHospitalizations()
						.stream()
						.filter(eh -> DataHelper.isSame(previousHospitalization, eh))
						.findFirst()
						.ifPresent(
							existingPreviousHospitalization -> pseudonymizer.restorePseudonymizedValues(
								PreviousHospitalizationDto.class,
								previousHospitalization,
								existingPreviousHospitalization,
								inJurisdiction)));

			pseudonymizer.restorePseudonymizedValues(SymptomsDto.class, dto.getSymptoms(), existingCaseDto.getSymptoms(), inJurisdiction);
			pseudonymizer
				.restorePseudonymizedValues(MaternalHistoryDto.class, dto.getMaternalHistory(), existingCaseDto.getMaternalHistory(), inJurisdiction);
		}
	}

	public CaseReferenceDto convertToReferenceDto(Case source) {

		CaseReferenceDto dto = toReferenceDto(source);

		if (dto != null) {
			boolean inJurisdiction = service.inJurisdictionOrOwned(source);
			Pseudonymizer.getDefault(userService::hasRight).pseudonymizeDto(CaseReferenceDto.class, dto, inJurisdiction, null);
		}

		return dto;
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_VIEW,
		UserRight._EXTERNAL_VISITS })
	public CaseDataDto toDto(Case source) {
		return toCaseDto(source);
	}

	public static CaseDataDto toCaseDto(Case source) {

		if (source == null) {
			return null;
		}

		CaseDataDto target = new CaseDataDto();
		DtoHelper.fillDto(target, source);

		target.setDisease(source.getDisease());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setDiseaseVariantDetails(source.getDiseaseVariantDetails());
		target.setPlagueType(source.getPlagueType());
		target.setDengueFeverType(source.getDengueFeverType());
		target.setRabiesType(source.getRabiesType());
		target.setCaseClassification(source.getCaseClassification());
		target.setCaseIdentificationSource(source.getCaseIdentificationSource());
		target.setScreeningType(source.getScreeningType());
		target.setClassificationUser(UserFacadeEjb.toReferenceDto(source.getClassificationUser()));
		target.setClassificationDate(source.getClassificationDate());
		target.setClassificationComment(source.getClassificationComment());
		target.setClinicalConfirmation(source.getClinicalConfirmation());
		target.setEpidemiologicalConfirmation(source.getEpidemiologicalConfirmation());
		target.setLaboratoryDiagnosticConfirmation(source.getLaboratoryDiagnosticConfirmation());
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setHospitalization(HospitalizationFacadeEjb.toDto(source.getHospitalization()));
		target.setEpiData(EpiDataFacadeEjb.toDto(source.getEpiData()));
		if (source.getTherapy() != null) {
			target.setTherapy(TherapyFacadeEjb.toDto(source.getTherapy()));
		}
		if (source.getClinicalCourse() != null) {
			target.setClinicalCourse(ClinicalCourseFacadeEjb.toDto(source.getClinicalCourse()));
		}
		target.setHealthConditions(HealthConditionsMapper.toDto(source.getHealthConditions()));
		if (source.getMaternalHistory() != null) {
			target.setMaternalHistory(MaternalHistoryFacadeEjb.toDto(source.getMaternalHistory()));
		}
		if (source.getPortHealthInfo() != null) {
			target.setPortHealthInfo(PortHealthInfoFacadeEjb.toDto(source.getPortHealthInfo()));
		}

		target.setResponsibleRegion(RegionFacadeEjb.toReferenceDto(source.getResponsibleRegion()));
		target.setResponsibleDistrict(DistrictFacadeEjb.toReferenceDto(source.getResponsibleDistrict()));
		target.setResponsibleCommunity(CommunityFacadeEjb.toReferenceDto(source.getResponsibleCommunity()));

		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());

		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setReportDate(source.getReportDate());
		target.setInvestigatedDate(source.getInvestigatedDate());
		target.setRegionLevelDate(source.getRegionLevelDate());
		target.setNationalLevelDate(source.getNationalLevelDate());
		target.setDistrictLevelDate(source.getDistrictLevelDate());

		target.setSurveillanceOfficer(UserFacadeEjb.toReferenceDto(source.getSurveillanceOfficer()));
		target.setClinicianName(source.getClinicianName());
		target.setClinicianPhone(source.getClinicianPhone());
		target.setClinicianEmail(source.getClinicianEmail());
		target.setCaseOfficer(UserFacadeEjb.toReferenceDto(source.getCaseOfficer()));
		target.setSymptoms(SymptomsFacadeEjb.toSymptomsDto(source.getSymptoms()));

		target.setPregnant(source.getPregnant());
		target.setVaccinationStatus(source.getVaccinationStatus());
		target.setSmallpoxVaccinationScar(source.getSmallpoxVaccinationScar());
		target.setSmallpoxVaccinationReceived(source.getSmallpoxVaccinationReceived());
		target.setSmallpoxLastVaccinationDate(source.getSmallpoxLastVaccinationDate());

		target.setEpidNumber(source.getEpidNumber());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		target.setOutcome(source.getOutcome());
		target.setOutcomeDate(source.getOutcomeDate());
		target.setSequelae(source.getSequelae());
		target.setSequelaeDetails(source.getSequelaeDetails());
		target.setNotifyingClinic(source.getNotifyingClinic());
		target.setNotifyingClinicDetails(source.getNotifyingClinicDetails());

		target.setCreationVersion(source.getCreationVersion());
		target.setCaseOrigin(source.getCaseOrigin());
		target.setPointOfEntry(PointOfEntryFacadeEjb.toReferenceDto(source.getPointOfEntry()));
		target.setPointOfEntryDetails(source.getPointOfEntryDetails());
		target.setAdditionalDetails(source.getAdditionalDetails());
		target.setExternalID(source.getExternalID());
		target.setExternalToken(source.getExternalToken());
		target.setInternalToken(source.getInternalToken());
		target.setSharedToCountry(source.isSharedToCountry());
		target.setQuarantine(source.getQuarantine());
		target.setQuarantineTypeDetails(source.getQuarantineTypeDetails());
		target.setQuarantineTo(source.getQuarantineTo());
		target.setQuarantineFrom(source.getQuarantineFrom());
		target.setQuarantineHelpNeeded(source.getQuarantineHelpNeeded());
		target.setQuarantineOrderedVerbally(source.isQuarantineOrderedVerbally());
		target.setQuarantineOrderedOfficialDocument(source.isQuarantineOrderedOfficialDocument());
		target.setQuarantineOrderedVerballyDate(source.getQuarantineOrderedVerballyDate());
		target.setQuarantineOrderedOfficialDocumentDate(source.getQuarantineOrderedOfficialDocumentDate());
		target.setQuarantineHomePossible(source.getQuarantineHomePossible());
		target.setQuarantineHomePossibleComment(source.getQuarantineHomePossibleComment());
		target.setQuarantineHomeSupplyEnsured(source.getQuarantineHomeSupplyEnsured());
		target.setQuarantineHomeSupplyEnsuredComment(source.getQuarantineHomeSupplyEnsuredComment());
		target.setQuarantineExtended(source.isQuarantineExtended());
		target.setQuarantineReduced(source.isQuarantineReduced());
		target.setQuarantineOfficialOrderSent(source.isQuarantineOfficialOrderSent());
		target.setQuarantineOfficialOrderSentDate(source.getQuarantineOfficialOrderSentDate());
		target.setPostpartum(source.getPostpartum());
		target.setTrimester(source.getTrimester());
		target.setFollowUpComment(source.getFollowUpComment());
		target.setFollowUpStatus(source.getFollowUpStatus());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setOverwriteFollowUpUntil(source.isOverwriteFollowUpUntil());
		target.setFacilityType(source.getFacilityType());

		target.setCaseIdIsm(source.getCaseIdIsm());
		target.setContactTracingFirstContactType(source.getContactTracingFirstContactType());
		target.setContactTracingFirstContactDate(source.getContactTracingFirstContactDate());
		target.setWasInQuarantineBeforeIsolation(source.getWasInQuarantineBeforeIsolation());
		target.setQuarantineReasonBeforeIsolation(source.getQuarantineReasonBeforeIsolation());
		target.setQuarantineReasonBeforeIsolationDetails(source.getQuarantineReasonBeforeIsolationDetails());
		target.setEndOfIsolationReason(source.getEndOfIsolationReason());
		target.setEndOfIsolationReasonDetails(source.getEndOfIsolationReasonDetails());

		target.setNosocomialOutbreak(source.isNosocomialOutbreak());
		target.setInfectionSetting(source.getInfectionSetting());

		target.setProhibitionToWork(source.getProhibitionToWork());
		target.setProhibitionToWorkFrom(source.getProhibitionToWorkFrom());
		target.setProhibitionToWorkUntil(source.getProhibitionToWorkUntil());

		target.setReInfection(source.getReInfection());
		target.setPreviousInfectionDate(source.getPreviousInfectionDate());
		target.setReinfectionStatus(source.getReinfectionStatus());
		if (source.getReinfectionDetails() != null) {
			target.setReinfectionDetails(new HashMap<>(source.getReinfectionDetails()));
		}

		target.setBloodOrganOrTissueDonated(source.getBloodOrganOrTissueDonated());

		target.setNotACaseReasonNegativeTest(source.isNotACaseReasonNegativeTest());
		target.setNotACaseReasonPhysicianInformation(source.isNotACaseReasonPhysicianInformation());
		target.setNotACaseReasonDifferentPathogen(source.isNotACaseReasonDifferentPathogen());
		target.setNotACaseReasonOther(source.isNotACaseReasonOther());
		target.setNotACaseReasonDetails(source.getNotACaseReasonDetails());
		target.setSormasToSormasOriginInfo(SormasToSormasOriginInfoFacadeEjb.toDto(source.getSormasToSormasOriginInfo()));
		target.setOwnershipHandedOver(source.getSormasToSormasShares().stream().anyMatch(ShareInfoHelper::isOwnerShipHandedOver));
		target.setFollowUpStatusChangeDate(source.getFollowUpStatusChangeDate());
		if (source.getFollowUpStatusChangeUser() != null) {
			target.setFollowUpStatusChangeUser(source.getFollowUpStatusChangeUser().toReference());
		}
		target.setDontShareWithReportingTool(source.isDontShareWithReportingTool());
		target.setCaseReferenceDefinition(source.getCaseReferenceDefinition());
		target.setPreviousQuarantineTo(source.getPreviousQuarantineTo());
		target.setQuarantineChangeComment(source.getQuarantineChangeComment());

		if (source.getExternalData() != null) {
			target.setExternalData(new HashMap<>(source.getExternalData()));
		}

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected CaseReferenceDto toRefDto(Case aCase) {
		return convertToReferenceDto(aCase);
	}

	public Case fillOrBuildEntity(@NotNull CaseDataDto source, Case target, boolean checkChangeDate) {
		boolean targetWasNull = isNull(target);

		target = DtoHelper.fillOrBuildEntity(source, target, Case::build, checkChangeDate);

		if (targetWasNull) {
			FacadeHelper.setUuidIfDtoExists(target.getHospitalization(), source.getHospitalization());
			FacadeHelper.setUuidIfDtoExists(target.getEpiData(), source.getEpiData());
			FacadeHelper.setUuidIfDtoExists(target.getSymptoms(), source.getSymptoms());
		}

		target.setDisease(source.getDisease());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setDiseaseVariantDetails(source.getDiseaseVariantDetails());
		target.setPlagueType(source.getPlagueType());
		target.setDengueFeverType(source.getDengueFeverType());
		target.setRabiesType(source.getRabiesType());
		if (source.getReportDate() != null) {
			target.setReportDate(source.getReportDate());
		} else {
			// make sure we do have a report date
			target.setReportDate(new Date());
		}
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setInvestigatedDate(source.getInvestigatedDate());
		target.setRegionLevelDate(source.getRegionLevelDate());
		target.setNationalLevelDate(source.getNationalLevelDate());
		target.setDistrictLevelDate(source.getDistrictLevelDate());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		if (source.getCaseClassification() != null) {
			target.setCaseClassification(source.getCaseClassification());
		}
		target.setCaseIdentificationSource(source.getCaseIdentificationSource());
		target.setScreeningType(source.getScreeningType());
		target.setClassificationUser(userService.getByReferenceDto(source.getClassificationUser()));
		target.setClassificationDate(source.getClassificationDate());
		target.setClassificationComment(source.getClassificationComment());
		target.setClinicalConfirmation(source.getClinicalConfirmation());
		target.setEpidemiologicalConfirmation(source.getEpidemiologicalConfirmation());
		target.setLaboratoryDiagnosticConfirmation(source.getLaboratoryDiagnosticConfirmation());
		if (source.getInvestigationStatus() != null) {
			target.setInvestigationStatus(source.getInvestigationStatus());
		}
		target.setHospitalization(hospitalizationFacade.fillOrBuildEntity(source.getHospitalization(), target.getHospitalization(), checkChangeDate));
		target.setEpiData(epiDataFacade.fillOrBuildEntity(source.getEpiData(), target.getEpiData(), checkChangeDate));
		if (source.getTherapy() == null) {
			source.setTherapy(TherapyDto.build());
		}
		target.setTherapy(therapyFacade.fillOrBuildEntity(source.getTherapy(), target.getTherapy(), checkChangeDate));
		if (source.getHealthConditions() == null) {
			source.setHealthConditions(HealthConditionsDto.build());
		}
		target.setHealthConditions(
			healthConditionsMapper.fillOrBuildEntity(source.getHealthConditions(), target.getHealthConditions(), checkChangeDate));
		if (source.getClinicalCourse() == null) {
			source.setClinicalCourse(ClinicalCourseDto.build());
		}
		target.setClinicalCourse(clinicalCourseFacade.fillOrBuildEntity(source.getClinicalCourse(), target.getClinicalCourse(), checkChangeDate));
		if (source.getMaternalHistory() == null) {
			source.setMaternalHistory(MaternalHistoryDto.build());
		}
		target.setMaternalHistory(maternalHistoryFacade.fillOrBuildEntity(source.getMaternalHistory(), target.getMaternalHistory(), checkChangeDate));
		if (source.getPortHealthInfo() == null) {
			source.setPortHealthInfo(PortHealthInfoDto.build());
		}
		target.setPortHealthInfo(portHealthInfoFacade.fillOrBuildEntity(source.getPortHealthInfo(), target.getPortHealthInfo(), checkChangeDate));

		target.setResponsibleRegion(regionService.getByReferenceDto(source.getResponsibleRegion()));
		target.setResponsibleDistrict(districtService.getByReferenceDto(source.getResponsibleDistrict()));
		target.setResponsibleCommunity(communityService.getByReferenceDto(source.getResponsibleCommunity()));

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());

		target.setSurveillanceOfficer(userService.getByReferenceDto(source.getSurveillanceOfficer()));
		target.setClinicianName(source.getClinicianName());
		target.setClinicianPhone(source.getClinicianPhone());
		target.setClinicianEmail(source.getClinicianEmail());
		target.setCaseOfficer(userService.getByReferenceDto(source.getCaseOfficer()));
		target.setSymptoms(symptomsFacade.fillOrBuildEntity(source.getSymptoms(), target.getSymptoms(), checkChangeDate));

		target.setPregnant(source.getPregnant());
		target.setVaccinationStatus(source.getVaccinationStatus());
		target.setSmallpoxVaccinationScar(source.getSmallpoxVaccinationScar());
		target.setSmallpoxVaccinationReceived(source.getSmallpoxVaccinationReceived());
		target.setSmallpoxLastVaccinationDate(source.getSmallpoxLastVaccinationDate());

		target.setEpidNumber(source.getEpidNumber());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		if (source.getOutcome() != null) {
			target.setOutcome(source.getOutcome());
		}
		target.setOutcomeDate(source.getOutcomeDate());
		target.setSequelae(source.getSequelae());
		target.setSequelaeDetails(source.getSequelaeDetails());
		target.setNotifyingClinic(source.getNotifyingClinic());
		target.setNotifyingClinicDetails(source.getNotifyingClinicDetails());

		target.setCreationVersion(source.getCreationVersion());
		if (source.getCaseOrigin() != null) {
			target.setCaseOrigin(source.getCaseOrigin());
		}
		target.setPointOfEntry(pointOfEntryService.getByReferenceDto(source.getPointOfEntry()));
		target.setPointOfEntryDetails(source.getPointOfEntryDetails());
		target.setAdditionalDetails(source.getAdditionalDetails());
		target.setExternalID(source.getExternalID());
		target.setExternalToken(source.getExternalToken());
		target.setInternalToken(source.getInternalToken());
		target.setSharedToCountry(source.isSharedToCountry());
		target.setQuarantine(source.getQuarantine());
		target.setQuarantineTypeDetails(source.getQuarantineTypeDetails());
		target.setQuarantineTo(source.getQuarantineTo());
		target.setQuarantineFrom(source.getQuarantineFrom());
		target.setQuarantineHelpNeeded(source.getQuarantineHelpNeeded());
		target.setQuarantineOrderedVerbally(source.isQuarantineOrderedVerbally());
		target.setQuarantineOrderedOfficialDocument(source.isQuarantineOrderedOfficialDocument());
		target.setQuarantineOrderedVerballyDate(source.getQuarantineOrderedVerballyDate());
		target.setQuarantineOrderedOfficialDocumentDate(source.getQuarantineOrderedOfficialDocumentDate());
		target.setQuarantineHomePossible(source.getQuarantineHomePossible());
		target.setQuarantineHomePossibleComment(source.getQuarantineHomePossibleComment());
		target.setQuarantineHomeSupplyEnsured(source.getQuarantineHomeSupplyEnsured());
		target.setQuarantineHomeSupplyEnsuredComment(source.getQuarantineHomeSupplyEnsuredComment());
		target.setQuarantineExtended(source.isQuarantineExtended());
		target.setQuarantineReduced(source.isQuarantineReduced());
		target.setQuarantineOfficialOrderSent(source.isQuarantineOfficialOrderSent());
		target.setQuarantineOfficialOrderSentDate(source.getQuarantineOfficialOrderSentDate());
		target.setPostpartum(source.getPostpartum());
		target.setTrimester(source.getTrimester());
		target.setFacilityType(source.getFacilityType());
		if (source.getSormasToSormasOriginInfo() != null) {
			target.setSormasToSormasOriginInfo(originInfoService.getByUuid(source.getSormasToSormasOriginInfo().getUuid()));
		}

		// TODO this makes sure follow-up is not overriden from the mobile app side. remove once that is implemented
		if (source.getFollowUpStatus() != null) {
			target.setFollowUpComment(source.getFollowUpComment());
			target.setFollowUpStatus(source.getFollowUpStatus());
			target.setFollowUpUntil(source.getFollowUpUntil());
			target.setOverwriteFollowUpUntil(source.isOverwriteFollowUpUntil());
			target.setFollowUpStatusChangeDate(source.getFollowUpStatusChangeDate());
			target.setFollowUpStatusChangeUser(userService.getByReferenceDto(source.getFollowUpStatusChangeUser()));
		}

		target.setCaseIdIsm(source.getCaseIdIsm());
		target.setContactTracingFirstContactType(source.getContactTracingFirstContactType());
		target.setContactTracingFirstContactDate(source.getContactTracingFirstContactDate());
		target.setQuarantineReasonBeforeIsolation(source.getQuarantineReasonBeforeIsolation());
		target.setWasInQuarantineBeforeIsolation(source.getWasInQuarantineBeforeIsolation());
		target.setQuarantineReasonBeforeIsolationDetails(source.getQuarantineReasonBeforeIsolationDetails());
		target.setEndOfIsolationReason(source.getEndOfIsolationReason());
		target.setEndOfIsolationReasonDetails(source.getEndOfIsolationReasonDetails());

		target.setNosocomialOutbreak(source.isNosocomialOutbreak());
		target.setInfectionSetting(source.getInfectionSetting());

		target.setProhibitionToWork(source.getProhibitionToWork());
		target.setProhibitionToWorkFrom(source.getProhibitionToWorkFrom());
		target.setProhibitionToWorkUntil(source.getProhibitionToWorkUntil());

		target.setReInfection(source.getReInfection());
		target.setPreviousInfectionDate(source.getPreviousInfectionDate());
		target.setReinfectionStatus(source.getReinfectionStatus());
		target.setReinfectionDetails(source.getReinfectionDetails());

		target.setBloodOrganOrTissueDonated(source.getBloodOrganOrTissueDonated());

		target.setNotACaseReasonNegativeTest(source.isNotACaseReasonNegativeTest());
		target.setNotACaseReasonPhysicianInformation(source.isNotACaseReasonPhysicianInformation());
		target.setNotACaseReasonDifferentPathogen(source.isNotACaseReasonDifferentPathogen());
		target.setNotACaseReasonOther(source.isNotACaseReasonOther());
		target.setNotACaseReasonDetails(source.getNotACaseReasonDetails());
		target.setDontShareWithReportingTool(source.isDontShareWithReportingTool());
		target.setCaseReferenceDefinition(source.getCaseReferenceDefinition());
		target.setPreviousQuarantineTo(source.getPreviousQuarantineTo());
		target.setQuarantineChangeComment(source.getQuarantineChangeComment());

		if (source.getExternalData() != null) {
			target.setExternalData(source.getExternalData());
		}

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	private Map<ReinfectionDetail, Boolean> cleanupReinfectionDetails(Map<ReinfectionDetail, Boolean> reinfectionDetails) {
		if (reinfectionDetails != null && reinfectionDetails.containsValue(Boolean.FALSE)) {
			Map<ReinfectionDetail, Boolean> onlyTrueReinfectionDetails = new HashMap<>();
			onlyTrueReinfectionDetails =
				reinfectionDetails.entrySet().stream().filter(Map.Entry::getValue).collect(Collectors.toMap(Map.Entry::getKey, entry -> true));

			return onlyTrueReinfectionDetails;
		} else {
			return reinfectionDetails;
		}
	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return DeletableEntityType.CASE;
	}

	private void updateInvestigationByStatus(CaseDataDto existingCase, Case caze) {

		CaseReferenceDto caseRef = caze.toReference();
		InvestigationStatus investigationStatus = caze.getInvestigationStatus();

		if (investigationStatus != InvestigationStatus.PENDING) {
			// Set the investigation date
			if (caze.getInvestigatedDate() == null) {
				caze.setInvestigatedDate(new Date());
			}

			// Set the task status of all investigation tasks to "Removed" because
			// the case status has been updated manually
			if (existingCase != null) {
				List<Task> pendingTasks =
					taskService.findBy(new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION).caze(caseRef).taskStatus(TaskStatus.PENDING), true);
				final boolean caseStatusSetToDone =
					caze.getInvestigationStatus() == InvestigationStatus.DONE && existingCase.getInvestigationStatus() != InvestigationStatus.DONE;
				for (Task task : pendingTasks) {
					task.setTaskStatus(caseStatusSetToDone ? TaskStatus.DONE : TaskStatus.REMOVED);
					task.setStatusChangeDate(new Date());
				}

				if (caseStatusSetToDone) {
					sendInvestigationDoneNotifications(caze);
				}
			}

		} else {
			// Remove the investigation date
			caze.setInvestigatedDate(null);

			// Create a new investigation task if none is present
			long investigationTaskCount =
				existingCase != null ? taskService.getCount(new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION).caze(caseRef)) : 0;

			if (investigationTaskCount == 0 && featureConfigurationFacade.isTaskGenerationFeatureEnabled(TaskType.CASE_INVESTIGATION)) {
				createInvestigationTask(caze);
			}
		}
	}

	@RightsAllowed(UserRight._CASE_EDIT)
	public void updateInvestigationByTask(Case caze) {

		CaseReferenceDto caseRef = caze.toReference();

		// any pending case investigation task?
		long pendingCount =
			taskService.getCount(new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION).caze(caseRef).taskStatus(TaskStatus.PENDING));

		if (pendingCount > 0) {
			// set status to investigation pending
			caze.setInvestigationStatus(InvestigationStatus.PENDING);
			// .. and clear date
			caze.setInvestigatedDate(null);
		} else {
			// get "case investigation" task created last
			List<Task> cazeTasks = taskService.findBy(new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION).caze(caseRef), true);

			if (!cazeTasks.isEmpty()) {
				Task youngestTask = cazeTasks.stream().max(new Comparator<Task>() {

					@Override
					public int compare(Task o1, Task o2) {
						return o1.getCreationDate().compareTo(o2.getCreationDate());
					}
				}).get();

				switch (youngestTask.getTaskStatus()) {
				case PENDING:
					throw new UnsupportedOperationException("there should not be any pending tasks");
				case DONE:
					caze.setInvestigationStatus(InvestigationStatus.DONE);
					caze.setInvestigatedDate(youngestTask.getStatusChangeDate());
					sendInvestigationDoneNotifications(caze);
					break;
				case REMOVED:
					caze.setInvestigationStatus(InvestigationStatus.DISCARDED);
					caze.setInvestigatedDate(youngestTask.getStatusChangeDate());
					break;
				case NOT_EXECUTABLE:
					caze.setInvestigationStatus(InvestigationStatus.PENDING);
					caze.setInvestigatedDate(null);
					break;
				default:
					break;
				}
			}
		}
	}

	private void createInvestigationTask(Case caze) {

		Task task = new Task();
		task.setTaskStatus(TaskStatus.PENDING);
		task.setTaskContext(TaskContext.CASE);
		task.setCaze(caze);
		task.setTaskType(TaskType.CASE_INVESTIGATION);
		task.setSuggestedStart(TaskHelper.getDefaultSuggestedStart());
		task.setDueDate(TaskHelper.getDefaultDueDate());
		task.setPriority(TaskPriority.NORMAL);

		assignOfficerOrSupervisorToTask(caze, task);

		taskService.ensurePersisted(task);
	}

	private void createActiveSearchForOtherCasesTask(Case caze) {

		Task task = new Task();
		task.setTaskStatus(TaskStatus.PENDING);
		task.setTaskContext(TaskContext.CASE);
		task.setCaze(caze);
		task.setTaskType(TaskType.ACTIVE_SEARCH_FOR_OTHER_CASES);
		task.setSuggestedStart(TaskHelper.getDefaultSuggestedStart());
		task.setDueDate(TaskHelper.getDefaultDueDate());
		task.setPriority(TaskPriority.NORMAL);

		assignOfficerOrSupervisorToTask(caze, task);

		taskService.ensurePersisted(task);
	}

	private void assignOfficerOrSupervisorToTask(Case caze, Task task) {

		User assignee = null;

		if (caze.getSurveillanceOfficer() != null) {
			// 1) The surveillance officer that is responsible for the case
			assignee = caze.getSurveillanceOfficer();
		} else {
			// 2) A random user with UserRight.CASE_RESPONSIBLE from the case responsible district
			assignee = getRandomDistrictCaseResponsible(caze.getResponsibleDistrict());
		}

		if (assignee == null && caze.getDistrict() != null) {
			// 3) A random surveillance officer from the case district
			assignee = getRandomDistrictCaseResponsible(caze.getDistrict());
		}

		if (assignee == null) {
			if (caze.getReportingUser() != null && (userRoleService.hasUserRight(caze.getReportingUser().getUserRoles(), UserRight.TASK_ASSIGN))) {
				// 4) If the case was created by a surveillance supervisor, assign them
				assignee = caze.getReportingUser();
			} else {
				// 5) Assign a random surveillance supervisor from the case responsible region
				assignee = getRandomRegionCaseResponsible(caze.getResponsibleRegion());
			}
			if (assignee == null && caze.getRegion() != null) {
				// 6) Assign a random surveillance supervisor from the case region
				assignee = getRandomRegionCaseResponsible(caze.getRegion());
			}
		}

		task.setAssigneeUser(assignee);
		if (assignee == null) {
			logger.warn("No valid assignee user found for task " + task.getUuid());
		}
	}

	private User getRandomDistrictCaseResponsible(District district) {

		return userService.getRandomDistrictUser(district, UserRight.CASE_RESPONSIBLE);
	}

	private User getRandomRegionCaseResponsible(Region region) {

		return userService.getRandomRegionUser(region, UserRight.CASE_RESPONSIBLE);
	}

	@Override
	public boolean doesEpidNumberExist(String epidNumber, String caseUuid, Disease caseDisease) {
		if (epidNumber == null) {
			return false;
		}

		int suffixSeperatorIndex = epidNumber.lastIndexOf('-');
		if (suffixSeperatorIndex == -1) {
			// no suffix - use the whole string as prefix
			suffixSeperatorIndex = epidNumber.length() - 1;
		}
		String prefixString = epidNumber.substring(0, suffixSeperatorIndex + 1);
		String suffixString = epidNumber.substring(suffixSeperatorIndex + 1);
		suffixString = suffixString.replaceAll("[^\\d]", "");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = cb.and(cb.equal(caze.get(Case.DELETED), false), cb.equal(caze.get(Case.DISEASE), caseDisease));
		if (!DataHelper.isNullOrEmpty(caseUuid)) {
			filter = cb.and(filter, cb.notEqual(caze.get(Case.UUID), caseUuid));
		}

		ParameterExpression<String> regexPattern = null, regexReplacement = null, regexFlags = null;
		if (suffixString.length() > 0) {
			// has to start with prefix
			filter = cb.and(filter, cb.like(caze.get(Case.EPID_NUMBER), prefixString + "%"));

			// for the suffix only consider the actual number. Any other characters and leading zeros are ignored
			int suffixNumber;
			try {
				suffixNumber = Integer.parseInt(suffixString);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
					String.format("Invalid suffix for epid number. epidNumber: '%s', suffixString: '%s'", epidNumber, suffixString),
					e);
			}
			regexPattern = cb.parameter(String.class);
			regexReplacement = cb.parameter(String.class);
			regexFlags = cb.parameter(String.class);
			Expression<String> epidNumberSuffixClean = cb.function(
				"regexp_replace",
				String.class,
				cb.substring(caze.get(Case.EPID_NUMBER), suffixSeperatorIndex + 2),
				regexPattern,
				regexReplacement,
				regexFlags);
			filter = cb.and(filter, cb.equal(cb.concat("0", epidNumberSuffixClean).as(Integer.class), suffixNumber));
		} else {
			filter = cb.and(filter, cb.equal(caze.get(Case.EPID_NUMBER), prefixString));
		}
		cq.where(filter);

		cq.select(caze.get(Case.EPID_NUMBER));
		TypedQuery<String> query = em.createQuery(cq);
		if (regexPattern != null) {
			query.setParameter(regexPattern, "\\D"); // Non-digits
			query.setParameter(regexReplacement, ""); // Replace all non-digits with empty string
			query.setParameter(regexFlags, "g"); // Global search
		}
		return QueryHelper.getFirstResult(query) != null;
	}

	@Override
	public boolean doesExternalTokenExist(String externalToken, String caseUuid) {
		return service.exists(
			(cb, caseRoot, cq) -> CriteriaBuilderHelper.and(
				cb,
				cb.equal(caseRoot.get(Case.EXTERNAL_TOKEN), externalToken),
				cb.notEqual(caseRoot.get(Case.UUID), caseUuid),
				cb.notEqual(caseRoot.get(Case.DELETED), Boolean.TRUE)));
	}

	@Override
	public List<Pair<DistrictDto, BigDecimal>> getCaseMeasurePerDistrict(Date fromDate, Date toDate, Disease disease, CaseMeasure caseMeasure) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<Case> caseRoot = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caseRoot);

		Root<District> districtRoot = cq.from(District.class);

		Predicate filter = service.createDefaultFilter(cb, caseRoot);
		if (fromDate != null || toDate != null) {
			filter = service.createCaseRelevanceFilter(caseQueryContext, fromDate, toDate);
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(caseRoot.get(Case.DISEASE), disease);
			filter = filter != null ? cb.and(filter, diseaseFilter) : diseaseFilter;
		}

		Predicate districtFilter = cb.or(
			cb.equal(caseRoot.get(Case.DISTRICT), districtRoot),
			cb.and(cb.isNull(caseRoot.get(Case.DISTRICT)), cb.equal(caseRoot.get(Case.RESPONSIBLE_DISTRICT), districtRoot)));
		filter = filter != null ? cb.and(filter, districtFilter) : districtFilter;

		cq.where(filter);

		cq.groupBy(districtRoot);
		cq.multiselect(districtRoot, cb.count(caseRoot));
		if (caseMeasure == CaseMeasure.CASE_COUNT) {
			cq.orderBy(cb.asc(cb.count(caseRoot)));
		}
		List<Object[]> results = em.createQuery(cq).getResultList();

		if (caseMeasure == CaseMeasure.CASE_COUNT) {
			return results.stream()
				.map(e -> new Pair<>(districtFacade.toDto((District) e[0]), new BigDecimal((Long) e[1])))
				.collect(Collectors.toList());
		} else {
			return results.stream().map(e -> {
				District district = (District) e[0];
				Integer population = populationDataFacade.getProjectedDistrictPopulation(district.getUuid());
				Long caseCount = (Long) e[1];

				if (population == null || population <= 0) {
					// No, or negative population - these entries will be cut off in the UI
					return new Pair<>(districtFacade.toDto(district), new BigDecimal(0));
				} else {
					return new Pair<>(
						districtFacade.toDto(district),
						InfrastructureHelper.getCaseIncidence(caseCount.intValue(), population, InfrastructureHelper.CASE_INCIDENCE_DIVISOR));
				}
			}).sorted(Comparator.comparing(Pair::getElement1)).collect(Collectors.toList());
		}
	}

	private void sendInvestigationDoneNotifications(Case caze) {

		try {
			String message =
				String.format(I18nProperties.getString(MessageContents.CONTENT_CASE_INVESTIGATION_DONE), DataHelper.getShortUuid(caze.getUuid()));
			notificationService.sendNotifications(
				NotificationType.CASE_INVESTIGATION_DONE,
				JurisdictionHelper.getCaseRegions(caze),
				null,
				MessageSubject.CASE_INVESTIGATION_DONE,
				message);
		} catch (NotificationDeliveryFailedException e) {
			logger.error("NotificationDeliveryFailedException when trying to notify supervisors about the completion of a case investigation.");
		}
	}

	@Override
	public Date getOldestCaseOnsetDate() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);
		Root<Case> from = cq.from(Case.class);
		Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS, JoinType.LEFT);

		Path<Timestamp> expression = symptoms.get(Symptoms.ONSET_DATE);
		cq.select(cb.least(expression));
		cq.where(cb.greaterThan(symptoms.get(Symptoms.ONSET_DATE), DateHelper.getDateZero(2000, 1, 1)));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public Date getOldestCaseReportDate() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);
		Root<Case> from = cq.from(Case.class);

		final Path<Timestamp> reportDate = from.get(Case.REPORT_DATE);
		cq.select(cb.least(reportDate));
		cq.where(cb.greaterThan(from.get(Case.REPORT_DATE), DateHelper.getDateZero(2000, 1, 1)));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public Date getOldestCaseOutcomeDate() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);
		Root<Case> from = cq.from(Case.class);

		final Path<Timestamp> reportDate = from.get(Case.OUTCOME_DATE);
		cq.select(cb.least(reportDate));
		cq.where(cb.greaterThan(from.get(Case.OUTCOME_DATE), DateHelper.getDateZero(2000, 1, 1)));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	@RightsAllowed(UserRight._CASE_MERGE)
	public void merge(String leadUuid, String otherUuid) {

		mergeCase(getCaseDataWithoutPseudonyimization(leadUuid), getCaseDataWithoutPseudonyimization(otherUuid), false);
	}

	private void mergeCase(CaseDataDto leadCaseData, CaseDataDto otherCaseData, boolean cloning) {

		// 1 Merge Dtos
		// 1.1 Case

		copyDtoValues(leadCaseData, otherCaseData, cloning);
		save(leadCaseData, !cloning, true, true);

		// 1.2 Person - Only merge when the persons have different UUIDs
		if (!cloning && !DataHelper.equal(leadCaseData.getPerson().getUuid(), otherCaseData.getPerson().getUuid())) {
			PersonDto leadPerson = personFacade.getByUuid(leadCaseData.getPerson().getUuid());
			PersonDto otherPerson = personFacade.getByUuid(otherCaseData.getPerson().getUuid());
			personFacade.mergePerson(leadPerson, otherPerson);
		} else {
			assert (DataHelper.equal(leadCaseData.getPerson().getUuid(), otherCaseData.getPerson().getUuid()));
		}

		// 2 Change CaseReference
		Case leadCase = service.getByUuid(leadCaseData.getUuid());
		Case otherCase = service.getByUuid(otherCaseData.getUuid());

		// 2.1 Contacts
		List<Contact> contacts = contactService.findBy(new ContactCriteria().caze(otherCase.toReference()), null);
		for (Contact contact : contacts) {
			if (cloning) {
				ContactDto newContact =
					ContactDto.build(leadCase.toReference(), leadCase.getDisease(), leadCase.getDiseaseDetails(), leadCase.getDiseaseVariant());
				newContact.setPerson(new PersonReferenceDto(contact.getPerson().getUuid()));
				DtoCopyHelper.copyDtoValues(newContact, contactFacade.toDto(contact), cloning);
				contactFacade.save(newContact, false, false);
			} else {
				// simply move existing entities to the merge target
				contact.setCaze(leadCase);
				contactService.ensurePersisted(contact);
			}
		}

		// 2.2 Samples
		List<Sample> samples = sampleService.findBy(new SampleCriteria().caze(otherCase.toReference()), null);
		for (Sample sample : samples) {
			if (cloning) {
				SampleDto newSample = SampleDto.build(sample.getReportingUser().toReference(), leadCase.toReference());
				DtoCopyHelper.copyDtoValues(newSample, SampleFacadeEjb.toDto(sample), cloning);
				sampleFacade.saveSample(newSample, false, true, true);

				// 2.2.1 Pathogen Tests
				for (PathogenTest pathogenTest : sample.getPathogenTests()) {
					PathogenTestDto newPathogenTest = PathogenTestDto.build(newSample.toReference(), pathogenTest.getLabUser().toReference());
					DtoCopyHelper.copyDtoValues(newPathogenTest, PathogenTestFacadeEjbLocal.toDto(pathogenTest), cloning);
					sampleTestFacade.savePathogenTest(newPathogenTest);
				}

				for (AdditionalTest additionalTest : sample.getAdditionalTests()) {
					AdditionalTestDto newAdditionalTest = AdditionalTestDto.build(newSample.toReference());
					DtoCopyHelper.copyDtoValues(newAdditionalTest, AdditionalTestFacadeEjbLocal.toDto(additionalTest), cloning);
					additionalTestFacade.saveAdditionalTest(newAdditionalTest);
				}
			} else {
				// simply move existing entities to the merge target
				sample.setAssociatedCase(leadCase);
				sampleService.ensurePersisted(sample);
			}
		}

		// 2.3 Tasks
		if (!cloning) {
			// simply move existing entities to the merge target

			List<Task> tasks = taskService.findBy(new TaskCriteria().caze(new CaseReferenceDto(otherCase.getUuid())), true);
			for (Task task : tasks) {
				task.setCaze(leadCase);
				taskService.ensurePersisted(task);
			}
		}

		// 3 Change Therapy Reference
		// 3.1 Treatments
		List<Treatment> treatments =
			treatmentService.findBy(new TreatmentCriteria().therapy(new TherapyReferenceDto(otherCase.getTherapy().getUuid())));
		TherapyReferenceDto leadCaseTherapyReference = new TherapyReferenceDto(leadCase.getTherapy().getUuid());
		for (Treatment treatment : treatments) {
			if (cloning) {
				TreatmentDto newTreatment = TreatmentDto.build(leadCaseTherapyReference);
				DtoCopyHelper.copyDtoValues(newTreatment, TreatmentFacadeEjb.toDto(treatment), cloning);
				treatmentFacade.saveTreatment(newTreatment);
			} else {
				// simply move existing entities to the merge target
				treatment.setTherapy(leadCase.getTherapy());
				treatmentService.ensurePersisted(treatment);
			}
		}

		// 3.2 Prescriptions
		List<Prescription> prescriptions =
			prescriptionService.findBy(new PrescriptionCriteria().therapy(new TherapyReferenceDto(otherCase.getTherapy().getUuid())));
		for (Prescription prescription : prescriptions) {
			if (cloning) {
				PrescriptionDto newPrescription = PrescriptionDto.buildPrescription(leadCaseTherapyReference);
				DtoCopyHelper.copyDtoValues(newPrescription, PrescriptionFacadeEjb.toDto(prescription), cloning);
				prescriptionFacade.savePrescription(newPrescription);
			} else {
				// simply move existing entities to the merge target
				prescription.setTherapy(leadCase.getTherapy());
				prescriptionService.ensurePersisted(prescription);
			}
		}

		// 4 Change Clinical Course Reference
		// 4.1 Clinical Visits
		List<ClinicalVisit> clinicalVisits = clinicalVisitService
			.findBy(new ClinicalVisitCriteria().clinicalCourse(new ClinicalCourseReferenceDto(otherCase.getClinicalCourse().getUuid())));
		for (ClinicalVisit clinicalVisit : clinicalVisits) {
			if (cloning) {
				ClinicalVisitDto newClinicalVisit = ClinicalVisitDto.build(leadCaseData.getClinicalCourse().toReference(), leadCase.getDisease());
				DtoCopyHelper.copyDtoValues(newClinicalVisit, ClinicalVisitFacadeEjb.toDto(clinicalVisit), cloning);
				clinicalVisitFacade.saveClinicalVisitForMergedCases(newClinicalVisit, leadCase.getUuid(), false);
			} else {
				// simply move existing entities to the merge target
				clinicalVisit.setClinicalCourse(leadCase.getClinicalCourse());
				clinicalVisitService.ensurePersisted(clinicalVisit);
			}
		}

		// 5 Attach otherCase visits to leadCase
		// (set the person and the disease of the visit, saveVisit does the rest)
		for (VisitDto otherVisit : otherCase.getVisits().stream().map(VisitFacadeEjb::toVisitDto).collect(Collectors.toList())) {
			otherVisit.setPerson(leadCaseData.getPerson());
			otherVisit.setDisease(leadCaseData.getDisease());
			visitFacade.save(otherVisit);
		}

		// 6 Documents
		List<Document> documents = documentService.getRelatedToEntity(DocumentRelatedEntityType.CASE, otherCase.getUuid());
		for (Document document : documents) {
			document.setRelatedEntityUuid(leadCaseData.getUuid());

			documentService.ensurePersisted(document);
		}

		// 7 Persist Event links through eventparticipants
		Set<EventParticipant> eventParticipants = otherCase.getEventParticipants();
		for (EventParticipant eventParticipant : eventParticipants) {
			eventParticipant.setResultingCase(leadCase);
			eventParticipantService.ensurePersisted(eventParticipant);
		}
		otherCase.getEventParticipants().clear();

		// 8 Exposures - Make sure there are no two probable infection environments
		// if there are more than 2 exposures marked as probable infection environment, find the one that originates from the otherCase and set it to false
		// the one originating from the otherCase should always be found at the higher index
		List<Exposure> probableExposuresList =
			leadCase.getEpiData().getExposures().stream().filter(Exposure::isProbableInfectionEnvironment).collect(Collectors.toList());
		while (probableExposuresList.size() >= 2) {
			// should never be > 2, but still make sure to set all but one exposures to false
			probableExposuresList.get(probableExposuresList.size() - 1).setProbableInfectionEnvironment(false);
			exposureService.ensurePersisted(probableExposuresList.get(probableExposuresList.size() - 1));
			probableExposuresList.remove(probableExposuresList.size() - 1);
		}

		// 9 Reports
		List<SurveillanceReport> surveillanceReports = surveillanceReportService.getByCaseUuids(Collections.singletonList(otherCase.getUuid()));
		surveillanceReports.forEach(surveillanceReport -> {
			SurveillanceReportDto surveillanceReportDto = surveillanceReportFacade.toDto(surveillanceReport);
			surveillanceReportDto.setCaze(leadCase.toReference());
			surveillanceReportFacade.save(surveillanceReportDto);
		});

		// 10 Activity as case
		final EpiData otherEpiData = otherCase.getEpiData();
		if (otherEpiData != null
			&& YesNoUnknown.YES == otherEpiData.getActivityAsCaseDetailsKnown()
			&& CollectionUtils.isNotEmpty(otherEpiData.getActivitiesAsCase())) {

			final EpiData leadEpiData = leadCase.getEpiData();
			leadEpiData.setActivityAsCaseDetailsKnown(YesNoUnknown.YES);
			epiDataService.ensurePersisted(leadEpiData);
		}

		// Travel entries reference
		List<TravelEntry> travelEntries = travelEntryService.getAllByResultingCase(otherCase);
		travelEntries.forEach(t -> {
			t.setResultingCase(leadCase);
			t.setPerson(leadCase.getPerson());
			travelEntryService.ensurePersisted(t);
		});
	}

	private void copyDtoValues(CaseDataDto leadCaseData, CaseDataDto otherCaseData, boolean cloning) {
		String leadAdditionalDetails = leadCaseData.getAdditionalDetails();
		String leadFollowUpComment = leadCaseData.getFollowUpComment();

		DtoCopyHelper.copyDtoValues(leadCaseData, otherCaseData, cloning);

		if (!cloning) {
			leadCaseData.setAdditionalDetails(DataHelper.joinStrings(" ", leadAdditionalDetails, otherCaseData.getAdditionalDetails()));
			leadCaseData.setFollowUpComment(DataHelper.joinStrings(" ", leadFollowUpComment, otherCaseData.getFollowUpComment()));
		}
	}

	@Override
	@RightsAllowed(UserRight._CASE_CREATE)
	public CaseDataDto cloneCase(CaseDataDto existingCaseDto) {
		CaseDataDto newCase = CaseDataDto.build(existingCaseDto.getPerson(), existingCaseDto.getDisease());
		newCase.setReportingUser(userService.getCurrentUser().toReference());
		mergeCase(newCase, existingCaseDto, true);
		return getCaseDataByUuid(newCase.getUuid());
	}

	/**
	 * Archives all cases that have not been changed for a defined amount of days
	 *
	 * @param daysAfterCaseGetsArchived
	 *            defines the amount of days
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@RightsAllowed(UserRight._SYSTEM)
	public void archiveAllArchivableCases(int daysAfterCaseGetsArchived) {

		archiveAllArchivableCases(daysAfterCaseGetsArchived, LocalDate.now());
	}

	@RightsAllowed(UserRight._SYSTEM)
	public void archiveAllArchivableCases(int daysAfterCaseGetsArchived, LocalDate referenceDate) {

		long startTime = DateHelper.startTime();

		LocalDate notChangedSince = referenceDate.minusDays(daysAfterCaseGetsArchived);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> from = cq.from(Case.class);

		CaseQueryContext qc = new CaseQueryContext(cb, cq, from);

		Timestamp notChangedTimestamp = Timestamp.valueOf(notChangedSince.atStartOfDay());
		cq.where(
			cb.equal(from.get(Case.ARCHIVED), false),
			cb.equal(from.get(Case.DELETED), false),
			cb.not(service.createChangeDateFilter(cb, qc.getJoins(), notChangedTimestamp, true)));
		cq.select(from.get(Case.UUID)).distinct(true);
		List<String> caseUuids = em.createQuery(cq).getResultList();

		if (!caseUuids.isEmpty()) {
			archive(caseUuids, true);
		}

		logger.debug(
			"archiveAllArchivableCases() finished. caseCount = {}, daysAfterCaseGetsArchived = {}, {}ms",
			caseUuids.size(),
			daysAfterCaseGetsArchived,
			DateHelper.durationMillies(startTime));
	}

	public Page<CaseFollowUpDto> getCaseFollowUpIndexPage(
		CaseFollowUpCriteria criteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {
		List<CaseFollowUpDto> caseFollowUpIndexList =
			getCaseFollowUpList(criteria, criteria.getReferenceDate(), criteria.getInterval(), offset, size, sortProperties);
		long totalElementCount = count(criteria);
		return new Page<>(caseFollowUpIndexList, offset, size, totalElementCount);

	}

	@Override
	public List<CaseFollowUpDto> getCaseFollowUpList(
		CaseCriteria caseCriteria,
		Date referenceDate,
		int interval,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		Date end = DateHelper.getEndOfDay(referenceDate);
		Date start = DateHelper.getStartOfDay(DateHelper.subtractDays(end, interval));

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Case> caze = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		final CaseJoins joins = caseQueryContext.getJoins();

		Path<String> firstName = joins.getPerson().get(Person.FIRST_NAME);
		Path<String> lastName = joins.getPerson().get(Person.LAST_NAME);

		Predicate filter =
			CriteriaBuilderHelper.and(cb, service.createUserFilter(caseQueryContext), service.createCriteriaFilter(caseCriteria, caseQueryContext));

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);

		final List<Order> orderList = new ArrayList<>();
		if (sortProperties != null && !sortProperties.isEmpty()) {
			for (SortProperty sortProperty : sortProperties) {
				OrderBuilder builder = createOrderBuilder(cb, sortProperty.ascending);
				final List<Order> order;

				switch (sortProperty.propertyName) {
				case FollowUpDto.UUID:
				case FollowUpDto.REPORT_DATE:
				case FollowUpDto.FOLLOW_UP_UNTIL:
					order = builder.build(caze.get(sortProperty.propertyName));
					break;
				case FollowUpDto.FIRST_NAME:
					order = builder.build(cb.lower(firstName));
					break;
				case FollowUpDto.SYMPTOM_JOURNAL_STATUS:
					order = builder.build(joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS));
					break;
				case FollowUpDto.LAST_NAME:
					order = builder.build(cb.lower(lastName));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}

				orderList.addAll(order);
			}
		} else {
			orderList.add(cb.desc(caze.get(Case.CHANGE_DATE)));
		}

		cq.multiselect(
			Stream
				.concat(
					Stream.of(
						caze.get(Case.UUID),
						firstName,
						lastName,
						caze.get(Case.REPORT_DATE),
						joins.getSymptoms().get(Symptoms.ONSET_DATE),
						caze.get(Case.FOLLOW_UP_UNTIL),
						joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS),
						caze.get(Case.DISEASE),
						JurisdictionHelper.booleanSelector(cb, service.inJurisdictionOrOwned(caseQueryContext))),
					orderList.stream().map(Order::getExpression))
				.collect(Collectors.toList()));

		cq.orderBy(orderList);

		List<CaseFollowUpDto> resultList = QueryHelper.getResultList(em, cq, new CaseFollowUpDtoResultTransformer(), first, max);
		if (!resultList.isEmpty()) {

			List<String> caseUuids = resultList.stream().map(FollowUpDto::getUuid).collect(Collectors.toList());

			CriteriaQuery<Object[]> visitsCq = cb.createQuery(Object[].class);
			Root<Case> visitsCqRoot = visitsCq.from(Case.class);
			Join<Case, Visit> visitsJoin = visitsCqRoot.join(Case.VISITS, JoinType.LEFT);
			Join<Visit, Symptoms> visitSymptomsJoin = visitsJoin.join(Visit.SYMPTOMS, JoinType.LEFT);

			visitsCq.where(
				CriteriaBuilderHelper.and(
					cb,
					caze.get(AbstractDomainObject.UUID).in(caseUuids),
					cb.isNotEmpty(visitsCqRoot.get(Case.VISITS)),
					cb.between(visitsJoin.get(Visit.VISIT_DATE_TIME), start, end)));
			visitsCq.multiselect(
				visitsCqRoot.get(Case.UUID),
				visitsJoin.get(Visit.VISIT_DATE_TIME),
				visitsJoin.get(Visit.VISIT_STATUS),
				visitsJoin.get(Visit.ORIGIN),
				visitSymptomsJoin.get(Symptoms.SYMPTOMATIC));
			// Sort by visit date so that we'll have the latest visit of each day
			visitsCq.orderBy(cb.asc(visitsJoin.get(Visit.VISIT_DATE_TIME)));

			visitsCq.orderBy(cb.asc(visitsJoin.get(Visit.VISIT_DATE_TIME)), cb.asc(visitsJoin.get(Visit.CREATION_DATE)));

			List<Object[]> visits = em.createQuery(visitsCq).getResultList();
			Map<String, CaseFollowUpDto> resultMap = resultList.stream().collect(Collectors.toMap(CaseFollowUpDto::getUuid, Function.identity()));

			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));

			for (CaseFollowUpDto caseFollowUpDto : resultMap.values()) {
				caseFollowUpDto.initVisitSize(interval + 1);
				pseudonymizer.pseudonymizeDto(CaseFollowUpDto.class, caseFollowUpDto, caseFollowUpDto.getInJurisdiction(), null);
			}

			for (Object[] v : visits) {
				int day = DateHelper.getDaysBetween(start, (Date) v[1]);
				VisitResultDto result = getVisitResult((VisitStatus) v[2], (VisitOrigin) v[3], (Boolean) v[4]);
				resultMap.get(v[0]).getVisitResults()[day - 1] = result;
			}
		}

		return resultList;
	}

	@Override
	@RightsAllowed(UserRight._CASE_EDIT)
	public FollowUpPeriodDto calculateFollowUpUntilDate(CaseDataDto caseDto, boolean ignoreOverwrite) {
		List<SampleDto> samples = Collections.emptyList();
		if (userService.hasRight(UserRight.SAMPLE_VIEW)) {
			samples = sampleFacade.getByCaseUuids(Collections.singletonList(caseDto.getUuid()));
		}
		return CaseLogic.calculateFollowUpUntilDate(
			caseDto,
			CaseLogic.getFollowUpStartDate(caseDto, samples),
			visitFacade.getVisitsByCase(caseDto.toReference()),
			diseaseConfigurationFacade.getCaseFollowUpDuration(caseDto.getDisease()),
			ignoreOverwrite,
			featureConfigurationFacade.isPropertyValueTrue(FeatureType.CASE_FOLLOWUP, FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE));
	}

	@Override
	@RightsAllowed(UserRight._CASE_EDIT)
	public void sendMessage(List<String> caseUuids, String subject, String messageContent, MessageType... messageTypes) {
		caseUuids.forEach(uuid -> {
			final Case aCase = service.getByUuid(uuid);
			final Person person = aCase.getPerson();

			try {
				messagingService.sendManualMessage(person, subject, messageContent, messageTypes);
			} catch (NotificationDeliveryFailedException e) {
				logger.error(
					String.format(
						"NotificationDeliveryFailedException when trying to notify person about: %s" + "Failed to send " + e.getMessageType()
							+ " to person with UUID %s.",
						subject,
						person.getUuid()));
			}
		});
	}

	@Override
	public long countCasesWithMissingContactInformation(List<String> caseUuids, MessageType messageType) {

		final AtomicLong totalCount = new AtomicLong();

		IterableHelper.executeBatched(
			caseUuids,
			ModelConstants.PARAMETER_LIMIT,
			batchedUuids -> totalCount.addAndGet(countCasesWithMissingContactInfo(batchedUuids, messageType)));

		return totalCount.get();
	}

	private Long countCasesWithMissingContactInfo(List<String> caseUuids, MessageType messageType) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> root = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, root);

		cq.select(cb.count(root));
		Expression<?> contactInformation = messageType == MessageType.EMAIL
			? caseQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_EMAIL_SUBQUERY)
			: caseQueryContext.getSubqueryExpression(CaseQueryContext.PERSON_PHONE_SUBQUERY);
		cq.where(cb.and(root.get(Case.UUID).in(caseUuids), cb.isNull(contactInformation)));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<ManualMessageLogDto> getMessageLog(String personUuid, MessageType messageType) {
		return manualMessageLogService.getByPersonUuid(personUuid, messageType)
			.stream()
			.map(
				mml -> new ManualMessageLogDto(
					mml.getMessageType(),
					mml.getSentDate(),
					mml.getSendingUser().toReference(),
					mml.getRecipientPerson().toReference()))
			.collect(Collectors.toList());
	}

	@Override
	public List<String> getUuidsNotShareableWithExternalReportingTools(List<String> caseUuids) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caseRoot = cq.from(Case.class);
		Join<Case, SormasToSormasOriginInfo> originInfoJoin = caseRoot.join(Case.SORMAS_TO_SORMAS_ORIGIN_INFO, JoinType.LEFT);
		Join<Case, SormasToSormasShareInfo> shareInfoJoin = caseRoot.join(Case.SORMAS_TO_SORMAS_SHARES, JoinType.LEFT);

		cq.select(caseRoot.get(Case.UUID));
		cq.where(
			cb.and(
				cb.or(
					cb.isFalse(originInfoJoin.get(SormasToSormasOriginInfo.OWNERSHIP_HANDED_OVER)),
					cb.isTrue(shareInfoJoin.get(SormasToSormasShareInfo.OWNERSHIP_HANDED_OVER)),
					cb.isTrue(caseRoot.get(Case.DONT_SHARE_WITH_REPORTING_TOOL))),
				caseRoot.get(Case.UUID).in(caseUuids)));
		cq.orderBy(cb.asc(caseRoot.get(AbstractDomainObject.CREATION_DATE)));

		return QueryHelper.getResultList(em, cq, null, null);
	}

	/**
	 * Find duplicates based on case and person dto
	 * Conditions:
	 * * same externalId
	 * * same externalToken
	 * * same first name, last name, date of birth, sex (null is considered equal to any sex), disease, reportDate (ignore time), district
	 *
	 * The reportDateThreshold allows to return duplicates where
	 * -reportDateThreshold <= match.reportDate <= reportDateThreshold
	 *
	 * @param casePerson
	 *            - case and person
	 * @param reportDateThreshold
	 *            - the range bounds on match.reportDate
	 * @return list of duplicate cases
	 */
	@Override
	public List<CasePersonDto> getDuplicates(@Valid CasePersonDto casePerson, int reportDateThreshold) {

		CaseDataDto searchCaze = casePerson.getCaze();
		PersonDto searchPerson = casePerson.getPerson();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caseRoot = cq.from(Case.class);
		CaseJoins caseCaseJoins = new CaseJoins(caseRoot);
		Join<Case, Person> person = caseCaseJoins.getPerson();

		cq.multiselect(caseRoot.get(Case.UUID), person.get(Person.UUID));

		Path<String> externalId = caseRoot.get(Case.EXTERNAL_ID);
		Path<String> externalToken = caseRoot.get(Case.EXTERNAL_TOKEN);

		Predicate externalIdPredicate = null;
		if (searchCaze.getExternalID() != null) {
			externalIdPredicate = and(cb, cb.isNotNull(externalId), cb.equal(cb.lower(externalId), searchCaze.getExternalID().toLowerCase().trim()));
		}

		Predicate externalTokenPredicate = null;
		if (searchCaze.getExternalToken() != null) {
			externalTokenPredicate =
				and(cb, cb.isNotNull(externalToken), cb.equal(cb.trim(cb.lower(externalToken)), searchCaze.getExternalToken().toLowerCase().trim()));
		}
		// todo this should use PersonService.buildSimilarityCriteriaFilter
		Predicate combinedPredicate = null;
		if (searchPerson.getFirstName() != null
			&& searchPerson.getLastName() != null
			&& searchCaze.getReportDate() != null
			&& searchCaze.getResponsibleDistrict() != null) {
			Predicate personPredicate = and(
				cb,
				cb.equal(cb.trim(cb.lower(person.get(Person.FIRST_NAME))), searchPerson.getFirstName().toLowerCase().trim()),
				cb.equal(cb.trim(cb.lower(person.get(Person.LAST_NAME))), searchPerson.getLastName().toLowerCase().trim()));

			if (searchPerson.getBirthdateDD() != null) {
				personPredicate = and(
					cb,
					personPredicate,
					or(cb, cb.isNull(person.get(Person.BIRTHDATE_DD)), cb.equal(person.get(Person.BIRTHDATE_DD), searchPerson.getBirthdateDD())));
			}

			if (searchPerson.getBirthdateMM() != null) {
				personPredicate = and(
					cb,
					personPredicate,
					or(cb, cb.isNull(person.get(Person.BIRTHDATE_DD)), cb.equal(person.get(Person.BIRTHDATE_MM), searchPerson.getBirthdateMM())));
			}

			if (searchPerson.getBirthdateYYYY() != null) {
				personPredicate = and(
					cb,
					personPredicate,
					or(
						cb,
						cb.isNull(person.get(Person.BIRTHDATE_YYYY)),
						cb.equal(person.get(Person.BIRTHDATE_YYYY), searchPerson.getBirthdateYYYY())));
			}

			if (searchPerson.getSex() != null) {
				personPredicate =
					and(cb, personPredicate, or(cb, cb.isNull(person.get(Person.SEX)), cb.equal(person.get(Person.SEX), searchPerson.getSex())));
			}

			final Predicate reportDatePredicate;
			if (reportDateThreshold == 0) {
				// threshold is zero: we want to get exact matches
				reportDatePredicate = cb.equal(
					cb.function(ExtendedPostgreSQL94Dialect.DATE, Date.class, caseRoot.get(Case.REPORT_DATE)),
					cb.function(ExtendedPostgreSQL94Dialect.DATE, Date.class, cb.literal(searchCaze.getReportDate())));
			} else {
				// threshold is nonzero: apply time range of threshold to the reportDate
				Date reportDate = casePerson.getCaze().getReportDate();
				Date dateBefore = DateHelper.subtractDays(reportDate, reportDateThreshold);
				Date dateAfter = DateHelper.addDays(reportDate, reportDateThreshold);
				reportDatePredicate = cb.between(
					cb.function(ExtendedPostgreSQL94Dialect.DATE, Date.class, caseRoot.get(Case.REPORT_DATE)),
					cb.function(ExtendedPostgreSQL94Dialect.DATE, Date.class, cb.literal(dateBefore)),
					cb.function(ExtendedPostgreSQL94Dialect.DATE, Date.class, cb.literal(dateAfter)));
			}

			Predicate districtPredicate = CriteriaBuilderHelper.or(
				cb,
				cb.equal(caseCaseJoins.getResponsibleDistrict().get(District.UUID), searchCaze.getResponsibleDistrict().getUuid()),
				cb.equal(caseCaseJoins.getDistrict().get(District.UUID), searchCaze.getResponsibleDistrict().getUuid()));
			if (searchCaze.getDistrict() != null) {
				districtPredicate = CriteriaBuilderHelper.or(
					cb,
					districtPredicate,
					cb.equal(caseCaseJoins.getResponsibleDistrict().get(District.UUID), searchCaze.getDistrict().getUuid()),
					cb.equal(caseCaseJoins.getDistrict().get(District.UUID), searchCaze.getDistrict().getUuid()));
			}

			combinedPredicate =
				and(cb, personPredicate, cb.equal(caseRoot.get(Case.DISEASE), searchCaze.getDisease()), reportDatePredicate, districtPredicate);
		}

		Predicate filters = or(cb, externalIdPredicate, externalTokenPredicate, combinedPredicate);
		if (filters == null) {
			return Collections.emptyList();
		}

		cq.where(filters);

		List<Object[]> duplicateUuids = em.createQuery(cq).getResultList();

		return duplicateUuids.stream()
			.map(
				(casePersonUuids) -> new CasePersonDto(
					getCaseDataByUuid((String) casePersonUuids[0]),
					personFacade.getByUuid((String) casePersonUuids[1])))
			.collect(Collectors.toList());
	}

	@Override
	public List<CaseDataDto> getDuplicatesWithPathogenTest(@Valid PersonReferenceDto personReferenceDto, PathogenTestDto pathogenTestDto) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(Case.class);
		Root<Case> caseRoot = cq.from(Case.class);

		CaseJoins caseCaseJoins = new CaseJoins(caseRoot);
		Join<Case, Person> personJoin = caseCaseJoins.getPerson();
		Join<Case, Sample> samplesJoin = caseCaseJoins.getSamples();
		Join<Sample, PathogenTest> pathogenTestJoin = caseCaseJoins.getSampleJoins().getPathogenTest();

		cq.select(caseRoot);
		Predicate filter = cb.and(
			cb.equal(caseRoot.get(Case.DISEASE), pathogenTestDto.getTestedDisease()),
			cb.equal(personJoin.get(Person.UUID), personReferenceDto.getUuid()),
			cb.equal(caseRoot.get(Case.DISEASE), pathogenTestJoin.get(PathogenTest.TESTED_DISEASE)),
			cb.exists(sampleService.exists(cb, cq, samplesJoin, pathogenTestDto.getSample().getUuid())));

		cq.where(filter);

		List<Case> duplicateCases = em.createQuery(cq).getResultList();
		return toDtos(duplicateCases.stream());
	}

	@Override
	public List<CasePersonDto> getDuplicates(@Valid CasePersonDto casePerson) {
		return getDuplicates(casePerson, 0);
	}

	@Override
	public List<CaseDataDto> getByPersonUuids(List<String> personUuids) {
		return toDtos(service.getByPersonUuids(personUuids).stream());
	}

	@Override
	public List<CaseDataDto> getByExternalId(String externalId) {
		return toPseudonymizedDtos(service.getByExternalId(externalId));
	}

	@Override
	@RightsAllowed(UserRight._CASE_EDIT)
	public void updateExternalData(@Valid List<ExternalDataDto> externalData) throws ExternalDataUpdateException {
		service.updateExternalData(externalData);
	}

	@RightsAllowed({
		UserRight._VISIT_CREATE,
		UserRight._VISIT_EDIT,
		UserRight._EXTERNAL_VISITS })
	public void updateSymptomsByVisit(Visit visit) {
		CaseDataDto cazeDto = toDto(visit.getCaze());
		SymptomsDto caseSymptoms = cazeDto.getSymptoms();
		SymptomsHelper.updateSymptoms(SymptomsFacadeEjb.toSymptomsDto(visit.getSymptoms()), caseSymptoms);

		caseSave(cazeDto, true, visit.getCaze(), cazeDto, true, true);
	}

	@LocalBean
	@Stateless
	public static class CaseFacadeEjbLocal extends CaseFacadeEjb {

		public CaseFacadeEjbLocal() {
		}

		@Inject
		public CaseFacadeEjbLocal(CaseService service) {
			super(service);
		}
	}
}
