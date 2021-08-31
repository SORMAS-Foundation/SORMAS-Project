/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.sormastosormas;

import java.util.Arrays;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestCriteria;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.task.TasksView;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class ShareRequestsView extends AbstractView {

	private static final long serialVersionUID = 8407693240992525507L;

	public static final String VIEW_NAME = "shareRequests";

	private final ShareRequestGridComponent gridComponent;

	public ShareRequestsView() {
		super(VIEW_NAME);

		ViewConfiguration viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);

		ShareRequestCriteria criteria = getCriteria();

		gridComponent = new ShareRequestGridComponent(viewConfiguration, criteria, () -> {
			navigateTo(criteria, true);
		}, () -> {
			ViewModelProviders.of(ShareRequestsView.class).remove(ShareRequestCriteria.class);
			navigateTo(null, true);
		});

		addComponent(gridComponent);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		gridComponent.reload(event);
	}

	private ShareRequestCriteria getCriteria() {
		ShareRequestCriteria criteria = ViewModelProviders.of(ShareRequestsView.class).get(ShareRequestCriteria.class);

		if (!ViewModelProviders.of(TasksView.class).has(TaskCriteria.class)) {
			// init default filter
			criteria = new ShareRequestCriteria();
			criteria.setStatusesExcepted(Arrays.asList(ShareRequestStatus.REJECTED, ShareRequestStatus.REVOKED));
			ViewModelProviders.of(ShareRequestsView.class).get(ShareRequestCriteria.class, criteria);
		}
		return criteria;
	}

}
