package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import de.symeda.sormas.app.R;

public class FilterCheckBoxField extends ControlCheckBoxField {

	public FilterCheckBoxField(Context context) {
		super(context);
	}

	public FilterCheckBoxField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FilterCheckBoxField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (inflater != null) {
			inflater.inflate(R.layout.filter_checkbox_field_layout, this);
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@Override
	public void enableErrorState(String errorMessage) {
		//Do nothing
	}

	@Override
	public void enableErrorState(int messageResourceId) {
		//Do nothing
	}

	@Override
	public void disableErrorState() {
		//Do nothing
	}

	@Override
	public void enableWarningState(int messageResourceId) {
		//Do nothing
	}

	@Override
	public void enableWarningState(String message) {
		//Do nothing
	}

	@Override
	public void disableWarningState() {
		//Do nothing
	}
}
