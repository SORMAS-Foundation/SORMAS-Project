package de.symeda.sormas.app.core.enumeration;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.R;

public class VisitStatusElaborator implements IStatusElaborator {

    private VisitStatus status = null;

    public VisitStatusElaborator(VisitStatus status) {
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
    public String getStatekey() {
        return ARG_VISIT_STATUS;
    }

    @Override
    public Enum getValue() {
        return this.status;
    }
}
