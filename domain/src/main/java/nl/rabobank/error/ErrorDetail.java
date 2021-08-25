package nl.rabobank.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDetail {

    @JsonProperty("error_code")
    private ErrorCode errorCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("argument")
    private String argument;
}