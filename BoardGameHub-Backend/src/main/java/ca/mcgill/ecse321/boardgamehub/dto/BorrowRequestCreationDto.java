package ca.mcgill.ecse321.boardgamehub.dto;

import java.sql.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

public class BorrowRequestCreationDto {

    @NotNull(message = "Requester ID is required.")
    @Positive(message = "Requester ID must be a positive number.")
    private int requesterId;

    @NotNull(message = "Requestee ID is required.")
    @Positive(message = "Requestee ID must be a positive number.")
    private int requesteeId;

    @NotNull(message = "Game Copy ID is required.")
    @Positive(message = "Game Copy ID must be a positive number.")
    private int gameCopyId;

    @NotBlank(message = "Comment cannot be empty.")
    private String comment;

    @NotNull(message = "Start date is required.")
    @FutureOrPresent(message = "Start date must be today or in the future.")
    private Date startDate;

    @NotNull(message = "End date is required.")
    @Future(message = "End date must be in the future.")
    private Date endDate;

    protected BorrowRequestCreationDto() {}

    public BorrowRequestCreationDto(int requesterId, int requesteeId, int gameCopyId, String comment, Date startDate, Date endDate) {
        this.requesterId = requesterId;
        this.requesteeId = requesteeId;
        this.gameCopyId = gameCopyId;
        this.comment = comment;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public int getRequesteeId() {
        return requesteeId;
    }

    public void setRequesteeId(int requesteeId) {
        this.requesteeId = requesteeId;
    }

    public int getGameCopyId() {
        return gameCopyId;
    }

    public void setGameCopyId(int gameCopyId) {
        this.gameCopyId = gameCopyId;
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
}
