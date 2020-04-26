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

package de.symeda.sormas.app.sample.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Random;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.CommonStatusCodes;

import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.barcode.BarcodeActivity;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.FilterCaseListLayoutBinding;
import de.symeda.sormas.app.databinding.FilterSampleListLayoutBinding;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.sample.read.SampleReadActivity;
import de.symeda.sormas.app.util.Callback;

import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

public class SampleListActivity extends PagedBaseListActivity {

    private static ShipmentStatus[] statusFilters = new ShipmentStatus[]{null,
            ShipmentStatus.NOT_SHIPPED, ShipmentStatus.SHIPPED,
            ShipmentStatus.RECEIVED, ShipmentStatus.REFERRED_OTHER_LAB
    };
    private SampleListViewModel model;
    private FilterSampleListLayoutBinding filterBinding;

    public static void startActivity(Context context, ShipmentStatus listFilter) {
        BaseListActivity.startActivity(context, SampleListActivity.class, buildBundle(getStatusFilterPosition(statusFilters, listFilter)));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showPreloader();
        adapter = new SampleListAdapter();
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
        model = ViewModelProviders.of(this).get(SampleListViewModel.class);
        model.initializeViewModel();
        model.getSamples().observe(this, samples -> {
            adapter.submitList(samples);
            hidePreloader();
        });
        setOpenPageCallback(p -> {
            showPreloader();
            model.getSampleCriteria().shipmentStatus(statusFilters[((PageMenuItem) p).getPosition()]);
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
            model.getSamples().getValue().getDataSource().invalidate();
        }
    }

    @Override
    public List<PageMenuItem> getPageMenuData(){
        return PageMenuItem.fromEnum(statusFilters, getContext());
    }

    @Override
    protected Callback getSynchronizeResultCallback() {
        // Reload the list after a synchronization has been done
        return () -> {
            showPreloader();
            model.getSamples().getValue().getDataSource().invalidate();
        };
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int) (new Random(DateTime.now().getMillis() * 1000).nextInt() / 10000000);
    }

    @Override
    protected PagedBaseListFragment buildListFragment(PageMenuItem menuItem) {
        if (menuItem != null) {
            ShipmentStatus listFilter = statusFilters[menuItem.getPosition()];
            return SampleListFragment.newInstance(listFilter);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_sample);
        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_samples_list;
    }

    @Override
    public void addFiltersToPageMenu() {
        View sampleListFilterView = getLayoutInflater().inflate(R.layout.filter_sample_list_layout, null);
        filterBinding = DataBindingUtil.bind(sampleListFilterView);

        pageMenu.addFilter(sampleListFilterView);

        filterBinding.scanFieldSampleId.setOnClickListener(e -> {
            Intent intent = new Intent(this, BarcodeActivity.class);
            startActivityForResult(intent, BarcodeActivity.RC_BARCODE_CAPTURE);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BarcodeActivity.RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                showPreloader();
                Sample sample = DatabaseHelper.getSampleDao().queryByFieldSampleId(data.getStringExtra(BarcodeActivity.BARCODE_RESULT));
                if(sample !=null)
                    SampleReadActivity.startActivity(getContext(), sample.getUuid());
                else
                    NotificationHelper.showNotification(this, WARNING, getString(R.string.sample_not_found));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
