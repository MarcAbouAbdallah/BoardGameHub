package ca.mcgill.ecse321.boardgamehub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.sql.Date;
import java.sql.Time;

@Entity
public class Event {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String location;
    private String description;
    private Date date;
    private Time startTime;
    private Time endTime;
    private int maxParticipants;
    @ManyToOne
    private Player organizer;
    @ManyToOne
    private GameCopy game;

    protected Event() {
    }

    public Event(String name, String location, String description, Date date, Time starTime, Time endTime, int maxParticipants, Player organizer, GameCopy game) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.date = date;
        this.startTime = starTime;
        this.endTime = endTime;
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
    public Time getStartTime() {
        return startTime;
    }
    public Time getEndTime() {
        return endTime;
    }

    
    public void setId(int aId) {
        id = aId;
    }
    public void setName(String aName) {
        name = aName;
    }
    public void setGame(GameCopy aGame) {
        game = aGame;
    }
    public void setOrganizer(Player aOrganizer) {
        organizer = aOrganizer;
    }
    public void setMaxParticipants(int aMaxParticipants) {
        maxParticipants = aMaxParticipants;
    }
    public void setDate(Date aDate) {
        date = aDate;
    }
    public void setDescription(String aDescription) {
        description = aDescription;
    }
    public void setLocation(String aLocation) {
        location = aLocation;
    }
    public void setStartTime(Time aStartTime) {
        startTime = aStartTime;
    }
    public void setEndTime(Time aEndTime) {
        endTime = aEndTime;
    }

    @Override
    public String toString() {
        return super.toString() + "[" +
            "name" + ":" + getName() + "," +
            "location" + ":" + getLocation() + "," +
            "description" + ":" + getDescription() + "," +
            "date" + ":" + (date != null ? date : "null") + "," +
            "startTime" + ":" + (startTime != null ? startTime : "null") + "," +
            "endTime" + ":" + (endTime != null ? endTime : "null") + "," +
            "maxParticipants" + ":" + getMaxParticipants() + "," +
            "organizer" + ":" + (getOrganizer() != null ? getOrganizer().getName() : "null") + "," +
            "game" + ":" + (getGame() != null ? getGame().getGame().getName() : "null") + "]";
    }
}