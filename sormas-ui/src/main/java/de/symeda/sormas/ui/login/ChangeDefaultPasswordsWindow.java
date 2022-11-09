/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui.login;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.opencsv.CSVWriter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.ui.utils.ButtonHelper;

public class ChangeDefaultPasswordsWindow extends Window {

	public ChangeDefaultPasswordsWindow(Runnable onContinue, List<UserDto> otherUsersWithDefaultPassword) {
		setCaption(" " + I18nProperties.getString(Strings.headingSecurityAlert));
		setIcon(VaadinIcons.WARNING);
		setWidth(40, Unit.PERCENTAGE);
		final VerticalLayout content = new VerticalLayout();
		content.setMargin(true);
		content.setWidthFull(); 

		Label introductionLabel = new Label(I18nProperties.getString(Strings.DefaultPassword_otherUsersIntroduction));
		introductionLabel.setWidthFull();
		introductionLabel.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		content.addComponent(introductionLabel);

		Label actionLabel = new Label(I18nProperties.getString(Strings.DefaultPassword_otherUsersAction));
		actionLabel.setWidthFull();
		actionLabel.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		content.addComponent(actionLabel);

		List<MutablePair<UserDto, String>> usersWithNewPassword =
			otherUsersWithDefaultPassword.stream().map(u -> new MutablePair<UserDto, String>(u, null)).collect(Collectors.toList());
		Grid<MutablePair<UserDto, String>> userGrid = new Grid<>();
		userGrid.setWidthFull();
		userGrid.setHeight(250, Unit.PIXELS);
		MultiSelectionModel<MutablePair<UserDto, String>> selectionModel =
			(MultiSelectionModel<MutablePair<UserDto, String>>) userGrid.setSelectionMode(Grid.SelectionMode.MULTI);
		userGrid.setItems(usersWithNewPassword);
		userGrid.addComponentColumn(element -> new Label(element.getLeft().getUserName()))
			.setCaption(I18nProperties.getCaption(Captions.User_userName));
		userGrid.addComponentColumn(element -> new Label(getNewPasswordOrUnchangedText(element.getRight())))
			.setCaption(I18nProperties.getString(Strings.DefaultPassword_newPassword));
		selectionModel.selectAll();
		content.addComponent(userGrid);

		Label newPasswordSetHintsLabel = new Label(I18nProperties.getString(Strings.DefaultPassword_otherUsersNewPasswordSetHints));
		newPasswordSetHintsLabel.setWidthFull();
		newPasswordSetHintsLabel.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		newPasswordSetHintsLabel.setStyleName(ValoTheme.LABEL_SUCCESS, true);
		newPasswordSetHintsLabel.setVisible(false);

		Button continueButton = ButtonHelper.createButton(Captions.actionRemindMeLater, (Button.ClickListener) clickEvent -> {
			close();
			onContinue.run();
		}, ValoTheme.BUTTON_PRIMARY);

		FileDownloader fileDownloader = new OnDemandFileDownloader(new OnDemandFileDownloader.OnDemandStreamResource() {

			@Override
			public String getFilename() {
				return "sormas-defaultusers.csv";
			}

			@Override
			public InputStream getStream() {
				StringWriter stringWriter = new StringWriter();
				CSVWriter csvWriter = CSVUtils.createCSVWriter(stringWriter, ',');
				csvWriter.writeNext(
					new String[] {
						I18nProperties.getCaption(Captions.User_userName),
						I18nProperties.getString(Strings.DefaultPassword_newPassword) });
				for (MutablePair<UserDto, String> item : usersWithNewPassword) {
					if (item.getRight() != null) {
						csvWriter.writeNext(
							new String[] {
								item.getLeft().getUserName(),
								getNewPasswordOrUnchangedText(item.getRight()) });
					}

				}
				return new ByteArrayInputStream(stringWriter.toString().getBytes(StandardCharsets.UTF_8));
			}
		});

		Button exportToCsvButton = ButtonHelper.createButton(Captions.export, (Button.ClickListener) clickEvent -> {
		});
		exportToCsvButton.setEnabled(false);

		fileDownloader.extend(exportToCsvButton);

		Button generateNewPasswordsButton = ButtonHelper.createButton(Captions.actionGenerateNewPasswords, (Button.ClickListener) clickEvent -> {
			selectionModel.getSelectedItems().forEach(item -> {
				item.setRight(FacadeProvider.getUserFacade().resetPassword(item.getLeft().getUuid()));
				userGrid.getDataProvider().refreshItem(item);
			});
			selectionModel.deselectAll();
			newPasswordSetHintsLabel.setVisible(true);
			continueButton.setCaption(I18nProperties.getCaption(Captions.actionContinue));
			exportToCsvButton.setEnabled(true);
			center();
		});

		HorizontalLayout buttonBarLayout = new HorizontalLayout();
		buttonBarLayout.addComponent(generateNewPasswordsButton);
		buttonBarLayout.addComponent(exportToCsvButton);
		content.addComponent(buttonBarLayout);
		content.addComponent(newPasswordSetHintsLabel);
		content.addComponent(continueButton);

		setModal(true);
		setResizable(false);
		setClosable(false);
		setContent(content);
		setDraggable(false);
	}

	private String getNewPasswordOrUnchangedText(String password) {
		if (password != null) {
			return password;
		} else {
			return I18nProperties.getString(Strings.DefaultPassword_unchanged);
		}
	}

	/**
	 * internal used mutable pair
	 * equals and hashcode method are only dependant on the left value ("key"), but not on the right one
	 * 
	 * @param <L>
	 *            the class of the left value (e.g. key)
	 * @param <R>
	 *            the class of the right value (e.g. value)
	 */
	private static class MutablePair<L, R> {

		private L left;
		private R right;

		public MutablePair(L left, R right) {
			this.left = left;
			this.right = right;
		}

		public L getLeft() {
			return left;
		}

		public void setLeft(L left) {
			this.left = left;
		}

		public R getRight() {
			return right;
		}

		public void setRight(R right) {
			this.right = right;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			MutablePair<?, ?> that = (MutablePair<?, ?>) o;
			return Objects.equals(left, that.left);
		}

		@Override
		public int hashCode() {
			return Objects.hash(left);
		}
	}

	/**
	 * This specializes {@link FileDownloader} in a way, such that both the file name and content can be determined
	 * on-demand, i.e. when the user has clicked the component.
	 *
	 * Based on https://vaadin.com/docs/v8/framework/articles/LettingTheUserDownloadAFile
	 * Cache time set to 0 to prevent caching of on demand generated content
	 */
	private static class OnDemandFileDownloader extends FileDownloader {

		private static final long serialVersionUID = 1L;
		private final OnDemandStreamResource onDemandStreamResource;

		public OnDemandFileDownloader(OnDemandStreamResource onDemandStreamResource) {
			super(new StreamResource(onDemandStreamResource, ""));
			this.onDemandStreamResource = onDemandStreamResource;
		}

		@Override
		public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path) throws IOException {
			getResource().setFilename(onDemandStreamResource.getFilename());
			getResource().setCacheTime(0);
			return super.handleConnectorRequest(request, response, path);
		}

		private StreamResource getResource() {
			StreamResource result;
			this.getSession().lock();
			try {
				result = (StreamResource) this.getResource("dl");
			} finally {
				this.getSession().unlock();
			}
			return result;
		}

		public interface OnDemandStreamResource extends StreamResource.StreamSource {

			String getFilename();
		}
	}
}
