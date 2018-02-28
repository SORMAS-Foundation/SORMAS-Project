package de.symeda.sormas.app.component;

/**
 * Created by Orson on 26/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class Item<C> {
    private String key;
    private C value;
    public Item(String key, C value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public C getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key;
    }
}
