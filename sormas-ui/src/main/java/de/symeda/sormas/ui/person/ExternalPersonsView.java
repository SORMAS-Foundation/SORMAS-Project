package de.symeda.sormas.ui.person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringEscapeUtils;
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
import com.vaadin.ui.renderers.HtmlRenderer;

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
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

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
	private SearchState state;

	public static class SearchState implements Serializable {

		private static final long serialVersionUID = 1L;
		private PersonalDataSearchRequest id = new PersonalDataSearchRequest();
		private PersonalDataSearchRequest criteria = new PersonalDataSearchRequest();
		private boolean expanded;
		private List<PersonalDataIndexDto> results = Collections.emptyList();
		private String message;
	}

	public ExternalPersonsView() {
		super(VIEW_NAME);
		if (!ExternalPersonDataComponent.isAvailable()) {
			return;
		}
		state = ViewModelProviders.of(ExternalPersonsView.class).get(SearchState.class);
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
		filters.setVisible(state.expanded);
		nationalHealthId.setVisible(!state.expanded);
		filters.setSpacing(true);
		Button toggle = new Button(I18nProperties.getCaption(state.expanded ? "externalPersonsSearchByHealthId" : "externalPersonsShowMoreFilters"));
		toggle.addClickListener(event -> {
			filters.setVisible(!filters.isVisible());
			nationalHealthId.setVisible(!filters.isVisible());
			toggle.setCaption(I18nProperties.getCaption(filters.isVisible() ? "externalPersonsSearchByHealthId" : "externalPersonsShowMoreFilters"));
			state.expanded = filters.isVisible();
			clearResults();
		});
		Button search = new Button(I18nProperties.getCaption(Captions.actionSearch), event -> search());
		Button reset = new Button(I18nProperties.getCaption(Captions.actionResetFilters), event -> {
			state.id = new PersonalDataSearchRequest();
			state.criteria = new PersonalDataSearchRequest();
			idBinder.setBean(state.id);
			filtersBinder.setBean(state.criteria);
			clearResults();
		});
		addLinkColumn(PersonalDataIndexDto::getNationalHealthId).setCaption(caption("nationalHealthId"));
		addLinkColumn(PersonalDataIndexDto::getLastName).setCaption(caption("lastName"));
		addLinkColumn(PersonalDataIndexDto::getFirstName).setCaption(caption("firstName"));
		addLinkColumn(PersonalDataIndexDto::getSex).setCaption(caption("sex"));
		addLinkColumn(PersonalDataIndexDto::getBirthDate).setCaption(caption("birthDate"));
		addLinkColumn(PersonalDataIndexDto::getMunicipality).setCaption(caption("municipalityOfResidence"));
		addLinkColumn(PersonalDataIndexDto::getLivingStatus).setCaption(caption("livingStatus"));
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.addStyleName("external-persons-grid");
		grid.setSizeFull();
		VerticalLayout content = new VerticalLayout(nationalHealthId, toggle, filters, new HorizontalLayout(search, reset), message, grid);
		content.setSizeFull();
		content.setExpandRatio(grid, 1);
		addComponent(content);
		idBinder.setBean(state.id);
		filtersBinder.setBean(state.criteria);
		grid.setItems(state.results);
		setMessage(state.message == null ? I18nProperties.getString("externalPersonsEnterCriteria") : state.message);
	}

	private Grid.Column<PersonalDataIndexDto, String> addLinkColumn(Function<PersonalDataIndexDto, ?> valueProvider) {
		return grid.addColumn(person -> {
			Object value = valueProvider.apply(person);
			String text = StringEscapeUtils.escapeHtml4(value == null ? "" : value.toString());
			if (StringUtils.isBlank(person.getNationalHealthId())) {
				return text;
			}
			String route = ExternalPersonView.VIEW_NAME + "/" + person.getNationalHealthId();
			return "<a href=\"#!" + StringEscapeUtils.escapeHtml4(route) + "\">" + (text.isEmpty() ? "&nbsp;" : text) + "</a>";
		}, new HtmlRenderer());
	}

	private void setMessage(String value) {
		state.message = value;
		message.setValue(value);
		if (I18nProperties.getString(Strings.errorExternalPersonDataLoad).equals(value)) {
			message.addStyleName(CssStyles.ERROR_COLOR_PRIMARY);
		} else {
			message.removeStyleName(CssStyles.ERROR_COLOR_PRIMARY);
		}
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
		state.results = Collections.emptyList();
		grid.setItems(state.results);
		setMessage(I18nProperties.getString("externalPersonsEnterCriteria"));
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
			return;
		}
		if (!validRange(request.getBirthYearFrom(), request.getBirthYearTo()) || !validRange(request.getAgeFrom(), request.getAgeTo())) {
			Notification.show(I18nProperties.getString("externalPersonsInvalidRange"), Notification.Type.WARNING_MESSAGE);
			return;
		}
		try {
			PersonalDataSearchResponse response = FacadeProvider.getPersonalDataProviderFacade().search(request);
			List<PersonalDataIndexDto> results = response.getResults() == null ? Collections.emptyList() : response.getResults();
			state.results = new ArrayList<>(results.subList(0, Math.min(100, results.size())));
			grid.setItems(state.results);
			setMessage(results.isEmpty() ? I18nProperties.getString(Strings.infoNoExternalPersonData) : "");
			boolean truncated = results.size() > 100 || response.getTotalCount() != null && response.getTotalCount() > 100;
			if (truncated) {
				setMessage(I18nProperties.getString("externalPersonsFirst100Results"));
				Notification.show(message.getValue(), Notification.Type.WARNING_MESSAGE);
			}
			if (results.size() == 1 && (response.getTotalCount() == null || response.getTotalCount() == 1)) {
				open(results.get(0));
			}
		} catch (RuntimeException e) {
			logger.error("Could not search external persons", e);
			setMessage(I18nProperties.getString(Strings.errorExternalPersonDataLoad));
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
