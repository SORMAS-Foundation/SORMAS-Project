package de.symeda.sormas.app;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

import de.symeda.sormas.app.component.SpinnerField;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

/**
 * Created by Mate Strysewske on 14.06.2017.
 */
public class SpinnerFieldViewAction implements ViewAction {

    private final Object selectedItem;

    public SpinnerFieldViewAction(Object selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    public Matcher<View> getConstraints() {
        return isAssignableFrom(SpinnerField.class);
    }

    @Override
    public String getDescription() {
        return "Spinner field";
    }

    @Override
    public void perform(UiController uiController, View view) {
        SpinnerField spinnerField = (SpinnerField) view;
        spinnerField.setValue(selectedItem);
    }

}
