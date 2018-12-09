import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
	public static TreeSet<FoodItem> foodList; //Stores the complete list of food items
	private static ListView<String> foodListView; //Stores the food list that we're viewing
	private static Meal meal;	//stores the current meal
	
/**
 * Constructor for the Main class. It doesn't take any parameters but it does initialize the food list
 */
	public Main () {
		foodList = new TreeSet<FoodItem>();
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			foodList = new TreeSet<FoodItem>(); //Instantiate the foodList
			Meal meal = new Meal();
			//parseFoodList("foodItems.txt");
			//Scene will consist of a VBox. The top node of the box will just be the exit button, and the bottom object will be a split pane
			VBox root = new VBox();
	        SplitPane split = new SplitPane();
	        VBox left = new VBox();
	        VBox right = new VBox();
	        GridPane mealGrid = new GridPane();
	        GridPane nutritionGrid = new GridPane();
	        
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
	        ComboBox type = new ComboBox();
	        type.getItems().addAll("Name", "Calories", "Carbohydrates", "Fat", "Fiber", "Protein");
	        ComboBox operator = new ComboBox();
	        operator.getItems().addAll("contains", "=", "<=", ">=", "<", ">");
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
	        
	      //Apply Filter action event that occurs when the Apply Filter button is pressed
	        EventHandler<ActionEvent> applyFilter = new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent e) {
	        		String filterName = (String)type.getValue(); //Get the values place in the filter field and add them all to the current filters
	        		String operatorName = (String)operator.getValue();
	        		String valueName = tf3.getText();
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
	        	}
	        };
	        
	        addFilter.setOnAction(applyFilter); //Apply the filter when the button is pressed
	        
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
	        for (FoodItem food : foodList) {
		        foodListView = addFoodToFilterList(foodListView, food);
	        }
	        foodListView.focusedProperty().addListener(new ChangeListener<Boolean>() {  
	            @Override  
	            public void changed(ObservableValue<? extends Boolean> observable,  
	                    Boolean wasFocused, Boolean isFocused) {  
	                if(isFocused) { 
	                	addToMeal.setDisable(false);
	                	addToMeal.setOnAction(new EventHandler<ActionEvent>() {
	        				@Override
	        				public void handle(ActionEvent event) {
	        					String foodStr = foodListView.getSelectionModel().getSelectedItem();
	        					FoodItem addFood = foodFromString(foodStr);
	        					meal.addToMeal(addFood);
	        					//Need to figure out how to add it to the meal grid after it's been added to the meal
	        				}
	        				});
	                	}
	                else { addToMeal.setDisable(true); }
	            }  
	        }); 
	        
	        //Add food and save to file HBox
	        HBox addAndSaveH = new HBox();
	        Region addAndSaveRegion1 = new HBox();
	        HBox.setHgrow(addAndSaveRegion1, Priority.ALWAYS);
	        
	        //Button fod user adding an individual food to the list
	        Button addFood = new Button("\uff0b Add food");
	        addFood.getStyleClass().add("custom-button");
	        addFood.setOnAction(new EventHandler<ActionEvent>() {
	        	//Will open a pop up diaglogue when Add food is selected
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
	    	        Button exitFood = new Button("Exit");
	    	        Region addFoodRegion1 = new Region();
	    	        HBox.setHgrow(addFoodRegion1, Priority.ALWAYS);
	    	        addFoodHBox.getChildren().addAll(saveFood, addFoodRegion1, exitFood);
	    	        
	    	        addFoodVBox.getChildren().addAll(addFoodGrid, addFoodHBox);
	    	        addFoodVBox.setSpacing(20);
	    	        addFoodVBox.setPadding(new Insets(5, 20, 5, 20));
	    	        
	    	        //Add this window to the scene and set it
	                Scene addFoodScene = new Scene(addFoodVBox, 350, 250);
	                addFoodGrid.setAlignment(Pos.TOP_CENTER);
	                addFoodWindow.setScene(addFoodScene);
	                addFoodWindow.show();
	                //Save button
	                saveFood.setOnAction(new EventHandler<ActionEvent>() {
	                	@Override
	                	public void handle(ActionEvent event) {
	                		String name = foodName.getText();
	                		Double calsD = Double.valueOf(calories.getText());
	                		Double carbsD = Double.valueOf(carbs.getText());
	                		Double fatD = Double.valueOf(fat.getText());
	                		Double fiberD = Double.valueOf(fiber.getText());
	                		Double proteinD = Double.valueOf(protein.getText());
	                		int nameHash = name.hashCode();
	                		String id = "" + nameHash + calsD.toString().replace(".", "") + carbsD.toString().replace(".", "") + fatD.toString().replace(".", "") + fiberD.toString().replace(".", "") + proteinD.toString().replace(".", "");
	                		FoodItem newFood = createFoodItem(id, name, calsD, fatD, carbsD, fiberD, proteinD); 
	    					if(foodList.contains(newFood)) {
	    							//If the user-entered food is already in the list, give them a pop up that indicates it's a duplicate
	    							final Stage dupFoodWindow = new Stage();
	    							dupFoodWindow.initModality(Modality.APPLICATION_MODAL);
	    							dupFoodWindow.initOwner(primaryStage);
	    		                
	    							VBox duplicateFoodV = new VBox();
	    							Label dupFood = new Label("This food is already in the list.");
	    							HBox duplicateFoodH = new HBox();
	    							Region dupFoodRegion = new Region();
	    							HBox.setHgrow(dupFoodRegion, Priority.ALWAYS);
	    							Button dupFoodOk = new Button("OK");
	    							dupFoodOk.getStyleClass().add("custom-button");
	    							duplicateFoodH.getChildren().addAll(dupFoodRegion, dupFoodOk);
	    							duplicateFoodV.getChildren().addAll(dupFood, duplicateFoodH);
	    							duplicateFoodV.setAlignment(Pos.TOP_LEFT);
	    							duplicateFoodV.setPadding(new Insets(5, 10, 5, 10));
	    							duplicateFoodV.setSpacing(25);
	    							Scene dupFoodScene = new Scene(duplicateFoodV, 300, 100);
	    							
	    							dupFoodWindow.setScene(dupFoodScene);
	    							dupFoodWindow.show();
	    							
	    							dupFoodOk.setOnAction(new EventHandler<ActionEvent>() {
	    								@Override
	    								public void handle(ActionEvent event) {
	    									dupFoodWindow.close();
	    								}
	    								});
	    							
	    							return;
	    					}
	    					
	                		addFood(newFood);
	                		
	                		//Once the food has been saved, re-sort the available foods list
	    					foodListView.getItems().clear();
		        			for (FoodItem food : foodList) {
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
	        			saveListToFile(newFoodFile);
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
	        			parseFoodList(newFoodList.getPath());
	        			filepath.setText(newFoodList.getPath());
	        			foodListView.getItems().clear();
	        			for (FoodItem food : foodList) {
	        				addFoodToFilterList(foodListView, food);
	        			}
	        			
	        		}
	        	}
	        };
	        
	        loadFile.setOnAction(loadFileEvent);
	        
	        //Add all the left side boxes
	        left.getChildren().addAll(filterVBox, foodsTitleHBox, foodListVBox, addAndSaveH, load);
	        left.setSpacing(20);
	        left.setPadding(new Insets(25, 25, 25, 25));
	        
	        //Right side
	        
	        //Current meal
	        Label currentMeal = new Label("Current meal");
	        currentMeal.getStyleClass().add("custom-header");
	        ColumnConstraints mealCol1 = new ColumnConstraints();
	        mealCol1.setPercentWidth(70);
	        ColumnConstraints mealCol2 = new ColumnConstraints();
	        mealCol2.setPercentWidth(30);
	        mealCol2.setHalignment(HPos.RIGHT);
	        mealGrid.getColumnConstraints().addAll(mealCol1, mealCol2);
	        
	        //Current Meal Grid
	        mealGrid.add(new Label("Pancakes"), 0, 0);
	        mealGrid = addGridRemoveButton(mealGrid, 1, 0);
	        
	        mealGrid.add(new Label("Eggs"), 0, 1);
	        mealGrid = addGridRemoveButton(mealGrid, 1, 1);
	        
	        mealGrid.add(new Label("Bacon"), 0, 2);
	        mealGrid = addGridRemoveButton(mealGrid, 1, 2);
	        
	        mealGrid.add(new Label("Milk"), 0, 3);
	        mealGrid = addGridRemoveButton(mealGrid, 1, 3);
	        
	        mealGrid.setHgap(20);
	        mealGrid.setVgap(5);
	        
	        //Meal Nutrition Grid
	        Label mealNutrition = new Label("Meal Nutrition");
	        mealNutrition.getStyleClass().add("custom-header");
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
	        
	        //add foods from meal to nutrition table
		    nutritionGrid = addNutritionTableRow(nutritionGrid, "Pancakes", 200, 20, 2, 2, 2, false);
		    nutritionGrid = addNutritionTableRow(nutritionGrid, "Eggs", 100, 10, 1, 1, 1, false);
		    nutritionGrid = addNutritionTableRow(nutritionGrid, "Bacon", 300, 30, 3, 3, 3, false);
	        nutritionGrid = addNutritionTableRow(nutritionGrid, "Milk", 50, 5, 0, 0, 0, false);
	        
	        //nutrition totals
	        nutritionGrid = addNutritionTableRow(nutritionGrid, "Total", 650, 65, 6, 6, 6, true);
	        
	        nutritionGrid.setVgap(5.0);
	        
	        Region region5 = new Region();
	        VBox.setVgrow(region5, Priority.ALWAYS);

	        right.getChildren().addAll(currentMeal, mealGrid, region5, mealNutrition, nutritionGrid);
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
	private GridPane addNutritionTableRow(GridPane grid, String food, double cal, double carbs, double fat, double fiber, double protein, boolean isBold) {
		
		//if this is the Totals row, add a blank row in between this and the last line
		if(food.equals("Total")) {
			int newRow = getRowCount(grid);
			Label blankCol1 = new Label("");
			Label blankCol2 = new Label("");
			Label blankCol3 = new Label("");
			Label blankCol4 = new Label("");
			Label blankCol5 = new Label("");
			Label blankCol6 = new Label("");
			grid.add(blankCol1, 0, newRow, 1, 1);
	        grid.add(blankCol2, 1, newRow, 1, 1);
	        grid.add(blankCol3, 2, newRow, 1, 1);
	        grid.add(blankCol4, 3, newRow, 1, 1);
	        grid.add(blankCol5, 4, newRow, 1, 1);
	        grid.add(blankCol6, 5, newRow, 1, 1);
		}
		int newRow = getRowCount(grid);
		Label col1 = new Label(food);
		Label col2 = new Label(Double.toString(cal));
		Label col3 = new Label(Double.toString(carbs));
		Label col4 = new Label(Double.toString(fat));
		Label col5 = new Label(Double.toString(fiber));
		Label col6 = new Label(Double.toString(protein));
		if (isBold) {
			col1.getStyleClass().add("label-bold");
			col2.getStyleClass().add("label-bold");
			col3.getStyleClass().add("label-bold");
			col4.getStyleClass().add("label-bold");
			col5.getStyleClass().add("label-bold");
			col6.getStyleClass().add("label-bold");
		}
		grid.add(col1, 0, newRow, 1, 1);
        grid.add(col2, 1, newRow, 1, 1);
        grid.add(col3, 2, newRow, 1, 1);
        grid.add(col4, 3, newRow, 1, 1);
        grid.add(col5, 4, newRow, 1, 1);
        grid.add(col6, 5, newRow, 1, 1);
		return grid;
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
	private ListView<String> addFoodToFilterList(ListView<String> foodListView, FoodItem food) {
    	String name = food.getName();
    	Double calories = food.getNutrientValue(Nutrients.CALORIES.toString());
    	Double carbs = food.getNutrientValue(Nutrients.CARBOHYDRATES.toString());
    	Double fat = food.getNutrientValue(Nutrients.FAT.toString());
    	Double fiber = food.getNutrientValue(Nutrients.FIBER.toString());
    	Double protein = food.getNutrientValue(Nutrients.PROTEIN.toString());
		foodListView.getItems().add(name + "\nCalories: " + Double.toString(calories) + "\nCarbohydrates: " + Double.toString(carbs) + " g\nFat " + Double.toString(fat) + " g\nFiber " + Double.toString(fiber) + " g\nProtein " + Double.toString(protein) + " g");
		return foodListView;
	}
	
	/**
	 * Add a food to the current meal grid
	 * @param foodList - the current meal grid
	 * @param food - the food to add
	 * @param nutritionGrid - the nutrition grid of the bottom of the screen that will also be updated with this food's nutrition info
	 * @return the mealGrid GridPane after the food has been added
	 */
	private GridPane addFoodToCurrentMealGrid(GridPane mealGrid, FoodItem food, GridPane nutritionGrid) {
		int newRow = getRowCount(mealGrid);
		mealGrid.add(new Label(food.getName()), 0, newRow);
        mealGrid = addGridRemoveButton(mealGrid, 1, newRow);
        nutritionGrid = addNutritionTableRow(nutritionGrid, food.getName(), food.getNutrientValue("calories"), food.getNutrientValue("carbohydrate"), food.getNutrientValue("fat"), food.getNutrientValue("fiber"), food.getNutrientValue("protein"), false);
		return mealGrid;
	}
	
	/**
	 * Add a food to the current meal grid
	 * @param foodStr - the string from the listView to convert to a food
	 * @return a food object converted from the listView string
	 */
	private FoodItem foodFromString(String foodStr) {
		FoodItem food = null;
		
		return food;
	}
	
	/**
	 * Returns the total number of populated rows in the specified GridPane
	 * @param grid - grid to evaluate
	 * @return nRows - the number of rows in grid
	 */
	private int getRowCount(GridPane grid) {
        int nRows = grid.getRowConstraints().size();
        for (int i = 0; i < grid.getChildren().size(); i++) {
            Node child = grid.getChildren().get(i);
            if (child.isManaged()) {
            	Integer rowIndex1 = grid.getRowIndex(child);
                int rowIndex2 = (rowIndex1 != null ? rowIndex1 : 0);
                int rowSpan = getNodeRowSpan(grid, child);
                int rowEnd = (rowSpan != GridPane.REMAINING ? getNodeRowIndex(grid, child) + rowSpan - 1 : GridPane.REMAINING);
                nRows = Math.max(nRows, (rowEnd != GridPane.REMAINING? rowEnd : rowIndex1) + 1);
            }
        }
        return nRows;
    }
	
	/**
	 * Returns the row number of node in grid. If it cannot be found, it returns 0
	 * @param grid - the grid containing the node
	 * @param node - the node to find the row index of
	 * @return int row number of node's location in the table
	 */
	private int getNodeRowIndex(GridPane grid, Node node) {
        Integer rowIndex = grid.getRowIndex(node);
        return rowIndex != null? rowIndex : 0;
    }
	/**
	 * Returns the number of rows that node spans inside of the grid object. If it cannot be found, it returns 1
	 * @param grid - the GridPane to evaluate
	 * @param node - the node to check for the row span
	 * @return int number of rows that node spans in the GridPane grid
	 */
	private int getNodeRowSpan(GridPane grid, Node node) {
        Integer rowspan = grid.getRowSpan(node);
        return rowspan != null? rowspan : 1;
    }
	/**
	 * Adds a button for removing a line from a grid
	 * @param grid - the GridPane to add the button to
	 * @param col - the column index where the button should be placed in grid
	 * @param row - the row index where the button should be placed in grid
	 * @return grid - the GridPane after the button has been inserted
	 */
	private GridPane addGridRemoveButton(GridPane grid, int col, int row) {
		Button remove = new Button("Remove \u2613");
        remove.getStyleClass().add("custom-button");
        remove.getStyleClass().add("remove-button");
        grid.add(remove, col, row);
        return grid;
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
	
	/**
     * Prints out the current food list to a text file
     * 
     * @param food-- the file to write the current list to
     */
	public static void saveListToFile(File saveFile) {
		try {
			PrintWriter pw = new PrintWriter(saveFile);
			for (FoodItem food : foodList) {
				String id = food.getID();
				String name = food.getName();
				Double calories = food.getNutrientValue("Calories");
				Double fat = food.getNutrientValue("Fat");
				Double carbs = food.getNutrientValue("Carbohydrates");
				Double fiber = food.getNutrientValue("Fiber");
				Double protein = food.getNutrientValue("Protein");
				String ln = id + "," + name + ",calories," + calories.toString() + ",fat," + fat.toString() + ",carbohydrate," + carbs + ",fiber," + fiber.toString() + ",protein," + protein.toString();
				
				pw.println(ln);
			}
			pw.close();
		}
		catch(Exception e) {
			//Put a message here that we were unable to save the file
		}
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
