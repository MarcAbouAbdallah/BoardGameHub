package ca.mcgill.ecse321.boardgamehub.dto;

import java.sql.Date;
import ca.mcgill.ecse321.boardgamehub.model.BorrowStatus;

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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }

    public int getRequesteeId() { return requesteeId; }
    public void setRequesteeId(int requesteeId) { this.requesteeId = requesteeId; }

    public int getGameCopyId() { return gameCopyId; }
    public void setGameCopyId(int gameCopyId) { this.gameCopyId = gameCopyId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public BorrowStatus getStatus() { return status; }
    public void setStatus(BorrowStatus status) { this.status = status; }

    public static BorrowRequestDto fromEntity(ca.mcgill.ecse321.boardgamehub.model.BorrowRequest borrowRequest) {
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
}
