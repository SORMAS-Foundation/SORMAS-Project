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

package de.symeda.sormas.app.person;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import android.content.Context;
import android.view.View;

import androidx.databinding.Observable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.controls.ControlButton;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogRootCancelCreateSelectButtonPanelLayoutBinding;
import de.symeda.sormas.app.databinding.DialogSelectOrCreatePersonLayoutBinding;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.ViewHelper;

public class SelectOrCreatePersonDialog extends AbstractDialog {

	public static final String TAG = SelectOrCreatePersonDialog.class.getSimpleName();

	private DialogSelectOrCreatePersonLayoutBinding contentBinding;

	private Person person;
	private PersonSimilarityCriteria similarityCriteria;
	private List<Person> similarPersons;

	private IEntryItemOnClickListener availablePersonItemClickCallback;
	private Callback createCallback;
	private final ObservableField<Person> selectedPerson = new ObservableField<>();

	// Static methods

	public static void selectOrCreatePerson(final Person person, final Consumer<Person> resultConsumer) {
		final SelectOrCreatePersonDialog personDialog = new SelectOrCreatePersonDialog(BaseActivity.getActiveActivity(), person);

		if (!personDialog.hasSimilarPersons()) {
			resultConsumer.accept(person);
			return;
		}

		personDialog.setPositiveCallback(() -> {
			if (personDialog.getSelectedPerson() != null && !personDialog.getSelectedPerson().getUuid().equals(person.getUuid())) {
				resultConsumer.accept(personDialog.getSelectedPerson());
			} else {
				personDialog.suppressNextDismiss();
				NotificationHelper.showDialogNotification(personDialog, NotificationType.ERROR, R.string.info_select_create_person);
			}
		});

		personDialog.createCallback = () -> {
			personDialog.dismiss();
			resultConsumer.accept(person);
		};

		personDialog.show();
	}

	// Constructors

	private SelectOrCreatePersonDialog(final FragmentActivity activity, Person person) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_select_or_create_person_layout,
			R.layout.dialog_root_cancel_create_select_button_panel_layout,
			R.string.heading_pick_or_create_person,
			-1);

		this.person = person;
		this.similarityCriteria = new PersonSimilarityCriteria().sex(person.getSex())
			.birthdateYYYY(person.getBirthdateYYYY())
			.birthdateMM(person.getBirthdateMM())
			.birthdateDD(person.getBirthdateDD());
		this.setSelectedPerson(null);
	}

	// Instance methods

	private boolean hasSimilarPersons() {
		if (similarPersons == null) {
			updateSimilarPersons();
		}

		return !similarPersons.isEmpty();
	}

	private void updateSimilarPersons() {
		List<String> similarNameUuids = DatabaseHelper.getPersonDao()
			.getRelevantPersonNames(similarityCriteria)
			.stream()
			.filter(
				name -> PersonHelper
					.areNamesSimilar(person.getFirstName(), person.getLastName(), name.getFirstName(), name.getLastName()))
			.map(PersonNameDto::getUuid)
			.collect(Collectors.toList());

		if (!similarNameUuids.isEmpty()) {
			similarPersons = DatabaseHelper.getPersonDao().queryUuids(similarNameUuids);
		} else {
			similarPersons = new ArrayList<>();
		}
	}

	private void setUpControlListeners() {
		availablePersonItemClickCallback = (v, item) -> {
			if (item == null) {
				return;
			}

			Person personItem = (Person) item;
			String tag = getActivity().getResources().getString(R.string.tag_row_item_select_or_create_person);
			ArrayList<View> views = ViewHelper.getViewsByTag(contentBinding.existingPersonsList, tag);
			setSelectedPerson(null);

			for (View itemView : views) {
				try {
					int itemViewId = itemView.getId();
					int vId = v.getId();

					if (itemViewId == vId && v.isSelected()) {
						itemView.setSelected(false);
					} else if (itemViewId == vId && !v.isSelected()) {
						itemView.setSelected(true);
						setSelectedPerson(personItem);
					} else {
						itemView.setSelected(false);
					}
				} catch (NumberFormatException ex) {
					NotificationHelper.showDialogNotification(SelectOrCreatePersonDialog.this, NotificationType.ERROR, R.string.error_internal_error);
				}
			}
		};
	}

	private ObservableArrayList makeObservable(List<Person> persons) {
		ObservableArrayList<Person> newList = new ObservableArrayList<>();
		newList.addAll(persons);
		return newList;
	}

	// Overrides

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		this.contentBinding = (DialogSelectOrCreatePersonLayoutBinding) binding;
		// Needs to be done here because callback needs to be initialized before being bound to the layout
		setUpControlListeners();
		binding.setVariable(BR.data, person);
		binding.setVariable(BR.availablePersons, makeObservable(similarPersons));
		binding.setVariable(BR.availablePersonItemClickCallback, availablePersonItemClickCallback);
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, final ViewDataBinding buttonPanelBinding) {
		this.selectedPerson.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {

			@Override
			public void onPropertyChanged(Observable observable, int i) {
				ControlButton btnCreate = ((DialogRootCancelCreateSelectButtonPanelLayoutBinding) buttonPanelBinding).buttonCreate;
				ControlButton btnSelect = getPositiveButton();

				if (getSelectedPerson() == null) {
					btnCreate.setVisibility(View.VISIBLE);
					btnSelect.setVisibility(View.GONE);
				} else {
					btnCreate.setVisibility(View.GONE);
					btnSelect.setVisibility(View.VISIBLE);
				}
			}
		});

		this.selectedPerson.notifyChange();

		((DialogRootCancelCreateSelectButtonPanelLayoutBinding) buttonPanelBinding).buttonCreate.setOnClickListener(v -> createCallback.call());
	}

	@Override
	public boolean isRounded() {
		return true;
	}

	@Override
	public int getPositiveButtonText() {
		return R.string.action_select;
	}

	@Override
	public int getNegativeButtonText() {
		return R.string.action_cancel;
	}

	// Getters & setters

	private Person getSelectedPerson() {
		return selectedPerson.get();
	}

	private void setSelectedPerson(Person selectedPerson) {
		this.selectedPerson.set(selectedPerson);
	}
}
