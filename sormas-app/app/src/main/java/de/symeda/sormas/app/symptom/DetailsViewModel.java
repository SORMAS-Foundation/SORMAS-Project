package de.symeda.sormas.app.symptom;

/**
 * Created by Orson on 19/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class DetailsViewModel implements ISymptomViewModel {

    private String mDetail;
    private boolean error;

    private OnDetailsViewModelErrorStateChanged onDetailsViewModelErrorStateChanged;

    public void setOnDetailsViewModelErrorStateChanged(OnDetailsViewModelErrorStateChanged onDetailsViewModelErrorStateChanged) {
        this.onDetailsViewModelErrorStateChanged = onDetailsViewModelErrorStateChanged;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;

        if (onDetailsViewModelErrorStateChanged != null)
            onDetailsViewModelErrorStateChanged.onChanged(this, error, null);
    }

    public void setError(boolean error, int errorMessageResId) {
        this.error = error;

        if (onDetailsViewModelErrorStateChanged != null)
            onDetailsViewModelErrorStateChanged.onChanged(this, error, errorMessageResId);
    }

    public DetailsViewModel() {
        this.mDetail = "";
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String detail) {
        this.mDetail = detail;
    }
}
