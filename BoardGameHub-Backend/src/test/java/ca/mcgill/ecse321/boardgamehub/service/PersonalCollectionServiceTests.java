package ca.mcgill.ecse321.boardgamehub.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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
    private static final int VALID_GAME_COPY_ID = 1;
    private static final int OTHER_PLAYER_ID = 2;

    private static final Player VALID_PLAYER = new Player("John", "john@mail.com", "jhKLEHGH75*(#$&", true);
    private static final Game VALID_GAME = new Game("Monopoly", 4, 2, "A board game about capitalism basically.", "https://images.unsplash.com/photo-1640461470346-c8b56497850a?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");

    @BeforeEach
    public void setUp() {
        VALID_PLAYER.setId(VALID_PLAYER_ID);
        VALID_GAME.setId(VALID_GAME_ID);
    }
    
    @Test
    public void testGetPersonalCollectionValid() {
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        
        List<GameCopy> collection = new ArrayList<>();
        GameCopy gameCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        gameCopy.setId(VALID_GAME_COPY_ID);
        collection.add(gameCopy);
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(collection);
        
        List<GameCopy> result = personalCollectionService.getPersonalCollection(VALID_PLAYER_ID);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(VALID_GAME.getId(), result.get(0).getGame().getId());
    }
    
    @Test
    public void testGetPersonalCollectionInvalidPlayer() {
        when(mockPlayerRepo.findById(123)).thenReturn(Optional.empty());
        
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            personalCollectionService.getPersonalCollection(123);
        });
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Player with ID 123 not found.", e.getMessage());
    }
    
    @Test
    public void testAddGameToPersonalCollectionValid() {
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        when(mockGameRepo.findById(VALID_GAME_ID)).thenReturn(Optional.of(VALID_GAME));
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(new ArrayList<>());
        when(mockGameCopyRepo.save(any(GameCopy.class))).thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));
        
        GameCopy added = personalCollectionService.addGameToPersonalCollection(VALID_PLAYER_ID, VALID_GAME_ID);
        
        assertNotNull(added);
        assertTrue(added.getIsAvailable());
        assertEquals(VALID_GAME, added.getGame());
        assertEquals(VALID_PLAYER, added.getOwner());
    }
    
    @Test
    public void testAddGameToPersonalCollectionAlreadyOwned() {
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        when(mockGameRepo.findById(VALID_GAME_ID)).thenReturn(Optional.of(VALID_GAME));
        
        List<GameCopy> collection = new ArrayList<>();
        GameCopy gameCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        collection.add(gameCopy);
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(collection);
        
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            personalCollectionService.addGameToPersonalCollection(VALID_PLAYER_ID, VALID_GAME_ID);
        });
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Player already owns this game.", e.getMessage());
    }
    
    @Test
    public void testRemoveGameCopy_ValidOwnership() {
        // Arrange
        GameCopy gameCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        gameCopy.setId(VALID_GAME_COPY_ID);

        when(mockGameCopyRepo.findById(VALID_GAME_COPY_ID)).thenReturn(Optional.of(gameCopy));

        // Act & Assert: Should not throw, and the repo should delete the game copy
        assertDoesNotThrow(() -> personalCollectionService.removeGameCopy(VALID_PLAYER_ID, VALID_GAME_COPY_ID));
        verify(mockGameCopyRepo, times(1)).delete(gameCopy);
    }

    @Test
    public void testRemoveGameCopy_NotOwner() {
        // Arrange: The copy's owner is VALID_PLAYER, but we pass OTHER_PLAYER_ID
        Player otherPlayer = new Player("Jane", "jane@mail.com", "pwd", false);
        otherPlayer.setId(OTHER_PLAYER_ID);

        // The copy belongs to VALID_PLAYER
        GameCopy gameCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        gameCopy.setId(VALID_GAME_COPY_ID);

        when(mockGameCopyRepo.findById(VALID_GAME_COPY_ID)).thenReturn(Optional.of(gameCopy));

        // Act & Assert: Expect 403 FORBIDDEN
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            personalCollectionService.removeGameCopy(OTHER_PLAYER_ID, VALID_GAME_COPY_ID);
        });
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
        assertEquals("You are not the owner of this game copy. Deletion is not allowed.", e.getMessage());
    }

    @Test
    public void testRemoveGameCopy_NotFound() {
        // Arrange
        when(mockGameCopyRepo.findById(VALID_GAME_COPY_ID)).thenReturn(Optional.empty());
        
        // Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            personalCollectionService.removeGameCopy(VALID_PLAYER_ID, VALID_GAME_COPY_ID);
        });
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals(String.format("Game copy with ID %d not found.", VALID_GAME_COPY_ID), e.getMessage());
    }

    @Test
    public void testGetAvailableGames() {
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        List<GameCopy> collection = new ArrayList<>();
        GameCopy availableCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        availableCopy.setId(VALID_GAME_COPY_ID);
        GameCopy lentCopy = new GameCopy(false, VALID_GAME, VALID_PLAYER);
        lentCopy.setId(VALID_GAME_COPY_ID + 1);
        collection.add(availableCopy);
        collection.add(lentCopy);
        when(mockGameCopyRepo.findByOwner(VALID_PLAYER)).thenReturn(collection);
        
        List<GameCopy> availableGames = personalCollectionService.getAvailableGames(VALID_PLAYER_ID);
        
        assertNotNull(availableGames);
        assertEquals(1, availableGames.size());
        assertTrue(availableGames.get(0).getIsAvailable());
    }
    
    @Test
    public void testLendGameCopyValid() {
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        GameCopy gameCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        gameCopy.setId(VALID_GAME_COPY_ID);
        when(mockGameCopyRepo.findById(VALID_GAME_COPY_ID)).thenReturn(Optional.of(gameCopy));
        when(mockGameCopyRepo.save(any(GameCopy.class))).thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));
        
        GameCopy lent = personalCollectionService.lendGameCopy(VALID_PLAYER_ID, VALID_GAME_COPY_ID);
        
        assertNotNull(lent);
        assertFalse(lent.getIsAvailable());
    }
    
    @Test
    public void testLendGameCopyAlreadyLent() {
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        GameCopy gameCopy = new GameCopy(false, VALID_GAME, VALID_PLAYER);
        gameCopy.setId(VALID_GAME_COPY_ID);
        when(mockGameCopyRepo.findById(VALID_GAME_COPY_ID)).thenReturn(Optional.of(gameCopy));
        
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            personalCollectionService.lendGameCopy(VALID_PLAYER_ID, VALID_GAME_COPY_ID);
        });
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Game copy is already lent out.", e.getMessage());
    }
    
    @Test
    public void testReturnGameCopyValid() {
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        GameCopy gameCopy = new GameCopy(false, VALID_GAME, VALID_PLAYER);
        gameCopy.setId(VALID_GAME_COPY_ID);
        when(mockGameCopyRepo.findById(VALID_GAME_COPY_ID)).thenReturn(Optional.of(gameCopy));
        when(mockGameCopyRepo.save(any(GameCopy.class))).thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));
        
        GameCopy returned = personalCollectionService.returnGameCopy(VALID_PLAYER_ID, VALID_GAME_COPY_ID);
        
        assertNotNull(returned);
        assertTrue(returned.getIsAvailable());
    }
    
    @Test
    public void testReturnGameCopyAlreadyAvailable() {
        when(mockPlayerRepo.findById(VALID_PLAYER_ID)).thenReturn(Optional.of(VALID_PLAYER));
        GameCopy gameCopy = new GameCopy(true, VALID_GAME, VALID_PLAYER);
        gameCopy.setId(VALID_GAME_COPY_ID);
        when(mockGameCopyRepo.findById(VALID_GAME_COPY_ID)).thenReturn(Optional.of(gameCopy));
        
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            personalCollectionService.returnGameCopy(VALID_PLAYER_ID, VALID_GAME_COPY_ID);
        });
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Game copy is already available.", e.getMessage());
    }
}