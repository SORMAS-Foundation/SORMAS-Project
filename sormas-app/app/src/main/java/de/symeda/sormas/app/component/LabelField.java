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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.user.User;

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

    @BindingAdapter("app:enumLabel")
    public static void setEnum(LabelField labelField, Enum e) {
        labelField.setValue(e!=null?e.toString():null);
    }

    @BindingAdapter("app:short_uuid")
    public static void setShortUuid(LabelField labelField, String uuid){
        labelField.setValue(DataHelper.getShortUuid(uuid));
    }

    @BindingAdapter("app:dateLabel")
    public static void setDate(LabelField labelField, Date date) {
        labelField.setValue(DateHelper.formatDDMMYYYY(date));
    }

    @BindingAdapter("app:dateTimeLabel")
    public static void setDateTime(LabelField labelField, Date date) {
        labelField.setValue(DateHelper.formatHmDDMMYYYY(date));
    }

    @BindingAdapter("app:userLabel")
    public static void setUser(LabelField labelField, User user) {
        labelField.setValue(user!=null?user.toString():null);
    }

    @BindingAdapter("app:personLabel")
    public static void setPerson(LabelField labelField, Person person) {
        labelField.setValue(person!=null?person.toString():null);
    }

    @BindingAdapter("app:personAgeSexLabel")
    public static void setPersonBirthdate(LabelField labelField, Person person) {
        String value = null;
        if(person!=null) {
            Calendar birthDate = new GregorianCalendar();
            birthDate.set(person.getBirthdateYYYY(), person.getBirthdateMM()!=null?person.getBirthdateMM()-1:0, person.getBirthdateDD()!=null?person.getBirthdateDD():1);
            DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = ApproximateAgeType.ApproximateAgeHelper.getApproximateAge(birthDate.getTime(),new Date());
            value = String.valueOf(approximateAge.getElement0()) + " " + String.valueOf(approximateAge.getElement1());

            value += person.getSex()!=null?", "+person.getSex():null;
        }
        labelField.setValue(value);
    }

    @BindingAdapter("app:cazeLabel")
    public static void setCaze(LabelField labelField, Case caze) {
        labelField.setValue(caze!=null?DataHelper.getShortUuid(caze.getUuid()) + (caze.getPerson()!=null?" " + caze.getPerson().toString():null):null);
    }

}
