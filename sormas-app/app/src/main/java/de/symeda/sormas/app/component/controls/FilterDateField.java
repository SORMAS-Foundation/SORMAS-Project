package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import de.symeda.sormas.app.R;

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
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
	public void enableErrorState(String errorMessage) {
		// Don't do anything here
	}

	@Override
	public void disableErrorState() {
		// Don't do anything here
	}
}
