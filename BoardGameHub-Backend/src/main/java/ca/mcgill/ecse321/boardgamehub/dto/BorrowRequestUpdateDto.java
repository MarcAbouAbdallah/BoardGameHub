package ca.mcgill.ecse321.boardgamehub.dto;

import java.sql.Date;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;

public class BorrowRequestUpdateDto {

    private String comment;

    @FutureOrPresent(message = "Start date must be today or in the future.")
    private Date startDate;

    @Future(message = "End date must be in the future.")
    private Date endDate;

    protected BorrowRequestUpdateDto() {}

    public BorrowRequestUpdateDto(String comment, Date startDate, Date endDate) {
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void validate() {
        if (comment != null && comment.isBlank()) {
            throw new IllegalArgumentException("Comment cannot be blank.");
        }

        if (startDate != null && endDate != null && !startDate.before(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
    }
}
