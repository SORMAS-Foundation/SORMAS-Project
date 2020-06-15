package de.symeda.sormas.backend.common;

public abstract class AbstractCoreAdoService<ADO extends CoreAdo> extends AbstractAdoService<ADO> {

	public AbstractCoreAdoService(Class<ADO> elementClass) {
		super(elementClass);
	}

	@Override
	public void delete(ADO deleteme) {

		deleteme.setDeleted(true);
		em.persist(deleteme);
		em.flush();
	}
}
