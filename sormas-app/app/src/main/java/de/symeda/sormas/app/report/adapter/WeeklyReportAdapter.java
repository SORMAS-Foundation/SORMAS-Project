package de.symeda.sormas.app.report.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.RowWeeklyReportListItemLayoutBinding;
import de.symeda.sormas.app.report.viewmodel.WeeklyReportViewModel;

/**
 * Created by Orson on 25/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class WeeklyReportAdapter extends DataBoundAdapter<RowWeeklyReportListItemLayoutBinding> {

    private static final String TAG = WeeklyReportAdapter.class.getSimpleName();

    private final Context context;
    private List<WeeklyReportViewModel> data = new ArrayList<>();
    private OnListItemClickListener mOnListItemClickListener;

    private LayerDrawable backgroundRowItem;
    private Drawable unreadListItemIndicator;

    public WeeklyReportAdapter(Context context, int rowLayout) {
        this(context, rowLayout, new ArrayList<WeeklyReportViewModel>());
    }

    public WeeklyReportAdapter(Context context, int rowLayout, List<WeeklyReportViewModel> data) {
        super(rowLayout);
        this.context = context;

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowWeeklyReportListItemLayoutBinding> holder,
                            int position, List<Object> payloads) {

        WeeklyReportViewModel record = data.get(position);
        holder.setData(record);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
