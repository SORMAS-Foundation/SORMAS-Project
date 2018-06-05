package de.symeda.sormas.app.component;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.util.AttributeSet;

import de.symeda.sormas.api.utils.DataHelper;

/**
 * Created by Orson on 01/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboTextUuidRead extends TeboTextRead {

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboTextUuidRead(Context context) {
        super(context);
    }

    public TeboTextUuidRead(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboTextUuidRead(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    public void setUuidValue(String value) {
        setValue(value);
    }

    public String getUuidValue() {
        return getValue();
    }

    // </editor-fold>

    @Override
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        super.initializeViews(context, attrs, defStyle);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        txtControlInput.setNextFocusLeftId(getNextFocusLeft());
        txtControlInput.setNextFocusRightId(getNextFocusRight());
        txtControlInput.setNextFocusUpId(getNextFocusUp());
        txtControlInput.setNextFocusDownId(getNextFocusDown());
        txtControlInput.setNextFocusForwardId(getNextFocusForward());

        txtControlInput.setImeOptions(getImeOptions());
    }

    @BindingAdapter(value={"uuidValue", "shortUuid", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setValue(TeboTextRead textField, String stringValue, boolean shortUuid, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (stringValue == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            if (stringValue != textField.getValue()) {
                if (shortUuid) {
                    val = DataHelper.getShortUuid(stringValue);
                } else {
                    val = stringValue;
                }

                textField.setValue(val);

                if (valueFormat != null && valueFormat.trim() != "") {
                    textField.updateControl(String.format(valueFormat, stringValue));
                } else {
                    textField.updateControl(val);
                }
            }
        }
    }

}
