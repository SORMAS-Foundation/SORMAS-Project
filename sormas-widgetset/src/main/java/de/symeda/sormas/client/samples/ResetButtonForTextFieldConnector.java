package de.symeda.sormas.client.samples;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VTextField;
import com.vaadin.shared.ui.Connect;
import de.symeda.sormas.samples.ResetButtonForTextField;

/**
 * Client side implementation of {@link ResetButtonForTextField}.
 * 
 * @see <a href="https://vaadin.com/blog/-/blogs/2656782">Extending components
 *      in Vaadin 7</a>
 */
@Connect(ResetButtonForTextField.class)
public class ResetButtonForTextFieldConnector extends
        AbstractExtensionConnector implements KeyUpHandler, AttachEvent.Handler {

    public static final String CLASSNAME = "resetbuttonfortextfield";
    private VTextField textField;
    private Element resetButtonElement;

    @Override
    protected void extend(ServerConnector serverConnector) {
        serverConnector
                .addStateChangeHandler(new StateChangeEvent.StateChangeHandler() {
                    @Override
                    public void onStateChanged(StateChangeEvent stateChangeEvent) {
                        Scheduler.get().scheduleDeferred(
                                new Scheduler.ScheduledCommand() {
                                    @Override
                                    public void execute() {
                                        updateResetButtonVisibility();
                                    }
                                });
                    }
                });

        textField = (VTextField) ((ComponentConnector) serverConnector)
                .getWidget();
        textField.addStyleName(CLASSNAME + "-textfield");

        resetButtonElement = DOM.createDiv();
        resetButtonElement.addClassName(CLASSNAME + "-resetbutton");

        textField.addAttachHandler(this);
        textField.addKeyUpHandler(this);
    }

    private void updateResetButtonVisibility() {
        if (textField.getValue().isEmpty()
                || textField.getStyleName().contains("v-textfield-prompt")) {
            resetButtonElement.getStyle().setDisplay(Style.Display.NONE);
        } else {
            resetButtonElement.getStyle().clearDisplay();
        }
    }

    public native void addResetButtonClickListener(Element el)
    /*-{
        var self = this;
        el.onclick = $entry(function () {
            self.@de.symeda.sormas.client.samples.ResetButtonForTextFieldConnector::clearTextField()();
        });
    }-*/;

    public native void removeResetButtonClickListener(Element el)
    /*-{
        el.onclick = null;
    }-*/;

    @Override
    public void onKeyUp(KeyUpEvent keyUpEvent) {
        updateResetButtonVisibility();
    }

    @Override
    public void onAttachOrDetach(AttachEvent attachEvent) {
        if (attachEvent.isAttached()) {
            textField.getElement().getParentElement()
                    .insertAfter(resetButtonElement, textField.getElement());
            updateResetButtonVisibility();
            addResetButtonClickListener(resetButtonElement);
        } else {
            Element parentElement = resetButtonElement.getParentElement();
            if (parentElement != null) {
                parentElement.removeChild(resetButtonElement);
            }
            removeResetButtonClickListener(resetButtonElement);
        }
    }

    private void clearTextField() {
        textField.setValue("");
        textField.valueChange(true);
        updateResetButtonVisibility();
        textField.getElement().focus();
    }
}
