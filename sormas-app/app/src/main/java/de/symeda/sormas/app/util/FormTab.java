package de.symeda.sormas.app.util;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Stefan Szczesny on 01.08.2016.
 */
public abstract class FormTab extends DialogFragment implements FormFragment {

    private Map<Integer, Object> model;

    protected void addDateField(final int dateFieldId, int btnDatePickerId) {
        final TextView dateField = (TextView) getView().findViewById(R.id.form_cp_date_of_birth);
        dateField.setEnabled(false);

        if(model.get(dateFieldId)!=null) {
            dateField.setText(DateHelper.formatDDMMYY((Date) model.get(dateFieldId))); // Set initial value to ui
        }

        ImageButton btn = (ImageButton) getView().findViewById(btnDatePickerId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SelectDateFragment newFragment = new SelectDateFragment(){
                    @Override
                    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                        model.put(dateFieldId, DateHelper.getDateZero(yy,mm,dd));
                        dateField.setText(DateHelper.formatDDMMYY((Date) model.get(dateFieldId))); // Set value to ui

                    }
                    @Override
                    public void onClear() {
                        model.put(dateFieldId, null);
                        dateField.setText("");
                    }
                };
                Bundle dateBundle = new Bundle();
                dateBundle.putSerializable(SelectDateFragment.DATE, (Date) model.get(dateFieldId));
                newFragment.setArguments(dateBundle);
                newFragment.show(getFragmentManager(), getResources().getText(R.string.headline_date_picker).toString());
            }
        });
    }

    protected Map<Integer, Object> getModel() {
        return model;
    }

    protected void initModel() {
        model = new HashMap<Integer, Object>();
    }

    protected abstract AbstractDomainObject commit(AbstractDomainObject ado);
}
