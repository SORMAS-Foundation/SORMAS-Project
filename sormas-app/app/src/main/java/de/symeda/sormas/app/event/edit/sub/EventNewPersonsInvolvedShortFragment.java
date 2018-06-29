package de.symeda.sormas.app.event.edit.sub;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.AbstractSormasActivity;
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
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.ISaveableWithCallback;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentEventNewPersonShortLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.util.TimeoutHelper;
import de.symeda.sormas.app.validation.EventParticipantValidator;

/**
 * Created by Orson on 27/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventNewPersonsInvolvedShortFragment extends BaseEditActivityFragment<FragmentEventNewPersonShortLayoutBinding, EventParticipant, EventParticipant> implements ISaveableWithCallback {

    public static final String TAG = EventNewPersonsInvolvedShortFragment.class.getSimpleName();

    private AsyncTask onResumeTask;
    private AsyncTask checkExistingPersonTask;
    private AsyncTask createPersonTask;
    private AsyncTask saveParticipantTask;
    private EventStatus pageStatus = null;
    private String recordUuid = null;
    private String eventUuid = null;
    private EventParticipant record;

    private IOnShortEventParticipantSaved mOnShortEventParticipantSaved;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
        saveEventUuidState(outState, eventUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (EventStatus) getPageStatusArg(arguments);
        eventUuid = getEventUuidArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();

        return String.format(r.getString(R.string.heading_sub_event_person_involved_new), DataHelper.getShortUuid(eventUuid));
    }

    @Override
    public EventParticipant getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            EventParticipant eventParticipant = getActivityRootData();
            resultHolder.forItem().add(eventParticipant);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (record == null)
                getActivity().finish();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEventNewPersonShortLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventNewPersonShortLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentEventNewPersonShortLayoutBinding contentBinding, EventParticipant eventParticipant) {

    }

    @Override
    public void onPageResume(FragmentEventNewPersonShortLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();
                }

                @Override
                public void execute(TaskResultHolder resultHolder) {
                    EventParticipant eventParticipant = getActivityRootData();
                    resultHolder.forItem().add(eventParticipant);
                }
            };
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record = itemIterator.next();

                    if (record != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_event_new_person_short_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public boolean showSaveAction() {
        return true;
    }

    @Override
    public boolean showAddAction() {
        return false;
    }

    public void setOnShortEventParticipantSaved(IOnShortEventParticipantSaved onShortEventParticipantSaved) {
        this.mOnShortEventParticipantSaved = onShortEventParticipantSaved;
    }

    public static EventNewPersonsInvolvedShortFragment newInstance(IActivityCommunicator activityCommunicator, EventFormNavigationCapsule capsule, EventParticipant activityRootData) {
        return newInstance(activityCommunicator, EventNewPersonsInvolvedShortFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);

        if (saveParticipantTask != null && !saveParticipantTask.isCancelled())
            saveParticipantTask.cancel(true);

        if (checkExistingPersonTask != null && !checkExistingPersonTask.isCancelled())
            checkExistingPersonTask.cancel(true);

        if (createPersonTask != null && !createPersonTask.isCancelled())
            createPersonTask.cancel(true);
    }

    @Override
    public void save(final NotificationContext nContext, final Callback.IAction callback) {
        final EventParticipant eventParticipantToSave = getActivityRootData();

        if (eventParticipantToSave == null)
            throw new IllegalArgumentException("eventParticipantToSave is null");

        // Validation
        EventParticipantValidator.clearErrorsForNewEventParticipant(getContentBinding());
        if (!EventParticipantValidator.validateNewEvent(nContext, eventParticipantToSave, getContentBinding())) {
            return;
        }

        checkExistingPersons(nContext, eventParticipantToSave, new Callback.IAction<List<Person>>() {
            @Override
            public void call(List<Person> existingPersons) {
                if (existingPersons.size() > 0) {
                    final SelectOrCreatePersonDialog personDialog = new SelectOrCreatePersonDialog(AbstractSormasActivity.getActiveActivity(), eventParticipantToSave.getPerson(), existingPersons);
                    personDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                        @Override
                        public void onOkClick(View v, Object item, View viewRoot) {
                            personDialog.dismiss();

                            //Select
                            if (item instanceof Person) {
                                eventParticipantToSave.setPerson((Person)item);
                                savePersonAndEventParticipant(nContext, eventParticipantToSave, callback);
                            }

                        }
                    });

                    personDialog.setOnCreateClickListener(new TeboAlertDialogInterface.CreateOnClickListener() {
                        @Override
                        public void onCreateClick(View v, Object item, View viewRoot) {
                            personDialog.dismiss();

                            if (item instanceof Person) {
                                eventParticipantToSave.setPerson((Person)item);
                                savePersonAndEventParticipant(nContext, eventParticipantToSave, callback);
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
                    savePersonAndEventParticipant(nContext, eventParticipantToSave, callback);
                }

            }
        });
    }

    private void checkExistingPersons(final NotificationContext nContext, final EventParticipant eventParticipantToSave, final Callback.IAction<List<Person>> callback) {
        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {

                @Override
                public void onPreExecute() {
                }

                @Override
                public void execute(TaskResultHolder resultHolder) {
//                    List<Person> existingPersons = DatabaseHelper.getPersonDao()
//                            .getAllByName(eventParticipantToSave.getPerson().getFirstName(),
//                            eventParticipantToSave.getPerson().getLastName());
//                    resultHolder.forList().add(existingPersons);
                }
            };
            checkExistingPersonTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null || !resultStatus.isSuccess()){
                        NotificationHelper.showNotification(nContext, NotificationType.ERROR,
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
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }

    }

    private void savePersonAndEventParticipant(final NotificationContext nContext, final EventParticipant eventParticipantToSave, final Callback.IAction callback) {
        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                private PersonDao personDao;
                private String saveUnsuccessful;

                @Override
                public void onPreExecute() {
                    getActivityCommunicator().showPreloader();
                    getActivityCommunicator().hideFragmentView();

                    personDao = DatabaseHelper.getPersonDao();
                    saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_event_person));
                }

                @Override
                public void execute(TaskResultHolder resultHolder) {
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
                    getActivityCommunicator().hidePreloader();
                    getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    EventParticipant savedRecord = null;
                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        savedRecord = itemIterator.next();

                    if (!resultStatus.isSuccess()) {
                        NotificationHelper.showNotification(nContext, NotificationType.ERROR, resultStatus.getMessage());
                        return;
                    }

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.Changes, getContext(), new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    NotificationHelper.showNotification(nContext, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_created), getResources().getString(R.string.entity_event_person)));
                                } else {
                                    NotificationHelper.showNotification(nContext, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_create_success), getResources().getString(R.string.entity_event_person)));
                                }
                                //finish();
                            }
                        });
                    } else {
                        NotificationHelper.showNotification(nContext, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_created), getResources().getString(R.string.entity_event_person)));
                        //finish();
                    }

                    TimeoutHelper.executeIn5Seconds(new Callback.IAction<AsyncTask>() {
                        @Override
                        public void call(AsyncTask result) {
                            if (mOnShortEventParticipantSaved != null)
                                mOnShortEventParticipantSaved.onSaved(eventParticipantToSave);
                            //goToNewEventParticipantFullView();
                            if (callback != null)
                                callback.call(null);
                        }
                    });
                }
            });
        } catch (Exception ex) {
            getActivityCommunicator().hidePreloader();
            getActivityCommunicator().showFragmentView();
        }
    }


}
