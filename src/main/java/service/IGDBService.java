package service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class IGDBService {
	//Store API credentials and token
	private String clientId;
	private String clientSecret;
	private String accessToken;
	private long tokenExpirationTime;
	
	//Base URL
	public static final String BASE_URL = "https://api.igdb.com/v4";
	
	public IGDBService() throws IOException, UnirestException {
		
		//Load config properties
		Properties props = new Properties();
		try(InputStream in = getClass().getResourceAsStream("/config.properties")){
			props.load(in);
		}
		
		//Retieve IGDB client credentials
		clientId = props.getProperty("igdb.client_id");

		clientSecret = props.getProperty("igdb.client_secret");
		
		loadTokenFromFile();
		//Authenticate via access token
		ensureValidToken();
		
		
		//Testing if token and expire logic works
		//System.out.println(accessToken);
		//System.out.println(tokenExpirationTime);
	}
	
	public String getClientId() {
		return clientId;
	}

	
	public String getAccessToken() throws FileNotFoundException, UnirestException, IOException {
		ensureValidToken();
		return accessToken;
	}


	//Authenticates with the IGDB API
	private void authenticate() throws UnirestException, FileNotFoundException, IOException {
		//POST request query
		HttpResponse<JsonNode> response = Unirest.post("https://id.twitch.tv/oauth2/token")
				.queryString("client_id", clientId)
				.queryString("client_secret", clientSecret)
				.queryString("grant_type", "client_credentials")
				.asJson();
		
		if(response.getStatus() >= 200 && response.getStatus() < 300) {
			//Parse the JSON response to extract the access token
			this.accessToken = response.getBody().getObject().getString("access_token");
			
			//Expire date
			long expiresIn = response.getBody().getObject().getLong("expires_in");
			this.tokenExpirationTime = System.currentTimeMillis() + (expiresIn * 1000);
			
			saveTokenToFile();
			
		} else {
			//Handle auth fail
			System.err.println("Authentication failed with status: " + response.getStatus());
		}
	}
	//Save token to a file
	private void saveTokenToFile() throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.setProperty("accessToken", accessToken);
		props.setProperty("tokenExpirationTime", String.valueOf(tokenExpirationTime));
		
		try(OutputStream output = new FileOutputStream("token.properties")){
			props.store(output, "IGDB API Token");
		}
	}
	
	//Load token from token.properties
	private void loadTokenFromFile() {
		Properties props = new Properties();
		try(InputStream input = new FileInputStream("token.properties")) {
			props.load(input);
			this.accessToken = props.getProperty("accessToken");
			this.tokenExpirationTime = Long.parseLong(props.getProperty("tokenExpirationTime", "0"));
		} catch (IOException | NumberFormatException e) {
			//System.out.println("No valid token found. Fetching a new one..");
		}
	}
	
	private void ensureValidToken() throws FileNotFoundException, UnirestException, IOException {
		if(accessToken == null || System.currentTimeMillis() >= tokenExpirationTime) {
			//System.out.println("Token expired or missing. Refreshing token...");
			authenticate();
		}
	}
}












