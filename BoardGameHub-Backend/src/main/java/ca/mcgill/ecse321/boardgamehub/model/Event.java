package ca.mcgill.ecse321.boardgamehub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.sql.Date;

@Entity
public class Event {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String location;
    private String description;
    private Date date;
    private int maxParticipants;
    @ManyToOne
    private Player organizer;
    @ManyToOne
    private GameCopy game;

    protected Event() {
    }

    public Event(String name, String location, String description, Date date, int maxParticipants, Player organizer, GameCopy game) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.date = date;
        this.maxParticipants = maxParticipants;
        this.organizer = organizer;
        this.game = game;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public GameCopy getGame() {
        return game;
    }
    public Player getOrganizer() {
        return organizer;
    }
    public int getMaxParticipants() {
        return maxParticipants;
    }
    public Date getDate() {
        return date;
    }
    public String getDescription() {
        return description;
    }
    public String getLocation() {
        return location;
    }

    
    public boolean setId(int aId) {
        id = aId;
        return true;
    }
    public boolean setName(String aName) {
        name = aName;
        return true;
    }
    public boolean setGame(GameCopy aGame) {
        game = aGame;
        return true;
    }
    public boolean setOrganizer(Player aOrganizer) {
        organizer = aOrganizer;
        return true;
    }
    public boolean setMaxParticipants(int aMaxParticipants) {
        maxParticipants = aMaxParticipants;
        return true;
    }
    public boolean setDate(Date aDate) {
        date = aDate;
        return true;
    }
    public boolean setDescription(String aDescription) {
        description = aDescription;
        return true;
    }
    public boolean setLocation(String aLocation) {
        location = aLocation;
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + "[" +
            "name" + ":" + getName() + "," +
            "location" + ":" + getLocation() + "," +
            "description" + ":" + getDescription() + "," +
            "date" + ":" + (date != null ? date : "null") + "," +
            "maxParticipants" + ":" + getMaxParticipants() + "," +
            "organizer" + ":" + (getOrganizer() != null ? getOrganizer().getName() : "null") + "," +
            "game" + ":" + (getGame() != null ? getGame().getGame().getName() : "null") + "]";

        //The code below seemed useless. It is an artifact from a previous year tutorial's version
        //of the toString method. I don't think it makes sense to use it in our case
        // + System.getProperties().getProperty("line.separator")
        // +
        // "  " + "date" + "="
        // + (getDate() != null 
        //     ? (!getDate().equals(this) 
        //         ? getDate().toString().replaceAll("  ", "    ") 
        //         : "this") 
        //     : "null");
    }

}