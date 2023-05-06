package com.cinoteck.application.views.campaign;

import java.util.List;

import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;

@PageTitle("Edit Campaign")
@Route(value = "/data", layout = MainLayout.class)
public class CampaignForm extends FormLayout {

	Binder<CampaignDto> binder = new BeanValidationBinder<>(CampaignDto.class);
	List<CampaignReferenceDto> campaignName;
	List<CampaignReferenceDto> campaignRound;
	List<CampaignReferenceDto> campaignStartDate;
	List<CampaignReferenceDto> campaignEndDate;
	List<CampaignReferenceDto> campaignDescription;

	TextField campaignNameField = new TextField("Campaign Name");
	ComboBox<CampaignReferenceDto> campaignRoundField = new ComboBox("Round");
	DatePicker campaignStartDateField = new DatePicker("Start Date");
	DatePicker campaignEndDateField = new DatePicker("End Date");
	TextField campaignDescriptionField = new TextField("Description");

	public CampaignForm(List<CampaignReferenceDto> campaignName, List<CampaignReferenceDto> campaignRound,
			List<CampaignReferenceDto> campaignStartDate, List<CampaignReferenceDto> campaignEndDate,
			List<CampaignReferenceDto> campaignDescription) {

		HorizontalLayout hor = new HorizontalLayout();
		Icon vaadinIcon = new Icon("lumo", "cross");
		
//		hor.setJustifyContentMode(JustifyContentMode.END);
		hor.setWidthFull();
		hor.add(vaadinIcon);
		hor.setHeight("5px");
//		this.setColspan(hor, 2);
//		vaadinIcon.addClickListener(event -> fireEvent(new CloseEvent(this)));
		add(hor);
		// Configure what is passed to the fields here
		configureFields();

	}

	private void configureFields() {
		FormLayout campaignForm = new FormLayout();

		H2 bInfo = new H2("Campaign Basics");

		binder.forField(campaignNameField).asRequired("Campaign Name is Required").bind(CampaignDto::getName,
				CampaignDto::setName);

		campaignRoundField.setItems();
		binder.forField(campaignRoundField).asRequired("Campaign Round is Required").bind(CampaignDto.ROUND);
		campaignRoundField.setItemLabelGenerator(CampaignReferenceDto::getCaption);
		campaignRoundField.addValueChangeListener(e -> {

		});

		binder.forField(campaignStartDateField).bind(CampaignDto.START_DATE);
		binder.forField(campaignEndDateField).bind(CampaignDto.END_DATE);

		binder.forField(campaignDescriptionField).asRequired("Campaign Description is Required")
				.bind(CampaignDto::getDescription, CampaignDto::setDescription);

		add(bInfo, campaignNameField, campaignRoundField, campaignStartDateField, campaignEndDateField,
				campaignDescriptionField);
	}

	public void setCampaign(CampaignDto user) {
		binder.setBean(user);
	}

	// Events
	public static abstract class CampaignFormEvent extends ComponentEvent<CampaignForm> {
		private CampaignDto campaign;

		protected CampaignFormEvent(CampaignForm source, CampaignDto campaign) {
			super(source, false);
			this.campaign = campaign;
		}

		public CampaignDto getCampaign() {
			return campaign;
		}
	}

	public static class SaveEvent extends CampaignFormEvent {
		SaveEvent(CampaignForm source, CampaignDto campaign) {
			super(source, campaign);
		}
	}

	public static class DeleteEvent extends CampaignFormEvent {
		DeleteEvent(CampaignForm source, CampaignDto contact) {
			super(source, contact);
		}

	}

	public static class CloseEvent extends CampaignFormEvent {
		CloseEvent(CampaignForm source) {
			super(source, null);
		}
	}

	public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
		return addListener(DeleteEvent.class, listener);
	}

	public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
		return addListener(SaveEvent.class, listener);
	}

	public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
		return addListener(CloseEvent.class, listener);
	}

}
