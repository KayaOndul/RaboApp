package nl.rabobank.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST.value()),
    NO_USERS_FOUND(HttpStatus.BAD_REQUEST.value()),
    NO_ERRORCODE_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value()),
    NOT_GRANTORS_ACCOUNT(HttpStatus.BAD_REQUEST.value()),
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value()),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST.value()),
    ALREADY_HAS_GRANT(HttpStatus.BAD_REQUEST.value());

    private final int httpStatus;

    ErrorCode(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static ErrorCode toEnum(String value) {
        try {
            return ErrorCode.valueOf(value);
        } catch (Exception e) {
            return ErrorCode.NO_ERRORCODE_FOUND;
        }
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
