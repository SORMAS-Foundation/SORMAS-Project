package de.symeda.sormas.api.personaldata;

import java.util.Objects;

import de.symeda.sormas.api.EntityDto;

public class CreatableEntityDto {

	private Class<? extends EntityDto> clazz;
	private String i18nName;

	public Class<? extends EntityDto> getClazz() {
		return clazz;
	}

	public void setClazz(Class<? extends EntityDto> clazz) {
		this.clazz = clazz;
	}

	public String getI18nName() {
		return i18nName;
	}

	public void setI18nName(String i18nName) {
		this.i18nName = i18nName;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		CreatableEntityDto that = (CreatableEntityDto) o;
		return Objects.equals(clazz, that.clazz) && Objects.equals(i18nName, that.i18nName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clazz, i18nName);
	}
}
