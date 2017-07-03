package de.symeda.sormas.ui.utils;

import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.utils.DataHelper;
import elemental.json.JsonValue;

@SuppressWarnings("serial")
public class UuidRenderer extends HtmlRenderer {
	
    @Override
    public JsonValue encode(String value) {
    	if(value != null && !value.isEmpty()) {
	    	value = "<a title='" + value + "'>" + DataHelper.getShortUuid(value) + "</a>";
	        return super.encode(value);
    	} else {
    		return null;
    	}
    }
}