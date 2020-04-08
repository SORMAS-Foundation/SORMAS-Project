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

package de.symeda.sormas.app.caze.list;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.databinding.FilterCaseListLayoutBinding;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

import static android.view.View.GONE;

public class CaseListActivity extends PagedBaseListActivity {

    private static InvestigationStatus[] statusFilters = new InvestigationStatus[]{null, InvestigationStatus.PENDING, InvestigationStatus.DONE};
    private CaseListViewModel model;
    private FilterCaseListLayoutBinding filterBinding;

    public static void startActivity(Context context, InvestigationStatus listFilter) {
        BaseListActivity.startActivity(context, CaseListActivity.class, buildBundle(getStatusFilterPosition(statusFilters, listFilter)));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showPreloader();
        adapter = new CaseListAdapter();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                // Scroll to the topmost position after cases have been inserted
                if (positionStart == 0) {
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewForList);
                    if (recyclerView != null) {
                        recyclerView.scrollToPosition(0);
                    }
                }
            }
            @Override
            public void onItemRangeMoved(int positionStart, int toPosition, int itemCount) {
                RecyclerView recyclerView = findViewById(R.id.recyclerViewForList);
                if (recyclerView != null) {
                    recyclerView.scrollToPosition(0);
                }
            }
        });
        model = ViewModelProviders.of(this).get(CaseListViewModel.class);
        model.getCases().observe(this, cases -> {
            adapter.submitList(cases);
            hidePreloader();
        });

        filterBinding.setCriteria(model.getCaseCriteria());

        setOpenPageCallback(p -> {
            showPreloader();
            model.getCaseCriteria().setInvestigationStatus(statusFilters[((PageMenuItem) p).getPosition()]);
            model.notifyCriteriaUpdated();
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getIntent().putExtra("refreshOnResume", true);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra("refreshOnResume", false)) {
            showPreloader();
            model.getCases().getValue().getDataSource().invalidate();
        }
    }

    @Override
    public List<PageMenuItem> getPageMenuData() {
        return PageMenuItem.fromEnum(statusFilters, getContext());
    }

    @Override
    protected Callback getSynchronizeResultCallback() {
        // Reload the list after a synchronization has been done
        return () -> {
            showPreloader();
            model.getCases().getValue().getDataSource().invalidate();
        };
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return new Random().nextInt(100);
        //return (int)(new Random(DateTime.now().getMillis() * 1000).nextInt()/10000000);
    }

    @Override
    protected PagedBaseListFragment buildListFragment(PageMenuItem menuItem) {
        if (menuItem != null) {
            InvestigationStatus listFilter = statusFilters[menuItem.getPosition()];
            return CaseListFragment.newInstance(listFilter);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_case);
        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_cases_list;
    }

    @Override
    public void goToNewView() {
        CaseNewActivity.startActivity(getContext());
        finish();
    }

    @Override
    public boolean isEntryCreateAllowed() {
        return ConfigProvider.hasUserRight(UserRight.CASE_CREATE);
    }

    @Override
    public void addFiltersToPageMenu() {
        View caseListFilterView = getLayoutInflater().inflate(R.layout.filter_case_list_layout, null);
        filterBinding = DataBindingUtil.bind(caseListFilterView);

        List<Item> diseases = DataUtils.toItems(DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true));
        filterBinding.diseaseFilter.initializeSpinner(diseases);
        List<Item> classifications = DataUtils.getEnumItems(CaseClassification.class);
        filterBinding.classificationFilter.initializeSpinner(classifications);
        List<Item> outcomes = DataUtils.getEnumItems(CaseOutcome.class);
        filterBinding.outcomeFilter.initializeSpinner(outcomes);

        if (UserRole.isPortHealthUser(ConfigProvider.getUser().getUserRoles())) {
            filterBinding.originFilter.setVisibility(GONE);
        } else {
            List<Item> caseOrigins = DataUtils.getEnumItems(CaseOrigin.class);
            filterBinding.originFilter.initializeSpinner(caseOrigins);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        List<Item> epiWeeks = DataUtils.toItems(DateHelper.createEpiWeekList(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR)));
        filterBinding.epiWeekFromFilter.initializeSpinner(epiWeeks);
        filterBinding.epiWeekToFilter.initializeSpinner(epiWeeks);

        pageMenu.addFilter(caseListFilterView);

        filterBinding.applyFilters.setOnClickListener(e -> {
            showPreloader();
            pageMenu.hideAll();
            model.notifyCriteriaUpdated();
        });

        filterBinding.resetFilters.setOnClickListener(e -> {
            showPreloader();
            pageMenu.hideAll();
            model.getCaseCriteria().setTextFilter(null);
            model.getCaseCriteria().setDisease(null);
            model.getCaseCriteria().setCaseClassification(null);
            model.getCaseCriteria().setOutcome(null);
            model.getCaseCriteria().setEpiWeekFrom(null);
            model.getCaseCriteria().setEpiWeekTo(null);
            model.getCaseCriteria().setCaseOrigin(null);
            filterBinding.invalidateAll();
            filterBinding.executePendingBindings();
            model.notifyCriteriaUpdated();
        });
    }

}
