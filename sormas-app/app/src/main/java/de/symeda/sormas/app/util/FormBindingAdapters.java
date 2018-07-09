package de.symeda.sormas.app.util;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.controls.ControlTextImageField;
import de.symeda.sormas.app.component.controls.ControlLinkField;
import de.symeda.sormas.app.component.controls.ControlTextReadField;

/**
 * Created by Orson on 19/12/2017.
 */

public class FormBindingAdapters {




    @BindingAdapter("resultStatus")
    public static void setResultStatus(ImageView imageView, SampleTestResultType resultType) {
        if (resultType != null) {
            Drawable drw;
            Context context = imageView.getContext();
            Resources resources = context.getResources();

            if (resultType == SampleTestResultType.POSITIVE) {
                drw = (Drawable) ContextCompat.getDrawable(context, R.drawable.ic_add_24dp).mutate();
                drw.setTint(resources.getColor(R.color.samplePositive));
                imageView.setBackground(drw);
            } else if (resultType == SampleTestResultType.NEGATIVE) {
                drw = (Drawable) ContextCompat.getDrawable(context, R.drawable.ic_remove_24dp).mutate();
                drw.setTint(resources.getColor(R.color.sampleNegative));
                imageView.setBackground(drw);
            } else if (resultType == SampleTestResultType.PENDING) {
                drw = (Drawable) ContextCompat.getDrawable(context, R.drawable.ic_pending_24dp).mutate();
                drw.setTint(resources.getColor(R.color.samplePending));
                imageView.setBackground(drw);
            } else if (resultType == SampleTestResultType.INDETERMINATE) {
                drw = (Drawable) ContextCompat.getDrawable(context, R.drawable.ic_do_not_disturb_on_24dp).mutate();
                drw.setTint(resources.getColor(R.color.sampleIndeterminate));
                imageView.setBackground(drw);
            }
        }
    }

    @BindingAdapter(value={"shipmentStatus", "defaultValue"}, requireAll=false)
    public static void setShipmentStatus(ControlTextImageField control, Sample sample, String defaultValue) {
        String val = defaultValue;
        Context context = control.getContext();
        Resources resources = context.getResources();

        if (sample == null) {
            control.setValue(val);
        } else {

            String stringValue;
            if (sample.isShipped()) {
                stringValue = val = DateHelper.formatShortDate(sample.getShipmentDate());
                if (!stringValue.equals(control.getValue())) {
                    //Set Text to shipment date
                    control.setValue(stringValue);

                    //Set icon to check & tint
                    control.setImageBackground(R.drawable.ic_check_circle_24dp, R.color.goodJob);
                }

            } else {
                stringValue = val = resources.getString(R.string.no);
                if (stringValue != control.getValue()) {
                    //Set text to No
                    control.setValue(stringValue);

                    //Set icon to cancel & tint
                    control.setImageBackground(R.drawable.ic_cancel_24dp, R.color.watchOut);
                }
            }
        }
    }

    @BindingAdapter(value={"receivedStatus", "defaultValue"}, requireAll=false)
    public static void setReceivedStatus(ControlTextImageField control, Sample sample, String defaultValue) {
        String val = defaultValue;
        Context context = control.getContext();
        Resources resources = context.getResources();

        if (sample == null) {
            control.setValue(val);
        } else {

            String stringValue;
            if (sample.isReceived()) {
                stringValue = val = DateHelper.formatShortDate(sample.getReceivedDate());
                if (stringValue != control.getValue()) {
                    //Set Text to shipment date
                    control.setValue(stringValue);

                    //Set icon to check & tint
                    control.setImageBackground(R.drawable.ic_check_circle_24dp, R.color.goodJob);
                }

            } else {
                stringValue = val = resources.getString(R.string.no);
                if (stringValue != control.getValue()) {
                    //Set text to No
                    control.setValue(stringValue);

                    //Set icon to cancel & tint
                    control.setImageBackground(R.drawable.ic_cancel_24dp, R.color.watchOut);
                }
            }
        }
    }

    @BindingAdapter(value={"cazeAndLocation", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setCazeAndLocation(ControlLinkField control, Case caze, String valueFormat, String defaultValue) {
        String val = defaultValue;
        control.setValueFormat(valueFormat);

        if (caze == null) {
            control.setValue(val);
        } else {
            String location = "";
            if (caze.getPerson() != null && caze.getPerson().getAddress() != null) {
                location = "\n" + caze.getPerson().getAddress().toString();
            }
            val = caze.toString() + location;

            if (valueFormat != null && valueFormat.trim() != "") {
                control.setValue(String.format(valueFormat, val));
            } else {
                control.setValue(val);
            }
        }
    }

    @BindingAdapter(value={"contactAndLocation", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setContactAndLocation(ControlLinkField control, Contact contact, String valueFormat, String defaultValue) {
        String val = defaultValue;
        control.setValueFormat(valueFormat);

        if (contact == null) {
            control.setValue(val);
        } else {
            String location = "";
            if (contact.getPerson() != null && contact.getPerson().getAddress() != null) {
                location = "\n" + contact.getPerson().getAddress().toString();
            }
            val = contact.toString() + location;

            if (valueFormat != null && valueFormat.trim() != "") {
                control.setValue(String.format(valueFormat, val));
            } else {
                control.setValue(val);
            }
        }
    }

    @BindingAdapter(value={"eventAndLocation", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setEventAndLocation(ControlLinkField control, Event event, String valueFormat, String defaultValue) {
        String val = defaultValue;
        control.setValueFormat(valueFormat);

        if (event == null) {
            control.setValue(val);
        } else {
            String location = "";
            if (event.getEventLocation() != null) {
                location = "\n" + event.getEventLocation().toString();
            }
            val = event.toString() + location;

            if (valueFormat != null && valueFormat.trim() != "") {
                control.setValue(String.format(valueFormat, val));
            } else {
                control.setValue(val);
            }
        }
    }

    @BindingAdapter("goneIfNull")
    public static void setGoneIfNull(ControlTextReadField textView, Object o) {
        if (o == null) {
            textView.setVisibility(View.GONE);
        }

        if (o instanceof String && o == "") {
            textView.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("goneIfEmpty")
    public static <T> void setGoneIfNull(ImageView control, List<T> list) {
        if (list == null || list.size() <= 0) {
            control.setVisibility(View.GONE);
        } else {
            control.setVisibility(View.VISIBLE);
        }
    }

    @BindingAdapter(value={"alternateBottomMarginIfEmpty", "emptyBottomMargin", "nonEmptyBottomMargin"}, requireAll=true)
    public static <T> void setAlternateBottomMarginIfEmpty(RelativeLayout viewGroup, List<T> list, float emptyBottomMargin, float nonEmptyBottomMargin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)viewGroup.getLayoutParams();

        if (list == null || list.size() <= 0) {
            params.bottomMargin = (int)emptyBottomMargin;
        } else {
            params.bottomMargin = (int)nonEmptyBottomMargin;
        }

        viewGroup.setLayoutParams(params);
    }

    @BindingAdapter(value={"locationValue", "valueFormat", "defaultValue"}, requireAll=false)
    public static void setLocationValue(ControlLinkField textField, Location location, String valueFormat, String defaultValue) {
        String val = defaultValue;
        textField.setValueFormat(valueFormat);

        if (location == null) {
            textField.setValue(val);
        } else {
            val = location.toString();

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setValue(String.format(valueFormat, val));
            } else {
                textField.setValue(val);
            }
        }
    }
}
