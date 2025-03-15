package ca.mcgill.ecse321.boardgamehub.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import ca.mcgill.ecse321.boardgamehub.model.Event;

public class EventResponseDto {
    private int id;
    private String name;
    private String description;
    private String location;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxParticipants;
    private String organizerName;
    private int gameId;
    private String gameName;
    private int participantsCount;

    protected EventResponseDto() {}

    public EventResponseDto(Event event, int participantsCount) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.maxParticipants = event.getMaxParticipants();
        this.date = event.getDate().toLocalDate();
        this.startTime = event.getStartTime().toLocalTime();
        this.endTime = event.getEndTime().toLocalTime();
        this.organizerName = event.getOrganizer().getName();
        this.gameId = event.getGame().getId();
        this.gameName = event.getGame().getGame().getName();
        this.participantsCount = participantsCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public int getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public String getLocation() {
        return location;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }
}


