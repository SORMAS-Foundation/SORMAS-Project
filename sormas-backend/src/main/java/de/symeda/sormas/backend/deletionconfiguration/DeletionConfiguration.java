package de.symeda.sormas.backend.deletionconfiguration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = DeletionConfiguration.TABLE_NAME)
@Audited
public class DeletionConfiguration extends AbstractDomainObject {

	private static final long serialVersionUID = 4027927530174727321L;

	public static final String TABLE_NAME = "deletionconfiguration";

	public static final String ENTITY_TYPE = "entityType";
	public static final String DELETION_REFERENCE = "deletionReference";
	public static final String DELETION_PERIOD = "deletionPeriod";

	private CoreEntityType entityType;
	private DeletionReference deletionReference;

	@Min(value = 7, message = Validations.numberTooSmall)
	@Max(value = Integer.MAX_VALUE, message = Validations.numberTooBig)
	Integer deletionPeriod;

	public static DeletionConfiguration build(CoreEntityType coreEntityType) {
		DeletionConfiguration deletionConfiguration = new DeletionConfiguration();
		deletionConfiguration.setEntityType(coreEntityType);
		return deletionConfiguration;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public CoreEntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(CoreEntityType entityType) {
		this.entityType = entityType;
	}

	@Enumerated(EnumType.STRING)
	@Column()
	public DeletionReference getDeletionReference() {
		return deletionReference;
	}

	public void setDeletionReference(DeletionReference deletionReference) {
		this.deletionReference = deletionReference;
	}

	@Column()
	public Integer getDeletionPeriod() {
		return deletionPeriod;
	}

	public void setDeletionPeriod(Integer deletionPeriod) {
		this.deletionPeriod = deletionPeriod;
	}
}
