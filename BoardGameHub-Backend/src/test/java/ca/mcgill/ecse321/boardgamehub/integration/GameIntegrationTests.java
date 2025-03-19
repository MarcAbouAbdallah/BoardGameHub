package ca.mcgill.ecse321.boardgamehub.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamehub.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameResponseDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameUpdateDto;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class GameIntegrationTests {
    
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GameRepository gameRepo;

    private static final String NAME = "Catan";
    private static final String DESCRIPTION = "Strategy Game";
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PLAYERS = 2;

    private static int gameId;

    @BeforeAll
    public void setup() {
        gameRepo.deleteAll();
    }

    @AfterAll
    public void clearDatabase() {
        gameRepo.deleteAll();
        playerRepo.deleteAll();
    }

    @Test
    @Order(0)
    public void testCreateGame_Fail() {
        GameCreationDto gameDto = new GameCreationDto(NAME, DESCRIPTION, MAX_PLAYERS, MAX_PLAYERS+1);
        ResponseEntity<ErrorDto> response = client.postForEntity("/games", gameDto, ErrorDto.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        ErrorDto responseDto = response.getBody();
		assertNotNull(responseDto);
        assertIterableEquals(
		    List.of("Min players cannot exceed max players."),
		    responseDto.getErrors());
    }

    @Test
    @Order(1)
    public void testCreateGame() {
        GameCreationDto gameDto = new GameCreationDto(NAME, DESCRIPTION, MAX_PLAYERS, MIN_PLAYERS);
        ResponseEntity<GameResponseDto> response = client.postForEntity("/games", gameDto, GameResponseDto.class);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        GameResponseDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Catan", responseBody.getName());
        gameId = responseBody.getId();
    }

    @Test
    @Order(2)
    public void testGetGameById() {
        ResponseEntity<GameResponseDto> response = client.getForEntity("/games/" + gameId, GameResponseDto.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());

        GameResponseDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Catan", responseBody.getName());
    }

    @Test
    @Order(3)
    public void testGetGameById_Fail() {
        ResponseEntity<ErrorDto> response = client.getForEntity("/games/" + gameId+1, ErrorDto.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDto responseDto = response.getBody();
		assertNotNull(responseDto);
        assertIterableEquals(
		    List.of("Game does not exist"),
		    responseDto.getErrors());
    }

    @Test
    @Order(4)
    public void testGetAllGames() {
        ResponseEntity<GameResponseDto[]> response = client.getForEntity("/games", GameResponseDto[].class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());

        GameResponseDto[] responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.length > 0);
    }

    @Test
    @Order(5)
    public void testUpdateGame() {
        GameUpdateDto updateDto = new GameUpdateDto(null, null, null, null);
        updateDto.setDescription("Updated strategy game");
        HttpEntity<GameUpdateDto> requestEntity = new HttpEntity<>(updateDto);
        ResponseEntity<GameResponseDto> response = client.exchange("/games/" + gameId, HttpMethod.PUT, requestEntity, GameResponseDto.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        GameResponseDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Updated strategy game", responseBody.getDescription());
    }

    @Test
    @Order(6)
    public void testUpdateGame_Fail() {
        GameUpdateDto updateDto = new GameUpdateDto(null, null, null, null);
        updateDto.setDescription("Updated strategy game");
        HttpEntity<GameUpdateDto> requestEntity = new HttpEntity<>(updateDto);
        int invalidId = gameId+1;
        ResponseEntity<ErrorDto> response = client.exchange("/games/" + invalidId, HttpMethod.PUT, requestEntity, ErrorDto.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDto responseDto = response.getBody();
		assertNotNull(responseDto);
        assertIterableEquals(
		    List.of(String.format("No game has Id %d", invalidId)),
		    responseDto.getErrors());
    }

    @Test
    @Order(7)
    public void testDeleteGame_Fail() {
        int invalidId = gameId+1;
        ResponseEntity<ErrorDto> response = client.exchange(
            getApiUrl("/games/" + invalidId),
            HttpMethod.DELETE,
            null,
            ErrorDto.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDto responseDto = response.getBody();
		assertNotNull(responseDto);
        assertIterableEquals(
		    List.of(String.format("Game does not exist", invalidId)),
		    responseDto.getErrors());
    }

    @Test
    @Order(8)
    public void testDeleteGame() {
        client.delete(getApiUrl("/games/" + gameId));
        ResponseEntity<GameResponseDto> response = client.getForEntity("/games/" + gameId, GameResponseDto.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private String getApiUrl(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }
}
