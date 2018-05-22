package de.symeda.sormas.app.symptom;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboDatePicker;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.databinding.RowEditSymptomListItemLayoutBinding;
import de.symeda.sormas.app.databinding.RowSymptomDetailsLesionsChildLayoutBinding;

/**
 * Created by Orson on 14/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SymptomFormListAdapter extends DataBoundAdapter<RowEditSymptomListItemLayoutBinding> {

    private static final String TAG = SymptomFormListAdapter.class.getSimpleName();

    private final Context context;
    private final FragmentManager fragmentManager;
    private final int rowLayout;
    private final String layoutName;
    private List<Symptom> data = new ArrayList<>();

    private INotificationContext notificationContext;

    //private OnTeboSwitchCheckedChangeListener mOnSymptomCheckedCallback;
    private OnSymptomStateChangeListener mOnSymptomStateChangeListener;

    public SymptomFormListAdapter(Context context, INotificationContext notificationContext, int rowLayout, FragmentManager fragmentManager) {
        this(context, notificationContext, rowLayout, new ArrayList<Symptom>(), fragmentManager);
    }

    public SymptomFormListAdapter(Context context, INotificationContext notificationContext, int rowLayout, List<Symptom> data, FragmentManager fragmentManager) {
        super(rowLayout);
        this.context = context;
        this.notificationContext = notificationContext;
        this.fragmentManager = fragmentManager;
        this.rowLayout = rowLayout;
        this.layoutName = context.getResources().getResourceEntryName(rowLayout);

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = data;
    }

    @Override
    protected void bindItem(final DataBoundViewHolder<RowEditSymptomListItemLayoutBinding> holder,
                            int position, List<Object> payloads) {

        Symptom record = data.get(position);

        record.setOnSymptomErrorStateChanged(new OnSymptomErrorStateChanged() {
            @Override
            public void onChanged(Symptom symptom, boolean errorState, Integer errorMessageResId) {
                if (errorState)
                    holder.binding.swhSymptomState.enableErrorState(notificationContext, errorMessageResId == null? -1 : errorMessageResId);
                else {
                    holder.binding.swhSymptomState.disableErrorState(notificationContext);
                }
            }
        });

        if (record.getChildViewModel() instanceof DetailsViewModel) {
            DetailsViewModel detailsViewModel = (DetailsViewModel)record.getChildViewModel();

            if (detailsViewModel != null) {
                detailsViewModel.setOnDetailsViewModelErrorStateChanged(new OnDetailsViewModelErrorStateChanged() {
                    @Override
                    public void onChanged(DetailsViewModel viewModel, boolean errorState, Integer errorMessageResId) {
                        if (errorState)
                            holder.binding.txtSymptomDetail.enableErrorState(notificationContext, errorMessageResId == null? -1 : errorMessageResId);
                        else {
                            holder.binding.txtSymptomDetail.disableErrorState(notificationContext);
                        }
                    }
                });
            }
        }


        if (record.getChildViewModel() instanceof LesionChildViewModel) {
            LesionChildViewModel lesionChildViewModel = (LesionChildViewModel)record.getChildViewModel();

            if (lesionChildViewModel != null) {
                lesionChildViewModel.setOnLesionChildViewModelErrorStateChanged(new OnLesionChildViewModelErrorStateChanged() {
                    @Override
                    public void onChanged(LesionChildViewModel viewModel, boolean errorState, Integer errorMessageResId) {
                        if (errorState)
                            holder.binding.swhSymptomState.enableErrorState(notificationContext, errorMessageResId == null? -1 : errorMessageResId);
                        else {
                            holder.binding.swhSymptomState.disableErrorState(notificationContext);
                        }
                    }
                });
            }
        }

        holder.setData(record);

        LinearLayout childRootView = getChildLayout(holder.binding);
        buildChildLayout(record, holder, childRootView);

        setOtherBindingVariable(holder.binding, record);

        holder.binding.setSymptomStateClass(SymptomState.class);
        holder.binding.setCheckedCallback(createCallback(record, holder));
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnSymptomStateChangeListener(OnSymptomStateChangeListener listener) {
        this.mOnSymptomStateChangeListener = listener;
    }

    //<editor-fold desc="Private Methods">
    private OnTeboSwitchCheckedChangeListener createCallback(Symptom symptomRecord,
                                                             DataBoundViewHolder<RowEditSymptomListItemLayoutBinding> holder) {
        return new OnTeboSwitchCheckedChangeListener() {
            private Symptom _symptomItem;
            private DataBoundViewHolder<RowEditSymptomListItemLayoutBinding> _holder;

            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (checkedId < 0)
                    return;

                SymptomState state = (SymptomState)checkedItem;
                Symptom activeSymptom = getActiveSymptomRecord(teboSwitch, _symptomItem);

                if (activeSymptom == null) {
                    Log.e(TAG, "The active symptom is null.");
                    return;
                }

                if (activeSymptom.getLastCheckedId() == checkedId) {
                    return;
                }

                activeSymptom.setLastCheckedId(checkedId);

                if (_symptomItem.getChildViewModel() instanceof DetailsViewModel) {
                    if (teboSwitch.getId() == R.id.swhSymptomState) {
                        if (state == SymptomState.YES && _symptomItem.hasDetail()) {
                            _holder.binding.txtSymptomDetail.setVisibility(View.VISIBLE);
                        } else {
                            _holder.binding.txtSymptomDetail.setVisibility(View.GONE);
                        }
                    }
                }

                if (_symptomItem.getChildViewModel() instanceof LesionChildViewModel) {
                    if (teboSwitch.getId() == R.id.swhSymptomState) {
                        if (state == SymptomState.YES && _symptomItem.hasDetail()) {
                            getChildLayout(_holder.binding).setVisibility(View.VISIBLE);
                        } else {
                            getChildLayout(_holder.binding).setVisibility(View.GONE);
                        }
                    }
                }

                performSymptomStateChanged(activeSymptom, state);
            }


            private OnTeboSwitchCheckedChangeListener init(Symptom s, DataBoundViewHolder<RowEditSymptomListItemLayoutBinding> h){
                _symptomItem = s;
                _holder = h;
                return this;
            }
        }.init(symptomRecord, holder);
    }

    private void buildChildLayout(Symptom s, DataBoundViewHolder<RowEditSymptomListItemLayoutBinding> holder, LinearLayout childRootView) {
        Integer layoutId = s.getDetailTemplateResId();

        if (layoutId == null) {
            removeChildLayout(childRootView);
            return;
        }

        ViewDataBinding childLayoutBinding = inflateChildLayout(s, layoutId);

        if (childLayoutBinding == null)
            return;

        bindChildVariables(s, holder, childLayoutBinding, layoutId);

        View childLayoutView = childLayoutBinding.getRoot();

        initializeChildLayout(childLayoutBinding, childLayoutView);
        addChildLayout(childRootView, childLayoutView);

    }

    private void bindChildVariables(Symptom s, DataBoundViewHolder<RowEditSymptomListItemLayoutBinding> holder, ViewDataBinding childLayoutBinding, int layoutId) {
        String layoutName = context.getResources().getResourceEntryName(layoutId);

        if (s.getChildViewModel() instanceof LesionChildViewModel) {
            RowSymptomDetailsLesionsChildLayoutBinding binding = (RowSymptomDetailsLesionsChildLayoutBinding)childLayoutBinding;

            binding.setShowNotificationCallback(holder.binding.getShowNotificationCallback());
            binding.setHideNotificationCallback(holder.binding.getHideNotificationCallback());
            binding.setData((LesionChildViewModel)s.getChildViewModel());
            binding.setSymptomStateClass(SymptomState.class);
            binding.setCheckedCallback(createCallback(s, holder));
        }
    }

    private ViewDataBinding inflateChildLayout(Symptom s, Integer layoutResId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutResId, null);

        return DataBindingUtil.bind(layout);
    }

    private void initializeChildLayout(ViewDataBinding childLayoutBinding, View childLayoutView) {
        final TeboDatePicker dtpLesionsOnsetDate = (TeboDatePicker) childLayoutView.findViewById(R.id.dtpLesionsOnsetDate);

        childLayoutBinding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public void onBound(ViewDataBinding binding) {
                super.onBound(binding);

                if (dtpLesionsOnsetDate == null)
                    return;

                dtpLesionsOnsetDate.initialize(fragmentManager);
            }
        });
    }

    private void addChildLayout(LinearLayout childRootView, View childLayoutView) {
        if (childRootView != null) {
            childRootView.removeAllViews();
            childRootView.addView(childLayoutView);
            childRootView.setVisibility(View.VISIBLE);
        }
    }

    private void removeChildLayout(LinearLayout childRootView) {
        if (childRootView != null) {
            childRootView.setVisibility(View.GONE);
            childRootView.removeAllViews();
        }
    }

    private LinearLayout getChildLayout(RowEditSymptomListItemLayoutBinding binding) {
        return (LinearLayout)binding.getRoot().findViewById(R.id.lesionsLayoutInclude);
    }

    private Symptom getActiveSymptomRecord(TeboSwitch teboSwitch, Symptom parent) {
        if (teboSwitch.getId() == R.id.swhSymptomState) {
            return parent;
        } else {
            if (parent.getChildViewModel() instanceof LesionChildViewModel) {
                LesionChildViewModel lesionChildViewModel = (LesionChildViewModel)parent.getChildViewModel();

                if (teboSwitch.getId() == R.id.swhRashThatItchState) {
                    return lesionChildViewModel.getLesionsThatItches();
                } else if (teboSwitch.getId() == R.id.swhLesionsState) {
                    return lesionChildViewModel.getLesionsInSameState();
                } else if (teboSwitch.getId() == R.id.swhLesionsSizeState) {
                    return lesionChildViewModel.getLesionsSameSize();
                } else if (teboSwitch.getId() == R.id.swhLesionsProfoundState) {
                    return lesionChildViewModel.getLesionsDeepAndProfound();
                } else if (teboSwitch.getId() == R.id.swhLesionsPic1) {
                    return lesionChildViewModel.getLesionsResemblePic1();
                } else if (teboSwitch.getId() == R.id.swhLesionsPic2) {
                    return lesionChildViewModel.getLesionsResemblePic2();
                } else if (teboSwitch.getId() == R.id.swhLesionsPic3) {
                    return lesionChildViewModel.getLesionsResemblePic3();
                } else if (teboSwitch.getId() == R.id.swhLesionsPic4) {
                    return lesionChildViewModel.getLesionsResemblePic4();
                }
            }
        }

        return null;
    }

    private void setOtherBindingVariable(final ViewDataBinding binding, Symptom record) {
        if (record.getChildViewModel() instanceof DetailsViewModel && !binding.setVariable(BR.detailsChildViewModel, record.getChildViewModel())) {
            Log.e(TAG, "There is no variable 'detailsChildViewModel' in layout " + layoutName);
        }

        if (record.getChildViewModel() instanceof LesionChildViewModel && !binding.setVariable(BR.lesionsChildViewModel, record.getChildViewModel())) {
            Log.e(TAG, "There is no variable 'lesionsChildViewModel' in layout " + layoutName);
        }
    }

    private void performSymptomStateChanged(Symptom symptom, SymptomState state) {
        if (this.mOnSymptomStateChangeListener != null)
            this.mOnSymptomStateChangeListener.onChange(symptom, state);
    }
    //</editor-fold>

}

