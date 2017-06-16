package de.symeda.sormas.app;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.component.YesNoUnknownField;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

/**
 * Created by Mate Strysewske on 15.06.2017.
 */

public class YesNoUnknownFieldViewAction implements ViewAction {

    private final YesNoUnknown value;

    public YesNoUnknownFieldViewAction(YesNoUnknown value) {
        this.value = value;
    }

    @Override
    public Matcher<View> getConstraints() {
        return isAssignableFrom(YesNoUnknownField.class);
    }

    @Override
    public String getDescription() {
        return "YesNoUnknown field";
    }

    @Override
    public void perform(UiController uiController, View view) {
        YesNoUnknownField yesNoUnknownField = (YesNoUnknownField) view;
        yesNoUnknownField.setValue(value);
    }

}
