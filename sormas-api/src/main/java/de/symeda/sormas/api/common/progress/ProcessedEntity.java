package de.symeda.sormas.api.common.progress;

import java.io.Serializable;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.uuid.HasUuid;

@AuditedClass
public class ProcessedEntity implements Serializable {

	String entityUuid;

	ProcessedEntityStatus processedEntityStatus;

	public static ProcessedEntity successful(HasUuid entity) {
		return new ProcessedEntity(entity.getUuid(), ProcessedEntityStatus.SUCCESS);
	}

	public static ProcessedEntity failedInternally(HasUuid entity) {
		return new ProcessedEntity(entity.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE);
	}

	public ProcessedEntity(String entityUuid, ProcessedEntityStatus processedEntityStatus) {
		this.entityUuid = entityUuid;
		this.processedEntityStatus = processedEntityStatus;
	}

	public String getEntityUuid() {
		return entityUuid;
	}

	public void setEntityUuid(String entityUuid) {
		this.entityUuid = entityUuid;
	}

	public ProcessedEntityStatus getProcessedEntityStatus() {
		return processedEntityStatus;
	}

	public void setProcessedEntityStatus(ProcessedEntityStatus processedEntityStatus) {
		this.processedEntityStatus = processedEntityStatus;
	}
}
