package de.symeda.auditlog.api.sample;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.auditlog.api.HasUuid;

@Audited
public class EntityWithHelperAttributes implements HasUuid {
	
	private final String uuid;
	private EntityWithIgnoredMethods firstEntity;
	private EntityWithIgnoredMethods secondEntity;
	private EntityWithIgnoredMethods thirdEntity;
	
	public EntityWithHelperAttributes(String uuid) {
		this.uuid = uuid;
	}
	
	@Override
	@AuditedIgnore
	public String getUuid() {
		return uuid;
	}
	
	@OneToOne
	public EntityWithIgnoredMethods getFirstEntity() {
		return firstEntity;
	}
	
	public void setFirstEntity(EntityWithIgnoredMethods firstEntity) {
		this.firstEntity = firstEntity;
	}
	
	@ManyToOne
	public EntityWithIgnoredMethods getSecondEntity() {
		return secondEntity;
	}
	
	public void setSecondEntity(EntityWithIgnoredMethods secondEntity) {
		this.secondEntity = secondEntity;
	}
	
	@OneToOne(mappedBy = "thirdEntity")
	public EntityWithIgnoredMethods getThirdEntity() {
		return thirdEntity;
	}
	
	public void setThirdEntity(EntityWithIgnoredMethods thirdEntity) {
		this.thirdEntity = thirdEntity;
	}

}
