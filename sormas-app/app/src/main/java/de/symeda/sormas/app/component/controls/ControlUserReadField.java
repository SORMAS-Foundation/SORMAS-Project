package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebView;

import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.dialog.InfoDialog;
import de.symeda.sormas.app.databinding.DialogUserfieldReadLayoutBindingImpl;

@BindingMethods({
	@BindingMethod(type = ControlUserReadField.class, attribute = "valueFormat", method = "setValueFormat") })
public class ControlUserReadField extends ControlTextReadField {

	private ControlButton userContactButton;

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

		userContactButton = this.findViewById(R.id.user_contact_info_button);

		userContactButton.setButtonType(ControlButtonType.LINE_PRIMARY);

		userContactButton.setOnClickListener(click -> {
			User value = (User) getValue();
			StringBuilder sb = new StringBuilder();
			String userPhone = null;
			String userEmail = null;
			if (value != null) {
				userPhone = value.getPhone();
				userEmail = value.getUserEmail();
			}

			sb.append("<b><h2>" + getResources().getString(R.string.heading_contact_information) + "</h2></b>");

			if (value == null) {
				sb.append(getResources().getString(R.string.message_no_user_selected));
			} else {
				sb.append("<b>").append(getResources().getString(R.string.caption_phone_number)).append("</b>");
				if (userPhone == null || userPhone.isEmpty()) {
					sb.append(getResources().getString(R.string.message_not_specified));
				} else {
					sb.append(userPhone);
				}
				sb.append("<br>");
				sb.append("<b>").append(getResources().getString(R.string.caption_email)).append("</b>");
				if (userEmail == null || userEmail.isEmpty()) {
					sb.append(getResources().getString(R.string.message_not_specified));
				} else {
					sb.append(userEmail);
				}
			}

			InfoDialog userContactDialog = new InfoDialog(getContext(), R.layout.dialog_userfield_read_layout, Html.fromHtml(sb.toString()));
			WebView userContactView = ((DialogUserfieldReadLayoutBindingImpl) userContactDialog.getBinding()).content;
			userContactView.loadData(sb.toString(), "text/html", "utf-8");
			userContactDialog.show();
		});
	}

}
