package de.symeda.sormas.app.task.landing;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.visualization.data.SummaryPieData;
import de.symeda.sormas.app.component.visualization.data.SummaryPieEntry;
import de.symeda.sormas.app.core.adapter.multiview.DataBinder;
import de.symeda.sormas.app.core.adapter.multiview.RecyclerViewDataBinderAdapter;

/**
 * Created by Orson on 27/11/2017.
 */

public class SummaryPieChartWithLegendBinder extends DataBinder<SummaryPieChartWithLegendBinder.ViewHolder, SummaryPieData> {

    private List<SummaryPieData> data = new ArrayList<>();

    public SummaryPieChartWithLegendBinder() {
        super();
    }

    public SummaryPieChartWithLegendBinder(RecyclerViewDataBinderAdapter dataBindAdapter) {
        super(dataBindAdapter);
    }

    @Override
    public SummaryPieChartWithLegendBinder.ViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.control_lps_cell_pie_layout, parent, false);
        return new SummaryPieChartWithLegendBinder.ViewHolder(view);
    }

    @Override
    public void bindToViewHolder(SummaryPieChartWithLegendBinder.ViewHolder holder, int position) {
        List<PieEntry> entries = new ArrayList<PieEntry>();

        if (position == PositionHelper.TASK_PRIORITY)
            holder.layout.setBackground(this.getContext().getResources().getDrawable(R.drawable.background_summary_cell_last));

        holder.txtTitle.setText(data.get(position).getTitle());

        for (SummaryPieEntry data : data.get(position).getEntries()) {
            entries.add(new PieEntry(data.getValue(), data.getLabel()));
        }

        //Add entries to data set
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setValueTextSize(10);
        dataSet.setDrawValues(false);
        dataSet.setSelectionShift(0);

        //Set colors
        dataSet.setColors(data.get(position).getColors());

        holder.pieChart.getDescription().setEnabled(false);
        holder.pieChart.setTouchEnabled(true);
        holder.pieChart.setRotationEnabled(true);
        holder.pieChart.setDrawHoleEnabled(false);
        holder.pieChart.setHoleRadius(0);
        holder.pieChart.setDrawEntryLabels(false);
        //holder.pieChart.setDrawSliceText(false);
        //holder.pieChart.setTex

        Legend legend = holder.pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setEnabled(false);




        //dataSet.setColor();
        //dataSet.setValueTextColor();

        PieData pieData = new PieData(dataSet);
        holder.pieChart.setData(pieData);
        holder.pieChart.invalidate();





        TaskPriorityLegendAdapter adapter = new TaskPriorityLegendAdapter(getContext(), data.get(position).getLegendEntries(),
                R.layout.control_lps_task_priority_legend_row_layout);
        holder.legendListView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void addAll(List<SummaryPieData> data) {
        this.data.addAll(data);
        notifyBinderDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyBinderDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View layout;
        PieChart pieChart;
        TextView txtTitle;
        ListView legendListView;

        public ViewHolder(View view) {
            super(view);
            layout = (View)itemView.findViewById(R.id.cellRootLayout);


            legendListView = (ListView) view.findViewById(R.id.lvLegend);
            pieChart = (PieChart) view.findViewById(R.id.pieChart);
            txtTitle = (TextView) view.findViewById(R.id.txtTitle);

            legendListView.setDividerHeight(0);
            legendListView.setClickable(false);
        }
    }

    static class PositionHelper {
        static final int TASK_PRIORITY = 0;
    }
}

