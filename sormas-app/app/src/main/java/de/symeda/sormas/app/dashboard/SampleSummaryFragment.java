package de.symeda.sormas.app.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseSummaryFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.DashboardNavigationCapsule;
import de.symeda.sormas.app.core.IActivityCommunicator;

/**
 * Created by Orson on 08/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class SampleSummaryFragment extends BaseSummaryFragment {

    public static final String TAG = SampleSummaryFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_summary_sample_layout, container, false);
        return view;
    }

    @Override
    protected int getContainerResId() {
        return R.id.fragment_frame_sample;
    }

    public static SampleSummaryFragment newInstance(IActivityCommunicator activityCommunicator, DashboardNavigationCapsule capsule) {
        try {
            return newInstance(activityCommunicator, SampleSummaryFragment.class, capsule);
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (java.lang.InstantiationException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }
}