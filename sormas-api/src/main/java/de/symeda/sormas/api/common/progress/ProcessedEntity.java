package de.symeda.sormas.api.common.progress;

import java.io.Serializable;

import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
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

	public static ProcessedEntity withSucess(String entityUuid) {
		return new ProcessedEntity(entityUuid, ProcessedEntityStatus.SUCCESS);
	}

	public static ProcessedEntity withExternalSurveillanceFailure(String entityUuid) {
		return new ProcessedEntity(entityUuid, ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE);
	}

	public static ProcessedEntity withAccessDeniedFailure(String entityUuid) {
		return new ProcessedEntity(entityUuid, ProcessedEntityStatus.ACCESS_DENIED_FAILURE);
	}

	public static ProcessedEntity withInternalFailure(String entityUuid) {
		return new ProcessedEntity(entityUuid, ProcessedEntityStatus.INTERNAL_FAILURE);
	}
}
