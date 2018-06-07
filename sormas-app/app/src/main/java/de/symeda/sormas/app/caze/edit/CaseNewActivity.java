package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.dialog.SelectOrCreatePersonDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 15/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseNewActivity extends BaseEditActivity<Case> {

    public static final String TAG = CaseNewActivity.class.getSimpleName();

    private AsyncTask createPersonTask;
    private AsyncTask selectPersonTask;
    private AsyncTask saveTask;

    private InvestigationStatus pageStatus = null;
    private String recordUuid = null;
    private BaseEditActivityFragment activeFragment = null;

    private MenuItem saveMenu = null;
    private MenuItem addMenu = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
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
        pageStatus = (InvestigationStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    protected Case getActivityRootData(String recordUuid) {
        return DatabaseHelper.getCaseDao().queryUuidWithEmbedded(recordUuid);
    }

    @Override
    protected Case getActivityRootDataIfRecordUuidNull() {
        Person _person = DatabaseHelper.getPersonDao().build();
        Case _case = DatabaseHelper.getCaseDao().build(_person);

        return _case;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment(Case activityRootData) throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(CaseNewActivity.this,
                    recordUuid).setEditPageStatus(pageStatus);
            activeFragment = CaseNewFragment.newInstance(this, dataCapsule, activityRootData);
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
        //TODO: Fix this
        if (!MenuOptionsHelper.handleEditModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_case_new;
    }

    @Override
    public void saveData() {
        if (activeFragment == null)
            return;

        final Case aCase = getStoredActivityRootData();
        final Person person = aCase.getPerson();

        if (aCase == null || person == null)
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
                    List<PersonNameDto> existingPersons = DatabaseHelper.getPersonDao().getPersonNameDtos();
                    List<Person> similarPersons = new ArrayList<>();
                    for (PersonNameDto existingPerson : existingPersons) {
                        if (PersonHelper.areNamesSimilar(person.getFirstName() + " " + person.getLastName(),
                                existingPerson.getFirstName() + " " + existingPerson.getLastName())) {
                            Person person = DatabaseHelper.getPersonDao().queryForId(existingPerson.getId());
                            similarPersons.add(person);
                        }
                    }
                    resultHolder.forList().add(similarPersons);
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

                    List<Person> existingPersons = new ArrayList<>();
                    ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

                    if (listIterator.hasNext())
                        existingPersons = listIterator.next();

                    if (existingPersons.size() > 0) {
                        final SelectOrCreatePersonDialog personDialog = new SelectOrCreatePersonDialog(AbstractSormasActivity.getActiveActivity(), person, existingPersons);
                        personDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                            @Override
                            public void onOkClick(View v, Object item, View viewRoot) {
                                personDialog.dismiss();

                                if (item == null)
                                    return;

                                //Select
                                if (item instanceof Person) {
                                    //TODO: Talk to Martin; we need to know this person's case info
                                    /*Case caze = DatabaseHelper.getCaseDao().getByPersonAndDisease(_person, aCase.getDisease());
                                    CaseDao caseDao;
                                    caseDao.getByPersonAndDisease(_person, aCase.getDisease());*/

                                    aCase.setPerson((Person)item);
                                    savePersonAndCase(aCase);
                                }

                            }
                        });

                        personDialog.setOnCreateClickListener(new TeboAlertDialogInterface.CreateOnClickListener() {
                            @Override
                            public void onCreateClick(View v, Object item, View viewRoot) {
                                personDialog.dismiss();

                                if (item instanceof Person) {
                                    aCase.setPerson((Person)item);
                                    savePersonAndCase(aCase);
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
                        savePersonAndCase(aCase);
                    }
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }

    }

    private void showCaseEditView(Case caze) {
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                caze.getUuid()).setEditPageStatus(caze.getInvestigationStatus());
        CaseEditActivity.goToActivity(CaseNewActivity.this, dataCapsule);
    }

    private void savePersonAndCase(final Case caze) {

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                private PersonDao personDao;
                private String saveUnsuccessful;

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    showPreloader();
                    hideFragmentView();

                    personDao = DatabaseHelper.getPersonDao();
                    saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_case));
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    try {
                        personDao.saveAndSnapshot(caze.getPerson());

                        caze.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
                        caze.setInvestigationStatus(InvestigationStatus.PENDING);

                        User user = ConfigProvider.getUser();
                        caze.setReportingUser(user);
                        if (user.hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
                            caze.setSurveillanceOfficer(user);
                        } else if (user.hasUserRole(UserRole.INFORMANT)) {
                            caze.setSurveillanceOfficer(user.getAssociatedOfficer());
                        }
                        caze.setReportDate(new Date());

                        Calendar calendar = Calendar.getInstance();
                        String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
                        caze.setEpidNumber(caze.getRegion().getEpidCode() != null ? caze.getRegion().getEpidCode() : ""
                                + "-" + caze.getDistrict().getEpidCode() != null ? caze.getDistrict().getEpidCode() : ""
                                + "-" + year + "-");

                        CaseDao caseDao = DatabaseHelper.getCaseDao();
                        caseDao.saveAndSnapshot(caze);
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

                    if (!resultStatus.isSuccess()) {
                        NotificationHelper.showNotification(CaseNewActivity.this, NotificationType.ERROR, resultStatus.getMessage());
                        return;
                    } else {
                        Resources r = getResources();
                        NotificationHelper.showNotification(CaseNewActivity.this, NotificationType.SUCCESS, String.format(r.getString(R.string.snackbar_create_success), r.getString(R.string.entity_case)));
                        showCaseEditView(caze);
                    }
                }
            });
        } catch (Exception ex) {
            hidePreloader();
            showFragmentView();
        }

    }

    public static <TActivity extends AbstractSormasActivity> void
    goToActivity(Context fromActivity, CaseFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, CaseNewActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);

        if (createPersonTask != null && !createPersonTask.isCancelled())
            createPersonTask.cancel(true);

        if (selectPersonTask != null && !selectPersonTask.isCancelled())
            selectPersonTask.cancel(true);
    }

}