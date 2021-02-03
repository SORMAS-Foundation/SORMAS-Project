package de.symeda.sormas.backend.labmessage;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class LabMessageService extends BaseAdoService<LabMessage> {

	public LabMessageService() {
		super(LabMessage.class);
	}

	public Predicate buildCriteriaFilter(CriteriaBuilder cb, Root<LabMessage> labMessage, LabMessageCriteria criteria) {
		Predicate filter = null;
		if (criteria.getProcessed() != null) {
			filter = cb.equal(labMessage.get(LabMessage.PROCESSED), criteria.getProcessed());
		}
		return filter;
	}

}
