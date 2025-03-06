package ca.mcgill.ecse321.boardgamehub.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Future;

public class EventCreationdto {
    @NotBlank(message = "Event name cannot be empty.")
    private String eventName;

    @NotBlank(message = "Event description cannot be empty.")
    private String eventDescription;

    @NotBlank(message = "Event location cannot be empty.")
    private String eventLocation;

    @Positive(message = "Maximum participants must be a positive number.")
    private int maxParticipants;

    @Future(message = "Event date must be in the future.")
    @NotNull(message = "Event date cannot be empty.")
    private LocalDate eventDate;

    @NotNull(message = "Board game Id is required.")
    private int gameId;

    @NotNull(message = "Oganizer Id is required.")
    private int organizerId;

    protected EventCreationdto() {}

    public EventCreationdto(String name, String location, String description, LocalDate date, int maxParticipants, int organizer, int game) {
        this.eventName = name;
        this.eventLocation = location;
        this.eventDescription = description;
        this.eventDate = date;
        this.maxParticipants = maxParticipants;
        this.organizerId = organizer;
        this.gameId = game;
    }

    public String getName() {
        return eventName;
    }

    public void setName(String name) {
        this.eventName = name;
    }

    public String getLocation() {
        return eventLocation;
    }

    public void setLocation(String location) {
        this.eventLocation = location;
    }

    public String getDescription() {
        return eventDescription;
    }

    public void setDescription(String description) {
        this.eventDescription = description;
    }

    public LocalDate getDate() {
        return eventDate;
    }

    public void setDate(LocalDate date) {
        this.eventDate = date;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getOrganizer() {
        return organizerId;
    }

    public void setOrganizer(int organizer) {
        this.organizerId = organizer;
    }

    public int getGame() {
        return gameId;
    }

    public void setGame(int game) {
        this.gameId = game;
    }

}
