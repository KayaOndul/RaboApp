package nl.rabobank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import nl.rabobank.mongo.MongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"nl.rabobank.*"})
@EntityScan(basePackages = {"nl.rabobank.*"})
@EnableMongoRepositories(basePackages ={"nl.rabobank.*"} )
@Import(MongoConfiguration.class)
public class RaboAssignmentApplication
{
    public static void main(final String[] args)
    {
        SpringApplication.run(RaboAssignmentApplication.class, args);
    }
}
