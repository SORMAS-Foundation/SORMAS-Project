/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.dialog.InfoDialog;
import de.symeda.sormas.app.databinding.DialogUserContactInfoLayoutBinding;

/**
 * Created by Orson on 30/12/2017.
 */

public class ViewHelper {

	public static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
		ArrayList<View> views = new ArrayList<View>();
		final int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);
			if (child instanceof ViewGroup) {
				views.addAll(getViewsByTag((ViewGroup) child, tag));
			}

			if (tag == null || tag.isEmpty()) {
				views.add(child);
				continue;
			}

			final Object tagObj = child.getTag();
			if (tagObj != null && tagObj.equals(tag)) {
				views.add(child);
			}

		}
		return views;
	}

	public static void formatInaccessibleTextView(TextView textField) {
		textField.setText(I18nProperties.getCaption(Captions.inaccessibleValue));
		textField.setTextColor(textField.getContext().getResources().getColor(R.color.disabled));
		textField.setTypeface(null, Typeface.ITALIC);
	}

	public static void removeInaccessibleTextViewFormat(TextView textField) {
		textField.setTextColor(textField.getContext().getResources().getColor(R.color.listActivityRowPrimaryText));
		textField.setTypeface(null, Typeface.NORMAL);
	}

	public static String getLatLonLocation(Location location) {
		if (location == null || location.getLatLonString().isEmpty()) {
			return null;
		} else {
			return "GPS: " + location.getLatLonString();
		}
	}

	public static void showUserContactInfo(User user, Resources resource, Context context, boolean isPseudonymized) {
		StringBuilder sb = new StringBuilder();
		String userPhone = null;
		String userEmail = null;
		if (user != null) {
			userPhone = user.getPhone();
			userEmail = user.getUserEmail();
		}

		sb.append("<b><h2>" + resource.getString(R.string.heading_contact_information) + "</h2></b>");

		if (user == null && !isPseudonymized) {
			showPseudonymized(resource, sb);
		} else {
			if (isPseudonymized) {
				showPseudonymized(resource, sb);
			} else {
				sb.append("<b>").append(resource.getString(R.string.caption_phone_number)).append("</b>");
				if (userPhone == null || userPhone.isEmpty()) {
					sb.append(resource.getString(R.string.message_not_specified));
				} else {
					sb.append("<a href=\"tel:" + userPhone + "\">" + userPhone + "</a>");
				}
				sb.append("<br>");
				sb.append("<b>").append(resource.getString(R.string.caption_email)).append("</b>");
				if (userEmail == null || userEmail.isEmpty()) {
					sb.append(resource.getString(R.string.message_not_specified));
				} else {
					sb.append("<a href=\"mailto:" + userEmail + "\">" + userEmail + "</a>");
				}
			}
		}

		InfoDialog userContactDialog = new InfoDialog(context, R.layout.dialog_user_contact_info_layout, Html.fromHtml(sb.toString()));
		WebView userContactView = ((DialogUserContactInfoLayoutBinding) userContactDialog.getBinding()).content;
		userContactView.loadData(sb.toString(), "text/html", "utf-8");
		userContactDialog.show();
	}

	private static void showPseudonymized(Resources resource, StringBuilder sb) {
		sb.append("<b>").append(resource.getString(R.string.caption_phone_number)).append("</b>");
		String inaccessibleValue = I18nProperties.getCaption(Captions.inaccessibleValue);
		sb.append("<i>").append(inaccessibleValue).append("<i>");
		sb.append("<br>");
		sb.append("<b>").append(resource.getString(R.string.caption_email)).append("</b>");
		sb.append("<i>").append(inaccessibleValue).append("<i>");
	}

}
