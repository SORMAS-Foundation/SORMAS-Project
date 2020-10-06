package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.docgeneneration.TemplateCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.RowCount;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class TemplatesView extends AbstractConfigurationView {

    private static final long serialVersionUID = -3469830069266335042L;

    public static final String VIEW_NAME = ROOT_VIEW_NAME + "/templates";

    private TemplateCriteria criteria;
    private ViewConfiguration viewConfiguration;

    // Filter
    private TextField searchField;
    private Button resetButton;

    private HorizontalLayout filterLayout;
    private VerticalLayout gridLayout;
    private TemplatesGrid grid;
    protected Button createButton;
    protected Button importButton;
    private MenuBar bulkOperationsDropdown;

    public TemplatesView()
    {
        super(VIEW_NAME);

        viewConfiguration = ViewModelProviders.of(TemplatesView.class).get(ViewConfiguration.class);
        criteria = ViewModelProviders.of(TemplatesView.class).get(TemplateCriteria.class);

        grid = new TemplatesGrid(criteria);
        gridLayout = new VerticalLayout();
        gridLayout.addComponent(createFilterBar());
        gridLayout.addComponent(new RowCount(Strings.labelNumberOfTemplates, grid.getItemCount())); //!
        gridLayout.addComponent(grid);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(false);
        gridLayout.setExpandRatio(grid, 1);
        gridLayout.setSizeFull();
        gridLayout.setStyleName("crud-main-layout");

        if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_IMPORT)) {
            importButton = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
                Window window = VaadinUiUtil.showPopupWindow(new InfrastructureImportLayout(InfrastructureType.DISTRICT));
                window.setCaption("TBD");
                window.addCloseListener(c -> {
                    grid.reload();
                });
            }, ValoTheme.BUTTON_PRIMARY);

            addHeaderComponent(importButton);
        }

        //!
        if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
            Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
            btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
            addHeaderComponent(btnEnterBulkEditMode);

            Button btnLeaveBulkEditMode =
                    ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
            btnLeaveBulkEditMode.setId("leaveBulkEditMode");
            btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
            addHeaderComponent(btnLeaveBulkEditMode);

            btnEnterBulkEditMode.addClickListener(e -> {
                bulkOperationsDropdown.setVisible(true);
                viewConfiguration.setInEagerMode(true);
                btnEnterBulkEditMode.setVisible(false);
                btnLeaveBulkEditMode.setVisible(true);
                searchField.setEnabled(false);
                grid.setEagerDataProvider();
                grid.reload();
            });
            btnLeaveBulkEditMode.addClickListener(e -> {
                bulkOperationsDropdown.setVisible(false);
                viewConfiguration.setInEagerMode(false);
                btnLeaveBulkEditMode.setVisible(false);
                btnEnterBulkEditMode.setVisible(true);
                searchField.setEnabled(true);
                navigateTo(criteria);
            });
        }

        addComponent(gridLayout);

    }

    private HorizontalLayout createFilterBar() {

        filterLayout = new HorizontalLayout();
        filterLayout.setMargin(false);
        filterLayout.setSpacing(true);
        filterLayout.setWidth(100, Unit.PERCENTAGE);

        searchField = new TextField();
        searchField.setId("search");
        searchField.setWidth(200, Unit.PIXELS);
        searchField.setNullRepresentation("");
        searchField.setInputPrompt(I18nProperties.getString(Strings.promptSearch));
        searchField.addTextChangeListener(e -> {
            criteria.nameEpidLike(e.getText());
            navigateTo(criteria);
        });
        CssStyles.style(searchField, CssStyles.FORCE_CAPTION);
        filterLayout.addComponent(searchField);

        resetButton = ButtonHelper.createButton(Captions.actionResetFilters, event -> {
            ViewModelProviders.of(TemplatesView.class).remove(TemplateCriteria.class);
            navigateTo(null);
        }, CssStyles.FORCE_CAPTION);
        resetButton.setVisible(false);

        filterLayout.addComponent(resetButton);

        HorizontalLayout actionButtonsLayout = new HorizontalLayout();
        actionButtonsLayout.setSpacing(true);

        // Bulk operation dropdown
        // delete operation needs to be implemented
        if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
            bulkOperationsDropdown = MenuBarHelper.createDropDown(
                    Captions.bulkActions,
                    new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.actionDelete), VaadinIcons.TRASH, selectedItem -> {
                        ControllerProvider.getInfrastructureController()
                                .archiveOrDearchiveAllSelectedItems(
                                        true,
                                        grid.asMultiSelect().getSelectedItems(),
                                        InfrastructureType.DISTRICT,
                                        new Runnable() {
                                            public void run() {
                                                navigateTo(criteria);
                                            }
                                        });
                    }, true));

            bulkOperationsDropdown
                    .setVisible(viewConfiguration.isInEagerMode());
            actionButtonsLayout.addComponent(bulkOperationsDropdown);
        }
        filterLayout.addComponent(actionButtonsLayout);
        filterLayout.setComponentAlignment(actionButtonsLayout, Alignment.BOTTOM_RIGHT);
        filterLayout.setExpandRatio(actionButtonsLayout, 1);

        return filterLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        super.enter(event);
        String params = event.getParameters().trim();
        if (params.startsWith("?")) {
            params = params.substring(1);
            criteria.fromUrlParams(params);
        }
        updateFilterComponents();
        grid.reload();
    }

    public void updateFilterComponents() {

        // TODO replace with Vaadin 8 databinding
        applyingCriteria = true;

        resetButton.setVisible(criteria.hasAnyFilterActive());
        searchField.setValue(criteria.getNameEpidLike());

        applyingCriteria = false;
    }
}
