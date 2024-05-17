/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.api.docgeneneration;

import java.io.Serializable;

import de.symeda.sormas.api.document.DocumentDto;

public class EmailAttachementDto implements Serializable {

	private static final long serialVersionUID = -6376903475087381451L;

	private final DocumentDto document;

	private final byte[] content;

	public EmailAttachementDto(DocumentDto document, byte[] content) {
		this.document = document;
		this.content = content;
	}

	public DocumentDto getDocument() {
		return document;
	}

	public byte[] getContent() {
		return content;
	}
}
