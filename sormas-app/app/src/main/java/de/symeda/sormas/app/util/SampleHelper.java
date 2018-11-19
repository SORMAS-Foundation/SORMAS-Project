/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.util;

import android.databinding.InverseMethod;

import de.symeda.sormas.app.core.YesNo;

import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.sample.ShipmentStatus;

public class SampleHelper {

    public static ShipmentStatus getShipmentStatus(Sample record) {
        if (record.getReferredToUuid() != null) {
            return ShipmentStatus.REFERRED_OTHER_LAB;
        } else if (record.isReceived()) {
            return ShipmentStatus.RECEIVED;
        } else if (record.isShipped()) {
            return ShipmentStatus.SHIPPED;
        } else {
            return ShipmentStatus.NOT_SHIPPED;
        }
    }

    @InverseMethod("toShippedInBoolean")
    public static YesNo isShipped(boolean answer) {
        return answer ? YesNo.YES : YesNo.NO;
    }

    public static boolean toShippedInBoolean(Object answer) {
        YesNo result = ((YesNo)answer);

        if (result == null)
            return false;

        return result == YesNo.YES ? true : false;
    }

}
