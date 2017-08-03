package de.symeda.sormas.app.component;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.R;

/**
 * Created by Stefan Szczesny on 11.10.2016.
 */

public class YesNoUnknownField extends PropertyField<YesNoUnknown> {

    private RadioGroup radioGroup;

    private InverseBindingListener inverseBindingListener;

    public YesNoUnknownField(Context context) {
        super(context);
        initializeViews(context);
    }

    public YesNoUnknownField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public YesNoUnknownField(Context context,
                             AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    @Override
    public void setValue(YesNoUnknown value) {
        if (value!=null) {
            ((RadioButton) radioGroup.getChildAt(value.ordinal())).setChecked(true);
        } else {
            radioGroup.clearCheck();
        }
    }

    @Override
    public YesNoUnknown getValue() {
        int indexSelected = radioGroup.indexOfChild(radioGroup
                .findViewById(radioGroup.getCheckedRadioButtonId()));
        return indexSelected>-1?YesNoUnknown.values()[indexSelected]:null;
    }


    @BindingAdapter("android:value")
    public static void setValue(YesNoUnknownField view, YesNoUnknown state) {
        view.setValue(state);
    }

    @InverseBindingAdapter(attribute = "android:value", event = "android:valueAttrChanged" /*default - can also be removed*/)
    public static YesNoUnknown getValue(YesNoUnknownField view) {
        return view.getValue();
    }

    @BindingAdapter("android:valueAttrChanged")
    public static void setListener(YesNoUnknownField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    protected void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.field_yes_no, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        radioGroup = (RadioGroup) this.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
        });

        caption = (TextView) this.findViewById(R.id.radio_caption);
        caption.setText(getCaption());
        addCaptionHintIfDescription();
        addCaptionOnClickListener();
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        caption.setEnabled(enabled);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(enabled);
        }
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((YesNoUnknownField) nextView).radioGroup.requestFocus();
    }

}
