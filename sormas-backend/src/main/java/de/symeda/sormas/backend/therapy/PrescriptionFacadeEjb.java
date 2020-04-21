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
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.PrescriptionExportDto;
import de.symeda.sormas.api.therapy.PrescriptionFacade;
import de.symeda.sormas.api.therapy.PrescriptionIndexDto;
import de.symeda.sormas.api.therapy.PrescriptionReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "PrescriptionFacade")
public class PrescriptionFacadeEjb implements PrescriptionFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	
	@EJB
	private PrescriptionService service;
	@EJB
	private UserService userService;
	@EJB
	private TherapyService therapyService;
	@EJB
	private CaseService caseService;
	
	@Override
	public List<PrescriptionIndexDto> getIndexList(PrescriptionCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PrescriptionIndexDto> cq = cb.createQuery(PrescriptionIndexDto.class);
		Root<Prescription> prescription = cq.from(Prescription.class);
		
		cq.multiselect(
				prescription.get(Prescription.UUID),
				prescription.get(Prescription.PRESCRIPTION_TYPE),
				prescription.get(Prescription.PRESCRIPTION_DETAILS),
				prescription.get(Prescription.TYPE_OF_DRUG),
				prescription.get(Prescription.PRESCRIPTION_DATE),
				prescription.get(Prescription.PRESCRIPTION_START),
				prescription.get(Prescription.PRESCRIPTION_END),
				prescription.get(Prescription.FREQUENCY),
				prescription.get(Prescription.DOSE),
				prescription.get(Prescription.ROUTE),
				prescription.get(Prescription.ROUTE_DETAILS),
				prescription.get(Prescription.PRESCRIBING_CLINICIAN));
		
		if (criteria != null) {
			cq.where(service.buildCriteriaFilter(criteria, cb, prescription));
		}
		
		cq.orderBy(cb.desc(prescription.get(Prescription.PRESCRIPTION_DATE)));
	
		return em.createQuery(cq).getResultList();
	}
	
	@Override
	public PrescriptionDto getPrescriptionByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}
	
	@Override
	public PrescriptionDto savePrescription(PrescriptionDto prescription) {
		Prescription entity = fromDto(prescription);
		
		service.ensurePersisted(entity);
		
		return toDto(entity);
	}

	@Override
	public void deletePrescription(String prescriptionUuid) {
		User user = userService.getCurrentUser();
		// TODO replace this with a proper user right call #944
		if (!user.hasAnyUserRole(UserRole.ADMIN, UserRole.CASE_SUPERVISOR)) {
			throw new UnsupportedOperationException("Only admins and clinicians are allowed to delete prescriptions");
		}
		
		Prescription prescription = service.getByUuid(prescriptionUuid);
		service.delete(prescription);
	}
	
	@Override
	public List<PrescriptionDto> getAllActivePrescriptionsAfter(Date date) {
		User user = userService.getCurrentUser();
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return service.getAllActivePrescriptionsAfter(date, user).stream()
				.map(p -> toDto(p))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<PrescriptionDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids)
				.stream()
				.map(p -> toDto(p))
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
	public List<PrescriptionExportDto> getExportList(CaseCriteria criteria, int first, int max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PrescriptionExportDto> cq = cb.createQuery(PrescriptionExportDto.class);
		Root<Prescription> prescription = cq.from(Prescription.class);
		Join<Prescription, Therapy> therapy = prescription.join(Prescription.THERAPY, JoinType.LEFT);
		Join<Therapy, Case> caze = therapy.join(Therapy.CASE, JoinType.LEFT);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);
		
		cq.multiselect(
				caze.get(Case.UUID),
				person.get(Person.FIRST_NAME),
				person.get(Person.LAST_NAME),
				prescription.get(Prescription.PRESCRIPTION_DATE),
				prescription.get(Prescription.PRESCRIPTION_START),
				prescription.get(Prescription.PRESCRIPTION_END),
				prescription.get(Prescription.PRESCRIBING_CLINICIAN),
				prescription.get(Prescription.PRESCRIPTION_TYPE),
				prescription.get(Prescription.PRESCRIPTION_DETAILS),
				prescription.get(Prescription.TYPE_OF_DRUG),
				prescription.get(Prescription.FREQUENCY),
				prescription.get(Prescription.DOSE),
				prescription.get(Prescription.ROUTE),
				prescription.get(Prescription.ROUTE_DETAILS),
				prescription.get(Prescription.ADDITIONAL_NOTES));
		
		Predicate filter = service.createUserFilter(cb, cq, prescription);
		Join<Case, Case> casePath = therapy.join(Therapy.CASE);
		Predicate criteriaFilter = caseService.createCriteriaFilter(criteria, cb, cq, casePath);
		filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		cq.where(filter);
		cq.orderBy(cb.desc(caze.get(Case.UUID)), cb.desc(prescription.get(Prescription.PRESCRIPTION_DATE)));
		
		return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
	}
	
	public static PrescriptionDto toDto(Prescription source) {
		if (source == null) {
			return null;
		}
		
		PrescriptionDto target = new PrescriptionDto();
		DtoHelper.fillDto(target, source);
		
		target.setTherapy(TherapyFacadeEjb.toReferenceDto(source.getTherapy()));
		target.setPrescriptionType(source.getPrescriptionType());
		target.setPrescriptionDetails(source.getPrescriptionDetails());
		target.setTypeOfDrug(source.getTypeOfDrug());
		target.setPrescriptionDate(source.getPrescriptionDate());
		target.setPrescribingClinician(source.getPrescribingClinician());
		target.setPrescriptionStart(source.getPrescriptionStart());
		target.setPrescriptionEnd(source.getPrescriptionEnd());
		target.setFrequency(source.getFrequency());
		target.setDose(source.getDose());
		target.setRoute(source.getRoute());
		target.setRouteDetails(source.getRouteDetails());
		target.setAdditionalNotes(source.getAdditionalNotes());
		
		return target;
	}
	
	public static PrescriptionReferenceDto toReferenceDto(Prescription source) {
		if (source == null) {
			return null;
		}
		
		PrescriptionReferenceDto reference = new PrescriptionReferenceDto(source.getUuid(), source.toString());
		return reference;
	}
	
	public Prescription fromDto(@NotNull PrescriptionDto source) {
		Prescription target = service.getByUuid(source.getUuid());
		
		if (target == null) {
			target = new Prescription();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		
		DtoHelper.validateDto(source, target);
		
		target.setTherapy(therapyService.getByReferenceDto(source.getTherapy()));
		target.setPrescriptionType(source.getPrescriptionType());
		target.setPrescriptionDetails(source.getPrescriptionDetails());
		target.setTypeOfDrug(source.getTypeOfDrug());
		target.setPrescriptionDate(source.getPrescriptionDate());
		target.setPrescribingClinician(source.getPrescribingClinician());
		target.setPrescriptionStart(source.getPrescriptionStart());
		target.setPrescriptionEnd(source.getPrescriptionEnd());
		target.setFrequency(source.getFrequency());
		target.setDose(source.getDose());
		target.setRoute(source.getRoute());
		target.setRouteDetails(source.getRouteDetails());
		target.setAdditionalNotes(source.getAdditionalNotes());
		
		return target;
	}

	@LocalBean
	@Stateless
	public static class PrescriptionFacadeEjbLocal extends PrescriptionFacadeEjb {

	}
	
}
