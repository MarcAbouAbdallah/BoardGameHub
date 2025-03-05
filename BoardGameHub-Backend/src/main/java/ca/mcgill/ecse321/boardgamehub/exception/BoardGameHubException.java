package ca.mcgill.ecse321.boardgamehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class BoardGameHubException extends RuntimeException {
    @NonNull
    private HttpStatus status;

    public BoardGameHubException(@NonNull HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    @NonNull
    public HttpStatus getStatus() {
        return status;
    }
}
