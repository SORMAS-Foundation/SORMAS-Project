package de.symeda.sormas.app.component;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.ControlLabelOnTouchListener;

/**
 * Created by Orson on 15/11/2017.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class TeboPropertyField<T> extends LinearLayout {

    protected TextView lblControlLabel;
    private TextView lblRequired;
    private String description;
    private String caption;
    private boolean showCaption;
    private int captionColor;
    private int textAlignment;
    private int gravity;
    private boolean required;

    private View captionFrame;

    private int nextFocusLeft;
    private int nextFocusRight;
    private int nextFocusUp;
    private int nextFocusDown;
    private int nextFocusForward;

    private int imeOptions;

    private boolean slim;
    private boolean emphasizeCaption;
    private boolean italicCaption;

    //private String caption;
    private ArrayList<ValueChangeListener> valueChangedListeners;
    private boolean showRequiredHint;

    public TeboPropertyField(Context context) {
        super(context);
        initializePropertyFieldViews(context, null, 0);
        initializeViews(context, null, 0);
        inflateView(context, null, 0);
    }

    public TeboPropertyField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializePropertyFieldViews(context, attrs, 0);
        initializeViews(context, attrs, 0);
        inflateView(context, null, 0);
    }

    public TeboPropertyField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializePropertyFieldViews(context, attrs, defStyle);
        initializeViews(context, attrs, defStyle);
        inflateView(context, null, 0);
    }

    // TODO: Talk to Martin about this
    /*public String getLblControlLabel() {
        String captionPropertyId = getFieldCaptionPropertyId();
        return I18nProperties.getFieldCaption(captionPropertyId);
    }

    public void setLblControlLabel(String newCaption) {
        if (lblControlLabel != null) {
            lblControlLabel.setText(newCaption);
        } else if ((lblControlLabel = (TextView) this.findViewById(R.id.lblControlLabel)) == null) {
            throw new NullPointerException("The control label object is null.");
        } else {
            lblControlLabel.setText(newCaption);
        }
    }*/


    public abstract void setValue(T value) throws InvalidValueException;
    public abstract T getValue();


    public void setCaption(String newCaption) {
        if ((lblControlLabel = (TextView) this.findViewById(R.id.lblControlLabel)) == null)
            throw new NullPointerException("The control label object is null.");

        lblControlLabel.setText(newCaption);
    }

    public String getCaption() {
        if ((lblControlLabel = (TextView) this.findViewById(R.id.lblControlLabel)) == null)
            throw new NullPointerException("The control label object is null.");

        return lblControlLabel.getText().toString();
    }

    /*

    public String getDescription1() {
        String captionPropertyId = getFieldCaptionPropertyId();
        return I18nProperties.getFieldDescription(captionPropertyId);
    }
    public void setError(String errorText) {
        lblControlLabel.setError(errorText);
        lblControlLabel.requestFocus();
    }

    public void setErrorWithoutFocus(String errorText) {
        lblControlLabel.setError(errorText);
    }

    public void clearError() {
        lblControlLabel.setError(null);
        lblControlLabel.clearFocus();
    }

    public void setRequiredHint(boolean showHint) {
        String captionText = lblControlLabel.getText().toString();
        String hintText = " <font color='red'>*</font>";
        if (showHint) {
            lblControlLabel.setText(Html.fromHtml(captionText + hintText), TextView.BufferType.SPANNABLE);
        } else {
            if (showRequiredHint) {
                lblControlLabel.setText(captionText.substring(0, captionText.length() - 2));
            }
        }

        showRequiredHint = showHint;
    }*/

    public void addValueChangedListener(ValueChangeListener listener) {
        if (valueChangedListeners == null) {
            valueChangedListeners = new ArrayList<ValueChangeListener>();
        }
        valueChangedListeners.add(listener);
    }

    public void addCaptionOnClickListener() {
        lblControlLabel.setOnClickListener(new ControlLabelOnTouchListener(this));
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void onValueChanged() {
        if (valueChangedListeners != null) {
            for (ValueChangeListener valueChangedListener : valueChangedListeners) {
                valueChangedListener.onChange(this);
            }
        }
    }

    protected abstract void initializeViews(Context context, AttributeSet attrs, int defStyle);

    protected abstract void inflateView(Context context, AttributeSet attrs, int defStyle);

    protected abstract void requestFocusForContentView(View nextView);

    //public abstract void setCaption(String value);

    //public abstract String getCaption();




    public interface ValueChangeListener {
        void onChange(TeboPropertyField field);
    }




    protected final String getFieldIdString() {

        String fieldIdString = getResources().getResourceName(getId());

        // this is a workaround used in AS < 2.3 because of https://code.google.com/p/android/issues/detail?id=212492 -->
        if (fieldIdString.endsWith("1")) {
            fieldIdString = fieldIdString.substring(0, fieldIdString.length() - 1);
        }
        return fieldIdString;
    }

    public String getPropertyId() {
        String fieldId = getFieldIdString();
        int seperatorIndex = fieldId.lastIndexOf("_");
        return fieldId.substring(seperatorIndex + 1);
    }

    protected final String getFieldCaptionPropertyId() {
        String fieldId = getFieldIdString();
        int seperatorIndex = fieldId.lastIndexOf("/");
        String captionPropertyId = fieldId.substring(seperatorIndex + 1, seperatorIndex+2).toUpperCase() + fieldId.substring(seperatorIndex + 2).replaceAll("_", ".");
        return captionPropertyId;
    }






    public void setDescription(String value) {
        description = value;
    }

    public String getDescription() {
        return description;
    }

    public String getControlCaption() {
        return caption;
    }

    public int getCaptionColor() {
        return captionColor;
    }

    public int getCaptionAlignment() {
        return textAlignment;
    }

    public int getCaptionGravity() {
        return gravity;
    }


    public int getImeOptions() {
        return imeOptions;
    }

    private void initializePropertyFieldViews(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboPropertyField,
                    0, 0);

            try {
                description = a.getString(R.styleable.TeboPropertyField_description);
                caption = a.getString(R.styleable.TeboPropertyField_labelCaption);
                showCaption = a.getBoolean(R.styleable.TeboPropertyField_showCaption, true);
                captionColor = a.getColor(R.styleable.TeboPropertyField_labelColor,
                        getResources().getColor(R.color.controlReadLabelColor));
                textAlignment = a.getInt(R.styleable.TeboPropertyField_textAlignment, View.TEXT_ALIGNMENT_VIEW_START);
                gravity = a.getInt(R.styleable.TeboPropertyField_gravity, Gravity.LEFT | Gravity.CENTER_VERTICAL);
                required = a.getBoolean(R.styleable.TeboPropertyField_required, false);


                nextFocusLeft = a.getResourceId(R.styleable.TeboPropertyField_nextFocusLeft, View.NO_ID);
                nextFocusRight = a.getResourceId(R.styleable.TeboPropertyField_nextFocusRight, View.NO_ID);
                nextFocusUp = a.getResourceId(R.styleable.TeboPropertyField_nextFocusUp, View.NO_ID);
                nextFocusDown = a.getResourceId(R.styleable.TeboPropertyField_nextFocusDown, View.NO_ID);
                nextFocusForward = a.getResourceId(R.styleable.TeboPropertyField_nextFocusForward, View.NO_ID);
                imeOptions = a.getInt(R.styleable.TeboPropertyField_imeOptions, EditorInfo.IME_NULL);

                slim = a.getBoolean(R.styleable.TeboPropertyField_slim, false);
                emphasizeCaption = a.getBoolean(R.styleable.TeboPropertyField_emphasizeCaption, true);
                italicCaption = a.getBoolean(R.styleable.TeboPropertyField_italicCaption, false);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        captionFrame = this.findViewById(R.id.captionFrame);
        lblControlLabel = (TextView) this.findViewById(R.id.lblControlLabel);

        if (lblControlLabel == null) {
            throw new NullPointerException("Cannot find control label; lblControlLabel");
        }

        lblControlLabel.setTextColor(getCaptionColor());
        lblControlLabel.setTextAlignment(getCaptionAlignment());

        if(getCaptionAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            lblControlLabel.setGravity(getCaptionGravity());
        }
        setCaption(getControlCaption());
        addCaptionOnClickListener();

        int typeFace = italicCaption ? Typeface.ITALIC : Typeface.NORMAL;

        if (!emphasizeCaption) {
            lblControlLabel.setTypeface(Typeface.create("sans-serif", typeFace));
            lblControlLabel.setAllCaps(false);
        } else {
            lblControlLabel.setTypeface(Typeface.create("sans-serif-medium", typeFace));
            lblControlLabel.setAllCaps(true);
        }


        lblRequired = (TextView) this.findViewById(R.id.lblRequired);
        setRequired(required);

        setShowCaption(showCaption);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (captionFrame == null && lblControlLabel == null)
            return;

        int visible = isShowCaption() ? View.VISIBLE : View.GONE;

        if (captionFrame != null) {
            captionFrame.setVisibility(visible);
            return;
        }

        if (lblControlLabel != null)
            lblControlLabel.setVisibility(visible);
    }


    public boolean isShowCaption() {
        return showCaption;
    }

    public void setShowCaption(boolean showCaption) {
        this.showCaption = showCaption;

        /*if (captionFrame == null && lblControlLabel == null)
            return;

        if (captionFrame != null) {
            captionFrame.setVisibility(showCaption ? VISIBLE : GONE);
            return;
        }

        if (lblControlLabel != null)
            lblControlLabel.setVisibility(showCaption ? VISIBLE : GONE);*/
    }

    public void setRequired(boolean value) {
        if(lblRequired == null)
            return;

        lblRequired.setVisibility((value)? VISIBLE : GONE);
    }

    public boolean isSlim() {
        return slim;
    }

    public void setSlim(boolean slim) {
        this.slim = slim;
    }

    public boolean isRequired() {
        if(lblRequired == null)
            return false;

        return (lblRequired.getVisibility() == VISIBLE)? true : false;
    }



    public int getNextFocusLeft() {
        return nextFocusLeft;
    }

    public int getNextFocusRight() {
        return nextFocusRight;
    }

    public int getNextFocusUp() {
        return nextFocusUp;
    }

    public int getNextFocusDown() {
        return nextFocusDown;
    }

    public int getNextFocusForward() {
        return nextFocusForward;
    }

    //TODO: Check enabling with UserRight
    public void setEnabled(boolean enabled, UserRight editOrCreateUserRight) {
        User user = ConfigProvider.getUser();
        //setFieldEnabledStatus(enabled && user.hasUserRight(editOrCreateUserRight));
    }

    /*@Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            throw new UnsupportedOperationException("If you want to enable a custom field, call setEnabled(boolean enabled, UserRight editOrCreateUserRight) instead.");
        } else {
            setFieldEnabledStatus(false);
        }
    }*/

    /**
     * Child classes need to override this method in order to set the enabled status at their
     * encapsulated elements, e.g. an EditText for the TextField class. This method should never be
     * called outside of the setEnabled methods in this class because it could bypass the
     * mechanism that makes sure that fields cannot be set to enabled without the needed rights.
     */
    //protected abstract void setFieldEnabledStatus(boolean enabled);


}
