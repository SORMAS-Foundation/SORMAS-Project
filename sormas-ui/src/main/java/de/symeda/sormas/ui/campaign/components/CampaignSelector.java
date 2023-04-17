package de.symeda.sormas.ui.campaign.components;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.HasValue;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class CampaignSelector extends HorizontalLayout {

	public ComboBox<CampaignReferenceDto> campaignCombo;
	private ComboBox<Object> yearFilter;
	boolean isCampaingChanged = false;

	public CampaignSelector() {
		
		if(UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria") != null && !UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria").toString().contains("campaign=")) {
			UI.getCurrent().getSession().getCurrent().setAttribute("lastcriteria", null);
		}
		
		setMargin(false);
		setSpacing(false);

		Label campaignLabel = new Label(I18nProperties.getCaption(Captions.Campaign));
		campaignLabel.addStyleName("v-caption");
		campaignLabel.addStyleName(CssStyles.HSPACE_RIGHT_4);
		

		campaignCombo = new ComboBox<>(" ");
		
		CampaignYearSelector();
		
		
		List<CampaignReferenceDto> campaigns = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
		campaigns.removeIf(ee -> (!ee.getCampaignYear().equals(yearFilter.getValue())));
		campaignCombo.setItems(campaigns);
		
		
		campaignCombo.setEmptySelectionCaption(I18nProperties.getCaption(Captions.campaignAllCampaigns));
		
		
		
		final CampaignReferenceDto lastStartedCampaign = FacadeProvider.getCampaignFacade().getLastStartedCampaign();
	
		System.out.println("fghjkl; "+UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria"));
		
			
		if(UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria") != null) {
			if(UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria").toString().contains("campaign=")) {
			String[] sessionx = UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria").toString().split("campaign=");
			if(sessionx.length > 0) {
				UI.getCurrent().getSession().getCurrent().setAttribute("isCampaingChanged", true);
				isCampaingChanged = true;
				
				String finaldex = sessionx[1].toString();
				if(sessionx[1].toString().contains("&")) {
					String[] dex = sessionx[1].toString().split("&");
					finaldex = dex[0].toString();
				}
				
				
				CampaignReferenceDto refDto = FacadeProvider.getCampaignFacade().getReferenceByUuid(finaldex);
				campaignCombo.setValue(refDto);
//				
//				
//				List<CampaignReferenceDto> stf = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
//				stf.removeIf(ee -> (!ee.getCampaignYear().equals(yearFilter.getValue())));
//				
//				yearFilter.setItems(stf.stream().distinct().collect(Collectors.toList()));
//				
//				
//				yearFilter.setValue(refDto.getCampaignYear());
				
			}
		}
			
		}
		
		if (lastStartedCampaign != null && !isCampaingChanged) {
				campaignCombo.setValue(lastStartedCampaign);
			}
		
		CssStyles.style(campaignCombo, CssStyles.SOFT_REQUIRED);
		//campaignCombo.setSpacing(true);
		addComponent(campaignCombo);
		this.setSpacing(true);
		this.setWidthFull();
	}
	
	public void CampaignYearSelector() {
		setMargin(false);
		setSpacing(false);

		Label yearLabel = new Label("Year");//I18nProperties.getCaption(Captions.Campaign));
		yearLabel.addStyleName("v-caption");
		yearLabel.addStyleName(CssStyles.HSPACE_RIGHT_4);
		addComponent(yearLabel);
		setComponentAlignment(yearLabel, Alignment.MIDDLE_CENTER);

		yearFilter = new ComboBox<>(" ");
		
		List<CampaignReferenceDto> campaigns = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
		boolean isCampaignChangedx_ = false;
		
		if(UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria") != null && UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria").toString().contains("campaign=")) {
			
			isCampaignChangedx_ = true;
		}
		
		
		List<String> camYearList = new ArrayList<>();
		for(CampaignReferenceDto camdreg :  campaigns) {
			camYearList.add(camdreg.getCampaignYear());
		}
		yearFilter.setItems(camYearList.stream().distinct().collect(Collectors.toList()));
		yearFilter.setEmptySelectionAllowed(false);
		yearFilter.addContextClickListener(ee -> {
			
			System.out.println("=================================");
		});
		yearFilter.addValueChangeListener(e -> {
			boolean isCampaignChangedx = false;
			
			if(UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria") != null 
					&& UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria").toString().contains("campaign=")
					&& e.getValue() == e.getOldValue()
					) {
				
				isCampaignChangedx = true;
			}
			System.out.println(e.getValue() + "==000000 = "+!isCampaignChangedx);
			System.out.println(e.getOldValue() + "==000000 = "+!isCampaignChangedx);
		//not working well when already in session campaign=
			
			if (!isCampaignChangedx) {
			
			campaignCombo.clear();
			
			List<CampaignReferenceDto> stf = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
			stf.removeIf(ee -> (!ee.getCampaignYear().equals(yearFilter.getValue())));
			
			campaignCombo.setItems(stf);
			
			
				campaignCombo.setSelectedItem(stf.get(0));
			}
			});
		
		
		final CampaignReferenceDto lastStartedCampaign = FacadeProvider.getCampaignFacade().getLastStartedCampaign();
	
		System.out.println("111111 = "+isCampaignChangedx_);
		if (lastStartedCampaign != null && !isCampaignChangedx_) {
			System.out.println("2222222");
			yearFilter.setValue(lastStartedCampaign.getCampaignYear());
		} else if(isCampaignChangedx_) {
			System.out.println("33333333");
			String[] sessionx = UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria").toString().split("campaign=");
			if(sessionx.length > 0) {
				System.out.println("setting year to cam year" + sessionx[1].toString());
				String finaldex = sessionx[1].toString();
				if(sessionx[1].toString().contains("&")) {
					String[] dex = sessionx[1].toString().split("&");
					finaldex = dex[0].toString();
				}

				CampaignReferenceDto refDto = FacadeProvider.getCampaignFacade().getReferenceByUuid(finaldex);
				
				campaignCombo.clear();
				
				List<CampaignReferenceDto> stf = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
				stf.removeIf(ee -> (!ee.getCampaignYear().equals(refDto.getCampaignYear())));
				
				campaignCombo.setItems(stf);
				
			System.out.println("setting year to cam year" + refDto.getCampaignYear());
			
			
			yearFilter.setValue(refDto.getCampaignYear());
			campaignCombo.setValue(refDto);
			}
		}
		
	
		CssStyles.style(yearFilter, CssStyles.SOFT_REQUIRED);
		addComponent(yearFilter);
	}

	public CampaignReferenceDto getValue() {
		return campaignCombo.getValue();
	}

	public void setValue(CampaignReferenceDto value) {
		campaignCombo.setValue(value);
	}

	public void addValueChangeListener(HasValue.ValueChangeListener listener) {
		campaignCombo.addValueChangeListener(listener);
	}
}
