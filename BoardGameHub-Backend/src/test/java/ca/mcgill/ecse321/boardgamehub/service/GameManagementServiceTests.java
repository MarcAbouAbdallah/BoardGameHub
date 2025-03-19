package ca.mcgill.ecse321.boardgamehub.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamehub.dto.GameCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameUpdateDto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;


@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class GameManagementServiceTests {
    @Mock
    private GameRepository gameRepo;
    
    @Mock
    private GameCopyRepository gameCopyRepo;

    @InjectMocks
    private GameManagementService gameManagementService;

    private static final int VALID_GAME_ID = 1;
    private static final int INVALID_GAME_ID = 99;

    private Game validGame;
    private GameCopy validGameCopy;
    private Player validPlayer;

    @BeforeEach
    void setup() {
        validGame = new Game("Chess", 2, 2, "A strategy game");
        validGame.setId(VALID_GAME_ID);
        
        validPlayer = new Player("Alice", "alice@email.com", "securePass", false);
        
        validGameCopy = new GameCopy(true, validGame, validPlayer);
    }

    @Test
    void testCreateGame_Success() {
        GameCreationDto dto = new GameCreationDto("Catan", "A strategy game", 4, 3);
        when(gameRepo.save(any(Game.class))).thenAnswer(invocation -> {
            Game game = invocation.getArgument(0);
            game.setId(VALID_GAME_ID);
            return game;
        });

        Game createdGame = gameManagementService.createGame(dto);

        assertNotNull(createdGame);
        assertEquals("Catan", createdGame.getName());
        assertEquals("A strategy game", createdGame.getDescription());
        assertEquals(4, createdGame.getMaxPlayers());
        assertEquals(3, createdGame.getMinPlayers());
    }

    @Test
    void testCreateGame_Failure() {
        GameCreationDto dto = new GameCreationDto("Chess", "A strategy game", 3, 5);
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            gameManagementService.createGame(dto);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("Min players cannot exceed max players.", exception.getMessage());
    }


    @Test
    void testDeleteGame_Success() {
        when(gameRepo.findGameById(VALID_GAME_ID)).thenReturn(validGame);
        when(gameCopyRepo.findByGame(validGame)).thenReturn(List.of(validGameCopy));

        assertDoesNotThrow(() -> gameManagementService.deleteGame(VALID_GAME_ID));
        verify(gameRepo, times(1)).delete(validGame);

        // Check that the copy associated to the game is deleted
        verify(gameCopyRepo, times(1)).delete(validGameCopy);

    }

    @Test
    void testDeleteGame_NotFound() {
        when(gameRepo.findGameById(INVALID_GAME_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            gameManagementService.deleteGame(INVALID_GAME_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game does not exist", exception.getMessage());
    }

    @Test
    void testUpdateGame_Success() {
        GameUpdateDto dto = new GameUpdateDto("Updated Chess", "New Description", 4, 2);
        when(gameRepo.findGameById(VALID_GAME_ID)).thenReturn(validGame);
        when(gameRepo.save(any(Game.class))).thenReturn(validGame);

        Game updatedGame = gameManagementService.updateGame(dto, VALID_GAME_ID);

        assertNotNull(updatedGame);
        assertEquals("Updated Chess", updatedGame.getName());
        assertEquals("New Description", updatedGame.getDescription());
        assertEquals(4, updatedGame.getMaxPlayers());
        assertEquals(2, updatedGame.getMinPlayers());
    }

    @Test
    void testUpdateGame_NotFound() {
        GameUpdateDto dto = new GameUpdateDto("Updated Chess", "New Description", 4, 2);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            gameManagementService.updateGame(dto, INVALID_GAME_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(String.format("No game has Id %d", INVALID_GAME_ID), exception.getMessage());
    }

    @Test
    void testFindGameById_Success() {
        when(gameRepo.findGameById(VALID_GAME_ID)).thenReturn(validGame);

        Game foundGame = gameManagementService.findGameById(VALID_GAME_ID);
        assertNotNull(foundGame);
        assertEquals("Chess", foundGame.getName());
    }

    @Test
    void testFindGameById_NotFound() {
        when(gameRepo.findGameById(INVALID_GAME_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            gameManagementService.findGameById(INVALID_GAME_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game does not exist", exception.getMessage());
    }
}