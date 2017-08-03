package de.symeda.sormas.backend;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Properties;

import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.enterprise.inject.Produces;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.mail.Session;
import javax.transaction.UserTransaction;

/**
 * Erzeugt Mocks für in Bean-Test benötigte Ressourcen / externe Services.<br />
 * Zum Abrufen von versendeten E-Mails {@link Mailbox#get(String)} verwenden (Empfänger-Adresse übergeben).
 * 
 * @author Stefan Kock
 */
public class MockProducer {

	private static SessionContext sessionContext = mock(SessionContext.class);
	private static Principal principal = mock(Principal.class);
	private static Topic topic = mock(Topic.class);
	private static ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
	private static TimerService timerService = mock(TimerService.class);
	private static Properties properties = mock(Properties.class);
	private static UserTransaction userTransaction = mock(UserTransaction.class);

	// Empfangender E-Mail-Server ist gemockt: org.jvnet.mock_javamail.Mailbox
	private static Session mailSession;
	static {
		// Sicherstellen, dass die default-Session nicht einen lokalen Mailserver nutzt (falls mock-javamail entfernt wird)
		Properties props = new Properties();
		props.setProperty("mail.host", "non@existent");
		mailSession = Session.getInstance(props);
	}

	static {
		wireMocks();
	}

	public static void resetMocks() {

		reset(sessionContext, principal, topic, connectionFactory, timerService, properties, userTransaction);
		wireMocks();
	}

	/**
	 * Verknüpft kaskadierte Mocks untereinander.
	 */
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

}
