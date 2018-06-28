package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import de.symeda.sormas.app.R;

public class ControlLinkField extends ControlTextReadField {

    // Constructors

    public ControlLinkField(Context context) {
        super(context);
    }

    public ControlLinkField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlLinkField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Overrides

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            inflater.inflate(R.layout.control_link_layout, this);
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setImeOptions(getImeOptions());
    }

}
