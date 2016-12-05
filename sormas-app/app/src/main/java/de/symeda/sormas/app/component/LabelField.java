package de.symeda.sormas.app.component;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.app.R;

/**
 * Created by Mate Strysewske on 29.11.2016.
 */

public class LabelField extends PropertyField<String> {

    private TextView textCaption;
    private TextView textContent;

    public LabelField(Context context) {
        super(context);
        initializeViews(context);
    }

    public LabelField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public LabelField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    @Override
    public void setValue(String value) {
        textContent.setText(value);
    }

    @Override
    public String getValue() {
        return textContent.getText().toString();
    }

    @BindingAdapter("android:value")
    public static void setValue(LabelField view, String text) {
        view.setValue(text);
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
        inflater.inflate(R.layout.field_label_field, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        textContent = (TextView) this.findViewById(R.id.text_content);
        textCaption = (TextView) this.findViewById(R.id.text_caption);
        textCaption.setText(getCaption());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textContent.setEnabled(enabled);
        textCaption.setEnabled(enabled);
    }

}
