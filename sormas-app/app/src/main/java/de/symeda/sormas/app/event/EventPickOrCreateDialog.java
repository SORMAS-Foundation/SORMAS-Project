package de.symeda.sormas.app.event;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;

import androidx.databinding.Observable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventCriteria;
import de.symeda.sormas.app.component.controls.ControlButton;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogEventPickOrCreateLayoutBinding;
import de.symeda.sormas.app.databinding.DialogRootCancelCreateSelectButtonPanelLayoutBinding;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.ViewHelper;

public class EventPickOrCreateDialog extends AbstractDialog {

	public static final String TAG = EventPickOrCreateDialog.class.getSimpleName();

	private DialogEventPickOrCreateLayoutBinding contentBinding;

	private EventCriteria eventCriteria;
	private List<Event> eventSelectList;
	private Event event;

	private IEntryItemOnClickListener eventSelectItemClickCallback;
	private Callback createCallback;
	private final ObservableField<Event> selectedEvent = new ObservableField<>();

	public static void pickOrCreateEvent(final Case rootCase, final Event newEvent, final Consumer<Event> pickedEventCallback) {
		final EventPickOrCreateDialog eventDialog = new EventPickOrCreateDialog(rootCase, BaseActivity.getActiveActivity(), newEvent);

		if (!eventDialog.hasSelectableEvents()) {
			pickedEventCallback.accept(newEvent);
			return;
		}

		//existing event
		eventDialog.setPositiveCallback(() -> {
			pickedEventCallback.accept(eventDialog.getSelectedEvent() != null ? eventDialog.getSelectedEvent() : newEvent);
		});

		//new event
		eventDialog.createCallback = () -> {
			eventDialog.dismiss();
			pickedEventCallback.accept(newEvent);
		};

		eventDialog.show();
	}

	// Contructors

	private EventPickOrCreateDialog(Case rootCase, final FragmentActivity activity, Event newEvent) {

		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_event_pick_or_create_layout,
			R.layout.dialog_root_cancel_create_select_button_panel_layout,
			R.string.heading_pick_or_create_event,
			-1);

		this.event = newEvent;
		EventCriteria eventCriteria = new EventCriteria();
		eventCriteria.setDisease(rootCase.getDisease());
		eventCriteria.caze(rootCase);
		this.eventCriteria = eventCriteria;

		this.setSelectedEvent(null);
	}

	// Instance methods

	private boolean hasSelectableEvents() {
		updateEventSelectList();
		return !eventSelectList.isEmpty();
	}

	private void updateEventSelectList() {
		eventSelectList = DatabaseHelper.getEventDao().queryByCriteria(eventCriteria, 0, 10);
	}

	private void setUpControlListeners() {
		eventSelectItemClickCallback = (v, item) -> {
			if (item == null) {
				return;
			}

			Event eventItem = (Event) item;
			String tag = "rowItemSelectOrCreateEvent";
			ArrayList<View> views = ViewHelper.getViewsByTag(contentBinding.existingEventsList, tag);
			setSelectedEvent(null);

			for (View itemView : views) {
				try {
					int itemViewId = itemView.getId();
					int vId = v.getId();

					if (itemViewId == vId && v.isSelected()) {
						itemView.setSelected(false);
					} else if (itemViewId == vId && !v.isSelected()) {
						itemView.setSelected(true);
						setSelectedEvent(eventItem);
					} else {
						itemView.setSelected(false);
					}
				} catch (NumberFormatException ex) {
					NotificationHelper.showDialogNotification(EventPickOrCreateDialog.this, NotificationType.ERROR, R.string.error_internal_error);
				}
			}
		};
	}

	private ObservableArrayList makeObservable(List<Event> events) {
		ObservableArrayList<Event> newList = new ObservableArrayList<>();
		newList.addAll(events);
		return newList;
	}

	// Overrides

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		this.contentBinding = (DialogEventPickOrCreateLayoutBinding) binding;

		setUpControlListeners();
		binding.setVariable(BR.data, event);
		binding.setVariable(BR.eventSelectList, makeObservable(eventSelectList));
		binding.setVariable(BR.eventSelectItemClickCallback, eventSelectItemClickCallback);

	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {

		this.selectedEvent.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {

			@Override
			public void onPropertyChanged(Observable sender, int propertyId) {
				ControlButton btnCreate = ((DialogRootCancelCreateSelectButtonPanelLayoutBinding) buttonPanelBinding).buttonCreate;
				ControlButton btnSelect = getPositiveButton();

				if (getSelectedEvent() == null) {
					btnCreate.setVisibility(View.VISIBLE);
					btnSelect.setVisibility(View.GONE);
				} else {
					btnCreate.setVisibility(View.GONE);
					btnSelect.setVisibility(View.VISIBLE);
				}
			}
		});

		this.selectedEvent.notifyChange();

		((DialogRootCancelCreateSelectButtonPanelLayoutBinding) buttonPanelBinding).buttonCreate.setOnClickListener(v -> createCallback.call());
	}

	@Override
	public int getPositiveButtonText() {
		return R.string.action_select;
	}

	@Override
	public int getNegativeButtonText() {
		return R.string.action_cancel;
	}

	// Getters & Setters

	private Event getSelectedEvent() {
		return selectedEvent.get();
	}

	private void setSelectedEvent(Event selectedEvent) {
		this.selectedEvent.set(selectedEvent);
	}
}
