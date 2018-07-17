package de.symeda.sormas.app.component.menu;

import android.view.View;
import android.widget.AdapterView;

public interface PageMenuClickListener {
    boolean onPageMenuClick(AdapterView<?> parent, View view, PageMenuItem menuItem, int position, long id) throws IllegalAccessException, InstantiationException;
}