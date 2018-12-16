package application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.IOException;

/**
 * This class represents the backend for managing all the operations associated with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu)
 */
public class FoodData implements FoodDataADT<FoodItem> {

  // List of all the food items.
  private TreeSet<FoodItem> foodItemList;

  // Map of nutrients and their corresponding index
  private HashMap<String, BPTree<Double, FoodItem>> indexes;

  // Set of all IDs, used to eliminate duplicates
  private HashSet<String> ids;


  /**
   * Public constructor
   */
  public FoodData() {
    foodItemList = new TreeSet<>();
    indexes = new HashMap<>();
    for (Nutrients n : Nutrients.values()) {
      indexes.put(n.toString(), new BPTree<>(5));
    }
    ids = new HashSet<>();
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
  private static FoodItem createFoodItem(String id, String name, double calories, double fat,
      double carbs, double fiber, double protein) {
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
   * 
   * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
   */
  @Override
  public void loadFoodItems(String filePath) {
    // when loading a new file, create a new FoodData object
    foodItemList = new TreeSet<>();
    for (Nutrients n : Nutrients.values()) {
      indexes.put(n.toString(), new BPTree<>(5));
    }
    ids = new HashSet<>();
    try (Scanner fileScanner = new Scanner(new File(filePath))) {
      String line;
      while (fileScanner.hasNextLine()) {
        line = fileScanner.nextLine();
        String[] foodItemData = line.split(",");

        // if input line does not contain 12 pieces, skip this line
        if (foodItemData.length != 12) {
          continue;
        }

        // grab id and name for FoodItem
        String id = foodItemData[0];
        String name = foodItemData[1];

        // if id or name is blank, skip
        if (id.equals("") || name.equals("")) {
          continue;
        }
        try { // parse nutrient information and add FoodItem to the foodItemList
          // set up index variables for each nutrient
          int calorieIndex = 0;
          int fatIndex = 0;
          int carbIndex = 0;
          int fiberIndex = 0;
          int proteinIndex = 0;

          // find correct index for each nutrient
          for (int i = 2; i < foodItemData.length; i += 2) {
            if (foodItemData[i].equals("calories")) {
              calorieIndex = i + 1;
            } else if (foodItemData[i].equals("fat")) {
              fatIndex = i + 1;
            } else if (foodItemData[i].equals("carbohydrate")) {
              carbIndex = i + 1;
            } else if (foodItemData[i].equals("fiber")) {
              fiberIndex = i + 1;
            } else if (foodItemData[i].equals("protein")) {
              proteinIndex = i + 1;
            }
          }

          // if any of the indices are still 0, skip this line
          if (calorieIndex == 0 || fatIndex == 0 || carbIndex == 0 || fiberIndex == 0
              || proteinIndex == 0) {
            continue;
          }
          
          // associate parsed values with the associated nutrient
          Double calories = Double.parseDouble(foodItemData[calorieIndex]);
          Double fat = Double.parseDouble(foodItemData[fatIndex]);
          Double carbs = Double.parseDouble(foodItemData[carbIndex]);
          Double fiber = Double.parseDouble(foodItemData[fiberIndex]);
          Double protein = Double.parseDouble(foodItemData[proteinIndex]);
          FoodItem newFood = createFoodItem(id, name, calories, fat, carbs, fiber, protein);
          addFoodItem(newFood);

        } catch (NumberFormatException ne) {
          continue;
        } catch (IllegalArgumentException ia) {
          continue;
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see skeleton.FoodDataADT#filterByName(java.lang.String)
   */
  @Override
  public List<FoodItem> filterByName(String substring) {
    NameFilter nmFilt = new NameFilter(substring);
    return nmFilt.executeFilter(new ArrayList<>(foodItemList));
  }

  /*
   * (non-Javadoc)
   * 
   * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
   */
  @Override
  public List<FoodItem> filterByNutrients(List<String> rules) {
    List<FoodItem> resultList = new ArrayList<>();
    for (String r : rules) {
      String[] pieces = r.split(" ");
      if (pieces.length != 3) {
        continue; // ignore invalid rule input, just continue to the next one
      }
      try {
        NutrientFilter nutFilt =
            new NutrientFilter(pieces[0], pieces[1], Double.parseDouble(pieces[2])); // create a
                                                                                     // filter based
                                                                                     // on the
                                                                                     // current rule
        List<FoodItem> tempList = new ArrayList<>();

        // execute the current filter, store in temporary list
        tempList = nutFilt.executeFilter(indexes.get(pieces[0]));

        // for the first rule, just add all the matching food
        if (resultList.size() == 0) {
          resultList.addAll(tempList);
        } else {
          for (FoodItem f : resultList) {

            // remove every food in resultList that is not in tempList
            if (!tempList.contains(f)) {
              resultList.remove(f);
            }
          }
        }
      } catch (NumberFormatException e) {
        continue; // if we can't parse the input rule, skip it
      }
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
   */
  @Override
  public void addFoodItem(FoodItem foodItem) {
    // only add if there is no matching id present
    if (!ids.contains(foodItem.getID())) {
      // update all instance variables with new item
      foodItemList.add(foodItem);
      ids.add(foodItem.getID());
      for (Nutrients n : Nutrients.values()) {
        indexes.get(n.toString()).insert(foodItem.getNutrientValue(n.toString()), foodItem);
      }
    } else { // throw exception if there is a duplicate
      throw new IllegalArgumentException();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see skeleton.FoodDataADT#getAllFoodItems()
   */
  @Override
  public List<FoodItem> getAllFoodItems() {
    return new ArrayList<>(foodItemList);
  }


  /*
   * (non-Javadoc)
   * 
   * @see FoodDataADT#saveFoodItems(java.lang.String)
   */
  @Override
  public void saveFoodItems(String filename) {
    try {
      FileWriter fw = new FileWriter(new File(filename));
      PrintWriter pw = new PrintWriter(fw);

      // construct TreeSet of the contents of foodItemList
      TreeSet<FoodItem> foodSet = new TreeSet<>(foodItemList);

      for (FoodItem f : foodSet) { // write out one line for each FoodItem
        pw.println(f.getID() + "," + f.getName() + ",calories," + f.getNutrientValue("CALORIES")
            + ",fat," + f.getNutrientValue("FAT") + ",carbohydrate,"
            + f.getNutrientValue("CARBOHYDRATES") + ",fiber," + f.getNutrientValue("FIBER")
            + ",protein," + f.getNutrientValue("PROTEIN"));
      }

      // close file resources
      pw.close();
      fw.close();

    } catch (IOException ioe) {
      System.out.println(ioe.getMessage());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public static void main(String[] args) {
    // read a data file
    // call other methods
    // instantiate an ADT
    FoodData testFoods = new FoodData();
    testFoods.loadFoodItems("foodItems.txt");
    for (FoodItem food : testFoods.getAllFoodItems()) {
      System.out.println(food.getName());
    }
    List<String> testFilters = new ArrayList<String>();
    testFilters.add("CALORIES == 200");
    List<FoodItem> testing = testFoods.indexes.get("CALORIES").rangeSearch(50.0, "<=");
    for (FoodItem food : testing) {
      System.out.println(food.getName());
    }
    List<FoodItem> filteredFoods = testFoods.filterByNutrients(testFilters);
    for (FoodItem food : filteredFoods) {
      System.out.println(food.getNutrientValue("CALORIES"));
    }
  }
}
