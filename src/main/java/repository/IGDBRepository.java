package repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import model.Game;
import service.HelperService;
import service.IGDBService;


public class IGDBRepository{
	private IGDBService igdbService;  // Dependency injection
	
	
	public IGDBRepository(IGDBService igdbService){
		this.igdbService = igdbService;
		
	}

	//Search game using API
	public List<Game> searchByName(String query) throws UnirestException, FileNotFoundException, IOException{
		
		String clientId = igdbService.getClientId();
		String accessToken = igdbService.getAccessToken();
		
		//POST request to search
		HttpResponse<JsonNode> response = Unirest.post(IGDBService.BASE_URL + "/games")
				.header("Client-ID", clientId)
				.header("Authorization", "Bearer " + accessToken)
				.header("Accept", "application/json")
				.body("search \"" + query + "\"; fields id, name, genres, involved_companies.company.name, first_release_date, cover.url, rating, summary, storyline; " +
						"where game_type = (0, 8, 9) & (status != 7 | status = null) & version_parent = null & platforms = (169, 48, 6, 167, 49); ")
				.asJson();
		//Debug
		//System.out.println("API Response: " + response.getBody());
		
		
		//Parse response and return a list of Game objects
		if(response.getStatus() >= 200 && response.getStatus() < 300) {
			return parseGames(response.getBody());
		}else {
	        //System.out.println("Error: IGDB API request failed with status " + response.getStatus());
	        return null;
		}	
	}

	private List<Game> parseGames(JsonNode jsonNode) {
		
		List<Game> games = new ArrayList<>();
		jsonNode.getArray().forEach(obj -> {
			JSONObject gameJson = (JSONObject) obj;
			Game game = new Game();
			game.setIgdb_id(gameJson.getInt("id"));
			game.setTitle(gameJson.getString("name"));
			
			if (gameJson.has("involved_companies")) {
			    JSONArray companiesArray = gameJson.getJSONArray("involved_companies");
			    for (int i = 0; i < companiesArray.length(); i++) {
			        JSONObject involvedObj = companiesArray.getJSONObject(i);
			        if (involvedObj.has("company")) {
			            JSONObject companyObj = involvedObj.getJSONObject("company");
			            String companyName = companyObj.getString("name");
			            // Process companyName as needed (e.g., add to a list)
			            game.setCompany(companyName);
			        }
			    }
			}
			/*
			if(gameJson.has("genres")) {
				game.setGenres(null)
			}*/
			
			if (gameJson.has("cover")) {
			    JSONObject coverObj = gameJson.getJSONObject("cover");
			    String coverUrl = coverObj.getString("url");
			    
			    if(coverUrl != null && !coverUrl.startsWith("http")) {
			    	coverUrl = "https:" + coverUrl;
			    }
			    
			    String hdUrl = coverUrl.replace("t_thumb", "t_cover_big");
			    
			    
			    
			    // Define a local file name (e.g., using the igdb_id)
			    String localFilePath = HelperService.IMAGE_CACHE_DIR + "/" + game.getIgdb_id() + ".jpg";
			    
			    File imageFile = new File(localFilePath);
			    if(!imageFile.exists()) {
			    	try {
						HelperService.downloadImage(hdUrl, localFilePath);
					} catch (IOException e) {
						e.printStackTrace();
					}
			    }
			    
			    // Now use coverUrl as needed
			    //game.setCover_image_path(coverUrl);  This returns actual URL not path
			    game.setCover_image_path(localFilePath);
			}
			
			if(gameJson.has("rating"))
			{
				
				game.setRating(Math.round(gameJson.getDouble("rating")));
			}else {
				game.setRating(0.0);
			}
			
			game.setNormalized_title(GameRepository.normalizeTitle(gameJson.getString("name")));
			
			if(gameJson.has("storyline"))
			{
				game.setStoryline(gameJson.getString("storyline"));
			}
			
			if(gameJson.has("summary"))
			{
				game.setSummary(gameJson.getString("summary"));
			}
			
			if (gameJson.has("first_release_date")) {
		        long timestamp = gameJson.getBigInteger("first_release_date").longValue(); // Unix timestamp in milliseconds
		        LocalDate releaseDate = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
		        game.setRelease_date(releaseDate);
		    }
			
			if(gameJson.has("genres")) {
				JSONArray genresArray = gameJson.getJSONArray("genres");
				
				List<Integer> genres = new ArrayList<Integer>();
				for(int i = 0; i < genresArray.length(); i++){
					genres.add(genresArray.getInt(i));
				}
				
				game.setGenres(genres);
			}
			
			//Add game to the list
			games.add(game);
		});
		return games;
	}
	
	
	//Might be unnecessary
	/*
	 public Game getGameById(int igdbId) throws FileNotFoundException, UnirestException, IOException {
	 
		String clientId = igdbService.getClientId();
		String accessToken = igdbService.getAccessToken();
		
		//POST request to search
				HttpResponse<JsonNode> response = Unirest.post(IGDBService.BASE_URL + "/games")
						.header("Client-ID", clientId)
						.header("Authorization", "Bearer " + accessToken)
						.header("Accept", "application/json")
						.body("fields *; where id = " + igdbId + ";")
						.asJson();
				
		if(response.getStatus() >= 200 && response.getStatus() < 300) {
			return parseGameFromJson(response.getBody().toString());
		}else {
	        //System.out.println("Error: IGDB API request failed with status " + response.getStatus());
	        return null;
		}
				
	}
*/	
    
    private String getJsonString(JSONObject json, String key) {
        return json.has(key) ? json.getString(key) : null;
    }

    private double getJsonDouble(JSONObject json, String key) {
        return json.has(key) ? json.getDouble(key) : 0.0;
    }
    
    public static void downloadScreenShots(int igdb_id) {
    	
    
   
    }
}
