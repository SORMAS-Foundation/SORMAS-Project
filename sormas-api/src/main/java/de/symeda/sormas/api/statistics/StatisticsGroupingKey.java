package de.symeda.sormas.api.statistics;

/**
 * Indicates that the class implementing this interface will be used for filter and grouping
 * purposes in the statistics section(s). All implementing classes should override and provide
 * custom implementations of the toString, equals, hashCode and compareTo methods (thereby also
 * implementing the Comparable interface). All implementing enums should provide custom 
 * implementations of the toString method.
 * 
 * @author Mate Strysewske
 */
public interface StatisticsGroupingKey {
	
	int keyCompareTo(StatisticsGroupingKey o);
	
}
