package de.symeda.auditlog.api.sample;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedAttribute;
import de.symeda.auditlog.api.HasUuid;

@Audited
public class AnonymizedEntity implements HasUuid {

	public static final String ANONYMIZING = "xxx";

	public static final String PWD = "pwd";

	private final String uuid;
	private final String pwd;

	public AnonymizedEntity(String uuid, String pwd) {
		this.uuid = uuid;
		this.pwd = pwd;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@AuditedAttribute(anonymous = true, anonymizingString = ANONYMIZING)
	public String getPwd() {
		return pwd;
	}

}
