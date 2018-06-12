package de.symeda.sormas.app.core.enumeration;

import android.content.res.Resources;

import de.symeda.sormas.app.R;

import de.symeda.sormas.api.caze.CaseClassification;

/**
 * Created by Orson on 06/01/2018.
 */

public class CaseClassificationElaborator implements IStatusElaborator {

    private Resources resources = null;
    private CaseClassification status = null;

    public CaseClassificationElaborator(CaseClassification status, Resources resources) {
        this.status = status;
        this.resources = resources;
    }

    @Override
    public String getFriendlyName() {
        if (status == CaseClassification.NOT_CLASSIFIED) {
            return resources.getString(R.string.status_case_classification_not_classified);
        } else if (status == CaseClassification.SUSPECT) {
            return resources.getString(R.string.status_case_classification_suspected);
        } else if (status == CaseClassification.PROBABLE) {
            return resources.getString(R.string.status_case_classification_probable);
        } else if (status == CaseClassification.CONFIRMED) {
            return resources.getString(R.string.status_case_classification_confirmed);
        } else if (status == CaseClassification.NO_CASE) {
            return resources.getString(R.string.status_case_classification_no_case);
        }

        return "";
    }

    @Override
    public int getColorIndicatorResource() {
        if (status == CaseClassification.NOT_CLASSIFIED) {
            return R.color.indicatorCaseNotYetClassified;
        } else if (status == CaseClassification.SUSPECT) {
            return R.color.indicatorCaseSuspected;
        } else if (status == CaseClassification.PROBABLE) {
            return R.color.indicatorCaseProbable;
        } else if (status == CaseClassification.CONFIRMED) {
            return R.color.indicatorCaseConfirmed;
        } else if (status == CaseClassification.NO_CASE) {
            return R.color.indicatorNotACase;
        }

        return R.color.noColor;
    }

    @Override
    public String getStatekey() {
        return ARG_CASE_CLASSIFICATION_STATUS;
    }

    @Override
    public Enum getValue() {
        return this.status;
    }
}
