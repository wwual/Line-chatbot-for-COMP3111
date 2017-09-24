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
		String result = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		String sCurrentLine;
		
		
		try {
			isr = new InputStreamReader(
                    this.getClass().getResourceAsStream(FILENAME));
			br = new BufferedReader(isr);
			
			while (result == null && (sCurrentLine = br.readLine()) != null) {
				String[] parts = sCurrentLine.split(":");
				int length=parts[0].length();
				String temp=null;
				for(int i=0; i<text.length()-length+1; i++) {
					temp=text.substring(i, i+length);
				if (temp.toLowerCase().equals(parts[0].toLowerCase())) {
					result = parts[1]; 
					break;
				}
			}
			
			if (result != null) { return result;}
			}
				
			Connection connection=getConnection();

			PreparedStatement stmt=connection.prepareStatement(
					"SELECT response FROM data WHERE keyword like('%', ?, '%')");
			
				
			stmt.setString(1, text);
			ResultSet rs=stmt.executeQuery();
			result=rs.getString(1);
			rs.close();
			stmt.close();
			connection.close();
			
		
		} catch (IOException e) {
			log.info("IOException while reading file: {}", e.toString());
		} finally {
			try {
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
				
				
			} catch (IOException ex) {
				log.info("IOException while closing file: {}", ex.toString());
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
