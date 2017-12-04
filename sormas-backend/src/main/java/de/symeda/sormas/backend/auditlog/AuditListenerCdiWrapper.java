package de.symeda.sormas.backend.auditlog;

import java.io.Serializable;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import de.symeda.auditlog.api.AuditListener;
import de.symeda.sormas.api.HasUuid;

public class AuditListenerCdiWrapper implements Serializable, AuditListener {

	private static final long serialVersionUID = 1L;

	private BeanManager beanManager;

	private BeanManager getBeanManager() {

		if (beanManager == null) {
			try {
				InitialContext initialContext = new InitialContext();
				beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");
				return beanManager;
			} catch (NamingException e) {
				throw new IllegalStateException("Couldn't get BeanManager through JNDI", e);
			}
		}

		return beanManager;
	}

	private AuditListener getBeanByName() {

		BeanManager beanManager = this.getBeanManager();

		Bean<?> bean = beanManager.resolve(beanManager.getBeans(DefaultAuditListener.class));
		AuditListener someBean = (AuditListener) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));

		return someBean;
	}

	@Override
	@PreUpdate
	@PrePersist
	public void prePersist(HasUuid o) {

		this.getBeanByName().prePersist(o);
	}

	@Override
	@PostLoad
	public void postLoad(HasUuid o) {

		this.getBeanByName().postLoad(o);
	}

	@Override
	@PreRemove
	public void preRemove(HasUuid o) {

		this.getBeanByName().preRemove(o);
	}
}
