package application;

import java.util.ArrayList;
import java.util.List;

/**
 * A filter that finds all FoodItems with a given name
 * 
 * @author Shannon Morison, Grant Perry, Kevin Boening, Billy Kirk
 *
 */
public class NameFilter {
	// Name of the FoodItem we are searching for
	private String nameToCompare;
	
	/**
	 * Constructor
	 * 
	 * @param name the name we want to filter on
	 */
	public NameFilter(String name) {
		nameToCompare = name.toLowerCase();
	}
	
	/**
	 * Accessor for nameToCompare
	 * 
	 * @return nameToCompare
	 */
	public String getName() {
		return nameToCompare;
	}
	
	/**
	 * Mutator for nameToCompare
	 * 
	 * @param name new value for nameToCompare
	 */
	public void setName(String name) {
		nameToCompare = name;
	}
	
	/**
	 * Gets the list of all the FoodItems that have the name we're looking for
	 * 
	 * @param tree tree of values with the name as keys and the FoodItem as values
	 * @return List of all FoodItems with this name
	 */
	public List<FoodItem> executeFilter(List<FoodItem> allFoodsList) {
		ArrayList<FoodItem> targetFoods = new ArrayList<>();
		for (FoodItem f : allFoodsList) {
			if (f.getName().toLowerCase().contains(nameToCompare)) {
				targetFoods.add(f);
			}
		}
		return targetFoods;
	}
}
