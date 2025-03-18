package ca.mcgill.ecse321.boardgamehub.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

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

import ca.mcgill.ecse321.boardgamehub.dto.EventResponseDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameResponseDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameUpdateDto;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
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
    @Order(1)
    public void testCreateGame() {
        GameCreationDto gameDto = new GameCreationDto(NAME, DESCRIPTION, MAX_PLAYERS, MIN_PLAYERS);
        ResponseEntity<GameResponseDto> response = client.postForEntity("/games", gameDto, GameResponseDto.class);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Catan", response.getBody().getName());
        gameId = response.getBody().getId();
    }

    @Test
    @Order(2)
    public void testGetGameById() {
        ResponseEntity<GameResponseDto> response = client.getForEntity("/games/" + gameId, GameResponseDto.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Catan", response.getBody().getName());
    }

    @Test
    @Order(3)
    public void testGetAllGames() {
        ResponseEntity<GameResponseDto[]> response = client.getForEntity("/games", GameResponseDto[].class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @Order(4)
    public void testUpdateGame() {
        GameUpdateDto updateDto = new GameUpdateDto(null, null, null, null);
        updateDto.setDescription("Updated strategy game");
        HttpEntity<GameUpdateDto> requestEntity = new HttpEntity<>(updateDto);
        ResponseEntity<GameResponseDto> response = client.exchange("/games/" + gameId, HttpMethod.PUT, requestEntity, GameResponseDto.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated strategy game", response.getBody().getDescription());
    }

    @Test
    @Order(5)
    public void testDeleteGame() {
        client.delete(getApiUrl("/games/" + gameId));
        ResponseEntity<GameResponseDto> response = client.getForEntity("/games/" + gameId, GameResponseDto.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private String getApiUrl(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }
}
