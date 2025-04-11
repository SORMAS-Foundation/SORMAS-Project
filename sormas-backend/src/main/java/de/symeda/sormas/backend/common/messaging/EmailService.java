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
package de.symeda.sormas.backend.common.messaging;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import fr.opensagres.xdocreport.document.json.JSONObject;

@Stateless(name = "EmailService")
@LocalBean
public class EmailService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "mail/MailSession")
	private Session mailSession;
	// For LUX basic authentication. // TODO Need to refactor
	private static boolean isBasicAuth = false;
	// OAuth2 with SMTP server. - with this getting the authentication unsuccessful issue
	private static boolean isOAuthSmtp = true;
	// OAUTH2 with Graph API - With this email functionality is working
	private static boolean isOAuthGraph = false;
	// graph API url for sending the email
	private final static String graphUrl = "https://graph.microsoft.com/v1.0/users/<userid>/sendMail";

	// For generating the OAuth2 token, tenant, client & secrets are required.
	private static final String TENANT_ID = "<tenantid>";
	private static final String CLIENT_ID = "<clientid>";
	private static final String CLIENT_SECRET = "<clientsecret>";
	// Scope for getting the token
	private static final String GRAPH_SCOPE = "https://graph.microsoft.com/.default";
	private static final String SMTP_SCOPE = "https://outlook.office365.com/.default";

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@Asynchronous
	public void sendEmailAsync(String recipient, String subject, String content) throws MessagingException {

		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		sendEmailAsync(recipient, subject, content, "text/plain; charset=utf-8");
	}

	public void sendEmail(String recipient, String subject, String content, Map<File, String> attachments) throws MessagingException {

		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		Multipart multipart = new MimeMultipart();
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(content, "text/plain; charset=utf-8");

		multipart.addBodyPart(messageBodyPart);

		for (Map.Entry<File, String> attachment : attachments.entrySet()) {
			File file = attachment.getKey();
			String fileName = attachment.getValue();

			MimeBodyPart attachPart = new MimeBodyPart();
			attachPart.setDataHandler(new DataHandler(new FileDataSource(file)));
			attachPart.setFileName(fileName);
			attachPart.setDisposition(Part.ATTACHMENT);

			multipart.addBodyPart(attachPart);
		}
		if (isBasicAuth) {
			sendEmailAsync(recipient, subject, multipart, null);
		} else {
			sendTestEmailAsync(recipient, subject, content, attachments);
		}
	}

	private void sendEmailAsync(String recipient, String subject, Object content, String contentType) throws MessagingException {
		MimeMessage message = new MimeMessage(mailSession);

		String senderAddress = configFacade.getEmailSenderAddress();
		String senderName = configFacade.getEmailSenderName();

		try {
			InternetAddress fromAddress = new InternetAddress(senderAddress, senderName);
			message.setFrom(fromAddress);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}

		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
		message.setSubject(subject, "UTF-8");
		message.setContent(content, contentType);

		Transport.send(message);
		logger.info("Mail sent to {}.", recipient);
	}

	private void sendTestEmailAsync(String recipient, String subject, Object content, Map<File, String> attachments) throws MessagingException {

		try {
			String accessToken;
			if (isOAuthSmtp) {
				accessToken = getAcessToken(SMTP_SCOPE);
				sendEmailAsyncWithOAuth(accessToken);
			}
			if (isOAuthGraph) {
				accessToken = getAcessToken(GRAPH_SCOPE);
				sendEmailAsyncWithApi(recipient, subject, accessToken, content, attachments);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}

	}

	private void sendEmailAsyncWithApi(String recipient, String subject, String accessToken, Object content, Map<File, String> attachments)
		throws IOException {
		//TODO Need to refactor
		Properties props = new Properties();
		props.put("mail.smtp.host", "<hostname>");
		props.put("mail.smtp.port", "<port>");
		props.put("mail.debug", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.auth.mechanisms", "XOAUTH2");

		URL url = new URL(graphUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", "Bearer " + accessToken);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);

		List<Map<String, String>> attachmentList = attachments.entrySet()
			.stream()
			.map(

				entry -> {
					try {
						return Map.of(
							"@odata.type",
							"#microsoft.graph.fileAttachment",
							"name",
							entry.getKey().getName(),  // Get file name
							"contentType",
							"application/pdf",
							"contentBytes",
							Base64.getEncoder().encodeToString(Files.readAllBytes(entry.getKey().toPath()))  // Base64 file content
						);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				})
			.collect(Collectors.toList());

		Map<String, Object> emailMap =
			Map.of("saveToSentItems", Boolean.TRUE, "message", Map.of("subject", subject, "body", Map.of("contentType", "Text", "content", content //body
			), "toRecipients", List.of(Map.of("emailAddress", Map.of("address", recipient))), "attachments", attachmentList));

		// Convert Java object to JSON string
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonMessage = objectMapper.writeValueAsString(emailMap);

		// Send the JSON message in the request body
		try (OutputStream os = connection.getOutputStream()) {
			byte[] input = jsonMessage.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		// Get the response code to check if the request was successful
		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_ACCEPTED) {
			logger.info("Email sent successfully!");
		} else {
			logger.info("Failed to send email. Response Code: " + responseCode);
			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
				String inputLine;
				StringBuilder response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				logger.error("Error response: " + response.toString());
				throw new RuntimeException(response.toString());
			}
		}
	}

	private void sendEmailAsyncWithOAuth(String token) throws IOException, MessagingException {
		//TODO Need to refactor
		Properties props = new Properties();
		props.put("mail.debug.auth", "true");
		props.put("mail.smtp.host", "hostname");
		props.put("mail.smtp.port", "port");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.auth.mechanisms", "XOAUTH2");

//		Session session = Session.getInstance(props, new Authenticator() {
//
//			@Override
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication("username@sormas.org", "<password>");
//			}
//		});

		Session session = Session.getInstance(props);

		Transport transport = session.getTransport("smtp");
		transport.connect("smtp.office365.com", "username@sormas.org", token);

		session.setDebug(true);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("username@sormas.org"));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("recipient@sormas.org"));
		message.setSubject("Test Email");
		message.setText("Hello, this is a test email!");

		transport.send(message);
	}

	private String getAcessToken(String scope) throws IOException {
		String url = "https://login.microsoftonline.com/" + TENANT_ID + "/oauth2/v2.0/token";

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		String body = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&scope=" + scope + "&grant_type=client_credentials";
		post.setEntity(new StringEntity(body));

		try (CloseableHttpResponse response = client.execute(post)) {
			String responseBody = EntityUtils.toString(response.getEntity());
			JSONObject json = new JSONObject(responseBody);
			return json.getString("access_token");
		}
	}
}
