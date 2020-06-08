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

import static android.view.View.GONE;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.controls.ControlLinkField;
import de.symeda.sormas.app.core.enumeration.PathogenTestResultTypeElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;

public class FormBindingAdapters {

	@BindingAdapter("resultStatusIcon")
	public static void setResultStatusIcon(ImageView imageView, PathogenTestResultType resultType) {
		if (resultType != null) {
			Context context = imageView.getContext();
			PathogenTestResultTypeElaborator elaborator = (PathogenTestResultTypeElaborator) StatusElaboratorFactory.getElaborator(resultType);

			int iconResource = elaborator.getIconResourceId();
			Drawable drawable = ContextCompat.getDrawable(context, iconResource).mutate();
			drawable.setTint(context.getResources().getColor(elaborator.getColorIndicatorResource()));
			imageView.setBackground(drawable);
		}
	}

	@BindingAdapter(value = {
		"cazeAndLocation",
		"valueFormat",
		"defaultValue" }, requireAll = false)
	public static void setCazeAndLocation(ControlLinkField control, Case caze, String valueFormat, String defaultValue) {
		String val = defaultValue;

		if (caze == null) {
			control.setValue(val);
		} else {
			String location = "";
			if (caze.getPerson() != null && caze.getPerson().getAddress() != null) {
				location = "\n" + caze.getPerson().getAddress().toString();
			}
			val = caze.toString() + location;

			if (valueFormat != null && valueFormat.trim() != "") {
				control.setValue(String.format(valueFormat, val));
			} else {
				control.setValue(val);
			}
		}
	}

	@BindingAdapter(value = {
		"contactAndLocation",
		"valueFormat",
		"defaultValue" }, requireAll = false)
	public static void setContactAndLocation(ControlLinkField control, Contact contact, String valueFormat, String defaultValue) {
		String val = defaultValue;

		if (contact == null) {
			control.setValue(val);
		} else {
			String location = "";
			if (contact.getPerson() != null && contact.getPerson().getAddress() != null) {
				location = "\n" + contact.getPerson().getAddress().toString();
			}
			val = contact.toString() + location;

			if (valueFormat != null && valueFormat.trim() != "") {
				control.setValue(String.format(valueFormat, val));
			} else {
				control.setValue(val);
			}
		}
	}

	@BindingAdapter(value = {
		"eventAndLocation",
		"valueFormat",
		"defaultValue" }, requireAll = false)
	public static void setEventAndLocation(ControlLinkField control, Event event, String valueFormat, String defaultValue) {
		String val = defaultValue;

		if (event == null) {
			control.setValue(val);
		} else {
			String location = "";
			if (event.getEventLocation() != null) {
				location = "\n" + event.getEventLocation().toString();
			}
			val = event.toString() + location;

			if (valueFormat != null && valueFormat.trim() != "") {
				control.setValue(String.format(valueFormat, val));
			} else {
				control.setValue(val);
			}
		}
	}

	@BindingAdapter("userViewRight")
	public static void setUserViewRight(View view, UserRight viewRight) {
		if (!ConfigProvider.hasUserRight(viewRight)) {
			view.setVisibility(GONE);
		}
	}

	@BindingAdapter("goneIfEmpty")
	public static void setGoneIfEmpty(View view, Object o) {
		if (o == null) {
			view.setVisibility(GONE);
		} else if (o instanceof String && DataHelper.isNullOrEmpty((String) o)) {
			view.setVisibility(GONE);
		} else if (o instanceof YesNoUnknown && YesNoUnknown.NO.equals(o)) {
			view.setVisibility(GONE);
		} else if (o instanceof SymptomState && SymptomState.NO.equals(o)) {
			view.setVisibility(GONE);
		}
	}

	@BindingAdapter(value = {
		"goneIfValue",
		"goneIfVariable" })
	public static void setGoneIf(View view, Enum goneIfValue, Enum goneIfVariable) {
		if (goneIfVariable == goneIfValue) {
			view.setVisibility(GONE);
		}
	}

	@BindingAdapter(value = {
		"goneIfNotValue",
		"goneIfVariable" })
	public static void setGoneIfNot(View view, Enum goneIfNotValue, Enum goneIfVariable) {
		if (goneIfVariable != goneIfNotValue) {
			view.setVisibility(GONE);
		}
	}

	@BindingAdapter(value = {
		"goneIfNotValue",
		"goneIfNotValue2",
		"goneIfVariable" })
	public static void setGoneIfNot(View view, Enum goneIfNotValue, Enum goneIfNotValue2, Enum goneIfVariable) {
		if (goneIfVariable != goneIfNotValue && goneIfVariable != goneIfNotValue2) {
			view.setVisibility(GONE);
		}
	}

	@BindingAdapter(value = {
		"alternateBottomMarginIfEmpty",
		"emptyBottomMargin",
		"nonEmptyBottomMargin" }, requireAll = true)
	public static <T> void setAlternateBottomMarginIfEmpty(
		RelativeLayout viewGroup,
		List<T> list,
		float emptyBottomMargin,
		float nonEmptyBottomMargin) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewGroup.getLayoutParams();

		if (list == null || list.size() <= 0) {
			params.bottomMargin = (int) emptyBottomMargin;
		} else {
			params.bottomMargin = (int) nonEmptyBottomMargin;
		}

		viewGroup.setLayoutParams(params);
	}

	@BindingAdapter(value = {
		"locationValue",
		"valueFormat",
		"defaultValue" }, requireAll = false)
	public static void setLocationValue(ControlLinkField textField, Location location, String valueFormat, String defaultValue) {
		String val = defaultValue;

		if (location == null) {
			textField.setValue(val);
		} else {
			val = location.toString();

			if (valueFormat != null && valueFormat.trim() != "") {
				textField.setValue(String.format(valueFormat, val));
			} else {
				textField.setValue(val);
			}
		}
	}
}
