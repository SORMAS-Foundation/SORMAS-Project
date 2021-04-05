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

import de.symeda.sormas.app.backend.campaign.data.CampaignFormCriteria;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class CampaignListViewModel extends ViewModel {

    private LiveData<PagedList<CampaignFormData>> campaignsList;
    private CampaignsDataFactory campaignsDataFactory;

    public CampaignListViewModel() {
        campaignsDataFactory = new CampaignsDataFactory();
        CampaignFormCriteria campaignFormCriteria = new CampaignFormCriteria();
        campaignsDataFactory.setCampaignFormCriteria(campaignFormCriteria);
        PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(32).setPageSize(16).build();

        LivePagedListBuilder campaignsListBuilder = new LivePagedListBuilder(campaignsDataFactory, config);
        campaignsList = campaignsListBuilder.build();
    }

    public LiveData<PagedList<CampaignFormData>> getCampaigns() {
        return campaignsList;
    }

    public static class CampaignDataSource extends PositionalDataSource<CampaignFormData> {

        private CampaignFormCriteria campaignFormCriteria;

        CampaignDataSource(CampaignFormCriteria campaignFormCriteria) {
            this.campaignFormCriteria = campaignFormCriteria;
        }

        @Override
        public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<CampaignFormData> callback) {
            long totalCount = DatabaseHelper.getCampaignFormDataDao().countByCriteria(campaignFormCriteria);
            int offset = params.requestedStartPosition;
            int count = params.requestedLoadSize;
            if (offset + count > totalCount) {
                offset = (int) Math.max(0, totalCount - count);
            }
            List<CampaignFormData> cases = DatabaseHelper.getCampaignFormDataDao().queryByCriteria(campaignFormCriteria, offset, count);
            callback.onResult(cases, offset, (int) totalCount);
        }

        @Override
        public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<CampaignFormData> callback) {
            List<CampaignFormData> campaigns = DatabaseHelper.getCampaignFormDataDao().queryByCriteria(campaignFormCriteria, params.startPosition, params.loadSize);
            callback.onResult(campaigns);
        }
    }

    public static class CampaignsDataFactory extends DataSource.Factory {

        private MutableLiveData<CampaignDataSource> mutableDataSource;
        private CampaignDataSource campaignDataSource;
        private CampaignFormCriteria campaignFormCriteria;

        CampaignsDataFactory() {
            this.mutableDataSource = new MutableLiveData<>();
        }

        @NonNull
        @Override
        public DataSource create() {
            campaignDataSource = new CampaignDataSource(campaignFormCriteria);
            mutableDataSource.postValue(campaignDataSource);
            return campaignDataSource;
        }

        void setCampaignFormCriteria(CampaignFormCriteria campaignFormCriteria) {
            this.campaignFormCriteria = campaignFormCriteria;
        }

        CampaignFormCriteria getCampaignFormCriteria() {
            return campaignFormCriteria;
        }
    }
}
