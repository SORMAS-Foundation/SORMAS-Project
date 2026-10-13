/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.ui.events.sevenoneseven;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentFacade;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.events.AbstractEventView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

/**
 * Tab of an event showing its 7-1-7 assessment. When no assessment exists yet, an empty form is shown and the assessment is
 * created on the first save.
 */
public class Event717AssessmentView extends AbstractEventView {

	private static final long serialVersionUID = 2731947268937745129L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/717assessment";

	private static final String TIMELINESS_LOC = "timeliness";

	private CommitDiscardWrapperComponent<Event717AssessmentForm> editComponent;

	public Event717AssessmentView() {
		super(VIEW_NAME);
	}

	/**
	 * @return Whether the 7-1-7 assessment is available for the current user.
	 */
	public static boolean isAvailable() {
		return UiUtil.permitted(FeatureType.EVENT_717_ASSESSMENT, UserRight.EVENT_717_ASSESSMENT_VIEW);
	}

	@Override
	protected void initView(String params) {

		setHeightUndefined();

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);

		Event717AssessmentFacade facade = FacadeProvider.getEvent717AssessmentFacade();
		String eventUuid = getEventRef().getUuid();
		boolean editAllowed = UiUtil.permitted(isEditAllowed(), UserRight.EVENT_717_ASSESSMENT_EDIT) && !isEventDeleted();

		Event717AssessmentDto assessment = facade.getByEventUuid(eventUuid);
		boolean isNew = assessment == null;
		if (isNew) {
			assessment = Event717AssessmentDto.build(getEventRef());
		}

		Event717AssessmentForm form = new Event717AssessmentForm(getEventRef(), editAllowed, assessment.isPseudonymized());
		form.setValue(assessment);

		editComponent = new CommitDiscardWrapperComponent<>(form, editAllowed, form.getFieldGroup());
		editComponent.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				facade.save(form.getValue());
				Notification.show(I18nProperties.getString(Strings.messageEvent717AssessmentSaved), Type.TRAY_NOTIFICATION);
				SormasUI.refreshView();
			}
		});

		if (!isNew && editAllowed) {
			editComponent.addDeleteListener(() -> {
				facade.deleteByEventUuid(eventUuid);
				Notification.show(I18nProperties.getString(Strings.messageEvent717AssessmentDeleted), Type.TRAY_NOTIFICATION);
				SormasUI.refreshView();
			}, I18nProperties.getCaption(Event717AssessmentDto.I18N_PREFIX));
		}

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent, true, TIMELINESS_LOC);
		container.addComponent(layout);

		Event717TimelinessPanel timelinessPanel = form.getTimelinessPanel();
		CssStyles.style(timelinessPanel, CssStyles.VIEW_SECTION);
		layout.addSidePanelComponent(timelinessPanel, TIMELINESS_LOC);

		if (!editAllowed) {
			// table fields stay enabled so that their entries can still be viewed in read-only popups
			for (Field<?> field : form.getFieldGroup().getFields()) {
				if (!(field instanceof AbstractEvent717EntriesField)) {
					field.setEnabled(false);
				}
			}
		}
	}
}
