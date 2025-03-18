package ca.mcgill.ecse321.boardgamehub.dto;

import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;

public class GameCopyUpdateDto {
    private Game game;
    private Player owner;
    private Boolean isAvailable;
    
    protected GameCopyUpdateDto() {}

    public GameCopyUpdateDto(Game game, Player owner, boolean isAvailable) {
        this.game = game;
        this.owner = owner;
        this.isAvailable = isAvailable;
    }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public Player getOwner() { return owner; }
    public void setOwner(Player owner) { this.owner = owner; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setMaxParticipants(Boolean isAvailable) { this.isAvailable = isAvailable; }
}
