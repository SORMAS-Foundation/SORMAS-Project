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
package de.symeda.sormas.backend;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.File;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Hashtable;
import java.util.Properties;

import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.transaction.UserTransaction;

import de.symeda.sormas.api.RequestContextHolder;
import de.symeda.sormas.api.RequestContextTO;
import de.symeda.sormas.api.customizableenum.CustomizableEnumFacade;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.backend.central.EtcdCentralClient;
import de.symeda.sormas.backend.central.EtcdCentralClientProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasDiscoveryServiceProducer;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.access.SormasToSormasDiscoveryService;
import de.symeda.sormas.backend.sormastosormas.crypto.SormasToSormasEncryptionFacadeEjb.SormasToSormasEncryptionFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.rest.SormasToSormasRestClient;
import de.symeda.sormas.backend.sormastosormas.rest.SormasToSormasRestClientProducer;

/**
 * Creates mocks for resources needed in bean test / external services.
 * 
 * @author Stefan Kock
 */
public class MockProducer implements InitialContextFactory {

	public static final String TMP_PATH = "target/tmp";

	private static InitialContext initialContext = mock(InitialContext.class);
	private static SessionContext sessionContext = mock(SessionContext.class, withSettings().lenient());
	private static Principal principal = mock(Principal.class, withSettings().lenient());
	private static Topic topic = mock(Topic.class);
	private static ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
	private static TimerService timerService = mock(TimerService.class);
	private static Properties properties = new Properties();
	private static UserTransaction userTransaction = mock(UserTransaction.class);
	private static RequestContextTO requestContextTO = new RequestContextTO(false);
	private static SormasToSormasRestClient s2sRestClient = mock(SormasToSormasRestClient.class);
	private static final EtcdCentralClient etcdCentralClient = mock(EtcdCentralClient.class);
	private static CustomizableEnumFacade customizableEnumFacadeForConverter = mock(CustomizableEnumFacade.class);

	private static ManagedScheduledExecutorService managedScheduledExecutorService = mock(ManagedScheduledExecutorService.class);

	private static SormasToSormasDiscoveryService sormasToSormasDiscoveryService = mock(SormasToSormasDiscoveryService.class);

	// Receiving e-mail server is mocked: org. jvnet. mock_javamail. mailbox
	private static Session mailSession;
	static {
		// Make sure that the default session does not use a local mail server
		Properties props = new Properties();
		props.setProperty("mail.host", "non@existent");
		mailSession = Session.getInstance(props);
	}

	static {
		try {
			Field instance = InfoProvider.class.getDeclaredField("instance");
			instance.setAccessible(true);
			instance.set(null, spy(InfoProvider.class));

			File tmpDir = new File(TMP_PATH);
			if (!tmpDir.exists()) {
				tmpDir.mkdir();
			}

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	static {
		wireMocks();
	}

	public static void resetMocks() {

		reset(
			initialContext,
			sessionContext,
			principal,
			topic,
			connectionFactory,
			timerService,
			userTransaction,
			s2sRestClient,
			managedScheduledExecutorService);
		wireMocks();
		resetProperties();
		requestContextTO.setMobileSync(false);
	}

	private static void resetProperties() {

		properties.clear();
		properties.setProperty(ConfigFacadeEjb.COUNTRY_NAME, "nigeria");
		properties.setProperty(ConfigFacadeEjb.CSV_SEPARATOR, ";");
		properties.setProperty(ConfigFacadeEjb.TEMP_FILES_PATH, TMP_PATH);
		properties.setProperty(ConfigFacadeEjb.DOCUMENT_FILES_PATH, TMP_PATH + "/documents");
	}

	public static void wireMocks() {

		when(sessionContext.getCallerPrincipal()).thenReturn(getPrincipal());
		RequestContextHolder.setRequestContext(requestContextTO);
	}

	public static CustomizableEnumFacade getCustomizableEnumFacadeForConverter() {
		return customizableEnumFacadeForConverter;
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

	public static SormasToSormasRestClient getSormasToSormasClient() {
		return s2sRestClient;
	}

	@Specializes
	public static class MockRestClientBuilderProducer extends SormasToSormasRestClientProducer {

		@Produces
		public SormasToSormasRestClient sormasToSormasClient(
			SormasToSormasDiscoveryService sormasToSormasDiscoveryService,
			SormasToSormasEncryptionFacadeEjbLocal sormasToSormasEncryptionEjb,
			ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb) {
			return s2sRestClient;
		}
	}

	public static EtcdCentralClient getEtcdCentralClient() {
		return etcdCentralClient;
	}

	@Specializes
	public static class MockEtcdCentralClientProducer extends EtcdCentralClientProducer {

		@Override
		@Produces
		public EtcdCentralClient etcdCentralClient(ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb) {
			return etcdCentralClient;
		}
	}

	@Override
	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
		when(initialContext.lookup("java:module/CustomizableEnumFacade")).thenReturn(customizableEnumFacadeForConverter);
		return initialContext;
	}

	@Produces
	public static ManagedScheduledExecutorService getManagedScheduledExecutorService() {
		return managedScheduledExecutorService;
	}

	@Specializes
	public static class MockSormasToSormasDiscoveryServiceProducer extends SormasToSormasDiscoveryServiceProducer {

		@Override
		@Produces
		public SormasToSormasDiscoveryService sormasToSormasDiscoveryService(
			SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal sormasToSormasFacadeEjb,
			ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb,
			EtcdCentralClient centralClient) {
			return sormasToSormasDiscoveryService;
		}
	}

	public static SormasToSormasDiscoveryService getSormasToSormasDiscoveryService() {
		return sormasToSormasDiscoveryService;
	}

	public static void mockProperty(String property, String value) {
		properties.setProperty(property, value);
	}

	/**
	 * @param mobileSync
	 *            {@code true} simulates mobile call.
	 */
	public static void setMobileSync(boolean mobileSync) {
		requestContextTO.setMobileSync(mobileSync);
	}
}
