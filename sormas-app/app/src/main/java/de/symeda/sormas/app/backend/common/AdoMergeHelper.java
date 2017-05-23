package de.symeda.sormas.app.backend.common;

import android.util.Log;

import com.googlecode.openbeans.BeanInfo;
import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import de.symeda.sormas.api.utils.DataHelper;

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

        return new Iterator<PropertyDescriptor>() {

            private PropertyDescriptor[] propertyDescriptors = null;
            private int currentPropertyIndex;

            private void init() {
                try {
                    BeanInfo beanInfo = Introspector.getBeanInfo(type);
                    propertyDescriptors = beanInfo.getPropertyDescriptors();
                } catch (IntrospectionException e) {
                    throw new RuntimeException(e);
                }
                currentPropertyIndex = -1;
                moveToNextEmbeddedAdoProperty();
            }

            private void moveToNextEmbeddedAdoProperty() {

                PropertyDescriptor element;
                do {
                    currentPropertyIndex++;
                    if (currentPropertyIndex == propertyDescriptors.length)
                        break;

                    element = propertyDescriptors[currentPropertyIndex];
                } while (!element.getPropertyType().isAnnotationPresent(EmbeddedAdo.class));
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
                moveToNextEmbeddedAdoProperty();

                return element;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * TODO old method - might be useful for merging of lists in AbstractAdoDao
     * @param beanInfo
     * @param property
     * @param baseFieldValue
     * @param sourceFieldValue
     * @param targetFieldValue
     */
    public static void mergeAdoList(BeanInfo beanInfo, PropertyDescriptor property, Collection<AbstractDomainObject> baseFieldValue, Collection<AbstractDomainObject> sourceFieldValue, Collection<AbstractDomainObject> targetFieldValue) {

        Collection<AbstractDomainObject> baseCollection = baseFieldValue;
        Collection<AbstractDomainObject> sourceCollection = sourceFieldValue;
        Collection<AbstractDomainObject> targetCollection = targetFieldValue;

        // create lists so we can retrieve the existing elements more convenient
        List<AbstractDomainObject> baseList = new ArrayList<AbstractDomainObject>(baseCollection);
        List<AbstractDomainObject> targetList = new ArrayList<AbstractDomainObject>(targetCollection);

        boolean elementTypeCheckDone = false;
        for (AbstractDomainObject sourceElement : sourceCollection) {

            if (!elementTypeCheckDone) {
                if (!sourceElement.getClass().isAnnotationPresent(EmbeddedAdo.class)) {
                    throw new UnsupportedOperationException(property.getName() + ": Merging of lists currently only supports elements annotated with EmbeddedAdo");
                }
                elementTypeCheckDone = true;
            }

            // 1. new entry?
            if (!baseCollection.contains(sourceElement)) {

                // validate situation
                if (sourceElement.getId() != null) {
                    throw new IllegalArgumentException("List elements in source entity are not supposed to be in the database yet: " + ((AbstractDomainObject) sourceElement).getUuid());
                }

                // just add to base and target
                baseCollection.add(sourceElement);
                targetCollection.add(sourceElement);
            }
            // 2. existing entry
            else {

                AbstractDomainObject baseElement = baseList.get(baseList.indexOf(sourceElement));

                // check for changes
                if (baseElement.getChangeDate().equals(sourceElement.getChangeDate())) {
                    continue; // unchanged -> next
                }

                int targetListIndex = targetList.indexOf(sourceElement);
                if (targetListIndex >= 0) {

                    // element exists in all three lists: merge
                    AbstractDomainObject targetElement = targetList.get(targetListIndex);

                    //mergeAdo(targetElement, baseElement, sourceElement);
                }
                else {

                    // we have a conflict, because element was changed on server and removed in app
                    Log.i(beanInfo.getBeanDescriptor().getName(), "Re-adding element to " + property.getName() +
                            " because there was a newer version: '" +  DataHelper.toStringNullable(sourceElement) + "'");

                    // re-add
                    targetCollection.add(sourceElement);

                    // replace old object with new one
                    baseCollection.remove(sourceElement);
                    baseCollection.add(sourceElement);
                }
            }
        }

        for (AbstractDomainObject baseElement : baseList) {
            if (!sourceCollection.contains(baseElement)) {
                // 3. element was removed from list

                // removed from target ..
                int targetListIndex = targetList.indexOf(baseElement);
                if (targetListIndex >= 0) {
                    AbstractDomainObject targetElement = targetList.get(targetListIndex);
                    if (targetElement.isModified()) {
                        // WARNING for now this will always happen, when a parent entity is modified,
                        // because we are setting modified to true for all children

                        // we have a conflict, because element was changed in app but removed on server
                        Log.i(beanInfo.getBeanDescriptor().getName(), "Changed element was removed from list " + property.getName() +
                                ": '" + DataHelper.toStringNullable(targetElement) + "'");
                    }
                    // do this in any case
                    targetCollection.remove(baseElement);
                }

                // .. and base
                baseCollection.remove(baseElement);
            }
        }
    }
}
