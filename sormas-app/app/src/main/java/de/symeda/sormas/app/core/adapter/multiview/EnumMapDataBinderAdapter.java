package de.symeda.sormas.app.core.adapter.multiview;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Orson on 27/11/2017.
 */

public abstract class EnumMapDataBinderAdapter<E extends Enum<E>> extends RecyclerViewDataBinderAdapter {

    private Map<E, DataBinder> binderMap = new HashMap<>();

    @Override
    public int getItemCount() {
        int itemCount = 0;
        for (DataBinder binder: binderMap.values()) {
            itemCount += binder.getItemCount();
        }

        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        return getEnumFromPosition(position).ordinal();
    }

    @Override
    public <T extends DataBinder> T getDataBinder(int viewType) {
        return getDataBinder(getEnumFromOrdinal(viewType));
    }

    @Override
    public int getPosition(DataBinder binder, int binderPosition) {
        int itemCount = getItemCount();
        int positionToReturn = itemCount;

        //Get view type (key) assoicated with binder
        E targetViewTypeEnum = getEnumFromBinder(binder);
        for (int i = 0, count = itemCount; i < count; i++) {
            //Is this the correct data binder to handle rendering
            if (targetViewTypeEnum == getEnumFromPosition(i)) {
                binderPosition--;
                if (binderPosition < 0) {
                    positionToReturn = i;
                    break;
                }
            }
        }
        return positionToReturn;
    }

    @Override
    public int getBinderPosition(int position) {
        //Get what view type (Enum) that should apply to a particular position
        E targetViewType = getEnumFromPosition(position);
        int binderPosition = -1;
        for (int i = 0; i <= position; i++) {
            if (targetViewType == getEnumFromPosition(i)) {
                binderPosition++;
            }
        }

        if (binderPosition == -1) {
            throw new IllegalArgumentException("Invalid ARGUMENT");
        }
        return binderPosition;
    }

    public E getEnumFromBinder(DataBinder binder) {
        for (Map.Entry<E, DataBinder> entry : binderMap.entrySet()) {
            if (entry.getValue().equals(binder)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Invalid Data Binder");
    }

    public <T extends DataBinder> T getDataBinder(E e) {
        return (T) binderMap.get(e);
    }

    public Map<E, DataBinder> getBinderMap() {
        return binderMap;
    }

    public void registerBinder(E e, DataBinder binder) {
        binderMap.put(e, binder);
    }

    @Override
    public void notifyBinderItemRangeChanged(DataBinder binder, int positionStart, int itemCount) {
        for (int i = positionStart; i <= itemCount; i++) {
            int p = getPosition(binder, i);
            notifyItemChanged(p);
        }
    }

    @Override
    public void notifyBinderItemRangeInserted(DataBinder binder, int positionStart, int itemCount) {
        for (int i = positionStart; i <= itemCount; i++) {
            notifyItemInserted(getPosition(binder, i));
        }
    }

    @Override
    public void notifyBinderItemRangeRemoved(DataBinder binder, int positionStart, int itemCount) {
        for (int i = positionStart; i <= itemCount; i++) {
            notifyItemRemoved(getPosition(binder, i));
        }
    }

    public abstract E getEnumFromPosition(int position);

    public abstract E getEnumFromOrdinal(int ordinal);







}
