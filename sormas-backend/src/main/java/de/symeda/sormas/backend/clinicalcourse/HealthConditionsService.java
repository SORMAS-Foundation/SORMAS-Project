package de.symeda.sormas.backend.clinicalcourse;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class HealthConditionsService extends AbstractAdoService<HealthConditions> {

	public HealthConditionsService() {
		super(HealthConditions.class);
	}

}
