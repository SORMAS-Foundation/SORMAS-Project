package de.symeda.sormas.app.campaign.list;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;

import java.util.List;

import de.symeda.sormas.app.backend.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class CampaignFormDataListViewModel extends ViewModel {

    private LiveData<PagedList<CampaignFormData>> campaignsList;
    private CampaignsDataFactory campaignsDataFactory;

    public CampaignFormDataListViewModel() {
        campaignsDataFactory = new CampaignsDataFactory();
        CampaignFormDataCriteria campaignFormDataCriteria = new CampaignFormDataCriteria();
        campaignsDataFactory.setCampaignFormDataCriteria(campaignFormDataCriteria);
        PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(32).setPageSize(16).build();

        LivePagedListBuilder campaignsListBuilder = new LivePagedListBuilder(campaignsDataFactory, config);
        campaignsList = campaignsListBuilder.build();
    }

    public LiveData<PagedList<CampaignFormData>> getCampaigns() {
        return campaignsList;
    }

    void notifyCriteriaUpdated() {
        if (campaignsList.getValue() != null) {
            campaignsList.getValue().getDataSource().invalidate();
            if (!campaignsList.getValue().isEmpty()) {
                campaignsList.getValue().loadAround(0);
            }
        }
    }

    public CampaignFormDataCriteria getCriteria() {
        return campaignsDataFactory.getCampaignFormDataCriteria();
    }

    public static class CampaignDataSource extends PositionalDataSource<CampaignFormData> {

        private CampaignFormDataCriteria campaignFormDataCriteria;

        CampaignDataSource(CampaignFormDataCriteria campaignFormDataCriteria) {
            this.campaignFormDataCriteria = campaignFormDataCriteria;
        }

        @Override
        public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<CampaignFormData> callback) {
            long totalCount = DatabaseHelper.getCampaignFormDataDao().countByCriteria(campaignFormDataCriteria);
            int offset = params.requestedStartPosition;
            int count = params.requestedLoadSize;
            if (offset + count > totalCount) {
                offset = (int) Math.max(0, totalCount - count);
            }
            List<CampaignFormData> cases = DatabaseHelper.getCampaignFormDataDao().queryByCriteria(campaignFormDataCriteria, offset, count);
            callback.onResult(cases, offset, (int) totalCount);
        }

        @Override
        public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<CampaignFormData> callback) {
            List<CampaignFormData> campaigns = DatabaseHelper.getCampaignFormDataDao().queryByCriteria(campaignFormDataCriteria, params.startPosition, params.loadSize);
            callback.onResult(campaigns);
        }
    }

    public static class CampaignsDataFactory extends DataSource.Factory {

        private MutableLiveData<CampaignDataSource> mutableDataSource;
        private CampaignDataSource campaignDataSource;
        private CampaignFormDataCriteria campaignFormDataCriteria;

        CampaignsDataFactory() {
            this.mutableDataSource = new MutableLiveData<>();
        }

        @NonNull
        @Override
        public DataSource create() {
            campaignDataSource = new CampaignDataSource(campaignFormDataCriteria);
            mutableDataSource.postValue(campaignDataSource);
            return campaignDataSource;
        }

        void setCampaignFormDataCriteria(CampaignFormDataCriteria campaignFormDataCriteria) {
            this.campaignFormDataCriteria = campaignFormDataCriteria;
        }

        CampaignFormDataCriteria getCampaignFormDataCriteria() {
            return campaignFormDataCriteria;
        }
    }
}
