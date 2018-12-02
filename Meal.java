import java.util.List;
import java.util.ArrayList;

/**
 * This class represents a meal with all the food items it contains
 * 
 * @author Shannon Morison, Grant Perry, Kevin Boening, Billy Kirk 
 *
 */
public class Meal {
	// list of all FoodItems within this meal
	private List<FoodItem> mealList;
	
	/**
	 * Meal constructor
	 */
	public Meal() {
		mealList = new ArrayList<>();
	}
	
	/**
	 * Accessor for mealList
	 * 
	 * @return mealList - the list of all FoodItems in this meal
	 */
	public List<FoodItem> getMealList() {
		return mealList;
	}
	
	/**
	 * Constructs a String containing the nutrition information for the meal
	 * 
	 * @return nutritionString a String containing the nutrition value in tabular format
	 */
	public String analyze() {
		String nutritionString = "Nutrition Information:\n";
		nutritionString += "Calories:\t\t\t" + getTotalNutrient(Nutrients.CALORIES)+ "\n";
		nutritionString += "Carbohydrates:\t\t" + getTotalNutrient(Nutrients.CARBOHYDRATES) + " g\n";
		nutritionString += "Fat:\t\t\t\t" + getTotalNutrient(Nutrients.FAT) + " g\n";
		nutritionString += "Protein:\t\t\t\t" + getTotalNutrient(Nutrients.PROTEIN) + " g\n";
		nutritionString += "Fiber:\t\t\t\t" + getTotalNutrient(Nutrients.FIBER) + " g";
		return nutritionString;
	}
	
	/**
	 * Adds one FoodItem to the Meal
	 * 
	 * @param food FoodItem to add
	 */
	public void addToMeal(FoodItem food) {
		mealList.add(food);
	}
	
	/**
	 * Removes one FoodItem from the meal
	 * 
	 * @param food FoodItem to remove
	 */
	public void removeFromMeal(FoodItem food) {
		mealList.remove(food);
	}
	
	/*
	private double getTotalCalories() {
		double calorieCount = 0.0;
		for (FoodItem f : mealList) {
			calorieCount += f.getNutrientValue("CALORIES");
		}
		return calorieCount;
	}
	
	private double getTotalFat() {
		double fatCount = 0.0;
		for (FoodItem f : mealList) {
			fatCount += f.getNutrientValue("FAT");
		}
		return fatCount;
	}
	
	private double getTotalCarbohydrates() {
		double carbCount = 0.0;
		for (FoodItem f : mealList) {
			carbCount += f.getNutrientValue("CARBOHYDRATES");
		}
		return carbCount;
	}
	
	private double getTotalFiber() {
		double fiberCount = 0.0;
		for (FoodItem f : mealList) {
			fiberCount += f.getNutrientValue("FIBER");
		}
		return fiberCount;
	}
	
	private double getTotalProtein() {
		double proteinCount = 0.0;
		for (FoodItem f : mealList) {
			proteinCount += f.getNutrientValue("PROTEIN");
		}
		return proteinCount;
	}
	*/
	
	/**
	 * Computes the total amount of a given nutrient and returns that value
	 * 
	 * @param n the nutrient to compute
	 * @return the amount of the nutrient n in the Meal
	 */
	public double getTotalNutrient(Nutrients n) {
		double nutrientAmount = 0.0;
		for (FoodItem f : mealList) {
			nutrientAmount += f.getNutrientValue(n.toString());
		}
		return nutrientAmount;
	}
	
}