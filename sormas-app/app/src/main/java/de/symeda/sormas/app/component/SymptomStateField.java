package de.symeda.sormas.app.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.R;

/**
 * Created by Stefan Szczesny on 11.10.2016.
 */

public class SymptomStateField extends LinearLayout {

    private RadioGroup mRadioGroup;
    private TextView mCaption;

    public SymptomStateField(Context context) {
        super(context);
        initializeViews(context);
    }

    public SymptomStateField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public SymptomStateField(Context context,
                             AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.field_yes_no, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mRadioGroup = (RadioGroup) this
                .findViewById(R.id.radio_group);
        mCaption = (TextView) this
                .findViewById(R.id.radio_caption);
    }

    public void setCaption(String caption) {
        mCaption.setText(caption);
    }

    public void setValue(SymptomState state) {
        if(state!=null) {
            ((RadioButton) mRadioGroup.getChildAt(state.ordinal())).setChecked(true);
        }
    }

    public SymptomState getValue() {
        return mRadioGroup.getCheckedRadioButtonId()>0?SymptomState.values()[mRadioGroup.getCheckedRadioButtonId()]:null;
    }

    public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener listener) {
        mRadioGroup.setOnCheckedChangeListener(listener);
    }
}
