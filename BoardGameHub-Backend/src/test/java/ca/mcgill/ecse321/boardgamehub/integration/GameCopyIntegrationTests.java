package ca.mcgill.ecse321.boardgamehub.integration;

import static org.junit.jupiter.api.Assertions.*;

import ca.mcgill.ecse321.boardgamehub.dto.GameCopyResponseDto;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamehub.repo.EventRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.RegistrationRepository;
import ca.mcgill.ecse321.boardgamehub.repo.ReviewRepository;

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

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private RegistrationRepository registrationRepo;

    @Autowired
    private BorrowRequestRepository borrowRequestRepo;

    private Player testPlayer;
    private Game testGame;

    private HttpHeaders headers;

    @BeforeAll
    public void setup() {
        // Arrange: clear all data
        clearDatabase();

        // Create test player
        testPlayer = new Player("Test Player", "testplayer@example.com", "password", false);
        playerRepo.save(testPlayer);

        // Create test game
        testGame = new Game("Test Game", 4, 2, "A fun test game", "https://images.unsplash.com/photo-1640461470346-c8b56497850a?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
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
    public void clearDatabase() {
        reviewRepo.deleteAll();
        registrationRepo.deleteAll();
        eventRepo.deleteAll();
        borrowRequestRepo.deleteAll();
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
            createURL("/gamecopies/players/"+testPlayer.getId()),
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
        //assertTrue(added.getIsAvailable());
    }

    @Test
    @Order(2)
    public void testGetAvailableGameCopies() {
        // Arrange: create one copy
        Map<String, Object> createPayload = new HashMap<>();
        createPayload.put("playerId", testPlayer.getId());
        createPayload.put("gameId", testGame.getId());
        client.postForEntity(
            createURL("/gamecopies"),
            new HttpEntity<>(createPayload, headers),
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
        GameCopyResponseDto[] available = response.getBody();
        assertNotNull(available);
        assertEquals(1, available.length);
        //assertTrue(available[0].getIsAvailable());
    }

    @Test
    @Order(2)
    public void testGetGameCopiesForGame() {
        // Arrange: create one copy
        Map<String, Object> createPayload = new HashMap<>();
        createPayload.put("playerId", testPlayer.getId());
        createPayload.put("gameId", testGame.getId());
        client.postForEntity(
            createURL("/gamecopies"),
            new HttpEntity<>(createPayload, headers),
            GameCopyResponseDto.class
        );

        ResponseEntity<GameCopyResponseDto[]> response = client.getForEntity(String.format("/gamecopies/games/%d", testGame.getId()), GameCopyResponseDto[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameCopyResponseDto[] copies = response.getBody();
        assertNotNull(copies);
        assertEquals(1, copies.length);
        GameCopyResponseDto copy = copies[0];
        assertEquals(testGame.getId(), copy.getGameId());
        assertEquals(testPlayer.getId(), copy.getOwnerId());
        //assertTrue(copy.getIsAvailable());
    }

    @Test
    @Order(4)
    public void testRemoveGameFromPersonalCollection() {
        // Arrange: create a copy
        Map<String, Object> createPayload = new HashMap<>();
        createPayload.put("playerId", testPlayer.getId());
        createPayload.put("gameId", testGame.getId());
        ResponseEntity<GameCopyResponseDto> addResponse = client.postForEntity(
            createURL("/gamecopies"),
            new HttpEntity<>(createPayload, headers),
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        GameCopyResponseDto addedCopy = addResponse.getBody();
        assertNotNull(addedCopy);

        // Act: DELETE /gamecopies/{gameCopyId}?userId={testPlayer.getId()}
        ResponseEntity<Void> deleteResponse = client.exchange(
            createURL("/gamecopies/" + addedCopy.getGameCopyId() + "?userId=" + testPlayer.getId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class
        );
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Act: GET /gamecopies with body {playerId} to verify deletion
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("playerId", testPlayer.getId());
        ResponseEntity<GameCopyResponseDto[]> getResponse = client.exchange(
            createURL("/gamecopies/players/"+testPlayer.getId()),
            HttpMethod.GET,
            new HttpEntity<>(searchBody, headers),
            GameCopyResponseDto[].class
        );
        // Assert
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        GameCopyResponseDto[] collection = getResponse.getBody();
        assertNotNull(collection);
        assertEquals(0, collection.length);
    }

    @Test
    @Order(5)
    public void testRemoveGameFromPersonalCollection_Unauthorized() {
        // Arrange: Create a game copy as testPlayer
        Map<String, Object> createPayload = new HashMap<>();
        createPayload.put("playerId", testPlayer.getId());
        createPayload.put("gameId", testGame.getId());
        ResponseEntity<GameCopyResponseDto> addResponse = client.postForEntity(
            createURL("/gamecopies"),
            new HttpEntity<>(createPayload, headers),
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        GameCopyResponseDto addedCopy = addResponse.getBody();
        assertNotNull(addedCopy);

        // Arrange: Create an unauthorized player (different from testPlayer)
        Player unauthorizedPlayer = new Player("Unauthorized User", "unauth@example.com", "pwd123", false);
        playerRepo.save(unauthorizedPlayer);

        // Act: Attempt to DELETE using unauthorized user's ID in the query parameter
        ResponseEntity<String> deleteResponse = client.exchange(
            createURL("/gamecopies/" + addedCopy.getGameCopyId() + "?userId=" + unauthorizedPlayer.getId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            String.class
        );

        // Assert: Expect FORBIDDEN (403)
        assertEquals(HttpStatus.FORBIDDEN, deleteResponse.getStatusCode());
        String errorBody = deleteResponse.getBody();
        assertNotNull(errorBody);
        assertTrue(errorBody.contains("not the owner") || errorBody.contains("Deletion is not allowed"),
                "Error message should indicate the user is not the owner.");
    }

    @Test
    @Order(8)
    public void testGetGameCopyById() {
        // Arrange: Create a new game copy
        Map<String, Object> createPayload = new HashMap<>();
        createPayload.put("playerId", testPlayer.getId());
        createPayload.put("gameId", testGame.getId());
        ResponseEntity<GameCopyResponseDto> addResponse = client.postForEntity(
            createURL("/gamecopies"),
            new HttpEntity<>(createPayload, headers),
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        GameCopyResponseDto createdCopy = addResponse.getBody();
        assertNotNull(createdCopy);

        // Act: Retrieve the game copy by its ID using GET /gamecopies/{gameCopyId}
        ResponseEntity<GameCopyResponseDto> getResponse = client.exchange(
            createURL("/gamecopies/" + createdCopy.getGameCopyId()),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GameCopyResponseDto.class
        );

        // Assert: Verify that the retrieved copy matches the created copy
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        GameCopyResponseDto retrievedCopy = getResponse.getBody();
        assertNotNull(retrievedCopy);
        assertEquals(createdCopy.getGameCopyId(), retrievedCopy.getGameCopyId());
        assertEquals(createdCopy.getOwnerId(), retrievedCopy.getOwnerId());
        assertEquals(createdCopy.getGameId(), retrievedCopy.getGameId());
    }
}