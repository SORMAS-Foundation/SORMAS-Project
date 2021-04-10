package de.symeda.sormas.app.campaign.list;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.Campaign;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.databinding.FilterCampaignFormDataListLayoutBinding;
import de.symeda.sormas.app.util.Callback;

public class CampaignFormDataListActivity extends PagedBaseListActivity {

    private static InvestigationStatus[] statusFilters = new InvestigationStatus[] {
            null,
            InvestigationStatus.PENDING,
            InvestigationStatus.DONE };

    private CampaignFormDataListViewModel model;
    private FilterCampaignFormDataListLayoutBinding filterBinding;

    public static void startActivity(Context context) {
        List<Campaign> activeCampaigns = DatabaseHelper.getCampaignDao().getAllActive();
        int pageMenuPosition = activeCampaigns.size() > 0 ? 1 : 0;
        BaseListActivity.startActivity(context, CampaignFormDataListActivity.class, buildBundle(pageMenuPosition));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showPreloader();
        adapter = new CampaignFormDataListAdapter();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
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

        model = ViewModelProviders.of(this).get(CampaignFormDataListViewModel.class);
        model.getCriteria().setCampaign(DatabaseHelper.getCampaignDao().getLastStartedCampaign());
        model.getCampaigns().observe(this, campaigns -> {
            adapter.submitList(campaigns);
            hidePreloader();
        });

        filterBinding.setCriteria(model.getCriteria());

        setOpenPageCallback(p -> {
            showPreloader();
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
            if (model.getCampaigns().getValue() != null) {
                model.getCampaigns().getValue().getDataSource().invalidate();
            }
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
            model.getCampaigns().getValue().getDataSource().invalidate();
        };
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
        return 0;
    }

    @Override
    protected PagedBaseListFragment buildListFragment(PageMenuItem menuItem) {
        return CampaignFormDataListFragment.newInstance();
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_campaigns_list;
    }

    @Override
    public void addFiltersToPageMenu() {
        View campaignsFormDataListFilterView = getLayoutInflater().inflate(R.layout.filter_campaign_form_data_list_layout, null);
        filterBinding = DataBindingUtil.bind(campaignsFormDataListFilterView);

        List<Item> campaigns = campaignsToItems(DatabaseHelper.getCampaignDao().getAllActive());
        filterBinding.campaignFilter.initializeSpinner(campaigns);

        pageMenu.addFilter(campaignsFormDataListFilterView);

        filterBinding.applyFilters.setOnClickListener(e -> {
            showPreloader();
            pageMenu.hideAll();
            model.notifyCriteriaUpdated();
        });

        filterBinding.resetFilters.setOnClickListener(e -> {
            showPreloader();
            pageMenu.hideAll();
            model.getCriteria().setCampaign(DatabaseHelper.getCampaignDao().getLastStartedCampaign());
            filterBinding.invalidateAll();
            filterBinding.executePendingBindings();
            model.notifyCriteriaUpdated();
        });
    }

    private List<Item> campaignsToItems(List<Campaign> campaigns) {
        List<Item> listOut = new ArrayList<>();
        listOut.add(new Item<Integer>("", null));
        for (Campaign campaign : campaigns) {
            listOut.add(new Item<>(campaign.getName(), campaign));
        }
        return listOut;
    }
}
