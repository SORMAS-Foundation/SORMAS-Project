package de.symeda.sormas.ui.caze;

import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class MergeCasesView extends AbstractView {

	public static final String VIEW_NAME = CasesView.VIEW_NAME + "/merge";

	private CaseCriteria criteria;

	private MergeCasesGrid grid;
	private MergeCasesFilterComponent filterComponent;
	
	public MergeCasesView() {
		super(VIEW_NAME);

		boolean criteriaUninitialized = !ViewModelProviders.of(MergeCasesView.class).has(CaseCriteria.class);

		criteria = ViewModelProviders.of(MergeCasesView.class).get(CaseCriteria.class);
		if (criteriaUninitialized) {
			criteria.creationDateFrom(DateHelper.subtractDays(new Date(), 30))
			.creationDateTo(new Date())
			.setRegion(UserProvider.getCurrent().getUser().getRegion());
		}

		grid = new MergeCasesGrid();
		grid.setCriteria(criteria);

		VerticalLayout gridLayout = new VerticalLayout();
		filterComponent = new MergeCasesFilterComponent(criteria);
		filterComponent.setFiltersUpdatedCallback(() -> {
			if (ViewModelProviders.of(MergeCasesView.class).has(CaseCriteria.class)) {
				grid.reload();
				filterComponent.updateDuplicateCountLabel(grid.getTreeData().getRootItems().size());
			} else {
				navigateTo(null);
			}
		});
		filterComponent.setIgnoreRegionCallback((ignoreRegion) -> {
			grid.reload(ignoreRegion);
		});
		gridLayout.addComponent(filterComponent);
		
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		addComponent(gridLayout);

		Button btnOpenGuide = new Button(I18nProperties.getCaption(Captions.caseOpenMergeGuide));
		btnOpenGuide.setId("openMergeGuide");
		btnOpenGuide.setIcon(VaadinIcons.QUESTION);
		btnOpenGuide.addClickListener(e -> buildAndOpenMergeInstructions());
		addHeaderComponent(btnOpenGuide);

		Button btnCalculateCompleteness = new Button(I18nProperties.getCaption(Captions.caseCalculateCompleteness));
		btnCalculateCompleteness.setId("calculateCompleteness");
		btnCalculateCompleteness.setIcon(VaadinIcons.CALC);
		btnCalculateCompleteness.addClickListener(e -> showCalculateCompletenessWindow());
		addHeaderComponent(btnCalculateCompleteness);

		Button btnBack = new Button(I18nProperties.getCaption(Captions.caseBackToDirectory));
		btnBack.setId("backToDirectory");
		btnBack.setIcon(VaadinIcons.ARROW_BACKWARD);
		btnBack.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnBack.addClickListener(e -> ControllerProvider.getCaseController().navigateToIndex());
		addHeaderComponent(btnBack);
	}

	private void buildAndOpenMergeInstructions() {
		Window window = VaadinUiUtil.showPopupWindow(new MergeGuideLayout());
		window.setWidth(1024, Unit.PIXELS);
		window.setCaption(I18nProperties.getString(Strings.headingMergeGuide));
	}

	private void showCalculateCompletenessWindow() {
		VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingConfirmUpdateCompleteness), 
				new Label(I18nProperties.getString(Strings.confirmationUpdateCompleteness)), 
				I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), null, e -> {
					if (e.booleanValue() == true) {
						grid.calculateCompletenessValues();
						new Notification(I18nProperties.getString(Strings.headingCasesArchived),
								I18nProperties.getString(Strings.messageCompletenessValuesUpdated), Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
					}
				});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		grid.reload();
		filterComponent.updateDuplicateCountLabel(grid.getTreeData().getRootItems().size());
	}

}
