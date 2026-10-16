package de.symeda.sormas.ui.person;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.personaldata.PersonalDataIndexDto;
import de.symeda.sormas.api.personaldata.PersonalDataSearchRequest;
import de.symeda.sormas.api.personaldata.PersonalDataSearchResponse;
import de.symeda.sormas.api.utils.luxembourg.LuxembourgNationalHealthIdValidator;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.AbstractView;

public class ExternalPersonsView extends AbstractView {

	public static final String VIEW_NAME = "externalPersons";
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ExternalPersonsView.class);
	private final Binder<PersonalDataSearchRequest> idBinder = new Binder<>(PersonalDataSearchRequest.class);
	private final Binder<PersonalDataSearchRequest> filtersBinder = new Binder<>(PersonalDataSearchRequest.class);
	private final TextField nationalHealthId = new TextField(caption("nationalHealthId"));
	private final GridLayout filters = new GridLayout(4, 4);
	private final Grid<PersonalDataIndexDto> grid = new Grid<>();
	private final Label message = new Label();

	public ExternalPersonsView() {
		super(VIEW_NAME);
		if (!ExternalPersonDataComponent.isAvailable()) {
			return;
		}
		idBinder.forField(nationalHealthId)
			.withValidator(
				value -> StringUtils.isBlank(value) || !LuxembourgNationalHealthIdValidator.isValidWithCause(value.trim()).isPresent(),
				I18nProperties.getValidationError(Validations.invalidNationalHealthId))
			.withConverter(StringUtils::trimToNull, value -> value == null ? "" : value)
			.bind("nationalHealthId");
		textFilter("lastName");
		textFilter("firstName");
		ComboBox<Sex> sex = new ComboBox<>(caption("sex"));
		sex.setItems(Sex.values());
		filtersBinder.bind(sex, "sex");
		filters.addComponent(sex);
		DateField birthDate = new DateField(caption("birthDate"));
		filtersBinder.bind(birthDate, "birthDate");
		filters.addComponent(birthDate);
		textFilter("countryOfResidence");
		textFilter("localityOfResidence");
		textFilter("municipalityOfResidence");
		integerFilter("birthYearFrom");
		integerFilter("birthYearTo");
		integerFilter("ageFrom");
		integerFilter("ageTo");
		ComboBox<Boolean> alive = new ComboBox<>(caption("alive"));
		alive.setItems(Boolean.TRUE, Boolean.FALSE);
		alive.setItemCaptionGenerator(value -> I18nProperties.getString(value ? Strings.yes : Strings.no));
		filtersBinder.bind(alive, "alive");
		filters.addComponent(alive);
		filters.setVisible(false);
		filters.setSpacing(true);
		Button toggle = new Button(I18nProperties.getCaption("externalPersonsShowMoreFilters"));
		toggle.addClickListener(event -> {
			filters.setVisible(!filters.isVisible());
			nationalHealthId.setVisible(!filters.isVisible());
			toggle.setCaption(I18nProperties.getCaption(filters.isVisible() ? "externalPersonsSearchByHealthId" : "externalPersonsShowMoreFilters"));
			clearResults();
		});
		Button search = new Button(I18nProperties.getCaption(Captions.actionSearch), event -> search());
		Button reset = new Button(I18nProperties.getCaption(Captions.actionResetFilters), event -> {
			idBinder.readBean(new PersonalDataSearchRequest());
			filtersBinder.readBean(new PersonalDataSearchRequest());
			clearResults();
		});
		grid.addColumn(PersonalDataIndexDto::getNationalHealthId).setCaption(caption("nationalHealthId"));
		grid.addColumn(PersonalDataIndexDto::getLastName).setCaption(caption("lastName"));
		grid.addColumn(PersonalDataIndexDto::getFirstName).setCaption(caption("firstName"));
		grid.addColumn(PersonalDataIndexDto::getSex).setCaption(caption("sex"));
		grid.addColumn(PersonalDataIndexDto::getBirthDate).setCaption(caption("birthDate"));
		grid.addColumn(PersonalDataIndexDto::getMunicipality).setCaption(caption("municipalityOfResidence"));
		grid.addColumn(PersonalDataIndexDto::getLivingStatus).setCaption(caption("livingStatus"));
		grid.addItemClickListener(event -> open(event.getItem()));
		grid.setSizeFull();
		VerticalLayout content = new VerticalLayout(nationalHealthId, toggle, filters, new HorizontalLayout(search, reset), message, grid);
		content.setSizeFull();
		content.setExpandRatio(grid, 1);
		addComponent(content);
		clearResults();
	}

	private static String caption(String property) {
		return I18nProperties.getPrefixCaption("ExternalPersons", property);
	}

	private void textFilter(String property) {
		TextField field = new TextField(caption(property));
		filtersBinder.forField(field).withConverter(StringUtils::trimToNull, value -> value == null ? "" : value).bind(property);
		filters.addComponent(field);
	}

	private void integerFilter(String property) {
		TextField field = new TextField(caption(property));
		filtersBinder.forField(field)
			.withNullRepresentation("")
			.withConverter(new StringToIntegerConverter(I18nProperties.getString("externalPersonsInvalidRange")))
			.withValidator(value -> value == null || value >= 0, I18nProperties.getString("externalPersonsInvalidRange"))
			.bind(property);
		filters.addComponent(field);
	}

	private void clearResults() {
		grid.setItems(Collections.emptyList());
		message.setValue(I18nProperties.getString("externalPersonsEnterCriteria"));
	}

	private void search() {
		if (!ExternalPersonDataComponent.isAvailable()) {
			clearResults();
			return;
		}
		PersonalDataSearchRequest request = new PersonalDataSearchRequest();
		if (!(filters.isVisible() ? filtersBinder : idBinder).writeBeanIfValid(request)) {
			return;
		}
		if (request.equals(new PersonalDataSearchRequest())) {
			clearResults();
			return;
		}
		if (!validRange(request.getBirthYearFrom(), request.getBirthYearTo()) || !validRange(request.getAgeFrom(), request.getAgeTo())) {
			Notification.show(I18nProperties.getString("externalPersonsInvalidRange"), Notification.Type.WARNING_MESSAGE);
			return;
		}
		try {
			PersonalDataSearchResponse response = FacadeProvider.getPersonalDataProviderFacade().search(request);
			List<PersonalDataIndexDto> results = response.getResults() == null ? Collections.emptyList() : response.getResults();
			grid.setItems(results.subList(0, Math.min(100, results.size())));
			message.setValue(results.isEmpty() ? I18nProperties.getString(Strings.infoNoExternalPersonData) : "");
			boolean truncated = results.size() > 100 || response.getTotalCount() != null && response.getTotalCount() > 100;
			if (truncated) {
				message.setValue(I18nProperties.getString("externalPersonsFirst100Results"));
				Notification.show(message.getValue(), Notification.Type.WARNING_MESSAGE);
			}
			if (results.size() == 1 && (response.getTotalCount() == null || response.getTotalCount() == 1)) {
				open(results.get(0));
			}
		} catch (RuntimeException e) {
			logger.error("Could not search external persons", e);
			grid.setItems(Collections.emptyList());
			message.setValue(I18nProperties.getString(Strings.errorExternalPersonDataLoad));
		}
	}

	private static boolean validRange(Integer from, Integer to) {
		return from == null || to == null || from <= to;
	}

	private void open(PersonalDataIndexDto person) {
		if (StringUtils.isNotBlank(person.getNationalHealthId())) {
			SormasUI.get().getNavigator().navigateTo(ExternalPersonView.VIEW_NAME + "/" + person.getNationalHealthId());
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
	}
}
