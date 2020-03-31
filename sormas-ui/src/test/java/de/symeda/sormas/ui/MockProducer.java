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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package de.symeda.sormas.ui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Properties;

import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.enterprise.inject.Produces;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.mail.Session;
import javax.transaction.UserTransaction;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

/**
 * Creates mocks for resources needed in bean test / external services. <br />
 * Use {@link Mailbox#get (String)} to retrieve e-mails sent (receiver address passed).
 * 
 * @author Stefan Kock
 */
public class MockProducer {

	private static SessionContext sessionContext = mock(SessionContext.class);
	private static Principal principal = mock(Principal.class);
	private static Topic topic = mock(Topic.class);
	private static ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
	private static TimerService timerService = mock(TimerService.class);
	private static Properties properties = new Properties();
	private static UserTransaction userTransaction = mock(UserTransaction.class);
	
	private static FacadeProvider facadeProvider = new FacadeProviderMock();

	// Receiving e-mail server is mocked: org. jvnet. mock_javamail. mailbox
	private static Session mailSession;
	static {
		properties.setProperty(ConfigFacadeEjb.COUNTRY_NAME,"nigeria");
		properties.setProperty(ConfigFacadeEjb.CSV_SEPARATOR, ",");
		
		try {
			Field instance = InfoProvider.class.getDeclaredField("instance");
			instance.setAccessible(true);
			instance.set(null, spy(InfoProvider.class));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	    try {
	        Field instance = FacadeProvider.class.getDeclaredField("instance");
	        instance.setAccessible(true);
	        instance.set(instance, facadeProvider);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }

		// Make sure that the default session does not use a local mail server (if mock-javamail is removed)
		mailSession = Session.getInstance(properties);
	}

	static {
		wireMocks();
	}

	public static void resetMocks() {

		reset(sessionContext, principal, topic, connectionFactory, timerService, userTransaction);
		wireMocks();
	}

	public static void wireMocks() {

		when(sessionContext.getCallerPrincipal()).thenReturn(principal);
	}

	@Produces
	public static SessionContext getSessionContext() {
		return sessionContext;
	}

	@Produces
	public static Topic getTopic() {
		return topic;
	}

	@Produces
	public static ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	@Produces
	public static TimerService getTimerService() {
		return timerService;
	}

	@Produces
	public static Session getMailSession() {
		return mailSession;
	}

	@Produces
	public static Properties getProperties() {
		return properties;
	}

	@Produces
	public static UserTransaction getUserTransaction() {
		return userTransaction;
	}

	@Produces
	public static Principal getPrincipal() {
		return principal;
	}
}
