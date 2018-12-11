import java.util.List;

/**
 * This class defines a rule used to filter out FoodItems that meet criteria related to the
 * nutrients in the food
 * 
 * @author Shannon Morison, Grant Perry, Kevin Boening, Billy Kirk
 *
 */
public class NutrientFilter {
	// Nutrient to be filtered on
	private String nutrientName;
	// Comparator for the filter
	private String comparator;
	// value to be compared against
	private Double value;
	
	/**
	 * Constructor for NutrientFilter
	 * 
	 * @param nut nutrient to filter on
	 * @param c Comparator to use, either <=, >=, or ==
	 * @param val value to compare based off of
	 */
	public NutrientFilter(String nut, String c, Double val) {
		nutrientName = nut;
		comparator = c;
		value = val;
	}
	
	/**
	 * Accessor for nutrientToCompare
	 * 
	 * @return nutrientToCompare
	 */
	public String getNutrient() {
		return nutrientName;
	}
	
	/**
	 * Accessor for comparator
	 * 
	 * @return comparator
	 */
	public String getComparator() {
		return comparator;
	}
	
	/**
	 * Accessor for value
	 * 
	 * @return value
	 */
	public double getValue() {
		return value;
	}
	
	/**
	 * Mutator for nutrientName
	 * 
	 * @param nut new value for nutrientName
	 */
	public void setNutrient(String nut) {
		nutrientName = nut;
	}
	
	/**
	 * Mutator for comparator
	 * 
	 * @param comp new comparator value
	 */
	public void setComparator(String comp) {
		comparator = comp;
	}
	
	/**
	 * Puts FoodItems meeting the criteria of this filter into a list
	 * 
	 * @param tree - the BPTree whose key is the value for nutrientToCompare for each FoodItem
	 * @return a List containing all the FoodItems that meet this filter's criterion
	 */
	public List<FoodItem> executeFilter(BPTree<Double, FoodItem> tree) {
		return tree.rangeSearch(value, comparator);
	}
}