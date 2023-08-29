package de.symeda.sormas.app.environment;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum EnvironmentSection
	implements
	StatusElaborator {

	ENVIRONMENT_INFO(R.string.caption_environment_information, R.drawable.ic_drawer_environment_blue_24dp),

	TASKS(R.string.caption_environment_tasks, R.drawable.ic_drawer_user_task_blue_24dp);

	private int friendlyNameResourceId;
	private int iconResourceId;

	EnvironmentSection(int friendlyNameResourceId, int iconResourceId) {
		this.friendlyNameResourceId = friendlyNameResourceId;
		this.iconResourceId = iconResourceId;
	}

	public static EnvironmentSection fromOrdinal(int ordinal) {
		return EnvironmentSection.values()[ordinal];
	}

	@Override
	public String getFriendlyName(Context context) {
		return context.getResources().getString(friendlyNameResourceId);
	}

	@Override
	public int getColorIndicatorResource() {
		return 0;
	}

	@Override
	public Enum getValue() {
		return null;
	}

	@Override
	public int getIconResourceId() {
		return iconResourceId;
	}
}
