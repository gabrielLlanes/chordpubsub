package lmg.peerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class PeerServiceApplication {

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(3);
		taskScheduler.setThreadNamePrefix("MainTaskScheduler");
		return taskScheduler;
	}

	@Bean
	public DataSource mysqlDataSource() throws IOException, IllegalStateException {
		String dbUrlFileLocation = System.getenv("DB_URL_FILE_LOCATION");
		String dbUsernameFileLocation = System.getenv("DB_USERNAME_FILE_LOCATION");
		String dbPasswordFileLocation = System.getenv("DB_PASSWORD_FILE_LOCATION");
		if(dbUrlFileLocation == null || dbUsernameFileLocation == null || dbPasswordFileLocation == null) {
			throw new IllegalStateException("Database configuration information was incomplete");
		}
		try {
			String dbUrl = new String(Files.readAllBytes(Path.of(dbUrlFileLocation)));
			String dbUsername = new String(Files.readAllBytes(Path.of(dbUsernameFileLocation)));
			String dbPassword = new String(Files.readAllBytes(Path.of(dbPasswordFileLocation)));
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setUrl(dbUrl);
			dataSource.setUsername(dbUsername);
			dataSource.setPassword(dbPassword);
			return dataSource;
		} catch(IOException e) {
			throw new IOException("Failed to read database configuration sources.", e);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(PeerServiceApplication.class, args);
	}


}
