package de.symeda.sormas.app.core.enumeration;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.shared.ShipmentStatus;

public class ShipmentStatusElaborator implements IStatusElaborator {

    private ShipmentStatus status = null;

    public ShipmentStatusElaborator(ShipmentStatus status) {
        this.status = status;
    }

    @Override
    public String getFriendlyName() {
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
    public String getStatekey() {
        return ARG_SHIPMENT_STATUS;
    }

    @Override
    public Enum getValue() {
        return this.status;
    }
}
