package de.symeda.sormas.ui.utils;

import java.util.Arrays;
import java.util.function.Consumer;

import com.vaadin.server.Sizeable;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.common.DeleteDetails;
import de.symeda.sormas.api.common.DeleteReason;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

public class DeletableUtils {

	public static void showDeleteWithReasonPopUp(String message, Consumer<DeleteDetails> handler) {
		VerticalLayout verticalLayout = new VerticalLayout();
		Label contentLabel = new Label(message);
		verticalLayout.addComponent(contentLabel);

		ComboBox<DeleteReason> deleteReasonComboBox = new ComboBox(null, Arrays.asList(DeleteReason.values()));
		deleteReasonComboBox.setCaption(I18nProperties.getCaption(Captions.deleteReason));
		deleteReasonComboBox.setWidth(100, Sizeable.Unit.PERCENTAGE);
		deleteReasonComboBox.setRequiredIndicatorVisible(true);

		verticalLayout.addComponent(deleteReasonComboBox);
		TextArea deleteOtherReason = new TextArea();
		verticalLayout.addComponent(deleteOtherReason);
		deleteOtherReason.setVisible(false);
		deleteOtherReason.setWidth(100, Sizeable.Unit.PERCENTAGE);
		deleteOtherReason.setRows(3);

		deleteOtherReason.setRequiredIndicatorVisible(true);

		deleteReasonComboBox.addValueChangeListener(valueChangeEvent -> {
			deleteOtherReason.setVisible(valueChangeEvent.getValue() == (DeleteReason.OTHER_REASON));
		});
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingDeleteConfirmation),
			verticalLayout,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					if (deleteReasonComboBox.isEmpty()) {
						deleteReasonComboBox.setComponentError(new UserError(I18nProperties.getString(Strings.messageDeleteReasonNotFilled)));
						return false;
					} else if (deleteReasonComboBox.getValue() == DeleteReason.OTHER_REASON && deleteOtherReason.isEmpty()) {
						deleteOtherReason.setComponentError(new UserError(I18nProperties.getString(Strings.messageOtherDeleteReasonNotFilled)));
						return false;
					}

					if (deleteReasonComboBox.getValue() != DeleteReason.OTHER_REASON && !deleteOtherReason.isEmpty()) {
						deleteOtherReason.clear();
					}

					handler.accept(new DeleteDetails(deleteReasonComboBox.getValue(), deleteOtherReason.getValue()));
				}

				return true;
			});
	}
}
