package ca.mcgill.ecse321.boardgamehub.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import ca.mcgill.ecse321.boardgamehub.model.BorrowRequest;
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
    private Player validBorrower;

    @BeforeEach
    void setup() {
        validGame = new Game("Chess", 2, 2, "A strategy game");
        validGame.setId(VALID_GAME_ID);
        
        validPlayer = new Player("Alice", "alice@email.com", "securePass", false);
        validBorrower = new Player("Bob", "bob@gmail.com", "Bob@123", false);
        
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
        assertEquals("A strategy game", createdGame.getDescription());
        assertEquals(2, createdGame.getMaxPlayers());
        assertEquals(2, createdGame.getMinPlayers());
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
        when(gameRepo.findGameById(INVALID_GAME_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            gameManagementService.updateGame(dto, INVALID_GAME_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game does not exist", exception.getMessage());
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
