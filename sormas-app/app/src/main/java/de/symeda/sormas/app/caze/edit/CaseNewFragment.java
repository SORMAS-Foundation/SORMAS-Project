package de.symeda.sormas.app.caze.edit;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentCaseNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureHelper;


public class CaseNewFragment extends BaseEditFragment<FragmentCaseNewLayoutBinding, Case, Case> {

    public static final String TAG = CaseNewFragment.class.getSimpleName();

    private Case record;

    private List<Item> diseaseList;
    private List<Item> plagueTypeList;
    private List<Item> dengueFeverTypeList;
    private List<Item> initialRegions;
    private List<Item> initialDistricts;
    private List<Item> initialCommunities;
    private List<Item> initialFacilities;

    public static CaseNewFragment newInstance(Case activityRootData) {
        return newInstance(CaseNewFragment.class, null, activityRootData);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_new_case);
    }

    @Override
    public Case getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();

        diseaseList = DataUtils.getEnumItems(Disease.class, true);
        plagueTypeList = DataUtils.getEnumItems(PlagueType.class, true);
        dengueFeverTypeList = DataUtils.getEnumItems(DengueFeverType.class, true);

        initialRegions = InfrastructureHelper.loadRegions();
        initialDistricts = InfrastructureHelper.loadDistricts(record.getRegion());
        initialCommunities = InfrastructureHelper.loadCommunities(record.getDistrict());
        initialFacilities = InfrastructureHelper.loadFacilities(record.getDistrict(), record.getCommunity());
    }

    @Override
    public void onLayoutBinding(FragmentCaseNewLayoutBinding contentBinding) {
        contentBinding.setData(record);

        if (isLiveValidationDisabled()) {
            disableLiveValidation(true);
        }

        InfrastructureHelper.initializeFacilityFields(contentBinding.caseDataRegion, initialRegions,
                contentBinding.caseDataDistrict, initialDistricts,
                contentBinding.caseDataCommunity, initialCommunities,
                contentBinding.caseDataHealthFacility, initialFacilities);

        contentBinding.caseDataDisease.initializeSpinner(diseaseList);
        contentBinding.caseDataPlagueType.initializeSpinner(plagueTypeList);
        contentBinding.caseDataDengueFeverType.initializeSpinner(dengueFeverTypeList);
    }

    @Override
    public void onAfterLayoutBinding(final FragmentCaseNewLayoutBinding contentBinding) {
        InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);

        contentBinding.caseDataRegion.setEnabled(false);
        contentBinding.caseDataRegion.setRequired(false);
        contentBinding.caseDataDistrict.setEnabled(false);
        contentBinding.caseDataDistrict.setRequired(false);

        User user = ConfigProvider.getUser();
        if (user.hasUserRole(UserRole.INFORMANT) && user.getHealthFacility() != null) {
            // Informants are not allowed to create cases in another health facility
            contentBinding.caseDataCommunity.setEnabled(false);
            contentBinding.caseDataCommunity.setRequired(false);
            contentBinding.caseDataHealthFacility.setEnabled(false);
            contentBinding.caseDataHealthFacility.setRequired(false);
        }
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_new_layout;
    }
}
