package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;

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
                    getBaseActivity().synchronizeChangedData();
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

    protected ControlPropertyField findFieldByPropertyId(String propertyIdWithoutPrefix, ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ControlPropertyField) {
                if (propertyIdWithoutPrefix.equals(((ControlPropertyField) child).getPropertyIdWithoutPrefix())) {
                    return (ControlPropertyField)child;
                }
            } else if (child instanceof ViewGroup) {
                ControlPropertyField field = findFieldByPropertyId(propertyIdWithoutPrefix, (ViewGroup) child);
                if (field != null) {
                    return field;
                }
            }
        }
        return null;
    }
}
