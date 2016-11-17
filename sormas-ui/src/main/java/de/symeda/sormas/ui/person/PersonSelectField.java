package de.symeda.sormas.ui.person;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.person.CasePersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;

@SuppressWarnings("serial")
public class PersonSelectField extends CustomField<PersonReferenceDto> {

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String CREATE_PERSON = "createPerson";
	
	private final TextField firstNameField = new TextField();
	private final TextField lastNameField = new TextField();
	private PersonGrid personGrid;
	private OptionGroup createNewPerson;
	
	@Override
	protected Component initContent() {
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setSizeUndefined();
		layout.setWidth(100, Unit.PERCENTAGE);
		
		HorizontalLayout nameLayout = new HorizontalLayout();
		nameLayout.setSpacing(true);
		nameLayout.setWidth(100, Unit.PERCENTAGE);
		
		firstNameField.setCaption(
				I18nProperties.getFieldCaption(CasePersonDto.I18N_PREFIX, CasePersonDto.FIRST_NAME, CasePersonDto.FIRST_NAME));
		firstNameField.setWidth(100, Unit.PERCENTAGE);
		firstNameField.addValueChangeListener(e -> {
			personGrid.setFirstNameFilter((String)e.getProperty().getValue());	
			selectBestMatch();
		});
		nameLayout.addComponent(firstNameField);
		
		lastNameField.setCaption(
				I18nProperties.getFieldCaption(CasePersonDto.I18N_PREFIX, CasePersonDto.LAST_NAME, CasePersonDto.LAST_NAME));
		lastNameField.setWidth(100, Unit.PERCENTAGE);
		lastNameField.addValueChangeListener(e -> {
			personGrid.setLastNameFilter((String)e.getProperty().getValue());
			selectBestMatch();
		});
		nameLayout.addComponent(lastNameField);

		layout.addComponent(nameLayout);
		
		initPersonGrid();
		// unselect "create new" when person is selected
		personGrid.addSelectionListener(e -> {
			if (e.getSelected().size() > 0) {
				createNewPerson.setValue(null);
			}
		});
		layout.addComponent(personGrid);
		
		createNewPerson = new OptionGroup(null);
		createNewPerson.addItem(CREATE_PERSON);
		createNewPerson.setItemCaption(CREATE_PERSON, I18nProperties.getFieldCaption(CasePersonDto.I18N_PREFIX, "createNew", "createNew"));
		// unselect grid when "create new" is selected
		createNewPerson.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				personGrid.select(null);
			}
		});
		layout.addComponent(createNewPerson);
		
		// set field values based on internal value
		setInternalValue(super.getInternalValue());
		
		return layout;
	}
	
	private void initPersonGrid() {
		if (personGrid == null) {
			personGrid = new PersonGrid();
			personGrid.setHeightByRows(6);
			personGrid.setCaption(I18nProperties.getFieldCaption(CasePersonDto.I18N_PREFIX, "select", "select"));
			personGrid.setSelectionMode(SelectionMode.SINGLE);
			personGrid.setFirstNameFilter(firstNameField.getValue());
			personGrid.setLastNameFilter(lastNameField.getValue());
		}
	}
	
	public void selectBestMatch() {
		if (personGrid.getContainerDataSource().size() == 1) {
			setInternalValue((PersonReferenceDto)personGrid.getContainerDataSource().firstItemId());
		}
		else {
			setInternalValue(null);
		}
	}
	
	@Override
	public Class<? extends PersonReferenceDto> getType() {
		return PersonReferenceDto.class;
	}
	
	@Override
	protected void setInternalValue(PersonReferenceDto newValue) {
		super.setInternalValue(newValue);
		
		if (newValue == null) {
			if (createNewPerson != null) {
				createNewPerson.setValue(CREATE_PERSON);
			}
		}
		else {
			if (personGrid != null) {
				personGrid.select(newValue);
			}
		}
	}
	
	@Override
	protected PersonReferenceDto getInternalValue() {
		
		if (personGrid != null) {
			PersonReferenceDto value = (PersonReferenceDto)personGrid.getSelectedRow();
			return value;
		}
		
		return super.getInternalValue();
	}

	public String getFirstName() {
		return firstNameField.getValue();
	}
	
	public void setFirstName(String firstName) {
		firstNameField.setValue(firstName);
	}

	public String getLastName() {
		return lastNameField.getValue();
	}
	
	public void setLastName(String lastName) {
		lastNameField.setValue(lastName);
	}
	
	public boolean hasMatches() {
		if (personGrid == null) {
			initPersonGrid();
		}
		return personGrid.getContainerDataSource().size() > 0;
	}
}