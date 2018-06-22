package de.symeda.sormas.app.util.layoutprocessor;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.DeathPlaceType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.TeboDatePicker;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.shared.OnDateOfDeathChangeListener;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 13/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class PresentConditionLayoutProcessor {

    private Context context;
    private ViewDataBinding contentBinding;
    private LinearLayout rootChildLayout;
    private ViewDataBinding binding;
    private int mLastLayoutResId;

    private Person record;
    private PresentCondition initialPresentCondition;
    private Date initialDeathDate;
    private CauseOfDeath initialDeathCause;
    private DeathPlaceType initialDeathPlace;
    private String initialDeathPlaceDesc;
    private Date initialBurialDate;
    private BurialConductor initialBurialConductor;
    private String initialBurialPlaceDesc;

    private List<Item> causeOfDeathList;
    private List<Item> deathPlaceTypeList;
    private List<Item> diseaseList;
    private List<Item> burialConductorList;
    private OnSetBindingVariableListener mOnSetBindingVariableListener;
    private CauseOfDeathLayoutProcessor causeOfDeathProcessor;
    private OnDateOfDeathChangeListener mOnDateOfDeathChangeListener;

    private FragmentManager fragmentManager;
    private TeboSpinner spnCauseOfDeath;
    private TeboSpinner spnDeathPlaceType;
    private TeboSpinner spnBurialConductor;
    private TeboDatePicker dtpDateOfDeath;
    private TeboDatePicker dtpBurialDate;

    public PresentConditionLayoutProcessor(Context context, FragmentManager fragmentManager,
                                           ViewDataBinding contentBinding, Person record,
                                           List<Item> causeOfDeathList, List<Item> deathPlaceTypeList,
                                           List<Item> diseaseList, List<Item> burialConductorList) {
        this.mLastLayoutResId = -1;
        this.context = context;
        this.contentBinding = contentBinding;
        this.record = record;

        this.causeOfDeathList = causeOfDeathList;
        this.deathPlaceTypeList = deathPlaceTypeList;
        this.diseaseList = diseaseList;
        this.burialConductorList = burialConductorList;

        this.initialPresentCondition = record.getPresentCondition();
        this.initialDeathDate = record.getDeathDate();
        this.initialDeathCause = record.getCauseOfDeath();
        this.initialDeathPlace = record.getDeathPlaceType();
        this.initialDeathPlaceDesc = record.getDeathPlaceDescription();
        this.initialBurialDate = record.getBurialDate();
        this.initialBurialConductor = record.getBurialConductor();
        this.initialBurialPlaceDesc = record.getBurialPlaceDescription();

        this.fragmentManager = fragmentManager;

        hideRootChildLayout();
    }

    public boolean processLayout(PresentCondition presentCondition) {
        int layoutResId = getLayoutResId(presentCondition);
        String layoutName = getLayoutName(layoutResId);

        if (mLastLayoutResId == layoutResId) {
            if (binding == null)
                return false;

            ensurePresentConditionDataIntegrity(presentCondition);
            performSetBindingVariable(binding, layoutName);

            return false;
        }

        mLastLayoutResId = layoutResId;

        if (layoutResId <= 0) {
            hideRootChildLayout();
            //getRootChildLayout().setVisibility(View.GONE);
            return false;
        }

        ensurePresentConditionDataIntegrity(presentCondition);
        binding = inflateChildLayout(layoutResId);

        if (binding == null)
            return false;

        performSetBindingVariable(binding, layoutName);

        View innerLayout = initializeChildLayout(binding);

        //toggleBurialControls(innerLayout, (presentCondition == PresentCondition.BURIED)? true : false);

        addToRootLayout(innerLayout);

        return true;

    }

    private ViewDataBinding inflateChildLayout(int layoutResId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutResId, null);
        ViewDataBinding binding = DataBindingUtil.bind(layout);

        this.causeOfDeathProcessor = new CauseOfDeathLayoutProcessor(context, binding, record, this.deathPlaceTypeList, this.diseaseList);
        this.causeOfDeathProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
                performSetBindingVariable(binding, layoutName);
            }
        });

        return binding;
    }

    private void notifyDateOfDeathChanged(TeboDatePicker view, Date value) {
        if (this.mOnDateOfDeathChangeListener != null)
            this.mOnDateOfDeathChangeListener.onChange(view, value);
    }

    public void setOnDateOfDeathChange(OnDateOfDeathChangeListener listener) {
        this.mOnDateOfDeathChangeListener = listener;
    }

    private View initializeChildLayout(ViewDataBinding binding) {
        final View innerLayout = binding.getRoot();
        spnCauseOfDeath = (TeboSpinner)innerLayout.findViewById(R.id.spnCauseOfDeath);
        spnDeathPlaceType = (TeboSpinner)innerLayout.findViewById(R.id.spnDeathPlaceType);
        spnBurialConductor = (TeboSpinner)innerLayout.findViewById(R.id.spnBurialConductor);
        dtpDateOfDeath = (TeboDatePicker)innerLayout.findViewById(R.id.dtpDateOfDeath);
        dtpBurialDate = (TeboDatePicker)innerLayout.findViewById(R.id.dtpBurialDate);

        if (dtpBurialDate != null)
            dtpBurialDate.initialize(fragmentManager);

        if (dtpBurialDate != null) {
            dtpDateOfDeath.initialize(fragmentManager);
            dtpDateOfDeath.addValueChangedListener(new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    notifyDateOfDeathChanged(dtpDateOfDeath, dtpDateOfDeath.getValue());
                }
            });
        }

        binding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public void onBound(ViewDataBinding binding) {
                super.onBound(binding);

                if (spnCauseOfDeath != null) {
                    spnCauseOfDeath.initialize(new TeboSpinner.ISpinnerInitConfig() {
                        @Override
                        public Object getSelectedValue() {
                            return null;
                        }

                        @Override
                        public List<Item> getDataSource(Object parentValue) {
                            return (causeOfDeathList.size() > 0) ? DataUtils.addEmptyItem(causeOfDeathList)
                                    : causeOfDeathList;
                        }

                        @Override
                        public VisualState getInitVisualState() {
                            return null;
                        }

                        @Override
                        public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                            if (!causeOfDeathProcessor.processLayout((CauseOfDeath)value))
                                return;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }


                if (spnDeathPlaceType != null) {
                    spnDeathPlaceType.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
                        @Override
                        public Object getSelectedValue() {
                            return null;
                        }

                        @Override
                        public List<Item> getDataSource(Object parentValue) {
                            return (deathPlaceTypeList.size() > 0) ? DataUtils.addEmptyItem(deathPlaceTypeList)
                                    : deathPlaceTypeList;
                        }

                        @Override
                        public VisualState getInitVisualState() {
                            return null;
                        }
                    });
                }

                if (spnBurialConductor != null) {
                    toggleBurialControls(innerLayout, true);
                    spnBurialConductor.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
                        @Override
                        public Object getSelectedValue() {
                            return null;
                        }

                        @Override
                        public List<Item> getDataSource(Object parentValue) {
                            return (burialConductorList.size() > 0) ? DataUtils.addEmptyItem(burialConductorList)
                                    : burialConductorList;
                        }

                        @Override
                        public VisualState getInitVisualState() {
                            return null;
                        }
                    });
                }

            }
        });

        return innerLayout;
    }

    private void addToRootLayout (View innerLayout) {
        if (getRootChildLayout() != null) {
            getRootChildLayout().removeAllViews();
            getRootChildLayout().addView(innerLayout);
            getRootChildLayout().setVisibility(View.VISIBLE);
        }
    }

    private LinearLayout getRootChildLayout() {
        if (rootChildLayout == null)
            rootChildLayout = (LinearLayout)contentBinding.getRoot().findViewById(R.id.presentConditionInclude);

        //rootChildLayout = (LinearLayout)contentBinding.presentConditionInclude.getRoot();

        return rootChildLayout;
    }

    private int getLayoutResId(PresentCondition condition) {
        if (condition == PresentCondition.DEAD) {
            return R.layout.fragment_edit_person_info_pcondition_dead_layout;
        } else if (condition == PresentCondition.BURIED) {
            return R.layout.fragment_edit_person_info_pcondition_buried_layout;
        }

        return -1;
    }

    private String getLayoutName(int layoutResId) {
        if (layoutResId <= 0)
            return null;

        return context.getResources().getResourceEntryName(layoutResId);
    }

    private void hideRootChildLayout() {
        if (getRootChildLayout() == null)
            return;

        getRootChildLayout().setVisibility(View.GONE);
        getRootChildLayout().removeAllViews();
    }

    private void ensurePresentConditionDataIntegrity(PresentCondition condition) {
        if (initialPresentCondition == PresentCondition.DEAD && condition == PresentCondition.DEAD) {
            record.setDeathDate(initialDeathDate);
            record.setCauseOfDeath(initialDeathCause);
            record.setDeathPlaceType(initialDeathPlace);
            record.setDeathPlaceDescription(initialDeathPlaceDesc);
        } else if (initialPresentCondition == PresentCondition.BURIED && condition == PresentCondition.BURIED) {
            record.setDeathDate(initialDeathDate);
            record.setCauseOfDeath(initialDeathCause);
            record.setDeathPlaceType(initialDeathPlace);
            record.setDeathPlaceDescription(initialDeathPlaceDesc);
            record.setBurialDate(initialBurialDate);
            record.setBurialConductor(initialBurialConductor);
            record.setBurialPlaceDescription(initialBurialPlaceDesc);
        } else if (initialPresentCondition == condition) {
            record.setDeathDate(initialDeathDate);
            record.setCauseOfDeath(initialDeathCause);
            record.setDeathPlaceType(initialDeathPlace);
            record.setDeathPlaceDescription(initialDeathPlaceDesc);
            record.setBurialDate(initialBurialDate);
            record.setBurialConductor(initialBurialConductor);
            record.setBurialPlaceDescription(initialBurialPlaceDesc);
        } else {
            record.setDeathDate(null);
            record.setCauseOfDeath(null);
            record.setDeathPlaceType(null);
            record.setDeathPlaceDescription(null);
            record.setBurialDate(null);
            record.setBurialConductor(null);
            record.setBurialPlaceDescription(null);
        }
    }

    private void performSetBindingVariable(ViewDataBinding binding, String layoutName) {
        if (this.mOnSetBindingVariableListener != null) {
            this.mOnSetBindingVariableListener.onSetBindingVariable(binding, layoutName);
        }
    }

    public void setOnSetBindingVariable(OnSetBindingVariableListener listener) {
        this.mOnSetBindingVariableListener = listener;
    }

    private void toggleBurialControls(View innerLayout, boolean visibility) {
        TeboDatePicker dtpBurialDate = (TeboDatePicker)innerLayout.findViewById(R.id.dtpBurialDate);
        TeboSpinner spnBurialConductor = (TeboSpinner)innerLayout.findViewById(R.id.spnBurialConductor);
        ControlTextEditField txtBurialPlaceDesc = (ControlTextEditField)innerLayout.findViewById(R.id.txtBurialPlaceDesc);

        if (dtpBurialDate != null)
            dtpBurialDate.setVisibility(visibility ? View.VISIBLE : View.GONE);

        if (spnBurialConductor != null)
            spnBurialConductor.setVisibility(visibility ? View.VISIBLE : View.GONE);

        if (txtBurialPlaceDesc != null)
            txtBurialPlaceDesc.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
}
