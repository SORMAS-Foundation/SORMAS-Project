package de.symeda.sormas.app.util;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Stefan Szczesny on 01.08.2016.
 */
public abstract class FormTab extends DialogFragment implements FormFragment {

    private Map<Integer, Object> model;

    /**
     * Fill the model-map and fill the ui. Appends an DatePickerDialog for open on button click and the nested binding.
     * @param dateFieldId
     * @param btnDatePickerId
     */
    protected void addDateField(final int dateFieldId, int btnDatePickerId) {
        final TextView dateField = (TextView) getView().findViewById(dateFieldId);
        //dateField.setEnabled(false);

        // Set initial value to ui
        if(model.get(dateFieldId)!=null) {
            dateField.setText(DateHelper.formatDDMMYY((Date) model.get(dateFieldId)));
        }

        ImageButton btn = (ImageButton) getView().findViewById(btnDatePickerId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SelectDateFragment newFragment = new SelectDateFragment(){
                    @Override
                    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                        model.put(dateFieldId, DateHelper.getDateZero(yy,mm,dd));
                        // Set value to ui
                        dateField.setText(DateHelper.formatDDMMYY((Date) model.get(dateFieldId)));

                    }
                    @Override
                    public void onClear() {
                        model.put(dateFieldId, null);
                        dateField.setText(null);
                    }
                };
                Bundle dateBundle = new Bundle();
                dateBundle.putSerializable(SelectDateFragment.DATE, (Date) model.get(dateFieldId));
                newFragment.setArguments(dateBundle);
                newFragment.show(getFragmentManager(), getResources().getText(R.string.headline_date_picker).toString());
            }
        });
    }

    /**
     * Fill the model-map and the ui for given Enum.
     * @param spinnerFieldId
     * @param enumClass
     */
    protected void addSpinnerField(final int spinnerFieldId, Class enumClass, final AdapterView.OnItemSelectedListener ...moreListeners) {
        final Spinner spinner = (Spinner) getView().findViewById(spinnerFieldId);

        List<Item> items = DataHelper.getEnumItems(enumClass);
        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model.put(spinnerFieldId, ((Item)parent.getItemAtPosition(position)).getValue());
                for (AdapterView.OnItemSelectedListener listener:moreListeners) {
                    listener.onItemSelected(parent,view,position,id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                model.put(spinnerFieldId, null);
                for (AdapterView.OnItemSelectedListener listener:moreListeners) {
                    listener.onNothingSelected(parent);
                }
            }
        });

        // Set initial value to ui
        if(model.get(spinnerFieldId)!=null) {
            int i = 0;
            for (Item item:items) {
                if(model.get(spinnerFieldId).equals(item.getValue())) {
                    break;
                }
                i++;
            }
            spinner.setSelection(i);
        }
    }


    protected Map<Integer, Object> getModel() {
        return model;
    }

    protected void initModel() {
        model = new HashMap<Integer, Object>();
    }

    protected abstract AbstractDomainObject commit(AbstractDomainObject ado);
}
