package de.symeda.sormas.app.component;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.user.User;

import static de.symeda.sormas.api.utils.DateHelper.formatTime;

/**
 * Created by Orson on 10/12/2017.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

@BindingMethods({
    @BindingMethod(type = TeboTextRead.class, attribute = "valueFormat", method = "setValueFormat")
})
public class TeboTextRead extends TeboPropertyField<String> implements ITextControlInterface {

    protected TextView txtControlInput;
    protected InverseBindingListener inverseBindingListener;

    private String value;
    private String valueFormat;
    private boolean singleLine;
    private int maxLines;
    private boolean formStyle;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboTextRead(Context context) {
        super(context);
    }

    public TeboTextRead(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboTextRead(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    @Override
    public void setValue(String value) {
        this.value = value;

        if (valueFormat == null || valueFormat.trim() == "") {
            updateControl(value);
        }
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public void updateControl(String value) {
        txtControlInput.setText(value);
        invalidate();
        requestLayout();
    }

    private void setValue(Date value) {
        this.value = null;

        if(value != null) {
            this.value = formatDate(value);

            if (valueFormat == null || valueFormat.trim() == "") {
                updateControl(this.value);
            }
        }
    }

    private static String formatDate(Date value) {
        return DateHelper.formatDate(value);
    }

    private static String formatDateAndTime(Date value) {
        return DateHelper.formatDate(value) + " " + formatTime(value);
    }

    public String getValueFormat() {
        return valueFormat;
    }

    public void setValueFormat(String valueFormat) {
        this.valueFormat = valueFormat;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtControlInput.setEnabled(enabled);
        lblControlLabel.setEnabled(enabled);

    }

    public void setInputType(int inputType) {
        txtControlInput.setInputType(inputType);
    }

    public boolean isSingleLine() {
        return this.singleLine;
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
        txtControlInput.setSingleLine(singleLine);
        txtControlInput.setEllipsize(TextUtils.TruncateAt.END);
        if (!singleLine) {
            txtControlInput.setMaxLines(maxLines);
            //txtControlInput.setLines(maxLines);
        }
    }

    // </editor-fold>

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    @Override
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            int valueFormatResource;

            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboTextRead,
                    0, 0);

            try {
                singleLine = a.getBoolean(R.styleable.TeboTextRead_singleLine, true);
                maxLines = a.getInt(R.styleable.TeboTextRead_maxLines, 1);
                formStyle = a.getBoolean(R.styleable.TeboTextRead_formStyle, false);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_textfield_read_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        txtControlInput = (TextView) this.findViewById(R.id.txtControlInput);
        txtControlInput.addTextChangedListener(new TextWatcher() {
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



        //setMaxLines(maxLines);

        //Set Hint
        //setValue(defaultValue);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();


        //@color/controlReadTextViewBackground

        if (formStyle) {
            Drawable background = getResources().getDrawable(R.drawable.background_control_text_read_form_style);
            int paddingTop = getResources().getDimensionPixelSize(R.dimen.textViewTopPadding);
            int paddingBottom = getResources().getDimensionPixelSize(R.dimen.textViewBottomPadding);
            int paddingRight = getResources().getDimensionPixelSize(R.dimen.textViewRightPadding);
            int paddingLeft = getResources().getDimensionPixelSize(R.dimen.textViewLeftPadding);

            txtControlInput.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

            setBackground(background);

            lblControlLabel.setIncludeFontPadding(true);
            txtControlInput.setIncludeFontPadding(true);
        } else {
            int backgroundColor = getResources().getColor(R.color.controlReadTextViewBackground);
            setBackground(null);
            txtControlInput.setBackgroundColor(backgroundColor);
            lblControlLabel.setIncludeFontPadding(false);
            txtControlInput.setIncludeFontPadding(false);
        }

        txtControlInput.setNextFocusLeftId(getNextFocusLeft());
        txtControlInput.setNextFocusRightId(getNextFocusRight());
        txtControlInput.setNextFocusUpId(getNextFocusUp());
        txtControlInput.setNextFocusDownId(getNextFocusDown());
        txtControlInput.setNextFocusForwardId(getNextFocusForward());

        txtControlInput.setImeOptions(getImeOptions());


        txtControlInput.setTextAlignment(getCaptionAlignment());

        if(getCaptionAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            txtControlInput.setGravity(getCaptionGravity());
        }


        setSingleLine(singleLine);
    }

    // <editor-fold defaultstate="collapsed" desc="Overriden from Base">

    @Override
    public void setBackgroundResource(int resid) {
        int pl = txtControlInput.getPaddingLeft();
        int pt = txtControlInput.getPaddingTop();
        int pr = txtControlInput.getPaddingRight();
        int pb = txtControlInput.getPaddingBottom();

        txtControlInput.setBackgroundResource(resid);

        txtControlInput.setPadding(pl, pt, pr, pb);
    }

    @Override
    public void setBackground(Drawable background) {
        int pl = txtControlInput.getPaddingLeft();
        int pt = txtControlInput.getPaddingTop();
        int pr = txtControlInput.getPaddingRight();
        int pb = txtControlInput.getPaddingBottom();

        txtControlInput.setBackground(background);

        txtControlInput.setPadding(pl, pt, pr, pb);
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TeboTextRead)nextView).txtControlInput.requestFocus();
        //((TextReadControl) nextView).setCursorToRight();
    }

    @Override
    public void updateCaption(String newCaption) {
        setCaption(newCaption);
    }

    @Override
    public int getCaptionColor() {
        if (!formStyle)
            return super.getCaptionColor();

        return getResources().getColor(R.color.controlTextColor);
    }

    // </editor-fold>


    @BindingAdapter(value={"value", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setValue(TeboTextRead textField, String stringValue, String valueFormat, String defaultValue) {
        /*String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (stringValue == null || stringValue.isEmpty()) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            if (stringValue != textField.getValue()) {
                val = stringValue;
                textField.setValue(val);

                if (valueFormat != null && valueFormat.trim() != "") {
                    textField.updateControl(String.format(valueFormat, stringValue));
                } else {
                    textField.updateControl(val);
                }
            }
        }*/
        setValue(textField, stringValue, "", valueFormat, defaultValue);
    }

    @BindingAdapter(value={"value", "appendValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setValue(TeboTextRead textField, String stringValue, String appendValue, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (stringValue == null || stringValue.isEmpty()) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            val = stringValue;
            textField.setValue(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, stringValue, appendValue));
            } else {
                textField.updateControl(val);
            }
        }
    }

    @BindingAdapter(value={"value", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setValue(TeboTextRead textField, Integer integerValue, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (integerValue == null || integerValue < 0) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            String stringValue = (integerValue != null) ? integerValue.toString() : "";
            if (stringValue != textField.getValue()) {
                val = Integer.toString(integerValue);
                textField.setValue(val);

                if (valueFormat != null && valueFormat.trim() != "") {
                    textField.updateControl(String.format(valueFormat, integerValue));
                } else {
                    textField.updateControl(val);
                }
            }
        }
    }

    @BindingAdapter(value={"value", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setValue(TeboTextRead textField, Float floatValue, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (floatValue == null || floatValue < 0f) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {

            if (floatValue.toString() != textField.getValue()) {
                val = Float.toString(floatValue);
                textField.setValue(val);

                if (valueFormat != null && valueFormat.trim() != "") {
                    textField.updateControl(String.format(valueFormat, floatValue));
                } else {
                    textField.updateControl(val);
                }
            }
        }
    }

    @BindingAdapter(value={"value", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setValue(TeboTextRead textField, Enum enumValue, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (enumValue == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            val = enumValue.toString();
            textField.setValue(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, val));
            } else {
                textField.updateControl(val);
            }
        }
    }

    @BindingAdapter(value={"enumValueWithDesc", "valueDesc", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setEnumValueWithDesc(TeboTextRead textField, Enum enumValue, String valueDesc, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (enumValue == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            val = enumValue.toString();
            textField.setValue(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                if (valueDesc != null && !valueDesc.isEmpty()) {
                    textField.updateControl(String.format(valueFormat, val, valueDesc));
                    return;
                }
            }

            textField.updateControl(val);
        }
    }

    @BindingAdapter(value={"value", "appendValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setValue(TeboTextRead textField, Integer integerValue, String appendValue, String valueFormat, String defaultValue) {
        setValue(textField, (integerValue != null) ? integerValue.toString() : "", appendValue, valueFormat, defaultValue);
    }

    @BindingAdapter(value={"stateAndLgaValue", "defaultValue"}, requireAll=false)
    public static void setStateAndLgaValue(TeboTextRead textField, Case caze, String defaultValue) {
        String valueFormat = textField.getResources().getString(R.string.two_text_hypenated_format);
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (caze == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            String region = caze.getRegion() == null ? "" : caze.getRegion().getName();
            String district = caze.getDistrict() == null ? "" : caze.getDistrict().getName();

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, region, district));
            } else {
                textField.updateControl(region);
            }
        }
    }

    @BindingAdapter(value={"stateAndLgaValue", "defaultValue"}, requireAll=false)
    public static void setStateAndLgaValue(TeboTextRead textField, PreviousHospitalization hospitalization, String defaultValue) {
        String valueFormat = textField.getResources().getString(R.string.two_text_hypenated_format);
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (hospitalization == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            String region = hospitalization.getRegion() == null ? "" : hospitalization.getRegion().getName();
            String district = hospitalization.getDistrict() == null ? "" : hospitalization.getDistrict().getName();

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, region, district));
            } else {
                textField.updateControl(region);
            }
        }
    }

    @BindingAdapter(value={"stateValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setStateValue(TeboTextRead textField, Facility facility, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (facility == null || facility.getRegion() == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            Region region = facility.getRegion();

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, region.toString()));
            } else {
                textField.updateControl(region.toString());
            }
        }
    }

    @BindingAdapter(value={"lgaValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setLgaValue(TeboTextRead textField, Facility facility, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (facility == null || facility.getDistrict() == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            District district = facility.getDistrict();

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, district.toString()));
            } else {
                textField.updateControl(district.toString());
            }
        }
    }

    @BindingAdapter(value={"wardValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setWardValue(TeboTextRead textField, Facility facility, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (facility == null || facility.getCommunity() == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            Community community = facility.getCommunity();

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, community.toString()));
            } else {
                textField.updateControl(community.toString());
            }
        }
    }

    @BindingAdapter(value={"wardValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setWardValue(TeboTextRead textField, Community community, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (community == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, community.toString()));
            } else {
                textField.updateControl(community.toString());
            }
        }
    }

    @BindingAdapter(value={"facilityValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setFacilityValue(TeboTextRead textField, Facility facility, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (facility == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, facility.toString()));
            } else {
                textField.updateControl(facility.toString());
            }
        }
    }



    //TODO: Orson - Remove this
    @BindingAdapter(value={"value", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setValue(TeboTextRead textField, Date dateValue, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (dateValue == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            String formattedDate = formatDate(dateValue);
            if (formattedDate != textField.getValue()) {
                val = formattedDate;
                textField.setValue(val);

                if (valueFormat != null && valueFormat.trim() != "") {
                    textField.updateControl(String.format(valueFormat, formattedDate));
                } else {
                    textField.updateControl(val);
                }
            }
        }
    }

    @BindingAdapter(value={"dateValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setDateValue(TeboTextRead textField, Date dateValue, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (dateValue == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            String formattedDate = formatDate(dateValue);
            if (formattedDate != textField.getValue()) {
                val = formattedDate;
                textField.setValue(val);

                if (valueFormat != null && valueFormat.trim() != "") {
                    textField.updateControl(String.format(valueFormat, formattedDate));
                } else {
                    textField.updateControl(val);
                }
            }
        }
    }

    @BindingAdapter(value={"dateRangeValue", "appendValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setDateRangeValue(TeboTextRead textField, Date fromValue, Date appendValue, String valueFormat, String defaultValue) {
        String from = "";
        String to = "";

        textField.setValueFormat(valueFormat);

        if (fromValue == null && appendValue == null) {
            textField.setValue(defaultValue);
            textField.updateControl(defaultValue);
            return;
        }

        if (fromValue == null) {
            from = " ? ";
        } else {
            from = DateHelper.formatDate(fromValue);
        }

        if (appendValue == null) {
            to = " ? ";
        } else {
            to = DateHelper.formatDate(appendValue);
        }

        if (valueFormat != null && valueFormat.trim() != "") {
            textField.updateControl(String.format(valueFormat, from, to));
        } else {
            textField.updateControl(from + " - " + to);
        }
    }

    @BindingAdapter(value={"timeValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setTimeValue(TeboTextRead textField, Date dateValue, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (dateValue == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            val = formatTime(dateValue);
            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, val));
            } else {
                textField.updateControl(val);
            }
        }
    }




    @BindingAdapter(value={"value", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setValue(TeboTextRead textField, Boolean booleanValue, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (booleanValue == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            String stringValue = booleanValue.toString();
            if (stringValue != textField.getValue()) {
                val = stringValue;
                textField.setValue(val);

                if (valueFormat != null && valueFormat.trim() != "") {
                    textField.updateControl(String.format(valueFormat, stringValue));
                } else {
                    textField.updateControl(val);
                }
            }
        }
    }

    @BindingAdapter(value={"value", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setShortUuid(TeboTextRead textField, User user, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (user == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            val = user.toString();
            if (val != textField.getValue()) {
                textField.setValue(val);

                if (valueFormat != null && valueFormat.trim() != "") {
                    textField.updateControl(String.format(valueFormat, val));
                } else {
                    textField.updateControl(val);
                }
            }
        }
    }

    @BindingAdapter(value={"ageWithDateValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setAgeWithDateValue(TeboTextRead textField, Person person, String valueFormat, String defaultValue) {
        Resources resources = textField.getContext().getResources();
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (person == null || (person.getBirthdateDD() == null || person.getBirthdateMM() == null || person.getBirthdateYYYY() == null)) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            val = (person.getApproximateAge() != null && person.getApproximateAge() >= 0)? person.getApproximateAge().toString() : "";

            if (val != textField.getValue()) {
                textField.setValue(val);

                if (valueFormat != null && valueFormat.trim() != "") {
                    //Age

                    //Year or Month
                    String ageType = "";
                    String monthLabel = resources.getString(R.string.abbr_months);
                    String yearLabel = resources.getString(R.string.abbr_years);
                    if (person.getApproximateAgeType() == ApproximateAgeType.YEARS) {
                        ageType = yearLabel;
                    } else if (person.getApproximateAgeType() == ApproximateAgeType.MONTHS) {
                        ageType = monthLabel;
                    }

                    //Dob
                    String dateFormat = textField.getContext().getResources().getString(R.string.date_format);
                    String date = "", month = "", year = "", dob = "";

                    if (person.getBirthdateDD() != null && person.getBirthdateMM() != null && person.getBirthdateYYYY() != null) {
                        date = person.getBirthdateDD() != null ? person.getBirthdateDD().toString() : "1";
                        month = person.getBirthdateMM() != null ? String.valueOf(person.getBirthdateMM() - 1) : "0";
                        year = person.getBirthdateYYYY() != null ? person.getBirthdateYYYY().toString() : "";

                        dob = String.format(dateFormat, date, month, year);
                    }

                    textField.updateControl(String.format(valueFormat, val, ageType, dob));
                } else {
                    textField.updateControl(val);
                }
            }
        }
    }

    @BindingAdapter(value={"occupationTypeValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setOccupationTypeValue(TeboTextRead textField, Person person, String valueFormat, String defaultValue) {
        Resources resources = textField.getContext().getResources();
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (person == null || person.getOccupationType() == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            val = (person.getOccupationType() != null)? person.getOccupationType().toString() : "'";

            if (val != textField.getValue()) {
                textField.setValue(val);

                if (valueFormat != null && valueFormat.trim() != "") {
                    //Occupation DetailsLayout
                    String occupationDetails = person.getOccupationDetails();

                    if (occupationDetails == null || occupationDetails == "") {
                        textField.updateControl(val);
                    } else {
                        textField.updateControl(String.format(valueFormat, val, occupationDetails));
                    }
                } else {
                    textField.updateControl(val);
                }
            }
        }
    }

    @BindingAdapter(value={"personValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setPersonValue(TeboTextRead textField, Person person, String valueFormat, String defaultValue) {
        Resources resources = textField.getContext().getResources();
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (person == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            val = (person != null)? person.toString() : "";

            if (val != textField.getValue()) {
                textField.setValue(val);

                if (valueFormat != null && valueFormat.trim() != "") {
                    textField.updateControl(String.format(valueFormat, val));
                } else {
                    textField.updateControl(val);
                }
            }
        }
    }

    @BindingAdapter(value={"personValue", "defaultValue"}, requireAll=false)
    public static void setPersonValue(TeboTextRead textField, Sample sample, String defaultValue) {
        Resources resources = textField.getContext().getResources();
        String val = defaultValue;
        String valueFormat = resources.getString(R.string.person_name_format);
        textField.setValueFormat(valueFormat);

        if (sample == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            String result = "";
            Case assocCase = sample.getAssociatedCase();

            if (assocCase == null) {
                textField.setValue(defaultValue);
                return;
            }

            Person person = assocCase.getPerson();

            if (person == null) {
                textField.setValue(defaultValue);
                return;
            }

            textField.setValue(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, person.getFirstName(), person.getLastName().toUpperCase()));
            } else {
                textField.updateControl(person.toString());
            }
        }
    }

    @BindingAdapter(value={"temperatureValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setTemperatureValue(TeboTextRead textField, Symptoms symptoms, String valueFormat, String defaultValue) {
        Resources resources = textField.getContext().getResources();
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (symptoms == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            val = (symptoms.getTemperature() != null)? symptoms.getTemperature().toString() : "";
            TemperatureSource tempSource = symptoms.getTemperatureSource();

            if (tempSource == null)
                valueFormat = null;

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, val, tempSource.toString()));
            } else {
                textField.updateControl(val);
            }
        }
    }

    @BindingAdapter(value={"locationValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setLocationValue(TeboTextRead textField, Location location, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (location == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            val = location.toString();
            textField.setValue(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, val));
            } else {
                textField.updateControl(val);
            }
        }
    }

    @BindingAdapter(value={"stateValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setStateValue(TeboTextRead textField, Location location, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (location == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            Region region = location.getRegion();

            if (region == null) {
                textField.setValue(defaultValue);
                textField.updateControl(defaultValue);
                return;
            }

            val = region.getName();
            textField.setValue(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, val));
            } else {
                textField.updateControl(val);
            }
        }
    }

    @BindingAdapter(value={"lgaValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setLgaValue(TeboTextRead textField, Location location, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (location == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            District district = location.getDistrict();

            if (district == null) {
                textField.setValue(defaultValue);
                textField.updateControl(defaultValue);
                return;
            }

            val = district.getName();
            textField.setValue(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, val));
            } else {
                textField.updateControl(val);
            }
        }
    }

    @BindingAdapter(value={"wardValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setWardValue(TeboTextRead textField, Location location, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (location == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            Community community = location.getCommunity();

            if (community == null) {
                textField.setValue(defaultValue);
                textField.updateControl(defaultValue);
                return;
            }

            val = community.getName();
            textField.setValue(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, val));
            } else {
                textField.updateControl(val);
            }
        }
    }

    @BindingAdapter(value={"cityValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setCityValue(TeboTextRead textField, Location location, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (location == null) {
            textField.setValue(val);
            textField.updateControl(val);
        } else {
            String city = location.getCity();

            if (city == null || city == "") {
                textField.setValue(defaultValue);
                textField.updateControl(defaultValue);
                return;
            }

            val = city;
            textField.setValue(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.updateControl(String.format(valueFormat, val));
            } else {
                textField.updateControl(val);
            }
        }
    }
}
