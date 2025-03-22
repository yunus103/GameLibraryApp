package com.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import com.mashape.unirest.http.exceptions.UnirestException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Game;
import repository.GameRepository;
import repository.IGDBRepository;
import repository.UserGameRepository;
import service.HelperService;
import service.IGDBService;

public class SearchController implements Initializable{

	@FXML
    private VBox resultsContainer;
	
	@FXML
	private ScrollPane scrollPane;
	
	private MainController mainController;
	
	IGDBService igdbService;
	IGDBRepository igdbRepo;
	GameRepository gameRepo;
	HelperService helper;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			igdbService = new IGDBService();
			igdbRepo = new IGDBRepository(igdbService);
			gameRepo = new GameRepository();
			helper = new HelperService(igdbRepo, gameRepo);
			
			
		} catch (IOException | UnirestException e) {
			
		}
		
		 if (scrollPane != null) {
		        scrollPane.setFitToWidth(true); // Ensure this is not called on a null reference
		    }
	}
	
	
	public void setMainController(MainController mainController) {
	    this.mainController = mainController;
	}
	
	public void handleSearch(String query) throws SQLException, IOException, UnirestException {
		
		if(helper == null) {
			//System.err.println("Error: helper is null. Check initialization in initialize().");
			return;
		}
		
		resultsContainer.getChildren().clear(); // Clear previous results
		

	    //System.out.println("Searching for: " + query); // Debugging step 1

	    List<Game> searchResults = helper.searchGame(query);

	    //System.out.println("Results found: " + searchResults.size()); // Debugging step 2
		
	    /*if (searchResults.isEmpty()) {
	        System.out.println("No results found for: " + query);
	    }*/
	    
		for(Game game : searchResults) {
			HBox gameEntry = creatGameEntry(game);
			resultsContainer.getChildren().add(gameEntry);
		}
		
		scrollPane.setVvalue(0);
	}

	private HBox creatGameEntry(Game game) throws SQLException, IOException {
		HBox hbox = new HBox(10);
		VBox vbox = new VBox(5);
		
		String coverPath = game.getCover_image_path();
        Image image = new Image("file:" + coverPath);
        ImageView imgView = new ImageView(image);
        imgView.setFitHeight(55);
        imgView.setFitWidth(40);
        
		Label titleLabel = new Label(game.getTitle());
		Label companyLabel = new Label(game.getCompany());
		
	    hbox.getStyleClass().add("search-result");
	    titleLabel.getStyleClass().add("search-result-title");
	    companyLabel.getStyleClass().add("search-result-company");
		
	    
	    hbox.getChildren().add(imgView);
		vbox.getChildren().addAll(titleLabel, companyLabel);
		hbox.getChildren().add(vbox);
		hbox.setCursor(Cursor.HAND);
		
		

	    // Add the "In Library" label if the game is in the library
	    if (UserGameRepository.isInLibrary(1, game.getIgdb_id())) {
	    	// Add a spacer to push the "In Library" label to the right
		    Region spacer = new Region();
		    HBox.setHgrow(spacer, Priority.ALWAYS); // Make the spacer grow to fill the space
		    
	        Label lib = new Label("In Library");
	        lib.getStyleClass().add("search-result-library");
	        hbox.getChildren().addAll(spacer, lib); // Add spacer and label
	    }
		
		hbox.setOnMouseClicked(event -> {
			try {
				openGameDetails(game);
			} catch (IOException | SQLException e) {
				

			}
		});
		
		return hbox;
	}


	private void openGameDetails(Game game) throws IOException, SQLException {
		//System.out.println("Clicked On: " + game.getTitle());
		
		if(mainController != null) {
			if(UserGameRepository.isInLibrary(1, game.getIgdb_id())) {
				UserGameDetailsController controller = mainController.setCenterContent("/view/GameDetailsLibrary.fxml");
				controller.setGame(game);
			}else {
				
				GameDetailsController controller = mainController.setCenterContent("/view/GameDetails.fxml");
				controller.setGame(game);
			}

		}else {
			//System.out.println("Main Controller is null");
		}
		
	}
	
	
}




