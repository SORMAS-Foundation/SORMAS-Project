package de.symeda.sormas.app.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 14/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboHint extends TeboPropertyField<String> implements ITextControlInterface {

    protected TextView txtControlInput;
    protected InverseBindingListener inverseBindingListener;
    private boolean singleLine;
    private int maxLines;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboHint(Context context) {
        super(context);
    }

    public TeboHint(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboHint(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    @Override
    public void setValue(String value) {
        txtControlInput.setText(value);
    }

    @Override
    public String getValue() {
        if (txtControlInput.getText() == null)
            return null;

        return txtControlInput.getText().toString();
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

    @Override
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            int valueFormatResource;

            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboHint,
                    0, 0);

            try {
                singleLine = a.getBoolean(R.styleable.TeboHint_singleLine, true);
                maxLines = a.getInt(R.styleable.TeboHint_maxLines, 1);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_hint_layout, this);
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
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

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
        return getResources().getColor(R.color.controlTextColor);
    }

    // </editor-fold>
}