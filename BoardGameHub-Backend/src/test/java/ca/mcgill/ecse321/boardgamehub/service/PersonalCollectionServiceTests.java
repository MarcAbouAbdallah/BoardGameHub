package ca.mcgill.ecse321.boardgamehub.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PersonalCollectionServiceTests {

    @Mock
    private PlayerRepository mockPlayerRepo;
    @Mock
    private GameRepository mockGameRepo;
    @Mock
    private GameCopyRepository mockGameCopyRepo;
    @InjectMocks
    private PersonalCollectionService personalCollectionService;
    
    private static final int VALID_PLAYER_ID = 0;
    private static final int VALID_GAME_ID = 0;
    private static final Player VALID_PLAYER = new Player("John", "john@mail.com", "jhKLEHGH75*(#$&", true);
    private static final Game VALID_GAME = new Game("Monopoly", 4, 2, "A board game about capitalism basically.");
    
    @BeforeEach
    public void setUp() {
        VALID_PLAYER.setId(VALID_PLAYER_ID);
        VALID_GAME.setId(VALID_GAME_ID);
    }
    
    @Test
    public void testGetPersonalCollectionValid() {
        // Arrange
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        
        List<GameCopy> collection = new ArrayList<>();
        GameCopy gameCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        collection.add(gameCopy);
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(collection);
        
        // Act
        List<GameCopy> result = personalCollectionService.getPersonalCollection(VALID_PLAYER_ID);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(VALID_GAME.getId(), result.get(0).getGame().getId());
    }
    
    @Test
    public void testGetPersonalCollectionInvalidPlayer() {
        // Arrange: Player not found
        when(mockPlayerRepo.findById(123)).thenReturn(Optional.empty());
        
        // Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            personalCollectionService.getPersonalCollection(123);
        });
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Player with ID 123 not found.", e.getMessage());
    }
    
    @Test
    public void testAddGameToPersonalCollectionValid() {
        // Arrange
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        when(mockGameRepo.findById(VALID_GAME_ID)).thenReturn(Optional.of(VALID_GAME));
        // Player does not yet own this game
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(new ArrayList<>());
        when(mockGameCopyRepo.save(any(GameCopy.class))).thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));
        
        // Act
        GameCopy added = personalCollectionService.addGameToPersonalCollection(VALID_PLAYER_ID, VALID_GAME_ID);
        
        // Assert
        assertNotNull(added);
        assertTrue(added.getIsAvailable());
        assertEquals(VALID_GAME, added.getGame());
        assertEquals(VALID_PLAYER, added.getOwner());
    }
    
    @Test
    public void testAddGameToPersonalCollectionAlreadyOwned() {
        // Arrange
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        when(mockGameRepo.findById(VALID_GAME_ID)).thenReturn(Optional.of(VALID_GAME));
        
        // Return a list with a copy that has the same game
        List<GameCopy> collection = new ArrayList<>();
        GameCopy gameCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        collection.add(gameCopy);
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(collection);
        
        // Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            personalCollectionService.addGameToPersonalCollection(VALID_PLAYER_ID, VALID_GAME_ID);
        });
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Player already owns this game.", e.getMessage());
    }
    
    @Test
    public void testRemoveGameFromPersonalCollectionValid() {
        // Arrange
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        when(mockGameRepo.findById(VALID_GAME_ID)).thenReturn(Optional.of(VALID_GAME));
        
        List<GameCopy> collection = new ArrayList<>();
        GameCopy gameCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        collection.add(gameCopy);
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(collection);
        
        // Act & Assert
        assertDoesNotThrow(() -> personalCollectionService.removeGameFromPersonalCollection(VALID_PLAYER_ID, VALID_GAME_ID));
        verify(mockGameCopyRepo, times(1)).delete(gameCopy);
    }
    
    @Test
    public void testRemoveGameFromPersonalCollectionNotFound() {
        // Arrange
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        when(mockGameRepo.findById(VALID_GAME_ID)).thenReturn(Optional.of(VALID_GAME));
        // Player's collection is empty
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(new ArrayList<>());
        
        // Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            personalCollectionService.removeGameFromPersonalCollection(VALID_PLAYER_ID, VALID_GAME_ID);
        });
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Game not found in player's collection.", e.getMessage());
    }
    
    @Test
    public void testGetAvailableGames() {
        // Arrange
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        List<GameCopy> collection = new ArrayList<>();
        GameCopy availableCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        GameCopy lentCopy = new GameCopy(false, VALID_GAME, VALID_PLAYER);
        collection.add(availableCopy);
        collection.add(lentCopy);
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(collection);
        
        // Act
        List<GameCopy> availableGames = personalCollectionService.getAvailableGames(VALID_PLAYER_ID);
        
        // Assert
        assertNotNull(availableGames);
        assertEquals(1, availableGames.size());
        assertTrue(availableGames.get(0).getIsAvailable());
    }
    
    @Test
    public void testLendGameCopyValid() {
        // Arrange
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        when(mockGameRepo.findById(VALID_GAME_ID)).thenReturn(Optional.of(VALID_GAME));
        List<GameCopy> collection = new ArrayList<>();
        // Initially available
        GameCopy gameCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        collection.add(gameCopy);
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(collection);
        when(mockGameCopyRepo.save(any(GameCopy.class))).thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));
        
        // Act
        GameCopy lent = personalCollectionService.lendGameCopy(VALID_PLAYER_ID, VALID_GAME_ID);
        
        // Assert
        assertNotNull(lent);
        assertFalse(lent.getIsAvailable());
    }
    
    @Test
    public void testLendGameCopyAlreadyLent() {
        // Arrange
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        when(mockGameRepo.findById(VALID_GAME_ID)).thenReturn(Optional.of(VALID_GAME));
        List<GameCopy> collection = new ArrayList<>();
        // Already lent out
        GameCopy gameCopy = new GameCopy(false, VALID_GAME, VALID_PLAYER);
        collection.add(gameCopy);
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(collection);
        
        // Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            personalCollectionService.lendGameCopy(VALID_PLAYER_ID, VALID_GAME_ID);
        });
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Game copy is already lent out.", e.getMessage());
    }
    
    @Test
    public void testReturnGameCopyValid() {
        // Arrange
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        when(mockGameRepo.findById(VALID_GAME_ID)).thenReturn(Optional.of(VALID_GAME));
        List<GameCopy> collection = new ArrayList<>();
        // Initially lent out (not available)
        GameCopy gameCopy = new GameCopy(false, VALID_GAME, VALID_PLAYER);
        collection.add(gameCopy);
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(collection);
        when(mockGameCopyRepo.save(any(GameCopy.class))).thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));
        
        // Act
        GameCopy returned = personalCollectionService.returnGameCopy(VALID_PLAYER_ID, VALID_GAME_ID);
        
        // Assert
        assertNotNull(returned);
        assertTrue(returned.getIsAvailable());
    }
    
    @Test
    public void testReturnGameCopyAlreadyAvailable() {
        // Arrange
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        when(mockGameRepo.findById(VALID_GAME_ID)).thenReturn(Optional.of(VALID_GAME));
        List<GameCopy> collection = new ArrayList<>();
        // Already available
        GameCopy gameCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        collection.add(gameCopy);
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(collection);
        
        // Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            personalCollectionService.returnGameCopy(VALID_PLAYER_ID, VALID_GAME_ID);
        });
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Game copy is already available.", e.getMessage());
    }
}