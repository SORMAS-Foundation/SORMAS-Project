/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.shareinfo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import de.symeda.sormas.backend.contact.Contact;

@Entity
@DiscriminatorValue("CONTACT")
public class ShareInfoContact extends ShareInfoEntity {

	private static final long serialVersionUID = -4405499048071738561L;

	public static final String CONTACT = "contact";

	private Contact contact;

	public ShareInfoContact() {
	}

	public ShareInfoContact(SormasToSormasShareInfo shareInfo, Contact contact) {
		super(shareInfo);
		this.contact = contact;
	}

	@ManyToOne
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}
}
