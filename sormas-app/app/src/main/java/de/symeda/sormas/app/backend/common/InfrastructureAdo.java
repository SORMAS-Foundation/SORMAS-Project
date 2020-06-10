package de.symeda.sormas.app.backend.common;

import javax.persistence.MappedSuperclass;

import com.j256.ormlite.field.DatabaseField;

@MappedSuperclass
public class InfrastructureAdo extends AbstractDomainObject {

	public static final String ARCHIVED = "archived";

	@DatabaseField
	private boolean archived = false;

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}
}
