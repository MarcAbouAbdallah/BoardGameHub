package ca.mcgill.ecse321.boardgamehub.dto;

import ca.mcgill.ecse321.boardgamehub.model.GameCopy;

public class GameCopyResponseDto {

    private int gameCopyId;
    private boolean isAvailable;
    private String gameName;
    private int gameId;
    private int ownerId;
    private String ownerName;

    protected GameCopyResponseDto() {
    }

    public GameCopyResponseDto(GameCopy copy, boolean isAvailable) {
        this.gameCopyId = copy.getId();
        this.isAvailable = isAvailable;
        this.gameName = copy.getGame().getName();
        this.gameId = copy.getGame().getId();
        this.ownerId = copy.getOwner().getId();
        this.ownerName = copy.getOwner().getName();
    }

    public int getGameCopyId() {
        return gameCopyId;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public String getGameName() {
        return gameName;
    }

    public int getGameId() {
        return gameId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setGameCopyId(int gameCopyId) {
        this.gameCopyId = gameCopyId;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    //This function is not in use due to the new way we check for availability.

    // public static GameCopyResponseDto fromGameCopy(GameCopy copy) {
    //     GameCopyResponseDto dto = new GameCopyResponseDto();
    //     dto.setGameCopyId(copy.getId());
    //     dto.setIsAvailable(copy.getIsAvailable());
    //     dto.setGameId(copy.getGame().getId());
    //     dto.setGameName(copy.getGame().getName());
    //     dto.setOwnerId(copy.getOwner().getId());
    //     dto.setOwnerName(copy.getOwner().getName());
    //     return dto;
    // }
}