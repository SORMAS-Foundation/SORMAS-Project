package de.symeda.sormas.app.event.edit;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.component.controls.ControlSwitchField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentEventEditLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.core.ISaveable;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.validation.EventValidator;

/**
 * Created by Orson on 07/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventEditFragment extends BaseEditActivityFragment<FragmentEventEditLayoutBinding, Event, Event> implements ISaveable {

    private AsyncTask onResumeTask;
    private AsyncTask saveEvent;
    private String recordUuid = null;
    private EventStatus pageStatus = null;
    private Event record;
    private IEntryItemOnClickListener onAddressLinkClickedCallback;

    private List<Item> diseaseList;
    private List<Item> typeOfPlaceList;

    private int mLastCheckedId = -1;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (EventStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_event_information);
    }

    @Override
    public Event getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Event event = getActivityRootData();

            if (event != null) {
                if (event.isUnreadOrChildUnread())
                    DatabaseHelper.getEventDao().markAsRead(event);
            }

            resultHolder.forItem().add(event);

            resultHolder.forOther().add(DataUtils.getEnumItems(Disease.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(TypeOfPlace.class, false));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (record == null)
                getActivity().finish();

            if (otherIterator.hasNext())
                diseaseList =  otherIterator.next();

            if (otherIterator.hasNext())
                typeOfPlaceList =  otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
        // init fields
        //toggleTypeOfPlaceTextField();

        EventValidator.setRequiredHintsForEventData(contentBinding);
        EventValidator.setSoftRequiredHintsForEventData(contentBinding);

        contentBinding.setData(record);
        contentBinding.setEventTypeClass(EventType.class);
//        contentBinding.setCheckedCallback(onEventTypeCheckedCallback);
        contentBinding.setAddressLinkCallback(onAddressLinkClickedCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
        contentBinding.spnDisease.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (diseaseList.size() > 0) ? DataUtils.addEmptyItem(diseaseList)
                        : diseaseList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                Disease disease = (Disease)value;

                if (disease == Disease.OTHER) {
                    getContentBinding().txtOtherDisease.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().txtOtherDisease.setVisibility(View.GONE);
                    getContentBinding().txtOtherDisease.setValue("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        contentBinding.spnTypeOfPlace.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (typeOfPlaceList.size() > 0) ? DataUtils.addEmptyItem(typeOfPlaceList)
                        : typeOfPlaceList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                toggleTypeOfPlaceTextField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        contentBinding.dtpDateOfAlert.setFragmentManager(getFragmentManager());
    }

    @Override
    protected void updateUI(FragmentEventEditLayoutBinding contentBinding, Event event) {
        contentBinding.spnDisease.setValue(event.getDisease(), true);
        contentBinding.spnTypeOfPlace.setValue(event.getTypeOfPlace(), true);
    }

    @Override
    public void onPageResume(FragmentEventEditLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
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
                    Event event = getActivityRootData();

                    if (event != null) {
                        if (event.isUnreadOrChildUnread())
                            DatabaseHelper.getEventDao().markAsRead(event);
                    }

                    resultHolder.forItem().add(event);
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
                        record =  itemIterator.next();

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
        return R.layout.fragment_event_edit_layout;
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

    private void setupCallback() {
//        onEventTypeCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(ControlSwitchField teboSwitch, Object checkedItem, int checkedId) {
//                if (mLastCheckedId == checkedId) {
//                    return;
//                }
//
//                mLastCheckedId = checkedId;
//
//
//
//            }
//        };

        onAddressLinkClickedCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final Location location = record.getEventLocation();
                final LocationDialog locationDialog = new LocationDialog(AbstractSormasActivity.getActiveActivity(), location);

                locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        getContentBinding().txtAddress.setValue(location.toString());
                        record.setEventLocation(location);

                        locationDialog.dismiss();
                    }
                });

                locationDialog.show(new Callback.IAction<AlertDialog>() {
                    @Override
                    public void call(AlertDialog result) {

                    }
                });
            }
        };
    }

    private void toggleTypeOfPlaceTextField() {
        TypeOfPlace typeOfPlace = (TypeOfPlace) record.getTypeOfPlace();
        if(typeOfPlace == TypeOfPlace.OTHER) {
            setFieldVisible(getContentBinding().txtOtherEventPlace, true);
        } else {
            // reset value
            getContentBinding().txtOtherEventPlace.setValue("");
            setFieldGone(getContentBinding().txtOtherEventPlace);
        }
    }

    public static EventEditFragment newInstance(IActivityCommunicator activityCommunicator, EventFormNavigationCapsule capsule, Event activityRootData) {
        return newInstance(activityCommunicator, EventEditFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);

        if (saveEvent != null && !saveEvent.isCancelled())
            saveEvent.cancel(true);
    }

    @Override
    public void save(final NotificationContext nContext) {
        final Event eventToSave = getActivityRootData();

        if (eventToSave == null)
            throw new IllegalArgumentException("eventToSave is null");

        EventValidator.clearErrorsForEventData(getContentBinding());
        if (!EventValidator.validateEventData(nContext, eventToSave, getContentBinding())) {
            return;
        }

        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                private EventDao dao;
                private String saveUnsuccessful;

                @Override
                public void onPreExecute() {
                    dao = DatabaseHelper.getEventDao();
                    saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_event));
                }

                @Override
                public void execute(TaskResultHolder resultHolder) {
                    try {
                        this.dao.saveAndSnapshot(eventToSave);
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save event", e);
                        resultHolder.setResultStatus(new BoolResult(false, saveUnsuccessful));
                        ErrorReportingHelper.sendCaughtException(tracker, e, eventToSave, true);
                    }
                }
            };
            saveEvent = executor.execute(new ITaskResultCallback() {
                private Event event;

                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    if (!resultStatus.isSuccess()) {
                        NotificationHelper.showNotification(nContext, NotificationType.ERROR, resultStatus.getMessage());
                        return;
                    } else {
                        NotificationHelper.showNotification(nContext, NotificationType.SUCCESS, "Event " + DataHelper.getShortUuid(event.getUuid()) + " saved");
                    }

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.Changes, getContext(), new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    NotificationHelper.showNotification(nContext, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_event)));
                                } else {
                                    NotificationHelper.showNotification(nContext, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_event)));
                                }
                                getActivity().finish();
                            }
                        });
                    } else {
                        NotificationHelper.showNotification(nContext, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_event)));
                        getActivity().finish();
                    }

                }

                private ITaskResultCallback init(Event event) {
                    this.event = event;

                    return this;
                }

            }.init(record));
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }
    }
}
