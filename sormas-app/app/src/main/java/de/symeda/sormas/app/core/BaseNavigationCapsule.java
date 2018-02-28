package de.symeda.sormas.app.core;

import android.content.Context;

import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Orson on 06/01/2018.
 */

public class BaseNavigationCapsule<T extends AbstractDomainObject> implements INavigationCapsule<T> {


    private IStatusElaborator filterStatus;
    private IStatusElaborator pageStatus;
    private List<IStatusElaborator> otherStatus = new ArrayList<>();
    private String recordUuid;
    private T record;


    public BaseNavigationCapsule(Context context, String recordUuid, Enum pageStatus) {
        this(context, recordUuid, null, pageStatus);
    }

    public BaseNavigationCapsule(Context context, String recordUuid, T record, Enum pageStatus) {
        this.recordUuid = recordUuid;
        this.record = record;

        /*if (filterStatus != null)
            this.filterStatus = StatusElaboratorFactory.getElaborator(context, filterStatus);*/

        if (pageStatus != null)
            this.pageStatus = StatusElaboratorFactory.getElaborator(context, pageStatus);

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
        return pageStatus;
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

}
