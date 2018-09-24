package com.vjdon.myClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@RestController
public class myClientApplication {

	@Autowired
   	private StringRedisTemplate template;

	@RequestMapping("/")
	String hello() {
		return "Hello Vijay!!!\n";
	}

	// @GetMapping(value = "/host")
	// public String getHost() {
	// 	String host = System.getenv("postgres_host");
	// 	String dbName = System.getenv("postgres_database");
	// 	String dbUser = System.getenv("postgres_username");
	// 	String password = System.getenv("postgres_password");
	// 	String output = String.format("Host: %s\n; Database: %s\n; Username: %s\n; Password: %s\n", host, dbName,
	// 			dbUser, password);
	// 	return output;
	// }

	@GetMapping(value = "/cache")
	public String getCache() throws Exception {
		ValueOperations<String, String> ops = this.template.opsForValue();

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(TimeZone.getTimeZone("PST"));
		String timestamp = sdf.format(new Date()) + " PST";

		String key = "Greeting: " + timestamp;
		String foundInCache = "Found in Cache";
		if (!this.template.hasKey(key)) {
			foundInCache = "Not found in Cache";
			ops.set(key, "Hello World: " + timestamp);
		}

		System.out.println(foundInCache);

		return String.format("/cache called, cache: %s", ops.get(key));
	}

	@GetMapping(value = "/db")
	public String getDb() throws Exception {

		String host = System.getenv("azure-postgres_host");
		String dbName = System.getenv("azure-postgres_database");
		String dbUser = System.getenv("azure-postgres_username");
		String password = System.getenv("azure-postgres_password");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException cnfe) {
			throw new ClassNotFoundException("PostgreSQL JDBC driver NOT detected in library path", cnfe);
		}

		System.out.println("Driver detected in the library path");

		Connection connection = null;
		try {
			String url = String.format("jdbc:postgresql://%s/%s", host, dbName);
			Properties properties = new Properties();
			properties.setProperty("user", dbUser);
			properties.setProperty("password", password);
			properties.setProperty("ssl", "true");

			connection = DriverManager.getConnection(url, properties);
		} catch (Exception e) {
			System.out.println("Failed to connection to DB");
		}

		if (connection != null) {
			System.out.println("Successfully created connection to database.");

			// Perform some SQL queries over the connection.
			try {
				// Drop previous table of same name if one exists.
				Statement statement = connection.createStatement();
				statement.execute("DROP TABLE IF EXISTS inventory;");
				System.out.println("Finished dropping table (if existed).");

				// Create table.
				statement.execute(
						"CREATE TABLE inventory (id serial PRIMARY KEY, name VARCHAR(50), quantity INTEGER, createdon VARCHAR(50));");
				System.out.println("Created table.");

				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
				sdf.setTimeZone(TimeZone.getTimeZone("PST"));
				String timestamp = sdf.format(new Date()) + " PST";

				// Insert some data into table.
				int nRowsInserted = 0;
				PreparedStatement preparedStatement = connection
						.prepareStatement("INSERT INTO inventory (name, quantity, createdon) VALUES (?, ?, ?);");
				preparedStatement.setString(1, "banana");
				preparedStatement.setInt(2, 150);
				preparedStatement.setString(3, timestamp);
				nRowsInserted += preparedStatement.executeUpdate();

				preparedStatement.setString(1, "orange");
				preparedStatement.setInt(2, 154);
				preparedStatement.setString(3, timestamp);
				nRowsInserted += preparedStatement.executeUpdate();

				preparedStatement.setString(1, "apple");
				preparedStatement.setInt(2, 100);
				preparedStatement.setString(3, timestamp);
				nRowsInserted += preparedStatement.executeUpdate();
				System.out.println(String.format("Inserted %d row(s) of data.", nRowsInserted));

				// NOTE No need to commit all changes to database, as auto-commit is enabled by
				// default.

			} catch (SQLException e) {
				throw new SQLException("Encountered an error when executing given sql statement.", e);
			}
		} else {
			System.out.println("Failed to create connection to database.");
		}
		System.out.println("Execution finished.");

		if (connection != null) {
			connection = null;
		}
		return "/db called";
	}

	public static void main(String[] args) {
		SpringApplication.run(myClientApplication.class, args);
	}
}
