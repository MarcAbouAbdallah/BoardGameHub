package ca.mcgill.ecse321.boardgamehub.dto;

import java.time.LocalDate;
import ca.mcgill.ecse321.boardgamehub.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamehub.model.BorrowStatus;

public class BorrowRequestResponseDto {

    private int id;
    private int requesterId;
    private String requesterName;
    private int requesteeId;
    private String requesteeName;
    private int gameCopyId;
    private String gameTitle;
    private String comment;
    private LocalDate startDate;
    private LocalDate endDate;
    private BorrowStatus status; 

    protected BorrowRequestResponseDto() {}

    public BorrowRequestResponseDto(BorrowRequest borrowRequest) {
        this.id = borrowRequest.getId();
        this.requesterId = borrowRequest.getRequester().getId();
        this.requesterName = borrowRequest.getRequester().getName();
        this.requesteeId = borrowRequest.getRequestee().getId();
        this.requesteeName = borrowRequest.getRequestee().getName();
        this.gameCopyId = borrowRequest.getGame().getId();
        this.gameTitle = borrowRequest.getGame().getGame().getName();
        this.comment = borrowRequest.getComment();
        this.startDate = borrowRequest.getStartDate().toLocalDate();
        this.endDate = borrowRequest.getEndDate().toLocalDate();
        this.status = borrowRequest.getStatus(); // Keep enum type
    }

    public int getId() {
        return id;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public int getRequesteeId() {
        return requesteeId;
    }

    public String getRequesteeName() {
        return requesteeName;
    }

    public int getGameCopyId() {
        return gameCopyId;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public String getComment() {
        return comment;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BorrowStatus getStatus() {
        return status;
    }
}
