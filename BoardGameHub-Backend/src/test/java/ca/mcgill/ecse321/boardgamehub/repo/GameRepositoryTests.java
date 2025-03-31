package ca.mcgill.ecse321.boardgamehub.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamehub.model.Game;

@SpringBootTest
public class GameRepositoryTests {

    @Autowired
    private GameRepository gameRepository;

    private Game chess;

    @AfterEach
    public void clearDatabase() {
        gameRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadGame() {
        chess = new Game("Chess", 2, 2, "A strategic board game", "https://images.unsplash.com/photo-1619163413327-546fdb903195?q=80&w=1287&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
        chess = gameRepository.save(chess);
        
        Game chessFromDb = gameRepository.findGameById(chess.getId());

        assertNotNull(chessFromDb);
        assertEquals(chess.getId(), chessFromDb.getId());
        assertEquals(chess.getName(), chessFromDb.getName());
        assertEquals(chess.getMaxPlayers(), chessFromDb.getMaxPlayers());
        assertEquals(chess.getMinPlayers(), chessFromDb.getMinPlayers());
        assertEquals(chess.getDescription(), chessFromDb.getDescription());
    }

    @Test
    public void testUpdateGame() {
        chess = new Game("Chess", 2, 2, "A strategic board game", "https://images.unsplash.com/photo-1619163413327-546fdb903195?q=80&w=1287&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
        chess = gameRepository.save(chess);
        
        chess.setName("Chess 2.0");
        chess.setDescription("A strategic board game for 2 players");
        chess = gameRepository.save(chess);

        Game chessFromDb = gameRepository.findGameById(chess.getId());

        assertNotNull(chessFromDb);
        assertEquals(chess.getId(), chessFromDb.getId());
        assertEquals("Chess 2.0", chessFromDb.getName());
        assertEquals("A strategic board game for 2 players", chessFromDb.getDescription());
    }

    @Test
    public void testDeleteGame() {
        chess = new Game("Chess", 2, 2, "A strategic board game", "https://images.unsplash.com/photo-1619163413327-546fdb903195?q=80&w=1287&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
        chess = gameRepository.save(chess);

        Game chessFromDb = gameRepository.findGameById(chess.getId());
        gameRepository.delete(chess);

        chessFromDb = gameRepository.findGameById(chess.getId());

        assertEquals(null, chessFromDb);
    }
}
