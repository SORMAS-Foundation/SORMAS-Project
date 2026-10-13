package de.symeda.sormas.app.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.CustomWebView;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.util.Callback;

public class NewsListFragment extends PagedBaseListFragment<NewsListAdapter> implements OnListItemClickListener {

	private LinearLayoutManager linearLayoutManager;
	private RecyclerView recyclerViewForList;
	private Callback refreshCallBack;

	public static NewsListFragment newInstance() {
		return newInstance(NewsListFragment.class, null);
	}

	public void setRefreshCallBack(Callback refreshCallBack) {
		this.refreshCallBack = refreshCallBack;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		recyclerViewForList = view.findViewById(R.id.recyclerViewForList);

		return view;
	}

	@Override
	public NewsListAdapter getNewListAdapter() {
		return (NewsListAdapter) ((NewsListActivity) getActivity()).getAdapter();
	}

	@Override
	public void onListItemClick(View view, int position, Object item) {
		News news = (News) item;
		if (!DataHelper.isHttpUrl(news.getNewsLink())) {
			NotificationHelper.showNotification(
				(NotificationContext) getActivity(),
				NotificationType.ERROR,
				I18nProperties.getValidationError(Validations.newsInvalidLink));
			return;
		}
		CustomWebView.startActivity(getContext(), news.getNewsLink());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		recyclerViewForList.setAdapter(getListAdapter());
		recyclerViewForList.setLayoutManager(linearLayoutManager);
	}

	@Override
	public void onResume() {
		super.onResume();
		final SwipeRefreshLayout swiperefresh = this.getView().findViewById(R.id.swiperefresh);
		if (swiperefresh != null) {
			swiperefresh.setOnRefreshListener(() -> {
				swiperefresh.setRefreshing(false);
				if (refreshCallBack != null) {
					refreshCallBack.call();
				}
			});
		}
	}

}
