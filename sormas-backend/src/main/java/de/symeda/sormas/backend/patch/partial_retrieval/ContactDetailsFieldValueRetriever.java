package de.symeda.sormas.backend.patch.partial_retrieval;

import static de.symeda.sormas.backend.patch.PatchFieldHelper.PATH_SEPARATOR;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.patch.partial_retrieval.FieldInfo;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;

@ApplicationScoped
public class ContactDetailsFieldValueRetriever implements SpecificFieldValueRetriever {

	@Override
	public FieldInfo getFieldInfo(String fieldName, EntityDto entityDto) {
		PersonDto personDto = (PersonDto) entityDto;

		boolean isPhone = fieldName.contains(PersonContactDetailDto.PHONE_NUMBER_TYPE);
		PersonContactDetailType targetType = isPhone ? PersonContactDetailType.PHONE : PersonContactDetailType.EMAIL;

		String contactValues = personDto.getPersonContactDetails()
			.stream()
			.filter(detail -> targetType.equals(detail.getPersonContactDetailType()))
			.map(PersonContactDetailDto::getContactInformation)
			.filter(StringUtils::isNotBlank)
			.sorted()
			.collect(Collectors.joining("; "));

		String captionKey = isPhone ? PersonContactDetailDto.PHONE_NUMBER_TYPE : PersonContactDetailDto.CONTACT_INFORMATION;
		String translatedFieldName = I18nProperties.getCaption(PersonContactDetailDto.I18N_PREFIX + PATH_SEPARATOR + captionKey, captionKey);

		return new FieldInfo().setFieldType(List.class).setFieldValue(contactValues).setTranslatedFieldName(translatedFieldName);
	}

	@Override
	public Set<String> getSupportedFields() {
		return Stream.of(PersonContactDetailDto.PHONE_NUMBER_TYPE, PersonContactDetailDto.CONTACT_INFORMATION)
			.map(suffix -> PersonContactDetailDto.I18N_PREFIX + PATH_SEPARATOR + suffix)
			.collect(Collectors.toSet());
	}
}
