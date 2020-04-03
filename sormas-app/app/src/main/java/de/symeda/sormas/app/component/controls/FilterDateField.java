package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;

public class FilterDateField extends ControlDateField {

    // Constructors

    public FilterDateField(Context context) {
        super(context);
    }

    public FilterDateField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterDateField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Overrides

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            inflater.inflate(R.layout.filter_date_field_layout, this);
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        setLiveValidationDisabled(true);
        super.onFinishInflate();
    }

    @Override
    protected void changeVisualState(VisualState state) {
        if (getUserEditRight() != null && !ConfigProvider.hasUserRight(getUserEditRight())) {
            state = VisualState.DISABLED;
        }

        if (this.visualState == state) {
            return;
        }

        visualState = state;

        int labelColor = getResources().getColor(state.getLabelColor());
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.TEXT_FIELD));
        int textColor = getResources().getColor(state.getTextColor());
        int hintColor = getResources().getColor(state.getHintColor());

        if (drawable != null) {
            drawable = drawable.mutate();
        }

        label.setTextColor(labelColor);
        setBackground(drawable);

        if (state != VisualState.ERROR) {
            input.setTextColor(textColor);
            input.setHintTextColor(hintColor);
        }

        setEnabled(state != VisualState.DISABLED);
    }

    @Override
    public void enableErrorState(String errorMessage) {
        // Don't do anything here
    }

    @Override
    public void disableErrorState() {
        // Don't do anything here
    }

}
