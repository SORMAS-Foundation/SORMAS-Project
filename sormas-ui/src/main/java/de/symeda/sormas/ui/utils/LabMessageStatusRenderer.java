package de.symeda.sormas.ui.utils;

import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import elemental.json.JsonValue;

public class LabMessageStatusRenderer extends TextRenderer {

	@Override
	public JsonValue encode(Object value) {

		if (value != null && (value.getClass().equals(boolean.class) || Boolean.class.isAssignableFrom(value.getClass()))) {
			if ((Boolean) value) {
				return super.encode(I18nProperties.getCaption(Captions.labMessageProcessed));
			} else {
				return super.encode(I18nProperties.getCaption(Captions.labMessageUnprocessed));
			}
		} else {
			return null;
		}
	}
}
