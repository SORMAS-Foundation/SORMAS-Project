package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.DataUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BaseFragment extends Fragment {

    protected UserRight editOrCreateUserRight;
    protected Tracker tracker;

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SormasApplication application = (SormasApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        manageActivityWriteRights(editOrCreateUserRight);
    }

    @Override
    public void onResume() {
        super.onResume();

        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout) this.getView().findViewById(R.id.swiperefresh);
        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getBaseActivity().synchronizeData(SynchronizeDataAsync.SyncMode.Changes, false, true, true, swiperefresh, null);
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected void setFieldVisibleOrGone(View v, boolean visible) {
        if (visible) {
            v.setVisibility(VISIBLE);
        } else {
            v.setVisibility(GONE);
            v.clearFocus();
        }
    }

    protected void setFieldVisible(View v, boolean visible) {
        if (visible) {
            v.setVisibility(VISIBLE);
        } else {
            v.setVisibility(GONE);
            v.clearFocus();
        }
    }

    protected void setFieldGone(View v) {
        v.setVisibility(GONE);
        v.clearFocus();
    }

    protected void deactivateField(View v) {
        v.setEnabled(false);
        v.clearFocus();
    }

    protected void manageActivityWriteRights(UserRight editRight) {
        User user = ConfigProvider.getUser();
        if (editRight == null || user.hasUserRight(editRight)) {
            return;
        }

        ViewGroup viewGroup = (ViewGroup) getView();
        setViewGroupAndChildrenReadOnly(viewGroup);
    }

    private void setViewGroupAndChildrenReadOnly(ViewGroup viewGroup) {

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view != null) {
                deactivateField(view);
                if (view instanceof ViewGroup) {
                    setViewGroupAndChildrenReadOnly((ViewGroup) view);
                }
            }
        }
    }

    protected void setVisibilityByDisease(Class<?> dtoClass, Disease disease, ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ControlPropertyField) {
                boolean visibleAllowed = isVisibleAllowed(dtoClass, disease, (ControlPropertyField) child);
                child.setVisibility(visibleAllowed && child.getVisibility() == VISIBLE ? VISIBLE : GONE);
            } else if (child instanceof ViewGroup) {
                setVisibilityByDisease(dtoClass, disease, (ViewGroup) child);
            }
        }
    }

    protected boolean isVisibleAllowed(Class<?> dtoClass, Disease disease, ControlPropertyField field) {
        String propertyId = field.getPropertyIdWithoutPrefix();
        return Diseases.DiseasesConfiguration.isDefinedOrMissing(dtoClass, propertyId, disease);
    }

    protected void setVisibleWhen(final ControlPropertyField targetField, ControlPropertyField sourceField, final Object sourceValue) {
        if (sourceField.getValue() != null && sourceField.getValue().equals(sourceValue)) {
            targetField.setVisibility(VISIBLE);
        } else {
            targetField.setVisibility(GONE);
        }

        sourceField.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                if (field.getValue() != null && field.getValue().equals(sourceValue)) {
                    targetField.setVisibility(VISIBLE);
                } else {
                    targetField.hideField(true);
                }
            }
        });
    }

    /**
     * Hide facilityDetails when no static health facility is selected and adjust the caption based on
     * the selected static health facility.
     */
    protected void initializeHealthFacilityDetailsFieldVisibility(final ControlPropertyField healthFacilityField, final ControlPropertyField healthFacilityDetailsField) {
        setHealthFacilityDetailsFieldVisibility(healthFacilityField, healthFacilityDetailsField);
        healthFacilityField.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                setHealthFacilityDetailsFieldVisibility(healthFacilityField, healthFacilityDetailsField);
            }
        });
    }

    private void setHealthFacilityDetailsFieldVisibility(ControlPropertyField healthFacilityField, ControlPropertyField healthFacilityDetailsField) {
        Facility selectedFacility = (Facility) healthFacilityField.getValue();

        if (selectedFacility != null) {
            boolean otherHealthFacility = selectedFacility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
            boolean noneHealthFacility = selectedFacility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);

            if (otherHealthFacility) {
                healthFacilityDetailsField.setVisibility(VISIBLE);
                healthFacilityDetailsField.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
            } else if (noneHealthFacility) {
                healthFacilityDetailsField.setVisibility(VISIBLE);
                healthFacilityDetailsField.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.NONE_HEALTH_FACILITY_DETAILS));
            } else {
                healthFacilityDetailsField.setVisibility(GONE);
            }
        } else {
            healthFacilityDetailsField.setVisibility(GONE);
        }
    }

    /**
     * Only show the causeOfDeathDetails field when either the selected cause of death is 'Other cause'
     * or the selected cause of death disease is 'Other'. Additionally, adjust the caption of the
     * causeOfDeathDetails field based on the selected options.
     */
    protected void initializeCauseOfDeathDetailsFieldVisibility(final ControlPropertyField causeOfDeathField, final ControlPropertyField causeOfDeathDiseaseField, final ControlPropertyField causeOfDeathDetailsField) {
        setCauseOfDeathDetailsFieldVisibility(causeOfDeathField, causeOfDeathDiseaseField, causeOfDeathDetailsField);
        causeOfDeathField.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                setCauseOfDeathDetailsFieldVisibility(causeOfDeathField, causeOfDeathDiseaseField, causeOfDeathDetailsField);
            }
        });
        causeOfDeathDiseaseField.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                setCauseOfDeathDetailsFieldVisibility(causeOfDeathField, causeOfDeathDiseaseField, causeOfDeathDetailsField);
            }
        });
    }

    private void setCauseOfDeathDetailsFieldVisibility(final ControlPropertyField causeOfDeathField, final ControlPropertyField causeOfDeathDiseaseField, final ControlPropertyField causeOfDeathDetailsField) {
        CauseOfDeath selectedCauseOfDeath = (CauseOfDeath) causeOfDeathField.getValue();
        Disease selectedCauseOfDeathDisease = (Disease) causeOfDeathDiseaseField.getValue();

        if (selectedCauseOfDeath == CauseOfDeath.OTHER_CAUSE) {
            causeOfDeathDetailsField.setVisibility(VISIBLE);
            causeOfDeathDetailsField.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.CAUSE_OF_DEATH_DETAILS));
        } else if (selectedCauseOfDeathDisease == Disease.OTHER) {
            causeOfDeathDetailsField.setVisibility(VISIBLE);
            causeOfDeathDetailsField.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.CAUSE_OF_DEATH_DISEASE_DETAILS));
        } else {
            causeOfDeathDetailsField.setVisibility(GONE);
        }
    }

    /**
     * Only show the occupationDetails field when an appropriate occupation is selected. Additionally,
     * adjust the caption of the occupationDetails field based on the selected occupation.
     */
    protected void initializeOccupationDetailsFieldVisibility(final ControlPropertyField occupationTypeField, final ControlPropertyField occupationDetailsField) {
        setOccupationDetailsFieldVisibility(occupationTypeField, occupationDetailsField);
        occupationTypeField.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                setOccupationDetailsFieldVisibility(occupationTypeField, occupationDetailsField);
            }
        });
    }

    private void setOccupationDetailsFieldVisibility(final ControlPropertyField occupationTypeField, final ControlPropertyField occupationDetailsField) {
        OccupationType selectedOccupationType = (OccupationType) occupationTypeField.getValue();

        switch (selectedOccupationType) {
            case BUSINESSMAN_WOMAN:
                occupationDetailsField.setVisibility(VISIBLE);
                occupationDetailsField.setCaption(I18nProperties.getFieldCaption(PersonDto.I18N_PREFIX + ".business." + PersonDto.OCCUPATION_DETAILS));
                break;
            case TRANSPORTER:
                occupationDetailsField.setVisibility(VISIBLE);
                occupationDetailsField.setCaption(I18nProperties.getFieldCaption(PersonDto.I18N_PREFIX + ".transporter." + PersonDto.OCCUPATION_DETAILS));
                break;
            case HEALTHCARE_WORKER:
                occupationDetailsField.setVisibility(VISIBLE);
                occupationDetailsField.setCaption(I18nProperties.getFieldCaption(PersonDto.I18N_PREFIX + ".healthcare." + PersonDto.OCCUPATION_DETAILS));
                break;
            case OTHER:
                occupationDetailsField.setVisibility(VISIBLE);
                occupationDetailsField.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.OCCUPATION_DETAILS));
                break;
            default:
                occupationDetailsField.setVisibility(GONE);
                break;
        }
    }

    protected void initializeFacilityFields(final ControlSpinnerField regionField, final ControlSpinnerField districtField, final ControlSpinnerField communityField, final ControlSpinnerField facilityField) {
        final List<Item> regions = DataUtils.toItems(DatabaseHelper.getRegionDao().queryForAll());
        final List<Item> districts = DataUtils.toItems(regionField.getValue() != null
                ? DatabaseHelper.getDistrictDao().getByRegion((Region) regionField.getValue())
                : new ArrayList<>(), true);
        final List<Item> communities = DataUtils.toItems(districtField.getValue() != null
                ? DatabaseHelper.getCommunityDao().getByDistrict((District) districtField.getValue())
                : new ArrayList<>(), true);
        final List<Item> facilities = DataUtils.toItems(communityField.getValue() != null
                ? DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity((Community) communityField.getValue(), true, false)
                : districtField.getValue() != null
                ? DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict((District) districtField.getValue(), true, false)
                : new ArrayList<>(), true);

        regionField.initializeSpinner(regions, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Region selectedRegion = (Region) field.getValue();
                if (selectedRegion != null) {
                    districtField.setSpinnerData(DataUtils.toItems(DatabaseHelper.getDistrictDao().getByRegion(selectedRegion)), districtField.getValue());
                } else {
                    districtField.setSpinnerData(null);
                }
            }
        });

        districtField.initializeSpinner(districts, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                District selectedDistrict = (District) field.getValue();
                if (selectedDistrict != null) {
                    communityField.setSpinnerData(DataUtils.toItems(DatabaseHelper.getCommunityDao().getByDistrict(selectedDistrict)), communityField.getValue());
                    facilityField.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict(selectedDistrict, true, false)), facilityField.getValue());
                } else {
                    communityField.setSpinnerData(null);
                    facilityField.setSpinnerData(null);
                }
            }
        });

        communityField.initializeSpinner(communities, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Community selectedCommunity = (Community) field.getValue();
                if (selectedCommunity != null) {
                    facilityField.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity(selectedCommunity, true, false)));
                } else if (districtField.getValue() != null) {
                    facilityField.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict((District) districtField.getValue(), true, false)));
                } else {
                    facilityField.setSpinnerData(null);
                }
            }
        });

        facilityField.initializeSpinner(facilities);
    }

}
