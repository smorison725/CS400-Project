import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.IOException;

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
        for (Nutrients n : Nutrients.values()) {
          indexes.put(n.toString(), new BPTree<>(5));
        }
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
        NameFilter nmFilt = new NameFilter(substring);
        return nmFilt.executeFilter(foodItemList);
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
        List<FoodItem> resultList = new ArrayList<>();
        for (String r : rules) {
          String[] pieces = r.split(" ");
          if (pieces.length != 3) {
            continue;
          }
          try {
            NutrientFilter nutFilt = new NutrientFilter(pieces[0], pieces[1], 
                Double.parseDouble(pieces[2]));
            List<FoodItem> tempList = new ArrayList<>();
            tempList = nutFilt.executeFilter(indexes.get(pieces[0]));
            if (resultList.size() == 0) {
              resultList.addAll(tempList);
            } else {
              for (FoodItem f : resultList) {
                if (!tempList.contains(f)) {
                  resultList.remove(f);
                }
              }
            }
          } catch (NumberFormatException e) {
            continue;
          }          
        }
        return resultList;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
        foodItemList.add(foodItem);
        for (Nutrients n : Nutrients.values()) {
          indexes.get(n.toString()).insert(foodItem.getNutrientValue(n.toString()), foodItem);
        }
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#getAllFoodItems()
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
        return foodItemList;
    }


	/*
	 * (non-Javadoc)
	 * @see FoodDataADT#saveFoodItems(java.lang.String)
	 */
    @Override
	public void saveFoodItems(String filename) {
		try {
		  FileWriter fw = new FileWriter(new File(filename));
		  PrintWriter pw = new PrintWriter(fw);
		  TreeSet<FoodItem> foodSet = new TreeSet<>();
		  foodSet.addAll(foodItemList);
		  for (FoodItem f : foodSet) {
		    pw.println(f.getID()+","+f.getName()+",calories,"+f.getNutrientValue("CALORIES")+",fat"
		        +f.getNutrientValue("FAT")+",carbohydrate,"+f.getNutrientValue("CARBOHYDRATES")
		        +",fiber,"+f.getNutrientValue("FIBER")+",protein,"+f.getNutrientValue("PROTEIN"));
		  }
		} catch (IOException ioe) {
		  System.out.println(ioe.getMessage());
		} catch (Exception e) {
		  System.out.println(e.getMessage());
		}
	}
}