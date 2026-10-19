package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ToyCellWriter implements CellWriter {

	public final List<String> writtenHeaders = new ArrayList<>();

	@Override
	public void write(Map<String, Object> targets, String target, String[] propertyPath, String header, String value) throws CellWriteException {
		writtenHeaders.add(header);
		Object owner = PropertyPaths.owner(targets.get(target), propertyPath);
		PropertyDescriptor property = PropertyPaths.descriptor(owner.getClass(), propertyPath[propertyPath.length - 1]);
		try {
			Object parsed = property.getPropertyType() == Integer.class ? Integer.valueOf(value) : value;
			property.getWriteMethod().invoke(owner, parsed);
		} catch (NumberFormatException e) {
			throw new CellWriteException(header + " is not a number");
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}
}
