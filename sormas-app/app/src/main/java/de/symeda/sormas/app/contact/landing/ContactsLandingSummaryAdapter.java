package de.symeda.sormas.app.contact.landing;

import android.content.Context;

import de.symeda.sormas.app.component.visualization.ViewTypeHelper.ViewTypeEnum;
import de.symeda.sormas.app.core.adapter.multiview.AdapterConfiguration;
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterConfiguration;

/**
 * Created by Orson on 01/12/2017.
 */

public class ContactsLandingSummaryAdapter extends EnumMapDataBinderAdapter<ViewTypeEnum> {

    private Context context;

    public ContactsLandingSummaryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewTypeEnum getEnumFromPosition(int position) {
        ViewTypeEnum viewType;

        switch (position) {
            case ContactsLandingSummaryAdapter.PositionHelper.TOTAL_CONTACTS: {
                viewType = ViewTypeEnum.TOTAL;
                break;
            }
            case PositionHelper.UNCONFIRMED_CONTACTS:
            case PositionHelper.CONFIRMED_CONTACTS:
            case PositionHelper.NOT_A_CONTACT:
            case PositionHelper.CONVERTED_TO_CASE:
            case PositionHelper.DROPPED: {
                viewType = ViewTypeEnum.SINGLE_CIRCULAR_PROGRESS;
                break;
            }
            default:
                throw new IllegalArgumentException("The value of position is invalid.");
        }


        return viewType;
    }

    @Override
    public ViewTypeEnum getEnumFromOrdinal(int ordinal) {
        return ViewTypeEnum.values()[ordinal];
    }

    public IAdapterConfiguration startConfig() {
        return new AdapterConfiguration<ViewTypeEnum>(this.context, this);
    }

    static class PositionHelper {
        static final int TOTAL_CONTACTS = 0;
        static final int UNCONFIRMED_CONTACTS = 1;
        static final int CONFIRMED_CONTACTS = 2;
        static final int NOT_A_CONTACT = 3;
        static final int CONVERTED_TO_CASE = 4;
        static final int DROPPED = 5;
    }

}
