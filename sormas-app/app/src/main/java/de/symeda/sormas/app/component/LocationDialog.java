package de.symeda.sormas.app.component;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.DataUtils;


/**
 * Created by Stefan Szczesny on 31.01.2017.
 */

public class LocationDialog extends AlertDialog.Builder {

    private Location location;
    private Callback positiveCallback;
    private Callback negativeCallback;

    public LocationDialog(FragmentActivity activity, final Location location) {
        super(activity);
        this.positiveCallback = null;
        this.negativeCallback = null;

        this.setTitle(activity.getResources().getString(R.string.headline_location));

        final View dialogView = activity.getLayoutInflater().inflate(R.layout.location_fragment_layout, null);
        this.setView(dialogView);

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

        this.setPositiveButton(activity.getResources().getString(R.string.action_done), new DialogInterface.OnClickListener() {
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

                if(positiveCallback!=null) {
                    positiveCallback.call();
                }
            }
        });
        this.setNegativeButton(activity.getResources().getString(R.string.action_dimiss), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(negativeCallback!=null) {
                    negativeCallback.call();
                }
            }
        });

        this.location = location;
    }


    public Location getLocation() {
        return this.location;
    }

    public void setPositiveCallback(Callback positiveCallback) {
        this.positiveCallback = positiveCallback;
    }

    public void setNegativeCallback(Callback negativeCallback) {
        this.negativeCallback = negativeCallback;
    }
}
