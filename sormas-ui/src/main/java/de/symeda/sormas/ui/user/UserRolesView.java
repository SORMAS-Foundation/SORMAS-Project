package de.symeda.sormas.ui.user;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.user.UserRoleCriteria;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;


public class UserRolesView extends AbstractUserView {
    private static final long serialVersionUID = -3533557348112305469L;

    public static final String VIEW_NAME = ROOT_VIEW_NAME + "/userroles";

    private UserRoleCriteria criteria;

    private UserRoleGrid grid;
    private Button btnExport;
    private Button createButton;

    private VerticalLayout gridLayout;

    public UserRolesView() {
        super(VIEW_NAME);

        criteria = ViewModelProviders.of(UserRolesView.class).get(UserRoleCriteria.class);

        grid = new UserRoleGrid();
      //  grid.setCriteria(criteria);
        gridLayout = new VerticalLayout();
      //  gridLayout.addComponent(createFilterBar());

        gridLayout.addComponent(grid);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(false);
        gridLayout.setSizeFull();
        gridLayout.setExpandRatio(grid, 1);
        gridLayout.setStyleName("crud-main-layout");

        addComponent(gridLayout);

      //  if (UserProvider.getCurrent().hasUserRight(UserRight.AGGREGATE_REPORT_EXPORT)) {
            btnExport = ButtonHelper.createIconButton(Captions.export, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_PRIMARY);
            addHeaderComponent(btnExport);

     //   }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        super.enter(event);
    }
}
