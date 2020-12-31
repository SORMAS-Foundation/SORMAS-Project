package de.symeda.sormas.backend.labmessage;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AbstractAdoService;

public class LabMessageService extends AbstractAdoService<LabMessage> {

	public LabMessageService() {
		super(LabMessage.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, ? extends LabMessage> from) {
		throw new UnsupportedOperationException();
	}
}
