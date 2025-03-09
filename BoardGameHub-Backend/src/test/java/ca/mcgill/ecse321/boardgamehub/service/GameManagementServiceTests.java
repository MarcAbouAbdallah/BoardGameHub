package ca.mcgill.ecse321.boardgamehub.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    }

    @Test
    void testDeleteGame_Success() {
        when(gameRepo.findGameById(VALID_GAME_ID)).thenReturn(validGame);
        doNothing().when(gameRepo).delete(validGame);

        assertDoesNotThrow(() -> gameManagementService.deleteGame(VALID_GAME_ID));
        verify(gameRepo, times(1)).delete(validGame);
    }

    @Test
    void testDeleteGame_NotFound() {
        when(gameRepo.findGameById(INVALID_GAME_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            gameManagementService.deleteGame(INVALID_GAME_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
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
    }

    @Test
    void testCreateGameCopy_Success() {
        GameCopyCreationDto dto = new GameCopyCreationDto(validGame, validPlayer, true);
        when(gameCopyRepo.save(any(GameCopy.class))).thenAnswer(invocation -> {
            GameCopy gameCopy = invocation.getArgument(0);
            gameCopy.setId(VALID_GAME_COPY_ID);
            return gameCopy;
        });

        GameCopy createdGameCopy = gameManagementService.createGameCopy(dto);

        assertNotNull(createdGameCopy);
        assertEquals(VALID_GAME_COPY_ID, createdGameCopy.getId());
    }

    @Test
    void testDeleteGameCopy_Success() {
        when(gameCopyRepo.findGameCopyById(VALID_GAME_COPY_ID)).thenReturn(validGameCopy);
        doNothing().when(gameCopyRepo).delete(validGameCopy);

        assertDoesNotThrow(() -> gameManagementService.deleteGameCopy(VALID_GAME_COPY_ID));
        verify(gameCopyRepo, times(1)).delete(validGameCopy);
    }

    @Test
    void testDeleteGameCopy_NotFound() {
        when(gameCopyRepo.findGameCopyById(INVALID_GAME_COPY_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            gameManagementService.deleteGameCopy(INVALID_GAME_COPY_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
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
    }
}
