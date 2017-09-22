package de.symeda.sormas.app.component;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Martin Wahnschaffe on 08.11.2016.
 */
public abstract class PropertyField<T> extends LinearLayout {

    protected TextView caption;

    private ArrayList<ValueChangeListener> valueChangedListeners;
    private boolean showRequiredHint;

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

    public void setCaption(String caption) {
        this.caption.setText(caption);
    }

    public String getDescription() {
        String captionPropertyId = getFieldCaptionPropertyId();
        return I18nProperties.getFieldDescription(captionPropertyId);
    }

    public void setError(String errorText) {
        caption.setError(errorText);
        caption.requestFocus();
    }

    public void setErrorWithoutFocus(String errorText) {
        caption.setError(errorText);
    }

    public void clearError() {
        caption.setError(null);
        caption.clearFocus();
    }

    public void setRequiredHint(boolean showHint) {
        String captionText = caption.getText().toString();
        String hintText = " <font color='red'>*</font>";
        if (showHint) {
            caption.setText(Html.fromHtml(captionText + hintText), TextView.BufferType.SPANNABLE);
        } else {
            if (showRequiredHint) {
                caption.setText(captionText.substring(0, captionText.length() - 2));
            }
        }

        showRequiredHint = showHint;
    }

    public abstract void setValue(T value);
    public abstract T getValue();

    public void addValueChangedListener(ValueChangeListener listener) {
        if (valueChangedListeners == null) {
            valueChangedListeners = new ArrayList<ValueChangeListener>();
        }
        valueChangedListeners.add(listener);
    }

    public void addCaptionOnClickListener() {
        caption.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (caption != null && e.getAction() == MotionEvent.ACTION_UP) {
                    if (caption.getError() != null) {
                        if (caption.isFocused()) {
                            caption.clearFocus(); // closes error popup
                            return true;
                        }
                    } else if (getDescription() != null && !getDescription().isEmpty()) {
                        HelpDialog helpDialog = new HelpDialog(getContext());
                        helpDialog.setMessage(getDescription());
                        helpDialog.show();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    protected abstract void requestFocusForContentView(View nextView);

    public interface ValueChangeListener {
        void onChange(PropertyField field);
    }

}

