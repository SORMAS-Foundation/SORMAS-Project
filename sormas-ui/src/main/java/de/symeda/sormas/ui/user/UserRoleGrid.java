package de.symeda.sormas.ui.user;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRoleCriteria;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class UserRoleGrid extends FilteredGrid<UserRoleDto, UserRoleCriteria> {

	public UserRoleGrid() {
		super(UserRoleDto.class);
        setSizeFull();

        setColumns(
                UserRoleDto.USER_RIGHTS,
                UserRoleDto.JURISDICTION_LEVEL,
                UserRoleDto.DESCRIPTION);

        for (Column<?, ?> column : getColumns()) {
            column.setCaption(I18nProperties.getPrefixCaption(UserDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
        }
	}



	//setLazyDataProvider();

	//setCriteria(getCriteria());

	//setColumns(UserRoleDto.CAPTION, UserRoleDto.JURISDICTION_LEVEL, UserRoleDto.DESCRIPTION, UserRoleDto.ENABLED);
}
