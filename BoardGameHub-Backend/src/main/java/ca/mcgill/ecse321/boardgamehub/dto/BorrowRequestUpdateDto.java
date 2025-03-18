package ca.mcgill.ecse321.boardgamehub.dto;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.BorrowRequest;

public class BorrowRequestUpdateDto {

    private String comment;
    private LocalDate startDate;
    private LocalDate endDate;

    protected BorrowRequestUpdateDto() {}

    public BorrowRequestUpdateDto(String comment, LocalDate startDate, LocalDate endDate) {
        this.comment = comment;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // Validate the updated fields
    public void validate(BorrowRequest existingRequest) {
        if (comment != null && comment.isBlank()) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Comment cannot be blank.");
        }
    
        // Validate updated start date against existing end date or new end date (if updated)
        if (startDate != null) {
            if (startDate.isBefore(LocalDate.now())) {
                throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "New start date must be in the future.");
            }
            if (endDate != null && startDate.isAfter(endDate)) {
                throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "New start date must be before the new end date.");
            }
            if (endDate == null && startDate.isAfter(existingRequest.getEndDate().toLocalDate())) {
                throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Start date must be before the current end date.");
            }
        }

        // Validate updated end date against existing start date
        if (endDate != null && endDate.isBefore(existingRequest.getStartDate().toLocalDate())) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "New end date must be after the current start date.");
        }
        
    }
}
