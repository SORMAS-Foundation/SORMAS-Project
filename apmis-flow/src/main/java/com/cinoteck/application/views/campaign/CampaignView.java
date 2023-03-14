package com.cinoteck.application.views.campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.cinoteck.application.views.MainLayout;
//import com.cinoteck.application.views.campaign.MonthlyExpense.DailyExpenses;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("All Campaigns")
@Route(value = "campaign", layout = MainLayout.class)
public class CampaignView extends VerticalLayout {

	CampaignFilterView campFilter = new CampaignFilterView();
	public CampaignView() {


		add(campFilter);
	}

}
