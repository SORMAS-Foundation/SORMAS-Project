package de.symeda.sormas.app.core.enumeration;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.app.R;

public class CaseClassificationElaborator implements IStatusElaborator {

    private CaseClassification status = null;

    public CaseClassificationElaborator(CaseClassification status) {
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
