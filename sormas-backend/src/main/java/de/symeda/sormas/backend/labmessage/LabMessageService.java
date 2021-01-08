package de.symeda.sormas.backend.labmessage;

import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.BaseAdoService;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Stateless
@LocalBean
public class LabMessageService extends BaseAdoService<LabMessage> {

	public LabMessageService() {
		super(LabMessage.class);
	}

	public Predicate createStatusFilter(CriteriaBuilder cb, Root<LabMessage> labMessage, LabMessageCriteria criteria) {
		return cb.equal(labMessage.get(LabMessage.PROCESSED), criteria.getProcessed());
	}

}
