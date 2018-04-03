package de.symeda.sormas.ui.caze;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.contact.ContactGrid;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class CaseContactsView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = "cases/contacts";

	private ContactGrid grid;    
	private ComboBox classificationFilter;
	private ComboBox districtFilter;
	private ComboBox officerFilter;
	private Button newButton;
	private VerticalLayout gridLayout;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	public CaseContactsView() {
		super(VIEW_NAME);
		setSizeFull();
		
		grid = new ContactGrid();

		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		grid.getContainer().addItemSetChangeListener(e -> {
			updateActiveStatusButtonCaption();
		});

		setSubComponent(gridLayout);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth(100, Unit.PERCENTAGE);

		classificationFilter = new ComboBox();
		classificationFilter.setWidth(240, Unit.PIXELS);
		classificationFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_CLASSIFICATION));
		classificationFilter.addValueChangeListener(e -> {
			ContactClassification classification = (ContactClassification) e.getProperty().getValue();
			grid.setClassificationFilter(classification);
		});
		topLayout.addComponent(classificationFilter);

		districtFilter = new ComboBox();
		districtFilter.setWidth(240, Unit.PIXELS);
		districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_DISTRICT_UUID));
		districtFilter.addValueChangeListener(e -> {
			DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
			grid.setDistrictFilter(district != null ? district.getUuid() : null);
		});
		topLayout.addComponent(districtFilter);

		officerFilter = new ComboBox();
		officerFilter.setWidth(240, Unit.PIXELS);
		officerFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_OFFICER_UUID));
		officerFilter.addValueChangeListener(e -> {
			UserReferenceDto officer = (UserReferenceDto) e.getProperty().getValue();
			grid.setContactOfficerFilter(officer != null ? officer.getUuid() : null);
		});
		topLayout.addComponent(officerFilter);

		topLayout.setExpandRatio(officerFilter, 1);
		return topLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setWidth("100%");
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		Button statusAll = new Button("All", e -> processStatusChange(null, e.getButton()));
		CssStyles.style(statusAll, ValoTheme.BUTTON_LINK, CssStyles.LINK_HIGHLIGHTED);
		statusAll.setCaptionAsHtml(true);
		statusFilterLayout.addComponent(statusAll);
		statusButtons.put(statusAll, "All");

		for (ContactStatus status : ContactStatus.values()) {
			Button statusButton = new Button(status.toString(), e -> {
				processStatusChange(status, e.getButton());
			});
			CssStyles.style(statusButton, ValoTheme.BUTTON_LINK, CssStyles.LINK_HIGHLIGHTED, CssStyles.LINK_HIGHLIGHTED_LIGHT);
			statusButton.setCaptionAsHtml(true);
			statusFilterLayout.addComponent(statusButton);
			statusButtons.put(statusButton, status.toString());
		}
		statusFilterLayout.setExpandRatio(statusFilterLayout.getComponent(statusFilterLayout.getComponentCount()-1), 1);

		// Bulk operation dropdown
		if (LoginHelper.hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			statusFilterLayout.setWidth(100, Unit.PERCENTAGE);

			MenuBar bulkOperationsDropdown = new MenuBar();	
			MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem("Bulk Actions", null);

			Command changeCommand = selectedItem -> {
				ControllerProvider.getContactController().showBulkContactDataEditComponent(grid.getSelectedRows(), getCaseRef().getUuid());
			};
			bulkOperationsItem.addItem("Edit...", FontAwesome.ELLIPSIS_H, changeCommand);

			Command cancelFollowUpCommand = selectedItem -> {
				ControllerProvider.getContactController().cancelFollowUpOfAllSelectedItems(grid.getSelectedRows(), new Runnable() {
					public void run() {
						grid.deselectAll();
						grid.reload();
					}
				});
			};
			bulkOperationsItem.addItem("Cancel follow-up", FontAwesome.TIMES, cancelFollowUpCommand);

			Command lostToFollowUpCommand = selectedItem -> {
				ControllerProvider.getContactController().setAllSelectedItemsToLostToFollowUp(grid.getSelectedRows(), new Runnable() {
					public void run() {
						grid.deselectAll();
						grid.reload();
					}
				});
			};
			bulkOperationsItem.addItem("Set to lost to follow-up", FontAwesome.UNLINK, lostToFollowUpCommand);

			Command deleteCommand = selectedItem -> {
				ControllerProvider.getContactController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
					public void run() {
						grid.deselectAll();
						grid.reload();
					}
				});
			};
			bulkOperationsItem.addItem("Delete", FontAwesome.TRASH, deleteCommand);

			statusFilterLayout.addComponent(bulkOperationsDropdown);
			statusFilterLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);
			statusFilterLayout.setExpandRatio(bulkOperationsDropdown, 1);
		}

		if (LoginHelper.hasUserRight(UserRight.CONTACT_EXPORT)) {
			Button exportButton = new Button("Export");
			exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			exportButton.setIcon(FontAwesome.DOWNLOAD);

			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(grid.getContainerDataSource(), new ArrayList<>(grid.getColumns()), "sormas_contacts", "sormas_contacts_" + DateHelper.formatDateForExport(new Date()) + ".csv");
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);

			statusFilterLayout.addComponent(exportButton);
			statusFilterLayout.setComponentAlignment(exportButton, Alignment.MIDDLE_RIGHT);
			if (!LoginHelper.hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				statusFilterLayout.setExpandRatio(exportButton, 1);
			}
		}

		if (LoginHelper.hasUserRight(UserRight.CONTACT_CREATE)) {
			newButton = new Button("New contact");
			newButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			newButton.setIcon(FontAwesome.PLUS_CIRCLE);
			newButton.addClickListener(e -> ControllerProvider.getContactController().create(this.getCaseRef()));
			statusFilterLayout.addComponent(newButton);
			statusFilterLayout.setComponentAlignment(newButton, Alignment.MIDDLE_RIGHT);
		}

		statusFilterLayout.addStyleName("top-bar");
		activeStatusButton = statusAll;
		return statusFilterLayout;
	}

	private void update() {
		grid.setCaseFilter(getCaseRef());

		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());

		classificationFilter.removeAllItems();
		classificationFilter.addItems((Object[]) ContactClassification.values());

		districtFilter.removeAllItems();
		districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(caseDto.getRegion().getUuid()));

		officerFilter.removeAllItems();
		officerFilter.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(caseDto.getRegion(), UserRole.CONTACT_OFFICER));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		update();
		grid.reload();
		updateActiveStatusButtonCaption();
	}

	private void updateActiveStatusButtonCaption() {
		if (activeStatusButton != null) {
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getContainer().size())));
		}
	}

	private void processStatusChange(ContactStatus contactStatus, Button button) {
		grid.setStatusFilter(contactStatus);
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.LINK_HIGHLIGHTED_LIGHT);
			b.setCaption(statusButtons.get(b));
		});
		CssStyles.removeStyles(button, CssStyles.LINK_HIGHLIGHTED_LIGHT);
		activeStatusButton = button;
		updateActiveStatusButtonCaption();
	}

}
