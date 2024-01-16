package de.symeda.sormas.ui.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleCriteria;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;

public class UserRolesView extends AbstractUserView {

	private static final long serialVersionUID = -3533557348112305469L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/userroles";

	public static final String ENABLED_FILTER = I18nProperties.getString(Strings.enabled);
	public static final String DISABLED_FILTER = I18nProperties.getString(Strings.disabled);
	public static final String ALL_FILTER = I18nProperties.getString(Strings.all);

	private UserRoleCriteria criteria;

	private UserRoleGrid grid;
	private Button createButton;

	private ComboBox userRightsFilter;
	private ComboBox jurisdictionFilter;
	private ComboBox enabledFilter;
	private CheckBox showOnlyRestrictedAccessToAssignedEntities;

	private VerticalLayout gridLayout;

	public UserRolesView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(UserRolesView.class).get(UserRoleCriteria.class);

		grid = new UserRoleGrid();
		grid.setCriteria(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		Button exportUserRightsButton = ButtonHelper.createIconButton(Captions.exportUserRoles, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_PRIMARY);

		new FileDownloader(new StreamResource(() -> new DownloadUtil.DelayedInputStream((out) -> {
			try {
				String documentPath = FacadeProvider.getUserRoleFacade().generateUserRolesDocument();
				IOUtils.copy(Files.newInputStream(new File(documentPath).toPath()), out);
			} catch (IOException e) {
				LoggerFactory.getLogger(DownloadUtil.class).error(e.getMessage(), e);
				new Notification(
					I18nProperties.getString(Strings.headingExportUserRightsFailed),
					I18nProperties.getString(Strings.messageUserRightsExportFailed),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}
		}, (e) -> {
		}), createFileNameWithCurrentDate(ExportEntityName.USER_ROLES, ".xlsx"))).extend(exportUserRightsButton);

		addHeaderComponent(exportUserRightsButton);

		if (UserProvider.getCurrent().hasUserRight(UserRight.USER_ROLE_EDIT)) {
			createButton = ButtonHelper.createIconButton(
				Captions.userRoleNewUserRole,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getUserRoleController().create(),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);

		enabledFilter = ComboBoxHelper.createComboBoxV7();
		enabledFilter.setId(UserRoleDto.ENABLED);
		enabledFilter.setWidth(200, Unit.PIXELS);
		enabledFilter.setInputPrompt(I18nProperties.getPrefixCaption(UserRoleDto.I18N_PREFIX, UserRoleDto.ENABLED));
		enabledFilter.addItems(ALL_FILTER, ENABLED_FILTER, DISABLED_FILTER);
		enabledFilter.addValueChangeListener(e -> {
			criteria.enabled(
				ENABLED_FILTER.equals(e.getProperty().getValue())
					? Boolean.TRUE
					: DISABLED_FILTER.equals(e.getProperty().getValue()) ? Boolean.FALSE : null);
			navigateTo(criteria);
		});
		actionButtonsLayout.addComponent(enabledFilter);
		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return statusFilterLayout;
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		userRightsFilter = ComboBoxHelper.createComboBoxV7();
		userRightsFilter.setId(UserRoleDto.USER_RIGHTS);
		userRightsFilter.setWidth(200, Unit.PIXELS);
		userRightsFilter.setInputPrompt(I18nProperties.getPrefixCaption(UserRoleDto.I18N_PREFIX, UserRoleDto.USER_RIGHTS));
		userRightsFilter.addItems((Object[]) UserRight.values());
		userRightsFilter.addValueChangeListener(e -> {
			criteria.userRight((UserRight) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(userRightsFilter);

		jurisdictionFilter = ComboBoxHelper.createComboBoxV7();
		jurisdictionFilter.setId(UserRoleDto.JURISDICTION_LEVEL);
		jurisdictionFilter.setWidth(200, Unit.PIXELS);
		jurisdictionFilter.setInputPrompt(I18nProperties.getPrefixCaption(UserRoleDto.I18N_PREFIX, UserRoleDto.JURISDICTION_LEVEL));
		jurisdictionFilter.addItems((Object[]) JurisdictionLevel.values());
		jurisdictionFilter.addValueChangeListener(e -> {
			criteria.jurisdictionLevel((JurisdictionLevel) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(jurisdictionFilter);

		showOnlyRestrictedAccessToAssignedEntities = new CheckBox();
		showOnlyRestrictedAccessToAssignedEntities.setId(UserRoleDto.RESTRICT_ACCESS_TO_ASSIGNED_ENTITIES);
		showOnlyRestrictedAccessToAssignedEntities.setCaption(I18nProperties.getCaption(Captions.userRoleShowOnlyRestrictedAccessToAssignCases));
		showOnlyRestrictedAccessToAssignedEntities.addStyleName(CssStyles.CHECKBOX_FILTER_INLINE);
		showOnlyRestrictedAccessToAssignedEntities.addValueChangeListener(e -> {
			criteria.setShowOnlyRestrictedAccessToAssignedEntities(e.getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(showOnlyRestrictedAccessToAssignedEntities);

		return filterLayout;
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		if (event != null) {
			String params = event.getParameters().trim();
			if (params.startsWith("?")) {
				params = params.substring(1);
				criteria.fromUrlParams(params);
			}
			updateFilterComponents();
		}
		grid.reload();
		super.enter(event);
	}

	public void updateFilterComponents() {

		applyingCriteria = true;

		jurisdictionFilter.setValue(criteria.getJurisdictionLevel() == null ? null : criteria.getJurisdictionLevel());
		enabledFilter.setValue(criteria.getEnabled() == null ? ALL_FILTER : criteria.getEnabled() ? ENABLED_FILTER : DISABLED_FILTER);
		userRightsFilter.setValue(criteria.getUserRight() == null ? null : criteria.getUserRight());
		showOnlyRestrictedAccessToAssignedEntities.setValue(
			criteria.getShowOnlyRestrictedAccessToAssignedEntities() == null
				? Boolean.FALSE
				: criteria.getShowOnlyRestrictedAccessToAssignedEntities());

		applyingCriteria = false;
	}

}
