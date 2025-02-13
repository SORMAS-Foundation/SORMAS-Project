package de.symeda.sormas.ui.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vaadin.data.provider.Query;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.api.news.eios.NewsStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.ViewConfiguration;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class NewsView extends AbstractView {

	public static final String VIEW_NAME = "news";
	private final NewsGrid grid;
	private NewsCriteria newsCriteria;
	private NewsFilterForm filterForm;
	private MenuBar bulkOperationsDropdown;
	private Button btnEnterBulkEditMode;
	private Button btnLeaveBulkEditMode;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;
	private ViewConfiguration viewConfiguration;

	public NewsView() {
		super(VIEW_NAME);
		viewConfiguration = ViewModelProviders.of(NewsView.class).get(ViewConfiguration.class);
		newsCriteria = ViewModelProviders.of(NewsView.class).get(NewsCriteria.class);
		grid = new NewsGrid(newsCriteria);
		final VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);
		addComponent(gridLayout);

		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		if (UserProvider.getCurrent().hasUserRight(UserRight.EDIT_NEWS)) {
			final ExpandableButton createNew =
				new ExpandableButton(I18nProperties.getCaption(Captions.createNew)).expand(e -> ControllerProvider.getNewsController().create());
			addHeaderComponent(createNew);

			btnEnterBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, e -> enterBulkEditMode());
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
			addHeaderComponent(btnEnterBulkEditMode);

			btnLeaveBulkEditMode = ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, e -> leaveBulkEditMode());
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
			addHeaderComponent(btnLeaveBulkEditMode);

		}
	}

	private void enterBulkEditMode() {
		bulkOperationsDropdown.setVisible(true);
		ViewModelProviders.of(NewsView.class).get(ViewConfiguration.class).setInEagerMode(true);
		btnEnterBulkEditMode.setVisible(false);
		btnLeaveBulkEditMode.setVisible(true);
		((NewsGrid) grid).reload();
		((NewsGrid) grid).setBulkEditMode(true);
	}

	private void leaveBulkEditMode() {
		bulkOperationsDropdown.setVisible(false);
		ViewModelProviders.of(NewsView.class).get(ViewConfiguration.class).setInEagerMode(false);
		btnEnterBulkEditMode.setVisible(true);
		btnLeaveBulkEditMode.setVisible(false);
		navigateTo(newsCriteria);
		((NewsGrid) grid).setBulkEditMode(false);
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);
		statusButtons = new HashMap<>();
		Button riskLevelAll = ButtonHelper.createButton(Captions.all, e -> {
			newsCriteria.setRiskLevel(null);
			navigateTo(newsCriteria);
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		riskLevelAll.setCaptionAsHtml(true);
		statusFilterLayout.addComponent(riskLevelAll);
		statusButtons.put(riskLevelAll, I18nProperties.getCaption(Captions.all));
		activeStatusButton = riskLevelAll;
		for (RiskLevel riskLevel : RiskLevel.values()) {
			Button button = ButtonHelper.createButton(riskLevel.toString(), e -> {
				newsCriteria.setRiskLevel(riskLevel);
				navigateTo(newsCriteria);
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
			button.setCaptionAsHtml(true);
			button.setData(riskLevel);
			statusFilterLayout.addComponent(button);
			statusButtons.put(button, riskLevel.toString());
		}

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);

		final List<MenuBarHelper.MenuBarItem> menuBarItems = new ArrayList<>();
		menuBarItems.add(new MenuBarHelper.MenuBarItem(NewsStatus.APPROVED.toString(), VaadinIcons.FILE_ADD, mi -> {
			ControllerProvider.getNewsController().approveNews(grid.getSelectedItems(), grid);
		}));
		menuBarItems.add(new MenuBarHelper.MenuBarItem(NewsStatus.UNUSEFUL.toString(), VaadinIcons.FILE_REMOVE, mi -> {
			ControllerProvider.getNewsController().markAsUnUseful(grid.getSelectedItems(), grid);
		}));
		bulkOperationsDropdown = MenuBarHelper.createDropDown(Captions.bulkActions, menuBarItems);
		actionButtonsLayout.addComponent(bulkOperationsDropdown);
		bulkOperationsDropdown.setVisible(viewConfiguration.isInEagerMode());
		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);
		return statusFilterLayout;
	}

	public VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);
		filterForm = new NewsFilterForm();
		filterForm.setValue(newsCriteria);
		filterLayout.addComponent(filterForm);
		filterForm.addApplyHandler(clickEvent -> navigateTo(newsCriteria));
		filterForm.addResetHandler(clickEvent -> {
			ViewModelProviders.of(NewsView.class).remove(NewsCriteria.class);
			navigateTo(null);
		});
		return filterLayout;
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			newsCriteria.fromUrlParams(params);
		}
		if (viewConfiguration.isInEagerMode()) {
			((NewsGrid) grid).setBulkEditMode(true);
		}
		updateFilterComponents();
	}

	public void updateFilterComponents() {

		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;
		updateStatusButtons();
		filterForm.setValue(newsCriteria);
		applyingCriteria = false;
	}

	private void updateStatusButtons() {

		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
			if (b.getData() == newsCriteria.getRiskLevel()) {
				activeStatusButton = b;
			}
		});

		if (activeStatusButton != null) {
			CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
			if (activeStatusButton != null) {
				int dataSize = grid.getDataProvider().size(new Query<>());
				activeStatusButton.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(dataSize)));
			}
		}
	}

}
