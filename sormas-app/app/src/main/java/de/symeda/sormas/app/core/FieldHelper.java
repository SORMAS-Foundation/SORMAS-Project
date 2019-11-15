package de.symeda.sormas.app.core;

import android.view.View;
import android.view.ViewGroup;

import androidx.arch.core.util.Function;

import de.symeda.sormas.app.component.controls.ControlPropertyField;

public final class FieldHelper {

    public static boolean iteratePropertyFields(ViewGroup parent, Function<ControlPropertyField, Boolean> callback) {
        for (int i=0; i<parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ControlPropertyField) {
                Boolean result = callback.apply((ControlPropertyField)child);
                if (Boolean.FALSE.equals(result)) {
                    return false;
                }
            } else if (child instanceof ViewGroup) {
                boolean result = iteratePropertyFields((ViewGroup)child, callback);
                if (!result) {
                    return false;
                }
            }
        }
        return true;
    }
}
