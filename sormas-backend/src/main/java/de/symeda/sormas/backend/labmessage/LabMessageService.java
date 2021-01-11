package de.symeda.sormas.backend.labmessage;

import de.symeda.sormas.backend.common.BaseAdoService;

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
