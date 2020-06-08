/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.dashboard.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseSummaryFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.visualization.SummaryCircularProgressBinder;
import de.symeda.sormas.app.component.visualization.SummaryTotalBinder;
import de.symeda.sormas.app.component.visualization.ViewTypeHelper;
import de.symeda.sormas.app.component.visualization.data.SummaryCircularData;
import de.symeda.sormas.app.component.visualization.data.SummaryPieData;
import de.symeda.sormas.app.component.visualization.data.SummaryPieEntry;
import de.symeda.sormas.app.component.visualization.data.SummaryTotalData;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterDataModifier;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterRegistrationContext;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterRegistrationService;
import de.symeda.sormas.app.task.TaskPriorityLegendEntry;
import de.symeda.sormas.app.task.landing.SummaryPieChartWithLegendBinder;
import de.symeda.sormas.app.util.PercentageUtils;
import de.symeda.sormas.app.util.ResourceUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Orson on 08/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class TaskSummaryFragment extends BaseSummaryFragment<ViewTypeHelper.ViewTypeEnum, TaskSummaryAdapter> {

	public static final String TAG = TaskSummaryFragment.class.getSimpleName();

	private TaskStatus statusFilters[] = new TaskStatus[] {
		TaskStatus.PENDING,
		null,
		TaskStatus.NOT_EXECUTABLE };
	private CompositeSubscription mSubscription = new CompositeSubscription();

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final List<Float> valueList = new ArrayList<>();

		showPreloader();
		Subscription mTaskPrioritySubscription = getTaskPriorityObservable().subscribe(new Subscriber<List<TaskPrioritySummaryEntry>>() {

			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {
				Log.e(TAG, e.getMessage(), e);
				hidePreloader();
				showEmptySummaryHint();
			}

			@Override
			public void onNext(final List<TaskPrioritySummaryEntry> taskPriorityData) {
				if (taskPriorityData == null)
					return;

				for (TaskPrioritySummaryEntry entry : taskPriorityData) {
					valueList.add(entry.getValue());
				}

				/*
				 * .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
				 * @Override
				 * public Observable<?> call(Observable<? extends Void> observable) {
				 * return observable.delay(ConstantHelper.REPEAT_INTERVAL_IN_SECONDS, TimeUnit.SECONDS);
				 * }
				 * }, Schedulers.io())
				 */

				Subscription mDataSubscription =
					Observable.zip(getTotalDataObservable(), getCircularDataObservable(), getPieDataObservable(), getMergeDataObservable())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<TaskObservableDataResult>() {

							@Override
							public void onCompleted() {
								Log.d(TAG, "Completed");
							}

							@Override
							public void onError(Throwable e) {
								Log.e(TAG, e.getMessage(), e);
								hidePreloader();
								showEmptySummaryHint();
							}

							@Override
							public void onNext(TaskObservableDataResult taskObservableDataResult) {
								final List<SummaryTotalData> totalData = taskObservableDataResult.getTotalData();
								final List<SummaryCircularData> circularData = taskObservableDataResult.getCircularData();
								final List<SummaryPieData> pieData = taskObservableDataResult.getPieData();

								try {
									getLandingAdapter().startConfig()
										.forViewType(ViewTypeHelper.ViewTypeEnum.TOTAL, new IAdapterRegistrationService() {

											@Override
											public void register(final IAdapterRegistrationContext context)
												throws java.lang.InstantiationException, IllegalAccessException {
												context.registerBinder(SummaryTotalBinder.class).registerData(totalData);
											}
										})
										.forViewType(ViewTypeHelper.ViewTypeEnum.SINGLE_CIRCULAR_PROGRESS, new IAdapterRegistrationService() {

											@Override
											public void register(final IAdapterRegistrationContext context)
												throws java.lang.InstantiationException, IllegalAccessException {
												context.registerBinder(SummaryCircularProgressBinder.class).registerData(circularData);
											}
										})
										.forViewType(ViewTypeHelper.ViewTypeEnum.PIECHART_WITH_LEGEND, new IAdapterRegistrationService() {

											@Override
											public void register(final IAdapterRegistrationContext context)
												throws java.lang.InstantiationException, IllegalAccessException {
												context.registerBinder(SummaryPieChartWithLegendBinder.class)
													.registerData(pieData)
													.forEach(new IAdapterDataModifier<SummaryPieData>() {

														@Override
														public void modify(SummaryPieData item, int position) {
															int i = 0;
															for (TaskPrioritySummaryEntry entry : taskPriorityData) {
																//TODO: Set value from database
																//entry.setFieldValue(null);

																item.addEntry(
																	new SummaryPieEntry(entry.getValue(), entry.getLabel(), entry.getKey()));
																item.addLegendEntry(
																	TaskPriorityLegendEntry.findByKey(entry.getKey())
																		.setValue(entry.getValue())
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
									Log.e(TAG, e.getMessage(), e);
								} catch (java.lang.InstantiationException e) {
									Log.e(TAG, e.getMessage(), e);
								}

								hidePreloader();
							}
						});

				mSubscription.add(mDataSubscription);
			}
		});

		mSubscription.add(mTaskPrioritySubscription);

		configure();
	}

	//<editor-fold desc="Observable Methods">

	private Observable<List<TaskPrioritySummaryEntry>> getTaskPriorityObservable() {
		//final XmlResourceParser parser = getResources().getXml(R.xml.data_landing_page_task_priority);
		return Observable.defer(new Func0<Observable<List<TaskPrioritySummaryEntry>>>() {

			@Override
			public Observable<List<TaskPrioritySummaryEntry>> call() {
				return Observable.create(new Observable.OnSubscribe<List<TaskPrioritySummaryEntry>>() {

					@Override
					public void call(Subscriber<? super List<TaskPrioritySummaryEntry>> subscriber) {
						try {
							List<TaskPrioritySummaryEntry> list = new ArrayList<TaskPrioritySummaryEntry>() {

								{
									add(new TaskPrioritySummaryEntry(0, TaskPriority.NORMAL.toString(), new Random().nextInt(100)));
									add(new TaskPrioritySummaryEntry(1, TaskPriority.LOW.toString(), new Random().nextInt(100)));
									add(new TaskPrioritySummaryEntry(2, TaskPriority.HIGH.toString(), new Random().nextInt(100)));
								}
							};

							subscriber.onNext(list);
							subscriber.onCompleted();
						} catch (Exception e) {
							subscriber.onError(e);
						}
					}
				});

				/*
				 * try {
				 * return Observable.just(new TaskPrioritySummaryParser().parse(parser));
				 * } catch (Exception e) {
				 * throw e;
				 * }
				 */
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	private Observable<List<SummaryTotalData>> getTotalDataObservable() {
		return Observable.defer(new Func0<Observable<List<SummaryTotalData>>>() {

			@Override
			public Observable<List<SummaryTotalData>> call() {
				return Observable.create(new Observable.OnSubscribe<List<SummaryTotalData>>() {

					@Override
					public void call(Subscriber<? super List<SummaryTotalData>> subscriber) {
						try {
							subscriber.onNext(getTotalDataAsync());
							subscriber.onCompleted();
						} catch (Exception e) {
							subscriber.onError(e);
						}
					}
				});
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	private Observable<List<SummaryPieData>> getPieDataObservable() {
		return Observable.defer(new Func0<Observable<List<SummaryPieData>>>() {

			@Override
			public Observable<List<SummaryPieData>> call() {
				return Observable.create(new Observable.OnSubscribe<List<SummaryPieData>>() {

					@Override
					public void call(Subscriber<? super List<SummaryPieData>> subscriber) {
						try {
							subscriber.onNext(getPieDataAsync());
							subscriber.onCompleted();
						} catch (Exception e) {
							subscriber.onError(e);
						}
					}
				});
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	private Observable<List<SummaryCircularData>> getCircularDataObservable() {
		return Observable.defer(new Func0<Observable<List<SummaryCircularData>>>() {

			@Override
			public Observable<List<SummaryCircularData>> call() {
				return Observable.create(new Observable.OnSubscribe<List<SummaryCircularData>>() {

					@Override
					public void call(Subscriber<? super List<SummaryCircularData>> subscriber) {
						try {
							subscriber.onNext(getCircularDataAsync());
							subscriber.onCompleted();
						} catch (Exception e) {
							subscriber.onError(e);
						}
					}
				});
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	private Func3<List<SummaryTotalData>, List<SummaryCircularData>, List<SummaryPieData>, TaskObservableDataResult> getMergeDataObservable() {
		return new Func3<List<SummaryTotalData>, List<SummaryCircularData>, List<SummaryPieData>, TaskObservableDataResult>() {

			@Override
			public TaskObservableDataResult call(
				List<SummaryTotalData> summaryTotalData,
				List<SummaryCircularData> summaryCircularData,
				List<SummaryPieData> summaryPieData) {
				return new TaskObservableDataResult(summaryTotalData, summaryCircularData, summaryPieData);
			}
		};
	}

	//</editor-fold>

	//<editor-fold desc="Private Methods">
	private List<SummaryTotalData> getTotalDataAsync() {
		List<SummaryTotalData> dataSet = new ArrayList<>();
		for (int i = 1; i <= 1; i++) {
			SummaryTotalData data = new SummaryTotalData();
			data.dataTitle = ResourceUtils.getString(getActivity(), R.string.caption_total_tasks);;
			data.dataValue = String.valueOf(new Random().nextInt(10000));
			dataSet.add(data);
		}

		return dataSet;
	}

	private List<SummaryPieData> getPieDataAsync() {
		List<SummaryPieData> dataSet = new ArrayList<>();

		String titleTotalTasks = ResourceUtils.getString(getActivity(), R.string.caption_total_tasks);
		dataSet.add(new SummaryPieData(titleTotalTasks));
		return dataSet;
	}

	private List<SummaryCircularData> getCircularDataAsync() {
		Random random = new Random();
		List<SummaryCircularData> dataSet = new ArrayList<>();

		String titlePending = ResourceUtils.getString(getActivity(), R.string.caption_tasks_pending);
		String titleDone = ResourceUtils.getString(getActivity(), R.string.caption_tasks_done);
		String titleRemoved = ResourceUtils.getString(getActivity(), R.string.caption_tasks_removed);
		String titleNotExecutable = ResourceUtils.getString(getActivity(), R.string.caption_tasks_not_executable);

		//Probable Cases
		SummaryCircularData data1 = new SummaryCircularData(titlePending, random.nextInt(10000), random.nextInt(100));

		//Suspected Case
		SummaryCircularData data2 = new SummaryCircularData(titleDone, random.nextInt(10000), random.nextInt(100));

		//Fatalities
		SummaryCircularData data3 = new SummaryCircularData(titleRemoved, random.nextInt(10000), random.nextInt(100));

		//Case Fatality Rate
		SummaryCircularData data4 = new SummaryCircularData(titleNotExecutable, random.nextInt(10000), random.nextInt(100));

		dataSet.add(data1);
		dataSet.add(data2);
		dataSet.add(data3);
		dataSet.add(data4);

		return dataSet;
	}
	//</editor-fold>

	//<editor-fold desc="More Overrides">

	@Override
	protected int getSectionTitleResId() {
		return R.string.heading_task_summary;
	}

	@Override
	protected int getEntityResId() {
		return R.string.entity_task;
	}

	@Override
	protected TaskSummaryAdapter createSummaryAdapter() {
		return new TaskSummaryAdapter(getActivity());
	}

	@Override
	protected RecyclerView.LayoutManager createLayoutManager() {
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
		gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

			@Override
			public int getSpanSize(int position) {
				//Using position, get span size from data
				if (position == TaskSummaryAdapter.PositionHelper.TASK_PRIORITY)
					return 2;

				return 1;
			}
		});

		return gridLayoutManager;
	}

	@Override
	protected int getContainerResId() {
		return R.id.fragment_frame_task;
	}

	@Override
	public String getIdentifier() {
		return TAG;
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mSubscription != null && !mSubscription.isUnsubscribed())
			mSubscription.unsubscribe();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mSubscription != null && !mSubscription.isUnsubscribed())
			mSubscription.unsubscribe();
	}
	//</editor-fold>

	public static TaskSummaryFragment newInstance() {
		return newInstance(TaskSummaryFragment.class, null);
	}
}
