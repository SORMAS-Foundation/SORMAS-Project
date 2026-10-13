package de.symeda.sormas.app.news;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.core.adapter.databinding.BindingViewHolder;
import de.symeda.sormas.app.databinding.RowNewsListItemLayoutBinding;
import de.symeda.sormas.app.event.edit.EventNewActivity;

public class NewsListAdapter extends BindingPagedListAdapter<News, RowNewsListItemLayoutBinding> {

	private final Context context;

	public NewsListAdapter(Context context) {
		super(R.layout.row_news_list_item_layout);
		this.context = context;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);
		if (holder instanceof BindingViewHolder) {
			BindingViewHolder<News, RowNewsListItemLayoutBinding> pageHolder = (BindingViewHolder<News, RowNewsListItemLayoutBinding>) holder;
			pageHolder.setOnListItemClickListener(this.mOnListItemClickListener);
			News data = pageHolder.getData();
			setColorInPriorityButton(pageHolder.binding.newsDataActionPriority, data.getRiskLevel());
			if (ConfigProvider.hasUserRight(UserRight.EVENT_CREATE)) {
				pageHolder.binding.newsDataPriorityContainer.setOnClickListener(l -> createNewEvent(data));
			}
		}
	}

	private void setColorInPriorityButton(View pageHolder, RiskLevel riskLevel) {
		if (context != null) {
			if (riskLevel == RiskLevel.HIGH) {
				pageHolder.setBackground(context.getDrawable(R.drawable.background_legend_high_priority));
			} else if (riskLevel == RiskLevel.MODERATE) {
				pageHolder.setBackground(context.getDrawable(R.drawable.background_legend_normal_priority));
			} else if (riskLevel == RiskLevel.LOW) {
				pageHolder.setBackground(context.getDrawable(R.drawable.background_legend_low_priority));
			}
		}
	}

	private void createNewEvent(News news) {
		EventNewActivity.startActivityFromNews(context, news);
	}
}
