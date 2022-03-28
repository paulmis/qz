package server.api;

import commons.entities.utils.ApiError;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import server.api.exceptions.PlayerAlreadyInLobbyOrGame;
import server.api.exceptions.UserAlreadyExistsException;

/**
 * Provides global API exception handling.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalAPIExceptionManager {
    /**
     * Handles validation errors for API fields annotated with @Valid.
     *
     * @param ex caught exception
     * @return API error
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors, ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiError handleBadArgumentException(IllegalArgumentException ex) {
        return new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    @ExceptionHandler({
        PlayerAlreadyInLobbyOrGame.class,
        UserAlreadyExistsException.class,
        IllegalStateException.class })
    public ApiError handlePlayerAlreadyInLobbyOrGameException(Exception ex) {
        return new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    /**
     * Processes all exception field errors and packs them into an API error.
     *
     * @param fieldErrors list of field errors
     * @param ex caught exception
     * @return API error
     */
    private ApiError processFieldErrors(List<FieldError> fieldErrors, MethodArgumentNotValidException ex) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Validation error");
        for (org.springframework.validation.FieldError fieldError : fieldErrors) {
            error.addError(fieldError.getDefaultMessage());
        }
        return error;
    }
}


