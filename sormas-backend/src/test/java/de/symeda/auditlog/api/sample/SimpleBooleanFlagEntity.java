package de.symeda.auditlog.api.sample;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedAttribute;
import de.symeda.auditlog.api.HasUuid;

@Audited
public class SimpleBooleanFlagEntity implements HasUuid {

	public static final String FLAG = "flag";

	private final String uuid;
	private final Boolean flag;

	public SimpleBooleanFlagEntity(String uuid, Boolean flag) {

		this.uuid = uuid;

		this.flag = flag;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@AuditedAttribute(DemoBooleanFormatter.class)
	public Boolean getFlag() {
		return flag;
	}

}