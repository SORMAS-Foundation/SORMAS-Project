package com.cinoteck.application.views.configurations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.region.RegionIndexDto;

public class RegionDataProvider extends AbstractBackEndDataProvider<RegionIndexDto, RegionFilter> {
	
	 final List<RegionIndexDto> regionList = new ArrayList<>(FacadeProvider.getRegionFacade().getAllRegions());

	@Override
	protected Stream<RegionIndexDto> fetchFromBackEnd(Query<RegionIndexDto, RegionFilter> query) {
		Stream<RegionIndexDto> stream = regionList.stream();
		// Filtering
        if (query.getFilter().isPresent()) {
            stream = stream.filter(e -> query.getFilter().get().test(e));
        }

        // Sorting
//        if (query.getSortOrders().size() > 0) {
//            stream = stream.sorted(sortComparator(query.getSortOrders()));
//        }

        // Pagination
        return stream.skip(query.getOffset()).limit(query.getLimit());
		
	}

	@Override
	protected int sizeInBackEnd(Query<RegionIndexDto, RegionFilter> query) {
		   return (int) fetchFromBackEnd(query).count();
	}
	
//	 private static Comparator<RegionIndexDto> sortComparator(List<QuerySortOrder> sortOrders) {
//	        return sortOrders.stream().map(sortOrder -> {
//	            Comparator<RegionIndexDto> comparator = nameComparator(sortOrder.getSorted());
//
//	            if (sortOrder.getDirection() == SortDirection.DESCENDING) {
//	                comparator = comparator.reversed();
//	            }
//
//	            return comparator;
//	        }).reduce(Comparator::thenComparing).orElse((p1, p2) -> 0);
//	    }
	
	
//	 private static Comparator<RegionIndexDto> nameComparator(String sorted) {
//	        if (sorted.equals("name")) {
//	            return Comparator.comparing(person -> person.get);
//	        } else if (sorted.equals("profession")) {
//	            return Comparator.comparing(person -> person.getProfession());
//	        }
//	        return (p1, p2) -> 0;
//	    }
	
}
