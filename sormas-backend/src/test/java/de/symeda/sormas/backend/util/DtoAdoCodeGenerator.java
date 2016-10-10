package de.symeda.sormas.backend.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.junit.Test;

import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.task.Task;

public class DtoAdoCodeGenerator {

	Class<?> ado = Task.class;
	Class<?> dto = TaskDto.class;

	@Test
	public void generateCopyJava() throws IntrospectionException {

		for (PropertyDescriptor property : Introspector.getBeanInfo(ado).getPropertyDescriptors()){

			if (property.getWriteMethod() == null) {
				continue;
			}
			
			if (AbstractDomainObject.UUID.equals(property.getName())) {
				continue;
			}
			
			try {
				Method toWriteMethod = dto.getMethod(property.getWriteMethod().getName(), property.getWriteMethod().getParameterTypes());
				System.out.println("a." + toWriteMethod.getName() + "(b." + property.getReadMethod().getName() + "());");
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
			}
		}
	}

	@Test
	public void generatePropertyConstantsJava() throws IntrospectionException {

		for (PropertyDescriptor property : Introspector.getBeanInfo(dto).getPropertyDescriptors()){
			String propertyName = property.getName();
			
			if (AbstractDomainObject.ID.equals(propertyName)
					|| AbstractDomainObject.UUID.equals(propertyName)
					|| AbstractDomainObject.CHANGE_DATE.equals(propertyName)
					|| AbstractDomainObject.CREATION_DATE.equals(propertyName)
					|| "class".equals(propertyName)) {
				continue;
			}

			System.out.println(String.format("public static final String %s = \"%s\";", 
					propertyName.replaceAll("(.)([A-Z])", "$1_$2").toUpperCase(), propertyName));
		}
	}
	
	@Test
	public void generateI18nProperties() throws IntrospectionException {

		String i18nPrefixName = ado.getSimpleName();
		
		for (PropertyDescriptor property : Introspector.getBeanInfo(dto).getPropertyDescriptors()){
			String propertyName = property.getName();
			
			if (AbstractDomainObject.ID.equals(propertyName)
					|| AbstractDomainObject.UUID.equals(propertyName)
					|| AbstractDomainObject.CHANGE_DATE.equals(propertyName)
					|| AbstractDomainObject.CREATION_DATE.equals(propertyName)
					|| "class".equals(propertyName)) {
				continue;
			}
			
			String caption = propertyName.replaceAll("(.)([A-Z])", "$1 $2").toLowerCase();
			caption = caption.substring(0, 1).toUpperCase() + caption.substring(1);

			System.out.println(String.format("%s.%s = %s", 
					i18nPrefixName, propertyName, caption));
		}
	}

}
