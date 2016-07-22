package de.symeda.sormas.app.caze;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasAppView;
import de.symeda.sormas.app.SurveillanceActivity;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class CaseEditView extends SormasAppView<SurveillanceActivity> {

    public static final int VIEW_ID = R.layout.case_edit_layout;

    public CaseEditView(SurveillanceActivity context) {
        super(context);
    }

    @Override
    protected void init() {
    }

    @Override
    protected String getViewName() {
        return "Case";
    }

    @Override
    protected void show() {
        super.show();
        
        final Button button = (Button) getContext().findViewById(R.id.button_back);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getContext().showCasesView();
            }
        });

    /*
        TabHost tabHost = (TabHost)getContext().findViewById(R.id.tab_host);


        TabSpec tab1 = tabHost.newTabSpec("First Tab");
        tab1.setIndicator("Tab1");
        tab1.setContent(new Intent(this  , CaseDataActivity.class ));
        TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        tab2.setIndicator("Tab2");
        TabSpec tab3 = tabHost.newTabSpec("Third tab");
        tab3.setIndicator("Tab3");

        // Add the tabs  to the TabHost to display.
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        */

    }

    public void setData(CaseDataDto dto) {
        populateFormView(dto);
    }

    /**
     * Create a form of case
     * @param dto
     */
    private void populateFormView( CaseDataDto dto) {
    }


}
