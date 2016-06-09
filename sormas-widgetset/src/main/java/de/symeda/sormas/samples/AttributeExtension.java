package de.symeda.sormas.samples;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.TextField;

/**
 * A JavaScript extension for adding arbitrary HTML attributes for components.
 */
@JavaScript("attribute_extension_connector.js")
public class AttributeExtension extends AbstractJavaScriptExtension {

    public void extend(TextField target) {
        super.extend(target);
    }

    @Override
    protected AttributeExtensionState getState() {
        return (AttributeExtensionState) super.getState();
    }

    public void setAttribute(String attribute, String value) {
        getState().attributes.put(attribute, value);
    }
}
