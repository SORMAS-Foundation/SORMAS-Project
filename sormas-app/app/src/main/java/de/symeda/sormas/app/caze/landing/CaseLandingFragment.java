package de.symeda.sormas.app.caze.landing;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.joda.time.DateTime;
import de.symeda.sormas.app.BaseLandingActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.list.CaseListActivity;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.component.visualization.SummaryCircularProgressBinder;
import de.symeda.sormas.app.component.visualization.SummaryTotalBinder;
import de.symeda.sormas.app.component.visualization.ViewTypeHelper;
import de.symeda.sormas.app.component.visualization.ViewTypeHelper.ViewTypeEnum;
import de.symeda.sormas.app.component.visualization.data.SummaryCircularData;
import de.symeda.sormas.app.component.visualization.data.SummaryTotalData;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterRegistrationContext;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterRegistrationService;
import de.symeda.sormas.app.util.ResourceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.caze.InvestigationStatus;

/**
 * Created by Orson on 11/12/2017.
 */

public class CaseLandingFragment extends BaseLandingActivityFragment<ViewTypeHelper.ViewTypeEnum, CasesLandingSummaryAdapter> {

    private InvestigationStatus statusFilters[];
    private final String DATA_XML_LANDING_MENU = "xml/data_landing_page_case_menu.xml";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.statusFilters = new InvestigationStatus[] { InvestigationStatus.PENDING, InvestigationStatus.DONE, InvestigationStatus.DISCARDED };
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
    public CasesLandingSummaryAdapter createLandingAdapter() {
        return new CasesLandingSummaryAdapter(getActivity());
    }

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        return new GridLayoutManager(getActivity(),3, GridLayoutManager.VERTICAL,false);
    }

    @Override
    public String getMenuData() {
        return DATA_XML_LANDING_MENU;
    }

    @Override
    public int onNotificationCountChanging(AdapterView<?> parent, LandingPageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int)(new Random(DateTime.now().getMillis() * 1000).nextInt()/10000000);
    }

    @Override
    public boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) {
        InvestigationStatus status = statusFilters[menuItem.getKey()];
        CaseLandingToListCapsule dataCapsule = new CaseLandingToListCapsule(getContext(), status, SearchStrategy.BY_FILTER_STATUS);
        CaseListActivity.goToActivity(getActivity(), dataCapsule);

        return true;
    }


    private List<SummaryTotalData> getTotalData() {
        List<SummaryTotalData> dataSet = new ArrayList<>();

        SummaryTotalData data = new SummaryTotalData();
        data.dataTitle = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_total_cases);;
        data.dataValue = "3120";
        dataSet.add(data);

        return dataSet;
    }

    private List<SummaryCircularData> getCircularData() {
        List<SummaryCircularData> dataSet = new ArrayList<>();

        String titleConfirmedCase = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_cases_confirmed_case);
        String titleProbableCase = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_cases_probable_case);
        String titleSuspectedCase = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_cases_suspected_case);
        String titleFatalities = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_cases_fatalities);
        String titleCaseFatalityRate = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_cases_case_fatality_rate);

        //Confirmed Cases
        SummaryCircularData data1 = new SummaryCircularData(titleConfirmedCase, 1010, 1.5 * 12.4);

        //Probable Cases
        SummaryCircularData data2 = new SummaryCircularData(titleProbableCase, 439, 1 * 12.4);

        //Suspected Case
        SummaryCircularData data3 = new SummaryCircularData(titleSuspectedCase, 1230, 2 * 12.4);

        //Fatalities
        SummaryCircularData data4 = new SummaryCircularData(titleFatalities, 231,
                3 * 12.4, R.color.circularProgressFinishedWatchOut,
                R.color.circularProgressUnfinishedWatchOut);

        //Case Fatality Rate
        SummaryCircularData data5 = new SummaryCircularData(titleCaseFatalityRate, 362,
                4 * 12.4, R.color.circularProgressFinishedWatchOut,
                R.color.circularProgressUnfinishedWatchOut);

        dataSet.add(data1);
        dataSet.add(data2);
        dataSet.add(data3);
        dataSet.add(data4);
        dataSet.add(data5);

        return dataSet;
    }

    public static CaseLandingFragment newInstance()
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(CaseLandingFragment.class);
    }
}
