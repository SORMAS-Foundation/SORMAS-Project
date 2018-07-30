package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BaseFragment extends Fragment {

    protected Tracker tracker;

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected static <TFragment extends BaseFragment> TFragment newInstance(Class<TFragment> fragmentClass, Bundle data) {

        TFragment fragment;
        try {
            fragment = fragmentClass.newInstance();
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (data != null) fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SormasApplication application = (SormasApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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
                    return (ControlPropertyField) child;
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
