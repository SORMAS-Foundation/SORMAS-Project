package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public final class PropertyPaths {

	private PropertyPaths() {
	}

	public static Object read(Object root, String[] path) {
		return walk(root, path, path.length);
	}

	public static Object owner(Object root, String[] path) {
		return walk(root, path, path.length - 1);
	}

	public static PropertyDescriptor descriptor(Class<?> type, String property) {
		try {
			return new PropertyDescriptor(property, type);
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException(type.getSimpleName() + " has no property " + property, e);
		}
	}

	public static boolean resolves(Class<?> type, String[] path) {
		Class<?> current = type;
		for (int i = 0; i < path.length && current != null; i++) {
			try {
				PropertyDescriptor property = new PropertyDescriptor(path[i], current);
				if (i == path.length - 1) {
					return property.getWriteMethod() != null;
				}
				current = property.getPropertyType();
			} catch (IntrospectionException e) {
				return false;
			}
		}
		return false;
	}

	private static Object walk(Object root, String[] path, int steps) {
		Object current = root;
		for (int i = 0; i < steps && current != null; i++) {
			try {
				current = descriptor(current.getClass(), path[i]).getReadMethod().invoke(current);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		}
		return current;
	}
}
