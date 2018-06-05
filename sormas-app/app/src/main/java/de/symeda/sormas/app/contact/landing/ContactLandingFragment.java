package de.symeda.sormas.app.contact.landing;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.BaseLandingActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.component.visualization.SummaryCircularProgressBinder;
import de.symeda.sormas.app.component.visualization.SummaryTotalBinder;
import de.symeda.sormas.app.component.visualization.ViewTypeHelper;
import de.symeda.sormas.app.component.visualization.ViewTypeHelper.ViewTypeEnum;
import de.symeda.sormas.app.component.visualization.data.SummaryCircularData;
import de.symeda.sormas.app.component.visualization.data.SummaryTotalData;
import de.symeda.sormas.app.contact.list.ContactListActivity;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterRegistrationContext;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterRegistrationService;
import de.symeda.sormas.app.util.ResourceUtils;

/**
 * Created by Orson on 11/12/2017.
 */

public class ContactLandingFragment extends BaseLandingActivityFragment<ViewTypeHelper.ViewTypeEnum, ContactsLandingSummaryAdapter> {

    private FollowUpStatus statusFilters[];
    private final int DATA_XML_LANDING_MENU = R.xml.data_landing_page_contact_menu; // "xml/data_landing_page_contact_menu.xml";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.statusFilters = new FollowUpStatus[] { FollowUpStatus.FOLLOW_UP, FollowUpStatus.COMPLETED,
                FollowUpStatus.CANCELED, FollowUpStatus.LOST,FollowUpStatus.NO_FOLLOW_UP };

        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            getLandingAdapter().startConfig().forViewType(ViewTypeEnum.TOTAL, new IAdapterRegistrationService() {
                @Override
                public void register(IAdapterRegistrationContext context) throws java.lang.InstantiationException, IllegalAccessException {
                    context.registerBinder(SummaryTotalBinder.class).registerData(getTotalData());
                }
            })
            .forViewType(ViewTypeEnum.SINGLE_CIRCULAR_PROGRESS, new IAdapterRegistrationService() {
                @Override
                public void register(IAdapterRegistrationContext context) throws java.lang.InstantiationException, IllegalAccessException {
                    context.registerBinder(SummaryCircularProgressBinder.class).registerData(getCircularData());
                }
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }

        configure();
    }

    @Override
    public ContactsLandingSummaryAdapter createLandingAdapter() {
        return new ContactsLandingSummaryAdapter(getActivity());
    }

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        return new GridLayoutManager(getActivity(),3, GridLayoutManager.VERTICAL,false);
    }

    @Override
    public int getMenuData() {
        return DATA_XML_LANDING_MENU;
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView<?> parent, LandingPageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int)(new Random(DateTime.now().getMillis() * 1000).nextInt()/10000000);
    }

    @Override
    public boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) {
        FollowUpStatus status = statusFilters[menuItem.getKey()];
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(getContext(), status, SearchBy.BY_FILTER_STATUS);
        ContactListActivity.goToActivity(getActivity(), dataCapsule);

        return true;
    }



    private List<SummaryTotalData> getTotalData() {
        List<SummaryTotalData> dataSet = new ArrayList<>();
        SummaryTotalData data = new SummaryTotalData();
        data.dataTitle = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_total_contacts);
        data.dataValue = "3120";
        dataSet.add(data);

        return dataSet;
    }

    private List<SummaryCircularData> getCircularData() {
        List<SummaryCircularData> dataSet = new ArrayList<>();

        String titleUnconfirmed = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_contacts_unconfirmed);
        String titleConfirmed = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_contacts_confirmed);
        String titleNotAContact = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_contacts_not_a_contact);
        String titleConvertedToCase = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_contacts_converted_to_case);
        String titleDropped = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_contacts_dropped);

        //Confirmed Cases
        SummaryCircularData data1 = new SummaryCircularData(titleUnconfirmed, 1010, 1.5 * 12.4);

        //Probable Cases
        SummaryCircularData data2 = new SummaryCircularData(titleConfirmed, 439, 1 * 12.4);

        //Suspected Case
        SummaryCircularData data3 = new SummaryCircularData(titleNotAContact, 1230, 2 * 12.4);

        //Fatalities
        SummaryCircularData data4 = new SummaryCircularData(titleConvertedToCase, 231, 3 * 12.4);

        //Case Fatality Rate
        SummaryCircularData data5 = new SummaryCircularData(titleDropped, 362, 4 * 12.4);

        dataSet.add(data1);
        dataSet.add(data2);
        dataSet.add(data3);
        dataSet.add(data4);
        dataSet.add(data5);

        return dataSet;
    }

    public static ContactLandingFragment newInstance()
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(ContactLandingFragment.class);
    }
}
