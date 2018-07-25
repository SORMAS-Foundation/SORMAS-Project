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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.I18nConstants;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.util.ResourceUtils;

@BindingMethods({@BindingMethod(type = ControlTextReadField.class, attribute = "valueFormat", method = "setValueFormat")})
public class ControlTextReadField extends ControlPropertyField<String> {

    // Views

    protected TextView textView;

    // Attributes

    private int maxLines;
    private boolean distinct;
    private Object internalValue;

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
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ControlTextReadField,
                    0, 0);

            try {
                maxLines = a.getInt(R.styleable.ControlTextReadField_maxLines, 2);
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
                if (isSlim()) {
                    inflater.inflate(R.layout.control_textfield_read_slim_layout, this);
                } else {
                    inflater.inflate(R.layout.control_textfield_read_layout, this);
                }
            }
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        textView = (TextView) this.findViewById(R.id.text_view);
        textView.setMaxLines(maxLines);
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

    public void setInputType(int inputType) {
        textView.setInputType(inputType);
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
        textView.setMaxLines(maxLines);
    }

    @Override
    protected void setFieldValue(String value) {
        textView.setText(value);
    }

    @Override
    protected String getFieldValue() {
        return textView.getText().toString();
    }

    @Override
    public void setValue(Object value) {
        internalValue = value;
        setFieldValue(DataHelper.toStringNullable(value));
    }

    public void setValue(Object value, Object internalValue) {
        this.internalValue = internalValue;
        setFieldValue(DataHelper.toStringNullable(value));
    }

    @Override
    public Object getValue() {
        return internalValue;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textView.setEnabled(enabled);
        label.setEnabled(enabled);
    }

    public static void setValue(ControlTextReadField textField, String stringValue, String appendValue, String valueFormat, String defaultValue, Object originalValue) {
        if (StringUtils.isEmpty(stringValue)) {
            textField.setValue(getDefaultValue(defaultValue), originalValue);
        } else {
            if (!StringUtils.isEmpty(valueFormat) && !StringUtils.isEmpty(appendValue)) {
                textField.setValue(String.format(valueFormat, stringValue, appendValue), originalValue);
            } else if (!StringUtils.isEmpty(appendValue)){
                // Default fallback if no valueFormat has been specified
                textField.setValue(stringValue + " - " + appendValue, originalValue);
            } else {
                textField.setValue(stringValue, originalValue);
            }
        }
    }

    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, String stringValue, String appendValue, String valueFormat, String defaultValue) {
       setValue(textField, stringValue, appendValue, valueFormat, defaultValue, stringValue);
    }

    @BindingAdapter(value = {"value", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, String stringValue, String valueFormat, String defaultValue) {
        setValue(textField, stringValue, null, valueFormat, defaultValue, stringValue);
    }

    // Integer
    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, Integer integerValue, String appendValue, String valueFormat, String defaultValue) {
        setValue(textField, integerValue != null ? integerValue.toString() : null, appendValue, valueFormat, defaultValue, integerValue);
    }

    // Float
    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, Float floatValue, String appendValue, String valueFormat, String defaultValue) {
        setValue(textField, floatValue != null ? floatValue.toString() : null, appendValue, valueFormat, defaultValue, floatValue);
    }

    // Enum
    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, Enum enumValue, String appendValue, String valueFormat, String defaultValue) {
        setValue(textField, enumValue != null ? enumValue.toString() : null, appendValue, valueFormat, defaultValue, enumValue);
    }

    // Abstract Domain Object
    @BindingAdapter(value = {"value", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, AbstractDomainObject ado, String valueFormat, String defaultValue) {
        setValue(textField, ado != null ? ado.toString() : null, null, valueFormat, defaultValue, ado);
    }

    // Date & date range
    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, Date dateValue, Date appendValue, String valueFormat, String defaultValue) {
        if (dateValue == null || appendValue == null) {
            setValue(textField, dateValue != null ? DateHelper.formatLocalShortDate(dateValue)
                    : appendValue != null ? DateHelper.formatLocalShortDate(appendValue)
                    : null, null, valueFormat, defaultValue, dateValue);
        } else {
            setValue(textField, DateHelper.formatLocalShortDate(dateValue), DateHelper.formatLocalShortDate(appendValue),
                    valueFormat, defaultValue, dateValue);
        }
    }

    /* Value types that need a different variable and method name */

    @BindingAdapter(value = {"lesionsLocations", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setLesionsLocations(ControlTextReadField textField, Symptoms symptoms, String valueFormat, String defaultValue) {
        StringBuilder lesionsLocationsString = new StringBuilder();
        for (String lesionsLocationId : SymptomsHelper.getLesionsLocationsPropertyIds()) {
            try {
                Method getter = Symptoms.class.getDeclaredMethod("get" + DataHelper.capitalize(lesionsLocationId));
                Boolean lesionsLocation = (Boolean) getter.invoke(symptoms);
                if (lesionsLocation != null) {
                    if (lesionsLocation) {
                        lesionsLocationsString.append(I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, lesionsLocationId))
                                .append(", ");
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        if (lesionsLocationsString.length() > 0) {
            lesionsLocationsString.delete(lesionsLocationsString.lastIndexOf(", "), lesionsLocationsString.length());
        }

        setValue(textField, lesionsLocationsString.toString(), valueFormat, defaultValue);
    }

    @BindingAdapter(value = {"value", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(ControlTextReadField textField, Boolean booleanValue, String valueFormat, String defaultValue) {
        setValue(textField, booleanValue == null ? "" : (Boolean.TRUE.equals(booleanValue) ? YesNoUnknown.YES.toString() : YesNoUnknown.NO.toString()), null, valueFormat, defaultValue, booleanValue);
    }

    // Time
    @BindingAdapter(value = {"timeValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setTimeValue(ControlTextReadField textField, Date dateValue, String valueFormat, String defaultValue) {
        setValue(textField, dateValue != null ? DateHelper.formatTime(dateValue) : null, null, valueFormat, defaultValue, dateValue);
    }

    // Short uuid
    @BindingAdapter(value = {"shortUuidValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setShortUuidValue(ControlTextReadField textField, String uuid, String valueFormat, String defaultValue) {
        setValue(textField, uuid != null ? DataHelper.getShortUuid(uuid) : null, null, valueFormat, defaultValue, uuid);
    }

    // Age with date
    @BindingAdapter(value = {"ageWithDateValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setAgeWithDateValue(ControlTextReadField textField, Person person, String valueFormat, String defaultValue) {
        if (valueFormat == null) {
            valueFormat = ResourceUtils.getString(textField.getContext(), R.string.age_with_birth_date_format);
        }

        if (person == null || person.getApproximateAge() == null) {
            setValue(textField, (String) null, valueFormat, defaultValue);
        } else {
            String age = person.getApproximateAge().toString();
            ApproximateAgeType ageType = person.getApproximateAgeType();
            String day = person.getBirthdateDD() != null ? person.getBirthdateDD().toString() : null;
            String month = person.getBirthdateMM() != null ? person.getBirthdateMM().toString() : null;
            String year = person.getBirthdateYYYY() != null ? person.getBirthdateYYYY().toString() : null;

            StringBuilder ageWithDateBuilder = new StringBuilder();
            ageWithDateBuilder.append(age).append(" ").append(ageType != null ? ageType.toString() : "");

            String dateOfBirth = null;
            if (year != null) {
                if (month != null) {
                    if (day != null) {
                        dateOfBirth = String.format(
                                ResourceUtils.getString(textField.getContext(), R.string.date_format),
                                day, month, year);
                    } else {
                        dateOfBirth = String.format(
                                ResourceUtils.getString(textField.getContext(), R.string.date_two_values_format),
                                month, year);
                    }
                } else {
                    dateOfBirth = year;
                }
            } else if (month != null && day != null) {
                dateOfBirth = String.format(
                        ResourceUtils.getString(textField.getContext(), R.string.date_two_values_format),
                        day, month);
            }

            if (dateOfBirth != null) {
                ageWithDateBuilder.append(" (").append(dateOfBirth).append(")");
            }

            textField.setValue(ageWithDateBuilder.toString());
        }
    }

}
