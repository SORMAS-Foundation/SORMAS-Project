package de.symeda.sormas.app.util;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.component.TextField;

/**
 * Created by Stefan Szczesny on 01.08.2016.
 */
public abstract class FormTab extends DialogFragment implements FormFragment {

//    public void showDateFragment(final PropertyField<Date> dateField) {
//        SelectDateFragment newFragment = new SelectDateFragment();
//
//        newFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet (DatePicker view,int yy, int mm, int dd){
//                dateField.setValue(DateHelper.getDateZero(yy, mm, dd));
//            }
//        });
//
//        newFragment.setOnClearListener(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dateField.setValue(null);
//                dateField.clearFocus();
//            }
//        });
//
//        Bundle dateBundle = new Bundle();
//        dateBundle.putSerializable(SelectDateFragment.DATE, dateField.getValue());
//        newFragment.setArguments(dateBundle);
//        newFragment.show(getFragmentManager(), getResources().getText(R.string.headline_date_picker).toString());
//    }
//
//    public void showTimeFragment(final DateTimeField timeField) {
//        SelectTimeFragment newFragment = new SelectTimeFragment();
//
//        newFragment.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                timeField.setValue(DateHelper.getTime(hourOfDay, minute));
//            }
//        });
//
//        newFragment.setOnClearListener(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                timeField.setValue(null);
//                timeField.clearFocus();
//            }
//        });
//
//        Bundle dateTimeBundle = new Bundle();
//        dateTimeBundle.putSerializable(SelectTimeFragment.DATE, timeField.getValue());
//        newFragment.setArguments(dateTimeBundle);
//        newFragment.show(getFragmentManager(), getResources().getText(R.string.headline_date_picker).toString());
//    }

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

                    FieldHelper.initCommunitySpinnerField((SpinnerField)dialogView.findViewById(R.id.location_community));

                    FieldHelper.initRegionSpinnerField((SpinnerField)dialogView.findViewById(R.id.location_region), new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            SpinnerField districtSpinner = (SpinnerField) dialogView.findViewById(R.id.location_district);
                            Object selectedValue = ((SpinnerField) dialogView.findViewById(R.id.location_region)).getValue();
                            if(districtSpinner != null) {
                                List<District> districtList = emptyList;
                                if(selectedValue != null) {
                                    districtList = DatabaseHelper.getDistrictDao().getByRegion((Region)selectedValue);
                                }
                                districtSpinner.setAdapterAndValue(districtSpinner.getValue(), DataUtils.toItems(districtList));
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    FieldHelper.initDistrictSpinnerField((SpinnerField)dialogView.findViewById(R.id.location_district), new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            SpinnerField communitySpinner = (SpinnerField) dialogView.findViewById(R.id.location_community);
                            Object selectedValue = ((SpinnerField) dialogView.findViewById(R.id.location_district)).getValue();
                            if(communitySpinner != null) {
                                List<Community> communityList = emptyList;
                                if(selectedValue != null) {
                                    communityList = DatabaseHelper.getCommunityDao().getByDistrict((District)selectedValue);
                                }
                                communitySpinner.setAdapterAndValue(communitySpinner.getValue(), DataUtils.toItems(communityList));
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

    protected void setFieldGone(View v) {
        v.setVisibility(View.GONE);
        v.clearFocus();
    }

    protected void activateField(View v) {
        v.setEnabled(true);
    }

    protected void reloadFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

}
