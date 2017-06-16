package de.symeda.sormas.app;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

import de.symeda.sormas.app.component.TextField;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

/**
 * Created by Mate Strysewske on 14.06.2017.
 */
public class TextFieldViewAction implements ViewAction {

    private final String text;

    public TextFieldViewAction(String text) {
        this.text = text;
    }

    @Override
    public Matcher<View> getConstraints() {
        return isAssignableFrom(TextField.class);
    }

    @Override
    public String getDescription() {
        return "Text field";
    }

    @Override
    public void perform(UiController uiController, View view) {
        TextField textField = (TextField) view;
        textField.setValue(text);
    }

}
