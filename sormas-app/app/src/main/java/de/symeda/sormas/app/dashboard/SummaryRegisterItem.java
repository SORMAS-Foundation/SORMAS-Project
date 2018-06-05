package de.symeda.sormas.app.dashboard;

import de.symeda.sormas.app.BaseSummaryFragment;

/**
 * Created by Orson on 09/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class SummaryRegisterItem {

    private String mTag;
    private BaseSummaryFragment mFragment;
    private boolean mCompleteStatus;

    public SummaryRegisterItem(BaseSummaryFragment mFragment) {
        this.mTag = mFragment.getIdentifier();
        this.mFragment = mFragment;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        this.mTag = tag;
    }

    public BaseSummaryFragment getFragment() {
        return mFragment;
    }

    public void setFragment(BaseSummaryFragment fragment) {
        this.mFragment = fragment;
    }

    public boolean isCompleteStatus() {
        return mCompleteStatus;
    }

    public void setCompleteStatus(boolean completeStatus) {
        this.mCompleteStatus = completeStatus;
    }
}
