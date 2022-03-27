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
import server.exceptions.ResourceNotFoundException;

/**
 * Provides global API exception handling.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler {
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

    /**
     * Handle invalid state exception.
     *
     * @param ex the exception.
     * @return the API error.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    public ApiError handleConflictException(IllegalStateException ex) {
        return new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    /**
     * Handle invalid parameters exception.
     *
     * @param ex the exception.
     * @return the API error.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiError handleBadArgumentException(IllegalArgumentException ex) {
        return new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    /**
     * Handles "resource not found" exceptions.
     *
     * @param ex caught exception
     * @return API error
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiError handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
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


