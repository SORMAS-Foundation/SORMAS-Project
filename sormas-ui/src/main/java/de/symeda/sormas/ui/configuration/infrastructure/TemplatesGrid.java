package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.stream.Collectors;

import com.vaadin.ui.Grid;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.DistrictCriteria;
import de.symeda.sormas.api.region.DistrictIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ViewConfiguration;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.api.docgeneneration.TemplateCriteria;
import de.symeda.sormas.api.docgeneneration.TemplateDto;



public class TemplatesGrid extends FilteredGrid<TemplateDto, TemplateCriteria>{
    private static final long serialVersionUID = -4437531616784715458L;

    public TemplatesGrid(TemplateCriteria criteria)
    {
        super(TemplateDto.class);
        setSizeFull();

        ViewConfiguration viewConfiguration = ViewModelProviders.of(TemplatesView.class).get(ViewConfiguration.class);
        setInEagerMode(viewConfiguration.isInEagerMode());

        if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
            setCriteria(criteria);
            setEagerDataProvider();
        } else {
            setLazyDataProvider();
            setCriteria(criteria);
        }

        setColumns(TemplateDto.NAME);

        /*
        if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
            addEditColumn(e -> ControllerProvider.getInfrastructureController().editDistrict(e.getUuid()));
        }
         */

        for (Grid.Column<?, ?> column : getColumns()) {
            column.setCaption(I18nProperties.getPrefixCaption(TemplateDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
        }
    }

    public void reload() {
        getDataProvider().refreshAll();
    }


    public void setLazyDataProvider() {

        DataProvider<TemplateDto, TemplateCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
                query -> FacadeProvider.getQuarantineOrderFacade().getAvailableTemplateDtos().stream(),
                query -> {
                    return (int) FacadeProvider.getQuarantineOrderFacade().count(query.getFilter().orElse(null));
                });
        setDataProvider(dataProvider);
        setSelectionMode(SelectionMode.NONE);
    }

    public void setEagerDataProvider() {

        ListDataProvider<TemplateDto> dataProvider =
                DataProvider.fromStream(FacadeProvider.getQuarantineOrderFacade().getAvailableTemplateDtos().stream());
        setDataProvider(dataProvider);
        setSelectionMode(SelectionMode.MULTI);
    }
}
