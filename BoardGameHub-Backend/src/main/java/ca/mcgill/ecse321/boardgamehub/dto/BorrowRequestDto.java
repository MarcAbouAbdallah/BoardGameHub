package ca.mcgill.ecse321.boardgamehub.dto;

import ca.mcgill.ecse321.boardgamehub.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamehub.model.BorrowStatus;

import java.sql.Date;

public class BorrowRequestDto {
    private int id;
    private int requesterId;
    private int requesteeId;
    private int gameCopyId;
    private String comment;
    private Date startDate;
    private Date endDate;
    private BorrowStatus status;

    public BorrowRequestDto(int id, int requesterId, int requesteeId, int gameCopyId, String comment, Date startDate, Date endDate, BorrowStatus status) {
        this.id = id;
        this.requesterId = requesterId;
        this.requesteeId = requesteeId;
        this.gameCopyId = gameCopyId;
        this.comment = comment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public static BorrowRequestDto fromBorrowRequest(BorrowRequest borrowRequest) {
        return new BorrowRequestDto(
            borrowRequest.getId(),
            borrowRequest.getRequester().getId(),
            borrowRequest.getRequestee().getId(),
            borrowRequest.getGame().getId(),
            borrowRequest.getComment(),
            borrowRequest.getStartDate(),
            borrowRequest.getEndDate(),
            borrowRequest.getStatus()
        );
    }

    public int getId() { return id; }
    public int getRequesterId() { return requesterId; }
    public int getRequesteeId() { return requesteeId; }
    public int getGameCopyId() { return gameCopyId; }
    public String getComment() { return comment; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public BorrowStatus getStatus() { return status; }
}
