package de.symeda.sormas.app;


import android.os.Bundle;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CasesView;

public class SurveillanceActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Noch nicht die optimale Lösung, ausprobieren wie man die View richtig anzeigt. Dafür will ich noch den ersten ViewChange (Liste -> Form) bauen.
        SormasAppView casesView = new CasesView(this, R.layout.cases_view);

    }



}
