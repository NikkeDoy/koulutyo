package fi.huitsinnevada.Koulutyo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KoulutyoApplication {

	public static void main(String[] args) {
        FileService.init();
		SpringApplication.run(KoulutyoApplication.class, args);
	}
}
