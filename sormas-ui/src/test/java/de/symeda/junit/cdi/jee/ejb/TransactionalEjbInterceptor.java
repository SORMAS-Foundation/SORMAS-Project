/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ---------------------------------------------------------------------
 * Based on Bean Testing: https://github.com/NovatecConsulting/BeanTest
 * Licensed under the Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */

package de.symeda.junit.cdi.jee.ejb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.QueryTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transactional interceptor to provide basic transaction propagation.
 * <p>
 * <b>Note</b> This implementation is intentionally not thread-safe, because unit tests are usually run in one thread. <br>
 * If you try to run unit tests in parallel, unexpected behavior may occur.
 */
@Priority(Interceptor.Priority.PLATFORM_BEFORE)
@TransactionalEjb
@Interceptor
public class TransactionalEjbInterceptor {

	/**
	 * Exceptions that should not cause the transaction to rollback according to Java EE Documentation.
	 * (http://docs.oracle.com/javaee/6/api/javax/persistence/PersistenceException.html)
	 */
	private static final Set<Class<?>> NO_ROLLBACK_EXCEPTIONS =
		new HashSet(Arrays.asList(NonUniqueResultException.class, NoResultException.class, QueryTimeoutException.class, LockTimeoutException.class));

	@Inject
	@PersistenceContext
	EntityManager em;

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalEjbInterceptor.class);
	private static int INTERCEPTOR_COUNTER = 0;

	public TransactionalEjbInterceptor() {
	}

	@AroundInvoke
	public Object manageTransaction(InvocationContext ctx) throws Exception {

		EntityTransaction transaction = this.em.getTransaction();
		if (!transaction.isActive()) {
			transaction.begin();
			LOGGER.debug("Transaction started");
		}

		++INTERCEPTOR_COUNTER;
		Object result = null;

		try {
			result = ctx.proceed();
		} catch (Exception e) {
			if (isFirstInterceptor()) {
				this.markRollbackTransaction(e);
			}

			throw e;
		} finally {
			this.processTransaction();
		}

		return result;
	}

	/**
	 * Commits the current transaction if it is not already marked as rollback via the {@link EntityTransaction#getRollbackOnly()} method.
	 * In that case, a rollback will be executed.
	 */
	private void processTransaction() throws Exception {
		EntityTransaction transaction = this.em.getTransaction();

		try {
			if (this.em.isOpen() && transaction.isActive() && isFirstInterceptor()) {
				if (transaction.getRollbackOnly()) {
					transaction.rollback();
					LOGGER.debug("Transaction was rolled back");
				} else {
					transaction.commit();
					LOGGER.debug("Transaction committed");
				}

				this.em.clear();
			}
		} catch (Exception e) {
			LOGGER.warn("Error when trying to commit transaction: {0}", e);
			throw e;
		} finally {
			--INTERCEPTOR_COUNTER;
		}
	}

	/**
	 * Marks the transaction for rollback via {@link EntityTransaction#setRollbackOnly()}.
	 */
	private void markRollbackTransaction(Exception exception) throws Exception {
		try {
			if (this.em.isOpen() && this.em.getTransaction().isActive() && shouldExceptionCauseRollback(exception)) {
				this.em.getTransaction().setRollbackOnly();
			}

		} catch (Exception var3) {
			LOGGER.warn("Error when trying to roll back the transaction: {0}", var3);
			throw var3;
		}
	}

	private static boolean isFirstInterceptor() {
		return INTERCEPTOR_COUNTER - 1 == 0;
	}

	private static boolean shouldExceptionCauseRollback(Exception e) {
		return !NO_ROLLBACK_EXCEPTIONS.contains(e.getClass());
	}
}
