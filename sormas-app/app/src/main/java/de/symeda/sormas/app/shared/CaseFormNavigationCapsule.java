package de.symeda.sormas.app.shared;

import android.content.Context;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

/**
 * Created by Orson on 09/01/2018.
 */

public class CaseFormNavigationCapsule extends BaseFormNavigationCapsule<Case, CaseFormNavigationCapsule>  {

    private IStatusElaborator filterStatus;
    private SearchBy searchBy;

    public CaseFormNavigationCapsule(Context context) {
        super(context, null, null);
    }

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