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
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.Grid;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.TextField;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.symeda.sormas.ui.CreateSpecificCasesLayout;
import de.symeda.sormas.api.infrastructure.district.DistrictFacade;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
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
import com.vaadin.ui.ComboBox;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import java.text.SimpleDateFormat;
import com.vaadin.ui.Notification;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryFacade;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import com.vaadin.ui.Notification.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class ReportsView extends AbstractView {

	private static final int _60000 = 60000; //5 min

	private static final long serialVersionUID = -226852255434803180L;

	public static final String VIEW_NAME = "reports";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private CaseDataDto caseDataDto;

	private PersonDto personDto;

	private final PersonFacade personFacade;

	private final UserFacade userFacade;

	private final CaseFacade caseFacade;

	private final PointOfEntryFacade pointOfEntryFacade;

	private final DistrictFacade districtFacade;

	private int CountnumberOfExCase = 0;

	private int CountnumberOfNotExCase = 0;

	private DistrictReferenceDto activeDistrict = null;

	private RegionReferenceDto region = null;

	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

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
		gridLayout.setExpandRatio(grid, 4);
		personFacade = FacadeProvider.getPersonFacade();
		userFacade = FacadeProvider.getUserFacade();
		caseFacade = FacadeProvider.getCaseFacade();
		pointOfEntryFacade = FacadeProvider.getPointOfEntryFacade();
		districtFacade = FacadeProvider.getDistrictFacade();

		addComponent(gridLayout);

		Button createReportButton = ButtonHelper.createIconButton(
				Captions.reportNewReport,
				VaadinIcons.PLUS_CIRCLE,
				e -> CaseReportWindow(),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createReportButton);
			if ((!UserProvider.getCurrent().hasUserRole(UserRole.SURVEILLANCE_OFFICER)) && (!UserProvider.getCurrent().hasUserRole(UserRole.POE_INFORMANT))) {
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
			PointOfEntryReferenceDto pointOfEntry = (PointOfEntryReferenceDto) pointofentries.getValue();
			
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
			
			if (currentUser.getDistrict() != null){
				activeDistrict = currentUser.getDistrict();
				// Si dans le formulaire d'utilisateur l'on a renseigné le
				// district alors la région a étée renseignée au préalabre
				region = currentUser.getRegion();
			}else{
				List<String> allDistrcits = districtFacade.getAllUuids();
				boolean found = false;
				for (String TheDistrict : allDistrcits){
					if (found == true){
						break;
					}
					List<PointOfEntryReferenceDto> PointOfEntries = pointOfEntryFacade.getAllActiveByDistrict(TheDistrict, false);
					for (PointOfEntryReferenceDto pntofentry : PointOfEntries){
						if(pointOfEntry.equals(pntofentry)){
							activeDistrict = districtFacade.getByUuid(TheDistrict).toReference();
							region = districtFacade.getByUuid(TheDistrict).getRegion();
							found = true;
							break;
						}
					}
				}
			}

			UserDto UserToSave = userFacade.getByUuid(currentUser.getUuid());
			if (UserToSave != null){
				String strExam = UserToSave.getNumberofexaminatedpeople();
				String strNotExam = UserToSave.getNumberofnonexaminatedpeople();
				String chaine = "";
				String Secondechaine = "";
				String SecondechaineDeux = "";
				String chaineDeux = "";
				logger.debug("La date a sauvegarder {}", date.getValue().toString());
				chaine = strExam + Tested_nomberPeople + ":" + pointOfEntry.getUuid() + "/" + date.getValue().toString() + ";";
				chaineDeux = strNotExam + numberOfNotExCase + ":" + pointOfEntry.getUuid() + "/" + date.getValue().toString() + ";";
				boolean tosave = false;
				
				if(Tested_nomberPeople > 0){
					String NbExted = UserToSave.getNumberofexaminatedpeople();
					String[] NbExtedArray = NbExted.split(";");
					if (!StringUtils.isBlank(NbExted)){
						boolean add = true;
						for (String valeurEntiere : NbExtedArray){
							if (valeurEntiere.contains(pointOfEntry.getUuid()+ "/" + date.getValue().toString())){
								logger.info("{} contenu dans {}", pointOfEntry.getUuid()+"+"+date.getValue().toString(), valeurEntiere);
								String[] array = valeurEntiere.split(":", 2);
								int numberPresent = Integer.parseInt(array[0]);
								logger.debug("Maintenant on a {}", numberPresent);
								numberPresent+=Tested_nomberPeople;
								String Newchaine = numberPresent + ":" + pointOfEntry.getUuid() + "/" + date.getValue().toString() + ";";
								logger.debug("Nous remplacons {} par {}", valeurEntiere, Newchaine);
								Secondechaine += Newchaine;
								add = false;
							}
							else{
								Secondechaine+=valeurEntiere + ";";
							}
						}
						if(add == true){
							Secondechaine+=Tested_nomberPeople + ":" + pointOfEntry.getUuid() + "/" + date.getValue().toString() + ";";
						}
						String ChaineFinal = Secondechaine;
						if (!StringUtils.isBlank(ChaineFinal)){
							UserToSave.setNumberofexaminatedpeople(ChaineFinal);
							tosave = true;
						}
					}else{
						logger.info("Enregidtrement de la data {}", chaine);
						UserToSave.setNumberofexaminatedpeople(chaine);
						tosave = true;
					}
				}
				if(numberOfNotExCase > 0){
					String NbNotExted = UserToSave.getNumberofnonexaminatedpeople();
					String[] NbNotExtedArray = NbNotExted.split(";");
					if (!StringUtils.isBlank(NbNotExted)){
						boolean adding = true;
						for (String valeurEntiere : NbNotExtedArray){
							if (valeurEntiere.contains(pointOfEntry.getUuid()+ "/" + date.getValue().toString())){
								logger.info("{} contenu dans {}...", pointOfEntry.getUuid(), valeurEntiere);
								String[] array = valeurEntiere.split(":", 2);
								int numberPresent = Integer.parseInt(array[0]);
								logger.debug("Maintenant on a {}...", numberPresent);
								numberPresent+=numberOfNotExCase;
								String Newchaine = numberPresent + ":" + pointOfEntry.getUuid() + "/" + date.getValue().toString() + ";";
								logger.debug("Nous remplacons {} par {} ...", valeurEntiere, Newchaine);
								SecondechaineDeux += Newchaine;
								adding = false;
							}
							else{
								SecondechaineDeux+=valeurEntiere + ";";
							}
						}
						if(adding == true){
							SecondechaineDeux+=numberOfNotExCase + ":" + pointOfEntry.getUuid() + "/" + date.getValue().toString() + ";";
						}
						String ChaineFinal = SecondechaineDeux;
						if (!StringUtils.isBlank(ChaineFinal)){
							UserToSave.setNumberofnonexaminatedpeople(ChaineFinal);
							tosave = true;
						}
					}else{
						logger.info("Enregidtrement de la data {} ...", chaineDeux);
						UserToSave.setNumberofnonexaminatedpeople(chaineDeux);
						tosave = true;
					}
				}
				
				if (tosave == true){
					userFacade.saveUser(UserToSave);
				}
				
			}
			
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
