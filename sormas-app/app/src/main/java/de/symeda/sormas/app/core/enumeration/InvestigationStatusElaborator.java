package de.symeda.sormas.app.core.enumeration;

import android.content.Context;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.R;

public class InvestigationStatusElaborator implements StatusElaborator {

    private InvestigationStatus status = null;

    public InvestigationStatusElaborator(InvestigationStatus status) {
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
    public Enum getValue() {
        return this.status;
    }

    @Override
    public int getIconResourceId() {
        switch (status) {
            case PENDING:
                return R.drawable.ic_lp_investigation_pending_192dp;
            case DONE:
                return R.drawable.ic_lp_investigation_done_192dp;
            case DISCARDED:
                return R.drawable.ic_lp_investigation_discarded_192dp;
            default:
                throw new IllegalArgumentException(status.toString());
        }
    }
}
