package com.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Game;
import repository.UserGameRepository;
import service.HelperService;
import service.IGDBService;

public class GameDetailsController implements Initializable{

    @FXML
    private Label gameSummary;
        
    @FXML
    private HBox screenshotHBox;
    
    @FXML
    private Label gameStoryline;

    @FXML
    private Label gameTitle;

    @FXML
    private ImageView mainImage;
    
    @FXML
    private VBox rightBar;

    @FXML
    private ScrollPane scrollPane;
    
    @FXML
    private VBox centerVbox;
    
    @FXML
    private ImageView coverImage;
    
    @FXML
    private ImageView shot1;
    
    @FXML
    private Label lbl_company;

    @FXML
    private Label lbl_date;

    @FXML
    private Label lbl_rating;
    
	private Game game;
	
	private List<String> screenshotPaths = new ArrayList<>();
	private int currentIndex = 0;
	
	@FXML
	private Button leftArrow, rightArrow;
	
	@FXML
	private Button btn_addToLibrary;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(false);
		
		centerVbox.prefWidthProperty().bind(scrollPane.widthProperty());
		
		rightBar.prefWidthProperty().bind(scrollPane.widthProperty().multiply(0.30));
		
		mainImage.fitWidthProperty().bind(centerVbox.widthProperty().multiply(0.9));
		coverImage.fitWidthProperty().bind(rightBar.widthProperty().multiply(0.93));
		
		
	}
	
	public void setGame(Game game) {
		this.game = game;
		setGameDetails(game);
		
		downloadScreenshots(game.getIgdb_id());
		
		try {
			if (UserGameRepository.isInLibrary(1, game.getIgdb_id())) {
	            btn_addToLibrary.setText("In Library");
	            btn_addToLibrary.setDisable(true);
	        } else {
	            btn_addToLibrary.setText("Add to Library");
	            btn_addToLibrary.setDisable(false);
	        }
		} catch (SQLException | IOException e) {

			e.printStackTrace();
		}
	}


	

	private void setGameDetails(Game game) {
		gameTitle.setText(game.getTitle());
		gameSummary.setText(game.getSummary());
		gameStoryline.setText(game.getStoryline());
		lbl_company.setText(game.getCompany());
		if(game.getRelease_date() != null) {
			String date = game.getRelease_date().toString();
			lbl_date.setText(date);
		}

		lbl_rating.setText(String.valueOf(game.getRating()));
		
		
		String imagePath = "data/cache/screenshots/" + game.getIgdb_id() + "/1.jpg";
		Image image = new Image("file:" + imagePath);
		
		String coverImagePath = "data/cache/images/" + game.getIgdb_id() + ".jpg";
		Image cover_image = new Image("file:" + coverImagePath);
		
		
		
		
		loadScreenshots(game.getIgdb_id());
		coverImage.setImage(cover_image);
		mainImage.setImage(image);
	}
	
	
	private void loadScreenshots(int igdb_id) {
		String shotsFolder = "data/cache/screenshots/" + igdb_id;
		File directory = new File(shotsFolder);
		
		screenshotHBox.getChildren().clear();
		screenshotPaths.clear();
		
		if(directory.exists() && directory.isDirectory()) {
			File[] screenshotFiles = directory.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
			
			if(screenshotFiles != null) {
				for(int i = 0; i < screenshotFiles.length; i++) {
					File file = screenshotFiles[i];
					
					screenshotPaths.add(file.toURI().toString());
					
					ImageView thumbnail = new ImageView(new Image(file.toURI().toString(), 95, 60, true, true));
					thumbnail.setPreserveRatio(true);
					thumbnail.setStyle("-fx-cursor: hand;");
					

					int index = i; // Capture index for lambda
					
					thumbnail.setOnMouseClicked(event -> {
						mainImage.setImage(new Image(file.toURI().toString()));
						currentIndex = index;

					});
					
					
					screenshotHBox.getChildren().add(thumbnail);
				}
			}
		}
		
		if (!screenshotPaths.isEmpty()) {
	        currentIndex = 0;
	        mainImage.setImage(new Image(screenshotPaths.get(currentIndex)));

	    }
		
	}
	
	
	
	public void handleLeftArrowClick() {
		if(currentIndex > 0) {
			currentIndex--;
			mainImage.setImage(new Image(screenshotPaths.get(currentIndex)));


		}
	}
	
	public void handleRightArrowClick() {
		if(currentIndex < screenshotPaths.size() - 1) {
			currentIndex++;
			mainImage.setImage(new Image(screenshotPaths.get(currentIndex)));

		}
	}
	
	public void handleAddButton(){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddToLibrary.fxml"));
			Parent root = loader.load();
			
			//Get the controller and set the data
			AddToLibraryController controller = loader.getController();
			controller.setGame(game);
			
			Stage stage = new Stage();
			stage.setTitle("Add To Library");
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
			
			
			if (UserGameRepository.isInLibrary(1, game.getIgdb_id())) {
	            btn_addToLibrary.setText("In Library");
	            btn_addToLibrary.setDisable(true);
	        }
		}catch(Exception e) {
			
		}
	}
	
	
	private void downloadScreenshots(int igdb_id) {
		String screenShotsFolder = "data/cache/screenshots/" + igdb_id;
		File directory = new File(screenShotsFolder);
		
		//Check if screenshots exist
		if(directory.exists() && directory.list().length > 0) {
			return;
		}
		
		//Create dir if not exist
		if(!directory.exists()) {
			directory.mkdirs();
		}
		
		try {
			//Fetch URLs from API
			List<String> screenShotUrls = fetchScreenshotUrls(igdb_id);
			
			int index = 1;
			for(String url : screenShotUrls) {
				String filePath = screenShotsFolder + "/" + index + ".jpg";
				String hdUrl = url.replace("t_thumb", "t_720p");
				HelperService.downloadImage(hdUrl, filePath);
				index++;
			}
			
			 // ✅ After downloading, update the UI
	        javafx.application.Platform.runLater(() -> {
	            setGameDetails(game);
	        });
			
		}catch(Exception e) {
			
		}
	}

	private List<String> fetchScreenshotUrls(int igdb_id) throws IOException, UnirestException {
		IGDBService igdbService = new IGDBService();
		
		String clientId = igdbService.getClientId();
		String accessToken = igdbService.getAccessToken();
		
		HttpResponse<JsonNode> response = Unirest.post(IGDBService.BASE_URL + "/games")
				.header("Client-ID", clientId)
				.header("Authorization", "Bearer " + accessToken)
				.header("Accept", "application/json")
				.body("fields screenshots.url; where id = " + igdb_id + ";")
				.asJson();
		
		if(!(response.getStatus() >= 200 && response.getStatus() < 300)) {
			return null;
		}
		
		List<String> screenshotUrls = new ArrayList<>();
		JsonNode jsonNode = response.getBody();
		
		jsonNode.getArray().forEach(obj -> {
			JSONObject gameJson = (JSONObject) obj;
			if(gameJson.has("screenshots")) {
				JSONArray screenshotsArray = gameJson.getJSONArray("screenshots");
				for(int i = 0; i < Math.min(screenshotsArray.length(), 6); i++) {
					JSONObject urlObj = screenshotsArray.getJSONObject(i);
					if(urlObj.has("url")) {
						String fullUrl = "https:" + urlObj.getString("url");
						screenshotUrls.add(fullUrl);
					}
				}
			}
		});
		
		return screenshotUrls;
	}
}
