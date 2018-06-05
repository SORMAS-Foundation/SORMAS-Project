package de.symeda.sormas.app.component.visualization.data;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 29/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SummaryCircularData {

    private String title;
    private double value;
    private double percentage;
    private int finishedColor;
    private int unfinishedColor;

    public SummaryCircularData(String title, double value, double percentage, int finishedColor, int unfinishedColor) {
        this.title = title;
        this.value = value;
        this.percentage = percentage;
        this.finishedColor = finishedColor;
        this.unfinishedColor = unfinishedColor;
    }

    public SummaryCircularData(String title, double value, double percentage) {
        this.title = title;
        this.value = value;
        this.percentage = percentage;
        this.finishedColor = R.color.circularProgressFinishedDefault;
        this.unfinishedColor = R.color.circularProgressUnfinishedDefault;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public int getFinishedColor() {
        return finishedColor;
    }

    public void setFinishedColor(int finishedColor) {
        this.finishedColor = finishedColor;
    }

    public int getUnfinishedColor() {
        return unfinishedColor;
    }

    public void setUnfinishedColor(int unfinishedColor) {
        this.unfinishedColor = unfinishedColor;
    }
}
