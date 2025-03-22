package com.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import model.Game;
import repository.GameRepository;
import repository.UserGameRepository;


public class HomePageController implements Initializable{
	@FXML
	private TilePane tilePane;
	
	private MainController mainController;
	
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
    	try {
			List<Game> games = UserGameRepository.getLastAddedGames(1);
			
			
			for(Game game : games) {
	    		String title = game.getTitle();
	    		String coverPath = game.getCover_image_path();
	    		int igdbId = game.getIgdb_id();
	    		
	    		Label lbl_title = new Label(title);
	    		Label lbl_path = new Label(coverPath);
	    		
	    		String imagePath = "data/cache/images/" + igdbId + ".jpg";
	    		Image image = new Image("file:" + imagePath);
	    		ImageView imgView = new ImageView(image);
	    		
	    		VBox vbox = new VBox();
	    		vbox.getChildren().add(imgView);
	    		vbox.getChildren().add(lbl_title);
	    		
	    	    vbox.getStyleClass().add("game-tile");
	    	    imgView.getStyleClass().add("game-image");
	    	    lbl_title.getStyleClass().add("game-title");
	    	    
	    		
	    		vbox.setOnMouseClicked(event -> {
	    			try {
	    				GameRepository repo = new GameRepository();
	    				Game game1 = repo.findById(igdbId); //Retrieve game info from games table not user_games
	    				LibraryController.openLibraryGameDetails(game1, mainController, "home");
	    			} catch (IOException | SQLException e) {
	    				
	    			}
	    		});
	    		
	    		
	    		tilePane.getChildren().add(vbox);
	    	}
			
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
