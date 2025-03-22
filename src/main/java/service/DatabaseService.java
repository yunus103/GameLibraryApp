package service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;



public class DatabaseService {
	private static Connection connection;
	
	public static Connection getConnection() throws SQLException, IOException{
		if(connection == null || connection.isClosed()) {
			Properties props = new Properties();
			props.load(DatabaseService.class.getResourceAsStream("/config.properties"));
			String url = props.getProperty("db.url");
			String user = props.getProperty("db.user");
			String password = props.getProperty("db.password");
			connection = DriverManager.getConnection(url, user, password);
			
			//System.out.println(url);
			//System.out.println(user);
			//System.out.println(password);
		}
		return connection;
	}
}
