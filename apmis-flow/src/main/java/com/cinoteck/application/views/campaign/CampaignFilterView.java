package com.cinoteck.application.views.campaign;

//import com.cinoteck.application.views.campaign.MonthlyExpense.DailyExpenses;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;


public class CampaignFilterView extends HorizontalLayout {
	private final TextField filterr = new TextField();

	private final Button addNewBtnn = new Button();

	private TextField searchField = new TextField();

	public CampaignFilterView() {

		
		searchField.setLabel("Search Campaign");
		searchField.setPlaceholder("Search");
		searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		searchField.setValueChangeMode(ValueChangeMode.EAGER);

		Select<String> campaignStatus = new Select<>();
		campaignStatus.setLabel("Campaign Status");
		campaignStatus.setItems("", "Active", "Archived", "Closed");

		Button validateForms = new Button("Validate Forms", new Icon(VaadinIcon.CHECK_CIRCLE));
		Button addNewForm = new Button("Add New Forms", new Icon(VaadinIcon.PLUS_CIRCLE));
		
		HorizontalLayout campaignAction = new HorizontalLayout();
		campaignAction.setClassName("actions");
		campaignAction.add(searchField, campaignStatus, validateForms, addNewForm);


		add(campaignAction);
	}

}