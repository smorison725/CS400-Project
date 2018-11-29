import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * This class is the main backbone of the food query/meal analysis program. It sets up all the data structures needed
 * to add food items to a list, create meals, analyze meals, and filter food items. 
 * 
 * It also runs the UI for the program.
 * 
 * @author Shannon Morison, Grant Perry, Kevin Boening, Billy Kirk
 */
public class Main {
	public static TreeSet<FoodItem> foodList; //Stores the complete list of food items
	
/**
 * Constructor for the Main class. It doesn't take any parameters but it does initialize the food list
 */
	public Main () {
		foodList = new TreeSet<FoodItem>();
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
	
    /**
     * Adds a single food to the main food list and all associated nutrient trees
     * 
     * @param food-- the food item being added
     */
	public static void addFood (FoodItem food) {
		foodList.add(food);
	}
	
	 /**
     * Parses the given text file and adds each food to the food list 
     * 
     * @param filePath- the path and name of the file containing the food items
     */
	public static void parseFoodList (String filePath) {
		foodList = new TreeSet<FoodItem>();
		try {
			FileReader file = new FileReader(filePath);
			BufferedReader bufferedReader = new BufferedReader(file);  //Create a buffered reader so we can read each line of the input file
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] dataItems = line.split(","); //Split the line around the commas to get the relevant data elements
				String id = dataItems[0];
				String name = dataItems[1];
				if (id == null || name == null) {
					continue; //Skip this iteration of the while loop if we don't have a name or id for this line
				}
				try {
					Double calories = Double.parseDouble(dataItems[3]);
					Double fat = Double.parseDouble(dataItems[5]);
					Double carbs = Double.parseDouble(dataItems[7]);
					Double fiber = Double.parseDouble(dataItems[9]);
					Double protein = Double.parseDouble(dataItems[11]);
					FoodItem newFood = createFoodItem(id, name, calories, fat, carbs, fiber, protein); 
					addFood(newFood);
				}
				catch (NumberFormatException ne) {
					continue; //If we can't parse a given line and store the double values, just move to the next line--don't quit the program
				}	
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		parseFoodList("fooditems.txt");
		for (FoodItem food : foodList) {
			System.out.println(food.getName());
		}
	}

}
