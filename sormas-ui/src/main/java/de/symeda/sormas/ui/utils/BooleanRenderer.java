package de.symeda.sormas.ui.utils;

import com.vaadin.ui.renderers.HtmlRenderer;

import elemental.json.JsonValue;

@SuppressWarnings("serial")
public class BooleanRenderer extends HtmlRenderer {
	
	@Override
    public JsonValue encode(String value) {
    	if(value != null && !value.isEmpty()) {
    		if (value.equals("true")) {
    			return super.encode("Yes");
    		} else {
    			return super.encode("No");
    		}
    	} else {
    		return null;
    	}
    }
	
}
