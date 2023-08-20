package de.symeda.sormas.app.environment.read;

import java.util.List;

import android.content.Context;
import android.view.MenuItem;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.environment.EnvironmentEditAuthorization;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.environment.EnvironmentSection;
import de.symeda.sormas.app.environment.edit.EnvironmentEditActivity;
import de.symeda.sormas.app.util.Bundler;

public class EnvironmentReadActivity extends BaseReadActivity<Environment> {

	public static void startActivity(Context context, String rootUuid, boolean finishInsteadOfUpNav) {
		BaseReadActivity.startActivity(context, EnvironmentReadActivity.class, buildBundle(rootUuid, finishInsteadOfUpNav));
	}

	public static void startActivity(Context context, String rootUuid) {
		BaseReadActivity.startActivity(context, EnvironmentReadActivity.class, buildBundle(rootUuid));
	}

	public static Bundler buildBundle(String rootUuid) {
		return BaseReadActivity.buildBundle(rootUuid);
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_environment_read;
	}

	@Override
	protected Environment queryRootEntity(String recordUuid) {
		return DatabaseHelper.getEnvironmentDao().queryUuid(recordUuid);
	}

	@Override
	public void goToEditView() {
		final EnvironmentSection section = EnvironmentSection.fromOrdinal(getActivePage().getPosition());
		EnvironmentEditActivity.startActivity(getContext(), getRootUuid(), section);
	}

	@Override
	protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Environment activityRootData) {
		final EnvironmentSection section = EnvironmentSection.fromOrdinal(menuItem.getPosition());
		BaseReadFragment fragment;
		switch (section) {
		case ENVIRONMENT_INFO:
			fragment = EnvironmentReadFragment.newInstance(activityRootData);
			break;
		case TASKS:
			fragment = EnvironmentReadTaskListFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}

		return fragment;
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		List<PageMenuItem> menuItems = PageMenuItem.fromEnum(EnvironmentSection.values(), getContext());
		return menuItems;
	}

	@Override
	protected void processActionbarMenu() {
		super.processActionbarMenu();
		final Environment selectedEnvironment = DatabaseHelper.getEnvironmentDao().queryUuid(getRootUuid());
		final MenuItem editMenu = getEditMenu();

		if (editMenu != null) {
			if (ConfigProvider.hasUserRight(UserRight.ENVIRONMENT_EDIT)
				&& EnvironmentEditAuthorization.isEnvironmentEditAllowed(selectedEnvironment)) {
				editMenu.setVisible(true);
			} else {
				editMenu.setVisible(false);
			}
		}
	}
}
