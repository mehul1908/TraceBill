package com.tracebill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TraceBillApplication {

	public static void main(String[] args) {
		SpringApplication.run(TraceBillApplication.class, args);
	}

}
