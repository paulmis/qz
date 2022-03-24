package commons.entities.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Holds information about API errors.
 */
@Data
public class ApiError {
    /**
     * Time the error occurred.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * HTTP status code.
     */
    private int status;

    /**
     * High-level description of the error.
     */
    private String description;

    /**
     * List of human-readable exception messages.
     */
    private List<String> errors = new ArrayList<>();

    public ApiError() {
    }

    /**
     * Initializes the ApiError.
     *
     * @param status the HTTP status to return
     * @param description high-level description of the error
     */
    public ApiError(int status, String description) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.description = description;
    }

    /**
     * Adds a new error message to the list of errors.
     *
     * @param error the error message
     */
    public void addError(String error) {
        errors.add(error);
    }
}