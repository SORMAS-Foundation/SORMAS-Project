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

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.sample.ShipmentStatus;

public class ShipmentStatusElaborator implements StatusElaborator {

	private ShipmentStatus status = null;

	public ShipmentStatusElaborator(ShipmentStatus status) {
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
		if (status == ShipmentStatus.NOT_SHIPPED) {
			return R.color.indicatorShipmentNotShipped;
		} else if (status == ShipmentStatus.SHIPPED) {
			return R.color.indicatorShipmentShipped;
		} else if (status == ShipmentStatus.RECEIVED) {
			return R.color.indicatorShipmentReceived;
		} else if (status == ShipmentStatus.REFERRED_OTHER_LAB) {
			return R.color.indicatorShipmentReferred;
		}

		return R.color.noColor;
	}

	@Override
	public Enum getValue() {
		return this.status;
	}

	@Override
	public int getIconResourceId() {
		switch (status) {
		case NOT_SHIPPED:
			return R.drawable.ic_lp_not_shipped_192dp;
		case SHIPPED:
			return R.drawable.ic_lp_shipped_192dp;
		case RECEIVED:
			return R.drawable.ic_lp_received_192dp;
		case REFERRED_OTHER_LAB:
			return R.drawable.ic_lp_referred_to_other_lab_192dp;
		default:
			throw new IllegalArgumentException(status.toString());
		}
	}
}
