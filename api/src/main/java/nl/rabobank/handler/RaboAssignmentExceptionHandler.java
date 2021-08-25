package nl.rabobank.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.error.ErrorCode;
import nl.rabobank.error.ErrorDetail;
import nl.rabobank.error.ErrorResponse;
import nl.rabobank.exception.ApiException;
import nl.rabobank.util.MessageTranslator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class RaboAssignmentExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${messages.default.error-prefix}")
    private String errorPrefix;

    private final MessageTranslator messageTranslator;


    @ExceptionHandler(value = {ApiException.class})
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ae, WebRequest request) {
        ErrorCode errorCode = ae.getErrorCode();
        ErrorDetail errorDetail = this.buildErrorDetail(errorCode);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(List.of(errorDetail));
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e, WebRequest request) {
        ErrorCode systemErrorCode = ErrorCode.SYSTEM_ERROR;
        ErrorDetail errorDetail = this.buildErrorDetail(systemErrorCode);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(List.of(errorDetail));
        return ResponseEntity.status(systemErrorCode.getHttpStatus())
                .body(errorResponse);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
                                                               WebRequest request) {
        ErrorDetail errorDetail = this.buildErrorDetail(ErrorCode.INVALID_REQUEST_BODY);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(List.of(errorDetail));
        return ResponseEntity.status(status)
                .body(errorResponse);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
                                                               WebRequest request) {

        List<ObjectError> errorList = getErrorList(ex);
        List<ErrorDetail> errorDetailList = new ArrayList<>();
        for (ObjectError objectError : errorList) {
            ErrorCode errorCode = ErrorCode.toEnum(objectError.getDefaultMessage());
            ErrorDetail errorDetail = this.buildErrorDetail(errorCode, getField(ex));
            errorDetailList.add(errorDetail);
        }
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(errorDetailList);
        return ResponseEntity.status(status)
                .body(errorResponse);
    }


    private List<ObjectError> getErrorList(MethodArgumentNotValidException ex) {
        return new ArrayList<>(ex.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(ObjectError::getObjectName, p -> p, (p, q) -> p))
                .values());
    }

    private String getField(MethodArgumentNotValidException ex) {

        try {
            return ex.getBindingResult()
                    .getFieldErrors()
                    .get(0)
                    .getField();
        } catch (Exception e) {
            return Objects.requireNonNull(ex.getBindingResult()
                    .getAllErrors()
                    .get(0)
                    .getCodes())[1];
        }
    }

    private ErrorDetail buildErrorDetail(ErrorCode errorCode) {
        return ErrorDetail.builder()
                .errorCode(errorCode)
                .message(messageTranslator.getMessage(errorPrefix + errorCode.name()))
                .build();
    }

    private ErrorDetail buildErrorDetail(ErrorCode errorCode, String argument) {
        ErrorDetail errorDetail = this.buildErrorDetail(errorCode);
        errorDetail.setArgument(argument);
        return errorDetail;
    }
}