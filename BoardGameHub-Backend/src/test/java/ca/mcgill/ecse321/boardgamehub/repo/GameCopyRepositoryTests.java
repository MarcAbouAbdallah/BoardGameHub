package ca.mcgill.ecse321.boardgamehub.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;

@SpringBootTest
public class GameCopyRepositoryTests {

    @Autowired
    private GameCopyRepository gameCopyRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private Game chess;
    private GameCopy chessCopy;
    private Player Sleepy;

    @BeforeEach
    public void setup() {
        chess = new Game("Chess", 2, 2, "A strategic board game");
        chess = gameRepository.save(chess);

        Sleepy = new Player("Sleepy", "IWasSleepy@gmail.com" , "password", true);
        Sleepy = playerRepository.save(Sleepy);
    }

    @AfterEach
    public void clearDatabase() {
        gameCopyRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadGameCopy() {
        chessCopy = new GameCopy(true, chess, Sleepy);
        chessCopy = gameCopyRepository.save(chessCopy);
        
        GameCopy chessCopyFromDb = gameCopyRepository.findById(chessCopy.getId()).orElse(null);

        assertNotNull(chessCopyFromDb);
        assertEquals(chessCopy.getId(), chessCopyFromDb.getId());
        assertEquals(chessCopy.getIsAvailable(), chessCopyFromDb.getIsAvailable());
        assertEquals(chessCopy.getGame().getId(), chessCopyFromDb.getGame().getId());
        assertEquals(chessCopy.getOwner().getId(), chessCopyFromDb.getOwner().getId());
    }

    @Test
    public void testUpdateGameCopy() {
        chessCopy = new GameCopy(true, chess, Sleepy);
        chessCopy = gameCopyRepository.save(chessCopy);
        
        chessCopy.setIsAvailable(false);
        chessCopy = gameCopyRepository.save(chessCopy);

        GameCopy chessCopyFromDb = gameCopyRepository.findGameCopyById(chessCopy.getId());

        assertNotNull(chessCopyFromDb);
        assertEquals(chessCopy.getId(), chessCopyFromDb.getId());
        assertEquals(false, chessCopyFromDb.getIsAvailable());
    }

    @Test
    public void testDeleteGameCopy() {
        chessCopy = new GameCopy(true, chess, Sleepy);
        chessCopy = gameCopyRepository.save(chessCopy);

        GameCopy chessCopyFromDb = gameCopyRepository.findGameCopyById(chessCopy.getId());
        gameCopyRepository.delete(chessCopy);

        chessCopyFromDb = gameCopyRepository.findGameCopyById(chessCopy.getId());

        assertEquals(null, chessCopyFromDb);
    }

    @Test
    public void testFindGameCopyByGame() {
        chessCopy = new GameCopy(true, chess, Sleepy);
        chessCopy = gameCopyRepository.save(chessCopy);

        List<GameCopy> game_list = gameCopyRepository.findByGame(chess);

        assertFalse(game_list.isEmpty());
        assertEquals(1, game_list.size());

        GameCopy chessCopyFromDb = game_list.get(0);

        assertNotNull(chessCopyFromDb);
        assertEquals(chessCopy.getId(), chessCopyFromDb.getId());
        assertEquals(chessCopy.getIsAvailable(), chessCopyFromDb.getIsAvailable());
        assertEquals(chessCopy.getGame().getId(), chessCopyFromDb.getGame().getId());
        assertEquals(chessCopy.getOwner().getId(), chessCopyFromDb.getOwner().getId());
    }
}
