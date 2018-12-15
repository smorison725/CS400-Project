package application;

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
	
	// variables for each nutrient
	private double totalCalories;
	private double totalCarbs;
	private double totalFat;
	private double totalProtein;
	private double totalFiber;
	
	/**
	 * Meal constructor
	 */
	public Meal() {
		mealList = new ArrayList<>();
		totalCalories = 0.0;
		totalCarbs = 0.0;
		totalFat = 0.0;
		totalProtein = 0.0;
		totalFiber = 0.0;
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
	 * @param food FoodItem to add, actually adds a copy of this food item
	 */
	public void addToMeal(FoodItem food) {
	  totalCalories += food.getNutrientValue(Nutrients.CALORIES.toString());
	  totalCarbs += food.getNutrientValue(Nutrients.CARBOHYDRATES.toString());
      totalFat += food.getNutrientValue(Nutrients.FAT.toString());
      totalProtein += food.getNutrientValue(Nutrients.PROTEIN.toString());
      totalFiber += food.getNutrientValue(Nutrients.FIBER.toString());
      FoodItem foodCopy = new FoodItem(food.getID(), food.getName());
      foodCopy.addNutrient(Nutrients.CALORIES.toString(), food.getNutrientValue(Nutrients.CALORIES.toString()));
      foodCopy.addNutrient(Nutrients.CARBOHYDRATES.toString(), food.getNutrientValue(Nutrients.CARBOHYDRATES.toString()));
      foodCopy.addNutrient(Nutrients.FAT.toString(), food.getNutrientValue(Nutrients.FAT.toString()));
      foodCopy.addNutrient(Nutrients.PROTEIN.toString(), food.getNutrientValue(Nutrients.PROTEIN.toString()));
      foodCopy.addNutrient(Nutrients.FIBER.toString(), food.getNutrientValue(Nutrients.FIBER.toString()));
	  mealList.add(foodCopy);
	}
	
	/**
	 * Adds one FoodItem to the Meal
	 * 
	 * @param food FoodItem to add, actually adds a copy of this food item
	 * @param index index in meal list to add the food item
	 * 
	 */
	public void addToMeal(int index, FoodItem food) {
	  totalCalories += food.getNutrientValue(Nutrients.CALORIES.toString());
	  totalCarbs += food.getNutrientValue(Nutrients.CARBOHYDRATES.toString());
      totalFat += food.getNutrientValue(Nutrients.FAT.toString());
      totalProtein += food.getNutrientValue(Nutrients.PROTEIN.toString());
      totalFiber += food.getNutrientValue(Nutrients.FIBER.toString());
      FoodItem foodCopy = new FoodItem(food.getID(), food.getName());
      foodCopy.addNutrient(Nutrients.CALORIES.toString(), food.getNutrientValue(Nutrients.CALORIES.toString()));
      foodCopy.addNutrient(Nutrients.CARBOHYDRATES.toString(), food.getNutrientValue(Nutrients.CARBOHYDRATES.toString()));
      foodCopy.addNutrient(Nutrients.FAT.toString(), food.getNutrientValue(Nutrients.FAT.toString()));
      foodCopy.addNutrient(Nutrients.PROTEIN.toString(), food.getNutrientValue(Nutrients.PROTEIN.toString()));
      foodCopy.addNutrient(Nutrients.FIBER.toString(), food.getNutrientValue(Nutrients.FIBER.toString()));
	  mealList.add(index, foodCopy);
	}
	
	/**
	 * Removes one FoodItem from the meal
	 * 
	 * @param food FoodItem to remove
	 */
	public void removeFromMeal(FoodItem food) {
	  totalCalories -= food.getNutrientValue(Nutrients.CALORIES.toString());
      totalCarbs -= food.getNutrientValue(Nutrients.CARBOHYDRATES.toString());
      totalFat -= food.getNutrientValue(Nutrients.FAT.toString());
      totalProtein -= food.getNutrientValue(Nutrients.PROTEIN.toString());
      totalFiber -= food.getNutrientValue(Nutrients.FIBER.toString());
	  mealList.remove(food);
	}
	
	/**
	 * Computes the total amount of a given nutrient and returns that value
	 * 
	 * @param n the nutrient to compute
	 * @return the amount of the nutrient n in the Meal
	 */
	public double getTotalNutrient(Nutrients n) {
		if (n.equals(Nutrients.CALORIES)) {
		  return totalCalories;
		} else if (n.equals(Nutrients.CARBOHYDRATES)) {
		  return totalCarbs;
		} else if (n.equals(Nutrients.FAT)) {
		  return totalFat;
		} else if (n.equals(Nutrients.FIBER)) {
		  return totalFiber;
		} else if (n.equals(Nutrients.PROTEIN)) {
		  return totalProtein;
		}
		return 0.0;
	}
	
}
