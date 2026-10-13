/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.epipulse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.epipulse.EpipulseExportCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.statistics.AbstractStatisticsView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ViewConfiguration;

// @Push
public class EpipulseExportView extends AbstractStatisticsView {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final long serialVersionUID = -5549414867103771784L;
	private static final int POLL_INTERVAL_MS = 10000;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/epipulse-export";

	private final EpipulseExportGridComponent epipulseExportGridComponent;
	private ViewConfiguration viewConfiguration;
	private Thread pollThread;

	public EpipulseExportView() {

		super(VIEW_NAME);

		if (!ViewModelProviders.of(EpipulseExportView.class).has(EpipulseExportCriteria.class)) {
			// init default filter
			EpipulseExportCriteria criteria = new EpipulseExportCriteria();
			ViewModelProviders.of(EpipulseExportView.class).get(EpipulseExportCriteria.class, criteria);
		}

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		epipulseExportGridComponent = new EpipulseExportGridComponent(getViewTitleLabel(), this);
		addComponent(epipulseExportGridComponent);

		if (UiUtil.permitted(UserRight.EPIPULSE_EXPORT_CREATE)) {
			Button createButton = ButtonHelper.createIconButton(
				Captions.epipulseNewExport,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getEpipulseExportController().create(epipulseExportGridComponent.getGrid()::reload),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		epipulseExportGridComponent.reload(event);

		//startPolling();
	}

	@Override
	public void detach() {
		super.detach();

		//stopPolling();
	}

	private void startPolling() {
		if (pollThread == null || !pollThread.isAlive()) {
			UI ui = UI.getCurrent();

			pollThread = new Thread(() -> {
				while (!Thread.currentThread().isInterrupted()) {
					try {
						Thread.sleep(POLL_INTERVAL_MS);

						ui.access(() -> {
							if (isAttached()) {
								epipulseExportGridComponent.getGrid().getDataProvider().refreshAll();
								logger.info("Reloaded Epipulse export grid");
							}
						});
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			});

			pollThread.setDaemon(true);
			pollThread.start();
		}
	}

	private void stopPolling() {
		if (pollThread != null && pollThread.isAlive()) {
			pollThread.interrupt();
		}
	}

	public ViewConfiguration getViewConfiguration() {
		return viewConfiguration;
	}
}
