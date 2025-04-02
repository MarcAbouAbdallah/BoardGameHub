package ca.mcgill.ecse321.boardgamehub.exception;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;

import ca.mcgill.ecse321.boardgamehub.dto.ErrorDto;

@ControllerAdvice
public class BoardGameHubExceptionHandler {
    @ExceptionHandler(BoardGameHubException.class)
    public ResponseEntity<ErrorDto> handleEventRegistrationException(BoardGameHubException e) {
        return new ResponseEntity<ErrorDto>(new ErrorDto(e.getMessage()), e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ArrayList<String> errorMessages = new ArrayList<String>();
        for (ObjectError err : ex.getAllErrors()) {
            errorMessages.add(err.getDefaultMessage());
        }
        ErrorDto responseBody = new ErrorDto(errorMessages);
        return new ResponseEntity<ErrorDto>(responseBody, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ErrorDto(errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        ErrorDto responseBody = new ErrorDto(ex.getMessage().toString().split(":")[1].trim());
        return new ResponseEntity<ErrorDto>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
