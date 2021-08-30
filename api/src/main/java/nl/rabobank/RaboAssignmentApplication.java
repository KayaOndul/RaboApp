package nl.rabobank;

import nl.rabobank.mongo.MongoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class RaboAssignmentApplication  {
    public static void main(final String[] args) {
        SpringApplication.run(RaboAssignmentApplication.class, args);
    }
}
