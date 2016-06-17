package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;

import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DoneListener;

/**
 * Besteht aus einem "confirm"/"Übernehmen"- und einem "cancel"/"Abbrechen"-Button
 * 
 * @author Martin Wahnschaffe
 */
@SuppressWarnings("serial")
public abstract class ConfirmationComponent extends HorizontalLayout {
	
	private List<DoneListener> doneListeners = new ArrayList<DoneListener>();

	private NativeButton confirmButton;
	private NativeButton cancelButton;
	
	public ConfirmationComponent() {
		this(false);
	}
	
	public ConfirmationComponent(boolean inverseOrder) {

		setSpacing(true);
		setSizeUndefined();
		
		NativeButton discardButton = getCancelButton();
		if (!inverseOrder)
			addComponent(discardButton);
		
		NativeButton commitButton = getConfirmButton();
		addComponent(commitButton);

		if (inverseOrder)
			addComponent(discardButton);
	}
	
	public NativeButton getConfirmButton() {
		if (confirmButton == null) {
			confirmButton = new NativeButton("Übernehmen");
			//CssStyles.addStyles(confirmButton, CssStyles.BUTTON, CssStyles.BUTTON_PRIMARY);

			confirmButton.addClickListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					onConfirm();
					onDone();
				}
			});
		}
		return confirmButton;
	}
	
	public NativeButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new NativeButton("Abbrechen");

			cancelButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					onCancel();
					onDone();
				}
			});
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

	private void onDone() {
		for (DoneListener listener : doneListeners)
			listener.onDone();
	}

	protected abstract void onConfirm();
	
	protected void onCancel() {};
}
