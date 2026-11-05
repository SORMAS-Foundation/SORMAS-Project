package de.symeda.sormas.ui.externalpersondata;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataIndexDto;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchRequest;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.luxembourg.LuxembourgNationalHealthIdValidator;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

public class ExternalPersonDataListView extends AbstractView {

	public static final String VIEW_NAME = "externalPersons";
	private static final long serialVersionUID = 1L;
	private static final int PAGE_SIZE = 50;
	private static final Logger logger = LoggerFactory.getLogger(ExternalPersonDataListView.class);
	private final Binder<ExternalPersonDataSearchRequest> idBinder = new Binder<>(ExternalPersonDataSearchRequest.class);
	private final Binder<ExternalPersonDataSearchRequest> filtersBinder = new Binder<>(ExternalPersonDataSearchRequest.class);
	private final TextField nationalHealthId = new TextField(I18nProperties.getCaption(Captions.ExternalPersons_nationalHealthId));
	private final GridLayout filters = new GridLayout(4, 4);
	private final ExternalPersonDataGrid grid = new ExternalPersonDataGrid(this::open);
	private final Label message = new Label();
	private SearchState state;
	private final Consumer<ExternalPersonDataIndexDto> selectedExternalPersonData;

	public static class SearchState implements Serializable {

		private static final long serialVersionUID = 1L;
		private ExternalPersonDataSearchRequest id = new ExternalPersonDataSearchRequest();
		private ExternalPersonDataSearchRequest criteria = new ExternalPersonDataSearchRequest();
		private boolean expanded;
		private List<ExternalPersonDataIndexDto> results = Collections.emptyList();
		private ExternalPersonDataSearchRequest searchRequest;
		private int totalCount;
		private String message;
	}

	public ExternalPersonDataListView() {
		this(null, null);
	}

	public ExternalPersonDataListView(PersonDto person, Consumer<ExternalPersonDataIndexDto> selectedExternalPersonDataConsumer) {
		super(VIEW_NAME);
		this.selectedExternalPersonData = selectedExternalPersonDataConsumer;

		boolean singlePersonMode = selectedExternalPersonDataConsumer != null && person != null;
		setViewHeaderVisible(!singlePersonMode);

		if (!ExternalPersonDataComponent.isAvailable()) {
			return;
		}
		state = person == null ? ViewModelProviders.of(ExternalPersonDataListView.class).get(SearchState.class) : new SearchState();
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
		textFilter(ExternalPersonDataSearchRequest.LAST_NAME, Captions.ExternalPersons_lastName);
		textFilter(ExternalPersonDataSearchRequest.FIRST_NAME, Captions.ExternalPersons_firstName);
		ComboBox<Sex> sex = new ComboBox<>(I18nProperties.getCaption(Captions.ExternalPersons_sex));
		sex.setItems(Sex.values());
		filtersBinder.bind(sex, "sex");
		filters.addComponent(sex);
		DateField birthDate = new DateField(I18nProperties.getCaption(Captions.ExternalPersons_birthDate));
		filtersBinder.bind(birthDate, "birthDate");
		filters.addComponent(birthDate);
		textFilter(ExternalPersonDataSearchRequest.COUNTRY_OF_RESIDENCE, Captions.ExternalPersons_countryOfResidence);
		textFilter(ExternalPersonDataSearchRequest.LOCALITY_OF_RESIDENCE, Captions.ExternalPersons_localityOfResidence);
		textFilter(ExternalPersonDataSearchRequest.MUNICIPALITY_OF_RESIDENCE, Captions.ExternalPersons_municipalityOfResidence);
		integerFilter(ExternalPersonDataSearchRequest.BIRTH_YEAR_FROM, Captions.ExternalPersons_birthYearFrom);
		integerFilter(ExternalPersonDataSearchRequest.BIRTH_YEAR_TO, Captions.ExternalPersons_birthYearTo);
		integerFilter(ExternalPersonDataSearchRequest.AGE_FROM, Captions.ExternalPersons_ageFrom);
		integerFilter(ExternalPersonDataSearchRequest.AGE_TO, Captions.ExternalPersons_ageTo);
		ComboBox<Boolean> alive = new ComboBox<>(I18nProperties.getCaption(Captions.ExternalPersons_alive));
		alive.setItems(Boolean.TRUE, Boolean.FALSE);
		alive.setItemCaptionGenerator(value -> I18nProperties.getString(value ? Strings.yes : Strings.no));
		filtersBinder.bind(alive, "alive");
		filters.addComponent(alive);
		filters.setVisible(state.expanded);
		nationalHealthId.setVisible(!state.expanded);
		filters.setSpacing(true);
		Button searchModeToggle = new Button(
			I18nProperties.getCaption(state.expanded ? Captions.externalPersonsSearchByHealthId : Captions.externalPersonsShowMoreFilters));
		searchModeToggle.addClickListener(event -> {
			filters.setVisible(!filters.isVisible());
			nationalHealthId.setVisible(!filters.isVisible());
			searchModeToggle.setCaption(
				I18nProperties.getCaption(filters.isVisible() ? Captions.externalPersonsSearchByHealthId : Captions.externalPersonsShowMoreFilters));
			state.expanded = filters.isVisible();
			clearResults();
		});

		if (singlePersonMode) {
			// No need to toggle in that mode: if known NationalHealthId can be set directly in person view.
			searchModeToggle.setVisible(false);
		}
		Button search = new Button(I18nProperties.getCaption(Captions.actionSearch), event -> search());
		Button reset = new Button(I18nProperties.getCaption(Captions.actionResetFilters), event -> {
			state.id = new ExternalPersonDataSearchRequest();
			state.criteria = new ExternalPersonDataSearchRequest();
			idBinder.setBean(state.id);
			filtersBinder.setBean(state.criteria);
			clearResults();
		});
		VerticalLayout content = new VerticalLayout(nationalHealthId, searchModeToggle, filters, new HorizontalLayout(search, reset), message, grid);
		if (singlePersonMode) {
			Label explanation = new Label(I18nProperties.getString(Strings.externalPersonsLinkSearchExplanation));
			explanation.setWidth(100, Unit.PERCENTAGE);
			Button minimal = new Button(I18nProperties.getCaption(Captions.externalPersonsResetMinimalCriteria), event -> {
				resetPersonCriteria(person, false);
				searchModeToggle.setCaption(I18nProperties.getCaption(Captions.externalPersonsSearchByHealthId));
			});
			Button maximal = new Button(I18nProperties.getCaption(Captions.externalPersonsResetMaximalCriteria), event -> {
				resetPersonCriteria(person, true);
				searchModeToggle.setCaption(I18nProperties.getCaption(Captions.externalPersonsSearchByHealthId));
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
		updateDataProvider();
		setMessage(state.message == null ? I18nProperties.getString(Strings.externalPersonsEnterCriteria) : state.message);
	}

	private void resetPersonCriteria(PersonDto person, boolean maximal) {
		state.id = new ExternalPersonDataSearchRequest();
		state.criteria = personCriteria(person, maximal);
		idBinder.setBean(state.id);
		filtersBinder.setBean(state.criteria);
		state.expanded = true;
		filters.setVisible(true);
		nationalHealthId.setVisible(false);
		clearResults();
	}

	private static ExternalPersonDataSearchRequest personCriteria(PersonDto person, boolean maximal) {
		ExternalPersonDataSearchRequest criteria = new ExternalPersonDataSearchRequest();
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

	private void setMessage(String value) {
		state.message = value;
		message.setValue(value);
		if (I18nProperties.getString(Strings.errorExternalPersonDataLoad).equals(value)) {
			message.addStyleName(CssStyles.ERROR_COLOR_PRIMARY);
		} else {
			message.removeStyleName(CssStyles.ERROR_COLOR_PRIMARY);
		}
	}

	private void textFilter(String property, String captionKey) {
		TextField field = new TextField(I18nProperties.getCaption(captionKey));
		filtersBinder.forField(field).withConverter(StringUtils::trimToNull, value -> value == null ? "" : value).bind(property);
		filters.addComponent(field);
	}

	private void integerFilter(String property, String captionKey) {
		TextField field = new TextField(I18nProperties.getCaption(captionKey));
		filtersBinder.forField(field)
			.withNullRepresentation("")
			.withConverter(new StringToIntegerConverter(I18nProperties.getString(Strings.externalPersonsInvalidRange)))
			.withValidator(value -> value == null || value >= 0, I18nProperties.getString(Strings.externalPersonsInvalidRange))
			.bind(property);
		filters.addComponent(field);
	}

	private void clearResults() {
		state.results = Collections.emptyList();
		state.searchRequest = null;
		state.totalCount = 0;
		updateDataProvider();
		setMessage(I18nProperties.getString(Strings.externalPersonsEnterCriteria));
	}

	private void search() {
		if (!ExternalPersonDataComponent.isAvailable()) {
			clearResults();
			return;
		}
		ExternalPersonDataSearchRequest request = new ExternalPersonDataSearchRequest();
		if (!(filters.isVisible() ? filtersBinder : idBinder).writeBeanIfValid(request)) {
			return;
		}
		if (request.equals(new ExternalPersonDataSearchRequest())) {
			return;
		}
		if (!validRange(request.getBirthYearFrom(), request.getBirthYearTo()) || !validRange(request.getAgeFrom(), request.getAgeTo())) {
			Notification.show(I18nProperties.getString(Strings.externalPersonsInvalidRange), Notification.Type.WARNING_MESSAGE);
			return;
		}
		try {
			request.setOffset(0);
			request.setLimit(PAGE_SIZE);
			ExternalPersonDataSearchResponse response = FacadeProvider.getExternalPersonDataProviderFacade().search(request);
			List<ExternalPersonDataIndexDto> results = response.getResults() == null ? Collections.emptyList() : response.getResults();
			state.results = new ArrayList<>(results);
			state.searchRequest = request;
			state.totalCount = response.getTotalCount() == null ? results.size() : response.getTotalCount();
			updateDataProvider();
			grid.scrollToStart();
			setMessage(results.isEmpty() ? I18nProperties.getString(Strings.infoNoExternalPersonData) : "");
			if (results.size() == 1 && response.getTotalCount() != null && response.getTotalCount() == 1) {
				open(results.get(0));
			}
		} catch (RuntimeException e) {
			logger.error("Could not search external persons", e);
			clearResults();
			setMessage(I18nProperties.getString(Strings.errorExternalPersonDataLoad));
		}
	}

	private void updateDataProvider() {
		grid.setDataProvider(
			DataProvider.fromCallbacks(query -> fetchResults(query.getOffset(), query.getLimit()).stream(), query -> state.totalCount));
	}

	private List<ExternalPersonDataIndexDto> fetchResults(int offset, int limit) {
		if (state.searchRequest == null || offset >= state.totalCount) {
			return Collections.emptyList();
		}
		int end = offset + Math.min(limit, state.totalCount - offset);
		if (end <= state.results.size()) {
			return state.results.subList(offset, end);
		}
		ExternalPersonDataSearchRequest request = SerializationUtils.clone(state.searchRequest);
		request.setOffset(offset);
		request.setLimit(limit);
		try {
			ExternalPersonDataSearchResponse response = FacadeProvider.getExternalPersonDataProviderFacade().search(request);
			return response.getResults() == null ? Collections.emptyList() : response.getResults();
		} catch (RuntimeException e) {
			logger.error("Could not load external persons page", e);
			setMessage(I18nProperties.getString(Strings.errorExternalPersonDataLoad));
			return Collections.emptyList();
		}
	}

	private static boolean validRange(Integer from, Integer to) {
		return from == null || to == null || from <= to;
	}

	private void open(ExternalPersonDataIndexDto person) {
		if (StringUtils.isNotBlank(person.getNationalHealthId())) {
			if (selectedExternalPersonData != null) {
				selectedExternalPersonData.accept(person);
			} else {
				SormasUI.get().getNavigator().navigateTo(ExternalPersonDataContainerView.VIEW_NAME + "/" + person.getNationalHealthId());
			}
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
	}
}
