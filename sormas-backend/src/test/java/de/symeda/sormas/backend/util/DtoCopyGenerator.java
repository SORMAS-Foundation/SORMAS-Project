package de.symeda.sormas.backend.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.junit.Test;

import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.symptoms.Symptoms;

public class DtoCopyGenerator {

	Class<?> from = Symptoms.class;
	Class<?> to = SymptomsDto.class;

	@Test
	public void generateCopyJava() throws IntrospectionException {

		for (PropertyDescriptor property : Introspector.getBeanInfo(from).getPropertyDescriptors()){

			if (property.getWriteMethod() == null) {
				continue;
			}
			
			if (AbstractDomainObject.UUID.equals(property.getName())) {
				continue;
			}
			
			try {
				Method toWriteMethod = to.getMethod(property.getWriteMethod().getName(), property.getWriteMethod().getParameterTypes());
				System.out.println("a." + toWriteMethod.getName() + "(b." + property.getReadMethod().getName() + "());");
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
			}
		}
	}

}
