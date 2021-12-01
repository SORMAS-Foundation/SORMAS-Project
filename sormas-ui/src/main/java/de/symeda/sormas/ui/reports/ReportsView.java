/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.reports;

import java.util.Date;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ReportsView extends AbstractView {

	private static final long serialVersionUID = -226852255434803180L;

	public static final String VIEW_NAME = "reports";

	private Grid grid;
	private VerticalLayout gridLayout;
	private AbstractSelect yearFilter;
	private AbstractSelect epiWeekFilter;

	public ReportsView() {
		super(VIEW_NAME);

		if (UserRole.getJurisdictionLevel(UserProvider.getCurrent().getUserRoles()) == JurisdictionLevel.NATION) {
			grid = new WeeklyReportRegionsGrid();
		} else {
			grid = new WeeklyReportOfficersGrid();
		}

		grid.setHeightMode(HeightMode.UNDEFINED);

		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);

		addComponent(gridLayout);

		Button createReportButton = ButtonHelper.createIconButton(
				Captions.reportNewReport,
				VaadinIcons.PLUS_CIRCLE,
				e -> CaseReportWindow(),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createReportButton);
			if (!UserProvider.getCurrent().hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
				createReportButton.setEnabled(false);
			}
		}

	public void CaseReportWindow() {
		Window window = VaadinUiUtil.createPopupWindow();
		window.setWidth((700), Sizeable.Unit.PIXELS);
		window.setCaption(I18nProperties.getCaption(Captions.reportNewReport));
		CreateSpecificCasesLayout layout = buildReportLayout(window);
		window.setContent(layout);
		UI.getCurrent().addWindow(window);
	}
	
	private CreateSpecificCasesLayout buildReportLayout(Window window) throws ValidationRuntimeException {

		String confirmCaption = I18nProperties.getCaption(
			Captions.saveNewReport);

		DateField date = new DateField("Date");
		ComboBox pointofentries = new ComboBox<PointOfEntryReferenceDto>(I18nProperties.getCaption(Captions.pointOfEntry));
		TextField people = new TextField(I18nProperties.getCaption(Captions.reportNumberpeople));
		TextField examinatedpeople = new TextField(I18nProperties.getCaption(Captions.reportNumberTestedpassenger));
		
		final UserDto currentUser = UserProvider.getCurrent().getUser();
		Runnable confirmCallback = () -> {
			int nomberOfPeople = 0;
			int Tested_nomberPeople = 0;
			PointOfEntryReferenceDto PointOfEntry = (PointOfEntryReferenceDto) pointofentries.getValue();
			
			try {
				DateHelper.parseDate(date.getValue().toString(), new SimpleDateFormat("dd/MM/yyyy"));
			} catch (Exception e) {
				throw new ValidationRuntimeException(
					I18nProperties.getString(Strings.messageDateError)
				);
			}

			if(pointofentries.getValue() == null) {
				throw new ValidationRuntimeException(
					I18nProperties.getString(Strings.NoEntryPointError));
			}
			
			try {
				nomberOfPeople = Integer.parseInt(people.getValue());
			} catch (Exception e) {
				throw new ValidationRuntimeException(
					I18nProperties.getString(Strings.BadPeopleNumberError));
			}

			if (nomberOfPeople <= 0){
				throw  new ValidationRuntimeException(
					I18nProperties.getString(Strings.NegativePeopleNumberError));
			}

			try {
				Tested_nomberPeople = Integer.parseInt(examinatedpeople.getValue());
			} catch (Exception e) {
				throw new ValidationRuntimeException(
					I18nProperties.getString(Strings.BadTestedPeopleNumberError));
			}

			if (Tested_nomberPeople < 0){
				throw  new ValidationRuntimeException(
					I18nProperties.getString(Strings.NegativeExaminatedPeopleNumberError));
			}

			int numberOfNotExCase = nomberOfPeople - Tested_nomberPeople;

			if (numberOfNotExCase < 0){
				throw new ValidationRuntimeException(I18nProperties.getString(
					Strings.DiffPeopleNumberError));
			}
			
			DistrictReferenceDto activeDistrict = null;
			RegionReferenceDto Region = null;
			if (currentUser.getDistrict() != null){
				logger.info("Le district de lutilisateur correspond a {} et la region {}", currentUser.getDistrict(), currentUser.getRegion());
				activeDistrict = currentUser.getDistrict();
				// Si dans le formulaire d'utilisateur l'on a renseigné le
				// district alors la région a étée renseignée au préalabre
				Region = currentUser.getRegion();
			}else{
				logger.info("Recherche via la liste des distrcits");
				List<String> allDistrcits = districtFacade.getAllUuids();
				logger.info("Liste des districts {}", allDistrcits);
				boolean found = false;
				for (String TheDistrict : allDistrcits){
					if (found == true){
						break;
					}
					List<PointOfEntryReferenceDto> PointOfEntries = pointOfEntryFacade.getAllActiveByDistrict(TheDistrict, false);
					for (PointOfEntryReferenceDto pntofentry : PointOfEntries){
						logger.info("Comparaison de {} et {}", PointOfEntry, pntofentry);
						if(PointOfEntry.equals(pntofentry)){
							logger.info("Point trouve : {} pour le district {}", pntofentry, TheDistrict);
							activeDistrict = districtFacade.getByUuid(TheDistrict).toReference();
							Region = districtFacade.getByUuid(TheDistrict).getRegion();
							logger.info("La region {} a te mise a jour par la meme occasion", Region);
							found = true;
							break;
						}
					}
				}
			}
			
			int i;
			int CountnumberOfExCase = 0;
			int CountnumberOfNotExCase = 0;
			for (i=0; i < nomberOfPeople; i++) {
				// importResult = caseImportFacade.DirectSaveCaseData(
				// values, entityClasses, entityProperties, entityPropertyPaths, false);
				personDto = new PersonDto();
				personDto.setFirstName("EMPTY_FIRST_FAME");
				personDto.setLastName("EMPTY_LAST_NAME");
				personDto.setSex(Sex.UNKNOWN);
				PersonDto createdPerson = personFacade.savePerson(personDto);
				logger.debug("Personne cree {} ", createdPerson.getUuid());

				caseDataDto = new CaseDataDto();
				caseDataDto.setDisease(Disease.UNDEFINED);
				caseDataDto.setDiseaseDetails(Disease.UNDEFINED.getName());
				caseDataDto.setPerson(createdPerson.toReference());
				if (CountnumberOfNotExCase < numberOfNotExCase){
					caseDataDto.setCaseClassification(CaseClassification.NOT_EXAMINATED);
					CountnumberOfNotExCase++;
				}else{
					caseDataDto.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
					CountnumberOfExCase++;
				}
				caseDataDto.setReportDate(DateHelper.parseDate(date.getValue().toString(), new SimpleDateFormat("dd/MM/yyyy")));
				caseDataDto.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
				caseDataDto.setPointOfEntry(PointOfEntry);
				caseDataDto.setResponsibleDistrict(activeDistrict);
				caseDataDto.setResponsibleRegion(Region);
				caseDataDto.setReportingUser(currentUser.toReference());
				caseDataDto.setInvestigationStatus(InvestigationStatus.PENDING);
				caseDataDto.setOutcome(CaseOutcome.NO_OUTCOME);
				
				CaseDataDto createdcase = caseFacade.saveCase(caseDataDto);
				logger.debug("Cas cree {}", createdcase.getUuid());
			}
			logger.debug("Creation des cas terminee. {} cas non examines"+
			" crees et {} cas examine crees.",
			CountnumberOfNotExCase, CountnumberOfExCase);
			
			Notification.show(I18nProperties.getString(Strings.messageCaseAutoSaved), Type.WARNING_MESSAGE);
			SormasUI.refreshView();
			UI.getCurrent().removeWindow(window);
		};

		return new CreateSpecificCasesLayout(
			confirmCallback, window::close, date,
			pointofentries, people,
			examinatedpeople, confirmCaption);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.addStyleName(CssStyles.VSPACE_3);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		EpiWeek prevEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
		int year = prevEpiWeek.getYear();
		int week = prevEpiWeek.getWeek();

		yearFilter = ComboBoxHelper.createComboBoxV7();
		yearFilter.setId(Strings.year);
		yearFilter.setWidth(200, Unit.PIXELS);
		yearFilter.setNullSelectionAllowed(false);
		yearFilter.addItems(DateHelper.getYearsToNow());
		yearFilter.select(year);
		yearFilter.setCaption(I18nProperties.getString(Strings.year));
		yearFilter.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		yearFilter.addValueChangeListener(e -> {
			updateEpiWeeks(
				(int) e.getProperty().getValue(),
				(int) epiWeekFilter.getValue());
			reloadGrid();
		});
		filterLayout.addComponent(yearFilter);

		epiWeekFilter = ComboBoxHelper.createComboBoxV7();
		epiWeekFilter.setId(Strings.epiWeek);
		epiWeekFilter.setWidth(200, Unit.PIXELS);
		epiWeekFilter.setNullSelectionAllowed(false);
		updateEpiWeeks(year, week);
		epiWeekFilter.setCaption(I18nProperties.getString(Strings.epiWeek));
		epiWeekFilter.addValueChangeListener(e -> {
			reloadGrid();
		});
		filterLayout.addComponent(epiWeekFilter);

		Button lastWeekButton = ButtonHelper.createButton(
			Captions.dashboardLastWeek,
			String.format(
				I18nProperties.getCaption(
					Captions.dashboardLastWeek),
					DateHelper.getPreviousEpiWeek(new Date()).toString()),
			e -> {
				EpiWeek epiWeek = DateHelper.getPreviousEpiWeek(new Date());
				yearFilter.select(epiWeek.getYear());
				epiWeekFilter.select(epiWeek.getWeek());
			},
			CssStyles.FORCE_CAPTION);

		filterLayout.addComponent(lastWeekButton);

		Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setDescription(
			I18nProperties.getString(Strings.infoWeeklyReportsView),
			ContentMode.HTML);
		infoLabel.setSizeUndefined();
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
		filterLayout.addComponent(infoLabel);
		filterLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_RIGHT);
		filterLayout.setExpandRatio(infoLabel, 1);

		return filterLayout;
	}

	private void updateEpiWeeks(int year, int week) {
		List<EpiWeek> epiWeekList = DateHelper.createEpiWeekList(year);
		for (EpiWeek epiWeek : epiWeekList) {
			epiWeekFilter.addItem(epiWeek.getWeek());
			epiWeekFilter.setItemCaption(epiWeek.getWeek(), epiWeek.toString());
		}
		epiWeekFilter.select(week);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		reloadGrid();
	}

	private void reloadGrid() {
		if (grid instanceof WeeklyReportRegionsGrid) {
			((WeeklyReportRegionsGrid) grid).reload((int) yearFilter.getValue(), (int) epiWeekFilter.getValue());
		} else {
			((WeeklyReportOfficersGrid) grid)
				.reload(UserProvider.getCurrent().getUser().getRegion(), (int) yearFilter.getValue(), (int) epiWeekFilter.getValue());
		}
	}
}
