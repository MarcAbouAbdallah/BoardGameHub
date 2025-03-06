package ca.mcgill.ecse321.boardgamehub.integration;

import static org.junit.jupiter.api.Assertions.*;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.response.GameCopyResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GameCopyIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GameCopyRepository gameCopyRepository;
    
    private Player testPlayer;
    private Game testGame;
    private HttpHeaders headers;
    
    @BeforeEach
    public void setup() {
        // Create test player and game using parameterized constructors
        testPlayer = new Player("Test Player", "testplayer@example.com", "password", false);
        playerRepository.save(testPlayer);
        
        testGame = new Game("Test Game", 4, 2, "A fun test game");
        gameRepository.save(testGame);
        
        headers = new HttpHeaders();
        headers.set("User-Id", String.valueOf(testPlayer.getId()));
    }
    
    @AfterEach
    public void cleanup() {
        gameCopyRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }
    
    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/gameCopies" + uri;
    }
    
    @Test
    public void testGetEmptyPersonalCollection() {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<GameCopyResponse[]> response = restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId()),
            HttpMethod.GET,
            requestEntity,
            GameCopyResponse[].class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
    }
    
    @Test
    public void testAddGameToPersonalCollection() {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<GameCopyResponse> response = restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/add?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponse.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameCopyResponse added = response.getBody();
        assertNotNull(added);
        assertEquals(testGame.getId(), added.getGameId());
        assertEquals(testPlayer.getId(), added.getOwnerId());
        assertTrue(added.getIsAvailable());
    }
    
    @Test
    public void testGetAvailableGameCopies() {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        // Add a game copy (available by default)
        restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/add?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponse.class
        );
        
        // Verify available copies exist
        ResponseEntity<GameCopyResponse[]> availableResponse = restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/available"),
            HttpMethod.GET,
            requestEntity,
            GameCopyResponse[].class
        );
        assertEquals(HttpStatus.OK, availableResponse.getStatusCode());
        assertNotNull(availableResponse.getBody());
        assertEquals(1, availableResponse.getBody().length);
        
        // Lend the game copy so it's no longer available
        restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/lend?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponse.class
        );
        
        // Verify available copies are now empty
        availableResponse = restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/available"),
            HttpMethod.GET,
            requestEntity,
            GameCopyResponse[].class
        );
        assertEquals(HttpStatus.OK, availableResponse.getStatusCode());
        assertNotNull(availableResponse.getBody());
        assertEquals(0, availableResponse.getBody().length);
    }
    
    @Test
    public void testLendAndReturnGameCopy() {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        // Add a game copy
        restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/add?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponse.class
        );
        // Lend the game copy
        ResponseEntity<GameCopyResponse> lendResponse = restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/lend?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponse.class
        );
        assertEquals(HttpStatus.OK, lendResponse.getStatusCode());
        GameCopyResponse lentCopy = lendResponse.getBody();
        assertNotNull(lentCopy);
        assertFalse(lentCopy.getIsAvailable());
        
        // Return the game copy
        ResponseEntity<GameCopyResponse> returnResponse = restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/return?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponse.class
        );
        assertEquals(HttpStatus.OK, returnResponse.getStatusCode());
        GameCopyResponse returnedCopy = returnResponse.getBody();
        assertNotNull(returnedCopy);
        assertTrue(returnedCopy.getIsAvailable());
    }
    
    @Test
    public void testRemoveGameFromPersonalCollection() {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        // Add a game copy first
        restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/add?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            GameCopyResponse.class
        );
        
        // Remove the game copy
        ResponseEntity<Void> removeResponse = restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/remove?gameId=" + testGame.getId()),
            HttpMethod.DELETE,
            requestEntity,
            Void.class
        );
        assertEquals(HttpStatus.OK, removeResponse.getStatusCode());
        
        // Verify that the collection is empty
        ResponseEntity<GameCopyResponse[]> getResponse = restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId()),
            HttpMethod.GET,
            requestEntity,
            GameCopyResponse[].class
        );
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(0, getResponse.getBody().length);
    }
    
    @Test
    public void testUnauthorizedAccess() {
        // Missing User-Id header should result in unauthorized response for modifying endpoints
        HttpHeaders noUserHeaders = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(noUserHeaders);
        ResponseEntity<String> addResponse = restTemplate.exchange(
            createURLWithPort("/" + testPlayer.getId() + "/add?gameId=" + testGame.getId()),
            HttpMethod.POST,
            requestEntity,
            String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, addResponse.getStatusCode());
    }
}