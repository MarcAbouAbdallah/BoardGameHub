package ca.mcgill.ecse321.boardgamehub.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        //Arrange
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
        //Arrange
        gameCopyRepo.deleteAll();
    }

    @AfterAll
    public void cleanup() {
        //Arrange
        gameCopyRepo.deleteAll();
        gameRepo.deleteAll();
        playerRepo.deleteAll();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + portFromUri(client.getRootUri()) + "/gameCopies" + uri;
    }

    private String portFromUri(String rootUri) {
        String[] parts = rootUri.split(":");
        String portWithSuffix = parts[2];
        return portWithSuffix.replaceAll("[^0-9]", "");
    }

    @Test
    @Order(0)
    public void testGetEmptyPersonalCollection() {
        //Arrange
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        //Act
        ResponseEntity<GameCopyResponseDto[]> response = client.exchange(
            createURLWithPort("/" + testPlayer.getId()),
            HttpMethod.GET,
            requestEntity,
            GameCopyResponseDto[].class
        );
        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameCopyResponseDto[] collection = response.getBody();
        assertNotNull(collection);
        assertEquals(0, collection.length);
    }

    @Test
    @Order(1)
    public void testAddGameToPersonalCollection() {
        //Arrange
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        //Act
        ResponseEntity<GameCopyResponseDto> response = client.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/add?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponseDto.class
        );
        //Assert
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
        //Arrange: Add a game copy
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        client.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/add?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponseDto.class
        );
        //Act: Get available copies
        ResponseEntity<GameCopyResponseDto[]> response = client.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/available"),
            HttpMethod.GET,
            requestEntity,
            GameCopyResponseDto[].class
        );
        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameCopyResponseDto[] availableBefore = response.getBody();
        assertNotNull(availableBefore);
        assertEquals(1, availableBefore.length);

        //Arrange: Lend the copy
        client.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/lend?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponseDto.class
        );
        //Act: Get available copies again
        response = client.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/available"),
            HttpMethod.GET,
            requestEntity,
            GameCopyResponseDto[].class
        );
        //Assert
        GameCopyResponseDto[] availableAfter = response.getBody();
        assertNotNull(availableAfter);
        assertEquals(0, availableAfter.length);
    }

    @Test
    @Order(3)
    public void testLendAndReturnGameCopy() {
        //Arrange: Add a new game copy
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<GameCopyResponseDto> addResponse = client.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/add?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        //Act: Lend the game copy
        ResponseEntity<GameCopyResponseDto> lendResponse = client.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/lend?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponseDto.class
        );
        //Assert
        assertEquals(HttpStatus.OK, lendResponse.getStatusCode());
        GameCopyResponseDto lentCopy = lendResponse.getBody();
        assertNotNull(lentCopy);
        assertFalse(lentCopy.getIsAvailable());
        //Act: Return the game copy
        ResponseEntity<GameCopyResponseDto> returnResponse = client.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/return?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponseDto.class
        );
        //Assert
        assertEquals(HttpStatus.OK, returnResponse.getStatusCode());
        GameCopyResponseDto returnedCopy = returnResponse.getBody();
        assertNotNull(returnedCopy);
        assertTrue(returnedCopy.getIsAvailable());
    }

    @Test
    @Order(4)
    public void testRemoveGameFromPersonalCollection() {
        //Arrange: Add a game copy
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<GameCopyResponseDto> addResponse = client.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/add?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponseDto.class
        );
        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        //Act: Remove the game copy
        ResponseEntity<Void> removeResponse = client.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/remove?gameId=" + testGame.getId()),
            HttpMethod.DELETE,
            requestEntity,
            Void.class
        );
        //Assert
        assertEquals(HttpStatus.NO_CONTENT, removeResponse.getStatusCode());
        ResponseEntity<GameCopyResponseDto[]> getResponse = client.exchange(
            createURLWithPort("/" + testPlayer.getId()),
            HttpMethod.GET,
            requestEntity,
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
        //Arrange: Prepare a request without the "User-Id" header.
        HttpHeaders noUserHeaders = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(noUserHeaders);
        //Act: Attempt to add a game copy.
        ResponseEntity<String> addResponse = client.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/add?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            String.class
        );
        //Assert
        assertEquals(HttpStatus.UNAUTHORIZED, addResponse.getStatusCode());
    }
}