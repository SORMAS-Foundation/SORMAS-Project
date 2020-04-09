package de.symeda.sormas.backend.therapy;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class TherapyService extends AbstractAdoService<Therapy> {

	public TherapyService() {
		super(Therapy.class);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Therapy, Therapy> from) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}
	
}
