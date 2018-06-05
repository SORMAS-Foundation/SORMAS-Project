package de.symeda.sormas.app.core.enumeration;

import android.content.res.Resources;

import de.symeda.sormas.app.R;

import de.symeda.sormas.api.visit.VisitStatus;

/**
 * Created by Orson on 02/01/2018.
 */

public class VisitStatusElaborator implements IStatusElaborator {

    private Resources resources = null;
    private VisitStatus status = null;

    public VisitStatusElaborator(VisitStatus status, Resources resources) {
        this.status = status;
        this.resources = resources;
    }

    @Override
    public String getFriendlyName() {
        if (status == VisitStatus.UNAVAILABLE) {
            return resources.getString(R.string.status_visit_unavailable);
        } else if (status == VisitStatus.UNCOOPERATIVE) {
            return resources.getString(R.string.status_visit_uncooperative);
        } else if (status == VisitStatus.COOPERATIVE) {
            return resources.getString(R.string.status_visit_cooperative);
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
