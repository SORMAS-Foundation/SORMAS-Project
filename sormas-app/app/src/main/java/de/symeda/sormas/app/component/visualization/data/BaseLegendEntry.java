package de.symeda.sormas.app.component.visualization.data;

/**
 * Created by Orson on 29/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class BaseLegendEntry {

    public abstract int getKey();

    public abstract int getLegendColor();

    public abstract String getPriorityName();

    public abstract int getLengendShape();

    public abstract BaseLegendEntry setValue(float value);

    public abstract BaseLegendEntry setPercentage(float percentage);

    public abstract float getValue();

    public abstract float getPercentage();
}
