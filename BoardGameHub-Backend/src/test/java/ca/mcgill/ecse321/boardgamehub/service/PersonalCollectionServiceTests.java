package ca.mcgill.ecse321.boardgamehub.service;

import static org.junit.jupiter.api.Assertions.*;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PersonalCollectionServiceTests {

    @Autowired
    private PersonalCollectionService personalGameCollectionService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameCopyRepository gameCopyRepository;

    private Player testPlayer;
    private Game testGame;

    @BeforeEach
    public void setup() {
        // Create test entities using parameterized constructors.
        testPlayer = new Player("Test Player", "testplayer@demo.ca", "testPassword", false);
        playerRepository.save(testPlayer);

        testGame = new Game("Test Game", 4, 2, "A fun test game");
        gameRepository.save(testGame);
    }

    @AfterEach
    public void cleanup() {
        gameCopyRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    public void testAddGameToPersonalCollection() {
        // Arrange
        List<GameCopy> initialCollection = personalGameCollectionService.getPersonalCollection(testPlayer.getId());
        assertTrue(initialCollection.isEmpty(), "Collection should be empty initially.");

        // Act
        GameCopy newCopy = personalGameCollectionService.addGameToPersonalCollection(testPlayer.getId(), testGame.getId());
        
        // Assert
        assertNotNull(newCopy, "Game copy should not be null.");
        assertEquals(testPlayer.getId(), newCopy.getOwner().getId(), "Owner should match test player.");
        assertEquals(testGame.getId(), newCopy.getGame().getId(), "Game should match test game.");
        assertTrue(newCopy.getIsAvailable(), "Game copy should be available.");

        // Act - get updated collection
        List<GameCopy> updatedCollection = personalGameCollectionService.getPersonalCollection(testPlayer.getId());
        
        // Assert - collection size updated correctly
        assertEquals(1, updatedCollection.size(), "Collection should have one game copy.");
    }

    @Test
    public void testDuplicateGameAdditionThrowsException() {
        // Arrange
        personalGameCollectionService.addGameToPersonalCollection(testPlayer.getId(), testGame.getId());
        
        // Act & Assert
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            personalGameCollectionService.addGameToPersonalCollection(testPlayer.getId(), testGame.getId())
        );
        // Assert
        assertTrue(exception.getMessage().contains("Player already owns this game."),
                   "Expected duplicate addition error.");
    }

    @Test
    public void testRemoveGameFromPersonalCollection() {
        // Arrange
        personalGameCollectionService.addGameToPersonalCollection(testPlayer.getId(), testGame.getId());
        List<GameCopy> collectionBeforeRemoval = personalGameCollectionService.getPersonalCollection(testPlayer.getId());
        assertEquals(1, collectionBeforeRemoval.size(), "Collection should have one game copy.");

        // Act
        personalGameCollectionService.removeGameFromPersonalCollection(testPlayer.getId(), testGame.getId());
        
        // Assert
        List<GameCopy> collectionAfterRemoval = personalGameCollectionService.getPersonalCollection(testPlayer.getId());
        assertTrue(collectionAfterRemoval.isEmpty(), "Collection should be empty after removal.");
    }

    @Test
    public void testRemoveNonExistentGameThrowsException() {
        // Arrange - ensure collection is empty
        // Act & Assert
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            personalGameCollectionService.removeGameFromPersonalCollection(testPlayer.getId(), testGame.getId())
        );
        // Assert
        assertTrue(exception.getMessage().contains("Game not found in player's collection."),
                   "Expected removal error.");
    }

    @Test
    public void testGetAvailableGames() {
        // Arrange
        List<GameCopy> available = personalGameCollectionService.getAvailableGames(testPlayer.getId());
        assertTrue(available.isEmpty(), "Available games should be empty initially.");

        // Act - add a game copy (available by default)
        personalGameCollectionService.addGameToPersonalCollection(testPlayer.getId(), testGame.getId());
        available = personalGameCollectionService.getAvailableGames(testPlayer.getId());
        
        // Assert - check available games count is 1
        assertEquals(1, available.size(), "Should have one available game copy.");

        // Act - lend the game copy
        personalGameCollectionService.lendGameCopy(testPlayer.getId(), testGame.getId());
        available = personalGameCollectionService.getAvailableGames(testPlayer.getId());
        
        // Assert - available games list should be empty after lending
        assertTrue(available.isEmpty(), "No available game copies after lending.");
    }

    @Test
    public void testLendGameCopy() {
        // Arrange
        personalGameCollectionService.addGameToPersonalCollection(testPlayer.getId(), testGame.getId());
        
        // Act
        GameCopy lentCopy = personalGameCollectionService.lendGameCopy(testPlayer.getId(), testGame.getId());
        
        // Assert
        assertFalse(lentCopy.getIsAvailable(), "Game copy should not be available after lending.");
    }

    @Test
    public void testLendGameCopyAlreadyLentOut() {
        // Arrange
        personalGameCollectionService.addGameToPersonalCollection(testPlayer.getId(), testGame.getId());
        personalGameCollectionService.lendGameCopy(testPlayer.getId(), testGame.getId());
        
        // Act & Assert
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            personalGameCollectionService.lendGameCopy(testPlayer.getId(), testGame.getId())
        );
        assertTrue(exception.getMessage().contains("Game copy is already lent out"),
                   "Expected exception for lending an already lent copy.");
    }

    @Test
    public void testReturnGameCopy() {
        // Arrange
        personalGameCollectionService.addGameToPersonalCollection(testPlayer.getId(), testGame.getId());
        personalGameCollectionService.lendGameCopy(testPlayer.getId(), testGame.getId());
        
        // Act
        GameCopy returnedCopy = personalGameCollectionService.returnGameCopy(testPlayer.getId(), testGame.getId());
        
        // Assert
        assertTrue(returnedCopy.getIsAvailable(), "Game copy should be available after returning.");
    }

    @Test
    public void testReturnGameCopyAlreadyAvailable() {
        // Arrange
        personalGameCollectionService.addGameToPersonalCollection(testPlayer.getId(), testGame.getId());
        
        // Act & Assert
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
            personalGameCollectionService.returnGameCopy(testPlayer.getId(), testGame.getId())
        );
        assertTrue(exception.getMessage().contains("Game copy is already available"),
                   "Expected exception for returning an available game copy.");
    }
}