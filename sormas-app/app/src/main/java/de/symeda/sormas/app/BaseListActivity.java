package de.symeda.sormas.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.symeda.sormas.app.core.ILandingToListNavigationCapsule;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.util.ConstantHelper;

/**
 * Created by Orson on 03/12/2017.
 */

public abstract class BaseListActivity extends AbstractSormasActivity implements IUpdateSubHeadingTitle, INotificationContext {

    private View statusFrame = null;
    private View applicationTitleBar = null;
    private TextView subHeadingListActivityTitle;
    private View fragmentFrame = null;
    private View rootView;
    private MenuItem newMenu = null;
    private BaseListActivityFragment activeFragment = null;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //initializeActivity(savedInstanceState);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean setHomeAsUpIndicator() {
        return false;
    }

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

    protected void initializeBaseActivity(Bundle savedInstanceState) {
        rootView = findViewById(R.id.base_layout);
        subHeadingListActivityTitle = (TextView)findViewById(R.id.subHeadingListActivityTitle);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getIntent().getExtras();
        initializeActivity(arguments);

        if (showTitleBar()) {
            applicationTitleBar = findViewById(R.id.applicationTitleBar);
            statusFrame = findViewById(R.id.statusFrame);
        }

        fragmentFrame = findViewById(R.id.fragment_frame);
        if (fragmentFrame != null) {
            try {
                if (savedInstanceState == null) {
                    // setting the fragment_frame
                    BaseListActivityFragment activeFragment = null;
                    activeFragment = getActiveReadFragment();
                    replaceFragment(activeFragment);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void initializeActivity(Bundle arguments);

    public abstract BaseListActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException;

    public void replaceFragment(BaseListActivityFragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        activeFragment = f;

        if (activeFragment != null) {
            activeFragment.setArguments(getIntent().getExtras());
            ft.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            ft.replace(R.id.fragment_frame, activeFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_action_bar, menu);

        newMenu = menu.findItem(R.id.action_new);

        processActionbarMenu();

        return true;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (newMenu != null)
            newMenu.setVisible(activeFragment.showNewAction());
    }

    public MenuItem getNewMenu() {
        return newMenu;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (applicationTitleBar != null) {
            if (showTitleBar()) {
                applicationTitleBar.setVisibility(View.VISIBLE);

                if (statusFrame != null) {
                    if (showStatusFrame()) {
                        Context statusFrameContext = statusFrame.getContext();

                        Drawable drw = (Drawable) ContextCompat.getDrawable(statusFrameContext, R.drawable.indicator_status_circle);
                        drw.setColorFilter(statusFrameContext.getResources().getColor(getStatusColorResource(statusFrameContext)), PorterDuff.Mode.SRC_OVER);

                        TextView txtStatusName = (TextView)statusFrame.findViewById(R.id.txtStatusName);
                        ImageView imgStatus = (ImageView)statusFrame.findViewById(R.id.statusIcon);


                        txtStatusName.setText(getStatusName(statusFrameContext));
                        imgStatus.setBackground(drw);

                        statusFrame.setVisibility(View.VISIBLE);
                    } else {
                        statusFrame.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            applicationTitleBar.setVisibility(View.GONE);
        }
    }

    public void setSubHeadingTitle(String title) {
        String t = (title == null)? "" : title;

        if (subHeadingListActivityTitle != null)
            subHeadingListActivityTitle.setText(t);
    }

    @Override
    public void updateSubHeadingTitle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSubHeadingTitle(int titleResId) {
        setSubHeadingTitle(getApplicationContext().getResources().getString(titleResId));
    }

    @Override
    public void updateSubHeadingTitle(String title) {
        setSubHeadingTitle(title);
    }

    public abstract Enum getStatus();

    public abstract boolean showStatusFrame();

    public abstract boolean showTitleBar();

    public String getStatusName(Context context) {
        Enum status = getStatus();

        if (status != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, status);
            if (elaborator != null)
                return elaborator.getFriendlyName();
        }

        return "";
    }

    public int getStatusColorResource(Context context) {
        Enum status = getStatus();

        if (status != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, status);
            if (elaborator != null)
                return elaborator.getColorIndicatorResource();
        }

        return R.color.noColor;
    }

    protected String getRecordUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_DATA_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_DATA_UUID);
            }
        }

        return result;
    }

    protected <E extends Enum<E>> E getFilterStatusArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_FILTER_STATUS)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_FILTER_STATUS);
            }
        }

        return e;
    }

    protected SearchBy getSearchStrategyArg(Bundle arguments) {
        SearchBy e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_SEARCH_STRATEGY)) {
                e = (SearchBy) arguments.getSerializable(ConstantHelper.ARG_SEARCH_STRATEGY);
            }
        }

        return e;
    }


    protected <E extends Enum<E>> void SaveFilterStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_FILTER_STATUS, status);
        }
    }

    protected void SaveRecordUuidState(Bundle outState, String recordUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_DATA_UUID, recordUuid);
        }
    }

    protected void SaveSearchStrategyState(Bundle outState, SearchBy status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_FILTER_STATUS, status);
        }
    }

    protected static <TActivity extends AbstractSormasActivity, TCapsule extends ILandingToListNavigationCapsule>
    void goToActivity(Context fromActivity, Class<TActivity> toActivity, TCapsule dataCapsule) {

        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        SearchBy searchBy = dataCapsule.getSearchStrategy();

        Intent intent = new Intent(fromActivity, toActivity);

        if (filterStatus != null)
            intent.putExtra(ConstantHelper.ARG_FILTER_STATUS, filterStatus.getValue());

        if (searchBy != null)
            intent.putExtra(ConstantHelper.ARG_SEARCH_STRATEGY, searchBy);

        fromActivity.startActivity(intent);
    }


    @Override
    public View getRootView() {
        return rootView;
    }
}
