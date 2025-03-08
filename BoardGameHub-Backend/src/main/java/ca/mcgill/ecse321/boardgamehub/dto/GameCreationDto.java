package ca.mcgill.ecse321.boardgamehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class GameCreationDto {
    @NotBlank(message = "Game name cannot be empty.")
    private String gameName;

    @NotBlank(message = "Game description cannot be empty.")
    private String gameDescription;

    @Positive(message = "MaxPlayers must be a positive number")
    private int maxPlayers;

    @Positive(message = "MinPlayers must be a positive number.")
    private int minPlayers;

    protected GameCreationDto() {}

    public GameCreationDto(String name, String description, int maxPlayers, int minPlayers) {
        this.gameName = name;
        this.gameDescription = description;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
    }

    public String getName(){
        return gameName;
    }

    public int getMaxPlayers(){
        return maxPlayers;
    }

    public int getMinPlayers(){
        return minPlayers;
    }

    public String getDescription(){
        return gameDescription;
    }
}
