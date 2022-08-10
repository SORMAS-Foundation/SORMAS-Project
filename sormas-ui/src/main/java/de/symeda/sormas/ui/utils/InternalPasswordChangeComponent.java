package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DoneListener;

@SuppressWarnings("serial")
public abstract class InternalPasswordChangeComponent extends HorizontalLayout {

	private transient List<DoneListener> doneListeners = new ArrayList<DoneListener>();

	private final boolean inverseOrder;
	private Button confirmButton;
	private Button cancelButton;
	private final String cancelButtonStyle;

	public InternalPasswordChangeComponent() {
		this(false);
	}

	public InternalPasswordChangeComponent(boolean inverseOrder) {
		this(inverseOrder, ValoTheme.BUTTON_PRIMARY);
	}

	public InternalPasswordChangeComponent(boolean inverseOrder, String cancelButtonStyle) {
		this.inverseOrder = inverseOrder;
		this.cancelButtonStyle = cancelButtonStyle;

		setSpacing(true);
		setSizeUndefined();

		Button discardButton = getCancelButton();
		if (!inverseOrder)
			addComponent(discardButton);

		Button commitButton = getConfirmButton();
		addComponent(commitButton);

		if (inverseOrder)
			addComponent(discardButton);
	}

	public void addExtraButton(Button button, Button.ClickListener handler) {
		button.addClickListener(e -> {
			handler.buttonClick(e);
			onDone();
		});

		addComponent(button, inverseOrder ? getComponentCount() - 1 : 0);
	}

	public Button getConfirmButton() {
		if (confirmButton == null) {
			confirmButton = ButtonHelper.createButton(Captions.actionConfirm, false, event -> {
				onConfirm();
				onDone();
			}, ValoTheme.BUTTON_PRIMARY);
		}

		return confirmButton;
	}

	public Button getCancelButton() {
		if (cancelButton == null) {
			cancelButton = ButtonHelper.createButton(Captions.actionCancel, false, event -> {
				onCancel();
				onDone();
			}, cancelButtonStyle);
		}

		return cancelButton;
	}

	public void addDoneListener(DoneListener listener) {
		if (!doneListeners.contains(listener))
			doneListeners.add(listener);
	}

	public void removeDoneListener(DoneListener listener) {
		doneListeners.remove(listener);
	}

	protected void onDone() {
		for (DoneListener listener : doneListeners)
			listener.onDone();
	}

	protected abstract void onConfirm();

	protected void onCancel() {

	};
}
