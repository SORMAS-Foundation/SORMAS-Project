package de.symeda.sormas.api.environment;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.SensitiveData;

@DependingOnFeatureType(featureType = FeatureType.ENVIRONMENT_MANAGEMENT)
public class EnvironmentReferenceDto extends ReferenceDto {

	@SensitiveData
	private String environmentName;

	private EventReferenceDto event;

	public EnvironmentReferenceDto() {

	}

	public EnvironmentReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public EnvironmentReferenceDto(String uuid, String environmentName) {
		setUuid(uuid);
		this.environmentName = environmentName;
	}

	/*
	 * public EnvironmentReferenceDto(String uuid, String environmentName) {
	 * setUuid(uuid);
	 * this.environmentName = environmentName;
	 * }
	 */

	@Override
	public String getCaption() {
		return environmentName.trim().length() > 0 ? environmentName : DataHelper.getShortUuid(getUuid());
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public EventReferenceDto event(EventReferenceDto event) {
		this.event = event;
		return event;
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public void setEvent(EventReferenceDto event) {
		this.event = event;
	}
}
