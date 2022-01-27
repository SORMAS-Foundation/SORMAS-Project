package de.symeda.sormas.backend.common;

import java.io.Serializable;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import de.symeda.sormas.backend.user.CurrentUser;
import de.symeda.sormas.backend.user.CurrentUserQualifier;
import de.symeda.sormas.backend.user.User;

class Inner implements Serializable {

	@Inject
	@CurrentUserQualifier
	private CurrentUser currentUser;

	public User getCurrentUser() {
		return currentUser.getUser();
	}
}

public class AbstractDomainObjectListener implements Serializable {

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

	private User getCurrentUser() {

		BeanManager manager = this.getBeanManager();

		Bean<?> bean = manager.resolve(manager.getBeans(Inner.class));
		Inner someBean = (Inner) manager.getReference(bean, bean.getBeanClass(), manager.createCreationalContext(bean));
		return someBean.getCurrentUser();
	}

	@PrePersist
	@PreUpdate
	private void beforeAnyUpdate(AbstractDomainObject ado) {
		User user = getCurrentUser();
		ado.setChangeUser(user);
	}
}
