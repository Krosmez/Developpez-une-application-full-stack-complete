package com.openclassrooms.mddapi.exception;

import com.openclassrooms.mddapi.exception.GlobalExceptionHandler.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("EmailAlreadyExists -> 409")
    void handleEmailAlreadyExists() {
        ResponseEntity<ErrorResponse> response =
                handler.handleEmailAlreadyExists(new EmailAlreadyExistsException("a@b.com"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message()).contains("a@b.com");
    }

    @Test
    @DisplayName("UsernameAlreadyExists -> 409")
    void handleUsernameAlreadyExists() {
        ResponseEntity<ErrorResponse> response =
                handler.handleUsernameAlreadyExists(new UsernameAlreadyExistsException("jean"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().message()).contains("jean");
    }

    @Test
    @DisplayName("InvalidCredentials -> 401")
    void handleInvalidCredentials() {
        ResponseEntity<ErrorResponse> response =
                handler.handleInvalidCredentials(new InvalidCredentialsException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().status()).isEqualTo(401);
    }

    @Test
    @DisplayName("ResourceNotFound -> 404")
    void handleResourceNotFound() {
        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFound(new ResourceNotFoundException("Post", 5L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().message()).contains("Post").contains("5");
    }

    @Test
    @DisplayName("Validation -> 400 avec le détail des champs")
    void handleValidationErrors() throws NoSuchMethodException {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "must not be blank"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                new org.springframework.core.MethodParameter(
                        GlobalExceptionHandlerTest.class.getDeclaredMethod("dummy", String.class), 0),
                bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().errors()).containsEntry("email", "must not be blank");
    }

    @Test
    @DisplayName("Exception générique -> 500")
    void handleGenericException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleGenericException(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().status()).isEqualTo(500);
    }

    @SuppressWarnings("unused")
    void dummy(String value) {
        // target method used to build a MethodParameter for the validation test
    }
}
