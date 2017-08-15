package de.symeda.sormas.app;

import android.accounts.AuthenticatorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.analytics.Tracker;

import java.net.ConnectException;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.synclog.SyncLogDao;
import de.symeda.sormas.app.caze.CaseEditTabs;
import de.symeda.sormas.app.component.SyncLogDialog;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.SlidingTabLayout;
import de.symeda.sormas.app.util.SyncCallback;

public abstract class AbstractTabActivity extends AbstractSormasActivity {

    public static final String KEY_PAGE = "page";

    protected ViewPager pager;
    protected SlidingTabLayout tabs;
    protected int currentTab = 0;

    protected void createTabViews(FragmentStatePagerAdapter adapter) {
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(false);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
            @Override
            public void onPageSelected(int position) {
                setCurrentTab(position);
                invalidateOptionsMenu();
            }
        });
    }


    public AbstractDomainObject getData(int position) {
        FormTab tab = getTabByPosition(position);
        if (tab != null) {
            return tab.getData();
        }
        return null;
    }

    public FormTab getTabByPosition(int position) {
        Object item = pager.getAdapter().instantiateItem(pager, position);
        if (item instanceof FormTab) {
            return (FormTab)item;
        }
        return null;
    }

    public void setCurrentTab(int currentTab) {
        this.currentTab = currentTab;
    }

    public void reloadTabs() {
        createTabViews((FragmentStatePagerAdapter) pager.getAdapter());
    }
}
