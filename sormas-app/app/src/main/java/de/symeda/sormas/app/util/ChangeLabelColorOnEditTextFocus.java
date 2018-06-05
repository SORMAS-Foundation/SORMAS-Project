package de.symeda.sormas.app.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 05/11/2017.
 */
public class ChangeLabelColorOnEditTextFocus implements View.OnFocusChangeListener {

    private Context context;
    private TextView label;

    public ChangeLabelColorOnEditTextFocus(Context context, TextView label) {
        this.context = context;
        this.label = label;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int colorOnFocus = this.context.getResources().getColor(R.color.labelFocus);
        int colorDefault = this.context.getResources().getColor(R.color.controlLabelColor);

        if (v.hasFocus()) {
            this.label.setTextColor(colorOnFocus);
        } else {
            this.label.setTextColor(colorDefault);
        }
    }
}
