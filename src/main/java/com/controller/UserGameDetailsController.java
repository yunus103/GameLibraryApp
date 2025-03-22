package com.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Game;
import repository.GameRepository;
import repository.UserGameRepository;
import service.HelperService;
import service.IGDBService;

public class UserGameDetailsController implements Initializable{
    @FXML
    private Label gameSummary;
      
    @FXML
    private Pane imagePane; 
    
    @FXML
    private HBox imageContainer;
    
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
    
    @FXML
    private HBox platformBox;

    @FXML
    private HBox priceBox;
    
    @FXML
    private HBox genresBox;

    @FXML
    private HBox hoursBox;
    
    @FXML
    private HBox genreContainer;

    @FXML
    private HBox infoContainer;
    
	private Game game;
	
	private List<String> screenshotPaths = new ArrayList<>();
	private int currentIndex = 0;
	
	private MainController mainController;
	private String parent;
	
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
		
		genreContainer.prefWidthProperty().bind(infoContainer.widthProperty().multiply(0.38));
		
		genresBox.setSpacing(3);
		platformBox.setSpacing(3);
	}
	
	public void setGame(Game game) {
		this.game = game;
		try {
			setGameDetails(game);
		} catch (SQLException | IOException e) {
	
			e.printStackTrace();
		}
		
		downloadScreenshots(game.getIgdb_id());
	}

	public void setMainController(MainController mainController) {
	    this.mainController = mainController;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}

	private void setGameDetails(Game game) throws SQLException, IOException {
		gameTitle.setText(game.getTitle());
		gameSummary.setText(game.getSummary());
		gameStoryline.setText(game.getStoryline());
		lbl_company.setText(game.getCompany());
		lbl_date.setText(game.getRelease_date().toString());
		lbl_rating.setText(String.valueOf(game.getRating()));
		
		
		String imagePath = "data/cache/screenshots/" + game.getIgdb_id() + "/1.jpg";
		Image image = new Image("file:" + imagePath);
		
		String coverImagePath = "data/cache/images/" + game.getIgdb_id() + ".jpg";
		Image cover_image = new Image("file:" + coverImagePath);
		

		//Retrieve genres and put in HBox dynamically
		List<String> genres = GameRepository.getGenreNamesById(game.getGenres());
		for(String genre : genres) {
			Label lbl = new Label(genre);
			lbl.setPrefWidth(Region.USE_COMPUTED_SIZE);
			lbl.setMaxWidth(Double.MAX_VALUE);
			lbl.setWrapText(true);
			lbl.setStyle(
		            "-fx-background-color: #555555;" +  // Grey background
		            "-fx-text-fill: white;" +            // White text color
		            "-fx-padding: 5 10;" +               // Padding inside the label
		            "-fx-background-radius: 9;"         // Rounded corners
		        );
			
			
			genresBox.getChildren().add(lbl);
		}
		
		
		
		//Retrieve platforms and put in HBox dynamically
		List<String> platforms = UserGameRepository.getUserPlatform(1, game.getIgdb_id());
		/*for(String platform : platforms) {
			Label lbl = new Label(platform);
			lbl.setStyle(
		            "-fx-background-color: #555555;" +  // Grey background
		            "-fx-text-fill: white;" +            // White text color
		            "-fx-padding: 5 10;" +               // Padding inside the label
		            "-fx-background-radius: 9;"         // Rounded corners
		        );
			
			platformBox.getChildren().add(lbl);
		}
		*/
		for(String platform : platforms) {
			//data/cache/images/80.jpg
			String path = "data/icons/" + platform + ".png";
            Image icon = new Image("file:" + path);
            ImageView imgView = new ImageView(icon);
            imgView.setFitHeight(32);
            imgView.setFitWidth(32);
            
            platformBox.getChildren().add(imgView);
		}
		
		
		double price = UserGameRepository.getPricePaid(1, game.getIgdb_id());
		int hours = UserGameRepository.getHoursPlayed(1, game.getIgdb_id());
		
		Label lbl_price = new Label(String.valueOf(price) + "₺");
		Label lbl_hours = new Label(String.valueOf(hours) + " Hours");
		
		lbl_price.setStyle(
	            "-fx-background-color: #555555;" +  // Grey background
	            "-fx-text-fill: white;" +            // White text color
	            "-fx-padding: 5 10;" +               // Padding inside the label
	            "-fx-background-radius: 9;"         // Rounded corners
	        );
		
		lbl_hours.setStyle(
	            "-fx-background-color: #555555;" +  // Grey background
	            "-fx-text-fill: white;" +            // White text color
	            "-fx-padding: 5 10;" +               // Padding inside the label
	            "-fx-background-radius: 9;"         // Rounded corners
	        );
		
		priceBox.getChildren().add(lbl_price);
		hoursBox.getChildren().add(lbl_hours);
		
		loadScreenshots(game.getIgdb_id());
		coverImage.setImage(cover_image);
		mainImage.setImage(image);
	}
	
	public void handleDelete() {
		
			boolean confirmed = deleteConfirmationAlert();
			
			if(confirmed) {
				try {
					UserGameRepository.deleteGameFromLibrary(1, game.getIgdb_id());
					if(mainController != null) {
						if(parent == "library") {
							LibraryController controller = mainController.setCenterContent("/view/LibraryPage.fxml");
						}else if(parent == "home") {
							HomePageController controller = mainController.setCenterContent("/view/HomePage.fxml");
						}
							
		
					}else {
						//System.out.println("Main Controller is null");
					}
				
				}catch (SQLException | IOException e) {
					e.printStackTrace();
					}
				
			} else {
				return;
			}
			
	}
	
	public static boolean deleteConfirmationAlert() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("DELETE THIS GAME");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete this game?");
		
		Optional<ButtonType> result = alert.showAndWait();
		
		return result.isPresent() && result.get() == ButtonType.OK;
	}
	
	public void handleUpdate() {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UpdateGameDetails.fxml"));
			Parent root = loader.load();
			
			//Get the controller and set the data
			UpdateGameDetailsController controller = loader.getController();
			controller.setGame(game);
			
			
			Stage stage = new Stage();
			stage.setTitle("Update Game Details");
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
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
			Image newImage = new Image(screenshotPaths.get(currentIndex));
			mainImage.setImage(newImage);
		}
	}
	
	public void handleRightArrowClick() {
		if(currentIndex < screenshotPaths.size() - 1) {
			currentIndex++;
			Image newImage = new Image(screenshotPaths.get(currentIndex));
			mainImage.setImage(newImage);
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
	            try {
					setGameDetails(game);
				} catch (SQLException | IOException e) {

					e.printStackTrace();
				}
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

