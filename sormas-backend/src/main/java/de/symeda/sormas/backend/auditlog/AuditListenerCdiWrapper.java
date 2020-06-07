/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
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
