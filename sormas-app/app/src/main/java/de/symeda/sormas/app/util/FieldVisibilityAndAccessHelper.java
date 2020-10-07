package de.symeda.sormas.app.util;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.Set;

import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlTextReadField;

public class FieldVisibilityAndAccessHelper {

	public static void setFieldVisibilitiesAndAccesses(
		Class<?> dtoClass,
		ViewGroup viewGroup,
		FieldVisibilityCheckers visibilityCheckers,
		UiFieldAccessCheckers accessCheckers) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View child = viewGroup.getChildAt(i);
			if (child instanceof ControlPropertyField) {
				String propertyId = ((ControlPropertyField) child).getSubPropertyId();
				boolean visibleAllowed = isVisibleAllowed(dtoClass, propertyId, visibilityCheckers);

				child.setVisibility(visibleAllowed && child.getVisibility() == VISIBLE ? VISIBLE : GONE);
				if (child.isEnabled() && !isFieldAccessible(dtoClass, propertyId, accessCheckers)) {
					setFieldInaccessibleValue(child);
				}
			} else if (child instanceof ViewGroup) {
				setFieldVisibilitiesAndAccesses(dtoClass, (ViewGroup) child, visibilityCheckers, accessCheckers);
			}
		}
	}

	public static void setFieldsInaccessible(ViewGroup viewGroup, Set<String> fieldIds) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View child = viewGroup.getChildAt(i);
			if (child instanceof ControlPropertyField) {
				String propertyId = ((ControlPropertyField) child).getSubPropertyId();
				if (fieldIds.contains(propertyId)) {
					setFieldInaccessibleValue(child);
				}
			} else if (child instanceof ViewGroup) {
				setFieldsInaccessible((ViewGroup) child, fieldIds);
			}
		}
	}

	private static void setFieldInaccessibleValue(View child) {
		child.setEnabled(false);
		if (child instanceof ControlPropertyEditField) {
			((ControlPropertyEditField) child).setHint(I18nProperties.getCaption(Captions.inaccessibleValue));
		} else if (child instanceof ControlTextReadField) {
			((ControlTextReadField) child).setInaccessibleValue(I18nProperties.getCaption(Captions.inaccessibleValue));
		}
	}

	public static boolean isVisibleAllowed(Class<?> dtoClass, String propertyId, FieldVisibilityCheckers visibilityCheckers) {
		if (visibilityCheckers == null) {
			return true;
		}

		return visibilityCheckers.isVisible(dtoClass, propertyId);
	}

	public static boolean isFieldAccessible(Class<?> dtoClass, String propertyId, UiFieldAccessCheckers accessCheckers) {
		if (accessCheckers == null) {
			return true;
		}

		return accessCheckers.isAccessible(dtoClass, propertyId);
	}
}
