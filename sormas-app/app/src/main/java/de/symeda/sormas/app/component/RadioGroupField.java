package de.symeda.sormas.app.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Mate Strysewske on 07.12.2016.
 */

public class RadioGroupField extends PropertyField<Object> {

    public static final String SHOW_CAPTION = "showCaption";

    private RadioGroup radioGroup;
    private List<Object> radioGroupElements = new ArrayList<>();

    private InverseBindingListener inverseBindingListener;

    private TypedArray attributes;

    public RadioGroupField(Context context) {
        super(context);
        initializeViews(context);
    }

    public RadioGroupField(Context context, AttributeSet attrs) {
        super(context, attrs);
        attributes = context.obtainStyledAttributes(attrs, R.styleable.FieldAttrs);
        initializeViews(context);
    }

    public RadioGroupField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    @BindingAdapter("android:value")
    public static void setValue(RadioGroupField view, RadioButton value) {
        view.setValue(value);
    }

    @InverseBindingAdapter(attribute = "android:value", event = "android:valueAttrChanged" /*default - can also be removed*/)
    public static Object getValue(RadioGroupField view) {
        return view.getValue();
    }

    @BindingAdapter("android:valueAttrChanged")
    public static void setListener(RadioGroupField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    @Override
    public void setValue(Object value) {
        if(radioGroup.getChildAt(radioGroupElements.indexOf(value)) != null) {
            radioGroup.check((radioGroup.getChildAt(radioGroupElements.indexOf(value))).getId());
        }
    }

    @Override
    public Object getValue() {
        int checkedButtonId = radioGroup.getCheckedRadioButtonId();
        if(checkedButtonId != -1) {
            View checkedButton = radioGroup.findViewById(checkedButtonId);
            return radioGroupElements.get(radioGroup.indexOfChild(checkedButton));
        } else {
            return null;
        }
    }

    public void initialize(Class enumClass) {
        List<Item> items = DataUtils.getEnumItems(enumClass);
        for(Item item : items) {
            this.addItem(item);
        }
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
        inflater.inflate(R.layout.field_radio_group_field, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        radioGroup = (RadioGroup) this.findViewById(R.id.rg_content);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
        });

        caption = (TextView) this.findViewById(R.id.rg_caption);
        if(attributes.getBoolean(R.styleable.FieldAttrs_show_caption, true)) {
            caption.setVisibility(View.VISIBLE);
            caption.setText(getCaption());
        }
        else {
            caption.setVisibility(View.GONE);
        }
        addCaptionOnClickListener();
    }

    public void addItem(Item item) {
        RadioButton button = new RadioButton(getContext());
        if(item.getValue() != null) {
            button.setText(item.getKey());
            radioGroup.addView(button);
            radioGroupElements.add(item.getValue());
        }
    }

    public void removeAllItems() {
        radioGroup.removeAllViews();
        radioGroupElements.clear();
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((RadioGroupField) nextView).radioGroup.requestFocus();
    }

    @Override
    protected void setFieldEnabledStatus(boolean enabled) {
        radioGroup.setEnabled(enabled);
        radioGroup.setFocusable(enabled);
    }

}
