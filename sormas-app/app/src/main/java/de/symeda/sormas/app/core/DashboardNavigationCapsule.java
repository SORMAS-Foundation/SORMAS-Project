package de.symeda.sormas.app.core;

import android.content.Context;

import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;

/**
 * Created by Orson on 08/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class DashboardNavigationCapsule implements IDashboardNavigationCapsule {

    private Context context;
    private Enum pageStatus;

    public DashboardNavigationCapsule(Context context) {
        this.context = context;
        this.pageStatus = null;
    }

    public DashboardNavigationCapsule(Context context, Enum pageStatus) {
        this.context = context;
        this.pageStatus = pageStatus;
    }

    @Override
    public IStatusElaborator getPageStatus() {
        if (pageStatus != null)
            return StatusElaboratorFactory.getElaborator(context, pageStatus);

        return null;
    }

    public IDashboardNavigationCapsule setPageStatus(Enum pageStatus) {
        this.pageStatus = pageStatus;
        return this;
    }
}
