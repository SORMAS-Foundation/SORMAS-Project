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

package de.symeda.sormas.ui.externalmessage;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageResult;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings({
	"java:S110", // suppress sonar too many parents warning
	"java:S2160" // equals not overridden; value-equality is not needed for Vaadin form components
})
public class ExternalMessageForm extends AbstractEditForm<ExternalMessageDto> {

	private static final long serialVersionUID = -3859401780981133265L;

	//@formatter:off
	private static final String HTML_LAYOUT =
			fluidRowLocs(ExternalMessageDto.UUID, ExternalMessageDto.MESSAGE_DATE_TIME) +
			fluidRowLocs(ExternalMessageDto.EXTERNAL_MESSAGE_DETAILS);
	//@formatter:on

	private TabSheet detailsTabSheet;
	private Panel structuredViewPanel;
	@SuppressWarnings("java:S1450") // referenced in addFields, tab listener and setValue - cannot be local
	private Panel detailedViewPanel;
	private VerticalLayout structuredViewContainer;
	private VerticalLayout detailedViewContainer;
	private boolean structuredViewLoaded;
	private boolean detailedViewLoaded;

	@SuppressWarnings("java:S1948") // Logger is effectively serializable via its name
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ExternalMessageForm() {
		super(ExternalMessageDto.class, ExternalMessageDto.I18N_PREFIX);
	}

	@Override
	protected void addFields() {
		addFields(ExternalMessageDto.UUID, ExternalMessageDto.MESSAGE_DATE_TIME);

		structuredViewContainer = new VerticalLayout();
		structuredViewContainer.setMargin(false);
		structuredViewContainer.setSizeUndefined();

		structuredViewPanel = new Panel();
		structuredViewPanel.setSizeFull();
		structuredViewPanel.addStyleName("lab-message-structured-view");
		structuredViewPanel.setContent(structuredViewContainer);

		detailedViewContainer = new VerticalLayout();
		detailedViewContainer.setMargin(false);
		detailedViewContainer.setSizeUndefined();

		detailedViewPanel = new Panel();
		detailedViewPanel.setSizeFull();
		detailedViewPanel.addStyleName("lab-message-detailed-view");
		detailedViewPanel.setContent(detailedViewContainer);

		detailsTabSheet = new TabSheet();
		detailsTabSheet.setSizeFull();
		detailsTabSheet.addStyleName("lab-message-details");
		detailsTabSheet.addTab(structuredViewPanel, I18nProperties.getCaption(Captions.externalMessage_structuredView));
		detailsTabSheet.addTab(detailedViewPanel, I18nProperties.getCaption(Captions.externalMessage_detailedView));

		detailsTabSheet.addSelectedTabChangeListener(event -> {
			ExternalMessageDto msg = getValue();
			if (msg == null) {
				return;
			}
			Component selected = event.getTabSheet().getSelectedTab();
			if (selected == structuredViewPanel) {
				loadStructuredView(msg);
			} else if (selected == detailedViewPanel) {
				loadDetailedView(msg);
			}
		});

		getContent().addComponent(detailsTabSheet, ExternalMessageDto.EXTERNAL_MESSAGE_DETAILS);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	public void setValue(ExternalMessageDto externalMessage) {
		super.setValue(externalMessage);
		getFieldGroup().setReadOnly(true);
		structuredViewLoaded = false;
		detailedViewLoaded = false;
		structuredViewContainer.removeAllComponents();
		detailedViewContainer.removeAllComponents();
		loadStructuredView(externalMessage);
		detailsTabSheet.setSelectedTab(structuredViewPanel);
	}

	private void loadStructuredView(ExternalMessageDto externalMessage) {
		if (structuredViewLoaded) {
			return;
		}
		try {
			ExternalMessageResult<String> result = FacadeProvider.getExternalLabResultsFacade().convertToHTML(externalMessage);
			if (result.isSuccess()) {
				CustomLayout layout = new CustomLayout();
				layout.setTemplateContents(result.getValue());
				structuredViewContainer.addComponent(layout);
			} else {
				structuredViewContainer.addComponent(createXmlDisplay(externalMessage.getExternalMessageDetails()));
				VaadinUiUtil.showWarningPopup(result.getError());
			}
		} catch (Exception e) {
			structuredViewContainer.addComponent(createXmlDisplay(externalMessage.getExternalMessageDetails()));
			logger.error(e.getMessage());
		}
		structuredViewLoaded = true;
	}

	private void loadDetailedView(ExternalMessageDto externalMessage) {
		if (detailedViewLoaded) {
			return;
		}
		try {
			ExternalMessageResult<String> result = FacadeProvider.getExternalLabResultsFacade().convertToDetailedHTML(externalMessage);
			if (result.isSuccess()) {
				CustomLayout layout = new CustomLayout();
				layout.setTemplateContents(result.getValue());
				detailedViewContainer.addComponent(layout);
			} else {
				detailedViewContainer.addComponent(createXmlDisplay(externalMessage.getExternalMessageDetails()));
				VaadinUiUtil.showWarningPopup(result.getError());
			}
		} catch (Exception e) {
			detailedViewContainer.addComponent(createXmlDisplay(externalMessage.getExternalMessageDetails()));
			logger.error(e.getMessage());
		}
		detailedViewLoaded = true;
	}

	private Component createXmlDisplay(String xml) {
		Label label = new Label();
		label.setValue(xml);
		label.setContentMode(ContentMode.PREFORMATTED);
		return label;
	}
}
