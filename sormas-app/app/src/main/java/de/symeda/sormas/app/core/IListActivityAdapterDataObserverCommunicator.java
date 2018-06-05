package de.symeda.sormas.app.core;

import android.view.View;

/**
 * Created by Orson on 06/12/2017.
 */

public interface IListActivityAdapterDataObserverCommunicator {

    int getListAdapterSize();

    View getEmptyListView();

    View getListView();
}
