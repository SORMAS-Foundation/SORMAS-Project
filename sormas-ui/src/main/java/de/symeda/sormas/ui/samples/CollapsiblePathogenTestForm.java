/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.samples;

import java.util.Optional;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.samples.pathogentestlink.PathogenTestListEntry;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

public class CollapsiblePathogenTestForm extends VerticalLayout {

	private static final long serialVersionUID = -7352963874627364843L;

	private CommitDiscardWrapperComponent<PathogenTestForm> commitDiscardForm;

	private VerticalLayout expandedLayout;
	private VerticalLayout collapsedLayout;

	private Runnable deleteHandler;

	private boolean deleteOnCancel;

	public CollapsiblePathogenTestForm(PathogenTestForm pathogenTestForm, boolean initialExpanded, boolean deleteOnCancel) {
		this.deleteOnCancel = deleteOnCancel;

		setSpacing(false);
		setMargin(false);

		commitDiscardForm = new CommitDiscardWrapperComponent<>(pathogenTestForm, pathogenTestForm.getFieldGroup());
		commitDiscardForm.setMargin(false);
		commitDiscardForm.setSpacing(false);

		commitDiscardForm.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionDone));
		commitDiscardForm.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
		commitDiscardForm.getDiscardButton().setId("cancel");
		commitDiscardForm.getButtonsPanel().setComponentAlignment(commitDiscardForm.getDiscardButton(), Alignment.BOTTOM_LEFT);
		commitDiscardForm.addDoneListener(this::collapse);

		expandedLayout = new VerticalLayout(commitDiscardForm);
		expandedLayout.setSpacing(false);
		expandedLayout.setMargin(false);

		collapsedLayout = new VerticalLayout();
		collapsedLayout.setSpacing(false);
		collapsedLayout.setMargin(false);
		collapsedLayout.setWidth(pathogenTestForm.getWidth(), pathogenTestForm.getWidthUnits());

		this.addComponents(collapsedLayout, expandedLayout);

		resetCollapsedLayout();
		pathogenTestForm.getFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {

			@Override
			public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {

			}

			@Override
			public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
				resetCollapsedLayout();
			}
		});

		if (initialExpanded) {
			expand();
		} else {
			collapse();
		}
	}

	private void resetCollapsedLayout() {
		PathogenTestDto pathogenTest = Optional.ofNullable(getValue()).orElse(new PathogenTestDto());

		collapsedLayout.removeAllComponents();
		PathogenTestListEntry pathogenTestEntry = new PathogenTestListEntry(pathogenTest, false);
		collapsedLayout.addComponent(pathogenTestEntry);

		pathogenTestEntry.addActionButton(pathogenTest.getUuid(), e -> {
			expand();
		}, true);

		if (deleteHandler != null) {
			pathogenTestEntry.addDeleteButton(pathogenTest.getUuid(), (e) -> {
				deleteHandler.run();
			});
		}
	}

	private void expand() {
		collapsedLayout.setVisible(false);
		expandedLayout.setVisible(true);
	}

	private void collapse() {
		collapsedLayout.setVisible(true);
		expandedLayout.setVisible(false);
	}

	public PathogenTestDto getValue() {
		return commitDiscardForm.getWrappedComponent().getValue();
	}

	public void setDeleteHandler(Runnable deleteHandler) {
		this.deleteHandler = deleteHandler;

		resetCollapsedLayout();

		commitDiscardForm.addDiscardListener(() -> {
			if (deleteOnCancel) {
				deleteHandler.run();
			}
		});

		commitDiscardForm.addCommitListener(() -> {
			deleteOnCancel = false;
		});
	}
}
