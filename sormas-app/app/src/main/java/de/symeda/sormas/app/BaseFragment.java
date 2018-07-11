package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlTextReadField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BaseFragment extends Fragment {

    protected UserRight editOrCreateUserRight;
    protected Tracker tracker;

    public BaseActivity getBaseActivity() {
        return (BaseActivity)getActivity();
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
        for (int i = 0; i < viewGroup.getChildCount(); i++){
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

    protected void setVisibleWhen(ControlPropertyField field, ControlPropertyField sourceField, final Object sourceValue) {
        if (sourceField.getInternalValue() != null && sourceField.getInternalValue().equals(sourceValue)) {
            field.setVisibility(VISIBLE);
        } else {
            field.setVisibility(GONE);
        }

        sourceField.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                if (field.getInternalValue() != null && field.getInternalValue().equals(sourceValue)) {
                    field.setVisibility(VISIBLE);
                } else {
                    field.hideField();
                }
            }
        });
    }

    protected void setHealthFacilityDetailsFieldVisibility(ControlPropertyField healthFacilityField, ControlPropertyField healthFacilityDetailsField) {
        Facility selectedFacility = (Facility) healthFacilityField.getInternalValue();

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

}
