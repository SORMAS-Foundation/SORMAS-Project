package de.symeda.sormas.app.core.enumeration;

import android.content.res.Resources;

import de.symeda.sormas.app.R;

import de.symeda.sormas.api.caze.InvestigationStatus;

/**
 * Created by Orson on 25/12/2017.
 */
public class InvestigationStatusElaborator implements IStatusElaborator {

    private Resources resources = null;
    private InvestigationStatus status = null;

    public InvestigationStatusElaborator(InvestigationStatus status, Resources resources) {
        this.status = status;
        this.resources = resources;
    }

    @Override
    public String getFriendlyName() {
        if (status == InvestigationStatus.PENDING) {
            return resources.getString(R.string.status_investigation_pending);
        } else if (status == InvestigationStatus.DONE) {
            return resources.getString(R.string.status_investigation_done);
        } else if (status == InvestigationStatus.DISCARDED) {
            return resources.getString(R.string.status_investigation_discarded);
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
