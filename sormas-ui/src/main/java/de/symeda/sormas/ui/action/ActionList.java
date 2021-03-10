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
package de.symeda.sormas.ui.action;

import java.util.List;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.action.ActionContext;
import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class ActionList extends PaginationList<ActionDto> {

	private final ActionCriteria actionCriteria;
	private final ActionContext context;

	public ActionList(ActionContext context, ActionCriteria actionCriteria, int maxDisplayedEntries) {

		super(maxDisplayedEntries);
		this.context = context;
		this.actionCriteria = actionCriteria;
	}

	@Override
	public void reload() {
		List<ActionDto> actions = FacadeProvider.getActionFacade().getActionList(actionCriteria, null, null);

		setEntries(actions);
		showPage(1);
		if (actions.isEmpty()) {
			Label noActionsLabel = new Label(String.format(I18nProperties.getCaption(Captions.actionNoActions), context.toString()));
			listLayout.addComponent(noActionsLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		SormasUI ui = ((SormasUI) getUI());
		boolean hasUserRightActionEdit = ui.getUserProvider().hasUserRight(UserRight.ACTION_EDIT);
		List<ActionDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			ActionDto action = displayedEntries.get(i);
			ActionListEntry listEntry = new ActionListEntry(action);
			if (hasUserRightActionEdit) {
				listEntry.addEditListener(
					i,
					(ClickListener) event -> ControllerProvider.getActionController().edit(ui, listEntry.getAction(), ActionList.this::reload));
			}
			listLayout.addComponent(listEntry);
		}
	}
}
