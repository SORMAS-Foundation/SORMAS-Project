package de.symeda.sormas.backend.therapy;

import java.sql.Timestamp;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.PrescriptionIndexDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TherapyFacade;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "TherapyFacade")
public class TherapyFacadeEjb implements TherapyFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	
	@EJB
	TherapyService service;
	@EJB
	PrescriptionService prescriptionService;
	@EJB
	TreatmentService treatmentService;
	@EJB
	private UserService userService;
	
	@Override
	public List<PrescriptionIndexDto> getPrescriptionIndexList(PrescriptionCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PrescriptionIndexDto> cq = cb.createQuery(PrescriptionIndexDto.class);
		Root<Prescription> prescription = cq.from(Prescription.class);
		
		cq.multiselect(
				prescription.get(Prescription.UUID),
				prescription.get(Prescription.PRESCRIPTION_TYPE),
				prescription.get(Prescription.PRESCRIPTION_DETAILS),
				prescription.get(Prescription.PRESCRIPTION_DATE),
				prescription.get(Prescription.PRESCRIPTION_START),
				prescription.get(Prescription.PRESCRIPTION_END),
				prescription.get(Prescription.FREQUENCY),
				prescription.get(Prescription.DOSE),
				prescription.get(Prescription.ROUTE),
				prescription.get(Prescription.ROUTE_DETAILS),
				prescription.get(Prescription.PRESCRIBING_CLINICIAN));
		
		if (criteria != null) {
			cq.where(prescriptionService.buildCriteriaFilter(criteria, cb, prescription));
		}
		
		cq.orderBy(cb.desc(prescription.get(Prescription.PRESCRIPTION_DATE)));
		
		return em.createQuery(cq).getResultList();
	}

	@Override
	public List<TreatmentIndexDto> getTreatmentIndexList(TreatmentCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TreatmentIndexDto> cq = cb.createQuery(TreatmentIndexDto.class);
		Root<Treatment> treatment = cq.from(Treatment.class);
		
		cq.multiselect(
				treatment.get(Treatment.UUID),
				treatment.get(Treatment.TREATMENT_TYPE),
				treatment.get(Treatment.TREATMENT_DETAILS),
				treatment.get(Treatment.TREATMENT_DATE_TIME),
				treatment.get(Treatment.DOSE),
				treatment.get(Treatment.ROUTE),
				treatment.get(Treatment.ROUTE_DETAILS),
				treatment.get(Treatment.EXECUTING_CLINICIAN));
		
		if (criteria != null) {
			cq.where(treatmentService.buildCriteriaFilter(criteria, cb, treatment));
		}
		
		cq.orderBy(cb.desc(treatment.get(Treatment.TREATMENT_DATE_TIME)));
		
		return em.createQuery(cq).getResultList();
	}
	
	@Override
	public PrescriptionDto getPrescriptionByUuid(String uuid) {
		return toPrescriptionDto(prescriptionService.getByUuid(uuid));
	}
	
	@Override
	public TreatmentDto getTreatmentByUuid(String uuid) {
		return toTreatmentDto(treatmentService.getByUuid(uuid));
	}
	
	@Override
	public PrescriptionDto savePrescription(PrescriptionDto prescription) {
		Prescription entity = fromPrescriptionDto(prescription);
		
		prescriptionService.ensurePersisted(entity);
		
		return toPrescriptionDto(entity);
	}
	
	@Override
	public TreatmentDto saveTreatment(TreatmentDto treatment) {
		Treatment entity = fromTreatmentDto(treatment);
		
		treatmentService.ensurePersisted(entity);
		
		return toTreatmentDto(entity);
	}
	
	@Override
	public void deletePrescription(String prescriptionUuid, String userUuid) {
		User user = userService.getByUuid(userUuid);
		// TODO replace this with a proper user right call #944
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities");
		}
		
		Prescription prescription = prescriptionService.getByUuid(prescriptionUuid);
		prescriptionService.delete(prescription);
	}
	
	@Override
	public void deleteTreatment(String treatmentUuid, String userUuid) {
		User user = userService.getByUuid(userUuid);
		// TODO replace this with a proper user right call #944
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities");
		}
		
		Treatment treatment = treatmentService.getByUuid(treatmentUuid);
		treatmentService.delete(treatment);
	}

	public static TherapyDto toDto(Therapy source) {
		if (source == null) {
			return null;
		}
		TherapyDto target = new TherapyDto();
		DtoHelper.fillDto(target, source);

		return target;
	}

	public Therapy fromDto(@NotNull TherapyDto source) {
		Therapy target = service.getByUuid(source.getUuid());
		
		if (target == null) {
			target = new Therapy();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}

		DtoHelper.validateDto(source, target);

		return target;
	}
	
	public static PrescriptionDto toPrescriptionDto(Prescription source) {
		if (source == null) {
			return null;
		}
		
		PrescriptionDto target = new PrescriptionDto();
		DtoHelper.fillDto(target, source);
		
		target.setTherapy(toDto(source.getTherapy()));
		target.setPrescriptionType(source.getPrescriptionType());
		target.setPrescriptionDetails(source.getPrescriptionDetails());
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
	
	public Prescription fromPrescriptionDto(@NotNull PrescriptionDto source) {
		Prescription target = prescriptionService.getByUuid(source.getUuid());
		
		if (target == null) {
			target = new Prescription();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		
		DtoHelper.validateDto(source, target);
		
		target.setTherapy(fromDto(source.getTherapy()));
		target.setPrescriptionType(source.getPrescriptionType());
		target.setPrescriptionDetails(source.getPrescriptionDetails());
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
	
	public static TreatmentDto toTreatmentDto(Treatment source) {
		if (source == null) {
			return null;
		}
		
		TreatmentDto target = new TreatmentDto();
		DtoHelper.fillDto(target, source);
		
		target.setTherapy(toDto(source.getTherapy()));
		target.setTreatmentType(source.getTreatmentType());
		target.setTreatmentDetails(source.getTreatmentDetails());
		target.setTreatmentDateTime(source.getTreatmentDateTime());
		target.setExecutingClinician(source.getExecutingClinician());
		target.setDose(source.getDose());
		target.setRoute(source.getRoute());
		target.setRouteDetails(source.getRouteDetails());
		target.setAdditionalNotes(source.getAdditionalNotes());
		
		return target;
	}
	
	public Treatment fromTreatmentDto(@NotNull TreatmentDto source) {
		Treatment target = treatmentService.getByUuid(source.getUuid());
		
		if (target == null) {
			target = new Treatment();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		
		DtoHelper.validateDto(source, target);

		target.setTherapy(fromDto(source.getTherapy()));
		target.setTreatmentType(source.getTreatmentType());
		target.setTreatmentDetails(source.getTreatmentDetails());
		target.setTreatmentDateTime(source.getTreatmentDateTime());
		target.setExecutingClinician(source.getExecutingClinician());
		target.setDose(source.getDose());
		target.setRoute(source.getRoute());
		target.setRouteDetails(source.getRouteDetails());
		target.setAdditionalNotes(source.getAdditionalNotes());
		
		return target;
	}

	@LocalBean
	@Stateless
	public static class TherapyFacadeEjbLocal extends TherapyFacadeEjb {

	}

}
