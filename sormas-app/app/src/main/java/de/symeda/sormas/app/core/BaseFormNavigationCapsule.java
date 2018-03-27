package de.symeda.sormas.app.core;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;

/**
 * Created by Orson on 09/01/2018.
 */

public class BaseFormNavigationCapsule<T extends AbstractDomainObject, TFormNavigationCapsule extends BaseFormNavigationCapsule> implements INavigationCapsule<T> {

    private Context context;
    private IStatusElaborator filterStatus;
    private Enum pageStatus;
    private List<IStatusElaborator> otherStatus = new ArrayList<>();
    private int activeMenuKey;
    private String recordUuid;
    private T record;
    private String sampleMaterial;
    private String personUuid;
    private String caseUuid;
    private String eventUuid;
    private String taskUuid;
    private String contactUuid;
    private String sampleUuid;
    private Disease disease;
    private boolean forVisit;
    private boolean visitCoorporative;
    private UserRight userRight;

    public BaseFormNavigationCapsule(Context context, String recordUuid, Enum pageStatus) {
        this(context, recordUuid, null, pageStatus);
    }

    public BaseFormNavigationCapsule(Context context, String recordUuid, T record, Enum pageStatus) {
        this.context = context;
        this.recordUuid = recordUuid;
        this.record = record;

        /*if (filterStatus != null)
            this.filterStatus = StatusElaboratorFactory.getElaborator(context, filterStatus);*/

        this.pageStatus = pageStatus;

        /*this.otherStatus = new ArrayList<>();
        for(Enum e: otherStatus) {
            if (e != null)
                this.otherStatus.add(StatusElaboratorFactory.getElaborator(context, e));
        }*/

    }

    @Override
    public IStatusElaborator getFilterStatus() {
        return filterStatus;
    }

    @Override
    public IStatusElaborator getPageStatus() {
        if (pageStatus != null)
            return StatusElaboratorFactory.getElaborator(context, pageStatus);

        return null;
    }

    @Override
    public List<IStatusElaborator> getOtherStatus() {
        return otherStatus;
    }

    @Override
    public String getRecordUuid() {
        return recordUuid;
    }

    @Override
    public T getRecord() {
        return record;
    }

    @Override
    public int getActiveMenuKey() {
        return activeMenuKey;
    }

    @SuppressWarnings("unchecked")
    public TFormNavigationCapsule setActiveMenu(int activeMenuKey) {
        this.activeMenuKey = activeMenuKey;
        return (TFormNavigationCapsule) this;
        //return this;
    }

    public void setPageStatus(Enum pageStatus) {
        this.pageStatus = pageStatus;
    }

    public TFormNavigationCapsule setSampleMaterial(String sampleMaterial) {
        this.sampleMaterial = sampleMaterial;
        return (TFormNavigationCapsule) this;
    }

    public TFormNavigationCapsule setPersonUuid(String personUuid) {
        this.personUuid = personUuid;
        return (TFormNavigationCapsule) this;
    }

    public TFormNavigationCapsule setCaseUuid(String caseUuid) {
        this.caseUuid = caseUuid;
        return (TFormNavigationCapsule) this;
    }

    public TFormNavigationCapsule setEventUuid(String eventUuid) {
        this.eventUuid = eventUuid;
        return (TFormNavigationCapsule) this;
    }

    public TFormNavigationCapsule setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
        return (TFormNavigationCapsule) this;
    }

    public TFormNavigationCapsule setContactUuid(String contactUuid) {
        this.contactUuid = contactUuid;
        return (TFormNavigationCapsule) this;
    }

    public TFormNavigationCapsule setSampleUuid(String sampleUuid) {
        this.sampleUuid = sampleUuid;
        return (TFormNavigationCapsule) this;
    }

    public TFormNavigationCapsule setDisease(Disease disease) {
        this.disease = disease;
        return (TFormNavigationCapsule) this;
    }

    public TFormNavigationCapsule setForVisitStatus(boolean forVisit) {
        this.forVisit = forVisit;
        return (TFormNavigationCapsule) this;
    }

    public TFormNavigationCapsule setVisitCooerativeStatus(boolean visitCoorporative) {
        this.visitCoorporative = visitCoorporative;
        return (TFormNavigationCapsule) this;
    }

    public TFormNavigationCapsule setUserRight(UserRight userRight) {
        this.userRight = userRight;
        return (TFormNavigationCapsule) this;
    }

    public String getSampleMaterial() {
        return sampleMaterial;
    }

    public String getPersonUuid() {
        return personUuid;
    }

    public String getCaseUuid() {
        return caseUuid;
    }

    @Override
    public String getEventUuid() {
        return eventUuid;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public String getContactUuid() {
        return contactUuid;
    }

    @Override
    public String getSampleUuid() {
        return sampleUuid;
    }

    @Override
    public Disease getDisease() {
        return disease;
    }

    @Override
    public boolean isForVisit() {
        return forVisit;
    }

    @Override
    public boolean isVisitCooperative() {
        return visitCoorporative;
    }

    @Override
    public UserRight getUserRight() {
        return userRight;
    }
}
