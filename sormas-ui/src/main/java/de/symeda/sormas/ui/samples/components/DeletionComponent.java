package de.symeda.sormas.ui.samples.components;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;

import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.utils.FormComponent;

/**
 * Deletion reason fields using Vaadin 8 components with own Binder.
 * Self-manages visibility of otherDeletionReason based on deletionReason value.
 */
public class DeletionComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private ComboBox<DeletionReason> deletionReasonField;
	private TextArea otherReasonField;

	public DeletionComponent() {
		super(PathogenTestDto.class);
		buildLayout();
		bindFields();
	}

	private void buildLayout() {
		deletionReasonField = createComboBox(PathogenTestDto.DELETION_REASON, PathogenTestDto.I18N_PREFIX);
		deletionReasonField.setItems(DeletionReason.values());
		deletionReasonField.setItemCaptionGenerator(DeletionReason::toString);
		addFullWidthRow(deletionReasonField);

		otherReasonField = createTextArea(PathogenTestDto.OTHER_DELETION_REASON, PathogenTestDto.I18N_PREFIX);
		otherReasonField.setRows(3);
		addFullWidthRow(otherReasonField);

		// Hidden by default
		deletionReasonField.setVisible(false);
		otherReasonField.setVisible(false);

		// Self-managed visibility: show otherReason only when OTHER_REASON selected
		track(deletionReasonField.addValueChangeListener(e -> {
			boolean showOther = e.getValue() == DeletionReason.OTHER_REASON;
			otherReasonField.setVisible(showOther);
			if (!showOther) {
				otherReasonField.clear();
			}
			updateRowAndSelfVisibility();
		}));
	}

	private void bindFields() {
		binder.forField(deletionReasonField).bind(PathogenTestDto::getDeletionReason, PathogenTestDto::setDeletionReason);
		binder.forField(otherReasonField).bind(PathogenTestDto::getOtherDeletionReason, PathogenTestDto::setOtherDeletionReason);
	}
}
