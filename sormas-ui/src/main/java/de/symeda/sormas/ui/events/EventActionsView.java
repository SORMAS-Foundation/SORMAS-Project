/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.action.ActionContext;
import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.action.ActionList;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class EventActionsView extends AbstractEventView {

	private static final long serialVersionUID = 597552547876685564L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/eventactions";

	private ActionCriteria criteria = new ActionCriteria();

	private ActionList list;
	private Button addButton;
	private DetailSubComponentWrapper listLayout;
	private EventActionFilterForm filterForm;

	public EventActionsView() {
		super(VIEW_NAME);
		setSizeFull();
	}

	/**
	 * Creates the top bar component with header and create button.
	 */
	private HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth(100, Unit.PERCENTAGE);

		Label header = new Label(I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.EVENT_ACTIONS));
		header.setSizeUndefined();
		CssStyles.style(header, CssStyles.H2, CssStyles.VSPACE_NONE);
		topLayout.addComponent(header);

		// add create button if user has role
		if (UiUtil.permitted(UserRight.ACTION_CREATE)) {
			addButton = ButtonHelper.createIconButton(Captions.actionCreate, VaadinIcons.PLUS_CIRCLE, e -> {
				ControllerProvider.getActionController().create(ActionContext.EVENT, this.getEventRef(), this::reload);
			}, ValoTheme.BUTTON_PRIMARY);

			topLayout.addComponent(addButton);
			topLayout.setComponentAlignment(addButton, Alignment.MIDDLE_RIGHT);
		}

		topLayout.addStyleName(CssStyles.VSPACE_3);
		return topLayout;
	}

	/**
	 * Create the filter bar.
	 */
	private HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		filterForm = new EventActionFilterForm();
		filterForm.addResetHandler(e -> navigateTo(null));
		filterForm.addApplyHandler(e -> navigateTo(criteria));
		filterLayout.addComponent(filterForm);

		return filterLayout;
	}

	@Override
	protected void initView(String params) {

		if (params.contains("?")) {
			criteria.fromUrlParams(params.substring(params.indexOf("?") + 1));
		}
		EventReferenceDto eventRef = getEventRef();
		criteria.event(eventRef);

		if (list == null) {
			list = new ActionList(ActionContext.EVENT, criteria, 20);
			listLayout = new DetailSubComponentWrapper(() -> null);
			listLayout.setSizeFull();
			listLayout.setMargin(true);
			listLayout.setSpacing(false);

			HorizontalLayout topBar = createTopBar();
			listLayout.addComponent(topBar);

			HorizontalLayout filterBar = createFilterBar();
			listLayout.addComponent(filterBar);

			listLayout.addComponent(list);
			listLayout.setExpandRatio(list, 1);
			setSubComponent(listLayout);

			boolean hasEventEditRight = UiUtil.permitted(UserRight.EVENT_EDIT);
			if (hasEventEditRight) {
				listLayout.setEnabled(isEditAllowed() && !isEventDeleted());
			} else {
				topBar.setEnabled(false);
				filterBar.setEnabled(false);
			}
		}

		updateFilterComponents();

		reload();
	}

	/**
	 * Update filter form values.
	 */
	private void updateFilterComponents() {
		// disable triggers on value change
		applyingCriteria = true;
		filterForm.setValue(criteria);
		applyingCriteria = false;
	}

	/**
	 * Reload datas in the list.
	 */
	private void reload() {
		list.reload();
	}
}
