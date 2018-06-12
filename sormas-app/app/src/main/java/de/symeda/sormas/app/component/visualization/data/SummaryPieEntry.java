package de.symeda.sormas.app.component.visualization.data;

/**
 * Created by Orson on 29/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SummaryPieEntry {

    private int entryKey;
    private float value;
    private String label;

    public SummaryPieEntry(float value, String label, int entryKey) {
        this.value = value;
        this.label = label;
        this.entryKey = entryKey;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getEntryKey() {
        return entryKey;
    }

    public void setEntryKey(int entryKey) {
        this.entryKey = entryKey;
    }
}
