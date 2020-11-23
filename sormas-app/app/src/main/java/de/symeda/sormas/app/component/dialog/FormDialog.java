package de.symeda.sormas.app.component.dialog;

import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;

public abstract class FormDialog extends AbstractDialog {

	private FieldVisibilityCheckers fieldVisibilityCheckers;
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
		this(
			activity,
			rootLayoutId,
			contentLayoutResourceId,
			buttonPanelLayoutResourceId,
			headingResourceId,
			subHeadingResourceId,
			closeOnPositiveButtonClick,
			fieldAccessCheckers,
			null);
	}

	public FormDialog(
		FragmentActivity activity,
		int rootLayoutId,
		int contentLayoutResourceId,
		int buttonPanelLayoutResourceId,
		int headingResourceId,
		int subHeadingResourceId,
		boolean closeOnPositiveButtonClick,
		UiFieldAccessCheckers fieldAccessCheckers,
		FieldVisibilityCheckers fieldVisibilityCheckers) {
		super(
			activity,
			rootLayoutId,
			contentLayoutResourceId,
			buttonPanelLayoutResourceId,
			headingResourceId,
			subHeadingResourceId,
			closeOnPositiveButtonClick);

		this.fieldAccessCheckers = fieldAccessCheckers;
		this.fieldVisibilityCheckers = fieldVisibilityCheckers;
	}

	protected void setFieldVisibilitiesAndAccesses(Class<?> dtoClass, ViewGroup viewGroup) {
		FieldVisibilityAndAccessHelper.setFieldVisibilitiesAndAccesses(dtoClass, viewGroup, fieldVisibilityCheckers, fieldAccessCheckers);
	}

	protected boolean isFieldAccessible(Class<?> dtoClass, String propertyId) {
		return FieldVisibilityAndAccessHelper.isFieldAccessible(dtoClass, propertyId, fieldAccessCheckers);
	}
}
