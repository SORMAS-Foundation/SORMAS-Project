package de.symeda.sormas.app.symptom;

/**
 * Created by Orson on 22/05/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public interface OnDetailsViewModelErrorStateChanged {
    void onChanged(DetailsViewModel viewModel, boolean errorState, Integer errorMessageResId);
}
