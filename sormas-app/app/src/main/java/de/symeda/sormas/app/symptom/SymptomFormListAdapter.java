package de.symeda.sormas.app.symptom;

import android.content.Context;
import android.view.View;

import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.databinding.RowEditSymptomListItemLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;

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
    private List<Symptom> data = new ArrayList<>();

    //private OnTeboSwitchCheckedChangeListener mOnSymptomCheckedCallback;
    private OnSymptomStateChangeListener mOnSymptomStateChangeListener;



    public SymptomFormListAdapter(Context context, int rowLayout) {
        this(context, rowLayout, new ArrayList<Symptom>());
    }

    public SymptomFormListAdapter(Context context, int rowLayout, List<Symptom> data) {
        super(rowLayout);
        this.context = context;

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
            private Symptom _SymptomItem;
            private DataBoundViewHolder<RowEditSymptomListItemLayoutBinding> _Holder;

            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (checkedId < 0)
                    return;

                SymptomState state = (SymptomState)checkedItem;

                if (_SymptomItem.getLastCheckedId() == checkedId) {
                    return;
                }

                _SymptomItem.setLastCheckedId(checkedId);

                if (state == SymptomState.YES && _SymptomItem.hasDetail()) {
                    _Holder.binding.txtSymptomDetail.setVisibility(View.VISIBLE);
                } else {
                    _Holder.binding.txtSymptomDetail.setVisibility(View.GONE);
                }

                performSymptomStateChanged(_SymptomItem, state);
            }


            private OnTeboSwitchCheckedChangeListener init(Symptom s, DataBoundViewHolder<RowEditSymptomListItemLayoutBinding> h){
                _SymptomItem = s;
                _Holder = h;
                return this;
            }
        }.init(symptomRecord, holder);
    }




    public void setOnSymptomStateChangeListener(OnSymptomStateChangeListener listener) {
        this.mOnSymptomStateChangeListener = listener;
    }

    private void performSymptomStateChanged(Symptom symptom, SymptomState state) {
        if (this.mOnSymptomStateChangeListener != null)
            this.mOnSymptomStateChangeListener.onChange(symptom, state);
    }


}

