package de.symeda.sormas.ui.task;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class TaskGridFilterForm extends AbstractFilterForm<TaskCriteria> {

	private static final long serialVersionUID = -8661345403078183133L;

	protected TaskGridFilterForm() {
		super(TaskCriteria.class, TaskIndexDto.I18N_PREFIX);
		getContent().removeComponent(APPLY_BUTTON_ID);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			TaskIndexDto.TASK_CONTEXT,
			TaskIndexDto.TASK_STATUS,
			TaskIndexDto.REGION,
			TaskIndexDto.DISTRICT,
			TaskCriteria.FREE_TEXT };
	}

	@Override
	protected void addFields() {
		addField(FieldConfiguration.pixelSized(TaskIndexDto.TASK_CONTEXT, 140));
		addField(FieldConfiguration.pixelSized(TaskIndexDto.TASK_STATUS, 140));

		final UserDto user = UserProvider.getCurrent().getUser();
		if (user.getRegion() == null && user.getDistrict() == null) {
			final ComboBox regionField = addField(FieldConfiguration.pixelSized(TaskIndexDto.REGION, 200));
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

			final ComboBox districtField = addDistrictField();
			districtField.setEnabled(false);

			regionField.addValueChangeListener(e -> {
				RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
				if (StringUtils.isNotBlank(region.getUuid())) {
					districtField.setEnabled(true);
					FieldHelper.updateItems(districtField, FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
				} else {
					districtField.setEnabled(false);
				}
			});
		} else if (user.getDistrict() == null) {
			FieldHelper.updateItems(addDistrictField(), FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
		}

		addField(
			FieldConfiguration
				.withCaptionAndPixelSized(TaskCriteria.FREE_TEXT, I18nProperties.getString(Strings.promptTaskContextEntitySearchField), 200));
	}

	private ComboBox addDistrictField() {
		final ComboBox districtField = addField(FieldConfiguration.pixelSized(TaskIndexDto.DISTRICT, 200));
		districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
		return districtField;
	}
}
