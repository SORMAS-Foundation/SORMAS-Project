package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.controls.ControlButton;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogSelectOrCreatePersonLayoutBinding;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.ViewHelper;

public class SelectOrCreatePersonDialog extends BaseTeboAlertDialog {

    public static final String TAG = SelectOrCreatePersonDialog.class.getSimpleName();

    private Person person;
    private List<PersonNameDto> existingPersons;
    private List<Person> similarPersons;

    private DialogSelectOrCreatePersonLayoutBinding binding;
    private IEntryItemOnClickListener updateSearchCallback;
    private IEntryItemOnClickListener availablePersonItemClickCallback;
    private final ObservableField<Person> selectedPerson = new ObservableField<>();

    public static void selectOrCreatePerson(Person person, final Consumer<Person> resultConsumer) {

        final SelectOrCreatePersonDialog personDialog = new SelectOrCreatePersonDialog(BaseActivity.getActiveActivity(), person);

        if (!personDialog.hasSimilarPersons()) {
            resultConsumer.accept(person);
            return;
        }

        personDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
            @Override
            public void onOkClick(View v, Object item, View viewRoot) {
                personDialog.dismiss();
                resultConsumer.accept((Person) item);
            }
        });

        personDialog.setOnCreateClickListener(new TeboAlertDialogInterface.CreateOnClickListener() {
            @Override
            public void onCreateClick(View v, Object item, View viewRoot) {
                personDialog.dismiss();
                resultConsumer.accept((Person) item);
            }
        });

        personDialog.setOnCancelClickListener(new TeboAlertDialogInterface.CancelOnClickListener() {

            @Override
            public void onCancelClick(View v, Object item, View viewRoot) {
                personDialog.dismiss();
            }
        });

        personDialog.show(null);
    }

    public SelectOrCreatePersonDialog(final FragmentActivity activity, Person person) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_select_or_create_person_layout,
                R.layout.dialog_root_cancel_create_select_button_panel_layout, R.string.heading_pick_or_create_person_dialog, -1);

        this.person = person;
        this.setSelectedPerson(null);

        setupCallback();
    }

    public boolean hasSimilarPersons() {
        if (similarPersons == null) {
            updateSimilarPersons();
        }
        return !similarPersons.isEmpty();
    }

    private void updateSimilarPersons() {

        if (existingPersons == null) {
            existingPersons = DatabaseHelper.getPersonDao().getPersonNameDtos();
        }

        similarPersons = new ArrayList<>();
        for (PersonNameDto existingPerson : existingPersons) {
            if (PersonHelper.areNamesSimilar(person.getFirstName() + " " + person.getLastName(),
                    existingPerson.getFirstName() + " " + existingPerson.getLastName())) {
                Person similarPerson = DatabaseHelper.getPersonDao().queryForId(existingPerson.getId());
                similarPersons.add(similarPerson);
            }
        }
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, final Callback.IAction callback) {
        if (getSelectedPerson() != null && getSelectedPerson().getUuid() != person.getUuid()) {
            if (callback != null)
                callback.call(getSelectedPerson());
        } else {
            NotificationHelper.showDialogNotification((NotificationContext)this, NotificationType.ERROR, R.string.snackbar_select_create_person);
            if (callback != null)
                callback.call(null);
        }
    }

    @Override
    protected void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onCancelClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void onCreateClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {

        if (binding.personFirstName.getValue().isEmpty() || binding.personLastName.getValue().isEmpty()) {
            NotificationHelper.showDialogNotification((NotificationContext)this, NotificationType.ERROR, R.string.snackbar_person_first_last_name);

            if (callback != null)
                callback.call(null);
        } else {
            person.setFirstName(binding.personFirstName.getValue());
            person.setLastName(binding.personLastName.getValue());

            if (callback != null)
                callback.call(person);
        }
    }

    @Override
    protected void recieveViewDataBinding(Context context, ViewDataBinding binding) {
        this.binding = (DialogSelectOrCreatePersonLayoutBinding)binding;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, person)) {
            Log.e(TAG, "There is no variable 'person' in layout " + layoutName);
        }

        if (!(binding instanceof DialogSelectOrCreatePersonLayoutBinding))
            return;

        if (!binding.setVariable(BR.availablePersons, makeObservable(similarPersons))) {
            Log.e(TAG, "There is no variable 'availablePersons' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.updateSearchCallback, updateSearchCallback)) {
            Log.e(TAG, "There is no variable 'updateSearchCallback' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.availablePersonItemClickCallback, availablePersonItemClickCallback)) {
            Log.e(TAG, "There is no variable 'availablePersonItemClickCallback' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeData(TaskResultHolder resultHolder, boolean executionComplete) {
        // TODO remove
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, final ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        this.selectedPerson.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {

                ControlButton btnCreate = getCreateButton();
                ControlButton btnSelect = getOkButton();

                if (getSelectedPerson() == null) {
                    if (btnCreate != null)
                        btnCreate.setVisibility(View.VISIBLE);

                    if (btnSelect != null)
                        btnSelect.setVisibility(View.GONE);
                } else {
                    if (btnCreate != null)
                        btnCreate.setVisibility(View.GONE);

                    if (btnSelect != null)
                        btnSelect.setVisibility(View.VISIBLE);
                }
            }
        });
        this.selectedPerson.notifyChange();
    }

    @Override
    public boolean isOkButtonVisible() {
        return true;
    }

    @Override
    public boolean isCancelButtonVisible() {
        return true;
    }

    @Override
    public boolean isCreateButtonVisible() {
        return true;
    }

    @Override
    public boolean isRounded() {
        return true;
    }

    @Override
    public int getPositiveButtonText() {
        return R.string.action_select;
    }

    private Person getSelectedPerson() {
        return selectedPerson.get();
    }

    private void setSelectedPerson(Person selectedPerson) {
        this.selectedPerson.set(selectedPerson);
    }

    private void setupCallback () {
        updateSearchCallback = new IEntryItemOnClickListener() {

            @Override
            public void onClick(View v, Object item) {
                // update person name from the fields
                person.setFirstName(binding.personFirstName.getValue());
                person.setLastName(binding.personLastName.getValue());

                updateSimilarPersons();
                binding.setAvailablePersons(makeObservable(similarPersons)); // why observable?
                setSelectedPerson(null);
            }
        };

        availablePersonItemClickCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (item == null)
                    return;

                Person personItem = (Person)item;
                String tag = getActivity().getResources().getString(R.string.tag_row_item_select_or_create_person);
                ArrayList<View> views = ViewHelper.getViewsByTag(binding.listExistingPersons, tag);
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
                        NotificationHelper.showDialogNotification((NotificationContext)SelectOrCreatePersonDialog.this, NotificationType.ERROR, R.string.notification_internal_error);
                    }
                }
            }
        };
    }

    private ObservableArrayList makeObservable(List<Person> persons) {
        ObservableArrayList newList = new ObservableArrayList();
        if (persons == null || persons.size() <= 0) {
            binding.pickOrCreatePersonDescription.setVisibility(View.GONE);
            binding.noRecordsDescription.setVisibility(View.VISIBLE);
            newList.addAll(new ArrayList<Person>());
        } else {
            binding.pickOrCreatePersonDescription.setVisibility(View.VISIBLE);
            binding.noRecordsDescription.setVisibility(View.GONE);
            newList.addAll(persons);
        }
        return newList;
    }
}
