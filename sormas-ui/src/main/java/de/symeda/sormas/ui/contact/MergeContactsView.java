package de.symeda.sormas.ui.contact;

import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.MergeGuideLayout;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class MergeContactsView extends AbstractView {

	public static final String VIEW_NAME = ContactsView.VIEW_NAME + "/merge";

	private ContactCriteria criteria;

	private MergeContactsGrid grid;
	private MergeContactsFilterComponent filterComponent;

	public MergeContactsView() {
		super(VIEW_NAME);

		boolean criteriaUninitialized = !ViewModelProviders.of(MergeContactsView.class).has(ContactCriteria.class);

		criteria = ViewModelProviders.of(MergeContactsView.class).get(ContactCriteria.class);
		if (criteriaUninitialized) {
			criteria.creationDateFrom(DateHelper.subtractDays(new Date(), 30))
				.creationDateTo(new Date())
				.setRegion(UserProvider.getCurrent().getUser().getRegion());
		}

		grid = new MergeContactsGrid();
		grid.setCriteria(criteria);

		VerticalLayout gridLayout = new VerticalLayout();
		filterComponent = new MergeContactsFilterComponent(criteria);
		filterComponent.setFiltersUpdatedCallback(() -> {
			if (ViewModelProviders.of(MergeContactsView.class).has(ContactCriteria.class)) {
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

		Button btnOpenGuide = ButtonHelper.createIconButton(Captions.caseOpenMergeGuide, VaadinIcons.QUESTION, e -> buildAndOpenMergeInstructions());
		CssStyles.style(btnOpenGuide, CssStyles.HSPACE_LEFT_5);
		addHeaderComponent(btnOpenGuide);

		Button btnCalculateCompleteness =
			ButtonHelper.createIconButton(Captions.caseCalculateCompleteness, VaadinIcons.CALC, e -> showCalculateCompletenessWindow());

		addHeaderComponent(btnCalculateCompleteness);

		Button btnBack = ButtonHelper.createIconButton(
			Captions.contactBackToDirectory,
			VaadinIcons.ARROW_BACKWARD,
			e -> ControllerProvider.getContactController().navigateToIndex(),
			ValoTheme.BUTTON_PRIMARY);

		addHeaderComponent(btnBack);
	}

	private void buildAndOpenMergeInstructions() {
		Window window = VaadinUiUtil.showPopupWindow(new MergeGuideLayout());
		window.setWidth(1024, Unit.PIXELS);
		window.setCaption(I18nProperties.getString(Strings.headingMergeGuide));
	}

	private void showCalculateCompletenessWindow() {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingConfirmUpdateCompleteness),
			new Label(I18nProperties.getString(Strings.confirmationUpdateCompleteness)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			null,
			e -> {
				if (e.booleanValue() == true) {
					grid.calculateCompletenessValues();
					new Notification(
						I18nProperties.getString(Strings.headingCasesArchived),
						I18nProperties.getString(Strings.messageCompletenessValuesUpdated),
						Notification.Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				}
			});
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		grid.reload();
		filterComponent.updateDuplicateCountLabel(grid.getTreeData().getRootItems().size());
	}

}
