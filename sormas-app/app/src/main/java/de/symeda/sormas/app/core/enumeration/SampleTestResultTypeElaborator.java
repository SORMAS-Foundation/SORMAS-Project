package de.symeda.sormas.app.core.enumeration;

import android.content.Context;

import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.app.R;

public class SampleTestResultTypeElaborator implements StatusElaborator {

    private SampleTestResultType resultType = null;

    public SampleTestResultTypeElaborator(SampleTestResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public String getFriendlyName(Context context) {
        return resultType.toString();
    }

    @Override
    public int getColorIndicatorResource() {
        switch (resultType) {
            case POSITIVE:
                return R.color.samplePositive;
            case NEGATIVE:
                return R.color.sampleNegative;
            case PENDING:
                return R.color.samplePending;
            case INDETERMINATE:
                return R.color.sampleIndeterminate;
            default:
                throw new IllegalArgumentException(resultType.toString());
        }
    }

    @Override
    public Enum getValue() {
        return this.resultType;
    }

    @Override
    public int getIconResourceId() {
        switch (resultType) {
            case POSITIVE:
                return R.drawable.ic_add_24dp;
            case NEGATIVE:
                return R.drawable.ic_remove_24dp;
            case PENDING:
                return R.drawable.ic_pending_24dp;
            case INDETERMINATE:
                return R.drawable.ic_do_not_disturb_on_24dp;
            default:
                throw new IllegalArgumentException(resultType.toString());
        }
    }

}
