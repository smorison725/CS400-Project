import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a food item with all its properties.
 * 
 * @author Shannon Morison, Grant Perry, Kevin Boening, Billy Kirk
 */
public class FoodItem implements Comparable {
    // The name of the food item.
    private String name;

    // The id of the food item.
    private String id;

    // Map of nutrients and value.
    private HashMap<String, Double> nutrients;
    
    /**
     * Constructor
     * @param name name of the food item
     * @param id unique id of the food item 
     */
    public FoodItem(String id, String name) {
    	this.id = id;
    	this.name = name;
    	this.nutrients = new HashMap<String, Double>();
    }
    
    /**
     * Gets the name of the food item
     * 
     * @return name of the food item
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the unique id of the food item
     * 
     * @return id of the food item
     */
    public String getID() {
        return id;
    }
    
    /**
     * Gets the nutrients of the food item
     * 
     * @return nutrients of the food item
     */
    public HashMap<String, Double> getNutrients() {
        return nutrients;
    }

    /**
     * Adds a nutrient and its value to this food. 
     * If nutrient already exists, updates its value.
     */
    public void addNutrient(String name, Double value) {
        nutrients.put(name, value);
    }

    /**
     * Returns the value of the given nutrient for this food item. 
     * If not present, then returns 0.
     */
    public double getNutrientValue(String name) {
        Double value = nutrients.get(name);
        if (value == null) {
        	return 0;
        }
        else {
        	return (double)value;
        }
    }
    
    /**
     * Determines if a food item should come before or after another (ordering based on the alphabetic order
     * of the food names)
     * 
     * @param foodToCompare- the food we're looking to order 
     * @return -1 if the current food item's name comes before the food being compared
     * 			0 if the current food item has the exact same name as the food being compared
     * 			1 if the current food item's name comes after the food being compared
     */
	@Override
	public int compareTo(Object o) {
		FoodItem foodToCompare = (FoodItem)o; //We can only compare two food items, so cast the parameter to a FoodItem
		String nameCompared = foodToCompare.getName();
    	return (this.getName().compareTo(nameCompared)); //Use String's compareTo method to compare the names of the two foods
	}
    
}
