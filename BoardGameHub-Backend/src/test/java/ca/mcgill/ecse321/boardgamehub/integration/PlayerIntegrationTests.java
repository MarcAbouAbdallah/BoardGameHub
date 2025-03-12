package ca.mcgill.ecse321.boardgamehub.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;

import ca.mcgill.ecse321.boardgamehub.dto.PlayerCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.PlayerLoginDto;
import ca.mcgill.ecse321.boardgamehub.dto.PlayerResponseDto;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class PlayerIntegrationTests {
    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PlayerRepository playerRepo;

    private final String VALID_NAME = "John Doe";
    private final String VALID_EMAIL = "john.doe@gmail.com";
    private final String VALID_PASSWORD = "password";
    private final String INVALID_EMAIL = "john.doe@mail";
    private final String INVALID_PASSWORD = "Wrongpassword";
    private final int INVALID_ID = 0;
    private int VALID_ID;


    @BeforeAll
    @AfterAll
    public void clearDataBase() {
        playerRepo.deleteAll();
    }
    
    
    @Test
    @Order(0)
    public void testCreateValidPlayer() {
        //Arrange
        PlayerCreationDto request = new PlayerCreationDto(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);

        //Act
        ResponseEntity<PlayerResponseDto> response = client.postForEntity("/players", request, PlayerResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        PlayerResponseDto createdPlayer = response.getBody();
        VALID_ID = createdPlayer.getId();
        assertNotNull(createdPlayer);
        assertEquals(VALID_NAME, createdPlayer.getName());
        assertEquals(VALID_EMAIL, createdPlayer.getEmail());
        assertTrue(createdPlayer.getId() > 0, "Player ID should be greater than 0");
        
    }

    @Test
    @Order(1)
    public void testLoginValidPlayer() {
        //
        PlayerLoginDto request = new PlayerLoginDto(VALID_EMAIL, VALID_PASSWORD);

        //Act
        ResponseEntity<PlayerResponseDto> response = client.postForEntity("/players/login", request, PlayerResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PlayerResponseDto loggedInPlayer = response.getBody();
        assertNotNull(loggedInPlayer);
        assertEquals(VALID_NAME, loggedInPlayer.getName());
        assertEquals(VALID_EMAIL, loggedInPlayer.getEmail());
        assertEquals(VALID_ID, loggedInPlayer.getId());
        assertEquals(false, loggedInPlayer.getIsGameOwner());
    }

    @Test
    @Order(2)
    public void testLoginInvalidEmail() {
        //Arrange
        PlayerLoginDto request = new PlayerLoginDto(INVALID_EMAIL, VALID_PASSWORD);

        //Act
        ResponseEntity<PlayerResponseDto> response = client.postForEntity("/players/login", request, PlayerResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        PlayerResponseDto loggedInPlayer = response.getBody();
        assertNotNull(loggedInPlayer);
        assertEquals(0, loggedInPlayer.getId());
    }

    @Test
    @Order(3)
    public void testLoginInvalidPassword() {
        //Arrange
        PlayerLoginDto request = new PlayerLoginDto(VALID_EMAIL, INVALID_PASSWORD);

        //Act
        ResponseEntity<PlayerResponseDto> response = client.postForEntity("/players/login", request, PlayerResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        PlayerResponseDto loggedInPlayer = response.getBody();
        assertNotNull(loggedInPlayer);
        assertEquals(0, loggedInPlayer.getId());
    }

    @Test
    @Order(4)
    public void testRetrieveValidPlayer() {
        //Arrange
        PlayerCreationDto request = new PlayerCreationDto(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        ResponseEntity<PlayerResponseDto> response = client.postForEntity("/players", request, PlayerResponseDto.class);
        PlayerResponseDto createdPlayer = response.getBody();
        VALID_ID = createdPlayer.getId();

        //Act
        ResponseEntity<PlayerResponseDto> retrieveResponse = client.getForEntity("/players/" + VALID_ID, PlayerResponseDto.class);

        //Assert
        assertNotNull(retrieveResponse);
        assertEquals(HttpStatus.OK, retrieveResponse.getStatusCode());
        PlayerResponseDto retrievedPlayer = retrieveResponse.getBody();
        assertNotNull(retrievedPlayer);
        assertEquals(VALID_NAME, retrievedPlayer.getName());
        assertEquals(VALID_EMAIL, retrievedPlayer.getEmail());
        assertEquals(VALID_ID, retrievedPlayer.getId());
    }

    @Test
    @Order(5)
    public void testRetrieveInvalidPlayer() {
        //Arrange
        int invalidId = INVALID_ID;

        //Act
        ResponseEntity<PlayerResponseDto> retrieveResponse = client.getForEntity("/players/" + invalidId, PlayerResponseDto.class);

        //Assert
        assertNotNull(retrieveResponse);
        assertEquals(HttpStatus.NOT_FOUND, retrieveResponse.getStatusCode());
        PlayerResponseDto retrievedPlayer = retrieveResponse.getBody();
        assertNotNull(retrievedPlayer);
        assertEquals(0, retrievedPlayer.getId());
    }

    @Test
    @Order(6)
    public void testUpdateValidPlayer() {
        //Arrange
        String newName = "Jane Doe";
        String newEmail = "jane.doe@mail.mcgill.ca";
        String newPassword = "newpassword";

        PlayerCreationDto request = new PlayerCreationDto(newName, newEmail, newPassword, true); 

        //Act
        ResponseEntity<PlayerResponseDto> response = client.exchange("/players/" + VALID_ID, HttpMethod.PUT, new HttpEntity<>(request), PlayerResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PlayerResponseDto updatedPlayer = response.getBody();
        assertNotNull(updatedPlayer);
        assertEquals(newName, updatedPlayer.getName());
        assertEquals(newEmail, updatedPlayer.getEmail());
        assertEquals(VALID_ID, updatedPlayer.getId());
        assertEquals(true, updatedPlayer.getIsGameOwner());
    }

    @Test
    @Order(7)
    public void testUpdateInvalidPlayer() {
        //Arrange
        int invalidId = INVALID_ID;
        String newName = "Jane Doe";
        String newEmail = "jane.doe@mail.mcgill.ca";
        String newPassword = "newpassword";
        PlayerCreationDto request = new PlayerCreationDto(newName, newEmail, newPassword, true);

        //Act
        ResponseEntity<PlayerResponseDto> response = client.exchange("/players/" + invalidId, HttpMethod.PUT, new HttpEntity<>(request), PlayerResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(8)
    public void testDeleteValidPlayer() {
        //Act
        ResponseEntity<PlayerResponseDto> response = client.exchange("/players/" + VALID_ID, HttpMethod.DELETE, null, PlayerResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @Order(9)
    public void testDeleteInvalidPlayer() {
        //Arrange
        int invalidId = INVALID_ID;

        //Act
        ResponseEntity<PlayerResponseDto> response = client.exchange("/players/" + invalidId, HttpMethod.DELETE, null, PlayerResponseDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
