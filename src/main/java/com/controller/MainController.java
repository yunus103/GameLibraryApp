package com.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.http.cookie.SetCookie;

import com.mashape.unirest.http.exceptions.UnirestException;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Game;
import repository.UserGameRepository;

public class MainController implements Initializable{
	
	@FXML
	public BorderPane mainBorderPane;
	 
	@FXML
    private Button btn_Home;

    @FXML
    private Button btn_close;

    @FXML
    private Button btn_library;

    @FXML
    private Button btn_minimize;

    @FXML
    private Button btn_search;

    @FXML
    private Button btn_wishlist;

    @FXML
    private TextField fld_search;
    
    @FXML
    private Button btn_maximize;
    
    @FXML
    private VBox navBar;
    
    @FXML
    private TilePane tilePane;
    
    private boolean isMaximized = false; // Now instance-based
    private double prevX, prevY, prevWidth, prevHeight;
    
    private final String HOME_PAGE_PATH = "/view/HomePage.fxml";
    private final String LIBRARY_PATH = "/view/LibraryPage.fxml";
    private final String SEARCH_RESULTS_PATH = "/view/SearchResults.fxml";
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		HomePageController homePageController = setCenterContent(HOME_PAGE_PATH);
		homePageController.setMainController(this);
		
		navBar.prefWidthProperty().bind(mainBorderPane.widthProperty().multiply(0.19));
		
		btn_maximize.setText("🗖");

		 
	}
    
    public void switchToHome() {
    	HomePageController homePageController = setCenterContent(HOME_PAGE_PATH);
		homePageController.setMainController(this);
    }
    
    public void switchToLibrary() {
    	LibraryController libraryController = setCenterContent(LIBRARY_PATH);
    	libraryController.setMainController(this);
    }
    
    public void search() {
    	String query = fld_search.getText().trim();
    	if(query.isEmpty()) return;
    	
    	
    	SearchController searchController = setCenterContent(SEARCH_RESULTS_PATH);
    	searchController.setMainController(this);
    	
    	if(searchController != null) {
    		try {
    			searchController.handleSearch(query);
    		}catch(SQLException | IOException | UnirestException e) {
    			
    		}
    	}
    	
    	
    }
    
    public void close() {
    	System.exit(0);
    }
    
    public void maximize() {
    	Stage stage = (Stage) btn_maximize.getScene().getWindow();
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds(); // Excludes the taskbar

        if (isMaximized) {
            // Restore previous size and position
            stage.setX(prevX);
            stage.setY(prevY);
            stage.setWidth(prevWidth);
            stage.setHeight(prevHeight);
            btn_maximize.setText("🗖"); // Restore icon
        } else {
            // Save current size and position *only once*
            prevX = stage.getX();
            prevY = stage.getY();
            prevWidth = stage.getWidth();
            prevHeight = stage.getHeight();

            // Manually maximize the window to fit within taskbar limits
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            
            btn_maximize.setText("🗗"); // Maximize icon
        }
        isMaximized = !isMaximized;
    }
    
    // Helper method to check the maximized state
    public boolean isMaximized() {
        return isMaximized;
    }

    // Restore the window from maximized state (used when dragging starts)
    public void restore(Stage stage) {
        stage.setX(prevX);
        stage.setY(prevY);
        stage.setWidth(prevWidth);
        stage.setHeight(prevHeight);
        btn_maximize.setText("🗖");
        isMaximized = false;
    }
    
    
    public void minimize() {
    	Stage stage = (Stage) btn_minimize.getScene().getWindow();
    	stage.toBack();
    }
    
    public <T> T setCenterContent(String fxmlPath) {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            // Apply fade-out transition to the current content
            if (mainBorderPane.getCenter() != null) {
                applyFadeTransition(mainBorderPane.getCenter(), 1.0, 0.0, 600); // Fade out
            }

            // Set the new content with fade-in transition
            content.setOpacity(0); // Start invisible
            mainBorderPane.setCenter(content);
            applyFadeTransition(content, 0.0, 1.0, 600); // Fade in

            return loader.getController();
    	}catch(IOException e) {
    		return null;
    	}
    }
    
    public void setRightContent(String fxmlPath) {
    	try {
    		Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
    		mainBorderPane.setRight(content);
    	}catch(IOException e) {
    		
    	}
    }
    
    public void clearRightContent() {
    	mainBorderPane.setRight(null);
    }

    public void applyFadeTransition(Node node, double fromValue, double toValue, int durationMillis) {
    	FadeTransition fadeTransition = new FadeTransition(Duration.millis(durationMillis), node);
    	fadeTransition.setFromValue(fromValue);
    	fadeTransition.setToValue(toValue);
    	fadeTransition.play();
    }
    
	public void switchToWishlist() {
		 // Clear the TilePane to remove any existing content
        tilePane.getChildren().clear();

        // Create the "Coming Soon" label
        Label lbl = new Label("Coming Soon");
        lbl.setTextFill(Color.WHITE); // Set text color to white
        lbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Add some styling

        // Add the label to the TilePane
        tilePane.getChildren().add(lbl);

        // Set the TilePane as the center content of the mainBorderPane
        tilePane.setOpacity(0); // Start with 0 opacity for fade-in effect
        mainBorderPane.setCenter(tilePane);

        // Apply a fade-in transition to the TilePane
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), tilePane);
        fadeTransition.setFromValue(0); // Start invisible
        fadeTransition.setToValue(1);   // End fully visible
        fadeTransition.play();

	}
}
