package application;

import java.util.List;

/**
 * This class defines a rule used to filter out FoodItems that meet a criterion related to one of 
 * the nutrients in the food
 * 
 * @author Shannon Morison, Grant Perry, Kevin Boening, Billy Kirk
 *
 */
public class NutrientFilter {
	// Nutrient to be filtered on
	private Nutrients nutrientToCompare;
	// Comparator for the filter
	private Operators operator;
	// value to be compared against
	private double value;
	
	/**
	 * Constructor for NutrientFilter
	 * 
	 * @param nut Nutrients element to filter on
	 * @param o Operators element indicating what comparison to do
	 * @param val value to compare based off of
	 */
	public NutrientFilter(Nutrients nut, Operators o, double val) {
		nutrientToCompare = nut;
		operator = o;
		value = val;
	}
	
	/**
	 * Accessor for nutrientToCompare
	 * 
	 * @return nutrientToCompare
	 */
	public Nutrients getNutrient() {
		return nutrientToCompare;
	}
	
	/**
	 * Accessor for operator
	 * 
	 * @return operator
	 */
	public Operators getOperator() {
		return operator;
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
	 * Mutator for nutrientToCompare
	 * 
	 * @param nut new value for nutrientToCompare
	 */
	public void setNutrient(Nutrients nut) {
		nutrientToCompare = nut;
	}
	
	/**
	 * Mutator for operator
	 * 
	 * @param op new operator value
	 */
	public void setOperator(Operators op) {
		operator = op;
	}
	
	/**
	 * Puts FoodItems meeting the criteria of this filter into a list
	 * 
	 * @param tree - the BPTree whose key is the value for nutrientToCompare for each FoodItem
	 * @return a List containing all the FoodItems that meet this filter's criterion
	 */
	public List<FoodItem> executeFilter(BPTree<Double, FoodItem> tree) {
		String comparator = "";
		if (operator == Operators.values()[0]) {
			comparator = ">=";
		} else if (operator == Operators.values()[1]) {
			comparator = "<=";
		} else if (operator == Operators.values()[2]) {
			comparator = "==";
		}
		return tree.rangeSearch(value, comparator);
	}
}