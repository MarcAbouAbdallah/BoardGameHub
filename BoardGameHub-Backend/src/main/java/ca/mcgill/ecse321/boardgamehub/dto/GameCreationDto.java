package ca.mcgill.ecse321.boardgamehub.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class GameCreationDto {
    @NotBlank(message = "Game name cannot be empty.")
    private String name;

    @NotBlank(message = "Game description cannot be empty.")
    private String description;

    @Positive(message = "MaxPlayers must be a positive number")
    private int maxPlayers;

    @Positive(message = "MinPlayers must be a positive number.")
    private int minPlayers;

    @URL(message = "Must be valid URL")
    private String photoURL;

    protected GameCreationDto() {}

    public GameCreationDto(String name, String description, int maxPlayers, int minPlayers, String photoURL) {
        this.name = name;
        this.description = description;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.photoURL = photoURL;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getName(){
        return name;
    }

    public int getMaxPlayers(){
        return maxPlayers;
    }

    public int getMinPlayers(){
        return minPlayers;
    }

    public String getDescription(){
        return description;
    }
}
