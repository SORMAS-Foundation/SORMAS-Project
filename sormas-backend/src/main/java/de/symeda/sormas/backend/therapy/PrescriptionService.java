package de.symeda.sormas.backend.therapy;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class PrescriptionService extends AbstractAdoService<Prescription> {

	public PrescriptionService() {
		super(Prescription.class);
	}
	
	public Predicate buildCriteriaFilter(PrescriptionCriteria criteria, CriteriaBuilder cb, Root<Prescription> prescription) {
		Predicate filter = null;
		Join<Prescription, Therapy> therapy = prescription.join(Prescription.THERAPY, JoinType.LEFT);
		
		if (criteria.getTherapy() != null) {
			filter = and(cb, filter, cb.equal(therapy.get(Therapy.UUID), criteria.getTherapy().getUuid()));
		}
		if (criteria.getPrescriptionType() != null) {
			filter = and(cb, filter, cb.equal(prescription.get(Prescription.PRESCRIPTION_TYPE), criteria.getPrescriptionType()));
		}
		if (!StringUtils.isEmpty(criteria.getTextFilter())) {
			String[] textFilters = criteria.getTextFilter().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = "%" + textFilters[i].toLowerCase() + "%";
				if (!StringUtils.isEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
							cb.like(cb.lower(prescription.get(Prescription.PRESCRIPTION_TYPE)), textFilter),
							cb.like(cb.lower(prescription.get(Prescription.PRESCRIPTION_DETAILS)), textFilter),
							cb.like(cb.lower(prescription.get(Prescription.TYPE_OF_DRUG)), textFilter),
							cb.like(cb.lower(prescription.get(Prescription.PRESCRIBING_CLINICIAN)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		
		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Prescription, Prescription> from, User user) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}
	
}
