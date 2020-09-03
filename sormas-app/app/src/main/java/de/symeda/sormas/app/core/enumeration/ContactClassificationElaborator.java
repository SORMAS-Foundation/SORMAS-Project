/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.core.enumeration;

import android.content.Context;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.NotImplementedException;

public class ContactClassificationElaborator implements StatusElaborator {

	private ContactClassification status = null;

	public ContactClassificationElaborator(ContactClassification status) {
		this.status = status;
	}

	@Override
	public String getFriendlyName(Context context) {
		if (status != null) {
			return status.toShortString();
		}
		return "";
	}

	@Override
	public int getColorIndicatorResource() {
		if (status == ContactClassification.UNCONFIRMED) {
			return R.color.indicatorPossibleContact;
		} else if (status == ContactClassification.CONFIRMED) {
			return R.color.indicatorConfirmedContact;
		} else if (status == ContactClassification.NO_CONTACT) {
			return R.color.indicatorNoContact;
		}
		return R.color.noColor;
	}

	@Override
	public Enum getValue() {
		return this.status;
	}

	@Override
	public int getIconResourceId() {
		throw new NotImplementedException("getIconResourceId");
	}
}
