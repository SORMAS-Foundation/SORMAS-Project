package de.symeda.sormas.app.core.enumeration;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.R;

public class InvestigationStatusElaborator implements IStatusElaborator {

    private InvestigationStatus status = null;

    public InvestigationStatusElaborator(InvestigationStatus status) {
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
        if (status == InvestigationStatus.PENDING) {
            return R.color.indicatorInvestigationPending;
        } else if (status == InvestigationStatus.DONE) {
            return R.color.indicatorInvestigationDone;
        } else if (status == InvestigationStatus.DISCARDED) {
            return R.color.indicatorInvestigationDiscarded;
        }

        return R.color.noColor;
    }

    @Override
    public String getStatekey() {
        return ARG_INVESTIGATION_STATUS;
    }

    @Override
    public Enum getValue() {
        return this.status;
    }
}
