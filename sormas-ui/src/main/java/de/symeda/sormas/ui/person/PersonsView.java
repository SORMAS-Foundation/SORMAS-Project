package de.symeda.sormas.ui.person;

import static java.util.Objects.nonNull;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.PersonDownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PersonsView extends AbstractView {

	public static final String VIEW_NAME = "persons";

	private final PersonCriteria criteria;
	private final FilteredGrid<?, PersonCriteria> grid;
	private Label noAccessLabel;
	private LinkedHashMap<Button, PersonAssociation> associationButtons;
	private Button activeAssociationButton;
	private PersonFilterForm filterForm;
	private final Map<PersonAssociation, Boolean> associationAllowedValues = new EnumMap<>(PersonAssociation.class);
	private final Map<PersonAssociation, Boolean> associationActiveValues = new EnumMap<>(PersonAssociation.class);

	public PersonsView() {
		super(VIEW_NAME);

		// Avoid calling ALL associations at view start because the query tends to take long time
		final VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		final HorizontalLayout associationFilterBar = createAssociationFilterBar();
		gridLayout.addComponent(associationFilterBar);
		gridLayout.setComponentAlignment(associationFilterBar, Alignment.MIDDLE_RIGHT);

		PersonAssociation defaultAssociation = getFirstAllowedPersonAssociation();
		if (defaultAssociation != null) {
			PersonCriteria defaultCriteria = new PersonCriteria().personAssociation(defaultAssociation);
			criteria = ViewModelProviders.of(PersonsView.class).get(PersonCriteria.class, defaultCriteria);
			grid = new PersonGrid(criteria);

			gridLayout.addComponent(grid);
			gridLayout.setMargin(true);
			gridLayout.setSpacing(false);
			gridLayout.setSizeFull();
			gridLayout.setExpandRatio(grid, 1);
			gridLayout.setStyleName("crud-main-layout");

			if (UserProvider.getCurrent().hasUserRight(UserRight.PERSON_EXPORT)) {
				VerticalLayout exportLayout = new VerticalLayout();
				exportLayout.setSpacing(true);
				exportLayout.setMargin(true);
				exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
				exportLayout.setWidth(200, Unit.PIXELS);

				PopupButton exportButton = ButtonHelper.createIconPopupButton(Captions.export, VaadinIcons.DOWNLOAD, exportLayout);
				addHeaderComponent(exportButton);

				Button basicExportButton = ButtonHelper.createIconButton(Captions.exportBasic, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
				basicExportButton.setDescription(I18nProperties.getString(Strings.infoBasicExport));
				basicExportButton.setWidth(100, Unit.PERCENTAGE);
				exportLayout.addComponent(basicExportButton);
				StreamResource streamResource =
					GridExportStreamResource.createStreamResourceWithSelectedItems(grid, Collections::emptySet, ExportEntityName.PERSONS);
				FileDownloader fileDownloader = new FileDownloader(streamResource);
				fileDownloader.extend(basicExportButton);

				StreamResource extendedExportStreamResource = PersonDownloadUtil.createPersonExportResource(grid.getCriteria(), null);
				addExportButton(
					extendedExportStreamResource,
					exportButton,
					exportLayout,
					VaadinIcons.FILE_TEXT,
					Captions.exportDetailed,
					Strings.infoDetailedExport);

				Button btnCustomExport = ButtonHelper.createIconButton(Captions.exportCustom, VaadinIcons.FILE_TEXT, e -> {
					ControllerProvider.getCustomExportController().openPersonExportWindow(grid.getCriteria());
					exportButton.setPopupVisible(false);
				}, ValoTheme.BUTTON_PRIMARY);
				btnCustomExport.setDescription(I18nProperties.getString(Strings.infoCustomExport));
				btnCustomExport.setWidth(100, Unit.PERCENTAGE);
				exportLayout.addComponent(btnCustomExport);
			}

			if (FacadeProvider.getGeocodingFacade().isEnabled()) {
				Button setMissingCoordinatesButton = ButtonHelper.createIconButton(
					I18nProperties.getCaption(Captions.personsSetMissingGeoCoordinates),
					VaadinIcons.MAP_MARKER,
					e -> showMissingCoordinatesPopUp(),
					ValoTheme.BUTTON_PRIMARY);
				addHeaderComponent(setMissingCoordinatesButton);
			}

			grid.getDataProvider().addDataProviderListener(e -> updateAssociationButtons());
		} else {
			criteria = new PersonCriteria();
			grid = null;
			noAccessLabel =
				new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoNoAccessToPersonEntities), ContentMode.HTML);
			gridLayout.addComponent(noAccessLabel);
		}

		addComponent(gridLayout);
	}

	private void showMissingCoordinatesPopUp() {

		Label popupDescLabel = new Label(I18nProperties.getString(Strings.confirmationSetMissingGeoCoordinates));
		CheckBox popupCheckbox = new CheckBox(I18nProperties.getCaption(Captions.personsReplaceGeoCoordinates));
		popupCheckbox.setValue(false);

		VerticalLayout popupLayout = new VerticalLayout();
		popupLayout.setMargin(false);
		popupLayout.setSpacing(true);
		popupDescLabel.setWidth(100, Unit.PERCENTAGE);
		popupCheckbox.setWidth(100, Unit.PERCENTAGE);
		popupLayout.addComponent(popupDescLabel);
		popupLayout.addComponent(popupCheckbox);

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.personsSetMissingGeoCoordinates),
			popupLayout,
			I18nProperties.getCaption(Captions.actionContinue),
			I18nProperties.getCaption(Captions.actionCancel),
			640,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					long changedPersons = FacadeProvider.getPersonFacade().setMissingGeoCoordinates(popupCheckbox.getValue());
					Notification.show(
						I18nProperties.getCaption(Captions.personsUpdated),
						String.format(I18nProperties.getString(Strings.notificationPersonsUpdated), changedPersons),
						Notification.Type.TRAY_NOTIFICATION);
				}
			});
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}

		updateFilterComponents();
	}

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		if (criteria.getPersonAssociation() != null && !isPersonAssociationAllowed(criteria.getPersonAssociation())) {
			PersonAssociation firstAllowedAssociation = getFirstAllowedPersonAssociation();
			// The following line is needed because we want to correct the value in PersonCriteria in order to have a consistent state; setting the default association is 
			// necessary because calling PersonCriteria.setPersonAssociation with null throws an exception.
			criteria.setPersonAssociation(firstAllowedAssociation != null ? firstAllowedAssociation : PersonCriteria.DEFAULT_ASSOCIATION);
		}

		updateAssociationButtons();

		filterForm.setValue(criteria);

		applyingCriteria = false;
	}

	private void updateAssociationButtons() {

		associationButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(associationButtons.get(b).toString());
			if (b.getData() == criteria.getPersonAssociation()) {
				activeAssociationButton = b;
			}
		});
		if (activeAssociationButton != null) {
			CssStyles.removeStyles(activeAssociationButton, CssStyles.BUTTON_FILTER_LIGHT);
			activeAssociationButton.setCaption(
				associationButtons.get(activeAssociationButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getItemCount())));
		}
	}

	public VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterForm = new PersonFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter()) {
				navigateTo(null);
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(PersonsView.class).remove(PersonCriteria.class);
			navigateTo(null, true);
		});
		filterForm.addApplyHandler(e -> {
			if (grid != null) {
				((PersonGrid) grid).reload();
			}
		});
		filterLayout.addComponent(filterForm);

		return filterLayout;
	}

	public HorizontalLayout createAssociationFilterBar() {
		HorizontalLayout associationFilterLayout = new HorizontalLayout();
		associationFilterLayout.setSpacing(true);
		associationFilterLayout.setMargin(false);
		associationFilterLayout.setWidth(100, Unit.PERCENTAGE);
		associationFilterLayout.addStyleName(CssStyles.VSPACE_3);

		associationButtons = new LinkedHashMap<>();

		for (PersonAssociation association : PersonAssociation.values()) {
			if (!isPersonAssociationAllowed(association)) {
				continue;
			}

			Button associationButton = ButtonHelper.createButton(association.toString(), e -> {
				if ((nonNull(UserProvider.getCurrent()) && !UserProvider.getCurrent().hasNationJurisdictionLevel())
					&& association == PersonAssociation.ALL) {
					Label contentLabel = new Label(I18nProperties.getString(Strings.confirmationSeeAllPersons));
					VaadinUiUtil.showConfirmationPopup(
						I18nProperties.getString(Strings.headingSeeAllPersons),
						contentLabel,
						I18nProperties.getString(Strings.yes),
						I18nProperties.getString(Strings.no),
						640,
						ee -> {
							if (Boolean.TRUE.equals(ee)) {
								criteria.personAssociation(association);
								navigateTo(criteria);
							}
						});
				} else {
					criteria.personAssociation(association);
					navigateTo(criteria);
				}
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
			associationButton.setData(association);
			associationButton.setCaptionAsHtml(true);

			associationFilterLayout.addComponent(associationButton);
			associationFilterLayout.setComponentAlignment(associationButton, Alignment.MIDDLE_LEFT);
			associationButtons.put(associationButton, association);
		}

		Label emptyLabel = new Label("");
		associationFilterLayout.addComponent(emptyLabel);
		associationFilterLayout.setComponentAlignment(emptyLabel, Alignment.MIDDLE_RIGHT);
		associationFilterLayout.setExpandRatio(emptyLabel, 1);

		return associationFilterLayout;
	}

	private PersonAssociation getFirstAllowedPersonAssociation() {

		Iterator<PersonAssociation> associationsIterator = associationButtons.values().iterator();
		PersonAssociation defaultAssociation = associationsIterator.hasNext() ? associationsIterator.next() : null;
		if (defaultAssociation == PersonAssociation.ALL) {
			defaultAssociation = associationsIterator.hasNext() ? associationsIterator.next() : null;
		}
		return defaultAssociation;
	}

	private boolean isPersonAssociationAllowed(PersonAssociation personAssociation) {

		if (associationAllowedValues.isEmpty()) {
			associationAllowedValues.put(PersonAssociation.IMMUNIZATION, UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_VIEW));
			associationActiveValues.put(
				PersonAssociation.IMMUNIZATION,
				FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.IMMUNIZATION_MANAGEMENT)
					&& !FacadeProvider.getFeatureConfigurationFacade()
						.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED));
			associationAllowedValues.put(PersonAssociation.TRAVEL_ENTRY, UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_VIEW));
			associationActiveValues.put(
				PersonAssociation.TRAVEL_ENTRY,
				FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TRAVEL_ENTRIES)
					&& FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY));
			associationAllowedValues.put(PersonAssociation.CONTACT, UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW));
			associationActiveValues
				.put(PersonAssociation.CONTACT, FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CONTACT_TRACING));
			associationAllowedValues.put(PersonAssociation.CASE, UserProvider.getCurrent().hasUserRight(UserRight.CASE_VIEW));
			associationActiveValues
				.put(PersonAssociation.CASE, FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE));
			associationAllowedValues
				.put(PersonAssociation.EVENT_PARTICIPANT, UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_VIEW));
			associationActiveValues.put(
				PersonAssociation.EVENT_PARTICIPANT,
				FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE));
		}

		if (personAssociation == PersonAssociation.ALL) {
			return (associationAllowedValues.get(PersonAssociation.IMMUNIZATION) || !associationActiveValues.get(PersonAssociation.IMMUNIZATION))
				&& (associationAllowedValues.get(PersonAssociation.TRAVEL_ENTRY) || !associationActiveValues.get(PersonAssociation.TRAVEL_ENTRY))
				&& (associationAllowedValues.get(PersonAssociation.CONTACT) || !associationActiveValues.get(PersonAssociation.CONTACT))
				&& (associationAllowedValues.get(PersonAssociation.CASE) || !associationActiveValues.get(PersonAssociation.CASE))
				&& (associationAllowedValues.get(PersonAssociation.EVENT_PARTICIPANT)
					|| !associationActiveValues.get(PersonAssociation.EVENT_PARTICIPANT));
		} else {
			return (associationAllowedValues.get(personAssociation) && associationActiveValues.get(personAssociation));
		}
	}

}
