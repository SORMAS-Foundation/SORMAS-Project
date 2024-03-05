package de.symeda.sormas.ui.immunization;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.immunization.components.layout.directory.FilterFormLayout;
import de.symeda.sormas.ui.immunization.components.layout.directory.ImmunizationDataLayout;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class ImmunizationsView extends AbstractView {

	public static final String VIEW_NAME = "immunizations";

	private final ImmunizationCriteria criteria;

	private FilterFormLayout filterFormLayout;
	private final ImmunizationDataLayout immunizationDataLayout;

	// Filters
	private Label relevanceStatusInfoLabel;
	private ComboBox relevanceStatusFilter;

	public ImmunizationsView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(ImmunizationsView.class).get(ImmunizationCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}
		immunizationDataLayout = new ImmunizationDataLayout(criteria);

		final VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(immunizationDataLayout);

		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(immunizationDataLayout, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		if (UiUtil.permitted(UserRight.IMMUNIZATION_CREATE)) {
			final ExpandableButton createButton =
				new ExpandableButton(Captions.immunizationNewImmunization).expand(e -> ControllerProvider.getImmunizationController().create());
			addHeaderComponent(createButton);
		}
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

	private void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}

		filterFormLayout.setValue(criteria);

		applyingCriteria = false;
	}

	private FilterFormLayout createFilterBar() {
		filterFormLayout = new FilterFormLayout();

		filterFormLayout.addResetHandler(clickEvent -> {
			ViewModelProviders.of(ImmunizationsView.class).remove(ImmunizationCriteria.class);
			navigateTo(null, true);
		});

		filterFormLayout.addApplyHandler(clickEvent -> {
			immunizationDataLayout.refreshGrid();
		});

		return filterFormLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);

		// Show active/archived/all dropdown
		if (UiUtil.permitted(UserRight.IMMUNIZATION_VIEW)) {

			if (UiUtil.enabled(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.IMMUNIZATION)) {

				int daysAfterTravelEntryGetsArchived = FacadeProvider.getFeatureConfigurationFacade()
					.getProperty(
						FeatureType.AUTOMATIC_ARCHIVING,
						DeletableEntityType.IMMUNIZATION,
						FeatureTypeProperty.THRESHOLD_IN_DAYS,
						Integer.class);
				if (daysAfterTravelEntryGetsArchived > 0) {
					relevanceStatusInfoLabel = new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " "
							+ String.format(I18nProperties.getString(Strings.infoArchivedTravelEntries), daysAfterTravelEntryGetsArchived),
						ContentMode.HTML);
					relevanceStatusInfoLabel.setVisible(false);
					relevanceStatusInfoLabel.addStyleName(CssStyles.LABEL_VERTICAL_ALIGN_SUPER);
					actionButtonsLayout.addComponent(relevanceStatusInfoLabel);
					actionButtonsLayout.setComponentAlignment(relevanceStatusInfoLabel, Alignment.MIDDLE_RIGHT);
				}
			}
			relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
			relevanceStatusFilter.setId("relevanceStatus");
			relevanceStatusFilter.setWidth(260, Unit.PIXELS);
			relevanceStatusFilter.setNullSelectionAllowed(false);
			relevanceStatusFilter.setTextInputAllowed(false);
			relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
			relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.immunizationActiveImmunizations));
			relevanceStatusFilter
				.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.immunizationArchivedImmunizations));
			relevanceStatusFilter.setItemCaption(
				EntityRelevanceStatus.ACTIVE_AND_ARCHIVED,
				I18nProperties.getCaption(Captions.immunizationAllActiveAndArchivedImmunizations));
			relevanceStatusFilter.setCaption("");

			if (UiUtil.permitted(UserRight.IMMUNIZATION_DELETE)) {
				relevanceStatusFilter
					.setItemCaption(EntityRelevanceStatus.DELETED, I18nProperties.getCaption(Captions.immunizationDeletedImmunizations));
			} else {
				relevanceStatusFilter.removeItem(EntityRelevanceStatus.DELETED);
			}

			relevanceStatusFilter.addValueChangeListener(e -> {
				if (relevanceStatusInfoLabel != null) {
					relevanceStatusInfoLabel.setVisible(EntityRelevanceStatus.ARCHIVED.equals(e.getProperty().getValue()));
				}
				criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
				navigateTo(criteria);
			});
			actionButtonsLayout.addComponent(relevanceStatusFilter);
		}

		if (actionButtonsLayout.getComponentCount() > 0) {
			statusFilterLayout.addComponent(actionButtonsLayout);
			statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
			statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);
		}

		return statusFilterLayout;
	}
}
