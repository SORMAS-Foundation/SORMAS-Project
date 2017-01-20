package de.symeda.sormas.ui.utils;

import com.vaadin.ui.renderers.HtmlRenderer;

import elemental.json.JsonValue;

@SuppressWarnings("serial")
public class ShortStringRenderer extends HtmlRenderer {
	
	private final int length;
	
	public ShortStringRenderer(int length) {
		this.length = length;
	}
	
	@Override
	public JsonValue encode(String value) {
		if(value != null && !value.isEmpty()) {
			if(value.length() > length) {
				value = value.substring(0, length);
				value += "...";
			}
			return super.encode(value);
		} else {
			return null;
		}
	}

}
