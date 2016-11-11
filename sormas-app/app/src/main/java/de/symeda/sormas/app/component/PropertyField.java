package de.symeda.sormas.app.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.symeda.sormas.api.I18nProperties;

/**
 * Created by Martin Wahnschaffe on 08.11.2016.
 */
public abstract class PropertyField<T> extends LinearLayout {

    private ArrayList<ValueChangeListener> valueChangedListeners;

    public PropertyField(Context context) {
        super(context);
    }
    public PropertyField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public PropertyField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public String getCaption() {
        String captionPropertyId = getFieldCaptionPropertyId();
        return I18nProperties.getFieldCaption(captionPropertyId);
    }

    public String getDescription() {
        String captionPropertyId = getFieldCaptionPropertyId();
        return I18nProperties.getFieldDescription(captionPropertyId);
    }


    public abstract void setValue(T value);

    public abstract T getValue();


    public void addValueChangedListener(ValueChangeListener listener) {
        if (valueChangedListeners == null) {
            valueChangedListeners = new ArrayList<ValueChangeListener>();
        }
        valueChangedListeners.add(listener);
    }

    public void removeValueChangedListener(ValueChangeListener listener) {
        if (valueChangedListeners != null) {
            int i = valueChangedListeners.indexOf(listener);
            if (i >= 0) {
                valueChangedListeners.remove(i);
            }
        }
    }

    protected void onValueChanged() {
        if (valueChangedListeners != null) {
            for (ValueChangeListener valueChangedListener : valueChangedListeners) {
                valueChangedListener.onChange(this);
            }
        }
    }

    protected final String getFieldIdString() {

        String fieldIdString = getResources().getResourceName(getId());

        // this is a workaround used in AS < 2.3 because of https://code.google.com/p/android/issues/detail?id=212492 -->
        if (fieldIdString.endsWith("1")) {
            fieldIdString = fieldIdString.substring(0, fieldIdString.length() - 1);
        }
        return fieldIdString;
    }

    public String getPropertyId() {
        String fieldId = getFieldIdString();
        int seperatorIndex = fieldId.lastIndexOf("_");
        return fieldId.substring(seperatorIndex + 1);
    }

    protected final String getFieldCaptionPropertyId() {
        String fieldId = getFieldIdString();
        int seperatorIndex = fieldId.lastIndexOf("/");
        String captionPropertyId = fieldId.substring(seperatorIndex + 1, seperatorIndex+2).toUpperCase() + fieldId.substring(seperatorIndex + 2).replaceAll("_", ".");
        return captionPropertyId;
    }


    public interface ValueChangeListener {
        void onChange(PropertyField field);
    }
}

