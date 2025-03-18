package ca.mcgill.ecse321.boardgamehub.integration;

import static org.junit.jupiter.api.Assertions.*;

import ca.mcgill.ecse321.boardgamehub.dto.GameCopyResponseDto;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class GameCopyIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameCopyRepository gameCopyRepo;

    private Player testPlayer;
    private Game testGame;

    private HttpHeaders headers;

    @BeforeAll
    public void setup() {
        // Arrange: clear all data
        gameCopyRepo.deleteAll();
        gameRepo.deleteAll();
        playerRepo.deleteAll();

        // Create test player
        testPlayer = new Player("Test Player", "testplayer@example.com", "password", false);
        playerRepo.save(testPlayer);

        // Create test game
        testGame = new Game("Test Game", 4, 2, "A fun test game");
        gameRepo.save(testGame);

        // Headers if needed (e.g., for auth simulation)
        headers = new HttpHeaders();
        headers.set("User-Id", String.valueOf(testPlayer.getId()));
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @AfterEach
    public void cleanupAfterEach() {
        // Clean up game copies between tests
        gameCopyRepo.deleteAll();
    }

    @AfterAll
    public void cleanup() {
        // Final cleanup
        gameCopyRepo.deleteAll();
        gameRepo.deleteAll();
        playerRepo.deleteAll();
    }

    private String createURL(String uri) {
        return client.getRootUri() + uri;
    }

    @Test
    @Order(0)
    public void testGetEmptyPersonalCollection() {
        // Arrange
        Map<String, Object> body = new HashMap<>();
        body.put("playerId", testPlayer.getId());
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Act
        ResponseEntity<GameCopyResponseDto[]> response = client.exchange(
            createURL("/gamecopies"),
            HttpMethod.GET,
            requestEntity,
            GameCopyResponseDto[].class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameCopyResponseDto[] collection = response.getBody();
        assertNotNull(collection);
        assertEquals(0, collection.length);
    }

    @Test
    @Order(1)
    public void testAddGameToPersonalCollection() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", testPlayer.getId());
        payload.put("gameId", testGame.getId());
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        // Act
        ResponseEntity<GameCopyResponseDto> response = client.exchange(
            createURL("/gamecopies"),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponseDto.class
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        GameCopyResponseDto added = response.getBody();
        assertNotNull(added);
        assertEquals(testGame.getId(), added.getGameId());
        assertEquals(testPlayer.getId(), added.getOwnerId());
        assertTrue(added.getIsAvailable());
    }

    @Test
    @Order(2)
    public void testGetAvailableGameCopies() {
        // Arrange: create one copy
        Map<String, Object> createBody = new HashMap<>();
        createBody.put("playerId", testPlayer.getId());
        createBody.put("gameId", testGame.getId());
        client.exchange(
            createURL("/gamecopies"),
            HttpMethod.POST,
            new HttpEntity<>(createBody, headers),
            GameCopyResponseDto.class
        );

        // Act: retrieve available copies
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("playerId", testPlayer.getId());
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(searchBody, headers);

        ResponseEntity<GameCopyResponseDto[]> response = client.exchange(
            createURL("/gamecopies/available"),
            HttpMethod.GET,
            requestEntity,
            GameCopyResponseDto[].class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameCopyResponseDto[] availableBefore = response.getBody();
        assertNotNull(availableBefore);
        assertEquals(1, availableBefore.length);
        assertTrue(availableBefore[0].getIsAvailable());
    }
    
    @Test
    @Order(3)
    public void testLendAndReturnGameCopy() {
        // Arrange: create a copy
        Map<String, Object> createBody = new HashMap<>();
        createBody.put("playerId", testPlayer.getId());
        createBody.put("gameId", testGame.getId());
        ResponseEntity<GameCopyResponseDto> addResponse = client.exchange(
            createURL("/gamecopies"),
            HttpMethod.POST,
            new HttpEntity<>(createBody, headers),
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        GameCopyResponseDto addedCopy = addResponse.getBody();
        assertNotNull(addedCopy);

        // Lend the copy: isAvailable = false
        Map<String, Object> lendPayload = new HashMap<>();
        lendPayload.put("ownerId", testPlayer.getId());
        lendPayload.put("isAvailable", false);
        ResponseEntity<GameCopyResponseDto> lendResponse = client.exchange(
            createURL("/gamecopies/" + addedCopy.getGameCopyId()),
            HttpMethod.PATCH,
            new HttpEntity<>(lendPayload, headers),
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.OK, lendResponse.getStatusCode());
        GameCopyResponseDto lentCopy = lendResponse.getBody();
        assertNotNull(lentCopy);
        assertFalse(lentCopy.getIsAvailable());

        // Return the copy: isAvailable = true
        Map<String, Object> returnPayload = new HashMap<>();
        returnPayload.put("ownerId", testPlayer.getId());
        returnPayload.put("isAvailable", true);
        ResponseEntity<GameCopyResponseDto> returnResponse = client.exchange(
            createURL("/gamecopies/" + addedCopy.getGameCopyId()),
            HttpMethod.PATCH,
            new HttpEntity<>(returnPayload, headers),
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.OK, returnResponse.getStatusCode());
        GameCopyResponseDto returnedCopy = returnResponse.getBody();
        assertNotNull(returnedCopy);
        assertTrue(returnedCopy.getIsAvailable());
    }

    @Test
    @Order(4)
    public void testRemoveGameFromPersonalCollection() {
        // Arrange: create a copy
        Map<String, Object> createBody = new HashMap<>();
        createBody.put("playerId", testPlayer.getId());
        createBody.put("gameId", testGame.getId());
        ResponseEntity<GameCopyResponseDto> addResponse = client.exchange(
            createURL("/gamecopies"),
            HttpMethod.POST,
            new HttpEntity<>(createBody, headers),
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        GameCopyResponseDto addedCopy = addResponse.getBody();
        assertNotNull(addedCopy);

        // Act: remove the copy
        ResponseEntity<Void> removeResponse = client.exchange(
            createURL("/gamecopies/" + addedCopy.getGameCopyId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, removeResponse.getStatusCode());

        // Assert: make sure it's gone
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("playerId", testPlayer.getId());
        ResponseEntity<GameCopyResponseDto[]> getResponse = client.exchange(
            createURL("/gamecopies"),
            HttpMethod.GET,
            new HttpEntity<>(searchBody, headers),
            GameCopyResponseDto[].class
        );
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        GameCopyResponseDto[] collection = getResponse.getBody();
        assertNotNull(collection);
        assertEquals(0, collection.length);
    }

    @Test
    @Order(5)
    public void testUnauthorizedAccess() {
        // Arrange: Remove "User-Id" header
        HttpHeaders noUserHeaders = new HttpHeaders();
        noUserHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> createBody = new HashMap<>();
        createBody.put("playerId", testPlayer.getId());
        createBody.put("gameId", testGame.getId());
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(createBody, noUserHeaders);

        // Act
        ResponseEntity<String> addResponse = client.exchange(
            createURL("/gamecopies"),
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, addResponse.getStatusCode());
    }
}