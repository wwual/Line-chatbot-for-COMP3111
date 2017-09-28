package com.example.bot.spring;
import java.sql.*;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;




@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	public String search(String text) throws Exception {
		String result=null;
		boolean exist=true;
		try{result=super.search(text);
				}catch(Exception e){exist=false;}
		if(exist){return result;}
		else {
		try {
				
			Connection connection=getConnection();

			PreparedStatement stmt=connection.prepareStatement(
					"SELECT response FROM data WHERE keyword like concat('%', ?, '%')");
			
			PreparedStatement stmt2=connection.prepareStatement(
					"UPDATE count SET times = times+1 WHERE keyword like concat('%', ?, '%')");
			
			PreparedStatement stmt3=connection.prepareStatement(
					"SELECT times FROM count WHERE keyword like concat('%', ?, '%')");
			
				
			stmt.setString(1, text);
			stmt2.setString(1, text);
			stmt3.setString(1, text);
			ResultSet rs=stmt.executeQuery();
			stmt2.executeUpdate();
			ResultSet rs2=stmt3.executeQuery();
			rs.next();
			rs2.next();
			result=rs.getString(1);
			int resultCount=rs2.getInt(1);
			result=result+ "  You had hit this sentece for " + resultCount + " times.";
			rs.close();
			rs2.close();
			stmt.close();
			stmt2.close();
			stmt3.close();
			connection.close();
			
		
		
		}	catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
		} 
		}
		if (result != null)
			return result;
		
		throw new Exception("NOT FOUND");
    }
	
	
	private final String FILENAME = "/static/database.txt";
	


	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
