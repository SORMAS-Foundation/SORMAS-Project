package de.symeda.sormas.app;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

import java.util.Date;

import de.symeda.sormas.app.component.DateField;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

/**
 * Created by Mate Strysewske on 15.06.2017.
 */

public class DateFieldViewAction implements ViewAction {

    private final Date date;

    public DateFieldViewAction(Date date) {
        this.date = date;
    }

    @Override
    public Matcher<View> getConstraints() {
        return isAssignableFrom(DateField.class);
    }

    @Override
    public String getDescription() {
        return "Date field";
    }

    @Override
    public void perform(UiController uiController, View view) {
        DateField dateField = (DateField) view;
        dateField.setValue(date);
    }

}

