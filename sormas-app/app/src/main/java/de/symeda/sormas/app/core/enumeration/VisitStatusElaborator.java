package de.symeda.sormas.app.core.enumeration;

import android.content.Context;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.NotImplementedException;

public class VisitStatusElaborator implements StatusElaborator {

    private VisitStatus status = null;

    public VisitStatusElaborator(VisitStatus status) {
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
        if (status == VisitStatus.UNAVAILABLE) {
            return R.color.indicatorVisitUnavailable;
        } else if (status == VisitStatus.UNCOOPERATIVE) {
            return R.color.indicatorVisitUncoperative;
        } else if (status == VisitStatus.COOPERATIVE) {
            return R.color.indicatorVisitCooperative;
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
