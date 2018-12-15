import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;

/**
 * This class is the main backbone of the food query/meal analysis program. It sets up all the data structures needed
 * to add food items to a list, create meals, analyze meals, and filter food items. 
 * 
 * It also runs the UI for the program.
 * 
 * @author Shannon Morison, Grant Perry, Kevin Boening, Billy Kirk
 */
public class Main extends Application {
	public static FoodData foodList; //Stores the complete list of food items
	private static ListView<FoodItem> foodListView; //Stores the food list that we're viewing
	private static List<FoodItem> filteredFoods; //Stores the filtered list of foods
  	private static final Label totalName = new Label("Total");
  	private static final Label totalCalories = new Label(Double.toString(0.0));
  	private static final Label totalCarbs = new Label(Double.toString(0.0));
  	private static final Label totalFat = new Label(Double.toString(0.0));
  	private static final Label totalFiber = new Label(Double.toString(0.0));
  	private static final Label totalProtein = new Label(Double.toString(0.0));
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			foodList = new FoodData(); //Instantiate the foodList
			Meal meal = new Meal();
			foodList.loadFoodItems("foodItems.txt"); //Set the food item list to the initial path
			filteredFoods = new ArrayList<FoodItem>();
			filteredFoods = foodList.getAllFoodItems(); //Set the filtered list to the full list initially
			//Scene will consist of a VBox. The top node of the box will just be the exit button, and the bottom object will be a split pane
			VBox root = new VBox();
	        SplitPane split = new SplitPane();
	        VBox left = new VBox();
	        VBox right = new VBox();
	        GridPane mealGrid = new GridPane();
	        ScrollPane mealScrollPane = new ScrollPane(mealGrid);
	        GridPane nutritionGrid = new GridPane();
	        ScrollPane nutritionScrollPane = new ScrollPane(nutritionGrid);
	        nutritionScrollPane.setFitToWidth(true);
	        
	        
	        //Exit button
	        Button exitProgram = new Button("\u2613");
	        exitProgram.setShape(new Circle(0.02));
	        exitProgram.getStyleClass().add("exit-button");
	        
	        //Filter selection dialogue
	        GridPane addFilt = new GridPane();
	        ColumnConstraints filtCol1 = new ColumnConstraints();
	        filtCol1.setPercentWidth(30);
	        ColumnConstraints filtCol2 = new ColumnConstraints();
	        filtCol2.setPercentWidth(20);
	        ColumnConstraints filtCol3 = new ColumnConstraints();
	        filtCol3.setPercentWidth(30);
	        ColumnConstraints filtCol4 = new ColumnConstraints();
	        filtCol4.setPercentWidth(20);
	        addFilt.getColumnConstraints().addAll(filtCol1, filtCol2, filtCol3, filtCol4);
	        addFilt.setHgap(10);
	        ComboBox<String> type = new ComboBox<>();
	        type.getItems().addAll("Name", "Calories", "Carbohydrates", "Fat", "Fiber", "Protein");
	        ComboBox<String> operator = new ComboBox<>();
	        operator.getItems().addAll("contains", "=", "<=", ">=");
	        TextField tf3 = new TextField();
	        Button addFilter = new Button("Apply filter");
	        
	        //only enable the apply filter button if all three criteria have something populated
	        addFilter.setDisable(true);
	        type.focusedProperty().addListener((ov, oldValue, newValue) -> {  
	            if((newValue != null) && (operator.getValue()!= null) && (!tf3.getText().isEmpty())) { addFilter.setDisable(false); }
	            else { addFilter.setDisable(true); }
	        });
	        operator.focusedProperty().addListener((ov, oldValue, newValue) -> {  
	            if((newValue != null) && (type.getValue()!= null) && (!tf3.getText().isEmpty())) { addFilter.setDisable(false); }
	            else { addFilter.setDisable(true); }
	        }); 
	        tf3.focusedProperty().addListener((ov, oldValue, newValue) -> {  
	            if((newValue != null) && (type.getValue()!= null) && (operator.getValue() != null)) { addFilter.setDisable(false); }
	            else { addFilter.setDisable(true); }
	        });
	        
	        //Add the filter selection objects to a grid pane
	        addFilter.getStyleClass().add("custom-button");
	        addFilt.add(type, 0, 0);
	        addFilt.add(operator, 1, 0);
	        addFilt.add(tf3, 2, 0);
	        addFilt.add(addFilter, 3, 0);
	        
	        //Section for currently applied filters
	        List<HBox> appliedFilters = new ArrayList<HBox>(); //Create a  list to store all the filters
	        Label currFilt = new Label("Current filters");
	        currFilt.getStyleClass().add("custom-subheader");
	        Button removeFilt = new Button("Remove filters \u2613");
	        removeFilt.getStyleClass().add("custom-button");
	        removeFilt.getStyleClass().add("remove-button");
	        Region region1 = new Region();
	        HBox.setHgrow(region1, Priority.ALWAYS);
	        Region region2 = new Region();
	        HBox.setHgrow(region2, Priority.ALWAYS);
	        HBox filters = new HBox();
	        filters.getChildren().addAll(currFilt, region2);
	        Label foodsLabel = new Label("Available Foods");
	        foodsLabel.getStyleClass().add("custom-header");
	        Region region3 = new Region();
	        HBox.setHgrow(region3, Priority.ALWAYS);
	        Region region4 = new Region();
	        HBox.setHgrow(region4, Priority.ALWAYS);
	        HBox foodsTitleHBox = new HBox();
	        foodsTitleHBox.getChildren().addAll(region3, foodsLabel, region4);
	        
	        //Add all of these into the filter VBox
	        VBox filterVBox = new VBox();
	        filterVBox.getChildren().addAll(addFilt, filters);
	        filterVBox.getChildren().addAll(appliedFilters);
	        filterVBox.getChildren().addAll(removeFilt);
	        filterVBox.setSpacing(7.5);
	        
	           //Meal Nutrition Grid
            Label mealNutrition = new Label("Meal Nutrition");
            mealNutrition.getStyleClass().add("custom-header");
            
            setUpNutritionGrid(nutritionGrid);
            final Label blankCol1 = new Label("");
            final Label blankCol2 = new Label("");
            final Label blankCol3 = new Label("");
            final Label blankCol4 = new Label("");
            final Label blankCol5 = new Label("");
            final Label blankCol6 = new Label("");
            
            // add blank row above totals
            nutritionGrid.add(blankCol1, 0, 1, 1, 1);
            nutritionGrid.add(blankCol2, 1, 1, 1, 1);
            nutritionGrid.add(blankCol3, 2, 1, 1, 1);
            nutritionGrid.add(blankCol4, 3, 1, 1, 1);
            nutritionGrid.add(blankCol5, 4, 1, 1, 1);
            nutritionGrid.add(blankCol6, 5, 1, 1, 1);
            
            // add totals row
            nutritionGrid.add(totalName, 0, 2, 1, 1);
            nutritionGrid.add(totalCalories, 1, 2, 1, 1);
            nutritionGrid.add(totalCarbs, 2, 2, 1, 1);
            nutritionGrid.add(totalFat, 3, 2, 1, 1);
            nutritionGrid.add(totalFiber, 4, 2, 1, 1);
            nutritionGrid.add(totalProtein, 5, 2, 1, 1);
            
            // bold columns in total row
            totalName.getStyleClass().add("label-bold");
            totalCalories.getStyleClass().add("label-bold");
            totalCarbs.getStyleClass().add("label-bold");
            totalFat.getStyleClass().add("label-bold");
            totalFiber.getStyleClass().add("label-bold");
            totalProtein.getStyleClass().add("label-bold");
	        
	        //Apply Filter action event that occurs when the Apply Filter button is pressed
	        EventHandler<ActionEvent> applyFilter = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e) {
	        		String filterName = null; //Get the values place in the filter field and add them all to the current filters
	        		String operatorName = null;
	        		String valueName = null;
	        		Double dValue;
	        		Alert fail = new Alert(AlertType.ERROR);
	        		try {
	        			filterName = (String)type.getValue();
	        			operatorName = (String)operator.getValue();
	        			valueName = tf3.getText();
	        			
	        			// don't allow name filter for non-contains operator, similarly don't allow contains to be used with non-Name filter
	        			if ((filterName.equals("Name") && !operatorName.equals("contains")) 
	        					|| (!filterName.equals("Name") && operatorName.equals("contains"))) { 
	        				fail.setTitle("Incompatible Operation");
	        				fail.setContentText(operatorName + " is incompatible with a filter of " + filterName);
	        				fail.setHeaderText(null);
	        				fail.setGraphic(null);
	        				type.valueProperty().set(null);
	        				operator.valueProperty().set(null);
	        				tf3.clear();
	        				fail.showAndWait();
	        				addFilter.setDisable(true);
	        				return;
	        			}
	        			
	        			// don't allow empty value field
	        			if (valueName.equals("")) {
	        				fail.setTitle("Invalid Value");
	        				fail.setContentText("Cannot filter on empty value");
	        				fail.setHeaderText(null);
	        				fail.setGraphic(null);
	        				type.valueProperty().set(null);
	        				operator.valueProperty().set(null);
	        				tf3.clear();
	        				fail.showAndWait();
	        				addFilter.setDisable(true);
	        				return;
	        			}
	        			
	        			// don't allow negative or non-numeric values to be used with the non-contains operator 
	        			// (at this point we know that the operator and filter name are compatible)
	        			if (!operatorName.equals("contains")) {
	        				dValue = Double.parseDouble(valueName);
	        				if (dValue < 0.0) {
	        					throw new NumberFormatException();
	        				}
	        			}
	        		} catch (NumberFormatException nf) {
	        			fail.setTitle("Invalid Number Value");
	        			fail.setContentText("Filter value for " + operatorName + " operator must be a positive number");
	        			fail.setHeaderText(null);
	        			fail.setGraphic(null);
	        			type.valueProperty().set(null);
	        			operator.valueProperty().set(null);
	        			tf3.clear();
	        			fail.showAndWait();
	        			addFilter.setDisable(true);
	        			return;
	        		}
	    	        Label lbl1 = new Label(filterName);
	    	        Label lbl2 = new Label(operatorName);
	    	        Label lbl3 = new Label(valueName);
	    	        HBox currFilter = new HBox();
	    	        currFilter.getChildren().addAll(lbl1, lbl2, lbl3);
	    	        currFilter.setSpacing(10.0);
	    	        appliedFilters.add(currFilter);
	    	        type.valueProperty().set(null); //Clear out the fields to prepare them for new values 
	    	        operator.valueProperty().set(null);
	    	        tf3.clear();
	    	        filterVBox.getChildren().clear();
	    	        filterVBox.getChildren().addAll(addFilt, filters); //Add all the labels back to the filter section so it appear appropriately
	    	        filterVBox.getChildren().addAll(appliedFilters);
	    	        filterVBox.getChildren().addAll(removeFilt);
	    	        List<FoodItem> tempList = new ArrayList<FoodItem>();
	    	        if (filterName == "Name" && valueName != null) {
	    	        	tempList = foodList.filterByName(valueName); //If the user chooses to filter on name, use that to find our temporary list
	    	        	filteredFoods.retainAll(tempList); //Use the retain all method to find an intersection
	    	        }
	    	        else if (valueName != null) {
		    	        List<String> filters = new ArrayList<String>();
		    	        if (operatorName.contentEquals("=")) {
		    	        	operatorName = "=="; //Update the operator name to ==
		    	        }
		    	        filters.add(filterName.toUpperCase() + " " + operatorName + " " + valueName);
	    	        	if (foodListView.getItems().size() > 0) {
	    	        		tempList = foodList.filterByNutrients(filters);
	    	        	}
	    	        	filteredFoods.retainAll(tempList);
	    	        }
	    	        foodListView.getItems().clear(); //Reprint the filtered list
        			for (FoodItem food : filteredFoods) {
        				addFoodToFilterList(foodListView, food);
        			}
        			
        			// disable button after applying since we clear out fields
        			addFilter.setDisable(true);
	        	}
	        };
	        
	      //Remove Filters action event that occurs when the Remove Filters button is pressed
	        EventHandler<ActionEvent> removeFilters = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e) {
	        		appliedFilters.clear(); //Remove all the filters from the list
	        		filterVBox.getChildren().clear();
	    	        filterVBox.getChildren().addAll(addFilt, filters); //Add all the labels back to the filter section so it appear appropriately
	    	        filterVBox.getChildren().addAll(appliedFilters);
	    	        filterVBox.getChildren().addAll(removeFilt);
	    	        foodListView.getItems().clear(); //Reprint the full list of foods
        			for (FoodItem food : foodList.getAllFoodItems()) {
        				addFoodToFilterList(foodListView, food);
        			}
        			filteredFoods = foodList.getAllFoodItems(); //Reset the filtered list to everything
	        	}
	        };
	        
	        addFilter.setOnAction(applyFilter); //Apply the filter when the button is pressed
	        removeFilt.setOnAction(removeFilters); //Remove all filters when the button is pressed
	        
	        //Button to add a food to the meal from the list. Only enabled if the food is selected
	        Button addToMeal = new Button("Add to meal \u2794");
	        
	        Region addMealRegion = new Region();
	        HBox.setHgrow(addMealRegion, Priority.ALWAYS);
	        HBox addFoodToMeal = new HBox();
	        addToMeal.setDisable(true);
	        addToMeal.getStyleClass().add("custom-button");
	        addFoodToMeal.getChildren().addAll(addMealRegion, addToMeal);
	        
	        //Add foods that met the filter
	        VBox foodListVBox = new VBox();
	        foodListView = new ListView<>();
	        for (FoodItem food : foodList.getAllFoodItems()) {
		        addFoodToFilterList(foodListView, food);
	        }
	        foodListView.focusedProperty().addListener(new ChangeListener<Boolean>() {
	            @Override
	            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasFocused,
	                Boolean isFocused) {
	              if (isFocused) {
	                addToMeal.setDisable(false);
	                addToMeal.setOnAction(new EventHandler<ActionEvent>() {
	                  @Override
	                  public void handle(ActionEvent event) {
	                	if (foodListView.getSelectionModel().getSelectedItem() != null) {
	                		FoodItem addFood = foodListView.getSelectionModel().getSelectedItem();

		                    // find correct spot to add the FoodItem
		                    int i;
		                    for (i = 0; i < meal.getMealList().size(); i++) {
		                    	if (meal.getMealList().get(i).getName().compareToIgnoreCase(addFood.getName()) > 0) {
		                    		break;
		                    	}
		                    }
		                    meal.addToMeal(i, addFood);
		                    
		                    // add row to the meal grid
		                    addFoodToCurrentMealGrid(i, mealGrid, addFood, nutritionGrid);

		                    // add remove button for food
		                    Button removeButton = addGridRemoveButton(mealGrid, 2, i);
		                    
		                    // add action for remove button
		                    removeButton.setOnAction(new EventHandler<ActionEvent>() {
		                      @Override
		                      public void handle(ActionEvent event) {
		                        // set up which rows we want to remove in each grid
		                        int mealRowToRemove = GridPane.getRowIndex(removeButton);
		                        int nutritionRowToRemove = mealRowToRemove + 1;
		                        
		                        // remove corresponding food from meal
		                        meal.removeFromMeal(meal.getMealList().get(GridPane.getRowIndex(removeButton)));
		                        
		                        // remove row from meal grid, bump up lower rows
		                        removeRow(mealGrid, mealRowToRemove);
		                        
		                        // remove row from nutrition grid, bump up lower rows
		                        removeRow(nutritionGrid, nutritionRowToRemove);

		                        // update totals row
		                        totalCalories.setText(Double.toString(meal.getTotalNutrient(Nutrients.CALORIES)));
		                        totalCarbs.setText(Double.toString(meal.getTotalNutrient(Nutrients.CARBOHYDRATES)));
		                        totalFat.setText(Double.toString(meal.getTotalNutrient(Nutrients.FAT)));
		                        totalFiber.setText(Double.toString(meal.getTotalNutrient(Nutrients.FIBER)));
		                        totalProtein.setText(Double.toString(meal.getTotalNutrient(Nutrients.PROTEIN)));
		                      }

		                    });

		                    // update totals
		                    totalCalories.setText(Double.toString(meal.getTotalNutrient(Nutrients.CALORIES)));
		                    totalCarbs.setText(Double.toString(meal.getTotalNutrient(Nutrients.CARBOHYDRATES)));
		                    totalFat.setText(Double.toString(meal.getTotalNutrient(Nutrients.FAT)));
		                    totalFiber.setText(Double.toString(meal.getTotalNutrient(Nutrients.FIBER)));
		                    totalProtein.setText(Double.toString(meal.getTotalNutrient(Nutrients.PROTEIN)));
	                	}
	                    
	                  }
	                });
	              } else {
	                addToMeal.setDisable(true);
	              }
	            }
	          });
	        
	        //Add food and save to file HBox
	        HBox addAndSaveH = new HBox();
	        Region addAndSaveRegion1 = new HBox();
	        HBox.setHgrow(addAndSaveRegion1, Priority.ALWAYS);
	        
	        //Button for user adding an individual food to the list
	        Button addFood = new Button("\uff0b Add food");
	        addFood.getStyleClass().add("custom-button");
	        addFood.setOnAction(new EventHandler<ActionEvent>() {
	        	//Will open a pop up dialogue when Add food is selected
	            @Override
	            public void handle(ActionEvent event) {
	                final Stage addFoodWindow = new Stage();
	                addFoodWindow.initModality(Modality.APPLICATION_MODAL);
	                addFoodWindow.initOwner(primaryStage);
	                VBox addFoodVBox = new VBox();
	                
	                GridPane addFoodGrid = new GridPane();
	                
	                //Fields in a grid for each parameter we need to make the food
	                addFoodGrid.add(new Label("Food Name"), 0, 0);
	                TextField foodName = new TextField();
	    	        addFoodGrid.add(foodName, 1, 0);
	    	        
	    	        addFoodGrid.add(new Label("Calories"), 0, 1);
	    	        TextField calories = new TextField();
	    	        addFoodGrid.add(calories, 1, 1);
	    	        
	    	        addFoodGrid.add(new Label("Carbohydrates"), 0, 2);
	    	        TextField carbs = new TextField();
	    	        addFoodGrid.add(carbs, 1, 2);
	    	        
	    	        addFoodGrid.add(new Label("Fat"), 0, 3);
	    	        TextField fat = new TextField();
	    	        addFoodGrid.add(fat, 1, 3);
	    	        
	    	        addFoodGrid.add(new Label("Fiber"), 0, 4);
	    	        TextField fiber = new TextField();
	    	        addFoodGrid.add(fiber, 1, 4);
	    	        
	    	        addFoodGrid.add(new Label("Protein"), 0, 5);
	    	        TextField protein = new TextField();
	    	        addFoodGrid.add(protein, 1, 5);
	    	        
	    	        addFoodGrid.setHgap(20);
	    	        addFoodGrid.setVgap(5);
	    	        
	    	        HBox addFoodHBox = new HBox();
	    	        
	    	        //Buttons at the bottom of the window for saving the food or exiting
	    	        Button saveFood = new Button("Save");
	    	        saveFood.getStyleClass().add("custom-button");
	    	        Button exitFood = new Button("Exit");
	    	        exitFood.getStyleClass().add("custom-button");
	    	        Region addFoodRegion1 = new Region();
	    	        HBox.setHgrow(addFoodRegion1, Priority.ALWAYS);
	    	        addFoodHBox.getChildren().addAll(saveFood, addFoodRegion1, exitFood);
	    	        
	    	        addFoodVBox.getChildren().addAll(addFoodGrid, addFoodHBox);
	    	        addFoodVBox.setSpacing(20);
	    	        addFoodVBox.setPadding(new Insets(5, 20, 5, 20));
	    	        
	    	        //Add this window to the scene and set it
	                Scene addFoodScene = new Scene(addFoodVBox, 350, 250);
	                addFoodScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	                addFoodGrid.setAlignment(Pos.TOP_CENTER);
	                addFoodWindow.setScene(addFoodScene);
	                addFoodWindow.show();
	                //Save button
	                saveFood.setOnAction(new EventHandler<ActionEvent>() {
	                	@Override
	                	public void handle(ActionEvent event) {
	                		String name;
	                		Double calsD;
	                		Double carbsD;
	                		Double fatD;
	                		Double fiberD;
	                		Double proteinD;
	                		FoodItem newFood = null;
	                		
	                		try {
	                			name = foodName.getText().trim();
	                			calsD = Double.valueOf(calories.getText().trim());
		                		carbsD = Double.valueOf(carbs.getText().trim());
		                		fatD = Double.valueOf(fat.getText().trim());
		                		fiberD = Double.valueOf(fiber.getText().trim());
		                		proteinD = Double.valueOf(protein.getText().trim());
		                	if (!(calsD >= 0.0) | !(carbsD >= 0.0) | !(fatD >= 0.0) | !(fiberD >= 0.0) | !(proteinD >= 0.0)) {
	                				Alert fail = new Alert(AlertType.ERROR);
	                				fail.setTitle("Invalid Nutrient");
	                				fail.setContentText("Nutrient values must be a number greater than or equal to zero");
	                				fail.setHeaderText(null);
	                				fail.setGraphic(null);
	                				calories.clear();
	                				carbs.clear();
	                				fat.clear();
	                				fiber.clear();
	                				protein.clear();
	                				foodName.clear();
	                				fail.showAndWait();
	                				return;
	                			}
		                	if(name.isEmpty()) {
		                			Alert fail = new Alert(AlertType.ERROR);
	                				fail.setTitle("Empty Name");
	                				fail.setContentText("Food must have a name");
	                				fail.setHeaderText(null);
	                				fail.setGraphic(null);
	                				calories.clear();
	                				carbs.clear();
	                				fat.clear();
	                				fiber.clear();
	                				protein.clear();
	                				foodName.clear();
	                				fail.showAndWait();
	                				return;
		                		}
		                	int nameHash = name.hashCode();
		                	String id = "" + nameHash + calsD.toString().replace(".", "") + carbsD.toString().replace(".", "") + fatD.toString().replace(".", "") + fiberD.toString().replace(".", "") + proteinD.toString().replace(".", "");
		                	newFood = createFoodItem(id, name, calsD, fatD, carbsD, fiberD, proteinD); 
	                		if(foodList.getIDs().contains(id)) {
	    							//If the user-entered food is already in the list, give them a pop up that indicates it's a duplicate
		                			Alert fail = new Alert(AlertType.ERROR);
	                				fail.setTitle("Food Already Exists");
	                				fail.setContentText(name + " is already in the list.");
	                				fail.setHeaderText(null);
	                				fail.setGraphic(null);
	                				calories.clear();
	                				carbs.clear();
	                				fat.clear();
	                				fiber.clear();
	                				protein.clear();
	                				foodName.clear();
	                				fail.showAndWait();
	                				return;
	    					}
		                	else {
		                			foodList.addFoodItem(newFood);
			                		//Once the food has been saved, re-sort the available foods list
			    					foodListView.getItems().clear();
				        			for (FoodItem food : foodList.getAllFoodItems()) {
				        				addFoodToFilterList(foodListView, food);
				        			}
				        			
				        			//Clear the grid dialog
			    					foodName.clear();
			    					calories.clear();
			    					carbs.clear();
			    					fat.clear();
			    					fiber.clear();
			    					protein.clear();
		                		}
	                		} 
	                		catch (NumberFormatException nf) {
	                			Alert fail = new Alert(AlertType.ERROR);
                				fail.setTitle("Invalid nutrient");
                				fail.setContentText("Nutrient values must be a number greater than or equal to zero");
                				fail.setHeaderText(null);
                				fail.setGraphic(null);
                				calories.clear();
                				carbs.clear();
                				fat.clear();
                				fiber.clear();
                				protein.clear();
                				foodName.clear();
                				fail.showAndWait();
	                		} 	                		
	                	}
	                	});
	                
	                //Exit button
	                exitFood.setOnAction(new EventHandler<ActionEvent>() {
	                	@Override
	                	public void handle(ActionEvent event) {
	                		addFoodWindow.close();
	                	}
	                	});}
	         });
	        
	        
	        //Button to save the available foods to a file
	        Button saveList = new Button("Save list");
	        saveList.getStyleClass().add("custom-button");
	        saveList.setOnAction(new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e) {
	        		FileChooser saveFile = new FileChooser();
	        		saveFile.setTitle("Save");
	        		File newFoodFile = saveFile.showSaveDialog(primaryStage);
	        		
	        		if(newFoodFile != null) {
	        			foodList.saveFoodItems(newFoodFile.getPath());
	        		}
	        	}
	        });
	        
	        addAndSaveH.getChildren().addAll(addFood, addAndSaveRegion1, saveList);
	        
	        foodListVBox.getChildren().addAll(addFoodToMeal, foodListView);
	        foodListVBox.setAlignment(Pos.CENTER);
	        foodListVBox.setSpacing(7.5);
	        
	        //Import file box, bottom left corner
	        HBox load = new HBox();
	        Button loadFile = new Button("Import");
	        loadFile.getStyleClass().add("custom-button");
	        Label filepath = new Label("Select file path");
	        load.getChildren().addAll(loadFile, filepath);
	        load.setSpacing(10.0);
	        load.setAlignment(Pos.CENTER_LEFT);
	        
	        //Load File event that occurs when the import button is pressed
	        EventHandler<ActionEvent> loadFileEvent = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e) {
	        		FileChooser importFile = new FileChooser();
	        		importFile.setTitle("Import");
	        		File newFoodList = importFile.showOpenDialog(primaryStage);
	        		if (newFoodList != null) {
	        			foodList.loadFoodItems(newFoodList.getPath());
	        			filepath.setText(newFoodList.getPath());
	        			foodListView.getItems().clear();
	        			for (FoodItem food : foodList.getAllFoodItems()) {
	        				addFoodToFilterList(foodListView, food);
	        			}
	        			filteredFoods = foodList.getAllFoodItems(); //Reset the filtered list to the new food items
		        		appliedFilters.clear(); //Remove all the filters from the list
		        		filterVBox.getChildren().clear();
		    	        filterVBox.getChildren().addAll(addFilt, filters); //Add all the labels back to the filter section so it appear appropriately
		    	        filterVBox.getChildren().addAll(appliedFilters);
		    	        filterVBox.getChildren().addAll(removeFilt);
	        		}
	        	}
	        };
	        
	        loadFile.setOnAction(loadFileEvent);
	        
	        //Add all the left side boxes
	        left.getChildren().addAll(filterVBox, foodsTitleHBox, foodListVBox, addAndSaveH, load);
	        left.setSpacing(20);
	        left.setPadding(new Insets(25, 25, 25, 25));
	        
	        //Current meal
	        Label currentMeal = new Label("Current meal");
	        currentMeal.getStyleClass().add("custom-header");
	        ColumnConstraints mealCol1 = new ColumnConstraints();
	        mealCol1.setPercentWidth(70);
	        ColumnConstraints mealCol2 = new ColumnConstraints();
	        mealCol2.setPercentWidth(30);
	        mealCol2.setHalignment(HPos.RIGHT);
	        mealGrid.getColumnConstraints().addAll(mealCol1, mealCol2);
	        
//	        //Current Meal Grid
//	        mealGrid.add(new Label("Pancakes"), 0, 0);
//	        addGridRemoveButton(mealGrid, 1, 0);
//	        
//	        mealGrid.add(new Label("Eggs"), 0, 1);
//	        addGridRemoveButton(mealGrid, 1, 1);
//	        
//	        mealGrid.add(new Label("Bacon"), 0, 2);
//	        addGridRemoveButton(mealGrid, 1, 2);
//	        
//	        mealGrid.add(new Label("Milk"), 0, 3);
//	        addGridRemoveButton(mealGrid, 1, 3);
	        
	        mealGrid.setHgap(20);
	        mealGrid.setVgap(5);
	        
	        //add foods from meal to nutrition table
//		    addNutritionTableRow(nutritionGrid, "Pancakes", 200, 20, 2, 2, 2, false);
//		    addNutritionTableRow(nutritionGrid, "Eggs", 100, 10, 1, 1, 1, false);
//		    addNutritionTableRow(nutritionGrid, "Bacon", 300, 30, 3, 3, 3, false);
//	        addNutritionTableRow(nutritionGrid, "Milk", 50, 5, 0, 0, 0, false);
	        
	        
	        
	        nutritionGrid.setVgap(5.0);
	        nutritionGrid.setHgap(0);
	        
	        Region region5 = new Region();
	        VBox.setVgrow(region5, Priority.ALWAYS);

	        right.getChildren().addAll(currentMeal, mealScrollPane, region5, mealNutrition, nutritionScrollPane);
	        right.setSpacing(25);
	        right.setPadding(new Insets(25, 25, 25, 25));
	        right.setAlignment(Pos.CENTER);
	        
	        //Add left and right sides together
	        split.getItems().addAll(left, right);
	        split.setStyle("-fx-box-border: transparent;");
	        root.getChildren().addAll(exitProgram, split);
	        root.setPadding(new Insets(5, 10, 5, 10));
	        Scene scene = new Scene(root, 1080, 1260);
			primaryStage.setTitle("Epic Meals");
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.setFill(javafx.scene.paint.Color.DARKGRAY);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			exitProgram.setOnAction(new EventHandler<ActionEvent>() {
            	@Override
            	public void handle(ActionEvent event) {
            		primaryStage.close();
            	}
            	});
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a row to the meal's nutrition totals table 
	 * @param index - row to add the food on
	 * @param grid - the nutrition totals table grid
	 * @param food - name of the food, or may be Total for the last row
	 * @param cal - number of calories in the food
	 * @param carbs - number of carbohydrates in the food
	 * @param fat - amount of fat in the food
	 * @param fiber - amount of fiber in the food
	 * @param protein - amount of protein in the food
	 * @param isBold - whether the text in this row should be bolded. Only true for the Totals row
	 * @return the GridPane grid after the row has been added to it
	 */
	private void addNutritionTableRow(int index, GridPane grid, String food, double cal, double carbs, double fat, double fiber, double protein) {// , boolean isBold) {
		// bump down lower rows
		for (Node n : grid.getChildren()) {
		  int currentIndex = GridPane.getRowIndex(n);
			if (currentIndex >= index) {
			  GridPane.setRowIndex(n, currentIndex + 1);
			}
		}
		
		// set up labels
		Label col1 = new Label(food);
		Label col2 = new Label(Double.toString(cal));
		Label col3 = new Label(Double.toString(carbs));
		Label col4 = new Label(Double.toString(fat));
		Label col5 = new Label(Double.toString(fiber));
		Label col6 = new Label(Double.toString(protein));
		
		// add labels
		grid.add(col1, 0, index, 1, 1);
        grid.add(col2, 1, index, 1, 1);
        grid.add(col3, 2, index, 1, 1);
        grid.add(col4, 3, index, 1, 1);
        grid.add(col5, 4, index, 1, 1);
        grid.add(col6, 5, index, 1, 1);
		// return grid;
	}
	
	/**
	 * Add a food to the list of available foods FoodsList that meet the filter requirements, if any
	 * @param foodList - the FoodList to add the food to
	 * @param food - the name of the food to add
	 * @param cal - the number of calories in the food
	 * @param carbs - the number of carbohydrates in the food
	 * @param fat - the amount of fat in the food
	 * @param fiber - the amount of fiber in the food
	 * @param protein - the amount of protein in the food
	 * @return the foodList ListView after the food has been added
	 */
	private void addFoodToFilterList(ListView<FoodItem> foodListView, FoodItem food) {
    	//String name = food.getName();
    	//Double calories = food.getNutrientValue(Nutrients.CALORIES.toString());
    	//Double carbs = food.getNutrientValue(Nutrients.CARBOHYDRATES.toString());
    	//Double fat = food.getNutrientValue(Nutrients.FAT.toString());
    	//Double fiber = food.getNutrientValue(Nutrients.FIBER.toString());
    	//Double protein = food.getNutrientValue(Nutrients.PROTEIN.toString());
    	foodListView.getItems().add(food);
		//foodListView.getItems().add(name + "\nCalories: " + Double.toString(calories) + "\nCarbohydrates: " + Double.toString(carbs) + " g\nFat " + Double.toString(fat) + " g\nFiber " + Double.toString(fiber) + " g\nProtein " + Double.toString(protein) + " g");
		// return foodListView;
	}
	
	/**
	 * Add a food to the current meal grid
	 * @param index - row to add the food on
	 * @param foodList - the current meal grid
	 * @param food - the food to add
	 * @param nutritionGrid - the nutrition grid of the bottom of the screen that will also be updated with this food's nutrition info
	 * @return the mealGrid GridPane after the food has been added
	 */
	private void addFoodToCurrentMealGrid(int index, GridPane mealGrid, FoodItem food, GridPane nutritionGrid) {
		// bump down lower rows
		for (Node n : mealGrid.getChildren()) {
			int currentIndex = GridPane.getRowIndex(n);
			if (currentIndex >= index) {
				GridPane.setRowIndex(n, currentIndex + 1);
			}
		}
		
		// add row to meal grid for new food item
		mealGrid.add(new Label(food.getName()), 0, index);
        
		// add row to nutrition grid
		addNutritionTableRow(index + 1, nutritionGrid, food.getName(), food.getNutrientValue(Nutrients.CALORIES.toString()), food.getNutrientValue(Nutrients.CARBOHYDRATES.toString()), food.getNutrientValue(Nutrients.FAT.toString()), food.getNutrientValue(Nutrients.FIBER.toString()), food.getNutrientValue(Nutrients.PROTEIN.toString())); // , false);
	}
	
	/**
	 * Adds a button for removing a line from a grid
	 * @param grid - the GridPane to add the button to
	 * @param col - the column index where the button should be placed in grid
	 * @param row - the row index where the button should be placed in grid
	 * @return grid - the GridPane after the button has been inserted
	 */
	  private Button addGridRemoveButton(GridPane grid, int col, int row) {
		    Button remove = new Button("Remove \u2613");
		    remove.getStyleClass().add("custom-button");
		    remove.getStyleClass().add("remove-button");
		    grid.add(remove, col, row);
		    return remove;
		  }
	
	/**
	 * Sets up the title column for the nutrition grid
	 * 
	 * @param nutritionGrid GridPane representing the grid for the meal's nutrition data
	 */
	private void setUpNutritionGrid(GridPane nutritionGrid) {
	  ColumnConstraints col1 = new ColumnConstraints();
      col1.setPercentWidth(25);
      ColumnConstraints otherCols = new ColumnConstraints();
      otherCols.setPercentWidth(15);
      nutritionGrid.getColumnConstraints().addAll(col1, otherCols, otherCols, otherCols, otherCols, otherCols);
      
      //header nutrition row
      Label hdrFood = new Label("Food");
      hdrFood.getStyleClass().add("label-bold");
      Label hdrCal = new Label("Cal");
      hdrCal.getStyleClass().add("label-bold");
      Label hdrCarbs= new Label("Carbs (g)");
      hdrCarbs.getStyleClass().add("label-bold");
      Label hdrFat = new Label("Fat (g)");
      hdrFat.getStyleClass().add("label-bold");
      Label hdrFiber = new Label("Fiber (g)");
      hdrFiber.getStyleClass().add("label-bold");
      Label hdrProtein = new Label("Protein (g)");
      hdrProtein.getStyleClass().add("label-bold");
      nutritionGrid.add(hdrFood, 0, 0, 1, 1);
      nutritionGrid.add(hdrCal, 1, 0, 1, 1);
      nutritionGrid.add(hdrCarbs, 2, 0, 1, 1);
      nutritionGrid.add(hdrFat, 3, 0, 1, 1);
      nutritionGrid.add(hdrFiber, 4, 0, 1, 1);
      nutritionGrid.add(hdrProtein, 5, 0, 1, 1);
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
	   * Removes a specified row from a grid
	   * @param grid grid to remove row from
	   * @param rowToRemove index of the row to remove
	   */
	  private static void removeRow(GridPane grid, int rowToRemove) {
	    // set to gather all nodes in the row we want to remove
	    HashSet<Node> nodesToRemove = new HashSet<>();
	    for (Node n : grid.getChildren()) {
	      if (GridPane.getRowIndex(n) == rowToRemove) {
	        nodesToRemove.add(n);
	      } else if (GridPane.getRowIndex(n) > rowToRemove) { // bump up all rows under the desired row
	        GridPane.setRowIndex(n, GridPane.getRowIndex(n) - 1);
	      }
	    }
	    grid.getChildren().removeAll(nodesToRemove);
	  }

	public static void main(String[] args) {
		//read a data file
		//call other methods
		//instantiate an ADT
		/*parseFoodList("foodItems.txt");
		for (FoodItem food : foodList) {
			System.out.println(food.getName());
		}*/
		launch(args);
	}

}
