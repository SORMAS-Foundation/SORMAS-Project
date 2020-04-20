package de.symeda.sormas.backend.therapy;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentExportDto;
import de.symeda.sormas.api.therapy.TreatmentFacade;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "TreatmentFacade")
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
		
		cq.multiselect(
				treatment.get(Treatment.UUID),
				treatment.get(Treatment.TREATMENT_TYPE),
				treatment.get(Treatment.TREATMENT_DETAILS),
				treatment.get(Treatment.TYPE_OF_DRUG),
				treatment.get(Treatment.TREATMENT_DATE_TIME),
				treatment.get(Treatment.DOSE),
				treatment.get(Treatment.ROUTE),
				treatment.get(Treatment.ROUTE_DETAILS),
				treatment.get(Treatment.EXECUTING_CLINICIAN));
		
		if (criteria != null) {
			cq.where(service.buildCriteriaFilter(criteria, cb, treatment));
		}
		
		cq.orderBy(cb.desc(treatment.get(Treatment.TREATMENT_DATE_TIME)));
		
		return em.createQuery(cq).getResultList();
	}
	
	@Override
	public TreatmentDto getTreatmentByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	@Override
	public TreatmentDto saveTreatment(TreatmentDto treatment) {
		Treatment entity = fromDto(treatment);
		
		service.ensurePersisted(entity);
		
		return toDto(entity);
	}

	@Override
	public void deleteTreatment(String treatmentUuid) {
		User user = userService.getCurrentUser();
		// TODO replace this with a proper user right call #944
		if (!user.hasAnyUserRole(UserRole.ADMIN, UserRole.CASE_SUPERVISOR)) {
			throw new UnsupportedOperationException("Only admins and clinicians are allowed to delete treatments");
		}
		
		Treatment treatment = service.getByUuid(treatmentUuid);
		service.delete(treatment);
	}

	@Override
	public List<TreatmentDto> getAllActiveTreatmentsAfter(Date date) {
		User user = userService.getCurrentUser();
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return service.getAllActiveTreatmentsAfter(date, user).stream()
				.map(t -> toDto(t))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<TreatmentDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids)
				.stream()
				.map(t -> toDto(t))
				.collect(Collectors.toList());
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
	public List<TreatmentExportDto> getExportList(CaseCriteria criteria, int first, int max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TreatmentExportDto> cq = cb.createQuery(TreatmentExportDto.class);
		Root<Treatment> treatment = cq.from(Treatment.class);
		Join<Treatment, Therapy> therapy = treatment.join(Treatment.THERAPY, JoinType.LEFT);
		Join<Therapy, Case> caze = therapy.join(Therapy.CASE, JoinType.LEFT);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);
		
		cq.multiselect(
				caze.get(Case.UUID),
				person.get(Person.FIRST_NAME),
				person.get(Person.LAST_NAME),
				treatment.get(Treatment.TREATMENT_DATE_TIME),
				treatment.get(Treatment.EXECUTING_CLINICIAN),
				treatment.get(Treatment.TREATMENT_TYPE),
				treatment.get(Treatment.TREATMENT_DETAILS),
				treatment.get(Treatment.TYPE_OF_DRUG),
				treatment.get(Treatment.DOSE),
				treatment.get(Treatment.ROUTE),
				treatment.get(Treatment.ROUTE_DETAILS),
				treatment.get(Treatment.ADDITIONAL_NOTES));
		
		Predicate filter = service.createUserFilter(cb, cq, treatment);
		Join<Case, Case> casePath = therapy.join(Therapy.CASE);
		Predicate criteriaFilter = caseService.createCriteriaFilter(criteria, cb, cq, casePath);
		filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		cq.where(filter);
		cq.orderBy(cb.desc(caze.get(Case.UUID)), cb.desc(treatment.get(Treatment.TREATMENT_DATE_TIME)));
		
		return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
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
	
	public Treatment fromDto(@NotNull TreatmentDto source) {
		Treatment target = service.getByUuid(source.getUuid());
		
		if (target == null) {
			target = new Treatment();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		
		DtoHelper.validateDto(source, target);

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
