package de.symeda.sormas.app.core.async;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.NoSuchElementException;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.core.BoolResult;

/**
 * Created by Orson on 09/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TaskResultHolder {
    public static final TaskResultHolder EMPTY = new EmptyTaskResultHolder();

    private BoolResult resultStatus = BoolResult.TRUE;

    private List<Object> otherHolder;
    private List<AbstractDomainObject> itemHolder;
    private List<List<? extends AbstractDomainObject>> listHolder;

    private ITaskResultOtherHolder otherHolderObject;
    private ITaskResultItemHolder itemHolderObject;
    private ITaskResultItemListHolder listHolderObject;
    private ITaskResultItemEnumerableHolder enumerableHolderObject;

    public TaskResultHolder() {
        this.itemHolder = new ArrayList<>();
        this.listHolder = new ArrayList<>();
        this.otherHolder = new ArrayList<>();
    }

    public ITaskResultOtherHolder forOther() {
        if (otherHolderObject == null)
            otherHolderObject = new TaskResultOtherHolder(otherHolder);

        return otherHolderObject;
    }

    public ITaskResultItemHolder forItem() {
        if (itemHolderObject == null)
            itemHolderObject = new TaskResultItemHolder(itemHolder);

        return itemHolderObject;
    }

    public ITaskResultItemListHolder forList() {
        if (listHolderObject == null)
            listHolderObject = new TaskResultItemListHolder(listHolder);

        return listHolderObject;
    }

    public <T> ITaskResultItemEnumerableHolder forEnumerable() {
        if (enumerableHolderObject == null)
            enumerableHolderObject = new TaskResultItemEnumerableHolder<T>();

        return enumerableHolderObject;
    }


    private static class EmptyTaskResultHolder extends TaskResultHolder {
        public EmptyTaskResultHolder() {
        }
    }

    private static class TaskResultItemHolder implements ITaskResultItemHolder {

        private List<AbstractDomainObject> holder;
        protected transient int modCount = 0;
        private int nextIndex = 0;

        public TaskResultItemHolder(List<AbstractDomainObject> holder) {
            this.holder = holder;
        }

        @Override
        public <ADO extends AbstractDomainObject> void add(ADO value) {
            if (holder != null) {
                holder.add(nextIndex, value);
                nextIndex = nextIndex + 1;
            }
        }

        public ITaskResultHolderIterator iterator() {
            return new TaskResultHolderIterator();
        }

        private class TaskResultHolderIterator implements ITaskResultHolderIterator {

            protected int limit = holder.size();

            int cursor;       // index of next element to return
            int lastRet = -1; // index of last element returned; -1 if no such
            int expectedModCount = modCount;

            public boolean hasNext() {
                return cursor < limit;
            }

            public <E> E next() {
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();

                int i = cursor;
                if (i >= limit)
                    throw new NoSuchElementException();

                if (i >= holder.size())
                    throw new ConcurrentModificationException();

                cursor = i + 1;
                return (E) holder.get(lastRet = i);
            }
        }
    }

    private static class TaskResultItemListHolder implements ITaskResultItemListHolder {

        private List<List<? extends AbstractDomainObject>> holder;
        protected transient int modCount = 0;
        private int nextIndex = 0;

        public TaskResultItemListHolder(List<List<? extends AbstractDomainObject>> holder) {
            this.holder = holder;
        }


        @Override
        public <ADO extends AbstractDomainObject> void add(List<ADO> value) {
            if (holder != null) {
                holder.add(nextIndex, value);
                nextIndex = nextIndex + 1;
            }
        }

        public ITaskResultHolderIterator iterator() {
            return new TaskResultHolderIterator();
        }

        private class TaskResultHolderIterator implements ITaskResultHolderIterator {

            protected int limit = holder.size();

            int cursor;       // index of next element to return
            int lastRet = -1; // index of last element returned; -1 if no such
            int expectedModCount = modCount;

            public boolean hasNext() {
                return cursor < limit;
            }

            public <E> E next() {
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();

                int i = cursor;
                if (i >= limit)
                    throw new NoSuchElementException();

                if (i >= holder.size())
                    throw new ConcurrentModificationException();

                cursor = i + 1;
                return (E) holder.get(lastRet = i);
            }
        }
    }

    private static class TaskResultOtherHolder implements ITaskResultOtherHolder {

        private List<Object> holder;
        protected transient int modCount = 0;
        private int nextIndex = 0;

        public TaskResultOtherHolder(List<Object> holder) {
            this.holder = holder;
        }

        @Override
        public <T> void add(T value) {
            if (holder != null) {
                holder.add(nextIndex, value);
                nextIndex = nextIndex + 1;
            }
        }

        public ITaskResultHolderIterator iterator() {
            return new TaskResultHolderIterator();
        }

        private class TaskResultHolderIterator implements ITaskResultHolderIterator {
            protected int limit = holder.size();

            int cursor;       // index of next element to return
            int lastRet = -1; // index of last element returned; -1 if no such
            int expectedModCount = modCount;

            public boolean hasNext() {
                return cursor < limit;
            }

            public <E> E next() {
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();

                int i = cursor;
                if (i >= limit)
                    throw new NoSuchElementException();

                if (i >= holder.size())
                    throw new ConcurrentModificationException();

                cursor = i + 1;
                return (E) holder.get(lastRet = i);
            }
        }
    }

    private static class TaskResultItemEnumerableHolder<T> implements ITaskResultItemEnumerableHolder<T> {

        private List<T> holder;
        protected transient int modCount = 0;
        private int nextIndex = 0;

        public TaskResultItemEnumerableHolder() {
            this.holder = new ArrayList<T>();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void add(T value) {
            if (holder != null) {
                holder.add(nextIndex, value);
                nextIndex = nextIndex + 1;
            }
        }

        public ITaskResultHolderEnumerableIterator<T> iterator() {
            return new TaskResultHolderIterator();
        }

        private class TaskResultHolderIterator implements ITaskResultHolderEnumerableIterator<T> {

            protected int limit = holder.size();

            int cursor;       // index of next element to return
            int lastRet = -1; // index of last element returned; -1 if no such
            int expectedModCount = modCount;

            public boolean hasNext() {
                return cursor < limit;
            }

            public T next() {
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();

                int i = cursor;
                if (i >= limit)
                    throw new NoSuchElementException();

                if (i >= holder.size())
                    throw new ConcurrentModificationException();

                cursor = i + 1;
                return (T) holder.get(lastRet = i);
            }
        }
    }

    public BoolResult getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(BoolResult resultStatus) {
        this.resultStatus = resultStatus;
    }

}
