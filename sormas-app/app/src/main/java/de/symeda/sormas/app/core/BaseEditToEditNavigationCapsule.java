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
 * Created by Orson on 12/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class BaseEditToEditNavigationCapsule implements IEditToEditNavigationCapsule {

    private IStatusElaborator filterStatus;
    private IStatusElaborator pageStatus;
    private List<IStatusElaborator> otherStatus = new ArrayList<>();
    private String recordUuid;

    public BaseEditToEditNavigationCapsule(Context context, String recordUuid, Enum filterStatus, Enum pageStatus) {
        this.recordUuid = recordUuid;

        if (filterStatus != null)
            this.filterStatus = StatusElaboratorFactory.getElaborator(context, filterStatus);

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
    public AbstractDomainObject getRecord() {
        return null;
    }

    @Override
    public String getSampleMaterial() {
        return null;
    }

    @Override
    public String getCaseUuid() {
        return null;
    }

    @Override
    public String getTaskUuid() {
        return null;
    }

    @Override
    public String getContactUuid() {
        return null;
    }

    @Override
    public String getSampleUuid() {
        return null;
    }

    @Override
    public Disease getDisease() {
        return null;
    }

    @Override
    public boolean isForVisit() {
        return false;
    }

    @Override
    public boolean isVisitCooperative() {
        return false;
    }

    @Override
    public UserRight getUserRight() {
        return null;
    }
}
