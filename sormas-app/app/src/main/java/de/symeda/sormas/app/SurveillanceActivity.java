package de.symeda.sormas.app;


import android.os.Bundle;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CaseEditView;
import de.symeda.sormas.app.caze.CasesView;

public class SurveillanceActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showCasesView();
    }

    public void showCasesView() {
        SormasAppView casesView = new CasesView(this);
        changeView(casesView, CasesView.VIEW_ID);
    }

    public void showCaseEditView(CaseDataDto dto) {
        SormasAppView casesView = new CaseEditView(this);
        changeView(casesView, CaseEditView.VIEW_ID);
    }

    private void changeView(SormasAppView view, int viewId) {
        setContentView(viewId);
        view.show();
    }
}
