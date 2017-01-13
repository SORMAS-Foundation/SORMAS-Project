package de.symeda.sormas.ui.utils;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.utils.DataHelper;
import elemental.json.JsonValue;

@SuppressWarnings("serial")
public class UuidRenderer extends HtmlRenderer {

	private final boolean withCreateCaseIfEmpty;
	
	public UuidRenderer() { 
		this.withCreateCaseIfEmpty = false;
	}
	
	public UuidRenderer(boolean withCreateCaseIfEmpty) {
		this.withCreateCaseIfEmpty = withCreateCaseIfEmpty;
	}
	
    @Override
    public JsonValue encode(String value) {
    	if(withCreateCaseIfEmpty && (value == null || value.isEmpty())) {
    		value = "<a title='Create new case'>Create</a> " + FontAwesome.EDIT.getHtml();
    		return super.encode(value);
    	}
    	
    	if(value != null && !value.isEmpty()) {
	    	value = "<a title='" + value + "'>" + DataHelper.getShortUuid(value) + "</a>";
	        return super.encode(value);
    	} else {
    		return null;
    	}
    }
}