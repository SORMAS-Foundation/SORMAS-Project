package de.symeda.sormas.ui.utils;

import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import elemental.json.JsonValue;

public class PathogenTestResultTypeRenderer extends TextRenderer {
    @Override
    public JsonValue encode(Object value) {
        if (value == null) {
            return super.encode(I18nProperties.getCaption(Captions.notTestedYet));
        }
        return super.encode(value);
    }
}
