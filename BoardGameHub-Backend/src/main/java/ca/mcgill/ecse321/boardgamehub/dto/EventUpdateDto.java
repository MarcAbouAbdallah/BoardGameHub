package ca.mcgill.ecse321.boardgamehub.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;

public class EventUpdateDto {
    private String name;
    private String description;
    private String location;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxParticipants; // Integer instead of int to ensure null values are allowed (field is not updated)
    private Integer gameId;

    protected EventUpdateDto() {}

    public EventUpdateDto(String name, String description, String location, LocalDate date, LocalTime startTime, LocalTime endTime, Integer maxParticipants, Integer game) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxParticipants = maxParticipants;
        this.gameId = game;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public Integer getGame() { return gameId; }
    public void setGame(Integer game) { this.gameId = game; }

    public void validate(){
        
        if (name != null && name.isBlank()) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST,"Event name cannot be blank.");
        }

        if (location != null && location.isBlank()) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST,"Event location cannot be blank.");
        }

        if (maxParticipants != null && maxParticipants <= 0) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST,"Maximum participants must be greater than zero.");
        }

        if (date != null && date.isBefore(LocalDate.now())) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST,"Event date must be in the future.");
        }

        if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST,"Start time must be before end time.");
        }

        if ((startTime == null && endTime != null) || (startTime != null && endTime == null)) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST,"Both start and end time must be provided.");
        }
    }
}

