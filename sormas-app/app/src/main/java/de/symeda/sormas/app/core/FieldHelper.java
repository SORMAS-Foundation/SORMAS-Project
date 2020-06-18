package de.symeda.sormas.app.core;

import android.view.View;
import android.view.ViewGroup;

import androidx.arch.core.util.Function;

import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.controls.ControlPropertyField;

public final class FieldHelper {

	/**
	 * @param callback
	 *            is called for every found {@link ControlPropertyField}. When false is returned, the iteration is cancelled
	 * @return false when the iteration was cancelled
	 */
	public static boolean iteratePropertyFields(ViewGroup parent, Function<ControlPropertyField, Boolean> callback) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			View child = parent.getChildAt(i);
			if (child instanceof ControlPropertyField) {
				Boolean result = callback.apply((ControlPropertyField) child);
				if (Boolean.FALSE.equals(result)) {
					return false;
				}
			} else if (child instanceof ViewGroup) {
				boolean result = iteratePropertyFields((ViewGroup) child, callback);
				if (!result) {
					return false;
				}
			}
		}
		return true;
	}

	public static PersonalDataFieldAccessChecker createPersonalDataFieldAccessChecker() {
		return PersonalDataFieldAccessChecker.create(ConfigProvider::hasUserRight);
	}

	public static SensitiveDataFieldAccessChecker createSensitiveDataFieldAccessChecker() {
		return SensitiveDataFieldAccessChecker.create(ConfigProvider::hasUserRight);
	}
}
