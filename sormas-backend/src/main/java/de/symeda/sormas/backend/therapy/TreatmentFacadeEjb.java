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
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentFacade;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "TreatmentFacade")
public class TreatmentFacadeEjb implements TreatmentFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	
	@EJB
	private TreatmentService service;
	@EJB
	private UserService userService;
	@EJB
	private TherapyService therapyService;
	@EJB
	private PrescriptionService prescriptionService;
	
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
	public void deleteTreatment(String treatmentUuid, String userUuid) {
		User user = userService.getByUuid(userUuid);
		// TODO replace this with a proper user right call #944
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities");
		}
		
		Treatment treatment = service.getByUuid(treatmentUuid);
		service.delete(treatment);
	}

	@Override
	public List<TreatmentDto> getAllActiveTreatmentsAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
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
	public List<String> getAllActiveUuids(String userUuid) {
		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}
		
		return service.getAllActiveUuids(user);
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
