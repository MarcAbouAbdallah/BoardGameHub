package ca.mcgill.ecse321.boardgamehub.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamehub.dto.GameCopyCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameCopyUpdateDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameUpdateDto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;

@ExtendWith(MockitoExtension.class)
public class GameManagementServiceTests {
    @Mock
    private GameRepository gameRepo;
    
    @Mock
    private GameCopyRepository gameCopyRepo;
    
    @Mock
    private BorrowRequestRepository borrowRequestRepo;

    @InjectMocks
    private GameManagementService gameManagementService;

    private static final int VALID_GAME_ID = 1;
    private static final int INVALID_GAME_ID = 99;
    private static final int VALID_GAME_COPY_ID = 2;
    private static final int INVALID_GAME_COPY_ID = 100;

    private Game validGame;
    private GameCopy validGameCopy;
    private Player validPlayer;

    @BeforeEach
    void setup() {
        validGame = new Game("Chess", 2, 2, "A strategy game");
        validGame.setId(VALID_GAME_ID);
        
        validPlayer = new Player("Alice", "alice@email.com", "securePass", false);
        
        validGameCopy = new GameCopy(true, validGame, validPlayer);
        validGameCopy.setId(VALID_GAME_COPY_ID);
    }

    @AfterEach
	public void clearDatabase() {
		gameRepo.deleteAll();
        gameCopyRepo.deleteAll();
	}

    @Test
    void testCreateGame_Success() {
        GameCreationDto dto = new GameCreationDto("Chess", "A strategy game", 2, 2);
        when(gameRepo.save(any(Game.class))).thenAnswer(invocation -> {
            Game game = invocation.getArgument(0);
            game.setId(VALID_GAME_ID);
            return game;
        });

        Game createdGame = gameManagementService.createGame(dto);

        assertNotNull(createdGame);
        assertEquals("Chess", createdGame.getName());
        assertEquals("A strategy game", createdGame.getDescription());
        assertEquals(2, createdGame.getMaxPlayers());
        assertEquals(2, createdGame.getMinPlayers());
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
        when(gameRepo.findGameById(INVALID_GAME_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            gameManagementService.updateGame(dto, INVALID_GAME_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No game has Id 99", exception.getMessage());
    }

    @Test
    void testUpdateGameCopy_Success() {
        GameCopyUpdateDto dto = new GameCopyUpdateDto(validGame, validPlayer, false);
        when(gameCopyRepo.findGameCopyById(VALID_GAME_COPY_ID)).thenReturn(validGameCopy);
        when(gameCopyRepo.save(any(GameCopy.class))).thenReturn(validGameCopy);

        GameCopy updatedGameCopy = gameManagementService.updateGameCopy(dto, VALID_GAME_COPY_ID);

        assertNotNull(updatedGameCopy);
        assertFalse(updatedGameCopy.getIsAvailable());
        assertEquals(validGame, updatedGameCopy.getGame());
        assertEquals(validPlayer, updatedGameCopy.getOwner());
    }

    @Test
    void testUpdateGameCopy_NotFound() {
        GameCopyUpdateDto dto = new GameCopyUpdateDto(validGame, validPlayer, false);
        when(gameCopyRepo.findGameCopyById(INVALID_GAME_COPY_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            gameManagementService.updateGameCopy(dto, INVALID_GAME_COPY_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No gamecopy has Id 100", exception.getMessage());
    }

    @Test
    void testGetOwnerById_Success() {
        when(gameCopyRepo.findGameCopyById(VALID_GAME_COPY_ID)).thenReturn(validGameCopy);

        Player owner = gameManagementService.getOwnerById(VALID_GAME_COPY_ID);

        assertNotNull(owner);
        assertEquals("Alice", owner.getName());
    }

    @Test
    void testGetOwnerById_NotFound() {
        when(gameCopyRepo.findGameCopyById(INVALID_GAME_COPY_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            gameManagementService.getOwnerById(INVALID_GAME_COPY_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game Copy does not exist", exception.getMessage());
    }
}
