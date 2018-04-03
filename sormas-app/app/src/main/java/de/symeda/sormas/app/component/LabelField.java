package de.symeda.sormas.app.component;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Mate Strysewske on 29.11.2016.
 */

public class LabelField extends PropertyField<String> {

    private TextView textContent;
    private String appendedText;
    protected InverseBindingListener inverseBindingListener;

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

    public void makeLink(OnClickListener listener) {
        appendedText = "\u279D";
        textContent.setPaintFlags(textContent.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textContent.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        this.setOnClickListener(listener);
    }

    @Override
    public void setValue(String value) {
        textContent.setText(value);
        if(appendedText != null && appendedText != "") {
            textContent.append(" " + appendedText);
        }
    }

    @Override
    public String getValue() {
        return textContent.getText().toString();
    }

    @BindingAdapter("android:value")
    public static void setValue(LabelField view, String text) {
        view.setValue(text);
    }

    @InverseBindingAdapter(attribute = "android:value", event = "android:valueAttrChanged" /*default - can also be removed*/)
    public static String getValue(LabelField view) {
        return view.getValue();
    }

    @BindingAdapter("android:valueAttrChanged")
    public static void setListener(LabelField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    public void setTextColor(int textColor) {
        textContent.setTextColor(textColor);
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
        textContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
        });
        caption = (TextView) this.findViewById(R.id.text_caption);
        caption.setText(getCaption());
        addCaptionOnClickListener();
    }

    @BindingAdapter("enum")
    public static void setEnumForLabel(LabelField labelField, Enum e) {
        labelField.setValue(e!=null?e.toString():null);
    }

    @BindingAdapter("short_uuid")
    public static void setShortUuidForLabel(LabelField labelField, String uuid){
        labelField.setValue(DataHelper.getShortUuid(uuid));
    }

    @BindingAdapter("date")
    public static void setDateForLabel(LabelField labelField, Date date) {
        labelField.setValue(DateHelper.formatDate(date));
    }

    @BindingAdapter("dateTime")
    public static void setDateTimeForLabel(LabelField labelField, Date date) {
        labelField.setValue(DateHelper.formatDateTime(date));
    }

    @BindingAdapter("user")
    public static void setUserForLabel(LabelField labelField, User user) {
        labelField.setValue(user!=null?user.toString():null);
    }

    @BindingAdapter("personLabel")
    public static void setPerson(LabelField labelField, Person person) {
        labelField.setValue(person!=null?person.toString():null);
    }

    @BindingAdapter("personAgeSexLabel")
    public static void setPersonBirthdate(LabelField labelField, Person person) {
        String value = "";
        if(person!=null) {
            if(person.getBirthdateYYYY() != null) {
                Calendar birthDate = new GregorianCalendar();
                birthDate.set(person.getBirthdateYYYY(), person.getBirthdateMM() != null ? person.getBirthdateMM() - 1 : 0, person.getBirthdateDD() != null ? person.getBirthdateDD() : 1);
                DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = ApproximateAgeType.ApproximateAgeHelper.getApproximateAge(birthDate.getTime(), new Date());
                value = String.valueOf(approximateAge.getElement0()) + " " + String.valueOf(approximateAge.getElement1());
            }

            value += person.getSex()!=null?", "+person.getSex():null;
        }
        labelField.setValue(value);
    }

    @BindingAdapter("caze")
    public static void setCazeForLabel(LabelField labelField, Case caze) {
        labelField.setValue(caze!=null?caze.toString():null);
    }

    @BindingAdapter("contact")
    public static void setContactForLabel(LabelField labelField, Contact contact) {
        labelField.setValue(contact!=null?contact.toString():null);
    }

    @BindingAdapter("event")
    public static void setEventForLabel(LabelField labelField, Event event) {
        labelField.setValue(event!=null?event.toString():null);
    }

    @BindingAdapter("cazeAndLocation")
    public static void setCazeAndLocationForLabel(LabelField labelField, Case caze) {
        labelField.setValue(caze!=null?caze.toString() + (caze.getPerson() != null && caze.getPerson().getAddress() != null ? "\n" + caze.getPerson().getAddress().toString() : ""):null);
    }

    @BindingAdapter("contactAndLocation")
    public static void setContactAndLocationForLabel(LabelField labelField, Contact contact) {
        labelField.setValue(contact!=null?contact.toString() + (contact.getPerson() != null && contact.getPerson().getAddress() != null ? "\n" + contact.getPerson().getAddress().toString() : ""):null);
    }

    @BindingAdapter("eventAndLocation")
    public static void setEventAndLocationForLabel(LabelField labelField, Event event) {
        labelField.setValue(event!=null?event.toString() + (event.getEventLocation() != null ? "\n" + event.getEventLocation().toString() : ""):null);
    }

    @BindingAdapter("sampleTypeOfTest")
    public static void setSampleTypeOfTest(LabelField labelField, String sampleUuid) {
        Sample sample = DatabaseHelper.getSampleDao().queryUuid(sampleUuid);
        SampleTest mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(sample);
        labelField.setValue(mostRecentTest != null ? mostRecentTest.getTestType().toString() : "");
    }

    @BindingAdapter("sampleTestResult")
    public static void setSampleTestResult(LabelField labelField, String sampleUuid) {
        Sample sample = DatabaseHelper.getSampleDao().queryUuid(sampleUuid);
        SampleTest mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(sample);
        labelField.setValue(mostRecentTest != null ? mostRecentTest.getTestResult().toString() : "");
    }

    @BindingAdapter("location")
    public static void setLocationForLabel(LabelField labelField, Location location) {
        if (location == null ) {
            labelField.setValue(labelField.getContext().getString(R.string.label_enter_location));
            labelField.setTextColor(Color.LTGRAY);
        } else {
            String locationString = location.getCompleteString();
            if (!locationString.isEmpty()) {
                labelField.setValue(locationString);
                labelField.setTextColor(Color.BLACK);
            } else {
                labelField.setValue(labelField.getContext().getString(R.string.label_enter_location));
                labelField.setTextColor(Color.LTGRAY);
            }
        }
    }

    @BindingAdapter("region")
    public static void setRegionForLabel(LabelField labelField, Region region) {
        labelField.setValue(region!=null?region.toString():"");
    }

    @BindingAdapter("district")
    public static void setDistrictForLabel(LabelField labelField, District district) {
        labelField.setValue(district!=null?district.toString():"");
    }

    @BindingAdapter("community")
    public static void setCommunityForLabel(LabelField labelField, Community community) {
        labelField.setValue(community!=null?community.toString():"");
    }

    @BindingAdapter("facility")
    public static void setFacilityForLabel(LabelField labelField, Facility facility) {
        labelField.setValue(facility!=null?facility.toString():"");
    }

    @BindingAdapter("number")
    public static void setNumberForLabel(LabelField labelField, Number number) {
        labelField.setValue(number!=null?number.toString():"");
    }

    @BindingAdapter("toString")
    public static void useToStringOfContent(LabelField labelField, Object content) {
        labelField.setValue(content != null ? content.toString() : "");
    }

    @BindingAdapter("locationLatLon")
    public static void setLocationLatLonForLabel(LabelField labelField, Location location) {
        setLatLonForLabel(labelField,
                location != null ? location.getLatitude() : null,
                location != null ? location.getLongitude() : null,
                location != null ? location.getLatLonAccuracy() : null);
    }

    public static void setLatLonForLabel(LabelField labelField, Double latitude, Double longitude, Float latLonAccuracy) {
        if (latitude == null || longitude == null) {
            labelField.setValue(labelField.getContext().getString(R.string.label_pick_gps));
        }
        else {
            labelField.setValue(Location.getLatLonString(latitude, longitude, latLonAccuracy));
        }
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((LabelField) nextView).textContent.requestFocus();
    }

    @Override
    protected void setFieldEnabledStatus(boolean enabled) {
        textContent.setEnabled(enabled);
        textContent.setFocusable(enabled);
    }

}
