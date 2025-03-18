package ca.mcgill.ecse321.boardgamehub.dto;

import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;

public class GameCopyCreationDto {
    @NotNull(message = "Game cannot be empty.")
    private Game game;

    @NotNull(message = "Owner cannot be empty.")
    private Player owner;

    @AssertTrue(message = "isAvailable must be true")
    private boolean isAvailable;

    protected GameCopyCreationDto() {}

    public GameCopyCreationDto(Game game, Player owner, boolean isAvailable) {
        this.game = game;
        this.owner = owner;
        this.isAvailable = isAvailable;
    }

    //getters

    public boolean getIsAvailable(){
        return isAvailable;
    }

    public Game getGame(){
        return game;
    }

    public Player getOwner(){
        return owner;
    }
}
