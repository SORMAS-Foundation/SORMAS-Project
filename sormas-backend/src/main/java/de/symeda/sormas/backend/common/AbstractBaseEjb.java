package de.symeda.sormas.backend.common;

public abstract class AbstractBaseEjb<ADO extends AbstractDomainObject, SRV extends AdoServiceWithUserFilter<ADO>> {

	protected SRV service;

	protected AbstractBaseEjb() {
	}

	protected AbstractBaseEjb(SRV service) {
		this.service = service;
	}

	// todo cannot be filled right now as we are missing ArchivableAbstractDomainObject
	// with this abstract class e.g., ImmunizationFacadeEjb could be wired up to this as well
	public abstract void archive(String uuid);

	public abstract void dearchive(String uuid);

	// FIXME(@JonasCir) #6821: Add missing functions like save, getByUuid etc
}
