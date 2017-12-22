package de.symeda.sormas.app.component;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import de.symeda.sormas.app.R;

/**
 * Created by Mate Strysewske on 10.08.2017.
 */
public class CheckBoxField extends PropertyField<Boolean> {

    protected CheckBox checkBox;
    protected InverseBindingListener inverseBindingListener;
    private CompoundButton.OnCheckedChangeListener additionalListener;

    public CheckBoxField(Context context) {
        super(context);
        initializeViews(context);
    }

    public CheckBoxField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CheckBoxField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    @Override
    public void setValue(Boolean value) {
        checkBox.setChecked(value == null ? false : value);
    }

    @Override
    public Boolean getValue() {
        return checkBox.isChecked();
    }

    @BindingAdapter("android:value")
    public static void setValue(CheckBoxField view, boolean value) {
        view.setValue(value);
    }

    @InverseBindingAdapter(attribute = "android:value", event = "android:valueAttrChanged")
    public static boolean getValue(CheckBoxField view) {
        return view.getValue();
    }

    @BindingAdapter("android:valueAttrChanged")
    public static void setListener(CheckBoxField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.field_checkbox_field, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        checkBox = (CheckBox) this.findViewById(R.id.check_box);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();

                if (additionalListener != null) {
                    additionalListener.onCheckedChanged(compoundButton, b);
                }
            }
        });
        checkBox.setText(getCaption());
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((CheckBoxField) nextView).checkBox.requestFocus();
    }

    public void setAdditionalListener(CompoundButton.OnCheckedChangeListener additionalListener) {
        this.additionalListener = additionalListener;
    }
}
