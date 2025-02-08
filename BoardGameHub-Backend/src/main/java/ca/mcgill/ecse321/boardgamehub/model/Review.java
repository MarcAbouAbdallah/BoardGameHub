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
    private Person person;
    @ManyToOne
    private Game game;

    protected Review() {
    }

    public Review(int rating, String comment, Date date, Person person, Game game) {
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.person = person;
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

    public Person getPerson() {
        return person;
    }

    public Game getGame() {
        return game;
    }
}
