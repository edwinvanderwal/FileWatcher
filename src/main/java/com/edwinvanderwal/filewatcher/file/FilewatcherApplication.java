package com.edwinvanderwal.filewatcher.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({
				"com.edwinvanderwal.filewatcher"				 
				 })
@EnableJpaRepositories("com.edwinvanderwal.filewatcher.repository")
 @EntityScan("com.edwinvanderwal.filewatcher.model")   
public class FilewatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilewatcherApplication.class, args);
	}

}
