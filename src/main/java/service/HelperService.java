package service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HeaderElement;

import com.controller.GameDetailsController;
import com.controller.MainController;
import com.mashape.unirest.http.exceptions.UnirestException;

import model.Game;
import repository.GameRepository;
import repository.IGDBRepository;
import repository.UserGameRepository;

public class HelperService {
	private static final int MIN_LOCAL_RESULTS = 3;
	private GameRepository gameRepo;
    private IGDBRepository igdbRepo;
    public static final String IMAGE_CACHE_DIR = "data/cache/images";
    
    public HelperService(IGDBRepository igdbRepo, GameRepository gameRepo) {
    	this.gameRepo = gameRepo;
    	this.igdbRepo = igdbRepo;
    }
	

	public List<Game> searchGame(String query) throws SQLException, IOException, UnirestException{
		
		List<Game> localResults = gameRepo.searchByName(query);

		//If results enough, MIN 5 results
		if(hasEnoughLocalResults(localResults)) {
			return localResults;
		}
		
		//Results not enough, call API
		List<Game> apiResults = igdbRepo.searchByName(query);
		
		for(Game game: apiResults) {
			//Check if it exist in DB
			if(gameRepo.findById(game.getIgdb_id()) == null) {
				gameRepo.save(game);
			}
		}
		
		return mergeResults(localResults, apiResults);
		
	}

	private List<Game> mergeResults(List<Game> localResults, List<Game> apiResults) {
		Map<Integer, Game> uniqueGames = new LinkedHashMap<>();
		
		//Add DB results first
		for(Game game: localResults) {
			uniqueGames.put(game.getIgdb_id(), game);
		}
		
		//Add API results, avoiding
		for(Game game: apiResults) {
			uniqueGames.putIfAbsent(game.getIgdb_id(), game);
		}
		
		return new ArrayList<>(uniqueGames.values());
	}


	private boolean hasEnoughLocalResults(List<Game> game) {
		return game.size() >= MIN_LOCAL_RESULTS;
	}
	
	public static void downloadImage(String imageUrl, String destinationPath) throws MalformedURLException, IOException {
		// Open an input stream from the URL
		try(InputStream in = new URL(imageUrl).openStream()){
			Files.copy(in, Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	
	public static void openGameDetails(Game game, MainController mainController) throws IOException {
		//System.out.println("Clicked On: " + game.getTitle());
		
		if(mainController != null) {
			GameDetailsController controller = mainController.setCenterContent("/view/GameDetails.fxml");
			controller.setGame(game);
		}else {
			//System.out.println("Main Controller is null");
		}
		
	}
}



