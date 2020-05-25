/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Objects;

import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BaseFragment extends Fragment {

    private FirebaseAnalytics firebaseAnalytics;

    private FieldVisibilityCheckers fieldVisibilityCheckers;
    private FieldAccessCheckers fieldAccessCheckers;

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
        firebaseAnalytics = application.getFirebaseAnalytics();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setFieldVisibilityCheckers(FieldVisibilityCheckers fieldVisibilityCheckers) {
        this.fieldVisibilityCheckers = fieldVisibilityCheckers;
    }

    public void setFieldAccessCheckers(FieldAccessCheckers fieldAccessCheckers) {
        this.fieldAccessCheckers = fieldAccessCheckers;
    }

    public void setFieldVisibilitiesAndAccesses(Class<?> dtoClass, ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ControlPropertyField) {
                String propertyId = ((ControlPropertyField) child).getSubPropertyId();
                boolean visibleAllowed = isVisibleAllowed(dtoClass, propertyId);

                child.setVisibility(visibleAllowed && child.getVisibility() == VISIBLE ? VISIBLE : GONE);
                if(child.isEnabled()  && !isFieldAccessible(dtoClass, propertyId)) {
                    child.setEnabled(false);
                }
            } else if (child instanceof ViewGroup) {
                setFieldVisibilitiesAndAccesses(dtoClass, (ViewGroup) child);
            }
        }
    }

    protected boolean isVisibleAllowed(Class<?> dtoClass, ControlPropertyField field) {
        String propertyId = field.getSubPropertyId();
        return isVisibleAllowed(dtoClass, propertyId);
    }

    private boolean isVisibleAllowed(Class<?> dtoClass, String propertyId) {
        if(fieldVisibilityCheckers == null){
            return true;
        }

        return fieldVisibilityCheckers.isVisible(dtoClass, propertyId);
    }

    private boolean isFieldAccessible(Class<?> dtoClass, String propertyId){
        if(fieldAccessCheckers == null){
            return true;
        }

        return fieldAccessCheckers.isAccessible(dtoClass, propertyId);
    }

    private boolean isFieldHiddenForCurrentCountry(Object propertyId, Class<?> dtoClass) {
        try {
            final java.lang.reflect.Field declaredField =
                    dtoClass.getDeclaredField(propertyId.toString());
            final String countryLocale = ConfigProvider.getServerLocale().toLowerCase();
            if (declaredField.isAnnotationPresent(HideForCountries.class)) {
                final String[] hideForCountries = Objects.requireNonNull(declaredField.getAnnotation(HideForCountries.class)).countries();
                for (String country : hideForCountries) {
                    if (countryLocale.startsWith(country)) {
                        return true;
                    }
                }
            }
            if (declaredField.isAnnotationPresent(HideForCountriesExcept.class)) {
                final String[] hideForCountriesExcept = Objects.requireNonNull(declaredField.getAnnotation(HideForCountriesExcept.class)).countries();
                boolean countryIncluded = false;
                for (String country : hideForCountriesExcept) {
                    if (countryLocale.startsWith(country)) {
                        countryIncluded = true;
                    }
                }
                if (!countryIncluded) {
                    return true;
                }
            }
        } catch (NoSuchFieldException e) {
            return false;
        }
        return false;
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
                if (propertyIdWithoutPrefix.equals(((ControlPropertyField) child).getSubPropertyId())) {
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

    public FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }
}
