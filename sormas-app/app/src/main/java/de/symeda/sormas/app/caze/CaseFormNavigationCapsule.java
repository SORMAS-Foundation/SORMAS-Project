package de.symeda.sormas.app.caze;

import android.content.Context;

import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class CaseFormNavigationCapsule extends BaseFormNavigationCapsule {

    private IStatusElaborator filterStatus;
    private SearchStrategy searchStrategy;

    public CaseFormNavigationCapsule(Context context, String recordUuid) {
        super(context, recordUuid, null);
    }

    public CaseFormNavigationCapsule setReadPageStatus(CaseClassification pageStatus) {
        setPageStatus(pageStatus);

        return this;
    }

    public CaseFormNavigationCapsule setEditPageStatus(InvestigationStatus pageStatus) {
        setPageStatus(pageStatus);

        return this;
    }
}