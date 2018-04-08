package de.symeda.sormas.app;

import android.os.Bundle;

import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IDashboardNavigationCapsule;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.util.ConstantHelper;

/**
 * Created by Orson on 08/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public abstract class BaseSummaryFragment extends BaseFragment {


    private IActivityCommunicator activityCommunicator;

    protected abstract int getContainerResId();

    public int getMinHeightResId() {
        return R.dimen.summaryFragmentMinHeight;
    }



    protected void setActivityCommunicator(IActivityCommunicator activityCommunicator) {
        this.activityCommunicator = activityCommunicator;
    }

    protected static <TFragment extends BaseSummaryFragment, TCapsule extends IDashboardNavigationCapsule> TFragment newInstance(IActivityCommunicator activityCommunicator, Class<TFragment> f, TCapsule dataCapsule) throws IllegalAccessException, java.lang.InstantiationException {
        TFragment fragment = f.newInstance();

        fragment.setActivityCommunicator(activityCommunicator);

        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }

        IStatusElaborator pageStatus = dataCapsule.getPageStatus();

        if (pageStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_PAGE_STATUS, pageStatus.getValue());

        fragment.setArguments(bundle);
        return fragment;
    }
}
