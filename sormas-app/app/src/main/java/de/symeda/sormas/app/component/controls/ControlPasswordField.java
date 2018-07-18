package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;

import de.symeda.sormas.app.R;

public class ControlPasswordField extends ControlTextEditField {

    // Constructors

    public ControlPasswordField(Context context) {
        super(context);
    }

    public ControlPasswordField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlPasswordField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Overrides

    @Override
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        super.initialize(context, attrs, defStyle);

        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            if (isSlim()) {
                inflater.inflate(R.layout.control_password_slim_layout, this);
            } else {
                inflater.inflate(R.layout.control_password_layout, this);
            }
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        TextInputLayout inputLayout = (TextInputLayout) this.findViewById(R.id.text_input_layout);
        inputLayout.setPasswordVisibilityToggleEnabled(true);
    }
}