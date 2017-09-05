package de.symeda.sormas.app.util;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.component.PropertyField;

public abstract class FormTab extends DialogFragment implements FormFragment {

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

    protected void setVisibilityByDisease(Class<?> fieldsDtoClazz, Disease disease, ViewGroup viewGroup) {
        for (int i=0; i<viewGroup.getChildCount(); i++){
            View child = viewGroup.getChildAt(i);
            if (child instanceof PropertyField) {
                String propertyId = ((PropertyField)child).getPropertyId();
                boolean definedOrMissing = Diseases.DiseasesConfiguration.isDefinedOrMissing(fieldsDtoClazz, propertyId, disease);
                child.setVisibility(definedOrMissing ? View.VISIBLE : View.GONE);
            }
            else if (child instanceof ViewGroup) {
                setVisibilityByDisease(fieldsDtoClazz, disease, (ViewGroup)child);
            }
        }
    }

    protected void setFieldVisibleOrGone(View v, boolean visible) {
        if (visible) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
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
