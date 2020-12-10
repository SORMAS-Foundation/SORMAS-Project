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

import java.io.IOException;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.insight.CarrierDetails.NetworkType;
import com.nexmo.client.insight.InsightClient;
import com.nexmo.client.insight.InsightStatus;
import com.nexmo.client.insight.StandardInsightResponse;
import com.nexmo.client.sms.MessageStatus;
import com.nexmo.client.sms.SmsSubmissionResponse;
import com.nexmo.client.sms.SmsSubmissionResponseMessage;
import com.nexmo.client.sms.messages.TextMessage;

import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;

@Stateless(name = "SmsService")
@LocalBean
public class SmsService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@Asynchronous
	public void sendSms(String phoneNumber, String content) throws IOException, NexmoClientException, InvalidPhoneNumberException {

		// Remove the initial + that indicates the beginning of the country code to match the Nexmo specification of allowed number formats
		if (phoneNumber.startsWith("+")) {
			phoneNumber = phoneNumber.substring(1);
		}

		NexmoClient client = NexmoClient.builder().apiKey(configFacade.getSmsAuthKey()).apiSecret(configFacade.getSmsAuthSecret()).build();

		// If the phone number is invalid, e.g. because it is a landline number or malformed otherwise, throw an exception
		InsightClient insightClient = client.getInsightClient();
		StandardInsightResponse insightResponse = insightClient.getStandardNumberInsight(phoneNumber);
		if (insightResponse.getStatus() != InsightStatus.SUCCESS || insightResponse.getCurrentCarrier().getNetworkType() != NetworkType.MOBILE) {
			throw new InvalidPhoneNumberException("Cannot send an SMS to the specified phone number", null);
		}

		SmsSubmissionResponse response =
			client.getSmsClient().submitMessage(new TextMessage(configFacade.getSormasInstanceName(), phoneNumber, content));
		for (SmsSubmissionResponseMessage message : response.getMessages()) {
			if (message.getStatus() == MessageStatus.OK) {
				logger.info("SMS successfully sent to {}.", phoneNumber);
			} else if (message.getErrorText() != null) {
				logger.info(
					"Error sending SMS to {} with following error: status: {}, text: '{}'.",
					phoneNumber,
					message.getStatus(),
					message.getErrorText());
			}
		}
	}
}
