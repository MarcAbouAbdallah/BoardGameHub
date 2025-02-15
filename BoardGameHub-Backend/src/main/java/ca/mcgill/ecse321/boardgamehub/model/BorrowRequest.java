package ca.mcgill.ecse321.boardgamehub.model;

import jakarta.persistence.*;
import java.sql.Date;

@Entity
public class BorrowRequest {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private Player requester;

    @ManyToOne
    private Player requestee;

    @ManyToOne
    private GameCopy game;

    @Enumerated(EnumType.STRING)
    private BorrowStatus status;

    private String comment;
    private Date startDate;
    private Date endDate;

    // Constructor
    protected BorrowRequest() {}

    // Constructor with fields
    public BorrowRequest(Player requester, Player requestee, GameCopy game, String comment, Date startDate, Date endDate) {
        this.requester = requester;
        this.requestee = requestee;
        this.game = game;
        this.comment = comment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = BorrowStatus.PENDING; // Default status
    }

    // Getters
    public int getId() { return id; }

    public Player getRequester() { return requester; }

    public Player getRequestee() { return requestee; }

    public GameCopy getGame() { return game; }

    public String getComment() { return comment; }

    public Date getStartDate() { return startDate; }

    public Date getEndDate() { return endDate; }

    public BorrowStatus getStatus() { return status; }

    // Setters
    public void setRequester(Player requester) { this.requester = requester; }

    public void setRequestee(Player requestee) { this.requestee = requestee; }

    public void setGame(GameCopy game) { this.game = game; }

    public void setComment(String comment) { this.comment = comment; }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public void setStatus(BorrowStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "BorrowRequest{" +
                "id=" + id +
                ", requester=" + (requester != null ? requester.getName() : "null") +
                ", requestee=" + (requestee != null ? requestee.getName() : "null") +
                ", game=" + (game != null ? game.getId() : "null") +
                ", comment='" + comment + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}
