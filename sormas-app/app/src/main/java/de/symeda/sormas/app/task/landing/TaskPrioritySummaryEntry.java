package de.symeda.sormas.app.task.landing;

/**
 * Created by Orson on 01/12/2017.
 */

public class TaskPrioritySummaryEntry {
    private int key;
    private String label;
    private float value;

    public TaskPrioritySummaryEntry(int key, String label, float value) {
        this.key = key;
        this.label = label;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
