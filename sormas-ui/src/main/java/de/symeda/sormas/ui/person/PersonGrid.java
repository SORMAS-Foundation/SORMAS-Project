package de.symeda.sormas.ui.person;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.login.LoginHelper;

@SuppressWarnings("serial")
public class PersonGrid extends Grid {

	public static final String LAST_DISEASE_LOC = "lastDiseaseLoc";
	
	private final List<PersonNameDto> persons;

	public PersonGrid(String firstName, String lastName) {
		persons = FacadeProvider.getPersonFacade().getNameDtos(LoginHelper.getCurrentUserAsReference());
		
		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		BeanItemContainer<PersonIndexDto> container = new BeanItemContainer<PersonIndexDto>(PersonIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		generatedContainer.addGeneratedProperty(LAST_DISEASE_LOC, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				PersonIndexDto person = (PersonIndexDto) itemId;
				if (person.getLastDisease() != null) {
					return "<a href='" + Page.getCurrent().getLocation() + "/data/" + 
							person.getLastCaseUuid() + "' target='_blank'>" + person.getLastDisease().toShortString() + 
							" (" + DateHelper.formatLocalShortDate(person.getLastDiseaseStartDate()) + ")</a>";
				} else {
					return "";
				}
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});

		setColumns(PersonIndexDto.FIRST_NAME, PersonIndexDto.LAST_NAME, PersonIndexDto.NICKNAME, 
				PersonIndexDto.APPROXIMATE_AGE, PersonIndexDto.SEX, PersonIndexDto.PRESENT_CONDITION,
				PersonIndexDto.DISTRICT_NAME, PersonIndexDto.COMMUNITY_NAME, PersonIndexDto.CITY,
				LAST_DISEASE_LOC);

		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
					PersonIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}

		getColumn(PersonIndexDto.FIRST_NAME).setMinimumWidth(150);
		getColumn(PersonIndexDto.LAST_NAME).setMinimumWidth(150);
		getColumn(LAST_DISEASE_LOC).setRenderer(new HtmlRenderer());

		reload(firstName, lastName);
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<PersonIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<PersonIndexDto>) container.getWrappedContainer();
	}

	public void reload(String firstName, String lastName) {
		List<PersonIndexDto> entries = new ArrayList<>();
		for (PersonNameDto person : persons) {
			if (PersonHelper.areNamesSimilar(firstName + " " + lastName, person.getFirstName() + " " + person.getLastName())) {
				PersonIndexDto indexDto = FacadeProvider.getPersonFacade().getIndexDto(person.getId());
				CaseDataDto lastCase = FacadeProvider.getCaseFacade().getLatestCaseByPerson(indexDto.getUuid(), LoginHelper.getCurrentUserAsReference().getUuid());
				if (lastCase != null) {
					indexDto.setLastDisease(lastCase.getDisease());
					indexDto.setLastDiseaseStartDate(CaseLogic.getStartDate(lastCase.getSymptoms().getOnsetDate(), lastCase.getReceptionDate(), lastCase.getReportDate()));
					indexDto.setLastCaseUuid(lastCase.getUuid());
				}
				entries.add(indexDto);
			}
		}

		getContainer().removeAllItems();
		getContainer().addAll(entries);    
		setHeightByRows(entries.size() > 0 ? (entries.size() <= 10 ? entries.size() : 10) : 1);
	}

	public void refresh(PersonIndexDto entry) {
		// We avoid updating the whole table through the backend here so we can
		// get a partial update for the grid
		BeanItem<PersonIndexDto> item = getContainer().getItem(entry);
		if (item != null) {
			// Updated product
			@SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(PersonIndexDto.UUID);
			p.fireValueChange();
		} else {
			// New product
			getContainer().addBean(entry);
		}
	}

	public void remove(PersonIndexDto entry) {
		getContainer().removeItem(entry);
	}
}


