package de.symeda.sormas.app.component.visualization;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.visualization.data.SummaryTotalData;
import de.symeda.sormas.app.core.adapter.multiview.DataBinder;
import de.symeda.sormas.app.core.adapter.multiview.RecyclerViewDataBinderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orson on 27/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SummaryTotalBinder extends DataBinder<SummaryTotalBinder.ViewHolder, SummaryTotalData> {

    private List<SummaryTotalData> data = new ArrayList<>();

    public SummaryTotalBinder() {
        super();
    }


    public SummaryTotalBinder(RecyclerViewDataBinderAdapter dataBindAdapter) {
        super(dataBindAdapter);
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.control_lps_cell_total_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindToViewHolder(ViewHolder holder, int position) {
        holder.txtTitle.setText(data.get(position).dataTitle);
        holder.txtValue.setText(data.get(position).dataValue);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void addAll(List<SummaryTotalData> data) {
        this.data.addAll(data);
        notifyBinderDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyBinderDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View layout;
        TextView txtTitle;
        TextView txtValue;

        public ViewHolder(View view) {
            super(view);
            layout = (View)itemView.findViewById(R.id.cellRootLayout);
            txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            txtValue = (TextView) view.findViewById(R.id.txtValue);
        }
    }

    static class PositionHelper {
        static final int TOTAL_TASKS = 0;
    }
}
