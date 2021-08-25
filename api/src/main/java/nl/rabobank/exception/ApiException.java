package nl.rabobank.exception;

import lombok.Getter;
import nl.rabobank.error.ErrorCode;

@Getter
public class ApiException extends RuntimeException{
    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }
}
