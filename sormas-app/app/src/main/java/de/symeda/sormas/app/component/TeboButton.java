package de.symeda.sormas.app.component;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.StateDrawableBuilder;

/**
 * Created by Orson on 15/11/2017.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboButton extends LinearLayout implements ButtonControlInterface {

    protected Button btnPrimary;
    protected InverseBindingListener inverseBindingListener;

    private String text;
    private TeboButtonType buttonType;
    private int buttonTypeValue;
    private Drawable drawableStart;
    private Drawable drawableEnd;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    private Drawable drawableTop;
    private Drawable drawableBottom;
    private boolean rounded;
    private boolean slim;
    private boolean iconOnly;
    //private View.OnClickListener onButtonOnClickListener;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboButton(Context context) {
        super(context);
        initializeViews(context, null);
    }

    public TeboButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs);
    }

    public TeboButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context, attrs);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    /*public void setText(int resId) {
        if (btnPrimary == null)
            return;

        if (resId <= 0)
            return;

        btnPrimary.setText(getContext().getResources().getString(resId));
    }*/

    public void setText(String value) {
        if (btnPrimary == null)
            return;

        this.text = value;

        btnPrimary.setText(value);
    }

    public String getText() {
        if (btnPrimary == null)
            return null;

        return btnPrimary.getText().toString();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (btnPrimary == null)
            return;

        btnPrimary.setEnabled(enabled);

    }

    public void setButtonType(TeboButtonType buttonType) {
        if (buttonType == null)
            this.buttonTypeValue = -1;
        else
            this.buttonTypeValue = buttonType.getValue();

        this.buttonType = buttonType;
        //inflateButton();

        //updateButton();
    }

    public void setButtonType(int buttonType) {
        this.buttonTypeValue = buttonType;
        this.buttonType = TeboButtonType.getButtonType(this.buttonTypeValue);

        //updateButton();
    }

    public void setRounded(boolean rounded) {
        this.rounded = rounded;

        //updateButton();
    }

    public void setSlim(boolean slim) {
        this.slim = slim;
    }

    public void setIconOnly(boolean iconOnly) {
        this.iconOnly = iconOnly;
    }

    // </editor-fold>

    protected void initializeViews(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboButton,
                    0, 0);

            try {
                text = a.getString(R.styleable.TeboButton_text);
                buttonTypeValue = a.getInteger(R.styleable.TeboButton_buttonType, 0);
                drawableStart = a.getDrawable(R.styleable.TeboButton_drawableStart);
                drawableEnd = a.getDrawable(R.styleable.TeboButton_drawableEnd);
                drawableLeft = a.getDrawable(R.styleable.TeboButton_drawableLeft);
                drawableRight = a.getDrawable(R.styleable.TeboButton_drawableRight);
                drawableTop = a.getDrawable(R.styleable.TeboButton_drawableTop);
                drawableBottom = a.getDrawable(R.styleable.TeboButton_drawableBottom);
                rounded = a.getBoolean(R.styleable.TeboButton_rounded, false);
                slim = a.getBoolean(R.styleable.TeboButton_slim, false);
                iconOnly = a.getBoolean(R.styleable.TeboButton_iconOnly, false);
            } finally {
                a.recycle();
            }

            buttonType = TeboButtonType.getButtonType(buttonTypeValue);
        }


        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_button_layout, this);
        //inflateButton();
    }

    // <editor-fold defaultstate="collapsed" desc="Overriden from Base">

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        btnPrimary = (Button) this.findViewById(R.id.btnPrimary);

        if (text != null && text != "")
            setText(text);

        //updateButton();


        //btnPrimary.getCompoundDrawables();

        /*Drawable img = getContext().getResources().getDrawable( R.drawable.);
        img.setBounds( 0, 0, 60, 60 );
        txtVw.setCompoundDrawables( img, null, null, null );*/
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        updateButton();
    }

    // </editor-fold>

    public void setOnButtonOnClick(View.OnClickListener onButtonOnClickListener) {
        if (btnPrimary == null)
            return;

        btnPrimary.setOnClickListener(onButtonOnClickListener);
    }

    private void updateButton() {
        if (buttonType == null)
            return;

        Resources resources = getResources();

        int textColor = resources.getColor(buttonType.getTextColor());
        int drawableTint = resources.getColor(buttonType.getDrawableTint());

        float height = TeboButtonType.getHeight(resources, slim);
        float textSize = TeboButtonType.getTextSize(resources, slim);
        int horizontalPadding = TeboButtonType.getHorizontalPadding(resources, slim, iconOnly);
        int verticalPadding = TeboButtonType.getVerticalPadding(resources, slim);
        int drawablePadding = TeboButtonType.getDrawablePadding(resources, slim, iconOnly);

        GradientDrawable normalDrawable = buttonType.getNormalStateDrawable(resources).makeRounded(rounded);
        GradientDrawable focusedDrawable = buttonType.getFocusedStateDrawable(resources).makeRounded(rounded);
        GradientDrawable pressedDrawable = buttonType.getPressedStateDrawable(resources).makeRounded(rounded);

        StateListDrawable stateDrawable = new StateDrawableBuilder()
                .setNormalDrawable(normalDrawable)
                .setFocusedDrawable(focusedDrawable)
                .setPressedDrawable(pressedDrawable)
                .build();

        if (drawableStart != null)
            drawableStart.setTint(drawableTint);

        if (drawableEnd != null)
            drawableEnd.setTint(drawableTint);

        if (drawableLeft != null)
            drawableLeft.setTint(drawableTint);

        if (drawableRight != null)
            drawableRight.setTint(drawableTint);

        if (drawableTop != null)
            drawableTop.setTint(drawableTint);

        if (drawableBottom != null)
            drawableBottom.setTint(drawableTint);

        btnPrimary.setCompoundDrawablesWithIntrinsicBounds( drawableLeft, drawableTop, drawableRight, drawableBottom);

        btnPrimary.setTextColor(textColor);
        btnPrimary.setBackground(stateDrawable);
        btnPrimary.setCompoundDrawablePadding(drawablePadding);

        //Set the height
        ViewGroup.LayoutParams param = btnPrimary.getLayoutParams();
        param.height = (int)height;

        btnPrimary.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        btnPrimary.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        if (iconOnly)
            btnPrimary.setText("");

    }
}