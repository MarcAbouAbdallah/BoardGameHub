package ca.mcgill.ecse321.boardgamehub.dto;

public class GameUpdateDto {
    private String name;
    private String description;
    private Integer maxPlayers;
    private Integer minPlayers;
    private String photoURL;

    protected GameUpdateDto() {}

    public GameUpdateDto(String name, String description, Integer maxPlayers, Integer minPlayers, String photoURL){
        this.name = name;
        this.description = description;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.photoURL = photoURL;
    }

    public String getPhotoURL() { return photoURL; }
    public void setPhotoURL(String photoURL) { this.photoURL = photoURL; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayerss(Integer maxPlayers) { this.maxPlayers = maxPlayers; }

    public Integer getMinPlayers() { return minPlayers; }
    public void setMinPlayers(Integer minPlayers) { this.minPlayers = minPlayers; }

    public void validate(){
        
        if (name != null && name.isBlank()) {
            throw new IllegalArgumentException("Event name cannot be blank.");
        }

        if (description != null && description.isBlank()) {
            throw new IllegalArgumentException("Game Description cannot be blank.");
        }

        if (maxPlayers != null && maxPlayers <= 0) {
            throw new IllegalArgumentException("Maximum players must be greater than zero.");
        }

        if (minPlayers != null && (minPlayers > maxPlayers) ) {
            throw new IllegalArgumentException("Minimum players cannot exceed maximum players");
        }
    }
}
