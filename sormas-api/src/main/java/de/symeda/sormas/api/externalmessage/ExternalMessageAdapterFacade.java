package de.symeda.sormas.api.externalmessage;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface ExternalMessageAdapterFacade {

	/**
	 * 
	 * @param since
	 * @return Messages that are new or have changed since
	 */
	ExternalMessageResult<List<ExternalMessageDto>> getExternalMessages(Date since);

	/**
	 * 
	 * @param message
	 *            message to be converted
	 * @return An ExternalMessageResult with a String that is a detailed HTML representation of the original notification
	 *         from the external message server. This HTML must not exceed a width of 550px.
	 */
	ExternalMessageResult<String> convertToHTML(ExternalMessageDto message);

	/**
	 *
	 * @param message
	 *            lab message to be converted
	 * @return An ExternalMessageResult with a byte array that is a detailed PDF representation of the original notification
	 *         * from the external lab message server
	 */
	ExternalMessageResult<byte[]> convertToPDF(ExternalMessageDto message);

	String getVersion();
}
