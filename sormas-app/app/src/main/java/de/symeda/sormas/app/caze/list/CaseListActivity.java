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

import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.databinding.FilterCaseListLayoutBinding;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationHelper;

public class CaseListActivity extends PagedBaseListActivity {

    private InvestigationStatus statusFilters[] = new InvestigationStatus[]{InvestigationStatus.PENDING, InvestigationStatus.DONE, InvestigationStatus.DISCARDED};
    private CaseListViewModel model;

    public static void startActivity(Context context, InvestigationStatus listFilter) {
        BaseListActivity.startActivity(context, CaseListActivity.class, buildBundle(listFilter));
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
        });
        model = ViewModelProviders.of(this).get(CaseListViewModel.class);
        model.getCases().observe(this, cases -> {
            adapter.submitList(cases);
            hidePreloader();
        });

        setOpenPageCallback(p -> {
            showPreloader();
            model.getCaseCriteria().investigationStatus(statusFilters[((PageMenuItem) p).getKey()]);
            model.notifyCriteriaUpdated();
        });
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
            InvestigationStatus listFilter = statusFilters[menuItem.getKey()];
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
        FilterCaseListLayoutBinding caseListFilterBinding = DataBindingUtil.bind(caseListFilterView);

        List<Item> diseases = DataUtils.toItems(DiseaseConfigurationHelper.getInstance().getAllActivePrimaryDiseases());
        caseListFilterBinding.diseaseFilter.initializeSpinner(diseases);
        List<Item> classifications = DataUtils.getEnumItems(CaseClassification.class);
        caseListFilterBinding.classificationFilter.initializeSpinner(classifications);
        List<Item> outcomes = DataUtils.getEnumItems(CaseOutcome.class);
        caseListFilterBinding.outcomeFilter.initializeSpinner(outcomes);

        pageMenu.addFilter(caseListFilterView);

        caseListFilterBinding.textFilter.addValueChangedListener(e -> {
            model.getCaseCriteria().textFilter((String) e.getValue());
        });
        caseListFilterBinding.diseaseFilter.addValueChangedListener(e -> {
            model.getCaseCriteria().disease((Disease) e.getValue());
        });
        caseListFilterBinding.classificationFilter.addValueChangedListener(e -> {
            model.getCaseCriteria().caseClassification((CaseClassification) e.getValue());
        });
        caseListFilterBinding.outcomeFilter.addValueChangedListener(e -> {
            model.getCaseCriteria().outcome((CaseOutcome) e.getValue());
        });

        caseListFilterBinding.applyFilters.setOnClickListener(e -> {
            showPreloader();
            model.notifyCriteriaUpdated();
        });

        caseListFilterBinding.resetFilters.setOnClickListener(e -> {
            showPreloader();
            caseListFilterBinding.textFilter.setValue(null);
            caseListFilterBinding.diseaseFilter.setValue(null);
            caseListFilterBinding.classificationFilter.setValue(null);
            caseListFilterBinding.outcomeFilter.setValue(null);
            model.notifyCriteriaUpdated();
        });

    }

}
