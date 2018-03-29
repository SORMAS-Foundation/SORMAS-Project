package de.symeda.sormas.app.task.landing;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.joda.time.DateTime;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseLandingActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.component.visualization.SummaryCircularProgressBinder;
import de.symeda.sormas.app.component.visualization.SummaryTotalBinder;
import de.symeda.sormas.app.component.visualization.ViewTypeHelper;
import de.symeda.sormas.app.component.visualization.ViewTypeHelper.ViewTypeEnum;
import de.symeda.sormas.app.component.visualization.data.SummaryCircularData;
import de.symeda.sormas.app.component.visualization.data.SummaryPieData;
import de.symeda.sormas.app.component.visualization.data.SummaryPieEntry;
import de.symeda.sormas.app.component.visualization.data.SummaryTotalData;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterDataModifier;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterRegistrationContext;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterRegistrationService;
import de.symeda.sormas.app.task.TaskPriorityLegendEntry;
import de.symeda.sormas.app.task.list.TaskListActivity;
import de.symeda.sormas.app.util.PercentageUtils;
import de.symeda.sormas.app.util.ResourceUtils;

/**
 * Created by Orson on 11/12/2017.
 */

public class TaskLandingFragment extends BaseLandingActivityFragment<ViewTypeHelper.ViewTypeEnum, TasksLandingSummaryAdapter> {

    private TaskStatus statusFilters[];
    private final String DATA_XML_LANDING_MENU = "xml/data_landing_page_task_menu.xml";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // task status "null" matches done and discarded tasks
        this.statusFilters = new TaskStatus[] { TaskStatus.PENDING, null, TaskStatus.NOT_EXECUTABLE };

        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            AssetManager assetManager = getContext().getAssets();
            InputStream dataInputStream = assetManager.open("xml/data_landing_page_task_priority.xml");
            final List<TaskPrioritySummaryEntry> taskPriorityData = new TaskPrioritySummaryParser(getContext()).parse(dataInputStream);


            final List<Float> valueList = new ArrayList<>();
            for (TaskPrioritySummaryEntry entry: taskPriorityData) {
                valueList.add(entry.getValue());
            }

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
            })
            .forViewType(ViewTypeEnum.PIECHART_WITH_LEGEND, new IAdapterRegistrationService() {
                @Override
                public void register(IAdapterRegistrationContext context) throws java.lang.InstantiationException, IllegalAccessException {
                    context.registerBinder(SummaryPieChartWithLegendBinder.class).registerData(getPieData()).forEach(new IAdapterDataModifier<SummaryPieData>() {
                        @Override
                        public void modify(SummaryPieData item, int position) {
                            //Get data from xml
                            int i = 0;
                            for(TaskPrioritySummaryEntry entry: taskPriorityData) {
                                //TODO: Set value from database
                                //entry.setValue(null);

                                item.addEntry(new SummaryPieEntry(entry.getValue(), entry.getLabel(), entry.getKey()));
                                item.addLegendEntry(TaskPriorityLegendEntry.findByKey(entry.getKey()).setValue(entry.getValue())
                                        .setPercentage(PercentageUtils.percentageOf(entry.getValue(), valueList)));

                                if (i == 0) {
                                    item.addColor(getContext().getResources().getColor(R.color.normalPriority));
                                } else if (i == 1) {
                                    item.addColor(getContext().getResources().getColor(R.color.lowPriority));
                                } else {
                                    item.addColor(getContext().getResources().getColor(R.color.highPriority));
                                }

                                i = i + 1;
                            }
                        }
                    });
                }
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        configure();
    }

    @Override
    public TasksLandingSummaryAdapter createLandingAdapter() {
        return new TasksLandingSummaryAdapter(getActivity());
    }

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3, GridLayoutManager.VERTICAL,false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //Using position, get span size from data
                if (position == TasksLandingSummaryAdapter.PositionHelper.TASK_PRIORITY)
                    return 2;

                return 1;
            }
        });


        return gridLayoutManager;
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
        TaskStatus status = statusFilters[menuItem.getKey()];
        TaskLandingToListCapsule dataCapsule = new TaskLandingToListCapsule(getContext(), status, SearchBy.BY_FILTER_STATUS);
        TaskListActivity.goToActivity(getActivity(), dataCapsule);

        return true;
    }

    private List<SummaryTotalData> getTotalData() {
        List<SummaryTotalData> dataSet = new ArrayList<>();
        for (int i = 1; i <= 1; i++) {
            SummaryTotalData data = new SummaryTotalData();
            data.dataTitle = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_total_tasks);;
            data.dataValue = "5839";
            dataSet.add(data);
        }

        return dataSet;
    }

    private List<SummaryPieData> getPieData() {
        List<SummaryPieData> dataSet = new ArrayList<>();

        String titleTotalTasks = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_total_tasks);
        dataSet.add(new SummaryPieData(titleTotalTasks));
        return dataSet;
    }

    private List<SummaryCircularData> getCircularData() {
        List<SummaryCircularData> dataSet = new ArrayList<>();

        String titlePending = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_tasks_pending);
        String titleDone = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_tasks_done);
        String titleRemoved = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_tasks_removed);
        String titleNotExecutable = ResourceUtils.getString(getActivity(), R.string.title_landing_cell_tasks_not_executable);

        //Probable Cases
        SummaryCircularData data1 = new SummaryCircularData(titlePending, 439, 1 * 12.4);

        //Suspected Case
        SummaryCircularData data2 = new SummaryCircularData(titleDone, 1230, 2 * 12.4);

        //Fatalities
        SummaryCircularData data3 = new SummaryCircularData(titleRemoved, 231, 3 * 12.4);

        //Case Fatality Rate
        SummaryCircularData data4 = new SummaryCircularData(titleNotExecutable, 362, 4 * 12.4);

        dataSet.add(data1);
        dataSet.add(data2);
        dataSet.add(data3);
        dataSet.add(data4);

        return dataSet;
    }

    public static TaskLandingFragment newInstance()
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(TaskLandingFragment.class);
    }
}
