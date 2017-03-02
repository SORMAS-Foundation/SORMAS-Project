package de.symeda.sormas.app.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import de.symeda.sormas.app.R;

public class YesNoUnknownVerticalField extends YesNoUnknownField {

    public YesNoUnknownVerticalField(Context context) {
        super(context);
    }

    public YesNoUnknownVerticalField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YesNoUnknownVerticalField(Context context,
                             AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.field_yes_no_vertical, this);
    }

}
