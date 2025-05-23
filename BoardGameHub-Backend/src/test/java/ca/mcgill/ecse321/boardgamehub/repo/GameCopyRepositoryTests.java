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
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameCopyRepository gameCopyRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    private Game chess;
    private GameCopy chessCopy;
    private Player Sleepy;

    @AfterEach
    public void clearData() {
        reviewRepository.deleteAll();
        registrationRepository.deleteAll();
        eventRepository.deleteAll();
        borrowRequestRepository.deleteAll();
        gameCopyRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        clearData();
        chess = new Game("Chess",
                        2,
                        2,
                        "A strategic board game",
                        "https://images.unsplash.com/photo-1619163413327-546fdb903195?q=80&w=1287&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
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
        chessCopy = new GameCopy(chess, Sleepy);
        chessCopy = gameCopyRepository.save(chessCopy);
        
        GameCopy chessCopyFromDb = gameCopyRepository.findById(chessCopy.getId()).orElse(null);

        assertNotNull(chessCopyFromDb);
        assertEquals(chessCopy.getId(), chessCopyFromDb.getId());
        //assertEquals(chessCopy.getIsAvailable(), chessCopyFromDb.getIsAvailable());
        assertEquals(chessCopy.getGame().getId(), chessCopyFromDb.getGame().getId());
        assertEquals(chessCopy.getOwner().getId(), chessCopyFromDb.getOwner().getId());
    }

    @Test
    public void testUpdateGameCopy() {
        chessCopy = new GameCopy(chess, Sleepy);
        chessCopy = gameCopyRepository.save(chessCopy);
        
        //chessCopy.setIsAvailable(false);
        chessCopy = gameCopyRepository.save(chessCopy);

        GameCopy chessCopyFromDb = gameCopyRepository.findGameCopyById(chessCopy.getId());

        assertNotNull(chessCopyFromDb);
        assertEquals(chessCopy.getId(), chessCopyFromDb.getId());
        //assertEquals(false, chessCopyFromDb.getIsAvailable());
    }

    @Test
    public void testDeleteGameCopy() {
        chessCopy = new GameCopy(chess, Sleepy);
        chessCopy = gameCopyRepository.save(chessCopy);

        GameCopy chessCopyFromDb = gameCopyRepository.findGameCopyById(chessCopy.getId());
        gameCopyRepository.delete(chessCopy);

        chessCopyFromDb = gameCopyRepository.findGameCopyById(chessCopy.getId());

        assertEquals(null, chessCopyFromDb);
    }

    @Test
    public void testFindGameCopyByGame() {
        chessCopy = new GameCopy(chess, Sleepy);
        chessCopy = gameCopyRepository.save(chessCopy);

        List<GameCopy> game_list = gameCopyRepository.findByGame(chess);

        assertFalse(game_list.isEmpty());
        assertEquals(1, game_list.size());

        GameCopy chessCopyFromDb = game_list.get(0);

        assertNotNull(chessCopyFromDb);
        assertEquals(chessCopy.getId(), chessCopyFromDb.getId());
        //assertEquals(chessCopy.getIsAvailable(), chessCopyFromDb.getIsAvailable());
        assertEquals(chessCopy.getGame().getId(), chessCopyFromDb.getGame().getId());
        assertEquals(chessCopy.getOwner().getId(), chessCopyFromDb.getOwner().getId());
    }

    @Test
    public void testFindGameCopyByOwner() {
        chessCopy = new GameCopy(chess, Sleepy);
        chessCopy = gameCopyRepository.save(chessCopy);

        List<GameCopy> game_list = gameCopyRepository.findByOwner(Sleepy);

        assertFalse(game_list.isEmpty());
        assertEquals(1, game_list.size());

        GameCopy chessCopyFromDb = game_list.get(0);

        assertNotNull(chessCopyFromDb);
        assertEquals(chessCopy.getId(), chessCopyFromDb.getId());
        //assertEquals(chessCopy.getIsAvailable(), chessCopyFromDb.getIsAvailable());
        assertEquals(chessCopy.getGame().getId(), chessCopyFromDb.getGame().getId());
        assertEquals(chessCopy.getOwner().getId(), chessCopyFromDb.getOwner().getId());
    }
}
