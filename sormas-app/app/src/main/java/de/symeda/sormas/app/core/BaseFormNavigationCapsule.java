package de.symeda.sormas.app.core;

import android.content.Context;

import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

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
}
