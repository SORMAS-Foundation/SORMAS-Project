package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.ViewHelper;

@BindingMethods({
	@BindingMethod(type = ControlUserReadField.class, attribute = "valueFormat", method = "setValueFormat") })
public class ControlUserReadField extends ControlTextReadField {

	public ControlUserReadField(Context context) {
		super(context);
	}

	public ControlUserReadField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initialize(Context context, AttributeSet attrs, int defStyle) {
		super.initialize(context, attrs, defStyle);
	}

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.control_userfield_read_layout, this);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		ImageView userContactButton = this.findViewById(R.id.user_contact_info_button);
		userContactButton.setOnClickListener(click -> {
			User user = (User) getValue();
			Resources resources = getResources();
			ViewHelper.showUserContactInfo(user, resources, getContext());
		});
	}

}
