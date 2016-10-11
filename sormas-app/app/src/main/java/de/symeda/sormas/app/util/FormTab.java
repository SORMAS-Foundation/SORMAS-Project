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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
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
import de.symeda.sormas.app.component.SymptomStateField;

/**
 * Created by Stefan Szczesny on 01.08.2016.
 */
public abstract class FormTab extends DialogFragment implements FormFragment {

    private Map<Integer, Object> model;

    /**
     * Fill the model-map and fill the ui. Appends an DatePickerDialog for open on button click and the nested binding.
     * @param dateFieldId
     */
    protected TextView addDateField(final int dateFieldId) {
        final TextView dateField = (TextView) getView().findViewById(dateFieldId);
        dateField.setInputType(InputType.TYPE_NULL);

        // Set initial value to ui
        if(model.get(dateFieldId)!=null) {
            dateField.setText(DateHelper.formatDDMMYYYY((Date) model.get(dateFieldId)));
            dateField.clearFocus();
        }

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showDateFragment(dateFieldId, dateField);
            }
        });
        dateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateFragment(dateFieldId, dateField);
                }
            }
        });

        return dateField;
    }

    private void showDateFragment(final int dateFieldId, final TextView dateField) {
        SelectDateFragment newFragment = new SelectDateFragment(){
            @Override
            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                model.put(dateFieldId, DateHelper.getDateZero(yy,mm,dd));
                dateField.setText(DateHelper.formatDDMMYYYY((Date) model.get(dateFieldId)));
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

    /**
     * Adds a YesNoField for given ID, Caption without any Listener.
     * @param yesNoFieldId
     * @param caption
     */
    protected  void addSymptomStateField(final int yesNoFieldId, String caption) {
        makeSymptomStateField(yesNoFieldId,caption,true,null);
    }

    /**
     * Adds a YesNoField for given ID, Caption without a Listener. Sets the visibility of the field.
     * @param yesNoFieldId
     * @param caption
     * @param visible
     */
    protected  void addSymptomStateField(final int yesNoFieldId, String caption, boolean visible) {
        makeSymptomStateField(yesNoFieldId,caption,visible,null);
    }


    /**
     * Adds a YesNoField for given ID, Caption and Listener. Sets the visibility of the field.
     * @param yesNoFieldId
     * @param caption
     * @param visible
     * @param listener
     */
    protected  void addSymptomStateField(final int yesNoFieldId, String caption, boolean visible, RadioGroup.OnCheckedChangeListener listener) {
        makeSymptomStateField(yesNoFieldId,caption,visible,listener);
    }

    private void makeSymptomStateField(final int yesNoFieldId, String caption, boolean visible, RadioGroup.OnCheckedChangeListener listener) {
        final SymptomStateField symptomStateField = (SymptomStateField) getView().findViewById(yesNoFieldId);
        if(visible) {
            symptomStateField.setVisibility(View.VISIBLE);
            symptomStateField.setCaption(caption);
            symptomStateField.setValue(((SymptomState)model.get(yesNoFieldId)));
            symptomStateField.setOnCheckedChangeListener(listener);
        }
        else {
            symptomStateField.setVisibility(View.GONE);
        }
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
     * Fill the spinner for the given list, set the selected entry, register the base listeners and the given ones.
     * @param spinnerFieldId
     */
    protected void addSpinnerField(final int spinnerFieldId, List<Item> items, final AdapterView.OnItemSelectedListener ...moreListeners) {
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

    protected void addUserSpinnerField(final int spinnerFieldId, List<UserRole> userRoles, final AdapterView.OnItemSelectedListener ...moreListeners) {
        UserDao userDao = DatabaseHelper.getUserDao();
        List<Item> items = null;
        if (userRoles.size() == 0) {
            items = DataUtils.getItems(userDao.queryForAll());
        } else {
            for (UserRole userRole : userRoles) {
                if (items == null) {
                    items = DataUtils.getItems(userDao.queryForEq(User.USER_ROLE, userRole));
                } else {
                    items = DataUtils.addItems(items, userDao.queryForEq(User.USER_ROLE, userRole));
                }
            }
        }
        makeSpinnerField(spinnerFieldId,items,moreListeners);
    }

    protected void addPersonSpinnerField(final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        PersonDao personDao = DatabaseHelper.getPersonDao();
        List<Item> items = null;
        try {
            items = DataUtils.getItems(personDao.getAllPersonsWithoutCase());
        } catch (SQLException e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        makeSpinnerField(spinnerFieldId,items, moreListeners);
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
     * Fill the location_fragment_layout.xml fragment and bind the given properties the a local model.
     * The local model is bound to the global model by the given fieldId and has to be committed backwards to the parent object.
     * @param locationFieldId
     */
    protected void addLocationField(final int locationFieldId, int locationBtnId) {
        try {
            // inner model for location, has to be committed for each item
            final Map<Integer,Object> innerModel = new HashMap<>();
            final Location location = model.get(locationFieldId)!=null?(Location)model.get(locationFieldId): DataUtils.createNew(Location.class);

            if(location.getRegion()!=null) {
                RegionDao regionDao = DatabaseHelper.getRegionDao();
                location.setRegion(regionDao.queryForId(location.getRegion().getId()));
            }

            if(location.getDistrict()!=null) {
                DistrictDao districtDao = DatabaseHelper.getDistrictDao();
                location.setDistrict(districtDao.queryForId(location.getDistrict().getId()));
            }

            if(location.getCommunity()!=null) {
                CommunityDao communityDaoDao = DatabaseHelper.getCommunityDao();
                location.setCommunity(communityDaoDao.queryForId(location.getCommunity().getId()));
            }

            // set the TextField for the location
            final EditText locationText = (EditText) getView().findViewById(locationFieldId);
            locationText.setEnabled(false);
            locationText.setText(location.toString());

            ImageButton btn = (ImageButton) getView().findViewById(locationBtnId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    dialogBuilder.setTitle(getResources().getString(R.string.headline_location));

                    final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.location_fragment_layout, null);
                    dialogBuilder.setView(dialogView);

                    innerModel.put(R.id.form_location_address, location.getAddress());
                    EditText addressField = (EditText) dialogView.findViewById(R.id.form_location_address);
                    addressField.setText((String)innerModel.get(R.id.form_location_address));

                    innerModel.put(R.id.form_location_address_details, location.getDetails());
                    EditText detailsField = (EditText) dialogView.findViewById(R.id.form_location_address_details);
                    detailsField.setText((String)innerModel.get(R.id.form_location_address_details));

                    innerModel.put(R.id.form_location_address_city, location.getCity());
                    EditText cityField = (EditText) dialogView.findViewById(R.id.form_location_address_city);
                    cityField.setText((String)innerModel.get(R.id.form_location_address_city));

                    innerModel.put(R.id.form_location_address_state, location.getRegion());
                    addRegionSpinnerField(innerModel,dialogView, R.id.form_location_address_state);

                    innerModel.put(R.id.form_location_address_lga, location.getDistrict());
                    addDistrictSpinnerField(innerModel,dialogView, R.id.form_location_address_lga);

                    innerModel.put(R.id.form_location_address_ward, location.getCommunity());
                    addCommunitySpinnerField(innerModel,dialogView, R.id.form_location_address_ward);


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
                            EditText addressField = (EditText) dialogView.findViewById(R.id.form_location_address);
                            location.setAddress(addressField.getText().toString());

                            EditText detailsField = (EditText) dialogView.findViewById(R.id.form_location_address_details);
                            location.setDetails(detailsField.getText().toString());

                            EditText cityField = (EditText) dialogView.findViewById(R.id.form_location_address_city);
                            location.setCity(cityField.getText().toString());

                            location.setRegion((Region)innerModel.get(R.id.form_location_address_state));
                            location.setDistrict((District)innerModel.get(R.id.form_location_address_lga));
                            location.setCommunity((Community)innerModel.get(R.id.form_location_address_ward));

                            /*
                            location.setLatitude((Float)innerModel.get(R.id.form_location_address_latitude));
                            location.setLongitude((Float)innerModel.get(R.id.form_location_address_longitude));
                            */

                            locationText.setText(location.toString());
                            // put the inner model for the given fieldID
                            model.put(locationFieldId, location);
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


    protected Map<Integer, Object> getModel() {
        return model;
    }

    protected void initModel() {
        model = new HashMap<Integer, Object>();
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


    @BindingAdapter("bind:enum")
    public static void setEnum(TextView textView, Enum e){
        textView.setText(e.toString());
    }


    @BindingAdapter("bind:short_uuid")
    public static void setShortUuid(TextView textView, String uuid){
        textView.setText(DataHelper.getShortUuid(uuid));
    }

    @BindingAdapter("bind:date")
    public static void setDate(TextView textView, Date date){
        textView.setText(DateHelper.formatDDMMYYYY(date));
    }

    @BindingAdapter("bind:person")
    public static void setPerson(TextView textView, Person person){
        textView.setText(person!=null?person.toString():null);
    }

    @BindingAdapter("bind:user")
    public static void setUser(TextView textView, User user){
        textView.setText(user!=null?user.toString():null);
    }

}
