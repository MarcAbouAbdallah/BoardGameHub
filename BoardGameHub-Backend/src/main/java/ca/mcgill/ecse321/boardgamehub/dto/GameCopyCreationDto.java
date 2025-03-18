package ca.mcgill.ecse321.boardgamehub.dto;

public class GameCopyCreationDto {
    
    private int playerId;
    private int gameId;
    
    public GameCopyCreationDto() {
    }
    
    public GameCopyCreationDto(int playerId, int gameId) {
        this.playerId = playerId;
        this.gameId = gameId;
    }
    
    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    public int getGameId() {
        return gameId;
    }
    
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
