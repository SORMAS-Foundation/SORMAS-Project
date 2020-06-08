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

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.transaction.TransactionScoped;

import de.symeda.auditlog.api.Auditor;
import de.symeda.auditlog.api.Current;
import de.symeda.auditlog.api.TransactionId;
import de.symeda.auditlog.api.UserId;

/**
 * Provides the relevant environment for the Auditlog.
 * 
 * @author Oliver Milke
 * @since 13.01.2016
 */
@Stateless
public class AuditContextProducer {

	@Resource
	private SessionContext context;

	/**
	 * Returns the information which user a change should be associated with according to the EJB context.
	 */
	@Produces
	@Current
	@Dependent
	public UserId getCurrentlyLoggedInUser() {

		final String name = context.getCallerPrincipal() == null ? "SYSTEM" : context.getCallerPrincipal().getName();
		return new UserId(name);
	}

	@Produces
	@Current
	@TransactionScoped
	public Auditor provideAuditor() {

		return new Auditor();
	}

	@Produces
	@Current
	@TransactionScoped
	public TransactionId provideTransactionId() {

		return new TransactionId();
	}
}
