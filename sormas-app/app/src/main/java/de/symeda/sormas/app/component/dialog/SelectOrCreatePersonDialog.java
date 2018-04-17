package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.TeboButton;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogSelectOrCreatePersonLayoutBinding;
import de.symeda.sormas.app.util.ViewHelper;

/**
 * Created by Orson on 25/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SelectOrCreatePersonDialog extends BaseTeboAlertDialog {

    public static final String TAG = SelectOrCreatePersonDialog.class.getSimpleName();

    private AsyncTask selectOrCreateTask;
    private Person data;
    private List<Person> existingPersons;

    private final Tracker tracker;
    private DialogSelectOrCreatePersonLayoutBinding binding;
    private IEntryItemOnClickListener updateSearchCallback;
    private IEntryItemOnClickListener availablePersonItemClickCallback;
    public final ObservableField<Person> selectedPerson = new ObservableField<>();



    public SelectOrCreatePersonDialog(final FragmentActivity activity, Person person, List<Person> existingPersons) {
        this(activity, R.string.heading_pick_or_create_person_dialog, -1, person, existingPersons);
    }

    public SelectOrCreatePersonDialog(final FragmentActivity activity, int headingResId, int subHeadingResId,
            Person person, List<Person> existingPersons) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_select_or_create_person_layout,
                R.layout.dialog_root_cancel_create_select_button_panel_layout, headingResId, subHeadingResId);




        this.data = person;
        this.setSelectedPerson(null);
        this.existingPersons = existingPersons;
        this.tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();

        setupCallback ();
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, final Callback.IAction callback) {
        if (getSelectedPerson() != null && getSelectedPerson().getUuid() != data.getUuid()) {
            if (callback != null)
                callback.call(getSelectedPerson());
        } else {
            NotificationHelper.showDialogNotification((INotificationContext)this, NotificationType.ERROR, R.string.snackbar_select_create_person);
        }

        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        if (selectOrCreateTask != null && !selectOrCreateTask.isCancelled())
            selectOrCreateTask.cancel(true);

        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void onCancelClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void onCreateClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        //TODO: Find out what's going on here
        if (callback != null)
            callback.call(getSelectedPerson());

        if (binding.txtFirstName.getValue().isEmpty() || binding.txtLastName.getValue().isEmpty()) {
            NotificationHelper.showDialogNotification((INotificationContext)this, NotificationType.ERROR, R.string.snackbar_person_first_last_name);
        } else {
            data.setFirstName(binding.txtFirstName.getValue());
            data.setLastName(binding.txtLastName.getValue());

            if (callback != null)
                callback.call(data);
        }

        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void recieveViewDataBinding(Context context, ViewDataBinding binding) {
        this.binding = (DialogSelectOrCreatePersonLayoutBinding)binding;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }

        if (!(binding instanceof DialogSelectOrCreatePersonLayoutBinding))
            return;

        if (!binding.setVariable(BR.availablePersons, getExisitingPersons(existingPersons))) {
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
        if (!executionComplete) {

        } else {

        }
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, final ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        this.selectedPerson.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {

                TeboButton btnCreate = getCreateButton();
                TeboButton btnSelect = getOkButton();

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
                data.setFirstName(binding.txtFirstName.getValue());
                data.setLastName(binding.txtLastName.getValue());

                List<Person> foundPersons = DatabaseHelper.getPersonDao().getAllByName(data.getFirstName(), data.getLastName());
                ObservableArrayList existingPersons = getExisitingPersons(foundPersons);
                binding.setAvailablePersons(existingPersons);
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

                for (View itemView : views) {
                    if (itemView == v && v.isSelected()) {
                        itemView.setSelected(false);
                        setSelectedPerson(null);
                    } else if (itemView == v && !v.isSelected()) {
                        itemView.setSelected(true);
                        setSelectedPerson(personItem);
                    } else {
                        itemView.setSelected(false);
                        setSelectedPerson(null);
                    }
                }
            }
        };
    }

    private ObservableArrayList getExisitingPersons(List<Person> persons) {
        ObservableArrayList newList = new ObservableArrayList();

        if (persons == null || persons.size() <= 0) {
            binding.txtSelectOrCreatePersonTitle.setVisibility(View.GONE);
            binding.txtNoRecordsTitle.setVisibility(View.VISIBLE);

            newList.addAll(new ArrayList<Person>());
        } else {
            binding.txtSelectOrCreatePersonTitle.setVisibility(View.VISIBLE);
            binding.txtNoRecordsTitle.setVisibility(View.GONE);

            newList.addAll(persons);
        }

        return newList;
    }

}
