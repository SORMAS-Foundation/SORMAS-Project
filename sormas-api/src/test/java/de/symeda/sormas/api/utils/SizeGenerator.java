/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.utils;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_SMALL;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.validation.constraints.Size;

import org.junit.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Utf8;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.visit.VisitDto;
import uk.co.jemos.podam.api.ObjectStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.PodamUtils;
import uk.co.jemos.podam.api.RandomDataProviderStrategyImpl;

public class SizeGenerator {

	private final static Class<? extends EntityDto>[] dtoClasses = new Class[] {
		CaseDataDto.class,
		ImmunizationDto.class,
		ContactDto.class,
		PersonDto.class,
		EventDto.class,
		EventParticipantDto.class,
		SampleDto.class,
		PathogenTestDto.class,
		AdditionalTestDto.class,
		TaskDto.class,
		VisitDto.class,
		WeeklyReportDto.class,
		AggregateReportDto.class,
		PrescriptionDto.class,
		TreatmentDto.class,
		ClinicalVisitDto.class
	};

	public static void main(String[] args) throws Exception {
		final PodamFactory factory = new PodamFactoryImpl();

		RandomDataProviderStrategyImpl strategy = (RandomDataProviderStrategyImpl) factory.getStrategy();
		strategy.addOrReplaceAttributeStrategy(Size.class, new ObjectStrategy(){
			@Override
			public Object getValue(Class<?> attrType, List<Annotation> attrAnnotations) {
				long length = CHARACTER_LIMIT_SMALL;

				StringBuilder sb = new StringBuilder();
				while (sb.length() < length) {
					sb.append(PodamUtils.getNiceCharacter());
				}
				return sb.toString();
			}
		});
		factory.setStrategy(strategy);

		for (Class<? extends EntityDto> dtoClass : dtoClasses) {
			getSizeOfFilledDto(factory, dtoClass);
		}
	}

	private static void getSizeOfFilledDto(PodamFactory factory, Class<? extends EntityDto> dtoClass) throws JsonProcessingException {
		final EntityDto dto = factory.manufacturePojo(dtoClass);
		Assert.assertNotNull(dto);
		final ObjectMapper objectMapper = new ObjectMapper();
		final String json = objectMapper.writeValueAsString(dto);
		System.out.println(dtoClass.getSimpleName() + " JSON (UTF-8) size: " + Utf8.encodedLength(json));
	}
}
