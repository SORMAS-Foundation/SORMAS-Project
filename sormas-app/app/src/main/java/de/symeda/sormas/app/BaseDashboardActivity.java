package de.symeda.sormas.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.app.core.IDashboardNavigationCapsule;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.dashboard.SummaryRegisterItem;
import de.symeda.sormas.app.util.ConstantHelper;

/**
 * Created by Orson on 08/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public abstract class BaseDashboardActivity extends AbstractSormasActivity implements INotificationContext { //, ISummaryLoadingStatusCommunicator

    private View rootView;
    private View fragmentFrame = null;
    private View statusFrame = null;
    private View applicationTitleBar = null;
    private TextView subHeadingListActivityTitle;

    private Enum pageStatus;
    /*private List<SummaryRegisterItem> mRegisteredFragments = null;
    private List<SummaryRegisterItem> mRegisteredFragments = null;*/
    private Map<String, SummaryRegisterItem> mRegisteredFragments = new HashMap<String, SummaryRegisterItem>();
    //private Map<String, ICallback<BoolResult>> mSummaryCompleteCallbacks = new HashMap<String, ICallback<BoolResult>>();

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //initializeActivity(savedInstanceState);
        initializeActivity(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean setHomeAsUpIndicator() {
        return true;
    }

    protected void initializeBaseActivity(Bundle savedInstanceState) {
        rootView = findViewById(R.id.base_layout);
        subHeadingListActivityTitle = (TextView)findViewById(R.id.subHeadingListActivityTitle);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA);

        pageStatus = getPageStatusArg(arguments);

        initializeActivity(arguments);

        if (showTitleBar()) {
            applicationTitleBar = findViewById(R.id.applicationTitleBar);
            statusFrame = findViewById(R.id.statusFrame);
        }

        fragmentFrame = findViewById(R.id.fragment_frame);
        if (fragmentFrame != null && savedInstanceState == null) {
            regiserSummaryFragments();
            replaceFragment(mRegisteredFragments);
        }
    }

    private void regiserSummaryFragments() {
        for(BaseSummaryFragment f: getSummaryFragments()) {
            mRegisteredFragments.put(f.getIdentifier(), new SummaryRegisterItem(f));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (applicationTitleBar != null && showTitleBar()) {
            applicationTitleBar.setVisibility(View.VISIBLE);

            if (statusFrame != null && showStatusFrame()) {
                Context statusFrameContext = statusFrame.getContext();

                Drawable drw = (Drawable) ContextCompat.getDrawable(statusFrameContext, R.drawable.indicator_status_circle);
                drw.setColorFilter(statusFrameContext.getResources().getColor(getStatusColorResource(statusFrameContext)), PorterDuff.Mode.SRC);

                TextView txtStatusName = (TextView)statusFrame.findViewById(R.id.txtStatusName);
                ImageView imgStatus = (ImageView)statusFrame.findViewById(R.id.statusIcon);


                txtStatusName.setText(getStatusName(statusFrameContext));
                imgStatus.setBackground(drw);

                statusFrame.setVisibility(View.VISIBLE);
            }
        }


    }

    //<editor-fold desc="Public Methods">

    @Override
    public void showFragmentView() {
        if (fragmentFrame != null)
            fragmentFrame.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFragmentView() {
        if (fragmentFrame != null)
            fragmentFrame.setVisibility(View.GONE);
    }

    public boolean showTitleBar() {
        return false;
    }

    public boolean showStatusFrame() {
        return false;
    }

    public Enum getPageStatus() {
        return pageStatus;
    }

    public int getStatusColorResource(Context context) {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, pageStatus);
            if (elaborator != null)
                return elaborator.getColorIndicatorResource();
        }

        return R.color.noColor;
    }

    public String getStatusName(Context context) {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, pageStatus);
            if (elaborator != null)
                return elaborator.getFriendlyName();
        }

        return "";
    }

    @Override
    public View getRootView() {
        return rootView;
    }
    //</editor-fold>

    //<editor-fold desc="Abstract Methods">
    protected abstract void initializeActivity(Bundle arguments);

    protected abstract List<BaseSummaryFragment> getSummaryFragments();
    //</editor-fold>

    //<editor-fold desc="Private Methods">
    private void replaceFragment(Map<String, SummaryRegisterItem> items) {
        FragmentManager manager = getSupportFragmentManager();

        if (items != null) {
            this.mRegisteredFragments = items;

            for (Map.Entry<String, SummaryRegisterItem> item : items.entrySet()) {
                if (item == null)
                    continue;

                BaseSummaryFragment f = item.getValue().getFragment();

                if (f == null)
                    continue;

                if (f.getArguments() == null)
                    f.setArguments(getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA));

                FragmentTransaction ft = manager.beginTransaction();

                FrameLayout frame = (FrameLayout)findViewById(f.getContainerResId());

                if (frame == null)
                    continue;

                ft.replace(f.getContainerResId(), f);
                ft.addToBackStack(null);
                ft.commit();

                //frame.setMinimumHeight(getResources().getDimensionPixelSize(f.getMinHeightResId()));
                frame.setVisibility(View.VISIBLE);
            }

        }
    }
    //</editor-fold>

    //<editor-fold desc="Navigation Capsule Stuff">

    protected <E extends Enum<E>> E getPageStatusArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_PAGE_STATUS)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_PAGE_STATUS);
            }
        }

        return e;
    }

    protected <E extends Enum<E>> void SavePageStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_PAGE_STATUS, status);
        }
    }

    //</editor-fold>

    //<editor-fold desc="Overrides">
    @Override
    protected int getRootActivityLayout() {
        return R.layout.activity_root_dashboard_layout;
    }
    //</editor-fold>

    protected static <TActivity extends AbstractSormasActivity, TCapsule extends IDashboardNavigationCapsule>
    void goToActivity(Context fromActivity, Class<TActivity> toActivity, TCapsule dataCapsule) {

        IStatusElaborator pageStatus = dataCapsule.getPageStatus();
        Intent intent = new Intent(fromActivity, toActivity);
        Bundle bundle = new Bundle();

        if (pageStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_PAGE_STATUS, pageStatus.getValue());

        intent.putExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA, bundle);

        fromActivity.startActivity(intent);
    }

    /*@Override
    public void registerOnSummaryLoadingCompletedCallback(String identifier, ICallback<BoolResult> callback) {
        if (!mSummaryCompleteCallbacks.containsKey(identifier))
            mSummaryCompleteCallbacks.put(identifier, callback);
    }

    @Override
    public void loadingCompleted(String identifier) {
        SummaryRegisterItem item = mRegisteredFragments.get(identifier);
        item.setCompleteStatus(true);

        boolean allComplete = true;
        for (Map.Entry<String, SummaryRegisterItem> entry : mRegisteredFragments.entrySet()) {
            allComplete = allComplete && entry.getValue().isCompleteStatus();
        }

        if (allComplete) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (Map.Entry<String, ICallback<BoolResult>> entry : mSummaryCompleteCallbacks.entrySet()) {
                        ICallback<BoolResult> callback = entry.getValue();
                        callback.call(BoolResult.TRUE);
                    }
                }
            }, 5000);
        }

    }*/
}
