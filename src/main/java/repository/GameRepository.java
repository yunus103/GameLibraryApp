package repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Game;
import service.DatabaseService;

//CRUD operations for game entity in the database
public class GameRepository {
	
	//Save game record to games table
	public void save(Game game) throws SQLException, IOException {
		//SQL INSERT 
		String sql = "INSERT INTO games (igdb_id, title, platform_id, release_date, company, cover_image_path, rating, normalized_title, summary, storyline) " + 
					 "VALUES(?,?,?,?,?,?,?,?,?,?)";
		
        // Obtain a connection using DatabaseService and use try-with-resources for auto-closing resources
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			//Set each parameter
			stmt.setInt(1, game.getIgdb_id());
			stmt.setString(2, game.getTitle());
			stmt.setInt(3, game.getPlatform_id());
			stmt.setDate(4, game.getRelease_date() != null ? Date.valueOf(game.getRelease_date()) : null);
			stmt.setString(5, game.getCompany());
			stmt.setString(6, game.getCover_image_path());
			stmt.setDouble(7, game.getRating());
			
			String normalizedTitle = normalizeTitle(game.getTitle());
	        stmt.setString(8, normalizedTitle);
	        stmt.setString(9, game.getSummary());
	        stmt.setString(10, game.getStoryline());
			stmt.executeUpdate();
			saveGenre(game);
			
		}
	}
	
	//Retrieve a Game by igdb_id
	public Game findById(int igdb_id) throws SQLException, IOException {
		String sql = "SELECT * FROM games WHERE igdb_id = ?";
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, igdb_id);
			
			try(ResultSet rs = stmt.executeQuery()){
				if(rs.next()) {
					//Map the ResultSet to a Game object
					return mapResultSetToGame(rs, conn);
				}
			}
		}
		return null;
	}
	
	//Retrieves all Game records from the database
	/*public List<Game> findAll() throws SQLException, IOException{
		List<Game> games = new ArrayList<>();
		String sql = "SELECT * FROM games";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery()) {
			
			//Itereate through the result set and map each row to a game object
			while(rs.next()) {
				games.add(mapResultSetToGame(rs, conn));
			}
			
		}
		return games;
	}
	*/
	
	//Delete a game record
	public void delete(int igdbId) throws SQLException, IOException {
		String sql = "DELETE FROM games WHERE igdb_id = ?";
		
		try(Connection conn = DatabaseService.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, igdbId);
			
			stmt.executeUpdate();
		}
	}
	
	// Utility method to map a ResultSet row to a Game object.
	private Game mapResultSetToGame(ResultSet rs, Connection conn) throws SQLException {
		Game game = new Game();
		game.setIgdb_id(rs.getInt("igdb_id"));
		game.setTitle(rs.getString("title"));
		game.setPlatform_id(rs.getInt("platform_id"));
		
		Date sqlDate = rs.getDate("release_date");
		if(sqlDate != null) {
			game.setRelease_date(sqlDate.toLocalDate());
		}
		
		game.setCompany(rs.getString("company"));
		game.setCover_image_path(rs.getString("cover_image_path"));
		game.setRating(rs.getDouble("rating"));
		game.setStoryline(rs.getString("storyline"));
		game.setSummary(rs.getString("summary"));
		
		try {
			game.setGenres(getGenresFromDB(rs.getInt("igdb_id"), conn));
		} catch (SQLException | IOException e) {

			e.printStackTrace();
		}
		return game;
	}
	
	private List<Integer> getGenresFromDB(int game_id, Connection conn) throws SQLException, IOException{
		
		String sql = "SELECT genre_id FROM game_genres WHERE game_id = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, game_id);
			
			ResultSet rs = stmt.executeQuery();
			
			List<Integer> genreIds = new ArrayList<Integer>();
			
			while(rs.next()) {
				genreIds.add(rs.getInt("genre_id"));
			}
			
			return genreIds;
		}

	}
	
	
	//Search a game by its name in DB. Used in HelperService
	public List<Game> searchByName(String query) throws SQLException, IOException {
	    List<Game> games = new ArrayList<>();
	    
	    // Normalize the search query
	    String normalizedQuery = normalizeTitle(query);
	    
	    // SQL query with priority calculation
	    String sql = "SELECT *, " +
	                 "CASE " +
	                 "  WHEN normalized_title = ? THEN 3 " +  // Exact match
	                 "  WHEN normalized_title LIKE ? THEN 2 " +  // Begins with
	                 "  ELSE 1 " +  // Contains
	                 "END AS priority " +
	                 "FROM games " +
	                 "WHERE normalized_title LIKE ? " +
	                 "ORDER BY priority DESC, title ASC";

	    try (Connection conn = DatabaseService.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        // Set parameters
	        stmt.setString(1, normalizedQuery);  // Exact match
	        stmt.setString(2, normalizedQuery + "%");  // Begins with
	        stmt.setString(3, "%" + normalizedQuery + "%");  // Contains

	        // Execute query
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	            	//System.out.println(rs.getString("title"));
	                games.add(mapResultSetToGame(rs, conn));
	            }
	        }
	    }

	    return games;
	}
	
	
	// Helper method to normalize titles
	public static String normalizeTitle(String title) {
	    if (title == null) return null;
	    return title.toLowerCase()
	               .replaceAll("[^a-z0-9]", "");  // Remove non-alphanumeric characters
	}
	
	public static void saveGenre(Game game) throws SQLException, IOException {
		//Link game to its genres
		if(game.getGenres() != null) {
			for(Integer genreId : game.getGenres()) {
			linkGameToGenre(game.getIgdb_id(), genreId);
			}
		}
		
	}

	public static List<String> getGenreNamesById(List<Integer> genreIds) throws SQLException, IOException {
	    // Check if the genreIds list is empty
	    if (genreIds == null || genreIds.isEmpty()) {
	    	//System.out.println("null");
	        return new ArrayList<>(); // Return an empty list if no genre IDs are provided
	    }

	    // Create a comma-separated list of genre IDs for the SQL IN clause
	    String placeholders = String.join(",", Collections.nCopies(genreIds.size(), "?"));
	    String sql = "SELECT genre_name FROM genres WHERE genre_id IN (" + placeholders + ")";

	    try (Connection conn = DatabaseService.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        // Set the genre IDs as parameters in the PreparedStatement
	        for (int i = 0; i < genreIds.size(); i++) {
	            stmt.setInt(i + 1, genreIds.get(i));
	        }

	        // Execute the query
	        ResultSet rs = stmt.executeQuery();

	        // Process the result set
	        List<String> genres = new ArrayList<>();
	        while (rs.next()) {
	            genres.add(rs.getString("genre_name"));
	        }

	        return genres;
	    }
	}
	private static void linkGameToGenre(int igdb_id, Integer genreId) throws SQLException, IOException {
		String insertGameGenre = "INSERT INTO game_genres (game_id, genre_id) VALUES (?, ?) "
				+ "ON DUPLICATE KEY UPDATE game_id = game_id;";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(insertGameGenre)){
			
			stmt.setInt(1, igdb_id);
			stmt.setInt(2, genreId);
			stmt.executeUpdate();
		}catch(SQLException e) {
			
		}
		
	}
	
	
}




