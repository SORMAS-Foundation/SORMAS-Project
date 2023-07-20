package de.symeda.sormas.api.common.progress;

import java.io.Serializable;

public class ProcessedEntity implements Serializable {

	String entityUuid;

	ProcessedEntityStatus processedEntityStatus;

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
