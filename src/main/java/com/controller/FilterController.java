package com.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FilterController implements Initializable{
	
    @FXML
    private CheckBox Adventure;

    @FXML
    private CheckBox Arcade;

    @FXML
    private CheckBox Card;

    @FXML
    private CheckBox EAGames;

    @FXML
    private CheckBox EpicGames;

    @FXML
    private CheckBox Fighting;

    @FXML
    private CheckBox Hack;

    @FXML
    private CheckBox Indie;

    @FXML
    private CheckBox MOBA;

    @FXML
    private CheckBox Music;

    @FXML
    private CheckBox Pinball;

    @FXML
    private CheckBox Platform;

    @FXML
    private CheckBox PlayStation;

    @FXML
    private CheckBox Point;

    @FXML
    private CheckBox Puzzle;

    @FXML
    private CheckBox Quiz;

    @FXML
    private CheckBox Real;

    @FXML
    private CheckBox Role;

    @FXML
    private CheckBox Shooter;

    @FXML
    private CheckBox Simulator;

    @FXML
    private CheckBox Sport;

    @FXML
    private CheckBox Steam;

    @FXML
    private CheckBox Strategy;

    @FXML
    private CheckBox Tactical;

    @FXML
    private CheckBox Turn;

    @FXML
    private CheckBox Ubisoft;

    @FXML
    private CheckBox Visual;
    
    @FXML
    private CheckBox Racing;

    @FXML
    private CheckBox Xbox;
    
    @FXML
    private CheckBox GoG;

    @FXML
    private VBox container;
    
    @FXML
    private Button btn_apply;
	
    private LibraryController libraryController; // Reference to LibraryController
    

    
    public void setLibraryController(LibraryController libraryController) {
        this.libraryController = libraryController;
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Set fixed size for the VBox
	    container.setMinSize(345, 455); // Minimum width and height
	    container.setPrefSize(345, 455); // Preferred width and height
	    container.setMaxSize(345, 455); // Maximum width and height
	    
	    // Restore previously selected genres
	    if (LibraryController.filterGenres != null) {
	        restoreSelectedGenres();
	    }

	    // Restore previously selected platforms
	    if (LibraryController.filterPlatforms != null) {
	        restoreSelectedPlatforms();
	    }
	}
	
	public void handleApply() {
		
		LibraryController.filterGenres = getSelectedGenres();
		LibraryController.filterPlatforms = getSelectedPlatforms();
		
		if(libraryController != null) {
			libraryController.loadGames(LibraryController.selectedSortingOption, getSelectedGenres(), getSelectedPlatforms());
		}
		
		// Restore previously selected genres
	    if (LibraryController.filterGenres != null) {
	        restoreSelectedGenres();
	    }

	    // Restore previously selected platforms
	    if (LibraryController.filterPlatforms != null) {
	        restoreSelectedPlatforms();
	    }
		
		// Close window
        Stage stage = (Stage) btn_apply.getScene().getWindow();
        stage.close();
	}
	
	private List<String> getSelectedPlatforms(){
		List<String> platforms = new ArrayList<String>();
		
		if(PlayStation.isSelected()) platforms.add("PlayStation");
		if(Steam.isSelected()) platforms.add("Steam");
		if (Xbox.isSelected()) platforms.add("Xbox");
	    if (EpicGames.isSelected()) platforms.add("Epic Games");
	    if (EAGames.isSelected()) platforms.add("EA Games");
	    if (Ubisoft.isSelected()) platforms.add("Ubisoft");
	    if(GoG.isSelected()) platforms.add("GoG");
	    return platforms;
	}
	
	private List<String> getSelectedGenres(){
		List<String> genres = new ArrayList<String>();
		
	    List<CheckBox> genreCheckBoxes = List.of(
		        Adventure, Arcade, Card, Fighting, Hack, Indie, MOBA, Music, Pinball,
		        Platform, Point, Puzzle, Quiz, Racing, Real, Role, Shooter, Simulator, Sport, Strategy, 
		        Tactical, Turn, Visual
		    );
	    
		for(CheckBox checkBox : genreCheckBoxes) {
			if(checkBox.isSelected()) {
				genres.add(checkBox.getText());
			}
		}
		
		return genres;
	}
	
	private void restoreSelectedGenres() {
	    List<CheckBox> genreCheckBoxes = List.of(
		        Adventure, Arcade, Card, Fighting, Hack, Indie, MOBA, Music, Pinball,
		        Platform, Point, Puzzle, Quiz, Racing, Real, Role, Shooter, Simulator, Sport, Strategy, 
		        Tactical, Turn, Visual
		    );
	    
	    for (CheckBox checkBox : genreCheckBoxes) {
	        if (LibraryController.filterGenres.contains(checkBox.getText())) {
	            checkBox.setSelected(true);
	        }
	    }
	}
	
	// Restore the selected platforms from LibraryController
	private void restoreSelectedPlatforms() {
	    if (PlayStation != null && LibraryController.filterPlatforms.contains("PlayStation")) {
	        PlayStation.setSelected(true);
	    }
	    if (Steam != null && LibraryController.filterPlatforms.contains("Steam")) {
	        Steam.setSelected(true);
	    }
	    if (Xbox != null && LibraryController.filterPlatforms.contains("Xbox")) {
	        Xbox.setSelected(true);
	    }
	    if (EpicGames != null && LibraryController.filterPlatforms.contains("Epic Games")) {
	        EpicGames.setSelected(true);
	    }
	    if (EAGames != null && LibraryController.filterPlatforms.contains("EA Games")) {
	        EAGames.setSelected(true);
	    }
	    if (Ubisoft != null && LibraryController.filterPlatforms.contains("Ubisoft")) {
	        Ubisoft.setSelected(true);
	    }
	    if (GoG != null && LibraryController.filterPlatforms.contains("GoG")) {
	        GoG.setSelected(true);
	    }
	}
}
