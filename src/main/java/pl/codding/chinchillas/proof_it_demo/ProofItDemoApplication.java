package pl.codding.chinchillas.proof_it_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ProofItDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProofItDemoApplication.class, args);
	}

}
