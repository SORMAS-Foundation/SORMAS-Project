package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.I18nConstants;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.util.ResourceUtils;

@BindingMethods({@BindingMethod(type = ControlTextReadField.class, attribute = "valueFormat", method = "setValueFormat")})
public class ControlTextReadField extends ControlPropertyField<String> {

    // Views

    protected TextView textView;

    // Attributes

    private String valueFormat;
    private boolean singleLine;
    private int maxLines;
    private boolean distinct;

    // Listeners

    protected InverseBindingListener inverseBindingListener;

    // Constructors

    public ControlTextReadField(Context context) {
        super(context);
    }

    public ControlTextReadField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlTextReadField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Instance methods

    protected static String getDefaultValue(String defaultValue) {
        if (defaultValue != null) {
            return defaultValue;
        } else {
            return I18nProperties.getText(I18nConstants.NOT_AVAILABLE_SHORT);
        }
    }

    // Overrides

    @Override
    public void setValue(String value) {
        textView.setText(value);
    }

    @Override
    public String getValue() {
        throw new UnsupportedOperationException("getValue is not supported by a read-only field");
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textView.setEnabled(enabled);
        label.setEnabled(enabled);

    }

    @Override
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ControlTextReadField,
                    0, 0);

            try {
                singleLine = a.getBoolean(R.styleable.ControlTextReadField_singleLine, true);
                maxLines = a.getInt(R.styleable.ControlTextReadField_maxLines, 1);
                distinct = a.getBoolean(R.styleable.ControlTextReadField_distinct, false);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            if (distinct) {
                if (isSlim()) {
                    inflater.inflate(R.layout.control_textfield_read_distinct_slim_layout, this);
                } else {
                    inflater.inflate(R.layout.control_textfield_read_distinct_layout, this);
                }
            } else {
                inflater.inflate(R.layout.control_textfield_read_layout, this);
            }
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        textView = (TextView) this.findViewById(R.id.text_view);
        setSingleLine(singleLine);
        textView.setImeOptions(getImeOptions());
        textView.setTextAlignment(getTextAlignment());
        if(getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            textView.setGravity(getGravity());
        }

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
        });
    }

    @Override
    public void setBackgroundResource(int resId) {
        setBackgroundResourceFor(textView, resId);
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundFor(textView, background);
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((ControlTextReadField) nextView).textView.requestFocus();
    }

    // Data binding, getters & setters

    public String getValueFormat() {
        return valueFormat;
    }

    public void setValueFormat(String valueFormat) {
        this.valueFormat = valueFormat;
    }

    public void setInputType(int inputType) {
        textView.setInputType(inputType);
    }

    public boolean isSingleLine() {
        return this.singleLine;
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
        if (singleLine) {
            textView.setMaxLines(1);
        } else {
            textView.setMaxLines(maxLines);
        }
    }

    /* Value types that can simply be called by setValue */

    // String
    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, String stringValue, String appendValue, String valueFormat, String defaultValue) {
        textField.setValueFormat(valueFormat);

        if (StringUtils.isEmpty(stringValue)) {
            textField.setValue(getDefaultValue(defaultValue));
        } else {
            if (!StringUtils.isEmpty(valueFormat) && !StringUtils.isEmpty(appendValue)) {
                textField.setValue(String.format(valueFormat, stringValue, appendValue));
            } else if (!StringUtils.isEmpty(appendValue)){
                // Default fallback if no valueFormat has been specified
                textField.setValue(stringValue + " - " + appendValue);
            } else {
                textField.setValue(stringValue);
            }
        }
    }

    @BindingAdapter(value = {"value", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, String stringValue, String valueFormat, String defaultValue) {
        setValue(textField, stringValue, null, valueFormat, defaultValue);
    }

    // Integer
    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, Integer integerValue, String appendValue, String valueFormat, String defaultValue) {
        setValue(textField, integerValue != null ? integerValue.toString() : null, appendValue, valueFormat, defaultValue);
    }

    // Float
    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, Float floatValue, String appendValue, String valueFormat, String defaultValue) {
        setValue(textField, floatValue != null ? floatValue.toString() : null, appendValue, valueFormat, defaultValue);
    }

    // Enum
    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, Enum enumValue, String appendValue, String valueFormat, String defaultValue) {
        setValue(textField, enumValue != null ? enumValue.toString() : null, appendValue, valueFormat, defaultValue);
    }

    // Abstract Domain Object
    @BindingAdapter(value = {"value", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, AbstractDomainObject ado, String valueFormat, String defaultValue) {
        setValue(textField, ado != null ? ado.toString() : null, valueFormat, defaultValue);
    }

    // Date & date range
    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, Date dateValue, Date appendValue, String valueFormat, String defaultValue) {
        if (dateValue == null || appendValue == null) {
            setValue(textField, dateValue != null ? DateHelper.formatDate(dateValue)
                    : appendValue != null ? DateHelper.formatDate(appendValue)
                    : null, null, valueFormat, defaultValue);
        } else {
            setValue(textField, DateHelper.formatDate(dateValue), DateHelper.formatDate(appendValue),
                    valueFormat, defaultValue);
        }
    }

    // Boolean
    @BindingAdapter(value = {"value", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, Boolean booleanValue, String valueFormat, String defaultValue) {
        setValue(textField, booleanValue != null ? booleanValue.toString() : null, valueFormat, defaultValue);
    }

    /* Value types that need a different variable and method name */

    // Time
    @BindingAdapter(value = {"timeValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setTimeValue(ControlTextReadField textField, Date dateValue, String valueFormat, String defaultValue) {
        setValue(textField, dateValue != null ? DateHelper.formatTime(dateValue) : null, valueFormat, defaultValue);
    }

    // Short uuid
    @BindingAdapter(value = {"shortUuidValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setShortUuidValue(ControlTextReadField textField, String uuid, String valueFormat, String defaultValue) {
        setValue(textField, uuid != null ? DataHelper.getShortUuid(uuid) : null, valueFormat, defaultValue);
    }

    // Age with date
    @BindingAdapter(value={"ageWithDateValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setAgeWithDateValue(ControlTextReadField textField, Person person, String valueFormat, String defaultValue) {
        if (person == null || person.getApproximateAge() == null) {
            setValue(textField, (String) null, valueFormat, defaultValue);
        } else {
            String age = person.getApproximateAge().toString();
            ApproximateAgeType ageType = person.getApproximateAgeType();
            String day, month, year;
            day = person.getBirthdateDD() != null ? person.getBirthdateDD().toString() : null;
            month = person.getBirthdateMM() != null ? person.getBirthdateMM().toString() : null;
            year = person.getBirthdateYYYY() != null ? person.getBirthdateYYYY().toString() : null;

            if (year != null) {
                if (month != null) {
                    if (day != null) {
                        String dateOfBirth = String.format(ResourceUtils.getString(
                                textField.getContext(), R.string.date_format), day, month, year);
                        textField.setValue(String.format(valueFormat, age, ageType, dateOfBirth));
                    } else {
                        String dateOfBirth = String.format(ResourceUtils.getString(
                                textField.getContext(), R.string.date_without_day_format), month, year);
                        textField.setValue(String.format(valueFormat, age, ageType, dateOfBirth));
                    }
                } else {
                    textField.setValue(String.format(valueFormat, age, ageType, year));
                }
            }
        }
    }

}
