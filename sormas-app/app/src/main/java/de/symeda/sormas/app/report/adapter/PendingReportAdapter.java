package de.symeda.sormas.app.report.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.RowPendingReportListItemLayoutBinding;
import de.symeda.sormas.app.report.viewmodel.PendingReportViewModel;

public class PendingReportAdapter extends DataBoundAdapter<RowPendingReportListItemLayoutBinding> {

    private static final String TAG = WeeklyReportAdapter.class.getSimpleName();

    private final Context context;
    private List<PendingReportViewModel> data = new ArrayList<>();

    public PendingReportAdapter(Context context, int rowLayout) {
        this(context, rowLayout, new ArrayList<PendingReportViewModel>());
    }

    public PendingReportAdapter(Context context, int rowLayout, List<PendingReportViewModel> data) {
        super(rowLayout);
        this.context = context;

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowPendingReportListItemLayoutBinding> holder,
                            int position, List<Object> payloads) {

        PendingReportViewModel record = data.get(position);
        holder.setData(record);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}