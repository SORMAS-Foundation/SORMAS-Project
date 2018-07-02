package de.symeda.sormas.app.event.edit.sub;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.event.EventParticipantDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.component.dialog.SelectOrCreatePersonDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.MenuOptionsHelper;
import de.symeda.sormas.app.util.SyncCallback;

public class EventParticipantNewActivity extends BaseEditActivity<EventParticipant> {

    public static final String TAG = EventParticipantNewActivity.class.getSimpleName();

    private AsyncTask saveTask;
    private AsyncTask checkExistingPersonTask;
    private AsyncTask createPersonTask;

    private String eventUuid = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveEventUuidState(outState, eventUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        eventUuid = getEventUuidArg(savedInstanceState);
    }

    @Override
    protected EventParticipant queryActivityRootEntity(String recordUuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected EventParticipant buildActivityRootEntity() {
        Person person = DatabaseHelper.getPersonDao().build();
        EventParticipant eventParticipant = DatabaseHelper.getEventParticipantDao().build();
        eventParticipant.setPerson(person);
        return eventParticipant;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_case);

        return true;
    }

    @Override
    protected BaseEditActivityFragment buildEditFragment(LandingPageMenuItem menuItem, EventParticipant activityRootData) {
        EventParticipantFormNavigationCapsule dataCapsule = new EventParticipantFormNavigationCapsule(EventParticipantNewActivity.this,
                getRootEntityUuid()).setEventUuid(eventUuid);
        return EventParticipantNewFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleEditModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_person_involved_new;
    }

    @Override
    public EventStatus getPageStatus() {
        return (EventStatus)super.getPageStatus();
    }

    @Override
    public void saveData() {
        final EventParticipant eventParticipantToSave = (EventParticipant)getActiveFragment().getPrimaryData();

        // TODO validation
//        // Validation
//        EventParticipantValidator.clearErrorsForNewEventParticipant(getContentBinding());
//        if (!EventParticipantValidator.validateNewEvent(nContext, eventParticipantToSave, getContentBinding())) {
//            return;
//        }

        checkExistingPersons(eventParticipantToSave, new Callback.IAction<List<Person>>() {
            @Override
            public void call(List<Person> existingPersons) {
                if (existingPersons.size() > 0) {
                    final SelectOrCreatePersonDialog personDialog = new SelectOrCreatePersonDialog(BaseActivity.getActiveActivity(), eventParticipantToSave.getPerson(), existingPersons);
                    personDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                        @Override
                        public void onOkClick(View v, Object item, View viewRoot) {
                            personDialog.dismiss();

                            //Select
                            if (item instanceof Person) {
                                eventParticipantToSave.setPerson((Person)item);
                                savePersonAndEventParticipant(eventParticipantToSave);
                            }

                        }
                    });

                    personDialog.setOnCreateClickListener(new TeboAlertDialogInterface.CreateOnClickListener() {
                        @Override
                        public void onCreateClick(View v, Object item, View viewRoot) {
                            personDialog.dismiss();

                            if (item instanceof Person) {
                                eventParticipantToSave.setPerson((Person)item);
                                savePersonAndEventParticipant(eventParticipantToSave);
                            }
                        }
                    });

                    personDialog.setOnCancelClickListener(new TeboAlertDialogInterface.CancelOnClickListener() {

                        @Override
                        public void onCancelClick(View v, Object item, View viewRoot) {
                            personDialog.dismiss();
                        }
                    });

                    personDialog.show(null);


                } else {
                    savePersonAndEventParticipant(eventParticipantToSave);
                }

            }
        });
    }

    private void checkExistingPersons(final EventParticipant eventParticipantToSave, final Callback.IAction<List<Person>> callback) {
        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {

                @Override
                public void onPreExecute() {
                }

                @Override
                public void doInBackground(TaskResultHolder resultHolder) {
//                    List<Person> existingPersons = DatabaseHelper.getPersonDao()
//                            .getAllByName(eventParticipantToSave.getPerson().getFirstName(),
//                            eventParticipantToSave.getPerson().getLastName());
//                    resultHolder.forList().add(existingPersons);
                }
            };
            checkExistingPersonTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getBaseActivity().hidePreloader();
                    //getBaseActivity().showFragmentView();

                    if (resultHolder == null || !resultStatus.isSuccess()){
                        NotificationHelper.showNotification(EventParticipantNewActivity.this, NotificationType.ERROR,
                                String.format(getResources().getString(R.string.snackbar_create_error),
                                        getResources().getString(R.string.entity_event_person)));
                        return;
                    }

                    List<Person> existingPersons = new ArrayList<>();
                    ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

                    if (listIterator.hasNext())
                        existingPersons = listIterator.next();

                    callback.call(existingPersons);
                }
            });
        } catch (Exception ex) {
            //getBaseActivity().hidePreloader();
            //getBaseActivity().showFragmentView();
        }

    }

    private void savePersonAndEventParticipant(final EventParticipant eventParticipantToSave) {

        DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
            private PersonDao personDao;
            private String saveUnsuccessful;

            @Override
            public void onPreExecute() {
                showPreloader();

                personDao = DatabaseHelper.getPersonDao();
                saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_event_person));
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) {
                try {
                    // save the person
                    personDao.saveAndSnapshot(eventParticipantToSave.getPerson());
                    // set the given event
                    final Event event = DatabaseHelper.getEventDao().queryUuid(eventUuid);
                    eventParticipantToSave.setEvent(event);
                    // save the contact
                    EventParticipantDao eventParticipantDao = DatabaseHelper.getEventParticipantDao();
                    eventParticipantDao.saveAndSnapshot(eventParticipantToSave);

                    resultHolder.forItem().add(eventParticipantToSave);
                } catch (DaoException e) {
                    Log.e(getClass().getName(), "Error while trying to save case", e);
                    resultHolder.setResultStatus(new BoolResult(false, saveUnsuccessful));
                    ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
                }
            }
        };
        createPersonTask = executor.execute(new ITaskResultCallback() {
            @Override
            public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                hidePreloader();

                if (resultHolder == null) {
                    return;
                }

                EventParticipant savedRecord = null;
                ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                if (itemIterator.hasNext())
                    savedRecord = itemIterator.next();

                if (!resultStatus.isSuccess()) {
                    NotificationHelper.showNotification(EventParticipantNewActivity.this, NotificationType.ERROR, resultStatus.getMessage());
                    return;
                }

                if (RetroProvider.isConnected()) {
                    SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.Changes, getContext(), new SyncCallback() {
                        @Override
                        public void call(boolean syncFailed, String syncFailedMessage) {
                            if (syncFailed) {
                                NotificationHelper.showNotification(EventParticipantNewActivity.this, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_created), getResources().getString(R.string.entity_event_person)));
                            } else {
                                NotificationHelper.showNotification(EventParticipantNewActivity.this, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_create_success), getResources().getString(R.string.entity_event_person)));
                            }
                            //finish();
                        }
                    });
                } else {
                    NotificationHelper.showNotification(EventParticipantNewActivity.this, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_created), getResources().getString(R.string.entity_event_person)));
                    //finish();
                }

                goToEventParticipantEditActivity();
            }
        });
    }

    private void goToEventParticipantEditActivity() {
        EventParticipantFormNavigationCapsule dataCapsule = new EventParticipantFormNavigationCapsule(this, getRootEntityUuid());
        EventParticipantEditActivity.goToActivity(this, dataCapsule);
    }

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, EventFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, EventParticipantNewActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}
