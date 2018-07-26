package de.symeda.sormas.app.util;

import android.databinding.InverseMethod;

import de.symeda.sormas.app.core.YesNo;

import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.shared.ShipmentStatus;

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
