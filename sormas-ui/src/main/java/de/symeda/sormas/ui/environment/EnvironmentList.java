package de.symeda.sormas.ui.environment;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.PaginationList;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EnvironmentList extends PaginationList<EnvironmentIndexDto> {

	private final EnvironmentCriteria criteria;
	private final Label noEnvironmentLabel;
	private final boolean isEditAllowed;
	private final EventReferenceDto event;

	public EnvironmentList(EventReferenceDto event, boolean isEditAllowed) {
		super(5);
		this.event = event;
		this.isEditAllowed = isEditAllowed;
		criteria = new EnvironmentCriteria();
		criteria.event(event);
		noEnvironmentLabel =
			new Label(String.format(I18nProperties.getCaption(Captions.environmentNoEnvs), DataHelper.getShortUuid(event.getUuid())));
	}

	@Override
	public void reload() {
		List<EnvironmentIndexDto> envs = FacadeProvider.getEnvironmentFacade().getEnvironmentsByEvent(criteria);
		setEntries(envs);
		if (!envs.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(noEnvironmentLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		List<EnvironmentIndexDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			EnvironmentIndexDto environmentIndex = displayedEntries.get(i);
			EnvironmentListEntry listEntry = new EnvironmentListEntry(environmentIndex);

			if (UiUtil.permitted(UserRight.ENVIRONMENT_CREATE)) {
				listEntry.addUnlinkEnvironmentListener(i, (Button.ClickListener) clickEvent -> {
					VaadinUiUtil.showConfirmationPopup(
						I18nProperties.getString(Strings.headingUnlinkEnvironmentFromEvent),
						new Label(I18nProperties.getString(Strings.confirmationUnlinkEnvironmentFromEvent)),
						I18nProperties.getString(Strings.yes),
						I18nProperties.getString(Strings.no),
						480,
						confirmed -> {
							if (confirmed) {
								ControllerProvider.getEnvironmentController().unlinkEnvironment(environmentIndex, event.getUuid());
								reload();
							}
						});
				});
			}

			if (UiUtil.permitted(UserRight.ENVIRONMENT_EDIT)) {
				listEntry.addActionButton(
					String.valueOf(i),
					(Button.ClickListener) event -> ControllerProvider.getEnvironmentController().navigateToData(environmentIndex.getUuid(), false),
					hasEditOrDeleteRights());
			}
			listEntry.setEnabled(isEditAllowed);
			listLayout.addComponent(listEntry);
		}
	}

	private boolean hasEditOrDeleteRights() {
		return UiUtil.permitted(UserRight.ENVIRONMENT_EDIT) || UiUtil.permitted(UserRight.ENVIRONMENT_DELETE);
	}
}
