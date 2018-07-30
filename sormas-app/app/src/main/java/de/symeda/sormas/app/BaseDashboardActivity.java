package de.symeda.sormas.app;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.dashboard.SummaryRegisterItem;

public abstract class BaseDashboardActivity extends BaseActivity {

    private View fragmentFrame = null;
    private View statusFrame = null;
    private View applicationTitleBar = null;
    private TextView subHeadingListActivityTitle;

    private Map<String, SummaryRegisterItem> activeFragments = new HashMap<String, SummaryRegisterItem>();

    @Override
    protected boolean isSubActivitiy() {
        return false;
    }

    @Override
    protected boolean openPage(PageMenuItem menuItem) {
        throw new UnsupportedOperationException();
    }

    protected void onCreateInner(Bundle savedInstanceState) {
        subHeadingListActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);

        if (showTitleBar()) {
            applicationTitleBar = findViewById(R.id.applicationTitleBar);
            statusFrame = findViewById(R.id.statusFrame);
        }

        fragmentFrame = findViewById(R.id.fragment_frame);
    }

    @Override
    protected void onResume() {
        super.onResume();

        replaceFragments(buildSummaryFragments());
    }

    public boolean showTitleBar() {
        return false;
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

    protected abstract List<BaseSummaryFragment> buildSummaryFragments();

    private void replaceFragments(List<BaseSummaryFragment> fragments) {

        boolean hadFragments = activeFragments != null && !activeFragments.isEmpty();
        this.activeFragments.clear();

        if (fragments != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            for (BaseSummaryFragment f : fragments) {
                if (f == null)
                    continue;

                FrameLayout frame = (FrameLayout) findViewById(f.getContainerResId());

                if (frame == null)
                    continue;

                ft.replace(f.getContainerResId(), f);
                activeFragments.put(f.getIdentifier(), new SummaryRegisterItem(f));

                //frame.setMinimumHeight(getResources().getDimensionPixelSize(f.getMinHeightResId()));
                frame.setVisibility(View.VISIBLE);
            }

            if (hadFragments) {
                ft.addToBackStack(null);
            }
            ft.commit();
        }

        updateStatusFrame();
    }

    @Override
    protected int getRootActivityLayout() {
        return R.layout.activity_root_dashboard_layout;
    }
}
