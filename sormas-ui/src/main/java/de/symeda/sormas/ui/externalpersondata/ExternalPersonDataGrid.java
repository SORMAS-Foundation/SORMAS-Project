package de.symeda.sormas.ui.externalpersondata;

import java.util.function.Consumer;
import java.util.function.Function;

import com.vaadin.ui.Grid;

import de.symeda.sormas.api.externalpersondata.ExternalPersonDataIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

public class ExternalPersonDataGrid extends Grid<ExternalPersonDataIndexDto> {

	private static final long serialVersionUID = 1L;

	public ExternalPersonDataGrid(Consumer<ExternalPersonDataIndexDto> openPerson) {
		initColumns();
		setSelectionMode(SelectionMode.NONE);
		addItemClickListener(event -> openPerson.accept(event.getItem()));
		addStyleName("external-persons-grid");
		setSizeFull();
	}

	private void initColumns() {
		addTextColumn(ExternalPersonDataIndexDto::getNationalHealthId, Captions.ExternalPersons_nationalHealthId);
		addTextColumn(ExternalPersonDataIndexDto::getLastName, Captions.ExternalPersons_lastName);
		addTextColumn(ExternalPersonDataIndexDto::getFirstName, Captions.ExternalPersons_firstName);
		addTextColumn(ExternalPersonDataIndexDto::getSex, Captions.ExternalPersons_sex);
		addTextColumn(ExternalPersonDataIndexDto::getBirthDate, Captions.ExternalPersons_birthDate);
		addTextColumn(ExternalPersonDataIndexDto::getMunicipality, Captions.ExternalPersons_municipalityOfResidence);
		addTextColumn(ExternalPersonDataIndexDto::getLivingStatus, Captions.ExternalPersons_livingStatus);
	}

	private void addTextColumn(Function<ExternalPersonDataIndexDto, ?> valueProvider, String captionKey) {
		addColumn(person -> {
			Object value = valueProvider.apply(person);
			return value == null ? "" : value.toString();
		}).setCaption(I18nProperties.getCaption(captionKey));
	}
}
