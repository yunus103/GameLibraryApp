package repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;
import com.mysql.cj.xdevapi.Result;

import model.Game;
import model.Platform;
import service.DatabaseService;

public class UserGameRepository {
	
	//Add a game to user library
	public static void addGameToLibrary(int user_id, int igdb_id, double price, String status, int hours) throws SQLException, IOException {
		String sql = "INSERT INTO user_games (user_id, igdb_id, hours_played, price_paid, status, added_date) VALUES(?,?,?,?,?,?)";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			
			stmt.setInt(1, user_id);
			stmt.setInt(2, igdb_id);
			stmt.setInt(3, hours);
			stmt.setDouble(4, price);
			stmt.setString(5, status);
			stmt.setTimestamp(6, currentTimestamp);
			
			stmt.executeUpdate();
		}
	}
	
	public static List<Game> getUserGames(int user_id, String sort) throws SQLException, IOException {
		String sql = "SELECT igdb_id, cover_image_path, title "
				+ "FROM user_games "
				+ "JOIN games USING(igdb_id) "
				+ "WHERE user_id = ? "
				+ "ORDER BY "
				+ sort;
				//+ "title ASC";
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, user_id);
			
			ResultSet rs = stmt.executeQuery();
			
			List<Game> games = new ArrayList<>();
			
			while(rs.next()) {
				Game game = new Game();
				game.setCover_image_path(rs.getString("cover_image_path"));
				game.setTitle(rs.getString("title"));
				game.setIgdb_id(rs.getInt("igdb_id"));
				
				games.add(game);
			}
			return games;
		}
	}
	
	//Filter game results, call in FilterController and LibraryController
	public static List<Game> getFilteredGames(int user_id, String sort, List<String> genres, List<String> platforms) throws SQLException, IOException{
		List<Game> games = new ArrayList<Game>();
		
		String sql = "SELECT g.igdb_id, g.title, g.cover_image_path, "
	               + "GROUP_CONCAT(DISTINCT gn.genre_name) AS genres, "
	               + "GROUP_CONCAT(DISTINCT p.name) AS platforms "
	               + "FROM games g "
	               + "JOIN user_games ug ON g.igdb_id = ug.igdb_id "
	               + "LEFT JOIN game_genres gg ON g.igdb_id = gg.game_id "
	               + "LEFT JOIN genres gn ON gg.genre_id = gn.genre_id "
	               + "LEFT JOIN user_game_platforms ugp ON g.igdb_id = ugp.game_id "
	               + "LEFT JOIN platforms p ON ugp.platform_id = p.id "
	               + "WHERE ug.user_id = ?";
		
		if(genres != null && !genres.isEmpty()) {
			String genrePlaceHolders = String.join(",", Collections.nCopies(genres.size(), "?"));
			sql += " AND gn.genre_name IN (" + genrePlaceHolders + ")";
		}
		
		if(platforms != null && !platforms.isEmpty()) {
			String platformPlaceHolders = String.join(",", Collections.nCopies(platforms.size(), "?"));
			sql += " AND p.name IN (" + platformPlaceHolders + ")";
		}
		
		sql += " GROUP BY g.igdb_id ORDER BY " + sort; // Grouping prevents duplicates
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			int index = 1;
			stmt.setInt(index++, user_id);
			
			// Set genre parameters
			if (genres != null && !genres.isEmpty()) {
	            for (String genre : genres) {
	                stmt.setString(index++, genre);
	            }
	        }
			
			// Set platform parameters
	        if (platforms != null && !platforms.isEmpty()) {
	            for (String platform : platforms) {
	                stmt.setString(index++, platform);
	            }
	        }
	        
	        
			try(ResultSet rs = stmt.executeQuery()){
				while(rs.next()) {					
					Game game = new Game();
					game.setCover_image_path(rs.getString("cover_image_path"));
					game.setTitle(rs.getString("title"));
					game.setIgdb_id(rs.getInt("igdb_id"));
					
					games.add(game);
					
				}
			}
		}
		
		return games;
	}
	
	//Update hours played for given user
	public static void updateHoursPlayed(int user_id, int igdb_id, int hours) throws SQLException, IOException {
		String sql = "UPDATE user_games SET hours_played = ? WHERE user_id = ? AND igdb_id = ?";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, hours);
			stmt.setInt(2, user_id);
			stmt.setInt(3, igdb_id);
			
			stmt.executeUpdate();
		}
	}
	
	public static void updatePricePaid(int user_id, int igdb_id, double price) throws SQLException, IOException {
		String sql = "UPDATE user_games SET price_paid = ? WHERE user_id = ? AND igdb_id = ?";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setDouble(1, price);
			stmt.setInt(2, user_id);
			stmt.setInt(3, igdb_id);
			
			stmt.executeUpdate();
		}
	}
	
	public static void updateIsFavorite(int user_id, int igdb_id, boolean is_favorite) throws SQLException, IOException {
		String sql = "UPDATE user_games SET is_favorite = ? WHERE user_id = ? AND igdb_id = ?";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setBoolean(1, is_favorite);
			stmt.setInt(2, user_id);
			stmt.setInt(3, igdb_id);
			
			stmt.executeUpdate();
		}
	}
	
	public static void updateStatus(int user_id, int igdb_id, String stat) throws SQLException, IOException {
		String sql = "UPDATE user_games SET status = ? WHERE user_id = ? AND igdb_id = ?";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1, stat);
			stmt.setInt(2, user_id);
			stmt.setInt(3, igdb_id);
			
			stmt.executeUpdate();
		}
	}
	

	public static List<String> getUserPlatform(int userId, int gameId) throws SQLException, IOException{
		String query = "SELECT name FROM platforms p " + 
					   "JOIN user_game_platforms ugp ON p.id = ugp.platform_id " + 
					   "WHERE ugp.user_id = ? AND ugp.game_id = ?";
		List<String> platforms = new ArrayList<>();
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setInt(1, userId);
			stmt.setInt(2, gameId);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				
				platforms.add(rs.getString("name"));
			}
		}catch(SQLException e) {
			
		}
				
		return platforms;
	}
	
	public static void updateUserPlatforms(int userId, int gameId, List<Integer> platformIds) throws IOException, SQLException {
	    
		String deleteQuery = "DELETE FROM user_game_platforms WHERE user_id = ? AND game_id = ?";
	    String insertQuery = "INSERT INTO user_game_platforms (user_id, game_id, platform_id) VALUES (?, ?, ?)";
	    
	    try (Connection conn = DatabaseService.getConnection();
	         PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
	         PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
	        
	        // Delete existing platforms
	        deleteStmt.setInt(1, userId);
	        deleteStmt.setInt(2, gameId);
	        deleteStmt.executeUpdate();
	        
	        // Insert new platform selections
	        for (int platformId : platformIds) {
	            insertStmt.setInt(1, userId);
	            insertStmt.setInt(2, gameId);
	            insertStmt.setInt(3, platformId);
	            insertStmt.executeUpdate(); // Use executeUpdate() instead of executeQuery()
	            
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace(); // Print error to debug
	    }
	}

	public static int getPlatformId(String platformName){
		String sql = "SELECT id FROM platforms WHERE name = ?";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1, platformName);
			
			ResultSet rs = stmt.executeQuery();
			
			//System.out.println(rs.getInt("id"));
			if (rs.next()) {  // Move cursor to first row
	            return rs.getInt("id");
	        }
			
		} catch (SQLException | IOException e) {

		}
		return 0;


	}
	
	public static List<Game> getLastAddedGames(int user_id) throws SQLException, IOException{
		
		String sql = "SELECT igdb_id, title, cover_image_path FROM user_games "
					+ "JOIN games USING(igdb_id) "
					+ "WHERE user_id = ? "
					+ "ORDER BY added_date DESC "
					+ "LIMIT 12";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, user_id);
			ResultSet rs = stmt.executeQuery();
			
			List<Game> games = new ArrayList<>();
			
			while(rs.next()) {
				String title = rs.getString("title");
				String coverPath = rs.getString("cover_image_path");
				int igdbId = rs.getInt("igdb_id");
				
				Game game = new Game();
				game.setCover_image_path(coverPath);
				game.setTitle(title);
				game.setIgdb_id(igdbId);
				
				games.add(game);
			}
			
			return games;
		}
		
	}
	
	public static boolean isInLibrary(int user_id, int igdb_id) throws SQLException, IOException {
		
		String sql = "SELECT * FROM user_games WHERE user_id = ? AND igdb_id = ?";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, user_id);
			stmt.setInt(2, igdb_id);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				return true;
			}else {
				return false;
			}
		}
	}
	
	public static void deleteGameFromLibrary(int user_id, int game_id) throws SQLException, IOException {
		String sql = "DELETE FROM user_games WHERE user_id = ? AND igdb_id = ?";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, user_id);
			stmt.setInt(2, game_id);
			
			stmt.executeUpdate();
		}
	}
	
	public static double getPricePaid(int user_id, int game_id) throws SQLException, IOException {
		String sql = "SELECT price_paid FROM user_games WHERE user_id = ? AND igdb_id = ?";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, user_id);
			stmt.setInt(2, game_id);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				return rs.getDouble("price_paid");
			} else {
				return 0.0;
			}
		}
	}
	
	public static int getHoursPlayed(int user_id, int game_id) throws SQLException, IOException {
		String sql = "SELECT hours_played FROM user_games WHERE user_id = ? AND igdb_id = ?";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, user_id);
			stmt.setInt(2, game_id);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt("hours_played");
			} else {
				return 0;
			}
		}
	}
	
	public static String getStatus(int user_id, int game_id) throws SQLException, IOException {
		String sql = "SELECT status FROM user_games WHERE user_id = ? AND igdb_id = ?";
		
		try(Connection conn = DatabaseService.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, user_id);
			stmt.setInt(2, game_id);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				return rs.getString("status");
			} else {
				return null;
			}
		}
	}
}






