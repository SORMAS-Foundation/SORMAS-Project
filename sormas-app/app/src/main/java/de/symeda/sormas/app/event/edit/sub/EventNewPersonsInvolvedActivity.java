package de.symeda.sormas.app.event.edit.sub;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.AbstractSormasActivity;
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
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.ICallback;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.NavigationHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.util.TimeoutHelper;

/**
 * Created by Orson on 27/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventNewPersonsInvolvedActivity extends BaseEditActivity<EventParticipant> {

    public static final String TAG = EventNewPersonsInvolvedActivity.class.getSimpleName();

    private AsyncTask saveTask;
    private AsyncTask createPersonTask;
    private EventStatus pageStatus = null;
    private String recordUuid = null;
    private String eventUuid = null;
    private int activeMenuKey = ConstantHelper.INDEX_FIRST_MENU;
    private BaseEditActivityFragment activeFragment = null;
    private MenuItem saveMenu = null;
    private MenuItem addMenu = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
        SaveEventUuidState(outState, eventUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        pageStatus = (EventStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
        eventUuid = getEventUuidArg(arguments);
    }

    @Override
    protected EventParticipant getActivityRootData(String recordUuid) {
        return null;
    }

    @Override
    protected EventParticipant getActivityRootDataIfRecordUuidNull() {
        Person person = DatabaseHelper.getPersonDao().build();
        EventParticipant eventParticipant = DatabaseHelper.getEventParticipantDao().build();
        eventParticipant.setPerson(person);

        return eventParticipant;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment(EventParticipant activityRootData) throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            EventFormNavigationCapsule dataCapsule = (EventFormNavigationCapsule)new EventFormNavigationCapsule(EventNewPersonsInvolvedActivity.this,
                    recordUuid, pageStatus).setEventUuid(eventUuid);
            activeFragment = EventNewPersonsInvolvedShortFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_case);

        return true;
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_menu, menu);

        saveMenu = menu.findItem(R.id.action_save);
        addMenu = menu.findItem(R.id.action_new);

        saveMenu.setTitle(R.string.action_save_case);

        processActionbarMenu();

        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavigationHelper.navigateUpFrom(this);
                return true;

            case R.id.action_save:
                saveData();
                return true;

            case R.id.option_menu_action_sync:
                //synchronizeChangedData();
                return true;

            case R.id.option_menu_action_markAllAsRead:
                /*CaseDao caseDao = DatabaseHelper.getCaseDao();
                PersonDao personDao = DatabaseHelper.getPersonDao();
                List<Case> cases = caseDao.queryForAll();
                for (Case caseToMark : cases) {
                    caseDao.markAsRead(caseToMark);
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof CasesListFragment) {
                        fragment.onResume();
                    }
                }*/
                return true;

            // Report problem button
            case R.id.action_report:
                /*UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();*/

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_person_involved_new;
    }

    private void checkExistingPersons(final ICallback<List<Person>> callback) {
        if (activeFragment == null)
            return;

        final EventParticipant eventParticipant = getStoredActivityRootData();

        if (eventParticipant == null)
            return;

        //TODO: Validation
        /*CaseValidator.clearErrorsForNewCase(binding);
        if (!CaseValidator.validateNewCase(caze, binding)) {
            return true;
        }*/

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    List<Person> existingPersons = DatabaseHelper.getPersonDao().getAllByName(eventParticipant.getPerson().getFirstName(), eventParticipant.getPerson().getLastName());
                    resultHolder.forList().add(existingPersons);
                }
            });
            saveTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null || !resultStatus.isSuccess()){
                        NotificationHelper.showNotification(EventNewPersonsInvolvedActivity.this, NotificationType.ERROR, String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_event_person)));
                        return;
                    }

                    List<Person> existingPersons = new ArrayList<>();
                    ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

                    if (listIterator.hasNext())
                        existingPersons = listIterator.next();

                    callback.result(existingPersons);
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }

    }

    //TODO: Delegate saving to fragments
    private void saveData()  {
        if (activeFragment == null)
            return;

        final EventParticipant recordToSave = getStoredActivityRootData();

        if (recordToSave == null)
            return;

        //TODO: Fix bad code
        if (activeFragment instanceof EventNewPersonsInvolvedFullFragment) {
            updateEventParticipantPerson();
            return;
        }


        // Validation
        /*EventParticipantNewFragmentLayoutBinding binding = eventParticipantNewPersonForm.getBinding();
        EventParticipantValidator.clearErrorsForNewEventParticipant(binding);
        if (!EventParticipantValidator.validateNewEvent(eventParticipant, binding)) {
            return true;
        }*/

        checkExistingPersons(new ICallback<List<Person>>() {
            @Override
            public void result(List<Person> existingPersons) {
                if (existingPersons.size() > 0) {
                    final SelectOrCreatePersonDialog personDialog = new SelectOrCreatePersonDialog(AbstractSormasActivity.getActiveActivity(), recordToSave.getPerson(), existingPersons);
                    personDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                        @Override
                        public void onOkClick(View v, Object item, View viewRoot) {
                            personDialog.dismiss();

                            //Select
                            if (item instanceof Person) {
                                recordToSave.setPerson((Person)item);
                                savePersonAndEventParticipant(recordToSave);
                            }

                        }
                    });

                    personDialog.setOnCreateClickListener(new TeboAlertDialogInterface.CreateOnClickListener() {
                        @Override
                        public void onCreateClick(View v, Object item, View viewRoot) {
                            personDialog.dismiss();

                            if (item instanceof Person) {
                                recordToSave.setPerson((Person)item);
                                savePersonAndEventParticipant(recordToSave);
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
                    savePersonAndEventParticipant(recordToSave);
                }

            }
        });
    }

    private void savePersonAndEventParticipant(final EventParticipant recordToSave) {
        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                private PersonDao personDao;
                private String saveUnsuccessful;

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    showPreloader();
                    hideFragmentView();

                    personDao = DatabaseHelper.getPersonDao();
                    saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_event_person));
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    try {
                        // save the person
                        personDao.saveAndSnapshot(recordToSave.getPerson());
                        // set the given event
                        final Event event = DatabaseHelper.getEventDao().queryUuid(eventUuid);
                        recordToSave.setEvent(event);
                        // save the contact
                        EventParticipantDao eventParticipantDao = DatabaseHelper.getEventParticipantDao();
                        eventParticipantDao.saveAndSnapshot(recordToSave);

                        resultHolder.forItem().add(recordToSave);
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save case", e);
                        resultHolder.setResultStatus(new BoolResult(false, saveUnsuccessful));
                        ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
                    }
                }
            });
            createPersonTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    hidePreloader();
                    showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    EventParticipant savedRecord = null;
                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        savedRecord = itemIterator.next();

                    if (!resultStatus.isSuccess()) {
                        NotificationHelper.showNotification(EventNewPersonsInvolvedActivity.this, NotificationType.ERROR, resultStatus.getMessage());
                        return;
                    }

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, EventNewPersonsInvolvedActivity.this, new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    NotificationHelper.showNotification(EventNewPersonsInvolvedActivity.this, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_created), getResources().getString(R.string.entity_event_person)));
                                } else {
                                    NotificationHelper.showNotification(EventNewPersonsInvolvedActivity.this, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_create_success), getResources().getString(R.string.entity_event_person)));
                                }
                                //finish();
                            }
                        });
                    } else {
                        NotificationHelper.showNotification(EventNewPersonsInvolvedActivity.this, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_created), getResources().getString(R.string.entity_event_person)));
                        //finish();
                    }

                    TimeoutHelper.executeIn5Seconds(new ICallback<AsyncTask>() {
                        @Override
                        public void result(AsyncTask result) {
                            goToNewEventParticipantFullView();
                        }
                    });
                }
            });
        } catch (Exception ex) {
            hidePreloader();
            showFragmentView();
        }
    }

    private void goToNewEventParticipantFullView() {
        try {
            EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(EventNewPersonsInvolvedActivity.this,
                    recordUuid, pageStatus).setEventUuid(eventUuid);
            activeFragment = EventNewPersonsInvolvedFullFragment.newInstance(this, dataCapsule, getStoredActivityRootData());
            changeFragment(activeFragment);
        } catch (InstantiationException ex) {
            Log.e(TAG, ex.getMessage());
        } catch (IllegalAccessException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void updateEventParticipantPerson() {
        if (activeFragment == null)
            return;

        final EventParticipant recordToSave = getStoredActivityRootData();

        if (recordToSave == null)
            return;

        //TODO: Validation
        /*EventParticipantValidator.clearErrorsForEventParticipantData(eventParticipantBinding);
        PersonValidator.clearErrors(personBinding);

        boolean validationError = false;

        if (!PersonValidator.validatePersonData(person, personBinding)) {
            validationError = true;
        }
        if (!EventParticipantValidator.validateEventParticipantData(eventParticipant, eventParticipantBinding)) {
            validationError = true;
        }

        if (validationError) {
            return true;
        }*/



        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                private PersonDao personDao;
                private EventParticipantDao eventParticipantDao;
                private String saveUnsuccessful;

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    personDao = DatabaseHelper.getPersonDao();
                    eventParticipantDao = DatabaseHelper.getEventParticipantDao();
                    saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_event_person));
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    try {
                        personDao.saveAndSnapshot(recordToSave.getPerson());
                        recordToSave.setPerson(recordToSave.getPerson());
                        eventParticipantDao.saveAndSnapshot(recordToSave);
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save event person", e);
                        resultHolder.setResultStatus(new BoolResult(false, saveUnsuccessful));
                        ErrorReportingHelper.sendCaughtException(tracker, e, recordToSave, true);
                    }
                }
            });
            saveTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    if (!resultStatus.isSuccess()) {
                        NotificationHelper.showNotification(EventNewPersonsInvolvedActivity.this, NotificationType.ERROR, resultStatus.getMessage());
                        return;
                    }

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, EventNewPersonsInvolvedActivity.this, new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    NotificationHelper.showNotification(EventNewPersonsInvolvedActivity.this, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_event_person)));
                                } else {
                                    NotificationHelper.showNotification(EventNewPersonsInvolvedActivity.this, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_event_person)));
                                }
                                //finish();
                            }
                        });
                    } else {
                        NotificationHelper.showNotification(EventNewPersonsInvolvedActivity.this, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_event_person)));
                        //finish();
                    }

                    TimeoutHelper.executeIn5Seconds(new ICallback<AsyncTask>() {
                        @Override
                        public void result(AsyncTask result) {
                            EventNewPersonsInvolvedActivity.this.finish();
                        }
                    });
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (saveMenu != null)
            saveMenu.setVisible(activeFragment.showSaveAction());

        if (addMenu != null)
            addMenu.setVisible(activeFragment.showAddAction());
    }

    public static <TActivity extends AbstractSormasActivity> void
    goToActivity(Context fromActivity, EventFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, EventNewPersonsInvolvedActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);

        if (createPersonTask != null && !createPersonTask.isCancelled())
            createPersonTask.cancel(true);
    }
}
