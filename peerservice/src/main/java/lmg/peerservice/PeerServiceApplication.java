package lmg.peerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PeerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeerServiceApplication.class, args);
	}


}
