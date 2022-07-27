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

package de.symeda.sormas.ui.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.CustomField;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.NotificationTypeGroup;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.CheckboxSetItemDataSource;

public class UserRoleNotificationCheckboxSet extends CustomField<UserRoleDto.NotificationTypes> {

	private static final long serialVersionUID = 4149126643467960889L;

	private VerticalLayout layout;

	@Override
	protected Component initContent() {
		layout = new VerticalLayout();
		layout.setWidthFull();
		layout.setMargin(new MarginInfo(true, false));

		buildCheckboxes();

		return layout;
	}

	@Override
	public Class<? extends UserRoleDto.NotificationTypes> getType() {
		return UserRoleDto.NotificationTypes.class;
	}

	protected void setInternalValue(UserRoleDto.NotificationTypes newValue) {
		super.setInternalValue(UserRoleDto.NotificationTypes.of(newValue.getSms(), newValue.getEmail()));
	}

	private void buildCheckboxes() {
		for (NotificationTypeGroup group : NotificationTypeGroup.values()) {
			List<CheckboxRow> rows = createGroupRows(group);

			layout.addComponent(createGroupHeader(group, rows));
			layout.addComponents(rows.toArray(new Component[] {}));
		}
	}

	private HorizontalLayout createGroupHeader(NotificationTypeGroup group, List<CheckboxRow> rows) {
		Label groupLabel = new Label(group.toString());
		groupLabel.addStyleName(CssStyles.H3);

		Label tickForAllLAbel = new Label(I18nProperties.getString(Strings.checkboxSetTickAnAnswerForAll));
		tickForAllLAbel.addStyleName(CssStyles.LABEL_ITALIC);

		Button tickAllButton = ButtonHelper.createButton(
			I18nProperties.getCaption(Captions.all),
			(e) -> rows.forEach(CheckboxRow::checkAll),
			ValoTheme.BUTTON_LINK,
			CssStyles.BUTTON_COMPACT);
		Button tickSmsButton = ButtonHelper.createButton(
			I18nProperties.getCaption(Captions.userRoleNotificationTypeSms),
			(e) -> rows.forEach(CheckboxRow::checkAllSms),
			ValoTheme.BUTTON_LINK,
			CssStyles.BUTTON_COMPACT);
		Button tickEmailButton = ButtonHelper.createButton(
			I18nProperties.getCaption(Captions.userRoleNotificationTypeEmail),
			(e) -> rows.forEach(CheckboxRow::checkAllEmail),
			ValoTheme.BUTTON_LINK,
			CssStyles.BUTTON_COMPACT);

		HorizontalLayout buttonsLayout = new HorizontalLayout(tickForAllLAbel, tickAllButton, tickSmsButton, tickEmailButton);
		buttonsLayout.setWidthFull();
		buttonsLayout.setExpandRatio(tickForAllLAbel, 1);

		HorizontalLayout headerLayout = new HorizontalLayout(groupLabel, buttonsLayout);
		headerLayout.setWidthFull();

		headerLayout.setExpandRatio(groupLabel, 0.5f);
		headerLayout.setExpandRatio(buttonsLayout, 0.5f);
		headerLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);

		return headerLayout;
	}

	private List<CheckboxRow> createGroupRows(NotificationTypeGroup group) {
		List<NotificationType> groupItems =
			Stream.of(NotificationType.values()).filter(n -> n.getNotificationTypeGroup() == group).collect(Collectors.toList());

		List<CheckboxRow> rows = new ArrayList<>();
		for (int i = 0, size = groupItems.size(); i < size; i += 2) {
			NotificationType item1 = groupItems.get(i);
			NotificationType item2 = i < size - 1 ? groupItems.get(i + 1) : null;

			rows.add(new CheckboxRow(item1, item2));
		}
		return rows;
	}

	private CheckBox createCheckbox(
		String captionTag,
		NotificationType item,
		Function<NotificationType, CheckboxSetItemDataSource<NotificationType>> dataSourceFactory) {
		CheckBox checkBox = new CheckBox(I18nProperties.getCaption(captionTag), dataSourceFactory.apply(item));
		checkBox.addValueChangeListener(e -> fireValueChange(false));

		return checkBox;
	}

	private CheckboxSetItemDataSource<NotificationType> createSmsCheckboxDataSource(NotificationType item2) {
		return createDataSource(item2, () -> getInternalValue().getSms());
	}

	private CheckboxSetItemDataSource<NotificationType> createEmailCheckboxDataSource(NotificationType item2) {
		return createDataSource(item2, () -> getInternalValue().getEmail());
	}

	private CheckboxSetItemDataSource<NotificationType> createDataSource(NotificationType type, Supplier<Set<NotificationType>> valueSetProvider) {
		return new CheckboxSetItemDataSource<>(type, v -> valueSetProvider.get().contains(v), (c, v) -> {
			Set<NotificationType> valueSet = valueSetProvider.get();
			if (c) {
				valueSet.add(v);
			} else {
				valueSet.remove(v);
			}
		});
	}

	private final class CheckboxRow extends HorizontalLayout {

		private static final long serialVersionUID = -3874252190392021250L;

		private final NotificationTypeCheckboxes left;
		private final NotificationTypeCheckboxes right;

		public CheckboxRow(NotificationType leftGroup, NotificationType rightGroup) {
			setWidthFull();
			setMargin(false);

			left = new NotificationTypeCheckboxes(leftGroup);
			addComponent(left);
			setExpandRatio(left, 0.45f);

			HorizontalLayout spacer = new HorizontalLayout();
			addComponent(spacer);
			setExpandRatio(spacer, 0.1f);

			right = rightGroup != null ? new NotificationTypeCheckboxes(rightGroup) : null;
			// if there is no rightGroup, create an empty container to keep the first checkbox in the first half of tha space
			Component rightComponent = right != null ? right : new HorizontalLayout();
			addComponent(rightComponent);
			setExpandRatio(rightComponent, 0.45f);
		}

		public void checkAll() {
			left.checkAll();

			if (right != null) {
				right.checkAll();
			}
		}

		public void checkAllSms() {
			left.checkSms();

			if (right != null) {
				right.checkSms();
			}
		}

		public void checkAllEmail() {
			left.checkEmail();

			if (right != null) {
				right.checkEmail();
			}
		}
	}

	private final class NotificationTypeCheckboxes extends HorizontalLayout {

		private static final long serialVersionUID = -4965656600618090731L;

		private final CheckBox smsCb;
		private final CheckBox emailCb;

		public NotificationTypeCheckboxes(NotificationType type) {
			Label label = new Label(type.toString());
			smsCb = createCheckbox(Captions.userRoleNotificationTypeSms, type, UserRoleNotificationCheckboxSet.this::createSmsCheckboxDataSource);
			emailCb =
				createCheckbox(Captions.userRoleNotificationTypeEmail, type, UserRoleNotificationCheckboxSet.this::createEmailCheckboxDataSource);

			addComponents(label, smsCb, emailCb);

			setWidthFull();
			setExpandRatio(label, 1);
			setExpandRatio(smsCb, 0);
			setExpandRatio(emailCb, 0);
		}

		public void checkAll() {
			smsCb.setValue(true);
			emailCb.setValue(true);
		}

		public void checkSms() {
			smsCb.setValue(true);
		}

		public void checkEmail() {
			emailCb.setValue(true);
		}
	}
}
