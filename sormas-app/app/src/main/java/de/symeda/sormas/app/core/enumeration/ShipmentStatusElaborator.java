package de.symeda.sormas.app.core.enumeration;

import android.content.res.Resources;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.shared.ShipmentStatus;

public class ShipmentStatusElaborator implements IStatusElaborator {

    private Resources resources = null;
    private ShipmentStatus status = null;

    public ShipmentStatusElaborator(ShipmentStatus status, Resources resources) {
        this.status = status;
        this.resources = resources;
    }

    @Override
    public String getFriendlyName() {
        if (status == ShipmentStatus.NOT_SHIPPED) {
            return resources.getString(R.string.status_shipment_not_shipped);
        } else if (status == ShipmentStatus.SHIPPED) {
            return resources.getString(R.string.status_shipment_shipped);
        } else if (status == ShipmentStatus.RECEIVED) {
            return resources.getString(R.string.status_shipment_received);
        } else if (status == ShipmentStatus.REFERRED_OTHER_LAB) {
            return resources.getString(R.string.status_shipment_referred_other_lab);
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
