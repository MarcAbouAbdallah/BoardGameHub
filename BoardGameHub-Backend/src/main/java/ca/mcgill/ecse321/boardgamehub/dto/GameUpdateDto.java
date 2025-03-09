package ca.mcgill.ecse321.boardgamehub.dto;

public class GameUpdateDto {
    private String name;
    private String description;
    private Integer maxPlayers;
    private Integer minPlayers;

    protected GameUpdateDto() {}

    public GameUpdateDto(String name, String description, Integer maxPlayers, Integer minPlayers){
        this.name = name;
        this.description = description;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMaxPlayers() { return maxPlayers; }
    public void setMaxParticipants(Integer maxPlayers) { this.maxPlayers = maxPlayers; }

    public Integer getMinPlayers() { return minPlayers; }
    public void setMinParticipants(Integer minPlayers) { this.minPlayers = minPlayers; }
}
