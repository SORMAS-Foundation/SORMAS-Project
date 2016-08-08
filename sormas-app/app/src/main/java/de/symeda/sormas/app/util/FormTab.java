package de.symeda.sormas.app.util;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.FacilityDao;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.CommunityDao;
import de.symeda.sormas.app.backend.region.DistrictDao;
import de.symeda.sormas.app.backend.region.RegionDao;

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
    protected void addDateField(final int dateFieldId, int btnDatePickerId, final DatePickerDialog.OnDateSetListener ...moreListeners) {
        final TextView dateField = (TextView) getView().findViewById(dateFieldId);
        //dateField.setEnabled(false);

        // Set initial value to ui
        if(model.get(dateFieldId)!=null) {
            dateField.setText(DateHelper.formatDDMMYYYY((Date) model.get(dateFieldId)));
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
                        dateField.setText(DateHelper.formatDDMMYYYY((Date) model.get(dateFieldId)));
                        for( DatePickerDialog.OnDateSetListener listener:moreListeners) {
                            listener.onDateSet(view,yy,mm,dd);
                        }
                    }
                    @Override
                    public void onClear() {
                        model.put(dateFieldId, null);
                        dateField.setText("");
                        dateField.clearFocus();
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
     * Fill the spinner for the given enum, set the selected entry, register the base listeners and the given ones.
     * @param spinnerFieldId
     * @param enumClass
     */
    protected void addSpinnerField(final int spinnerFieldId, Class enumClass, final AdapterView.OnItemSelectedListener ...moreListeners) {
        List<Item> items = DataUtils.getEnumItems(enumClass);
        makeSpinnerField(spinnerFieldId, items, moreListeners);
    }

    /**
     * Fill the spinner for facility selection.
     * See {@see addSpinnerField()}
     * @param spinnerFieldId
     * @param moreListeners
     */
    protected void addFacilitySpinnerField(final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        FacilityDao facilityDao = DatabaseHelper.getFacilityDao();
        List<Item> items = DataUtils.getItems(facilityDao.queryForAll());
        makeSpinnerField(spinnerFieldId,items, moreListeners);
    }

    protected void addRegionSpinnerField(Map<Integer, Object> model, View parentView, final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        RegionDao regionDao = DatabaseHelper.getRegionDao();
        List<Item> items = DataUtils.getItems(regionDao.queryForAll());
        makeSpinnerField(model,parentView,spinnerFieldId,items, moreListeners);
    }

    protected void addDistrictSpinnerField(Map<Integer, Object> model, View parentView, final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        DistrictDao districtDao = DatabaseHelper.getDistrictDao();
        List<Item> items = DataUtils.getItems(districtDao.queryForAll());
        makeSpinnerField(model,parentView,spinnerFieldId,items, moreListeners);
    }

    protected void addCommunitySpinnerField(Map<Integer, Object> model, View parentView,  final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        CommunityDao communityDao = DatabaseHelper.getCommunityDao();
        List<Item> items = DataUtils.getItems(communityDao.queryForAll());
        makeSpinnerField(model,parentView,spinnerFieldId,items, moreListeners);
    }

    private void makeSpinnerField(final int spinnerFieldId, List<Item> items, final AdapterView.OnItemSelectedListener[] moreListeners) {
        makeSpinnerField(getModel(), getView(), spinnerFieldId, items, moreListeners);
    }

    private Map<Integer, Object> makeSpinnerField(final Map<Integer, Object> model, View parentView, final int spinnerFieldId, List<Item> items, final AdapterView.OnItemSelectedListener[] moreListeners) {
        final Spinner spinner = (Spinner) parentView.findViewById(spinnerFieldId);
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

        return model;
    }

    /**
     * Fill the location_layout.xml fragment and bind the given properties the a local model.
     * The local model is bound to the global model by the given fieldId and has to be committed backwards to the parent object.
     * @param locationFieldId
     */
    protected void addLocationField(final int locationFieldId) {
        // parentView for location
        View parentView = (View) getView().findViewById(locationFieldId);

        // inner model for location, has to be committed for each item
        Map<Integer,Object> innerModel = new HashMap<>();

        Location location = (Location) model.get(locationFieldId);
        if(location==null) {
            location = new Location();
        }

        innerModel.put(R.id.form_location_address,location.getAddress());
        EditText addressField = (EditText) parentView.findViewById(R.id.form_location_address);
        addressField.setText((String)innerModel.get(R.id.form_location_address));

        innerModel.put(R.id.form_location_address_details,location.getDetails());
        EditText detailsField = (EditText) parentView.findViewById(R.id.form_location_address_details);
        detailsField.setText((String)innerModel.get(R.id.form_location_address_details));

        innerModel.put(R.id.form_location_address_city,location.getCity());
        EditText cityField = (EditText) parentView.findViewById(R.id.form_location_address_city);
        cityField.setText((String)innerModel.get(R.id.form_location_address_city));

        innerModel.put(R.id.form_location_address_state, location.getRegion());
        addRegionSpinnerField(innerModel,parentView, R.id.form_location_address_state);

        innerModel.put(R.id.form_location_address_lga, location.getDistrict());
        addDistrictSpinnerField(innerModel,parentView, R.id.form_location_address_lga);

        innerModel.put(R.id.form_location_address_ward, location.getCommunity());
        addCommunitySpinnerField(innerModel,parentView, R.id.form_location_address_ward);

        innerModel.put(R.id.form_location_address_latitude,location.getLatitude());
        EditText latitudeField = (EditText) parentView.findViewById(R.id.form_location_address_latitude);
        latitudeField.setText((String)innerModel.get(R.id.form_location_address_latitude));

        innerModel.put(R.id.form_location_address_longitude,location.getLongitude());
        EditText longitudeField = (EditText) parentView.findViewById(R.id.form_location_address_longitude);
        longitudeField.setText((String)innerModel.get(R.id.form_location_address_longitude));

        // put the inner model for the given fieldID
        model.put(locationFieldId, innerModel);
    }

    protected void deactivateField(View v) {
        v.setEnabled(false);
        v.clearFocus();
    }

    protected void activateField(View v) {
        v.setEnabled(true);
    }


    protected Map<Integer, Object> getModel() {
        return model;
    }

    protected void initModel() {
        model = new HashMap<Integer, Object>();
    }

    protected abstract AbstractDomainObject commit(AbstractDomainObject ado);
}
