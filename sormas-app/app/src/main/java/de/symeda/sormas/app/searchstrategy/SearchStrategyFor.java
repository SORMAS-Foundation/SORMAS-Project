package de.symeda.sormas.app.searchstrategy;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.shared.ShipmentStatus;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class SearchStrategyFor<ADO extends AbstractDomainObject> {
    private final int value;
    private final String displayName;


    public static final SearchStrategyFor CASE = new CaseStrategySelector();
    public static final SearchStrategyFor CONTACT = new ContactStrategySelector();
    public static final SearchStrategyFor EVENT = new EventStrategySelector();
    public static final SearchStrategyFor SAMPLE = new SampleStrategySelector();
    public static final SearchStrategyFor TASK = new TaskStrategySelector();

    protected SearchStrategyFor(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public abstract ISearchExecutor<ADO> selector(SearchBy by, Enum status, String recordId);

    private static class CaseStrategySelector extends SearchStrategyFor<Case>
    {
        public CaseStrategySelector() {
            super(0, "Case Search Strategy Selector");
        }

        @Override
        public ISearchExecutor<Case> selector(SearchBy by, Enum status, String recordId) {
            ISearchStrategy<Case> searchStrategy = null;

            if (by == SearchBy.BY_FILTER_STATUS) {
                searchStrategy = new CaseSearchByStatusStrategy((InvestigationStatus)status);
            } else {
                searchStrategy = new CaseSearchByAllStrategy();
            }

            return new SearchExecutor<Case>(searchStrategy);
        }
    }

    private static class ContactStrategySelector extends SearchStrategyFor<Contact>
    {
        public ContactStrategySelector() {
            super(1, "Contact Search Strategy Selector");
        }

        @Override
        public ISearchExecutor<Contact> selector(SearchBy by, Enum status, String recordId) {
            ISearchStrategy<Contact> searchStrategy = null;

            if (by == SearchBy.BY_FILTER_STATUS) {
                searchStrategy = new ContactSearchByStatusStrategy((FollowUpStatus)status);
            } else if (by == SearchBy.BY_CASE_ID) {
                searchStrategy = new ContactSearchByCaseStrategy(recordId);
            }

            return new SearchExecutor<Contact>(searchStrategy);
        }
    }

    private static class EventStrategySelector extends SearchStrategyFor<Event>
    {
        public EventStrategySelector() {
            super(2, "Event Search Strategy Selector");
        }

        @Override
        public ISearchExecutor<Event> selector(SearchBy by, Enum status, String recordId) {
            ISearchStrategy<Event> searchStrategy = null;

            if (by == SearchBy.BY_FILTER_STATUS) {
                searchStrategy = new EventSearchByStatusStrategy((EventStatus)status);
            }

            return new SearchExecutor<Event>(searchStrategy);
        }
    }

    private static class SampleStrategySelector extends SearchStrategyFor<Sample>
    {
        public SampleStrategySelector() {
            super(3, "Sample Search Strategy Selector");
        }

        @Override
        public ISearchExecutor<Sample> selector(SearchBy by, Enum status, String recordId) {
            ISearchStrategy<Sample> searchStrategy = null;

            if (by == SearchBy.BY_FILTER_STATUS) {
                searchStrategy = new SampleSearchByStatusStrategy((ShipmentStatus)status);
            } else if (by == SearchBy.BY_CASE_ID) {
                searchStrategy = new SampleSearchByCaseStrategy(recordId);
            } else {
                searchStrategy = new SampleSearchByAllStrategy();
            }

            return new SearchExecutor<Sample>(searchStrategy);
        }
    }

    private static class TaskStrategySelector extends SearchStrategyFor<Task>
    {
        public TaskStrategySelector() {
            super(4, "Task Search Strategy Selector");
        }

        @Override
        public ISearchExecutor<Task> selector(SearchBy by, Enum status, String recordId) {
            ISearchStrategy<Task> searchStrategy = null;

            if (by == SearchBy.BY_FILTER_STATUS) {
                searchStrategy = new TaskSearchByStatusStrategy((TaskStatus)status);
            } else if (by == SearchBy.BY_CASE_ID) {
                searchStrategy = new TaskSearchByCaseStrategy(recordId);
            } else if (by == SearchBy.BY_CONTACT_ID) {
                searchStrategy = new TaskSearchByContactStrategy(recordId);
            } else if (by == SearchBy.BY_EVENT_ID) {
                searchStrategy = new TaskSearchByEventStrategy(recordId);
            }

            return new SearchExecutor<Task>(searchStrategy);
        }
    }
}
