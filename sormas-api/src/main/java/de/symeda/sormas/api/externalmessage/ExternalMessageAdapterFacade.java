/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
 */

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
	 * The method should return the HTML source to display a structured/simpler view of the message.
	 * 
	 * @param message
	 *            message to be converted
	 * @return An ExternalMessageResult with a String that is a detailed HTML representation of the original notification
	 *         from the external message server. This HTML must not exceed a width of 550px.
	 */
	ExternalMessageResult<String> convertToHTML(ExternalMessageDto message);

	/**
	 * The method should return the HTML source to display a detailed view of the message.
	 * 
	 * @param message
	 *            message to be converted
	 * @return An ExternalMessageResult with a String that is a full/raw HTML representation of the message,
	 *         Falls back to {@link #convertToHTML(ExternalMessageDto)} by default.
	 */
	default ExternalMessageResult<String> convertToDetailedHTML(ExternalMessageDto message) {
		return convertToHTML(message);
	}

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
