package de.symeda.sormas.app.core.enumeration;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.app.R;

public class SampleTestResultTypeElaborator implements IStatusElaborator {

    private SampleTestResultType resultType = null;

    public SampleTestResultTypeElaborator(SampleTestResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public String getFriendlyName() {
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

    public Drawable getDrawable(Context context) {
        switch (resultType) {
            case POSITIVE:
                return ContextCompat.getDrawable(context, R.drawable.ic_add_24dp).mutate();
            case NEGATIVE:
                return ContextCompat.getDrawable(context, R.drawable.ic_remove_24dp).mutate();
            case PENDING:
                return ContextCompat.getDrawable(context, R.drawable.ic_pending_24dp).mutate();
            case INDETERMINATE:
                return ContextCompat.getDrawable(context, R.drawable.ic_do_not_disturb_on_24dp).mutate();
            default:
                throw new IllegalArgumentException(resultType.toString());
        }
    }

    @Override
    public String getStatekey() {
        return ARG_SAMPLE_TEST_RESULT_TYPE;
    }

    @Override
    public Enum getValue() {
        return this.resultType;
    }

}
