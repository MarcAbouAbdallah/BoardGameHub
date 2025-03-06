package ca.mcgill.ecse321.boardgamehub.middleware;

import ca.mcgill.ecse321.boardgamehub.model.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerContext {

    private static final ThreadLocal<Player> currentPlayer = new ThreadLocal<>();

    public void setCurrentPlayer(Player player) {
        currentPlayer.set(player);
    }

    public Player getCurrentPlayer() {
        return currentPlayer.get();
    }

    public void clear() {
        currentPlayer.remove();
    }
}