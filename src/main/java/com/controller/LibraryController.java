package com.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Game;
import repository.GameRepository;
import repository.UserGameRepository;
import service.HelperService;

public class LibraryController implements Initializable{
    @FXML
    private Button filterButton;

    @FXML
    private ComboBox<String> sortBox;

    @FXML
    private TilePane tilePane;
    
    @FXML
    private ScrollPane scrollPane;
    
    private MainController mainController;
    
    public static String selectedSortingOption = "Alphabetical A-Z"; // Default value
    public static List<String> filterGenres = null;
    public static List<String> filterPlatforms = null;
    
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tilePane.getStylesheets().add(getClass().getResource("/css/GameTiles.css").toExternalForm());
		sortBox.getItems().addAll("Alphabetical A-Z", "Alphabetical Z-A", "Last Added", "Rating");
		
		// Set a default sorting option
        sortBox.setValue(selectedSortingOption);
        
        // Load games with the default sorting option
        loadGames(sortBox.getValue(), null, null);
		
        // Add a listener to the ComboBox to reload games when the sorting option changes
        sortBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            	selectedSortingOption = newValue;
                loadGames(newValue, filterGenres, filterPlatforms);
            }
        });
    }
        
	

	public void loadGames(String sortingType, List<String> genres, List<String> platforms) {
		try {
		//Clear first
		tilePane.getChildren().clear();
		
		String query = handleSorting(sortingType);
		
		 // Fetch games from the database
        List<Game> games = UserGameRepository.getFilteredGames(1, query, genres, platforms);

        if(games.isEmpty()) {
        	Label lbl = new Label("No games found!");
        	lbl.setFont(new Font(16));
        	lbl.setPadding(new Insets(5, 0, 10, 0));
        	
        	tilePane.getChildren().add(lbl);
        }
        // Display each game in the tilePane
        for (Game game : games) {
            int igdb_id = game.getIgdb_id();
            String title = game.getTitle();
            String coverPath = game.getCover_image_path();

            Label lbl_title = new Label(title);

            Image image = new Image("file:" + coverPath);
            ImageView imgView = new ImageView(image);

            VBox vbox = new VBox();
            vbox.getChildren().add(imgView);
            vbox.getChildren().add(lbl_title);

    	    vbox.getStyleClass().add("game-tile");
    	    imgView.getStyleClass().add("game-image");
    	    lbl_title.getStyleClass().add("game-title");

            imgView.setOnMouseClicked(event -> {
                try {
                    GameRepository repo = new GameRepository();
                    Game game1 = repo.findById(igdb_id);
                    openLibraryGameDetails(game1, mainController, "library");
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            });

            tilePane.getChildren().add(vbox);
        }
    } catch (SQLException | IOException e) {
        e.printStackTrace();
    	}
	}
	
	private String handleSorting(String sortingType) {
		String sortQuery = "title ASC";
		switch (sortingType) {
		case "Alphabetical A-Z":
			sortQuery = "title ASC";
			break;
			
		case "Alphabetical Z-A":
			sortQuery = "title DESC";
			break;

		case "Last Added":
			sortQuery = "added_date DESC";
			break;

		case "Rating":
			sortQuery = "rating DESC";
			break;

		default:
			break;
		}
		return sortQuery;
	}

    public static void openLibraryGameDetails(Game game, MainController mainController, String parent) {
    	//parent -> for checking whether it was opened from library or homepage
    	if(mainController != null) {
			UserGameDetailsController controller = mainController.setCenterContent("/view/GameDetailsLibrary.fxml");
			controller.setGame(game);
			controller.setMainController(mainController);
			controller.setParent(parent);
		}else {
			//System.out.println("Main Controller is null");
		}
    }
    
    public void handleFilterButton() throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Filter.fxml"));
    	Parent root = loader.load();
    	
    	FilterController controller = loader.getController();
    	
    	//Pass the current LibraryController instance
    	controller.setLibraryController(this);
    	
    	Stage stage = new Stage();
    	stage.setTitle("Filter");
    	stage.setScene(new Scene(root));
    	stage.initModality(Modality.APPLICATION_MODAL);
    	stage.showAndWait();
    }
    
}
