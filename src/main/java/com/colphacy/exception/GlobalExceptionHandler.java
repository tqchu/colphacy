package com.colphacy.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler is a class that extends ResponseEntityExceptionHandler to provide centralized exception handling across all {@code @RequestMapping} methods through {@code @ExceptionHandler} methods.
 * <p>
 * This class is annotated with {@code @ControllerAdvice} which makes it applicable to all controllers in the application. It allows for global exception handling, common model attribute population, and so on, applicable to all {@code @RequestMapping} methods.
 * <p>
 * The main responsibility of this class is to handle exceptions that are not caught by the controller classes. It does this by overriding the methods from ResponseEntityExceptionHandler and adding new methods with the {@code @ExceptionHandler} annotation.
 * <p>
 * Each method in this class should be annotated with {@code @ExceptionHandler} and should have a single parameter: an exception type to be handled by the method. The methods can have flexible signatures, they may return a {@code ResponseEntity} or a {@code ModelAndView}, or they can be void.
 * <p>
 * Here is an example of how to define an exception handler method:
 * <pre>
 * {@code
 * \@ExceptionHandler(YourException.class)
 * public ResponseEntity<Object> handleYourException(YourException ex) {
 *     // Your handling logic goes here
 * }
 * }
 * </pre>
 * <p>
 * Remember to replace {@code YourException} with the type of exception you want to handle, and to implement your own handling logic in the method body.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    static final String DEFAULT_ERROR_NAME = "error";

    /**
     * Creates a map containing the error message of the given exception.
     *
     * @param errorName the key to use for the error message in the map
     * @param ex        the exception to get the error message from
     * @return a map containing the error message of the given exception
     */
    private Map<String, String> getError(String errorName, Exception ex) {
        Map<String, String> errors = new HashMap<>();
        String message = ex.getMessage();
        errors.put(errorName, message);
        return errors;
    }

    private Map<String, String> getError(String errorName, String message) {
        Map<String, String> errors = new HashMap<>();
        errors.put(errorName, message);
        return errors;
    }

    /**
     * This method handles ConstraintViolationException by returning a map of errors.
     * ConstraintViolationException will be thrown when bean validation fails for request params or path variables.
     * The key of the map is the property path and the value is the error message.
     *
     * @return a ResponseEntity containing a map of errors
     */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        // entry of field name and error message
                        violation -> violation.getPropertyPath().toString().split("\\.")[1],
                        ConstraintViolation::getMessage
                ));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method overrides the handleTypeMismatch method from ResponseEntityExceptionHandler to provide custom handling for TypeMismatchException.
     * It returns a user-readable error message in the response.
     *
     * @return a ResponseEntity containing an error message
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        MethodArgumentTypeMismatchException exception = (MethodArgumentTypeMismatchException) ex;
        Map<String, String> error = new HashMap<>();
        String fieldName = exception.getName();
        Class<?> requiredType = exception.getRequiredType();
        if (requiredType != null) {
            String message = "Sai định dạng";
            error.put(fieldName, message);
        } else {
            String message = "wrong type";
            error.put(fieldName, message);
        }
        return new ResponseEntity<>(error, headers, status);
    }

    /**
     * This method overrides the handleMissingServletRequestParameter method from ResponseEntityExceptionHandler to provide custom handling for MissingServletRequestParameterException.
     * It returns the error message from the exception in the response.
     *
     * @return a ResponseEntity containing an error message
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String fieldName = ex.getParameterName();
        return new ResponseEntity<>(getError(fieldName, ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RecordNotFoundException.class})
    @ResponseBody
    public ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex) {
        return new ResponseEntity<>(getError(DEFAULT_ERROR_NAME, ex), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles MethodArgumentNotValidException by returning a ResponseEntity with a map of field errors and HttpStatus.BAD_REQUEST.
     * MethodArgumentNotValidException will be thrown when @RequestBody data validation fails.
     *
     * @return a ResponseEntity with a map of field errors and HttpStatus.BAD_REQUEST
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpMessageNotReadableException by returning a ResponseEntity with a map of field errors and HttpStatus.BAD_REQUEST.
     * HttpMessageNotReadableException will be thrown when @RequestBody data cannot be parsed from json.
     *
     * @return a ResponseEntity with an error message and HttpStatus.BAD_REQUEST
     */
    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            String fieldName = invalidFormatException.getPath().get(0).getFieldName();
            String message = "Sai định dạng";
            error.put(fieldName, message);
        } else {
            String message = "Invalid request body format.";
            error.put(DEFAULT_ERROR_NAME, message);
        }
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other internal server exceptions
     *
     * @return a ResponseEntity with an error message and a status indicating internal server error
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        String traceId = String.valueOf(System.currentTimeMillis());
        LOGGER.error("An error occurred: ", ex);
        LOGGER.error("TraceId: {}", traceId);
        Map<String, String> error = new HashMap<>();
        error.put(DEFAULT_ERROR_NAME, "Có lỗi xảy ra");
        error.put("traceId", traceId);
        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles NoHandlerFoundException by returning a ResponseEntity with an error message and HttpStatus.NOT_FOUND.
     * NoHandlerFoundException is thrown when the server cannot find a handler for a request, typically indicating that the requested resource does not exist.
     *
     * @return a ResponseEntity with an error message and HttpStatus.NOT_FOUND
     */
    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put(DEFAULT_ERROR_NAME, "Không tìm thấy trang");
        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles InvalidFieldsException by returning a ResponseEntity with a map of field errors and HttpStatus.BAD_REQUEST.
     * InvalidFieldsException is thrown when the server finds invalid fields in a request, typically indicating that the request does not meet certain validation criteria.
     *
     * @return a ResponseEntity with a map of field errors and HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler(InvalidFieldsException.class)
    public ResponseEntity<Object> handleInvalidFieldsException(InvalidFieldsException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles AccessDeniedException by returning a ResponseEntity with an error message and HttpStatus.FORBIDDEN.
     * AccessDeniedException is thrown when the server finds that the user does not have the necessary permissions to access a certain resource.
     *
     * @return a ResponseEntity with an error message and HttpStatus.FORBIDDEN
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(getError(DEFAULT_ERROR_NAME, "Truy cập bị từ chối"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>(getError(DEFAULT_ERROR_NAME, "File không thể lớn hơn 10MB"), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (Objects.requireNonNull(error.getCodes())[error.getCodes().length - 1].equals("typeMismatch")) {
                errors.put(((FieldError) error).getField(), "Giá trị không hợp lệ");
            } else {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            }
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ReceiverDeletingException.class})
    @ResponseBody
    public ResponseEntity<Object> handleReceiverDeletingException(ReceiverDeletingException ex) {
        return new ResponseEntity<>(getError(DEFAULT_ERROR_NAME, ex), HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler({ConversionFailedException.class})



}


