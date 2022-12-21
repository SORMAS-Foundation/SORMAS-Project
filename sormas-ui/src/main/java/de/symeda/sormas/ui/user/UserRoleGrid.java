package de.symeda.sormas.ui.user;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRoleCriteria;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class UserRoleGrid extends FilteredGrid<UserRoleDto, UserRoleCriteria> {

	public UserRoleGrid() {
		super(UserRoleDto.class);
		setSizeFull();
		setLazyDataProvider();

		setColumns(UserRoleDto.UUID, UserRoleDto.CAPTION, UserRoleDto.JURISDICTION_LEVEL, UserRoleDto.DESCRIPTION);

		((Column<UserRoleDto, String>) getColumn(UserRoleDto.UUID)).setRenderer(new UuidRenderer());
		addItemClickListener(new ShowDetailsListener<>(UserRoleDto.UUID, e -> ControllerProvider.getUserRoleController().editData(e.getUuid())));

		for (Column<?, ?> column : getColumns()) {
			if (column.getId().equals(UserRoleDto.DESCRIPTION)) {
				column.setMaximumWidth(700);
			}
			if (column.getId().equals(UserRoleDto.CAPTION)) {
				column.setCaption(I18nProperties.getCaption(Captions.UserRole));
			} else {
				column.setCaption(I18nProperties.getPrefixCaption(UserRoleDto.I18N_PREFIX, column.getId(), column.getCaption()));
			}
		}
	}

	public void setLazyDataProvider() {

		setLazyDataProvider(FacadeProvider.getUserRoleFacade()::getIndexList, FacadeProvider.getUserRoleFacade()::count);
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

}
