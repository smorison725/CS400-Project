package application;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;

/**
 * This class represents the backend for managing all 
 * the operations associated with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu)
 */
public class FoodData implements FoodDataADT<FoodItem> {
    
    // List of all the food items.
    private List<FoodItem> foodItemList;

    // Map of nutrients and their corresponding index
    private HashMap<String, BPTree<Double, FoodItem>> indexes;
    
    
    /**
     * Public constructor
     */
    public FoodData() {
        foodItemList = new ArrayList<>();
        indexes = new HashMap<>();
    }
    
    /**
     * Creates a new food item from a given set of data about that food item
     * 
     * @param id- the id of the new food item
     * @param name- the name of the new food item
     * @param calories- the number of calories in the food item
     * @param fat- the number of fat grams in the food item
     * @param carbs- the number of carbohydrate grams in the food item
     * @param fiber- the number of fiber grams in the food item
     * @param protein- the number of protein grams in the food item
     * @return a food item object
     */
	private static FoodItem createFoodItem (String id, String name, double calories, double fat, double carbs, double fiber, double protein) {
		FoodItem food = new FoodItem(id, name);
		food.addNutrient(Nutrients.CALORIES.toString(), calories);
		food.addNutrient(Nutrients.FAT.toString(), fat);
		food.addNutrient(Nutrients.CARBOHYDRATES.toString(), carbs);
		food.addNutrient(Nutrients.FIBER.toString(), fiber);
		food.addNutrient(Nutrients.PROTEIN.toString(), protein);
		return food;
	}    
    
    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
     */
    @Override
    public void loadFoodItems(String filePath) {
        try {
        	Scanner fileScanner = new Scanner(new File(filePath));
        	String line;
        	while (fileScanner.hasNextLine()) {
        		line = fileScanner.nextLine();
        		String[] foodItemData = line.split(",");
        		String id = foodItemData[0];
        		String name = foodItemData[1];
        		if ((id == null || id.equals("")) || (name == null || name.equals(""))) {
        			continue;
        		}
        		try {
        			Double calories = Double.parseDouble(foodItemData[3]);
					Double fat = Double.parseDouble(foodItemData[5]);
					Double carbs = Double.parseDouble(foodItemData[7]);
					Double fiber = Double.parseDouble(foodItemData[9]);
					Double protein = Double.parseDouble(foodItemData[11]);
					FoodItem newFood = createFoodItem(id, name, calories, fat, carbs, fiber, protein);
					addFoodItem(newFood);
        		} catch (NumberFormatException ne){
        			continue;
        		}
        	}
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
        NameFilter nf = new NameFilter(substring);
        return nf.executeFilter(foodItemList);
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
        // TODO : Complete
        return null;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
        // TODO : Complete
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#getAllFoodItems()
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
        // TODO : Complete
        return null;
    }


	@Override
	public void saveFoodItems(String filename) {
		// TODO Auto-generated method stub
		
	}

}
