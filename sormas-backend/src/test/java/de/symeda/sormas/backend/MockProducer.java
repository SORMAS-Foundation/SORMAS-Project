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
	private static Properties properties = mock(Properties.class);
	private static UserTransaction userTransaction = mock(UserTransaction.class);

	// Receiving e-mail server is mocked: org. jvnet. mock_javamail. mailbox
	private static Session mailSession;
	static {
		Properties props = new Properties();
		props.setProperty("country.name","nigeria");

		// Make sure that the default session does not use a local mail server (if mock-javamail is removed)
		mailSession = Session.getInstance(props);
	}

	static {
		wireMocks();
	}

	public static void resetMocks() {

		reset(sessionContext, principal, topic, connectionFactory, timerService, properties, userTransaction);
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

}
