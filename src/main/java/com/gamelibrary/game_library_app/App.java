package com.gamelibrary.game_library_app;


import com.controller.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;




public class App extends Application{

	private static final double BORDER_SIZE = 5;
	private double xOffset = 0;
	private double yOffset = 0;
	private boolean isResizing = false;
	
	@Override
	public void start(Stage stage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainLayout.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController();
        Scene scene = new Scene(root);
		stage.initStyle(StageStyle.UNDECORATED);
		
		Node topBar = root.lookup("#topBar"); 
		if (topBar != null) {
		    enableDrag(stage, topBar, scene, mainController);
		}
		
		stage.setMinHeight(620);
		stage.setMinWidth(1100);
		stage.setScene(scene);
		stage.show();
	}
	
	private void enableDrag(Stage stage, Node topBar, Scene scene, MainController mainController) {
		// Enable dragging only on the top bar
	    topBar.setOnMousePressed(event -> {
	        //if (stage.isMaximized()) return; // Disable dragging when maximized

	    	if(mainController.isMaximized()) {
	    		mainController.restore(stage);
	    		
	    		xOffset = event.getSceneX();
		        yOffset = event.getSceneY();
		        
	    	} else {
	    		xOffset = event.getSceneX();
	            yOffset = event.getSceneY();
	    	}
	    });

	    topBar.setOnMouseDragged(event -> {
	        if (!mainController.isMaximized()) {
	        	stage.setX(event.getScreenX() - xOffset);
	        	stage.setY(event.getScreenY() - yOffset);
	        }

	    });

	    // Enable resizing by dragging the window edges
	    scene.setOnMouseMoved(event -> {
	        double mouseX = event.getSceneX();
	        double mouseY = event.getSceneY();
	        double width = stage.getWidth();
	        double height = stage.getHeight();

	        boolean right = mouseX > width - BORDER_SIZE;
	        boolean bottom = mouseY > height - BORDER_SIZE;

	        if (right && bottom) {
	            scene.setCursor(Cursor.NW_RESIZE);
	        } else if (right) {
	            scene.setCursor(Cursor.E_RESIZE);
	        } else if (bottom) {
	            scene.setCursor(Cursor.S_RESIZE);
	        } else {
	            scene.setCursor(Cursor.DEFAULT);
	        }
	    });

	    scene.setOnMouseDragged(event -> {
	        if (stage.isMaximized()) return;

	        double mouseX = event.getSceneX();
	        double mouseY = event.getSceneY();
	        double width = stage.getWidth();
	        double height = stage.getHeight();

	        if (scene.getCursor() == Cursor.E_RESIZE) {
	            stage.setWidth(mouseX);
	        } else if (scene.getCursor() == Cursor.S_RESIZE) {
	            stage.setHeight(mouseY);
	        } else if (scene.getCursor() == Cursor.NW_RESIZE) {
	            stage.setWidth(mouseX);
	            stage.setHeight(mouseY);
	        }
	    });
	}
	
    public static void main(String[] args) {
    	launch(args);
    }


}
