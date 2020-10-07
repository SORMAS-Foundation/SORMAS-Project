package de.symeda.sormas.app.component.dialog;

import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;

public abstract class FormDialog extends AbstractDialog {

	protected UiFieldAccessCheckers fieldAccessCheckers;

	public FormDialog(
		FragmentActivity activity,
		int rootLayoutId,
		int contentLayoutResourceId,
		int buttonPanelLayoutResourceId,
		int headingResourceId,
		int subHeadingResourceId,
		UiFieldAccessCheckers fieldAccessCheckers) {
		this(
			activity,
			rootLayoutId,
			contentLayoutResourceId,
			buttonPanelLayoutResourceId,
			headingResourceId,
			subHeadingResourceId,
			false,
			fieldAccessCheckers);
	}

	public FormDialog(
		FragmentActivity activity,
		int rootLayoutId,
		int contentLayoutResourceId,
		int buttonPanelLayoutResourceId,
		int headingResourceId,
		int subHeadingResourceId,
		boolean closeOnPositiveButtonClick,
		UiFieldAccessCheckers fieldAccessCheckers) {
		super(
			activity,
			rootLayoutId,
			contentLayoutResourceId,
			buttonPanelLayoutResourceId,
			headingResourceId,
			subHeadingResourceId,
			closeOnPositiveButtonClick);

		this.fieldAccessCheckers = fieldAccessCheckers;
	}

	protected void setFieldVisibilitiesAndAccesses(Class<?> dtoClass, ViewGroup viewGroup) {
		FieldVisibilityAndAccessHelper.setFieldVisibilitiesAndAccesses(dtoClass, viewGroup, null, fieldAccessCheckers);
	}

	protected boolean isFieldAccessible(Class<?> dtoClass, String propertyId) {
		return FieldVisibilityAndAccessHelper.isFieldAccessible(dtoClass, propertyId, fieldAccessCheckers);
	}
}
