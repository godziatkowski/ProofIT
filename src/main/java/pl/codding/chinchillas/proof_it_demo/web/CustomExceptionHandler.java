package pl.codding.chinchillas.proof_it_demo.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ValidationError> processValidationError(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                 .getAllErrors()
                 .stream()
                 .map(error -> new ValidationError(((FieldError) error).getField(), error.getDefaultMessage()))
                 .toList();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ValidationError> processValidationError(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                 .stream()
                 .map(error -> new ValidationError(error.getPropertyPath().toString(), error.getMessage()))
                 .toList();
    }

    record ValidationError(String field, String message) {}

}
