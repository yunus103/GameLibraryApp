package com.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Game;
import repository.UserGameRepository;

public class AddToLibraryController implements Initializable{
	 	
		@FXML
		private VBox vbox;
		
		@FXML
	    private Button btn_add;

	    @FXML
	    private CheckBox check_ea;
	    
	    @FXML
	    private CheckBox check_GoG;
	    
	    @FXML
	    private CheckBox check_epic;

	    @FXML
	    private CheckBox check_ps;

	    @FXML
	    private CheckBox check_steam;

	    @FXML
	    private CheckBox check_ubisoft;

	    @FXML
	    private CheckBox check_xbox;

	    @FXML
	    private Spinner<Integer> hoursSpinner;

	    @FXML
	    private TextField fld_price;

	    @FXML
	    private ComboBox<String> statusBox;
	    
	    private Game game;
	    
	    public void setGame(Game game) {
	    	this.game = game;
	    }
	    
		@Override
		public void initialize(URL location, ResourceBundle resources) {
			statusBox.getItems().addAll("Playing", "Completed", "Wishlist", "Dropped");
			
			// Ensure the spinner starts at 0 and has valid values
			hoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0));
			
			 hoursSpinner.getEditor().addEventFilter(KeyEvent.KEY_TYPED, event -> {
		            if (!event.getCharacter().matches("\\d")) { // Only allow digits
		                event.consume(); // Reject the input
		            }
		        });
			 
			fld_price.textProperty().addListener((observable, oldValue, newValue) -> {
	            if (!newValue.matches("\\d*\\.?\\d{0,2}")) {  // Allows decimal numbers with max 2 decimals
	                fld_price.setText(oldValue);
	            }
	        });
		}
		
		public void handleAdd() throws SQLException, IOException {
			try {
				// Validate that at least one platform is selected
		        if (!check_ps.isSelected() && !check_steam.isSelected() && !check_xbox.isSelected() &&
		            !check_epic.isSelected() && !check_ea.isSelected() && !check_ubisoft.isSelected() && !check_GoG.isSelected()) {
		            // Show an error message to the user
		            showAlert("No Platform Selected", "Please select at least one platform.");
		            return; // Stop further execution
		        }
				
				double price;
				if(fld_price.getText().isEmpty()) {
					price = 0.0;
				}else {
					price = Double.parseDouble(fld_price.getText());
				}
				

				List<String> platforms = new ArrayList<String>();
				if(check_ps.isSelected()) platforms.add("PlayStation");
				if (check_steam.isSelected()) platforms.add("Steam");
	            if (check_xbox.isSelected()) platforms.add("Xbox");
	            if (check_epic.isSelected()) platforms.add("Epic Games");
	            if (check_ea.isSelected()) platforms.add("EA Games");
	            if (check_ubisoft.isSelected()) platforms.add("Ubisoft");
	            if(check_GoG.isSelected()) platforms.add("GoG");
	            
	            List<Integer> platformIds = new ArrayList<Integer>();
	            for(String platform : platforms) {
	            	
	            	int id = UserGameRepository.getPlatformId(platform);
	            	platformIds.add(id);
	            }
	            
	            String status = statusBox.getValue();
	            if(status == null) {
	            	return;
	            }
	            
	            int hours = hoursSpinner.getValue();
	            
	            
				UserGameRepository.addGameToLibrary(1, game.getIgdb_id(), price, status, hours);
				UserGameRepository.updateUserPlatforms(1, game.getIgdb_id(), platformIds);
				
				// Close window
	            Stage stage = (Stage) btn_add.getScene().getWindow();
	            stage.close();
			}catch(NumberFormatException e) {
				
			}
		}
		
		private void showAlert(String title, String message) {
		    Alert alert = new Alert(Alert.AlertType.ERROR);
		    alert.setTitle(title);
		    alert.setHeaderText(null);
		    alert.setContentText(message);
		    alert.showAndWait();
		}
}
