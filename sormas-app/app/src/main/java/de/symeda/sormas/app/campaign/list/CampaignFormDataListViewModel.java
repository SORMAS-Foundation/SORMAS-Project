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

import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class CampaignFormDataListViewModel extends ViewModel {

    private LiveData<PagedList<CampaignFormData>> campaignFormDataList;
    private CampaignFormDataFactory campaignFormDataFactory;

    public CampaignFormDataListViewModel() {
        campaignFormDataFactory = new CampaignFormDataFactory();
        CampaignFormDataCriteria campaignFormDataCriteria = new CampaignFormDataCriteria();
        campaignFormDataFactory.setCampaignFormDataCriteria(campaignFormDataCriteria);
        PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(32).setPageSize(16).build();

        LivePagedListBuilder campaignsListBuilder = new LivePagedListBuilder(campaignFormDataFactory, config);
        campaignFormDataList = campaignsListBuilder.build();
    }

    public LiveData<PagedList<CampaignFormData>> getCampaignFormDataList() {
        return campaignFormDataList;
    }

    void notifyCriteriaUpdated() {
        if (campaignFormDataList.getValue() != null) {
            campaignFormDataList.getValue().getDataSource().invalidate();
            if (!campaignFormDataList.getValue().isEmpty()) {
                campaignFormDataList.getValue().loadAround(0);
            }
        }
    }

    public CampaignFormDataCriteria getCriteria() {
        return campaignFormDataFactory.getCampaignFormDataCriteria();
    }

    public static class CampaignFormDataDataSource extends PositionalDataSource<CampaignFormData> {

        private CampaignFormDataCriteria campaignFormDataCriteria;

        CampaignFormDataDataSource(CampaignFormDataCriteria campaignFormDataCriteria) {
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
            List<CampaignFormData> formDataList = DatabaseHelper.getCampaignFormDataDao().queryByCriteria(campaignFormDataCriteria, offset, count);
            callback.onResult(formDataList, offset, (int) totalCount);
        }

        @Override
        public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<CampaignFormData> callback) {
            List<CampaignFormData> formDataList = DatabaseHelper.getCampaignFormDataDao().queryByCriteria(campaignFormDataCriteria, params.startPosition, params.loadSize);
            callback.onResult(formDataList);
        }
    }

    public static class CampaignFormDataFactory extends DataSource.Factory {

        private MutableLiveData<CampaignFormDataDataSource> mutableDataSource;
        private CampaignFormDataDataSource campaignFormDataDataSource;
        private CampaignFormDataCriteria campaignFormDataCriteria;

        CampaignFormDataFactory() {
            this.mutableDataSource = new MutableLiveData<>();
        }

        @NonNull
        @Override
        public DataSource create() {
            campaignFormDataDataSource = new CampaignFormDataDataSource(campaignFormDataCriteria);
            mutableDataSource.postValue(campaignFormDataDataSource);
            return campaignFormDataDataSource;
        }

        public CampaignFormDataCriteria getCampaignFormDataCriteria() {
            return campaignFormDataCriteria;
        }

        public void setCampaignFormDataCriteria(CampaignFormDataCriteria campaignFormDataCriteria) {
            this.campaignFormDataCriteria = campaignFormDataCriteria;
        }
    }
}
