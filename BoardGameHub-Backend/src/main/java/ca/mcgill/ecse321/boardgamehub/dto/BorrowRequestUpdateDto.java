package ca.mcgill.ecse321.boardgamehub.dto;

import java.sql.Date;

public class BorrowRequestUpdateDto {

    private String comment;
    private Date startDate;
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

    /**
     * Validate the update request using the existing BorrowRequest state. Ensuring that only valid modifications are allowed.
     */
    public void validate(BorrowRequest existingRequest) {
        if (comment != null && comment.isBlank()) {
            throw new IllegalArgumentException("Comment cannot be blank.");
        }

        if (startDate != null && endDate != null && !startDate.before(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }

        if (endDate != null && endDate.before(existingRequest.getStartDate())) {
            throw new IllegalArgumentException("End date must be after the existing start date.");
        }
    }
}
