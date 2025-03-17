package ca.mcgill.ecse321.boardgamehub.integration;

import static org.junit.jupiter.api.Assertions.*;

import ca.mcgill.ecse321.boardgamehub.dto.GameCopyResponseDto;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    private final String TEST_PLAYER_NAME = "Test Player";
    private final String TEST_PLAYER_EMAIL = "testplayer@example.com";
    private final String TEST_PLAYER_PASSWORD = "password";
    private final String TEST_GAME_NAME = "Test Game";
    private final int TEST_GAME_MIN_PLAYERS = 4;
    private final int TEST_GAME_MAX_PLAYERS = 2;
    private final String TEST_GAME_DESCRIPTION = "A fun test game";

    private Player testPlayer;
    private Game testGame;
    private HttpHeaders headers;

    @BeforeAll
    public void setup() {
        // Arrange
        gameCopyRepo.deleteAll();
        gameRepo.deleteAll();
        playerRepo.deleteAll();

        testPlayer = new Player(TEST_PLAYER_NAME, TEST_PLAYER_EMAIL, TEST_PLAYER_PASSWORD, false);
        playerRepo.save(testPlayer);

        testGame = new Game(TEST_GAME_NAME, TEST_GAME_MIN_PLAYERS, TEST_GAME_MAX_PLAYERS, TEST_GAME_DESCRIPTION);
        gameRepo.save(testGame);

        headers = new HttpHeaders();
        headers.set("User-Id", String.valueOf(testPlayer.getId()));
    }

    @AfterEach
    public void cleanupAfterEach() {
        // Arrange
        gameCopyRepo.deleteAll();
    }

    @AfterAll
    public void cleanup() {
        // Arrange
        gameCopyRepo.deleteAll();
        gameRepo.deleteAll();
        playerRepo.deleteAll();
    }

    // Utility to build a full URL from a URI fragment
    private String createURLWithPort(String uri) {
        return "http://localhost:" + portFromUri(client.getRootUri()) + uri;
    }

    private String portFromUri(String rootUri) {
        String[] parts = rootUri.split(":");
        String portWithSuffix = parts[2];
        return portWithSuffix.replaceAll("[^0-9]", "");
    }

    @Test
    @Order(0)
    public void testGetEmptyPersonalCollection() {
        // Arrange
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        // Act
        ResponseEntity<GameCopyResponseDto[]> response = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies"),
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
        payload.put("gameId", testGame.getId());
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
        // Act
        ResponseEntity<GameCopyResponseDto> response = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies"),
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
    public void testAssociateExistingGameCopy() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("gameId", testGame.getId());
        HttpEntity<Map<String, Object>> addEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<GameCopyResponseDto> addResponse = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies"),
            HttpMethod.POST,
            addEntity,
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        GameCopyResponseDto createdCopy = addResponse.getBody();

        var gameCopy = gameCopyRepo.findById(createdCopy.getGameCopyId()).get();
        gameCopy.setOwner(null);
        gameCopyRepo.save(gameCopy);
        // Act
        HttpEntity<?> associateEntity = new HttpEntity<>(headers);
        ResponseEntity<GameCopyResponseDto> associateResponse = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies/existing?gameCopyId=" + gameCopy.getId()),
            HttpMethod.POST,
            associateEntity,
            GameCopyResponseDto.class
        );
        // Assert
        assertEquals(HttpStatus.CREATED, associateResponse.getStatusCode());
        GameCopyResponseDto associatedCopy = associateResponse.getBody();
        assertNotNull(associatedCopy);
        assertEquals(testPlayer.getId(), associatedCopy.getOwnerId());
    }

    @Test
    @Order(3)
    public void testGetAvailableGameCopies() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("gameId", testGame.getId());
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
        client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies"),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponseDto.class
        );
        // Act
        ResponseEntity<GameCopyResponseDto[]> response = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies/available"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GameCopyResponseDto[].class
        );
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameCopyResponseDto[] availableBefore = response.getBody();
        assertNotNull(availableBefore);
        assertEquals(1, availableBefore.length);

        // Arrange
        Map<String, Object> patchPayload = new HashMap<>();
        patchPayload.put("isAvailable", false);
        HttpEntity<Map<String, Object>> lendEntity = new HttpEntity<>(patchPayload, headers);
        // Act
        ResponseEntity<GameCopyResponseDto> lendResponse = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies/" + availableBefore[0].getGameCopyId()),
            HttpMethod.PATCH,
            lendEntity,
            GameCopyResponseDto.class
        );
        // Assert
        assertEquals(HttpStatus.OK, lendResponse.getStatusCode());
        GameCopyResponseDto lentCopy = lendResponse.getBody();
        assertNotNull(lentCopy);
        assertFalse(lentCopy.getIsAvailable());

        // Act
        ResponseEntity<GameCopyResponseDto[]> responseAfter = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies/available"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GameCopyResponseDto[].class
        );
        // Assert
        GameCopyResponseDto[] availableAfter = responseAfter.getBody();
        assertNotNull(availableAfter);
        assertEquals(0, availableAfter.length);
    }

    @Test
    @Order(4)
    public void testLendAndReturnGameCopy() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("gameId", testGame.getId());
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<GameCopyResponseDto> addResponse = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies"),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        GameCopyResponseDto addedCopy = addResponse.getBody();
        assertNotNull(addedCopy);

        // Arrange
        Map<String, Object> lendPayload = new HashMap<>();
        lendPayload.put("isAvailable", false);
        HttpEntity<Map<String, Object>> lendEntity = new HttpEntity<>(lendPayload, headers);
        // Act
        ResponseEntity<GameCopyResponseDto> lendResponse = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies/" + addedCopy.getGameCopyId()),
            HttpMethod.PATCH,
            lendEntity,
            GameCopyResponseDto.class
        );
        // Assert
        assertEquals(HttpStatus.OK, lendResponse.getStatusCode());
        GameCopyResponseDto lentCopy = lendResponse.getBody();
        assertNotNull(lentCopy);
        assertFalse(lentCopy.getIsAvailable());

        // Arrange
        Map<String, Object> returnPayload = new HashMap<>();
        returnPayload.put("isAvailable", true);
        HttpEntity<Map<String, Object>> returnEntity = new HttpEntity<>(returnPayload, headers);
        // Act
        ResponseEntity<GameCopyResponseDto> returnResponse = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies/" + addedCopy.getGameCopyId()),
            HttpMethod.PATCH,
            returnEntity,
            GameCopyResponseDto.class
        );
        // Assert
        assertEquals(HttpStatus.OK, returnResponse.getStatusCode());
        GameCopyResponseDto returnedCopy = returnResponse.getBody();
        assertNotNull(returnedCopy);
        assertTrue(returnedCopy.getIsAvailable());
    }

    @Test
    @Order(5)
    public void testRemoveGameFromPersonalCollection() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("gameId", testGame.getId());
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<GameCopyResponseDto> addResponse = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies"),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        GameCopyResponseDto addedCopy = addResponse.getBody();
        assertNotNull(addedCopy);

        // Act
        ResponseEntity<Void> removeResponse = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies/" + addedCopy.getGameCopyId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class
        );
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, removeResponse.getStatusCode());

        // Act
        ResponseEntity<GameCopyResponseDto[]> getResponse = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GameCopyResponseDto[].class
        );
        // Assert
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        GameCopyResponseDto[] collection = getResponse.getBody();
        assertNotNull(collection);
        assertEquals(0, collection.length);
    }

    @Test
    @Order(6)
    public void testUnauthorizedAccess() {
        // Arrange
        HttpHeaders noUserHeaders = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(noUserHeaders);
        // Act
        ResponseEntity<String> addResponse = client.exchange(
            createURLWithPort("/players/" + testPlayer.getId() + "/gameCopies"),
            HttpMethod.POST,
            requestEntity,
            String.class
        );
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, addResponse.getStatusCode());
    }
}