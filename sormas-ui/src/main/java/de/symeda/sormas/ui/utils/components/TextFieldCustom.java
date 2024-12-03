package de.symeda.sormas.ui.utils.components;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.TextField;

public class TextFieldCustom extends TextField {

    /* Value change events */

    private static final Method VALUE_CHANGE_METHOD;

    static {
        try {
            VALUE_CHANGE_METHOD = Property.ValueChangeListener.class
                    .getDeclaredMethod("valueChange",
                            new Class[] { Property.ValueChangeEvent.class });
        } catch (final NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException(
                    "Internal error finding methods in AbstractField");
        }
    }

    public Registration addTextFieldValueChangeListener(Property.ValueChangeListener listener) {
        Registration registration = addListener(ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
        // ensure "automatic immediate handling" works
        markAsDirty();
        return registration;
    }

	public interface TextFieldValueChangeListener extends SerializableEventListener {

		Method TEXTFIELD_CHANGE_METHOD =
			ReflectTools.findMethod(TextFieldValueChangeListener.class, "textFieldValueChange", TextFieldValueChangeEvent.class);

		void textFieldValueChange(TextFieldValueChangeEvent event);

	}

	public static final class TextFieldValueChangeEvent extends Component.Event {

		private static final long serialVersionUID = 7689979222604215471L;

		public TextFieldValueChangeEvent(TextField source) {
			super(source);
		}

		public TextField getTextField() {
			return (TextField) getComponent();
		}
	}

}
