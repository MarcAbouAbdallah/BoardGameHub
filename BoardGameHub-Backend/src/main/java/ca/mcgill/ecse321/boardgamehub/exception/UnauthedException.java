package ca.mcgill.ecse321.boardgamehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthedException extends RuntimeException {
    public UnauthedException(String message) {
        super(message);
    }
}