package ca.mcgill.ecse321.boardgamehub.response;

import ca.mcgill.ecse321.boardgamehub.model.GameCopy;

public class GameCopyResponse {
    
    private int id;
    private boolean isAvailable;
    private int gameId;
    private String gameName;
    private int ownerId;
    private String ownerName;

    public GameCopyResponse() {}

    public static GameCopyResponse fromGameCopy(GameCopy copy) {
        GameCopyResponse response = new GameCopyResponse();
        response.id = copy.getId();
        response.isAvailable = copy.getIsAvailable();
        response.gameId = copy.getGame().getId();
        response.gameName = copy.getGame().getName();
        response.ownerId = copy.getOwner().getId();
        response.ownerName = copy.getOwner().getName();
        return response;
    }

    public int getId() {
        return id;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public int getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}