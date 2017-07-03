package de.symeda.sormas.app.backend.common;

import com.googlecode.openbeans.BeanInfo;
import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import de.symeda.sormas.api.utils.DataHelper;

/**
 * Contains methods that help to iterate through properties of ADO.
 *
 * Created by Martin Wahnschaffe on 18.05.2017.
 */
public final class AdoPropertyHelper {

    private static final ConcurrentHashMap<Class<? extends AbstractDomainObject>, PropertyDescriptor[]> propertyDescriptorCache
            = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<PropertyDescriptor, Boolean> propertyHasEmbeddedAnnotationCache
            = new ConcurrentHashMap<>();

    public static PropertyDescriptor[] getPropertyDescriptors(Class<? extends AbstractDomainObject> type) {

        if (!propertyDescriptorCache.containsKey(type)) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(type);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                propertyDescriptorCache.put(type, propertyDescriptors);
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
        }
        return propertyDescriptorCache.get(type);
    }

    public static boolean hasEmbeddedAnnotation(PropertyDescriptor property) {
        if (!propertyHasEmbeddedAnnotationCache.containsKey(property)) {
            propertyHasEmbeddedAnnotationCache.put(property, property.getPropertyType().isAnnotationPresent(EmbeddedAdo.class));
        }
        return propertyHasEmbeddedAnnotationCache.get(property);
    }

    /**
     * Check if any property of the two objects is different.
     * a and b need to have the same type
     * @param includeEmbedded also check children (recursively)
     */
    public static boolean hasModifiedProperty(AbstractDomainObject a, AbstractDomainObject b, boolean includeEmbedded) {

        try {
            // ignore parent property
            EmbeddedAdo annotation = a.getClass().getAnnotation(EmbeddedAdo.class);
            String parentProperty = annotation != null ? annotation.parentAccessor() : "";

            for (PropertyDescriptor property : getPropertyDescriptors(a.getClass())) {

                // ignore some types and specific properties
                if (!AdoPropertyHelper.isModifyableProperty(property)
                        || parentProperty.equals(property.getName())) {
                    continue;
                }

                // there are four types of properties:

                // 1. embedded domain objects like a Location or Symptoms
                // -> ignore for now
                if (AdoPropertyHelper.hasEmbeddedAnnotation(property)) {
                    continue;
                }
                // 2. "value" types like String, Date, Enum, ...
                // 3. reference domain objects like a reference to a Person or a District
                // -> basic equals check
                else if (DataHelper.isValueType(property.getPropertyType())
                        || AbstractDomainObject.class.isAssignableFrom(property.getPropertyType())) {

                    Object valueA = property.getReadMethod().invoke(a);
                    Object valueB = property.getReadMethod().invoke(b);

                    if (!DataHelper.equal(valueA, valueB)) {
                        return true;
                    }

                }
                // 4. lists of embedded domain objects
                // -> basic equals check for the list (does not check for changes of the single elements)
                else if (Collection.class.isAssignableFrom(property.getPropertyType())) {

                    Collection<AbstractDomainObject> collectionA = (Collection<AbstractDomainObject>)property.getReadMethod().invoke(a);
                    Collection<AbstractDomainObject> collectionB = (Collection<AbstractDomainObject>)property.getReadMethod().invoke(b);

                    if (!DataHelper.equal(collectionA, collectionB)) {
                        return true;
                    }

                } else {
                    // Other objects are not supported
                    throw new UnsupportedOperationException(property.getPropertyType().getName() + " is not supported as a property type.");
                }
            }

            // do we have to recursively look into children?
            if (includeEmbedded) {
                for (PropertyDescriptor property : getPropertyDescriptors(a.getClass())) {

                    if (property.getWriteMethod() == null
                            || property.getReadMethod() == null
                            || parentProperty.equals(property.getName()))
                        continue;


                    // 1. embedded domain objects like a Location or Symptoms
                    // -> recursion
                    if (AdoPropertyHelper.hasEmbeddedAnnotation(property)) {

                        AbstractDomainObject embeddedA = (AbstractDomainObject)property.getReadMethod().invoke(a);
                        AbstractDomainObject embeddedB = (AbstractDomainObject)property.getReadMethod().invoke(b);

                        if (hasModifiedProperty(embeddedA, embeddedB, true)) {
                            return true;
                        }
                    }
                    // 4. lists of embedded domain objects
                    // -> recursion for all elements
                    else if (Collection.class.isAssignableFrom(property.getPropertyType())) {

                        // order and size of collections is equal - otherwise we would already have returned
                        Collection<AbstractDomainObject> collectionA = (Collection<AbstractDomainObject>)property.getReadMethod().invoke(a);
                        Collection<AbstractDomainObject> collectionB = (Collection<AbstractDomainObject>)property.getReadMethod().invoke(b);

                        Iterator<AbstractDomainObject> iteratorA = collectionA.iterator();
                        Iterator<AbstractDomainObject> iteratorB = collectionB.iterator();
                        while (iteratorA.hasNext()) {
                            AbstractDomainObject elementA = iteratorA.next();
                            AbstractDomainObject elementB = iteratorB.next();

                            if (hasModifiedProperty(elementA, elementB, true)) {
                                return true;
                            }

                        }
                    }
                }
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public static boolean isModifyableProperty(PropertyDescriptor property) {
        // ignore some types and specific properties
        if (AbstractDomainObject.CREATION_DATE.equals(property.getName())
                || AbstractDomainObject.CHANGE_DATE.equals(property.getName())
                || AbstractDomainObject.LOCAL_CHANGE_DATE.equals(property.getName())
                || AbstractDomainObject.UUID.equals(property.getName())
                || AbstractDomainObject.ID.equals(property.getName())
                || AbstractDomainObject.SNAPSHOT.equals(property.getName())
                || AbstractDomainObject.MODIFIED.equals(property.getName())
                || AbstractDomainObject.LAST_OPENED_DATE.equals(property.getName())
                || property.getWriteMethod() == null
                || property.getReadMethod() == null)
            return false;
        return true;
    }


    /**
     * TODO move to proper class
     * @param type
     * @return
     */
    public static Iterator<PropertyDescriptor> getEmbeddedAdoProperties(final Class<? extends AbstractDomainObject> type) {

        return new PropertyIterator(type) {
            protected boolean filterProperty(PropertyDescriptor property) {
                return hasEmbeddedAnnotation(property);
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

        /**
         * @return true if the property should be part of the iteration
         */
        protected abstract boolean filterProperty(PropertyDescriptor property);

        private void init() {
            propertyDescriptors = getPropertyDescriptors(type);
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
