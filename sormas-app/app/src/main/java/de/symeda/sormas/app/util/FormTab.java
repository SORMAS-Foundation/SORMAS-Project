package de.symeda.sormas.app.util;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import de.symeda.sormas.app.component.DateField;
import de.symeda.sormas.app.component.RadioGroupField;
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

        dateField.clearFocus();
        return dateField;
    }

    private void showDateFragment(final DateField dateField) {
        SelectDateFragment newFragment = new SelectDateFragment();
        
        newFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet (DatePicker view,int yy, int mm, int dd){
                dateField.setValue(DateHelper.getDateZero(yy, mm, dd));
            }
        });

        newFragment.setOnClearListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dateField.setValue(null);
                dateField.clearFocus();
            }
        });

        Bundle dateBundle = new Bundle();
        dateBundle.putSerializable(SelectDateFragment.DATE, dateField.getValue());
        newFragment.setArguments(dateBundle);
        newFragment.show(getFragmentManager(), getResources().getText(R.string.headline_date_picker).toString());
    }

    protected RadioGroupField addRadioGroupField(final int radioGroupFieldId, Class enumClass) {
        List<Item> items = DataUtils.getEnumItems(enumClass);
        final RadioGroupField radioGroupField = (RadioGroupField) getView().findViewById(radioGroupFieldId);
        for(Item item : items) {
            radioGroupField.addItem(item);
        }
        return radioGroupField;
    }

    /**
     * Fill the spinner for the given enum, set the selected entry, register the base listeners and the given ones.
     * @param spinnerFieldId
     * @param enumClass
     */
    protected SpinnerField addSpinnerField(final int spinnerFieldId, Class enumClass, final AdapterView.OnItemSelectedListener ...moreListeners) {
        List<Item> items = DataUtils.getEnumItems(enumClass);
        return initSpinnerField(null, spinnerFieldId, items, moreListeners);
    }

    /**
     * Fill the spinner for the given list, set the selected entry, register the base listeners and the given ones.
     * @param spinnerFieldId
     */
    protected SpinnerField addSpinnerField(final int spinnerFieldId, List<Item> items, final AdapterView.OnItemSelectedListener ...moreListeners) {
        return initSpinnerField(null, spinnerFieldId, items, moreListeners);
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
        return initSpinnerField(null,spinnerFieldId,items,moreListeners);
    }

    protected SpinnerField addRegionSpinnerField(View parentView, final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        RegionDao regionDao = DatabaseHelper.getRegionDao();
        List<Item> items = DataUtils.getItems(regionDao.queryForAll());
        return initSpinnerField(parentView,spinnerFieldId,items, moreListeners);
    }

    protected SpinnerField addDistrictSpinnerField(View parentView, final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        DistrictDao districtDao = DatabaseHelper.getDistrictDao();
        List<Item> items = DataUtils.getItems(districtDao.queryForAll());
        return initSpinnerField(parentView,spinnerFieldId,items, moreListeners);
    }

    protected SpinnerField addCommunitySpinnerField(View parentView,  final int spinnerFieldId, final AdapterView.OnItemSelectedListener ...moreListeners) {
        CommunityDao communityDao = DatabaseHelper.getCommunityDao();
        List<Item> items = DataUtils.getItems(communityDao.queryForAll());
        return initSpinnerField(parentView,spinnerFieldId,items, moreListeners);
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
        return initSpinnerField(null,spinnerFieldId,items, moreListeners);
    }

    private SpinnerField initSpinnerField(View parentView, final int spinnerFieldId, List<Item> items, final AdapterView.OnItemSelectedListener[] moreListeners) {
        final SpinnerField spinnerField;
        if(parentView != null) {
            spinnerField = (SpinnerField) parentView.findViewById(spinnerFieldId);
        } else {
            spinnerField = (SpinnerField) getView().findViewById(spinnerFieldId);
        }
        spinnerField.setSpinnerAdapter(items);
        for(AdapterView.OnItemSelectedListener listener : moreListeners) {
            spinnerField.getSpinnerFieldListener().registerListener(listener);
        }
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

                    final List emptyList = new ArrayList<>();

                    addCommunitySpinnerField(dialogView, R.id.location_community);

                    addRegionSpinnerField(dialogView, R.id.location_region, new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            SpinnerField districtSpinner = (SpinnerField) dialogView.findViewById(R.id.location_district);
                            Object selectedValue = ((SpinnerField) dialogView.findViewById(R.id.location_region)).getValue();
                            if(districtSpinner != null) {
                                List<District> districtList = emptyList;
                                if(selectedValue != null) {
                                    districtList = DatabaseHelper.getDistrictDao().getByRegion((Region)selectedValue);
                                }
                                setSpinnerValue(districtSpinner.getValue(), DataUtils.getItems(districtList), districtSpinner);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    addDistrictSpinnerField(dialogView, R.id.location_district, new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            SpinnerField communitySpinner = (SpinnerField) dialogView.findViewById(R.id.location_community);
                            Object selectedValue = ((SpinnerField) dialogView.findViewById(R.id.location_district)).getValue();
                            if(communitySpinner != null) {
                                List<Community> communityList = emptyList;
                                if(selectedValue != null) {
                                    communityList = DatabaseHelper.getCommunityDao().getByDistrict((District)selectedValue);
                                }
                                setSpinnerValue(communitySpinner.getValue(), DataUtils.getItems(communityList), communitySpinner);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

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

                            locationText.setValue(location.toString());

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
