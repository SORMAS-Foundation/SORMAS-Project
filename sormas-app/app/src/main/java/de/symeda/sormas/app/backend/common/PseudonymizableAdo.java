package de.symeda.sormas.app.backend.common;

import javax.persistence.MappedSuperclass;

import com.j256.ormlite.field.DatabaseField;

@MappedSuperclass
public abstract class PseudonymizableAdo extends AbstractDomainObject {

	@DatabaseField
	private boolean pseudonymized;

	public boolean isPseudonymized() {
		return pseudonymized;
	}

	public void setPseudonymized(boolean pseudonymized) {
		this.pseudonymized = pseudonymized;
	}
}
