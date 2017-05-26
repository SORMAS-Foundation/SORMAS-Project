package de.symeda.sormas.app.backend.common;

import com.googlecode.openbeans.BeanInfo;
import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * TODO rename or delete
 *
 * Created by Martin Wahnschaffe on 18.05.2017.
 */
public final class AdoMergeHelper {

    /**
     * TODO move to proper class
     * @param type
     * @return
     */
    public static Iterator<PropertyDescriptor> getEmbeddedAdoProperties(final Class<? extends AbstractDomainObject> type) {

        return new PropertyIterator(type) {
            protected boolean filterProperty(PropertyDescriptor property) {
                return property.getPropertyType().isAnnotationPresent(EmbeddedAdo.class);
            }
        };
    }


    /**
     * TODO move to proper class
     * @param type
     * @return
     */
    public static Iterator<PropertyDescriptor> getCollectionProperties(final Class<? extends AbstractDomainObject> type) {

        return new PropertyIterator(type) {
            protected boolean filterProperty(PropertyDescriptor property) {
                return Collection.class.isAssignableFrom(property.getPropertyType());
            }
        };
    }

    private static abstract class PropertyIterator implements Iterator<PropertyDescriptor> {

        private final Class<? extends AbstractDomainObject> type;
        private PropertyDescriptor[] propertyDescriptors;
        private int currentPropertyIndex;

        public PropertyIterator(Class<? extends AbstractDomainObject> type) {
            this.type = type;
            propertyDescriptors = null;
        }

        protected abstract boolean filterProperty(PropertyDescriptor property);

        private void init() {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(type);
                propertyDescriptors = beanInfo.getPropertyDescriptors();
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
            currentPropertyIndex = -1;
            moveToNextFilteredProperty();
        }

        private void moveToNextFilteredProperty() {

            PropertyDescriptor element;
            do {
                currentPropertyIndex++;
                if (currentPropertyIndex == propertyDescriptors.length)
                    break;

                element = propertyDescriptors[currentPropertyIndex];
            } while (!filterProperty(element));
        }

        @Override
        public boolean hasNext() {
            if (propertyDescriptors == null) {
                init();
            }
            return currentPropertyIndex < propertyDescriptors.length;
        }

        @Override
        public PropertyDescriptor next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            PropertyDescriptor element = propertyDescriptors[currentPropertyIndex];
            moveToNextFilteredProperty();

            return element;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
