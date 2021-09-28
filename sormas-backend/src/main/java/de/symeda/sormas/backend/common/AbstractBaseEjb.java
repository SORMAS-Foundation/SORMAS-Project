package de.symeda.sormas.backend.common;

// todo should we use BaseAdoService?
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
}
