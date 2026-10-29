package de.symeda.sormas.ui.externalperson;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
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
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.personaldata.PersonalDataIndexDto;
import de.symeda.sormas.api.personaldata.PersonalDataSearchRequest;
import de.symeda.sormas.api.personaldata.PersonalDataSearchResponse;
import de.symeda.sormas.api.utils.luxembourg.LuxembourgNationalHealthIdValidator;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.person.ExternalPersonDataComponent;
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
	private final Consumer<PersonalDataIndexDto> selection;
	private final boolean singlePersonMode;

	public static class SearchState implements Serializable {

		private static final long serialVersionUID = 1L;
		private PersonalDataSearchRequest id = new PersonalDataSearchRequest();
		private PersonalDataSearchRequest criteria = new PersonalDataSearchRequest();
		private boolean expanded;
		private List<PersonalDataIndexDto> results = Collections.emptyList();
		private String message;
	}

	public ExternalPersonsView() {
		this(null, null);
	}

	public ExternalPersonsView(PersonDto person, Consumer<PersonalDataIndexDto> selection) {
		super(VIEW_NAME);
		this.selection = selection;

		this.singlePersonMode = selection != null && person != null;

		if (!ExternalPersonDataComponent.isAvailable()) {
			return;
		}
		state = person == null ? ViewModelProviders.of(ExternalPersonsView.class).get(SearchState.class) : new SearchState();
		if (person != null) {
			state.expanded = true;
			state.criteria = personCriteria(person, false);
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
		filters.setVisible(state.expanded);
		nationalHealthId.setVisible(!state.expanded);
		filters.setSpacing(true);
		Button searchModeToggle =
			new Button(I18nProperties.getCaption(state.expanded ? "externalPersonsSearchByHealthId" : "externalPersonsShowMoreFilters"));
		searchModeToggle.addClickListener(event -> {
			filters.setVisible(!filters.isVisible());
			nationalHealthId.setVisible(!filters.isVisible());
			searchModeToggle
				.setCaption(I18nProperties.getCaption(filters.isVisible() ? "externalPersonsSearchByHealthId" : "externalPersonsShowMoreFilters"));
			state.expanded = filters.isVisible();
			clearResults();
		});

		if (singlePersonMode) {
			searchModeToggle.setVisible(false);
		}
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
		VerticalLayout content = new VerticalLayout(nationalHealthId, searchModeToggle, filters, new HorizontalLayout(search, reset), message, grid);
		if (singlePersonMode) {
			Label explanation = new Label(I18nProperties.getString("externalPersonsLinkSearchExplanation"));
			explanation.setWidth(100, Unit.PERCENTAGE);
			Button minimal = new Button(I18nProperties.getCaption("externalPersonsResetMinimalCriteria"), event -> {
				resetPersonCriteria(person, false);
				searchModeToggle.setCaption(I18nProperties.getCaption("externalPersonsSearchByHealthId"));
			});
			Button maximal = new Button(I18nProperties.getCaption("externalPersonsResetMaximalCriteria"), event -> {
				resetPersonCriteria(person, true);
				searchModeToggle.setCaption(I18nProperties.getCaption("externalPersonsSearchByHealthId"));
			});
			VerticalLayout presets = new VerticalLayout(explanation, new HorizontalLayout(minimal, maximal));
			presets.setMargin(true);
			content.addComponent(presets, 2);
		}
		content.setSizeFull();
		content.setExpandRatio(grid, 1);
		addComponent(content);
		idBinder.setBean(state.id);
		filtersBinder.setBean(state.criteria);
		grid.setItems(state.results);
		setMessage(state.message == null ? I18nProperties.getString("externalPersonsEnterCriteria") : state.message);
	}

	private void resetPersonCriteria(PersonDto person, boolean maximal) {
		state.id = new PersonalDataSearchRequest();
		state.criteria = personCriteria(person, maximal);
		idBinder.setBean(state.id);
		filtersBinder.setBean(state.criteria);
		state.expanded = true;
		filters.setVisible(true);
		nationalHealthId.setVisible(false);
		clearResults();
	}

	private static PersonalDataSearchRequest personCriteria(PersonDto person, boolean maximal) {
		PersonalDataSearchRequest criteria = new PersonalDataSearchRequest();
		criteria.setFirstName(person.getFirstName());
		criteria.setLastName(person.getLastName());
		if (person.getBirthdateYYYY() != null) {
			if (person.getBirthdateMM() != null && person.getBirthdateDD() != null) {
				criteria.setBirthDate(LocalDate.of(person.getBirthdateYYYY(), person.getBirthdateMM(), person.getBirthdateDD()));
			} else {
				criteria.setBirthYearFrom(person.getBirthdateYYYY());
				criteria.setBirthYearTo(person.getBirthdateYYYY());
			}
		}
		if (maximal) {
			criteria.setSex(person.getSex());
			PresentCondition condition = person.getPresentCondition();
			if (condition == PresentCondition.ALIVE) {
				criteria.setAlive(true);
			} else if (condition != null && condition.isDeceased()) {
				criteria.setAlive(false);
			}
			if (person.getBirthdateYYYY() == null) {
				Integer age = ApproximateAgeHelper.getAgeYears(person.getApproximateAge(), person.getApproximateAgeType());
				criteria.setAgeFrom(age);
				criteria.setAgeTo(age);
			}
			LocationDto address = person.getAddress();
			if (address != null) {
				criteria.setCountryOfResidence(address.getCountry() == null ? null : address.getCountry().getIsoCode());
				criteria.setLocalityOfResidence(address.getCity());
				criteria.setMunicipalityOfResidence(address.getCommunity() == null ? null : address.getCommunity().getCaption());
			}
		}
		return criteria;
	}

	private Grid.Column<PersonalDataIndexDto, ?> addLinkColumn(Function<PersonalDataIndexDto, ?> valueProvider) {
		if (selection != null) {
			return grid.addComponentColumn(person -> {
				Object value = valueProvider.apply(person);
				Button button = new Button(value == null ? "" : value.toString(), event -> open(person));
				button.addStyleName(com.vaadin.ui.themes.ValoTheme.BUTTON_LINK);
				button.setEnabled(StringUtils.isNotBlank(person.getNationalHealthId()));
				return button;
			});
		}
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
			if (selection != null) {
				selection.accept(person);
			} else {
				SormasUI.get().getNavigator().navigateTo(ExternalPersonView.VIEW_NAME + "/" + person.getNationalHealthId());
			}
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
	}
}
