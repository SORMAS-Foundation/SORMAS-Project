package de.symeda.sormas.app.symptom;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.databinding.RowEditSymptomListItemLayoutBinding;

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
    private final int rowLayout;
    private final String layoutName;
    private List<Symptom> data = new ArrayList<>();

    //private OnTeboSwitchCheckedChangeListener mOnSymptomCheckedCallback;
    private OnSymptomStateChangeListener mOnSymptomStateChangeListener;



    public SymptomFormListAdapter(Context context, int rowLayout) {
        this(context, rowLayout, new ArrayList<Symptom>());
    }

    public SymptomFormListAdapter(Context context, int rowLayout, List<Symptom> data) {
        super(rowLayout);
        this.context = context;
        this.rowLayout = rowLayout;
        this.layoutName = context.getResources().getResourceEntryName(rowLayout);

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowEditSymptomListItemLayoutBinding> holder,
                            int position, List<Object> payloads) {

        Symptom record = data.get(position);
        holder.setData(record);
        setOtherBindingVariable(holder.binding, record);
        holder.binding.setSymptomStateClass(SymptomState.class);
        holder.binding.setCheckedCallback(createCallback(record, holder));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

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

                if (_symptomItem.getLastCheckedId() == checkedId) {
                    return;
                }

                _symptomItem.setLastCheckedId(checkedId);

                if (_symptomItem.getChildViewModel() instanceof DetailsViewModel) {
                    if (state == SymptomState.YES && _symptomItem.hasDetail()) {
                        _holder.binding.txtSymptomDetail.setVisibility(View.VISIBLE);
                    } else {
                        _holder.binding.txtSymptomDetail.setVisibility(View.GONE);
                    }
                }

                if (_symptomItem.getChildViewModel() instanceof LesionChildViewModel) {
                    if (state == SymptomState.YES && _symptomItem.hasDetail()) {
                        getLesionsLayout(_holder.binding).setVisibility(View.VISIBLE);
                    } else {
                        getLesionsLayout(_holder.binding).setVisibility(View.GONE);
                    }
                }

                performSymptomStateChanged(_symptomItem, state);
            }


            private OnTeboSwitchCheckedChangeListener init(Symptom s, DataBoundViewHolder<RowEditSymptomListItemLayoutBinding> h){
                _symptomItem = s;
                _holder = h;
                return this;
            }
        }.init(symptomRecord, holder);
    }

    private LinearLayout getLesionsLayout(ViewDataBinding binding) {
        return (LinearLayout)binding.getRoot().findViewById(R.id.lesionsLayoutInclude);
    }

    private void setOtherBindingVariable(final ViewDataBinding binding, Symptom record) {
        if (record.getChildViewModel() instanceof DetailsViewModel && !binding.setVariable(BR.detailsChildViewModel, record.getChildViewModel())) {
            Log.e(TAG, "There is no variable 'detailsChildViewModel' in layout " + layoutName);
        }

        if (record.getChildViewModel() instanceof LesionChildViewModel && !binding.setVariable(BR.lesionsChildViewModel, record.getChildViewModel())) {
            Log.e(TAG, "There is no variable 'lesionsChildViewModel' in layout " + layoutName);
        }
    }

    public void setOnSymptomStateChangeListener(OnSymptomStateChangeListener listener) {
        this.mOnSymptomStateChangeListener = listener;
    }

    private void performSymptomStateChanged(Symptom symptom, SymptomState state) {
        if (this.mOnSymptomStateChangeListener != null)
            this.mOnSymptomStateChangeListener.onChange(symptom, state);
    }


}

