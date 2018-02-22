package de.symeda.sormas.backend.common;

import java.io.IOException;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.insight.CarrierDetails.NetworkType;
import com.nexmo.client.insight.InsightClient;
import com.nexmo.client.insight.standard.StandardInsightResponse;
import com.nexmo.client.sms.SmsSubmissionResult;
import com.nexmo.client.sms.messages.TextMessage;

import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;

@Stateless(name = "SmsService")
@LocalBean
public class SmsService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@EJB
	ConfigFacadeEjbLocal configFacade;

	@Asynchronous
	public void sendSms(String phoneNumber, String subject, String content) throws IOException, NexmoClientException, InvalidPhoneNumberException {
		// Remove the initial + that indicates the beginning of the country code to match the Nexmo specification of allowed number formats
		if (phoneNumber.startsWith("+")) {
			phoneNumber = phoneNumber.substring(1);
		}
		
		AuthMethod auth = new TokenAuthMethod(configFacade.getSmsAuthKey(), configFacade.getSmsAuthSecret());
		NexmoClient client = new NexmoClient(auth);

		// If the phone number is invalid, e.g. because it is a landline number or malformed otherwise, throw an exception
		InsightClient insightClient = client.getInsightClient();
		StandardInsightResponse insightResponse = insightClient.getStandardNumberInsight(phoneNumber);
		if (insightResponse.getStatus() != 0 || insightResponse.getCurrentCarrier().getNetworkType() != NetworkType.MOBILE) {
			throw new InvalidPhoneNumberException("Cannot send an SMS to the specified phone number" , null);
		}

		SmsSubmissionResult[] results = client.getSmsClient().submitMessage(new TextMessage(
				"SORMAS",
				phoneNumber,
				content));
		
		for (SmsSubmissionResult result : results) {
			if (result.getStatus() == 0) {
				logger.info("SMS successfully sent to {}.", phoneNumber);
			} else  if (result.getErrorText() != null) {
				logger.info("Error sending SMS to {} with following error: {}.", phoneNumber, result.getErrorText());
			}
		}
		
	}

}
