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

package de.symeda.sormas.app.component.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.Html;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.synclog.SyncLog;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogSyncLogLayoutBinding;
import de.symeda.sormas.app.util.DateFormatHelper;

public class SyncLogDialog extends AbstractDialog {

	public static final String TAG = SyncLogDialog.class.getSimpleName();

	private static int INITIAL_LOG_LIMIT = 10;

	private DialogSyncLogLayoutBinding contentBinding;

	private int lastDisplayCount = 0;
	private int displayCount = INITIAL_LOG_LIMIT;
	private List<SyncLog> logs = new ArrayList<>();
	private StringBuilder content = new StringBuilder();
	private Date lastDate;

	public SyncLogDialog(final FragmentActivity activity) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_sync_log_layout,
			R.layout.dialog_root_two_button_panel_layout,
			R.string.heading_sync_conflicts,
			-1);
	}

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		this.contentBinding = (DialogSyncLogLayoutBinding) binding;
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
		logs = DatabaseHelper.getSyncLogDao().queryForAll(AbstractDomainObject.CREATION_DATE, false);
		buildAndDisplayDialogContent();

		setPositiveCallback(() -> {
			if (logs.size() > lastDisplayCount) {
				buildAndDisplayDialogContent();
			} else {
				NotificationHelper.showDialogNotification(SyncLogDialog.this, NotificationType.INFO, R.string.message_no_more_entries);
			}
		});
	}

	@Override
	public int getPositiveButtonText() {
		return R.string.action_loadMore;
	}

	@Override
	public int getPositiveButtonIconResourceId() {
		return R.drawable.ic_autorenew_black_24dp;
	}

	private void buildAndDisplayDialogContent() {
		if (logs.size() == 0) {
			contentBinding.setData(Html.fromHtml(getActivity().getResources().getString(R.string.hint_no_sync_errors)));
		} else {
			for (int i = lastDisplayCount; i < displayCount; i++) {
				if (i >= logs.size()) {
					break;
				}
				SyncLog log = logs.get(i);
				if (lastDate != null && DateHelper.isSameDay(lastDate, log.getCreationDate())) {
					content.append("<p><b>").append(log.getEntityName()).append("</b><br/>").append(log.getConflictText()).append("</p>");
				} else {
					if (lastDate != null) {
						content.append("<br/>");
					}
					content.append("<p><b><u>")
						.append(DateFormatHelper.formatLocalDate(log.getCreationDate()))
						.append("</u></b></p><p><b>")
						.append(log.getEntityName())
						.append("</b><br/>")
						.append(log.getConflictText())
						.append("</p>");
				}
				lastDate = log.getCreationDate();
			}

			lastDisplayCount = displayCount;
			displayCount += INITIAL_LOG_LIMIT;

			contentBinding.setData(Html.fromHtml(content.toString()));
		}
	}
}
