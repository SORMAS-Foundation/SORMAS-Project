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

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.share.ShareRequestCriteria;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.task.TasksView;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

public class ShareRequestsView extends AbstractView {

	private static final long serialVersionUID = 8407693240992525507L;

	public static final String VIEW_NAME = "shareRequests";

	private final ShareRequestGridComponent gridComponent;

	public ShareRequestsView() {
		super(VIEW_NAME);

		ShareRequestsViewConfiguration viewConfiguration = ViewModelProviders.of(getClass()).get(ShareRequestsViewConfiguration.class);

		ShareRequestCriteria criteria = getCriteria();

		OptionGroup viewSwitcher = new OptionGroup();
		viewSwitcher.setId("viewSwitcher");
		CssStyles.style(viewSwitcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		viewSwitcher.addItem(ShareRequestViewType.INCOMING);
		viewSwitcher.setItemCaption(ShareRequestViewType.INCOMING, I18nProperties.getEnumCaption(ShareRequestViewType.INCOMING));

		viewSwitcher.addItem(ShareRequestViewType.OUTGOING);
		viewSwitcher.setItemCaption(ShareRequestViewType.OUTGOING, I18nProperties.getEnumCaption(ShareRequestViewType.OUTGOING));

		viewSwitcher.setValue(viewConfiguration.getViewType());
		viewSwitcher.addValueChangeListener(e -> {
			ShareRequestViewType viewType = (ShareRequestViewType) e.getProperty().getValue();

			viewConfiguration.setViewType(viewType);
			SormasUI.get().getNavigator().navigateTo(VIEW_NAME);
		});
		addHeaderComponent(viewSwitcher);

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
			ViewModelProviders.of(ShareRequestsView.class).get(ShareRequestCriteria.class, criteria);
		}
		return criteria;
	}

}
