package ca.mcgill.ecse321.boardgamehub.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
        chess = new Game("Chess", 2, 2, "A strategic board game");
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
        chess = new Game("Chess", 2, 2, "A strategic board game");
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
        chess = new Game("Chess", 2, 2, "A strategic board game");
        chess = gameRepository.save(chess);

        Game chessFromDb = gameRepository.findGameById(chess.getId());
        gameRepository.delete(chess);

        chessFromDb = gameRepository.findGameById(chess.getId());

        assertEquals(null, chessFromDb);
    }
}
