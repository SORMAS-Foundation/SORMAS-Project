package de.symeda.sormas.app.util.layoutprocessor;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 22/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseDiseaseLayoutProcessor {

    private Context context;
    private ViewDataBinding contentBinding;
    private LinearLayout rootChildLayout;
    private ViewDataBinding binding;

    private int mLastLayoutResId;
    private Case record;
    private Disease initialDisease;
    private PlagueType initialPlagueType;
    private DengueFeverType initialDengueFeverType;
    private Vaccination initialVaccination;
    private VaccinationInfoSource initialVaccinationInfoSource;
    private Date initialDataOfLastVaccination;
    private YesNoUnknown initialPrevVaccination;
    private String initialNumbValidDoses;

    private List<Item> vaccinationList;
    private List<Item> vaccinationInfoSourceList;
    private List<Item> plagueList;
    private List<Item> dengueFeverList;

    private FragmentManager fragmentManager;
    private ControlSpinnerField spnVaccination;
    private ControlSpinnerField spnVaccinationInfoSource;
    private ControlSpinnerField spnPlague;
    private ControlSpinnerField spnDengueFever;
    private ControlDateField dtpLastVaccinationDate;

    private OnSetBindingVariableListener mOnSetBindingVariableListener;

    public CaseDiseaseLayoutProcessor(Context context, FragmentManager fragmentManager, ViewDataBinding contentBinding, Case record,
                                      List<Item> vaccinationList, List<Item> vaccinationInfoSourceList, List<Item> plagueList, List<Item> dengueFeverList) {
        this.mLastLayoutResId = -1;
        this.context = context;
        this.contentBinding = contentBinding;
        this.record = record;

        this.vaccinationList = vaccinationList;
        this.vaccinationInfoSourceList = vaccinationInfoSourceList;
        this.plagueList = plagueList;
        this.dengueFeverList = dengueFeverList;

        this.initialDisease = record.getDisease();
        this.initialPlagueType = record.getPlagueType();
        this.initialDengueFeverType = record.getDengueFeverType();
        this.initialVaccination = record.getVaccination();
        this.initialVaccinationInfoSource = record.getVaccinationInfoSource();
        this.initialDataOfLastVaccination = record.getVaccinationDate();
        this.initialPrevVaccination = record.getSmallpoxVaccinationReceived();
        this.initialNumbValidDoses = record.getVaccinationDoses();
        this.fragmentManager = fragmentManager;

        hideRootChildLayout();
    }

    public boolean processLayout(Disease disease) {
        if (getRootChildLayout() == null)
            return false;

        int layoutResId = getLayoutResId(disease);
        String layoutName = getLayoutName(layoutResId);

        if (mLastLayoutResId == layoutResId) {
            if (binding == null)
                return false;

            ensureDataIntegrity(disease);
            performSetBindingVariable(binding, layoutName);

            return false;
        }

        mLastLayoutResId = layoutResId;

        if (layoutResId <= 0) {
            hideRootChildLayout();
            //getRootChildLayout().setVisibility(View.GONE);
            return false;
        }

        ensureDataIntegrity(disease);
        binding = inflateChildLayout(layoutResId);

        if (binding == null)
            return false;

        performSetBindingVariable(binding, layoutName);

        return initializeChildLayout(binding);

    }

    private ViewDataBinding inflateChildLayout(int layoutResId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutResId, null);

        ViewDataBinding b = DataBindingUtil.bind(layout);
        String layoutName = context.getResources().getResourceEntryName(layoutResId);
        performSetBindingVariable(b, layoutName);

        return b;
    }

    private boolean initializeChildLayout(ViewDataBinding binding) {
        View innerRootLayout = binding.getRoot();

        spnVaccination = (ControlSpinnerField)innerRootLayout.findViewById(R.id.spnVaccination);
        spnVaccinationInfoSource = (ControlSpinnerField)innerRootLayout.findViewById(R.id.spnVaccinationInfoSource);
        spnPlague = (ControlSpinnerField)innerRootLayout.findViewById(R.id.spnPlague);
        spnDengueFever = (ControlSpinnerField)innerRootLayout.findViewById(R.id.spnDengueFever);
        dtpLastVaccinationDate = (ControlDateField) innerRootLayout.findViewById(R.id.dtpLastVaccinationDate);

        if (dtpLastVaccinationDate != null)
            dtpLastVaccinationDate.setFragmentManager(fragmentManager);

        binding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public void onBound(ViewDataBinding binding) {
                super.onBound(binding);

                if (spnPlague != null) {
                    spnPlague.initializeSpinner(plagueList);
                }

                if (spnDengueFever != null) {
                    spnDengueFever.initializeSpinner(dengueFeverList);
                }


                if (spnVaccination != null) {
                    spnVaccination.initializeSpinner(vaccinationList);
                }

                if (spnVaccinationInfoSource != null) {
                    spnVaccinationInfoSource.initializeSpinner(vaccinationInfoSourceList);
                }


            }
        });

        if (getRootChildLayout() != null) {
            getRootChildLayout().removeAllViews();
            getRootChildLayout().addView(innerRootLayout);

            getRootChildLayout().setVisibility(View.VISIBLE);
        }

        return true;
    }

    private void ensureDataIntegrity(Disease disease) {
        if (initialDisease == disease) {
            record.setPlagueType(initialPlagueType);
            record.setDengueFeverType(initialDengueFeverType);
            record.setVaccination(initialVaccination);
            record.setVaccinationInfoSource(initialVaccinationInfoSource);
            record.setVaccinationDate(initialDataOfLastVaccination);
            record.setSmallpoxVaccinationReceived(initialPrevVaccination);
            record.setVaccinationDoses(initialNumbValidDoses);
        } else {
            record.setPlagueType(null);
            record.setDengueFeverType(null);
            record.setVaccination(null);
            record.setVaccinationInfoSource(null);
            record.setVaccinationDate(null);
            record.setSmallpoxVaccinationReceived(null);
            record.setVaccinationDoses(null);
        }
    }

    private int getLayoutResId(Disease disease) {
        if (disease == Disease.YELLOW_FEVER) {
            return R.layout.fragment_edit_case_info_disease_yellow_fever_layout;
        } else if (disease == Disease.MONKEYPOX) {
            return R.layout.fragment_edit_case_info_disease_monkeypox_layout;
        } else if (disease == Disease.MEASLES) {
            return R.layout.fragment_edit_case_info_disease_measles_layout;
        } else if (disease == Disease.CSM) {
            return R.layout.fragment_edit_case_info_disease_csm_layout;
        } else if (disease == Disease.PLAGUE) {
            return R.layout.fragment_edit_case_info_disease_plague_layout;
        } else if (disease == Disease.DENGUE) {
            return R.layout.fragment_edit_case_info_disease_dengue_fever_layout;
        }

        return -1;
    }

    private String getLayoutName(int layoutResId) {
        if (layoutResId <= 0)
            return null;

        return context.getResources().getResourceEntryName(layoutResId);
    }

    private LinearLayout getRootChildLayout() {
        if (rootChildLayout == null)
            rootChildLayout = (LinearLayout)contentBinding.getRoot().findViewById(R.id.caseDiseaseDetailsInclude);

        //rootChildLayout = (LinearLayout)contentBinding.occupationDetailsInclude.getRoot();

        return rootChildLayout;
    }

    private void hideRootChildLayout() {
        if (getRootChildLayout() == null)
            return;

        getRootChildLayout().setVisibility(View.GONE);
        getRootChildLayout().removeAllViews();
    }

    private void performSetBindingVariable(ViewDataBinding binding, String layoutName) {
        if (this.mOnSetBindingVariableListener != null)
            this.mOnSetBindingVariableListener.onSetBindingVariable(binding, layoutName);
    }

    public void setOnSetBindingVariable(OnSetBindingVariableListener listener) {
        this.mOnSetBindingVariableListener = listener;
    }
}
