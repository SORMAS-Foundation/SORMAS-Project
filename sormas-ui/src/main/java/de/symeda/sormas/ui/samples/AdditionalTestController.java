package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.AdditionalTestFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class AdditionalTestController {

	private AdditionalTestFacade facade = FacadeProvider.getAdditionalTestFacade();

	public AdditionalTestController() {

	}

	public List<AdditionalTestDto> getAdditionalTestsBySample(String sampleUuid) {
		return facade.getAllBySample(sampleUuid);
	}

	public void openCreateComponent(String sampleUuid, Runnable callback) {
		AdditionalTestForm form = new AdditionalTestForm(FacadeProvider.getSampleFacade().getSampleByUuid(sampleUuid), true);
		form.setValue(AdditionalTestDto.build(FacadeProvider.getSampleFacade().getReferenceByUuid(sampleUuid)));
		final CommitDiscardWrapperComponent<AdditionalTestForm> component =
			new CommitDiscardWrapperComponent<>(form, UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_CREATE), form.getFieldGroup());

		Window window = VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingCreateAdditionalTest));
		window.setWidth(form.getWidth() + 90, Unit.PIXELS);
		window.setHeight(80, Unit.PERCENTAGE);

		component.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					FacadeProvider.getAdditionalTestFacade().saveAdditionalTest(form.getValue());
					Notification.show(I18nProperties.getString(Strings.messageAdditionalTestSaved), Type.TRAY_NOTIFICATION);
					if (callback != null) {
						callback.run();
					}
				}
			}
		});
	}

	public void openEditComponent(AdditionalTestDto dto, Runnable callback) {

		AdditionalTestDto newDto = FacadeProvider.getAdditionalTestFacade().getByUuid(dto.getUuid());
		AdditionalTestForm form = new AdditionalTestForm(FacadeProvider.getSampleFacade().getSampleByUuid(dto.getSample().getUuid()), false);
		form.setValue(newDto);
		final CommitDiscardWrapperComponent<AdditionalTestForm> component =
			new CommitDiscardWrapperComponent<>(form, UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_EDIT), form.getFieldGroup());

		Window window = VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingEditAdditionalTest));
		window.setWidth(form.getWidth() + 90, Unit.PIXELS);
		window.setHeight(80, Unit.PERCENTAGE);

		component.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					FacadeProvider.getAdditionalTestFacade().saveAdditionalTest(form.getValue());
					Notification.show(I18nProperties.getString(Strings.messageAdditionalTestSaved), Type.TRAY_NOTIFICATION);
					if (callback != null) {
						callback.run();
					}
				}
			}
		});

		if (UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_DELETE)) {
			component.addDeleteListener(() -> {
				FacadeProvider.getAdditionalTestFacade().deleteAdditionalTest(dto.getUuid());
				window.close();
				Notification.show(I18nProperties.getString(Strings.messageAdditionalTestDeleted), Type.TRAY_NOTIFICATION);
				if (callback != null) {
					callback.run();
				}
			}, I18nProperties.getString(Strings.entityAdditionalTest));
		}
	}
}
