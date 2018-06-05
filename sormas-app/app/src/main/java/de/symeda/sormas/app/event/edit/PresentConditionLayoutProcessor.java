package de.symeda.sormas.app.event.edit;

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

import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.TeboDatePicker;
import de.symeda.sormas.app.component.TeboPropertyField;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.databinding.FragmentEventEditPersonInfoLayoutBinding;
import de.symeda.sormas.app.shared.OnDateOfDeathChangeListener;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 12/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class PresentConditionLayoutProcessor {

    private Context context;
    private FragmentEventEditPersonInfoLayoutBinding contentBinding;
    private LinearLayout rootChildLayout;
    private ViewDataBinding binding;
    private int mLastLayoutResId;

    private Person record;
    private PresentCondition initialPresentCondition;
    private Date initialDeathDate;
    private CauseOfDeath initialDeathCause;

    private List<Item> causeOfDeathList;
    private List<Item> deathPlaceTypeList;
    private List<Item> diseaseList;
    private OnSetBindingVariableListener mOnSetBindingVariableListener;
    private CauseOfDeathLayoutProcessor causeOfDeathProcessor;
    private OnDateOfDeathChangeListener mOnDateOfDeathChangeListener;

    private FragmentManager fragmentManager;
    private TeboSpinner spnCauseOfDeath;
    private TeboDatePicker dtpDateOfDeath;


    public PresentConditionLayoutProcessor(Context context, FragmentManager fragmentManager,
                                           FragmentEventEditPersonInfoLayoutBinding contentBinding, Person record,
                                           List<Item> causeOfDeathList, List<Item> deathPlaceTypeList, List<Item> diseaseList) {
        this.mLastLayoutResId = -1;
        this.context = context;
        this.contentBinding = contentBinding;
        this.record = record;

        this.causeOfDeathList = causeOfDeathList;
        this.deathPlaceTypeList = deathPlaceTypeList;
        this.diseaseList = diseaseList;

        this.initialPresentCondition = record.getPresentCondition();
        this.initialDeathDate = record.getDeathDate();
        this.initialDeathCause = record.getCauseOfDeath();

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

        return initializeChildLayout(binding);

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

        //setRootNotificationBindingVariable(b, layoutName);
        //setLocalBindingVariable(b, layoutName);

        return binding;

    }

    private void notifyDateOfDeathChanged(TeboDatePicker view, Date value) {
        if (this.mOnDateOfDeathChangeListener != null)
            this.mOnDateOfDeathChangeListener.onChange(view, value);
    }

    public void setOnDateOfDeathChange(OnDateOfDeathChangeListener listener) {
        this.mOnDateOfDeathChangeListener = listener;
    }

    private boolean initializeChildLayout(ViewDataBinding binding) {
        View innerRootLayout = binding.getRoot();
        spnCauseOfDeath = (TeboSpinner)innerRootLayout.findViewById(R.id.spnCauseOfDeath);
        dtpDateOfDeath = (TeboDatePicker)innerRootLayout.findViewById(R.id.dtpDateOfDeath);


        dtpDateOfDeath.initialize(fragmentManager);
        dtpDateOfDeath.addValueChangedListener(new TeboPropertyField.ValueChangeListener() {
            @Override
            public void onChange(TeboPropertyField field) {
                notifyDateOfDeathChanged(dtpDateOfDeath, dtpDateOfDeath.getValue());
            }
        });

        binding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public void onBound(ViewDataBinding binding) {
                super.onBound(binding);

                if (spnCauseOfDeath == null)
                    return;

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
        });


        //causeOfDeathDetailLayout = (LinearLayout)presentConditionLayout.findViewById(R.id.causeOfDeathInclude);
        //initializePresentConditionChildLayout(innerRootLayout);

        if (getRootChildLayout() != null) {
            getRootChildLayout().removeAllViews();
            getRootChildLayout().addView(innerRootLayout);
            getRootChildLayout().setVisibility(View.VISIBLE);
        }

        return true;
    }

    private LinearLayout getRootChildLayout() {
        if (rootChildLayout == null)
            rootChildLayout = (LinearLayout)contentBinding.presentConditionInclude.getRoot();

        return rootChildLayout;
    }

    private int getLayoutResId(PresentCondition condition) {
        if (condition == PresentCondition.DEAD || condition == PresentCondition.BURIED) {
            return R.layout.fragment_event_edit_person_info_pcondition_dead_layout;
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
        if ((initialPresentCondition == PresentCondition.DEAD ||
                initialPresentCondition == PresentCondition.BURIED) &&
                (condition == PresentCondition.DEAD ||
                        condition == PresentCondition.BURIED)) {
            record.setDeathDate(initialDeathDate);
            record.setCauseOfDeath(initialDeathCause);
        } else if (initialPresentCondition == condition) {
            record.setDeathDate(initialDeathDate);
            record.setCauseOfDeath(initialDeathCause);
        } else {
            record.setDeathDate(null);
            record.setCauseOfDeath(null);
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

}
