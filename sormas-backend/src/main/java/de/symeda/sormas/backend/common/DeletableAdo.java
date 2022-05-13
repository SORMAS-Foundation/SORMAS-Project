package de.symeda.sormas.backend.common;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.common.DeleteReason;

/**
 * An extension of the {@link AbstractDomainObject} that defines data that is essential to the system.
 * This data may not be deleted from the system, e.g. because it is
 * relevant for external systems that share data with or use data from SORMAS.
 */
@MappedSuperclass
@Audited
public abstract class DeletableAdo extends AbstractDomainObject {

	private static final long serialVersionUID = 6512756286608581221L;

	public static final String DELETED = "deleted";
	public static final String DELETE_REASON = "deleteReason";
	public static final String OTHER_DELETE_REASON = "otherDeleteReason";

	private boolean deleted;
	private DeleteReason deleteReason;
	private String otherDeleteReason;

	@Column
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Enumerated(EnumType.STRING)
	public DeleteReason getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(DeleteReason deleteReason) {
		this.deleteReason = deleteReason;
	}

	@Column
	public String getOtherDeleteReason() {
		return otherDeleteReason;
	}

	public void setOtherDeleteReason(String deleteOtherReason) {
		this.otherDeleteReason = deleteOtherReason;
	}
}
