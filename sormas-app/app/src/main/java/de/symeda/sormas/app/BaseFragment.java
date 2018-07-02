package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.controls.ControlPropertyField;

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
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
            v.clearFocus();
        }
    }

    protected void setFieldVisible(View v, boolean visible) {
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

    protected void setVisibilityByDisease(Class<?> fieldsDtoClazz, Disease disease, ViewGroup viewGroup) {
        for (int i=0; i<viewGroup.getChildCount(); i++){
            View child = viewGroup.getChildAt(i);
            if (child instanceof ControlPropertyField) {
                String propertyId = ((ControlPropertyField)child).getPropertyId();
                boolean definedOrMissing = Diseases.DiseasesConfiguration.isDefinedOrMissing(fieldsDtoClazz, propertyId, disease);
                child.setVisibility(definedOrMissing ? View.VISIBLE : View.GONE);
            }
            else if (child instanceof ViewGroup) {
                setVisibilityByDisease(fieldsDtoClazz, disease, (ViewGroup)child);
            }
        }
    }
}
