package de.symeda.sormas.ui.labmessage;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageFetchResult;
import de.symeda.sormas.api.labmessage.NewMessagesState;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.samples.SamplesView;
import de.symeda.sormas.ui.samples.SamplesViewType;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class LabMessagesView extends AbstractView {

	public static final String VIEW_NAME = SamplesView.VIEW_NAME + "/labMessages";

	private final LabMessagesGridComponent listComponent;

	public LabMessagesView() {

		super(VIEW_NAME);

		if (!ViewModelProviders.of(LabMessagesView.class).has(LabMessageCriteria.class)) {
			// init default filter
			LabMessageCriteria criteria = new LabMessageCriteria();
			ViewModelProviders.of(LabMessagesView.class).get(LabMessageCriteria.class, criteria);
		}

		listComponent = new LabMessagesGridComponent(getViewTitleLabel(), this);
		addComponent(listComponent);

		OptionGroup samplesViewSwitcher = new OptionGroup();
		samplesViewSwitcher.setId("samplesViewSwitcher");
		CssStyles.style(samplesViewSwitcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		for (SamplesViewType type : SamplesViewType.values()) {
			samplesViewSwitcher.addItem(type);
			samplesViewSwitcher.setItemCaption(type, I18nProperties.getEnumCaption(type));
		}

		samplesViewSwitcher.setValue(SamplesViewType.LAB_MESSAGES);
		samplesViewSwitcher.addValueChangeListener(e -> SormasUI.get().getNavigator().navigateTo(SamplesView.VIEW_NAME));
		addHeaderComponent(samplesViewSwitcher);

		addHeaderComponent(ButtonHelper.createIconButton(Captions.labMessageFetch, VaadinIcons.REFRESH, e -> {
			LabMessageFetchResult fetchResult = FacadeProvider.getLabMessageFacade().fetchAndSaveExternalLabMessages();
			if (!fetchResult.isSuccess()) {
				VaadinUiUtil.showWarningPopup(fetchResult.getError());
			} else if (NewMessagesState.NO_NEW_MESSAGES.equals(fetchResult.getNewMessageState())) {
				VaadinUiUtil.showWarningPopup(I18nProperties.getCaption(Captions.labMessageNoNewMessages));
			} else {
				listComponent.getGrid().reload();
			}
		}, ValoTheme.BUTTON_PRIMARY));
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		listComponent.reload(event);
	}
}
