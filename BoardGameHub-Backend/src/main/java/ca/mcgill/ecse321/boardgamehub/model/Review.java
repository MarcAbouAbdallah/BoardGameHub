package ca.mcgill.ecse321.boardgamehub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.sql.Date;

@Entity
public class Review {
    @Id
    @GeneratedValue
    private int id;
    private int rating;
    private String comment;
    private Date date;
    @ManyToOne
    private Player reviewer;
    @ManyToOne
    private Game game;

    protected Review() {
    }

    public Review(int rating, String comment, Date date, Player reviewer, Game game) {
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.reviewer = reviewer;
        this.game = game;
    }

    public int getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Date getDate() {
        return date;
    }

    public Player getReviewer() {
        return reviewer;
    }

    public Game getGame() {
        return game;
    }

    public boolean setId(int aId) {
        id = aId;
        return true;
    }
    public boolean setComment(String aComment) {
        comment = aComment;
        return true;
    }
    public boolean setDate(Date aDate) {
        date = aDate;
        return true;
    }
    public boolean setReviewer(Player aReviewer) {
        reviewer = aReviewer;
        return true;
    }
    public boolean setGame(Game aGame) {
        game = aGame;
        return true;
    }
    public boolean setRating(int aRating) {
        rating = aRating;
        return true;
    } 

    @Override
    public String toString() {
        return super.toString() + "[" +
            "id" + ":" + getId() + "," +
            "rating" + ":" + getRating() + "," +
            "comment" + ":" + getComment() + "," +
            "reviewer" + ":" + (getReviewer() != null ? getReviewer().getName() : "null") + "," +
            "game" + ":" + (getGame() != null ? getGame().getName() : "null") + "]";
            
            //The code below seemed useless. It is an artifact from a previous year tutorial's version
            //of the toString method. I don't think it makes sense to use it in our case
            // + System.getProperties().getProperty("line.separator")
            // +
            // "  " + "date" + "="
            // + (getDate() != null ? !getDate().equals(this) ? getDate().toString().replaceAll("  ", "    ") : "this"
            //         : "null");
    }
    
}
