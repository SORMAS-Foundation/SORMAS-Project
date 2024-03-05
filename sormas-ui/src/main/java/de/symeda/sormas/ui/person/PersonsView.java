package de.symeda.sormas.ui.person;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.PersonDownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;
import de.symeda.sormas.ui.utils.components.popupmenu.PopupMenu;

public class PersonsView extends AbstractView {

	private static final long serialVersionUID = -6292580716619536538L;

	public static final String VIEW_NAME = "persons";

	private final PersonCriteria criteria;
	private final FilteredGrid<?, PersonCriteria> grid;
	private Label noAccessLabel;
	private LinkedHashMap<Button, PersonAssociation> associationButtons;
	private Button activeAssociationButton;
	private PersonFilterForm filterForm;

	private ViewConfiguration viewConfiguration;

	// Bulk operations
	private MenuBar bulkOperationsDropdown;

	public PersonsView() {
		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(PersonsView.class).get(ViewConfiguration.class);
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

			if (UiUtil.permitted(UserRight.PERSON_EXPORT)) {
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

			grid.addDataSizeChangeListener(e -> updateAssociationButtons());
		} else {
			criteria = new PersonCriteria();
			grid = null;
			associationFilterBar.setVisible(false);
			noAccessLabel =
				new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoNoAccessToPersonEntities), ContentMode.HTML);
			gridLayout.addComponent(noAccessLabel);
		}

		addComponent(gridLayout);

		final PopupMenu moreButton = new PopupMenu(I18nProperties.getCaption(Captions.moreActions));

		if (UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
			Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
			moreButton.addMenuEntry(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
			moreButton.addMenuEntry(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(true);
				ViewModelProviders.of(PersonsView.class).get(ViewConfiguration.class).setInEagerMode(true);
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				((PersonGrid) grid).reload();
				((PersonGrid) grid).setBulkEditMode(true);
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(false);
				ViewModelProviders.of(PersonsView.class).get(ViewConfiguration.class).setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				navigateTo(criteria);
				((PersonGrid) grid).setBulkEditMode(false);
			});
		}

		if (moreButton.hasMenuEntries()) {
			addHeaderComponent(moreButton);
		}
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

		if (viewConfiguration.isInEagerMode()) {
			((PersonGrid) grid).setBulkEditMode(true);
		}

		updateFilterComponents();
	}

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		if (criteria.getPersonAssociation() != null && !associationButtons.values().contains(criteria.getPersonAssociation())) {
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
		if (activeAssociationButton != null && grid != null) {
			CssStyles.removeStyles(activeAssociationButton, CssStyles.BUTTON_FILTER_LIGHT);
			activeAssociationButton.setCaption(
				associationButtons.get(activeAssociationButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getDataSize())));
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
		associationFilterLayout.setMargin(new MarginInfo(true, false, false, false));
		associationFilterLayout.setWidth(100, Unit.PERCENTAGE);
		associationFilterLayout.addStyleName(CssStyles.VSPACE_3);

		associationButtons = new LinkedHashMap<>();
		for (PersonAssociation association : FacadeProvider.getPersonFacade().getPermittedAssociations()) {

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

		// Bulk operation dropdown
		if (UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
			List<MenuBarHelper.MenuBarItem> bulkActions = new ArrayList<>();
			if (UiUtil.permitted(UserRight.PERSON_MERGE)) {
				bulkActions.add(
					new MenuBarHelper.MenuBarItem(
						I18nProperties.getCaption(Captions.actionMerge),
						VaadinIcons.COMPRESS_SQUARE,
						mi -> grid.bulkActionHandler(items -> {
							if (items.size() != 2) {
								VaadinUiUtil.showWarningPopup(I18nProperties.getString(Strings.messageCannotMergeMoreThanTwoPersons));
							} else {

								Iterator selectionsIterator = items.iterator();
								final PersonIndexDto person1 = (PersonIndexDto) selectionsIterator.next();
								final PersonIndexDto person2 = (PersonIndexDto) selectionsIterator.next();

								final PersonDto leadPersonDto = FacadeProvider.getPersonFacade().getByUuid(person1.getUuid());
								final PersonSimilarityCriteria criteria = new PersonSimilarityCriteria().sex(leadPersonDto.getSex())
									.nationalHealthId(leadPersonDto.getNationalHealthId())
									.passportNumber(leadPersonDto.getPassportNumber())
									.birthdateDD(leadPersonDto.getBirthdateDD())
									.birthdateMM(leadPersonDto.getBirthdateMM())
									.birthdateYYYY(leadPersonDto.getBirthdateYYYY());
								criteria.setName(leadPersonDto);

								if (!FacadeProvider.getPersonFacade().isPersonSimilar(criteria, person2.getUuid())) {
									VaadinUiUtil.showConfirmationPopup(
										I18nProperties.getString(Strings.headingPickOrMergePersonConfirmation),
										new Label(I18nProperties.getString(Strings.infoPersonMergeConfirmationForNonSimilarPersons)),
										I18nProperties.getCaption(Captions.actionProceed),
										I18nProperties.getCaption(Captions.actionCancel),
										800,
										confirmAgain -> {
											if (Boolean.TRUE.equals(confirmAgain)) {
												ControllerProvider.getPersonController().mergePersons(person1, person2);
											}
										});
								} else {
									ControllerProvider.getPersonController().mergePersons(person1, person2);
								}
							}
							grid.deselectAll();
						}, true)));
			} ;

			bulkOperationsDropdown = MenuBarHelper.createDropDown(Captions.bulkActions, bulkActions);
			bulkOperationsDropdown.setVisible(viewConfiguration.isInEagerMode());
			associationFilterLayout.addComponent(bulkOperationsDropdown);
		}

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
}
