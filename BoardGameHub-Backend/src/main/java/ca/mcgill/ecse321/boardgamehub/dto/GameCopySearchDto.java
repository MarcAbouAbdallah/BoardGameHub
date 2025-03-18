package ca.mcgill.ecse321.boardgamehub.dto;

public class GameCopySearchDto {
    
    private int playerId;
    
    public GameCopySearchDto() {
    }
    
    public GameCopySearchDto(int playerId) {
        this.playerId = playerId;
    }
    
    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}