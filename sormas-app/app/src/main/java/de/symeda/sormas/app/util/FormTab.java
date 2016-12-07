package de.symeda.sormas.app.util;

import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.FacilityDao;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.CommunityDao;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.DistrictDao;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDao;
import de.symeda.sormas.app.component.DateField;
import de.symeda.sormas.app.component.LabelField;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.component.TextField;

/**
 * Created by Stefan Szczesny on 01.08.2016.
 */
public abstract class FormTab extends DialogFragment implements FormFragment {

    /**
     * Fill the model-map and fill the ui. Appends an DatePickerDialog for open on button click and the nested binding.
     * @param dateFieldId
     */
    protected DateField addDateField(final int dateFieldId) {
        final DateField dateField = (DateField) getView().findViewById(dateFieldId);
        dateField.setInputType(InputType.TYPE_NULL);
        dateField.clearFocus();

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showDateFragment(dateField);
            }
        });
        dateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateFragment(dateField);
                }
            }
        });

        return dateField;
    }

    private void showDateFragment(final DateField dateField) {
        SelectDateFragment newFragment = new SelectDateFragment(){
            @Override
            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                dateField.setValue(DateHelper.getDateZero(yy, mm, dd));
            }
            @Override
            public void onClear() {
                dateField.setValue(null);
                dateField.clearFocus();
            }
        };

        Bundle dateBundle = new Bundle();
        dateBundle.putSerializable(SelectDateFragment.DATE, dateField.getValue());
        newFragment.setArguments(dateBundle);
        newFragment.show(getFragmentManager(), getResources().getText(R.string.headline_date_picker).toString());
    }

    /**
     * Fill the spinner for the given enum, set the selected entry, register the base listeners and the given ones.
     * @param spinnerFieldId
     * @param enumClass
     */
    protected SpinnerField addSpinnerField(final int spinnerFieldId, Class enumClass, final AdapterView.OnItemSelectedListener ...moreListeners) {
        List<Item> items = DataUtils.getEnumItems(enumClass);
        return makeSpinnerField(spinnerFieldId, items, moreListeners);
    }

    /**
     * Fill the spinner for the given list, set the selected entry, register the base listeners and the given ones.
     * @param spinnerFieldId
     */
    protected SpinnerField addSpinnerField(final int spinnerFieldId, List<Item> items, final AdapterView.OnItemSelectedListener ...moreListeners) {
        return makeSpinnerField(spinnerFieldId, items, moreListeners);
    }

    /**
     * Fill the spinner for facility selection.
     * See {@see addSpinnerField()}
     * @param spinnerFieldId
     * @param moreListeners
     */
    protected SpinnerField addFacilitySpinnerField(final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        FacilityDao facilityDao = DatabaseHelper.getFacilityDao();
        List<Item> items = DataUtils.getItems(facilityDao.queryForAll());
        return makeSpinnerField(spinnerFieldId,items, moreListeners);
    }

    protected SpinnerField addRegionSpinnerField(View parentView, final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        RegionDao regionDao = DatabaseHelper.getRegionDao();
        List<Item> items = DataUtils.getItems(regionDao.queryForAll());
        return makeSpinnerField(parentView,spinnerFieldId,items, moreListeners);
    }

    protected SpinnerField addDistrictSpinnerField(View parentView, final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        DistrictDao districtDao = DatabaseHelper.getDistrictDao();
        List<Item> items = DataUtils.getItems(districtDao.queryForAll());
        return makeSpinnerField(parentView,spinnerFieldId,items, moreListeners);
    }

    protected SpinnerField addCommunitySpinnerField(View parentView,  final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        CommunityDao communityDao = DatabaseHelper.getCommunityDao();
        List<Item> items = DataUtils.getItems(communityDao.queryForAll());
        return makeSpinnerField(parentView,spinnerFieldId,items, moreListeners);
    }

    protected SpinnerField addPersonSpinnerField(final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        PersonDao personDao = DatabaseHelper.getPersonDao();
        List<Item> items = null;
        try {
            items = DataUtils.getItems(personDao.getAllPersonsWithoutCase());
        } catch (SQLException e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return makeSpinnerField(spinnerFieldId,items, moreListeners);
    }

    private SpinnerField makeSpinnerField(final int spinnerFieldId, List<Item> items, final AdapterView.OnItemSelectedListener[] moreListeners) {
        return makeSpinnerField(getView(), spinnerFieldId, items, moreListeners);
    }

    private SpinnerField makeSpinnerField(View parentView, final int spinnerFieldId, List<Item> items, final AdapterView.OnItemSelectedListener[] moreListeners) {
        final SpinnerField spinnerField = (SpinnerField) parentView.findViewById(spinnerFieldId);
        spinnerField.setSpinnerAdapter(items);

        final List<AdapterView.OnItemSelectedListener> moreListenersAll = new ArrayList<>(Arrays.asList(moreListeners));

        // This is crucial for data binding because it allows the listeners from the component
        // classes to still work
        if(spinnerField.getOnItemSelectedListener() != null) {
            moreListenersAll.add(spinnerField.getOnItemSelectedListener());
        }

        spinnerField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //spinnerField.setValue(spinnerField.getItemAtPosition(position));
                for (AdapterView.OnItemSelectedListener listener:moreListenersAll) {
                    listener.onItemSelected(parent,view,position,id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //spinnerField.setValue(null);
                for (AdapterView.OnItemSelectedListener listener:moreListeners) {
                    listener.onNothingSelected(parent);
                }
            }
        });

        return spinnerField;
    }

    /**
     * Update the spinner list and set selected value.
     * @param selectedItem
     * @param items
     * @param spinnerField
     */
    protected void setSpinnerValue(Object selectedItem, List<Item> items, SpinnerField spinnerField) {
        spinnerField.setSpinnerAdapter(items);
        spinnerField.setValue(selectedItem);
    }

    /**
     * Fill the location_fragment_layout.xml fragment and bind the given properties the a local model.
     * The local model is bound to the global model by the given fieldId and has to be committed backwards to the parent object.
     * @param locationFieldId
     */
    protected void addLocationField(final Person person, final int locationFieldId, int locationBtnId) {
        try {
            // inner model for location, has to be committed for each item
            //final Map<Integer,Object> innerModel = new HashMap<>();
            //final Location location = model.get(locationFieldId)!=null?(Location)model.get(locationFieldId): DataUtils.createNew(Location.class);

            final Location location = person.getAddress() != null ? person.getAddress() : DataUtils.createNew(Location.class);
            DatabaseHelper.getLocationDao().initializeLocation(location);

            // set the TextField for the location
            final TextField locationText = (TextField) getView().findViewById(locationFieldId);
            locationText.setEnabled(false);
            locationText.setValue(location.toString());

            ImageButton btn = (ImageButton) getView().findViewById(locationBtnId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    dialogBuilder.setTitle(getResources().getString(R.string.headline_location));

                    final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.location_fragment_layout, null);
                    dialogBuilder.setView(dialogView);

                    /**
                    innerModel.put(R.id.form_location_address, location.getAddress());
                    EditText addressField = (EditText) dialogView.findViewById(R.id.form_location_address);
                    addressField.setText((String)innerModel.get(R.id.form_location_address));

                    innerModel.put(R.id.form_location_address_details, location.getDetails());
                    EditText detailsField = (EditText) dialogView.findViewById(R.id.form_location_address_details);
                    detailsField.setText((String)innerModel.get(R.id.form_location_address_details));

                    innerModel.put(R.id.form_location_address_city, location.getCity());
                    EditText cityField = (EditText) dialogView.findViewById(R.id.form_location_address_city);
                    cityField.setText((String)innerModel.get(R.id.form_location_address_city));

                    innerModel.put(R.id.location_region, location.getRegion());
                    addRegionSpinnerField(innerModel,dialogView, R.id.location_region);

                    innerModel.put(R.id.location_district, location.getDistrict());
                    addDistrictSpinnerField(innerModel,dialogView, R.id.location_region);

                    innerModel.put(R.id.location_community, location.getCommunity());
                    addCommunitySpinnerField(innerModel,dialogView, R.id.location_community);
                     */

//                    if(((SpinnerField) dialogView.findViewById(R.id.location_region)).getValue() != null) {
//                        location.setRegion((Region)((SpinnerField) getView().findViewById(R.id.location_region)).getValue());
//                    }
//                    if(((SpinnerField) dialogView.findViewById(R.id.location_district)).getValue() != null) {
//                        location.setDistrict((District)((SpinnerField) getView().findViewById(R.id.location_district)).getValue());
//                    }
//                    if(((SpinnerField) dialogView.findViewById(R.id.location_community)).getValue() != null) {
//                        location.setCommunity((Community)((SpinnerField) getView().findViewById(R.id.location_community)).getValue());
//                    }

                    addRegionSpinnerField(dialogView, R.id.location_region);
                    addDistrictSpinnerField(dialogView, R.id.location_district);
                    addCommunitySpinnerField(dialogView, R.id.location_community);

                    if(location.getRegion() != null) {
                        ((SpinnerField) dialogView.findViewById(R.id.location_region)).setValue(location.getRegion());
                    }
                    if(location.getDistrict() != null) {
                        ((SpinnerField) dialogView.findViewById(R.id.location_district)).setValue(location.getDistrict());
                    }
                    if(location.getCommunity() != null) {
                        ((SpinnerField) dialogView.findViewById(R.id.location_community)).setValue(location.getCommunity());
                    }
                    if(location.getAddress() != null) {
                        ((TextField) dialogView.findViewById(R.id.location_address)).setValue(location.getAddress());
                    }
                    if(location.getDetails() != null) {
                        ((TextField) dialogView.findViewById(R.id.location_details)).setValue(location.getDetails());
                    }
                    if(location.getCity() != null) {
                        ((TextField) dialogView.findViewById(R.id.location_city)).setValue(location.getCity());
                    }

                    /*
                    innerModel.put(R.id.form_location_address_latitude, location.getLatitude());
                    EditText latitudeField = (EditText) dialogView.findViewById(R.id.form_location_address_latitude);
                    latitudeField.setText((String)innerModel.get(R.id.form_location_address_latitude));


                    innerModel.put(R.id.form_location_address_longitude, location.getLongitude());
                    EditText longitudeField = (EditText) dialogView.findViewById(R.id.form_location_address_longitude);
                    longitudeField.setText((String)innerModel.get(R.id.form_location_address_longitude));
                    */

                    dialogBuilder.setPositiveButton(getResources().getString(R.string.action_done), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            TextField addressField = (TextField) dialogView.findViewById(R.id.location_address);
                            location.setAddress(addressField.getValue());
                            TextField detailsField = (TextField) dialogView.findViewById(R.id.location_details);
                            location.setDetails(detailsField.getValue());
                            TextField cityField = (TextField) dialogView.findViewById(R.id.location_city);
                            location.setCity(cityField.getValue());

                            location.setRegion((Region)((SpinnerField) dialogView.findViewById(R.id.location_region)).getValue());
                            location.setDistrict((District)((SpinnerField) dialogView.findViewById(R.id.location_district)).getValue());
                            location.setCommunity((Community)((SpinnerField) dialogView.findViewById(R.id.location_community)).getValue());

                            /*
                            location.setLatitude((Float)innerModel.get(R.id.form_location_address_latitude));
                            location.setLongitude((Float)innerModel.get(R.id.form_location_address_longitude));
                            */

                            locationText.setValue(location.toString());
                            // put the inner model for the given fieldID
                            //model.put(locationFieldId, location);

                            person.setAddress(location);
                        }
                    });
                    dialogBuilder.setNegativeButton(getResources().getString(R.string.action_dimiss), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    AlertDialog newPersonDialog = dialogBuilder.create();
                    newPersonDialog.show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    protected void deactivateField(View v) {
        v.setEnabled(false);
        v.clearFocus();
    }

    protected void setFieldVisible(View v, boolean visible) {
        if (visible) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.INVISIBLE);
            v.clearFocus();
        }
    }

    protected void activateField(View v) {
        v.setEnabled(true);
    }

    protected abstract AbstractDomainObject commit(AbstractDomainObject ado);

    protected void reloadFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    protected List<Item> toItems(List<?> listBefore, boolean withNull) {
        List<Item> listItems = new ArrayList<>();
        if(withNull) {
            listItems.add(new Item("",null));
        }
        for (Object o : listBefore) {
            listItems.add(new Item(String.valueOf(o),o));
        }
        return listItems;
    }



}
