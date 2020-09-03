package de.symeda.sormas.app.report.aggregate;

import java.util.List;

import android.content.Context;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseReportActivity;
import de.symeda.sormas.app.BaseReportFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.PageMenuItem;

public class AggregateReportsActivity extends BaseReportActivity {

	public static void startActivity(Context context) {
		BaseActivity.startActivity(context, AggregateReportsActivity.class, buildBundle(0));
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		setPageMenuVisibility(false);
		return null;
	}

	@Override
	public BaseReportFragment buildReportFragment(PageMenuItem menuItem) {
		return AggregateReportsFragment.newInstance();
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.main_menu_aggregate_reports;
	}

	@Override
	protected boolean showTitleBar() {
		return true;
	}
}
