package de.symeda.sormas.samples;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.TextField;

/**
 * An extension adding a button in a text field for clearing the field. Only
 * shown when the text field is non-empty.
 * 
 * @see <a href="https://vaadin.com/blog/-/blogs/2656782">Extending components
 *      in Vaadin 7</a>
 */
public class ResetButtonForTextField extends AbstractExtension {

    public static void extend(TextField field) {
        new ResetButtonForTextField().extend((AbstractClientConnector) field);
    }
}
