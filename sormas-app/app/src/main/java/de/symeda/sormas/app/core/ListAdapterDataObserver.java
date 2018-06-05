package de.symeda.sormas.app.core;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.View;

/**
 * Created by Orson on 06/12/2017.
 */

public class ListAdapterDataObserver extends AdapterDataObserver {

    private IListActivityAdapterDataObserverCommunicator communicator;


    public ListAdapterDataObserver(IListActivityAdapterDataObserverCommunicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public void onChanged() {
        toggleListEmptyHint();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        toggleListEmptyHint();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        // fallback to onItemRangeChanged(positionStart, itemCount) if app
        // does not override this method.
        onItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        toggleListEmptyHint();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        toggleListEmptyHint();
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        toggleListEmptyHint();
    }


    private void toggleListEmptyHint() {
        if (this.communicator.getListAdapterSize() == 0) {
            this.communicator.getEmptyListView().setVisibility(View.VISIBLE);
            this.communicator.getListView().setVisibility(View.GONE);
        } else {
            this.communicator.getEmptyListView().setVisibility(View.GONE);
            this.communicator.getListView().setVisibility(View.VISIBLE);
        }
    }
}
