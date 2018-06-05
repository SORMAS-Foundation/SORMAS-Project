package de.symeda.sormas.app.component.visualization.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orson on 29/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SummaryPieData {


    private String title;
    private List<SummaryPieEntry> entries;
    private List<Integer> colors;
    private List<BaseLegendEntry> legendEntries;

    public SummaryPieData(String title) {
        this.title = title;
        this.entries = new ArrayList<SummaryPieEntry>();
        this.colors = new ArrayList<Integer>();
    }

    public SummaryPieData(String title, List<SummaryPieEntry> entries, List<Integer> colors) {
        this.title = title;
        this.entries = entries;
        this.colors = colors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SummaryPieEntry> getEntries() {
        return entries;
    }

    public List<BaseLegendEntry> getLegendEntries() {
        return legendEntries;
    }

    public void setEntries(List<SummaryPieEntry> entries) {
        this.entries = entries;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public void addEntry(SummaryPieEntry entry) {
        if (this.entries == null)
            this.entries = new ArrayList<SummaryPieEntry>();

        this.entries.add(entry);
    }

    public void addColor(Integer color) {
        if (this.colors == null)
            this.colors = new ArrayList<Integer>();

        this.colors.add(color);
    }

    public void addLegendEntry(BaseLegendEntry entry) {
        if (this.legendEntries == null)
            this.legendEntries = new ArrayList<>();

        this.legendEntries.add(entry);
    }

}



