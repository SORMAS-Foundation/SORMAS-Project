package de.symeda.sormas.app.component;

/**
 * Created by Stefan Szczesny on 02.08.2016.
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
