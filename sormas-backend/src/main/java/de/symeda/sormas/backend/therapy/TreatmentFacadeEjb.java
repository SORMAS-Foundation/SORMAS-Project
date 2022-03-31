package de.symeda.sormas.backend.therapy;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentExportDto;
import de.symeda.sormas.api.therapy.TreatmentFacade;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "TreatmentFacade")
@RolesAllowed(UserRight._CASE_VIEW)
public class TreatmentFacadeEjb implements TreatmentFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private TreatmentService service;
	@EJB
	private UserService userService;
	@EJB
	private TherapyService therapyService;
	@EJB
	private PrescriptionService prescriptionService;
	@EJB
	private CaseService caseService;

	@Override
	public List<TreatmentIndexDto> getIndexList(TreatmentCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TreatmentIndexDto> cq = cb.createQuery(TreatmentIndexDto.class);
		Root<Treatment> treatment = cq.from(Treatment.class);
		TreatmentJoins joins = new TreatmentJoins(treatment);

		cq.multiselect(
			treatment.get(Treatment.UUID),
			treatment.get(Treatment.TREATMENT_TYPE),
			treatment.get(Treatment.TREATMENT_DETAILS),
			treatment.get(Treatment.TYPE_OF_DRUG),
			treatment.get(Treatment.TREATMENT_DATE_TIME),
			treatment.get(Treatment.DOSE),
			treatment.get(Treatment.ROUTE),
			treatment.get(Treatment.ROUTE_DETAILS),
			treatment.get(Treatment.EXECUTING_CLINICIAN),
			JurisdictionHelper.booleanSelector(cb, caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, cq, joins.getCaze()))));

		if (criteria != null) {
			cq.where(service.buildCriteriaFilter(criteria, cb, treatment));
		}

		cq.orderBy(cb.desc(treatment.get(Treatment.TREATMENT_DATE_TIME)));

		List<TreatmentIndexDto> indexList = em.createQuery(cq).getResultList();

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(TreatmentIndexDto.class, indexList, t -> t.getInJurisdiction(), (t, inJurisdiction) -> {
			pseudonymizer.pseudonymizeDto(TreatmentIndexDto.TreatmentIndexType.class, t.getTreatmentIndexType(), inJurisdiction, null);
			pseudonymizer.pseudonymizeDto(TreatmentIndexDto.TreatmentIndexRoute.class, t.getTreatmentIndexRoute(), inJurisdiction, null);
		});

		return indexList;
	}

	@Override
	public TreatmentDto getTreatmentByUuid(String uuid) {
		return convertToDto(service.getByUuid(uuid), Pseudonymizer.getDefault(userService::hasRight));
	}

	@Override
	@RolesAllowed({UserRight._TREATMENT_CREATE, UserRight._TREATMENT_EDIT})
	public TreatmentDto saveTreatment(@Valid TreatmentDto source) {
		Treatment existingTreatment = service.getByUuid(source.getUuid());
		TreatmentDto existingDto = toDto(existingTreatment);

		restorePseudonymizedDto(source, existingTreatment, existingDto);

		Treatment entity = fromDto(source, existingTreatment, true);
		service.ensurePersisted(entity);
		return toDto(entity);
	}

	@Override
	@RolesAllowed(UserRight._TREATMENT_DELETE)
	public void deleteTreatment(String treatmentUuid) {
		Treatment treatment = service.getByUuid(treatmentUuid);
		service.deletePermanent(treatment);
	}

	@Override
	public List<TreatmentDto> getAllActiveTreatmentsAfter(Date date) {
		return getAllActiveTreatmentsAfter(date, null, null);
	}

	@Override
	public List<TreatmentDto> getAllActiveTreatmentsAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {
		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return service.getAllActiveTreatmentsAfter(date, user, batchSize, lastSynchronizedUuid)
			.stream()
			.map(t -> convertToDto(t, pseudonymizer))
			.collect(Collectors.toList());
	}

	@Override
	public List<TreatmentDto> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return service.getByUuids(uuids).stream().map(t -> convertToDto(t, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllActiveUuids() {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveUuids(user);
	}

	@Override
	public List<TreatmentExportDto> getExportList(CaseCriteria criteria, Collection<String> selectedRows, int first, int max) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TreatmentExportDto> cq = cb.createQuery(TreatmentExportDto.class);
		Root<Treatment> treatment = cq.from(Treatment.class);
		TreatmentJoins joins = new TreatmentJoins(treatment);

		cq.multiselect(
			joins.getCaze().get(Case.UUID),
			joins.getCasePerson().get(Person.FIRST_NAME),
			joins.getCasePerson().get(Person.LAST_NAME),
			treatment.get(Treatment.TREATMENT_DATE_TIME),
			treatment.get(Treatment.EXECUTING_CLINICIAN),
			treatment.get(Treatment.TREATMENT_TYPE),
			treatment.get(Treatment.TREATMENT_DETAILS),
			treatment.get(Treatment.TYPE_OF_DRUG),
			treatment.get(Treatment.DOSE),
			treatment.get(Treatment.ROUTE),
			treatment.get(Treatment.ROUTE_DETAILS),
			treatment.get(Treatment.ADDITIONAL_NOTES),
			JurisdictionHelper.booleanSelector(cb, caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, cq, joins.getCaze()))));

		Predicate filter = service.createUserFilter(cb, cq, treatment);
		Predicate criteriaFilter = caseService.createCriteriaFilter(criteria, new CaseQueryContext(cb, cq, joins.getCaze()));
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, joins.getCaze().get(Case.UUID));
		cq.where(filter);
		cq.orderBy(cb.desc(joins.getCaze().get(Case.UUID)), cb.desc(treatment.get(Treatment.TREATMENT_DATE_TIME)));

		List<TreatmentExportDto> exportList = QueryHelper.getResultList(em, cq, first, max);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(TreatmentExportDto.class, exportList, t -> t.getInJurisdiction(), null);

		return exportList;
	}

	private TreatmentDto convertToDto(Treatment source, Pseudonymizer pseudonymizer) {
		TreatmentDto dto = toDto(source);

		pseudonymizeDto(source, dto, pseudonymizer);

		return dto;
	}

	private void pseudonymizeDto(Treatment source, TreatmentDto dto, Pseudonymizer pseudonymizer) {
		if (source != null && dto != null) {
			pseudonymizer.pseudonymizeDto(TreatmentDto.class, dto, caseService.inJurisdictionOrOwned(source.getTherapy().getCaze()), null);
		}
	}

	private void restorePseudonymizedDto(TreatmentDto source, Treatment existingTreatment, TreatmentDto existingDto) {
		if (existingTreatment != null) {
			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
			pseudonymizer.restorePseudonymizedValues(
				TreatmentDto.class,
				source,
				existingDto,
				caseService.inJurisdictionOrOwned(existingTreatment.getTherapy().getCaze()));
		}
	}

	public static TreatmentDto toDto(Treatment source) {

		if (source == null) {
			return null;
		}

		TreatmentDto target = new TreatmentDto();
		DtoHelper.fillDto(target, source);

		target.setTherapy(TherapyFacadeEjb.toReferenceDto(source.getTherapy()));
		target.setTreatmentType(source.getTreatmentType());
		target.setTreatmentDetails(source.getTreatmentDetails());
		target.setTypeOfDrug(source.getTypeOfDrug());
		target.setTreatmentDateTime(source.getTreatmentDateTime());
		target.setExecutingClinician(source.getExecutingClinician());
		target.setDose(source.getDose());
		target.setRoute(source.getRoute());
		target.setRouteDetails(source.getRouteDetails());
		target.setAdditionalNotes(source.getAdditionalNotes());
		target.setPrescription(PrescriptionFacadeEjb.toReferenceDto(source.getPrescription()));

		return target;
	}

	public Treatment fromDto(@NotNull TreatmentDto source, Treatment target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, Treatment::new, checkChangeDate);

		target.setTherapy(therapyService.getByReferenceDto(source.getTherapy()));
		target.setTreatmentType(source.getTreatmentType());
		target.setTreatmentDetails(source.getTreatmentDetails());
		target.setTypeOfDrug(source.getTypeOfDrug());
		target.setTreatmentDateTime(source.getTreatmentDateTime());
		target.setExecutingClinician(source.getExecutingClinician());
		target.setDose(source.getDose());
		target.setRoute(source.getRoute());
		target.setRouteDetails(source.getRouteDetails());
		target.setAdditionalNotes(source.getAdditionalNotes());
		target.setPrescription(prescriptionService.getByReferenceDto(source.getPrescription()));

		return target;
	}

	@LocalBean
	@Stateless
	public static class TreatmentFacadeEjbLocal extends TreatmentFacadeEjb {

	}
}
