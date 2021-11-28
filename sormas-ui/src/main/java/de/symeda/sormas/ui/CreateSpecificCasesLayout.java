package de.symeda.sormas.ui;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.FacadeProvider;
import com.vaadin.ui.HorizontalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class CreateSpecificCasesLayout extends VerticalLayout {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	public CreateSpecificCasesLayout(
		Runnable confirmCallback,
		Runnable closePopupCallback,
		DateField repDate,
		ComboBox pointOfEntry,
		TextField peolple,
		TextField examinatedpeople,
		String confirmCaption) {
		
		setMargin(true);
		setSpacing(false);
		setWidth(100, Unit.PERCENTAGE);

		ConfirmationComponent confirmationComponent = new ConfirmationComponent(false) {

			@Override
			protected void onConfirm() {
				confirmCallback.run();
			}

			@Override
			protected void onCancel() {
				closePopupCallback.run();
			}
		};
		final DistrictReferenceDto currentUserDistrict = UserProvider.getCurrent().getUser().getDistrict();
		final PointOfEntryReferenceDto currentUserPoE = UserProvider.getCurrent().getUser().getPointOfEntry();
		List<PointOfEntryReferenceDto> POEs = FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(
			null, false);
		if (currentUserDistrict != null){
			POEs = FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(
			currentUserDistrict.getUuid(), false);
		}
		
		if (currentUserPoE != null) {
			pointOfEntry.setItems(currentUserPoE);
		}
		else{
			logger.info(
				"Pas de point dentree direct pour le user, "+
				"recherche si un district est definie");
			if (POEs != null){
				pointOfEntry.setItems(POEs);
				logger.info("Mise a jour via le distrtict effectue");
			}
		}
		
		HorizontalLayout l1 = buildFirstLayout(repDate, pointOfEntry);
		HorizontalLayout l2 = buildSecondLayout(peolple, examinatedpeople);
		// peolple.setText("0");
		// examinatedpeople.setText("0");
		addComponent(l1);
		addComponent(l2);
		setComponentAlignment(l1, Alignment.TOP_CENTER);
		setComponentAlignment(l2, Alignment.TOP_CENTER);

		confirmationComponent.getConfirmButton().setCaption(confirmCaption);
		confirmationComponent.getCancelButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
		confirmationComponent.setMargin(true);
		addComponent(confirmationComponent);
		setComponentAlignment(confirmationComponent, Alignment.TOP_CENTER);
	}

	private HorizontalLayout buildFirstLayout(DateField repDate, ComboBox pointOfEntry) {
		HorizontalLayout firstLayout = new HorizontalLayout();
		firstLayout.setMargin(false);

		
		repDate.setWidth(60, Unit.PERCENTAGE);
		repDate.setRequiredIndicatorVisible(true);
		firstLayout.addComponent(repDate);

		pointOfEntry.setWidth(60, Unit.PERCENTAGE);
		pointOfEntry.setRequiredIndicatorVisible(true);
		HorizontalLayout secondLayout = new HorizontalLayout();
		secondLayout.setMargin(false);
		secondLayout.addComponent(pointOfEntry);

		firstLayout.addComponent(secondLayout);

		return firstLayout;
	}

	private HorizontalLayout buildSecondLayout(TextField peolple, TextField examinatedpeople) {
		HorizontalLayout tirhtLayout = new HorizontalLayout();
		tirhtLayout.setMargin(false);

		peolple.setWidth(60, Unit.PERCENTAGE);
		peolple.setRequiredIndicatorVisible(true);
		tirhtLayout.addComponent(peolple);

		HorizontalLayout fourthLayout = new HorizontalLayout();
		fourthLayout.setMargin(false);
		examinatedpeople.setWidth(60, Unit.PERCENTAGE);
		examinatedpeople.setRequiredIndicatorVisible(true);
		fourthLayout.addComponent(examinatedpeople);

		tirhtLayout.addComponent(fourthLayout);

		return tirhtLayout;
	}
}