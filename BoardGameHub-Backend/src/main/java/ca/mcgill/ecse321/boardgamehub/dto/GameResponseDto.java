package ca.mcgill.ecse321.boardgamehub.dto;

import ca.mcgill.ecse321.boardgamehub.model.Game;
public class GameResponseDto {
    private int id;
    private String name;
    private String description;
    private int maxPlayers;
    private int minPlayers;

    protected GameResponseDto() {}

    public GameResponseDto(Game game){
        this.id = game.getId();
        this.name = game.getName();
        this.description = game.getDescription();
        this.maxPlayers = game.getMaxPlayers();
        this.minPlayers = game.getMinPlayers();
    }

    public int getId(){
        return id;
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
