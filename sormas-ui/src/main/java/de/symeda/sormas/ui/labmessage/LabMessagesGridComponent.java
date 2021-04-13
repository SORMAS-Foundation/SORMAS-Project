package de.symeda.sormas.ui.labmessage;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class LabMessagesGridComponent extends VerticalLayout {

	private LabMessageCriteria criteria;

	private LabMessageGrid grid;
	private LabMessagesView labMessagesView;
	private Map<Button, String> statusButtons;
	private Button activeStatusButton;

	private VerticalLayout gridLayout;

	private Label viewTitleLabel;
	private String originalViewTitle;

	public LabMessagesGridComponent(Label viewTitleLabel, LabMessagesView labMessagesView) {
		setSizeFull();
		setMargin(false);

		this.viewTitleLabel = viewTitleLabel;
		this.labMessagesView = labMessagesView;
		originalViewTitle = viewTitleLabel.getValue();

		criteria = ViewModelProviders.of(LabMessagesView.class).get(LabMessageCriteria.class);

		grid = new LabMessageGrid(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createProcessStatusFilterBar());
		gridLayout.addComponent(grid);
		grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());

		gridLayout.setMargin(true);
		styleGridLayout(gridLayout);

		addComponent(gridLayout);
	}

	public HorizontalLayout createProcessStatusFilterBar() {
		HorizontalLayout processStatusFilterLayout = new HorizontalLayout();
		processStatusFilterLayout.setMargin(false);
		processStatusFilterLayout.setSpacing(true);
		processStatusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		Button allButton = createAndAddStatusButton(null, processStatusFilterLayout);
		activeStatusButton = allButton;

		createAndAddStatusButton(LabMessageStatus.UNPROCESSED, processStatusFilterLayout);
		createAndAddStatusButton(LabMessageStatus.PROCESSED, processStatusFilterLayout);
		createAndAddStatusButton(LabMessageStatus.UNCLEAR, processStatusFilterLayout);
		createAndAddStatusButton(LabMessageStatus.FORWARDED, processStatusFilterLayout);

		return processStatusFilterLayout;
	}

	private void styleGridLayout(VerticalLayout gridLayout) {
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
	}

	public void reload(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateStatusButtons();
		grid.reload();
	}

	private Button createAndAddStatusButton(@Nullable LabMessageStatus status, HorizontalLayout buttonLayout) {
		Button button = ButtonHelper.createButton(status == null ? I18nProperties.getCaption(Captions.all) : status.toString(), e -> {
			criteria.labMessageStatus(status);
			labMessagesView.navigateTo(criteria);
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);

		if (status != null) {
			button.setData(status);
		}

		button.setCaptionAsHtml(true);

		buttonLayout.addComponent(button);
		statusButtons.put(button, button.getCaption());

		return button;
	}

	private void updateStatusButtons() {
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
			if (b.getData() == criteria.getLabMessageStatus()) {
				activeStatusButton = b;
			}
		});
		if (activeStatusButton != null) {
			CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
			activeStatusButton
				.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getItemCount())));
		}

		LabMessageStatus activeStatus = (LabMessageStatus) activeStatusButton.getData();
		grid.updateProcessColumnVisibility(activeStatus == null || activeStatus.isProcessable());
	}

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		labMessagesView.setApplyingCriteria(true);

		updateStatusButtons();

		labMessagesView.setApplyingCriteria(false);
	}

	public LabMessageGrid getGrid() {
		return grid;
	}

	public LabMessageCriteria getCriteria() {
		return criteria;
	}
}
