package nl.rabobank.handler;

import nl.rabobank.error.ErrorCode;
import nl.rabobank.error.ErrorResponse;
import nl.rabobank.exception.ApiException;
import nl.rabobank.util.MessageTranslator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RaboAssignmentExceptionHandlerTest {
    @InjectMocks
    private RaboAssignmentExceptionHandler raboAssignmentExceptionHandler;

    @Mock
    private MessageTranslator messageTranslator;

    @Test
    void shouldHandleApiException() {
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;
        ApiException exception = new ApiException(errorCode);

        WebRequest webRequest = mock(WebRequest.class);

        when(messageTranslator.getMessage(anyString())).thenReturn("returnMessage");

        ResponseEntity<ErrorResponse> responseEntity = raboAssignmentExceptionHandler.handleApiException(exception, webRequest);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(errorCode.getHttpStatus());
        assertThat(responseEntity.getBody()
                .getErrors()
                .get(0)
                .getMessage()).isEqualTo("returnMessage");
    }

    @Test
    void shouldHandleException() {
        Exception exception = new Exception("msg");

        WebRequest webRequest = mock(WebRequest.class);

        when(messageTranslator.getMessage(anyString())).thenReturn("msg");

        ResponseEntity<ErrorResponse> responseEntity = raboAssignmentExceptionHandler.handleUnknownException(exception, webRequest);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(ErrorCode.SYSTEM_ERROR.getHttpStatus());
        assertThat(responseEntity.getBody()
                .getErrors()
                .get(0)
                .getMessage()).isEqualTo("msg");
    }

    @Test
    void shouldHandleHttpMessageNotReadable() {
        HttpInputMessage httpInputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("msg", httpInputMessage);
        WebRequest webRequest = mock(WebRequest.class);

        ResponseEntity<Object> responseEntity = raboAssignmentExceptionHandler.handleHttpMessageNotReadable(exception, HttpHeaders.EMPTY,
                HttpStatus.BAD_REQUEST,
                webRequest);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        Object objectResponse = responseEntity.getBody();
        ErrorResponse errorResponse = (ErrorResponse) objectResponse;
        assertThat(errorResponse.getErrors()
                .get(0)
                .getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST_BODY);
    }

    @Test
    void shouldHandleMethodArgumentNotValid() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException methodArgumentNotValidException = new MethodArgumentNotValidException(mock(MethodParameter.class),
                bindingResult) {
            @Override
            public String getMessage() {
                return "method-argument-not-valid-exception";
            }
        };

        WebRequest webRequest = mock(WebRequest.class);

        ResponseEntity<Object> responseEntity = raboAssignmentExceptionHandler.handleMethodArgumentNotValid(methodArgumentNotValidException,
                HttpHeaders.EMPTY,
                HttpStatus.BAD_REQUEST,
                webRequest);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}