package de.symeda.sormas.backend.deletionconfiguration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = DeletionConfiguration.TABLE_NAME)
public class DeletionConfiguration extends AbstractDomainObject {

	private static final long serialVersionUID = 4027927530174727321L;

	public static final String TABLE_NAME = "deletionconfiguration";

	public static final String ENTITY_TYPE = "entityType";
	public static final String DELETION_REFERENCE = "deletionReference";
	public static final String DELETION_PERIOD = "deletionPeriod";

	private DeletableEntityType entityType;
	private DeletionReference deletionReference;

	@Min(value = 7, message = Validations.numberTooSmall)
	@Max(value = Integer.MAX_VALUE, message = Validations.numberTooBig)
	public Integer deletionPeriod;

	public static DeletionConfiguration build(DeletableEntityType deletableEntityType) {

		return build(deletableEntityType, null, null);
	}

	public static DeletionConfiguration build(DeletableEntityType deletableEntityType, DeletionReference deletionReference) {

		return build(deletableEntityType, deletionReference, null);
	}

	public static DeletionConfiguration build(DeletableEntityType deletableEntityType, DeletionReference deletionReference, Integer deletionPeriod) {

		DeletionConfiguration deletionConfiguration = new DeletionConfiguration();
		deletionConfiguration.setEntityType(deletableEntityType);
		deletionConfiguration.setDeletionReference(deletionReference);
		deletionConfiguration.setDeletionPeriod(deletionPeriod);
		return deletionConfiguration;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public DeletableEntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(DeletableEntityType entityType) {
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
