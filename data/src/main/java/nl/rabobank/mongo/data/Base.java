package nl.rabobank.mongo.data;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

@Data
public class Base {
    @Id
    private Long id;

}
