package de.symeda.sormas.app.epid;

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

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.databinding.RowEditAnimalContactListItemLayoutBinding;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Created by Orson on 20/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class AnimalContactFormListAdapter  extends DataBoundAdapter<RowEditAnimalContactListItemLayoutBinding> {

    private static final String TAG = AnimalContactFormListAdapter.class.getSimpleName();

    private final Context context;
    private List<AnimalContact> data = new ArrayList<>();
    private OnSetBindingVariableListener mOnSetBindingVariableListener;
    private FragmentManager fragmentManager;

    public AnimalContactFormListAdapter(Context context, int rowLayout, FragmentManager fragmentManager) {
        this(context, rowLayout, fragmentManager, new ArrayList<AnimalContact>());
    }

    public AnimalContactFormListAdapter(Context context, int rowLayout, FragmentManager fragmentManager, List<AnimalContact> data) {
        super(rowLayout);
        this.context = context;
        this.fragmentManager = fragmentManager;

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowEditAnimalContactListItemLayoutBinding> holder,
                            int position, List<Object> payloads) {

        AnimalContact record = data.get(position);


        holder.setData(record);
        holder.binding.setYesNoUnknownClass(YesNoUnknown.class);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

//    private OnTeboSwitchCheckedChangeListener createCallback(final AnimalContact animalContactRecord,
//                                                              DataBoundViewHolder<RowEditAnimalContactListItemLayoutBinding> holder) {
//        return new OnTeboSwitchCheckedChangeListener() {
//            private AnimalContact _animalContactItem;
//            private DataBoundViewHolder<RowEditAnimalContactListItemLayoutBinding> _holder;
//
//            @Override
//            public void onCheckedChanged(ControlSwitchField teboSwitch, Object checkedItem, int checkedId) {
//                if (checkedId < 0)
//                    return;
//
//                YesNoUnknown state = (YesNoUnknown)checkedItem;
//
//                if (_animalContactItem.getLastCheckedId() == checkedId) {
//                    return;
//                }
//
//                _animalContactItem.setLastCheckedId(checkedId);
//
//                if (animalContactRecord.hasChildLayout() && animalContactRecord.getState() == YesNoUnknown.YES) {
//                    String layoutName = teboSwitch.getContext().getResources().getResourceEntryName(animalContactRecord.getLayoutResourceId());
//                    ViewDataBinding binding = inflateChildLayout(animalContactRecord.getLayoutResourceId());
//
//                    if (binding == null)
//                        return;
//
//                    setLocalBindingVariable(binding, layoutName, animalContactRecord);
//                    performSetBindingVariable(binding, layoutName, animalContactRecord);
//
//                    View innerLayout = initializeChildLayout(binding);
//
//                    addToRootLayout(innerLayout, _holder);
//                } else {
//                    hideRootChildLayout(_holder);
//                }
//            }
//
//
//            private OnTeboSwitchCheckedChangeListener init(AnimalContact s,
//                DataBoundViewHolder<RowEditAnimalContactListItemLayoutBinding> h,
//                DataBoundAdapter<RowEditAnimalContactListItemLayoutBinding> adapter){
//                _animalContactItem = s;
//                _holder = h;
//                return this;
//            }
//        }.init(animalContactRecord, holder, this);
//    }


    private ViewDataBinding inflateChildLayout(int layoutResId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutResId, null);
        ViewDataBinding binding = DataBindingUtil.bind(layout);

        return binding;
    }

    private View initializeChildLayout(ViewDataBinding binding) {
        final View innerLayout = binding.getRoot();
        ControlDateField dtpLastExposureDate = (ControlDateField)innerLayout.findViewById(R.id.dtpLastExposureDate);

        if (dtpLastExposureDate != null)
            dtpLastExposureDate.initializeDateField(fragmentManager);

        binding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public void onBound(ViewDataBinding binding) {
                super.onBound(binding);
            }
        });

        return innerLayout;
    }

    private LinearLayout getRootChildLayout(DataBoundViewHolder<RowEditAnimalContactListItemLayoutBinding> holder) {
        return (LinearLayout)holder.binding.animalContactDetailsInclude.getRoot();
    }

    private void hideRootChildLayout(DataBoundViewHolder<RowEditAnimalContactListItemLayoutBinding> holder) {
        if (getRootChildLayout(holder) == null)
            return;

        getRootChildLayout(holder).setVisibility(View.GONE);
        getRootChildLayout(holder).removeAllViews();
    }

    private void addToRootLayout(View innerLayout, DataBoundViewHolder<RowEditAnimalContactListItemLayoutBinding> holder) {
        if (getRootChildLayout(holder) != null) {
            getRootChildLayout(holder).removeAllViews();
            getRootChildLayout(holder).addView(innerLayout);
            getRootChildLayout(holder).setVisibility(View.VISIBLE);
        }
    }

    private void setLocalBindingVariable(final ViewDataBinding binding, String layoutName, AnimalContact animalContactRecord) {
        if (!binding.setVariable(BR.data, animalContactRecord)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.yesNoUnknownClass, YesNoUnknown.class)) {
            Log.e(TAG, "There is no variable 'yesNoUnknownClass' in layout " + layoutName);
        }
    }

    private void performSetBindingVariable(ViewDataBinding binding, String layoutName, AnimalContact animalContactRecord) {
        if (this.mOnSetBindingVariableListener != null) {
            this.mOnSetBindingVariableListener.onSetBindingVariable(binding, layoutName);
        }
    }

    public void setOnSetBindingVariable(OnSetBindingVariableListener listener) {
        this.mOnSetBindingVariableListener = listener;
    }
}