package de.symeda.sormas.app.sample.edit;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.YesNo;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.validation.SampleValidator;

public class SampleEditFragment extends BaseEditFragment<FragmentSampleEditLayoutBinding, Sample, Sample> {

    private Sample record;
    private SampleTest mostRecentTest;

    private IEntryItemOnClickListener onRecentTestItemClickListener;

    private List<Item> sampleMaterialList;
    private List<Item> testTypeList;
    private List<Item> sampleSourceList;
    private List<Facility> labList;

    private OnLinkClickListener referralLinkCallback;

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_sample_information);
    }

    @Override
    public Sample getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(record);

        labList = DatabaseHelper.getFacilityDao().getLaboratories(true);
        sampleMaterialList = DataUtils.getEnumItems(SampleMaterial.class, false);
        testTypeList = DataUtils.getEnumItems(SampleTestType.class, false);
        sampleSourceList = DataUtils.getEnumItems(SampleSource.class, false);
    }

    @Override
    public void onLayoutBinding(FragmentSampleEditLayoutBinding contentBinding) {
        if (record == null)
            return;

        setupCallback();

        if (!record.isShipped()) {
            contentBinding.sampleShipmentDate.setVisibility(View.GONE);
            contentBinding.sampleShipmentDetails.setVisibility(View.GONE);
        }

        if (record.getSampleMaterial() == SampleMaterial.OTHER) {
            contentBinding.sampleSampleMaterialText.setVisibility(View.INVISIBLE);
        }

        if (record.getLab().getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
            contentBinding.sampleLabDetails.setVisibility(View.VISIBLE);
        }

        if (record.getAssociatedCase().getDisease() != Disease.NEW_INFLUENCA) {
            contentBinding.sampleSampleSource.setVisibility(View.GONE);
        }

        if (record.isReceived()) {
            contentBinding.sampleReceivedLayout.setVisibility(View.VISIBLE);
        }

        if (record.getSpecimenCondition() != SpecimenCondition.NOT_ADEQUATE) {
            contentBinding.recentTestLayout.setVisibility(View.VISIBLE);
            if (mostRecentTest != null) {
                contentBinding.sampleSuggestedTypeOfTest.setVisibility(View.VISIBLE);
                //contentBinding.sampleTestResult.setVisibility(View.VISIBLE);
            } else {
                contentBinding.sampleNoRecentTestText.setVisibility(View.VISIBLE);
            }
        }

        // only show referred to field when there is a referred sample
        if (record.getReferredTo() != null) {
            final Sample referredSample = record.getReferredTo();
            contentBinding.sampleReferredTo.setVisibility(View.VISIBLE);
            contentBinding.sampleReferredTo.setValue(getActivity().getResources().getString(R.string.sample_referred_to) + " " + referredSample.getLab().toString() + " " + "\u279D");
        } else {
            contentBinding.sampleReferredTo.setVisibility(View.GONE);
        }

        //TODO: Set required hints for sample data
        SampleValidator.setRequiredHintsForSampleData(contentBinding);

        contentBinding.setData(record);
        contentBinding.setCaze(record.getAssociatedCase());
        contentBinding.setLab(record.getLab());
        contentBinding.setResults(getTestResults());
        contentBinding.setRecentTestItemClickCallback(onRecentTestItemClickListener);

        contentBinding.setYesNoClass(YesNo.class);
        contentBinding.setReferralLinkCallback(referralLinkCallback);
    }

    @Override
    public void onAfterLayoutBinding(final FragmentSampleEditLayoutBinding contentBinding) {
        contentBinding.sampleSuggestedTypeOfTest.initializeSpinner(testTypeList);

        contentBinding.sampleSampleSource.initializeSpinner(sampleSourceList);

        contentBinding.sampleSampleMaterial.initializeSpinner(sampleMaterialList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                SampleMaterial material = (SampleMaterial) field.getValue();

                if (material == SampleMaterial.OTHER) {
                    contentBinding.sampleSampleMaterialText.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.sampleSampleMaterialText.setVisibility(View.INVISIBLE);
                    contentBinding.sampleSampleMaterialText.setValue("");
                }
            }
        });

        contentBinding.sampleLab.initializeSpinner(DataUtils.toItems(labList), null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Facility laboratory = (Facility) field.getValue();

                if (laboratory != null && laboratory.getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
                    contentBinding.sampleLabDetails.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.sampleLabDetails.setVisibility(View.GONE);
                    contentBinding.sampleLabDetails.setValue("");
                }
            }
        });

        contentBinding.sampleSampleDateTime.initializeDateTimeField(getFragmentManager());
        contentBinding.sampleShipmentDate.initializeDateField(getFragmentManager());

        //TODO: Properly disable Tebo controls
        if (!ConfigProvider.getUser().equals(record.getReportingUser())) {
            contentBinding.sampleSampleCode.changeVisualState(VisualState.DISABLED);
            contentBinding.sampleSampleDateTime.changeVisualState(VisualState.DISABLED);
            contentBinding.sampleSampleDateTime.changeVisualState(VisualState.DISABLED);
            contentBinding.sampleSampleSource.changeVisualState(VisualState.DISABLED);
            contentBinding.sampleSampleMaterial.changeVisualState(VisualState.DISABLED);
            contentBinding.sampleSampleMaterialText.changeVisualState(VisualState.DISABLED);
            contentBinding.sampleSuggestedTypeOfTest.changeVisualState(VisualState.DISABLED);
            contentBinding.sampleLab.changeVisualState(VisualState.DISABLED);
            contentBinding.sampleLabDetails.changeVisualState(VisualState.DISABLED);
            contentBinding.sampleShipped.changeVisualState(VisualState.DISABLED);
            contentBinding.sampleShipmentDate.changeVisualState(VisualState.DISABLED);
            contentBinding.sampleShipmentDetails.changeVisualState(VisualState.DISABLED);
        }
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_sample_edit_layout;
    }

    private ObservableArrayList getTestResults() {
        ObservableArrayList results = new ObservableArrayList();
        if (mostRecentTest != null)
            results.add(mostRecentTest);
        return results;
    }

    private void setupCallback() {
        onRecentTestItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {

            }
        };

        referralLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (record != null && record.getReferredTo() != null) {
                    Sample referredSample = record.getReferredTo();
                    String sampleMaterial = record.getSampleMaterialText();
                    ShipmentStatus pageStatus = (ShipmentStatus)getBaseEditActivity().getPageStatus();

                    SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(getContext(), referredSample.getUuid(), pageStatus)
                            .setSampleMaterial(sampleMaterial);
                    SampleEditActivity.goToActivity(getActivity(), dataCapsule);
                }
            }
        };
    }

    public static SampleEditFragment newInstance(SampleFormNavigationCapsule capsule, Sample activityRootData) {
        return newInstance(SampleEditFragment.class, capsule, activityRootData);
    }
}
