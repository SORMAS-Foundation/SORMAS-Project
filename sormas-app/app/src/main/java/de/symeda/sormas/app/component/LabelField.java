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
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Mate Strysewske on 29.11.2016.
 */

public class LabelField extends PropertyField<String> {

    private TextView textCaption;
    private TextView textContent;
    private String appendedText;

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

    @BindingAdapter("app:enum")
    public static void setEnumForLabel(LabelField labelField, Enum e) {
        labelField.setValue(e!=null?e.toString():null);
    }

    @BindingAdapter("app:short_uuid")
    public static void setShortUuidForLabel(LabelField labelField, String uuid){
        labelField.setValue(DataHelper.getShortUuid(uuid));
    }

    @BindingAdapter("app:date")
    public static void setDateForLabel(LabelField labelField, Date date) {
        labelField.setValue(DateHelper.formatDDMMYYYY(date));
    }

    @BindingAdapter("app:dateTime")
    public static void setDateTimeForLabel(LabelField labelField, Date date) {
        labelField.setValue(DateHelper.formatHmDDMMYYYY(date));
    }

    @BindingAdapter("app:user")
    public static void setUserForLabel(LabelField labelField, User user) {
        labelField.setValue(user!=null?user.toString():null);
    }

    @BindingAdapter("app:personLabel")
    public static void setPerson(LabelField labelField, Person person) {
        labelField.setValue(person!=null?person.toString():null);
    }

    @BindingAdapter("app:personAgeSexLabel")
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

    @BindingAdapter("app:caze")
    public static void setCazeForLabel(LabelField labelField, Case caze) {
        labelField.setValue(caze!=null?DataHelper.getShortUuid(caze.getUuid()) + (caze.getPerson()!=null?" " + caze.getPerson().toString():null):null);
    }

    @BindingAdapter("app:contact")
    public static void setContactForLabel(LabelField labelField, Contact contact) {
        labelField.setValue(contact!=null?DataHelper.getShortUuid(contact.getUuid()) + (contact.getPerson()!=null?" " + contact.getPerson().toString():null):null);
    }

}
