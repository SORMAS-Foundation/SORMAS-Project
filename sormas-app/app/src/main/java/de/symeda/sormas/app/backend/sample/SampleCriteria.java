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

package de.symeda.sormas.app.backend.sample;

import java.io.Serializable;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.sample.ShipmentStatus;

public class SampleCriteria implements Serializable {

	private ShipmentStatus shipmentStatus;
	private Case caze;

	public SampleCriteria shipmentStatus(ShipmentStatus shipmentStatus) {
		this.shipmentStatus = shipmentStatus;
		return this;
	}

	public ShipmentStatus getShipmentStatus() {
		return shipmentStatus;
	}

	public SampleCriteria caze(Case caze) {
		this.caze = caze;
		return this;
	}

	public Case getCaze() {
		return caze;
	}
}
