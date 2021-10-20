package de.symeda.sormas.api.document;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class DocumentCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = -9174165215694877624L;

	@NotNull
	private DocumentRelatedEntityType documentRelatedEntityType;

	@NotNull
	private List<String> entityUuids;

	public DocumentRelatedEntityType getDocumentRelatedEntityType() {
		return documentRelatedEntityType;
	}

	public void setDocumentRelatedEntityType(DocumentRelatedEntityType documentRelatedEntityType) {
		this.documentRelatedEntityType = documentRelatedEntityType;
	}

	public List<String> getEntityUuids() {
		return entityUuids;
	}

	public void setEntityUuids(List<String> entityUuids) {
		this.entityUuids = entityUuids;
	}
}
