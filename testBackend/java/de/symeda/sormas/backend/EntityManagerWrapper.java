package de.symeda.sormas.backend;

import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@LocalBean
@PermitAll
public class EntityManagerWrapper {

	@PersistenceContext(unitName = "beanTestPU")
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}
}
