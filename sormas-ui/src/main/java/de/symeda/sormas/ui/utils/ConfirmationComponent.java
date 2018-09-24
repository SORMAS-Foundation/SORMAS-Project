package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DoneListener;

/**
 * Besteht aus einem "confirm"/"Übernehmen"- und einem "cancel"/"Abbrechen"-Button
 * 
 * @author Martin Wahnschaffe
 */
@SuppressWarnings("serial")
public abstract class ConfirmationComponent extends HorizontalLayout {
	
	private transient List<DoneListener> doneListeners = new ArrayList<DoneListener>();

	private Button confirmButton;
	private Button cancelButton;
	
	public ConfirmationComponent() {
		this(false);
	}
	
	public ConfirmationComponent(boolean inverseOrder) {

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
	
	public Button getConfirmButton() {
		if (confirmButton == null) {
			confirmButton = new Button("Übernehmen");
			cancelButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

			confirmButton.addClickListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					onConfirm();
					onDone();
				}
			});
		}
		return confirmButton;
	}
	
	public Button getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new Button("Abbrechen");
			cancelButton.addStyleName(ValoTheme.BUTTON_LINK);

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

	protected void onDone() {
		for (DoneListener listener : doneListeners)
			listener.onDone();
	}

	protected abstract void onConfirm();
	
	protected void onCancel() {};
}
