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

public class BaseFormNavigationCapsule<T extends AbstractDomainObject> implements INavigationCapsule<T> {// IListToReadNavigationCapsule {

    private Context context;
    private IStatusElaborator filterStatus;
    private Enum pageStatus;
    private List<IStatusElaborator> otherStatus = new ArrayList<>();
    private String recordUuid;
    private T record;
    private String sampleMaterial;
    private String caseUuid;
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

    public void setPageStatus(Enum pageStatus) {
        this.pageStatus = pageStatus;
    }

    public BaseFormNavigationCapsule<T> setSampleMaterial(String sampleMaterial) {
        this.sampleMaterial = sampleMaterial;
        return this;
    }

    public BaseFormNavigationCapsule<T> setCaseUuid(String caseUuid) {
        this.caseUuid = caseUuid;
        return this;
    }

    public BaseFormNavigationCapsule<T> setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
        return this;
    }

    public BaseFormNavigationCapsule<T> setContactUuid(String contactUuid) {
        this.contactUuid = contactUuid;
        return this;
    }

    public BaseFormNavigationCapsule<T> setSampleUuid(String sampleUuid) {
        this.sampleUuid = sampleUuid;
        return this;
    }

    public BaseFormNavigationCapsule<T> setDisease(Disease disease) {
        this.disease = disease;
        return this;
    }

    public BaseFormNavigationCapsule<T> setForVisitStatus(boolean forVisit) {
        this.forVisit = forVisit;
        return this;
    }

    public BaseFormNavigationCapsule<T> setVisitCooerativeStatus(boolean visitCoorporative) {
        this.visitCoorporative = visitCoorporative;
        return this;
    }

    public BaseFormNavigationCapsule<T> setUserRight(UserRight userRight) {
        this.userRight = userRight;
        return this;
    }

    public String getSampleMaterial() {
        return sampleMaterial;
    }

    public String getCaseUuid() {
        return caseUuid;
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
