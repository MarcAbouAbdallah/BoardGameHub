package ca.mcgill.ecse321.boardgamehub.integration;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamehub.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewResponseDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewUpdateDto;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.ReviewRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ReviewIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired 
    private GameRepository gameRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    private int createdReviewId;//What?

    private static final Player VALID_PLAYER = new Player("John", "john@mail.com", "jhKLEHGH75*(#$&", true);
    private static final Game VALID_GAME = new Game("Monopoly", 4, 2, "A board game about capitalism basically.");
    private static final int VALID_RATING = 9;
    private static final String VALID_COMMENT = "I have ruined my friend's day and couldn't be happier about it!";
    //private static final int VALID_PLAYER_ID = VALID_PLAYER.getId();//0;
    //private static final String VALID_GAME_NAME = "Monopoly";


    @BeforeAll
    public void setup() {
        playerRepo.save(VALID_PLAYER);
        gameRepo.save(VALID_GAME);
    }

    @AfterAll
    public void clearDataBase() {
        reviewRepo.deleteAll();
        playerRepo.deleteAll();
        gameRepo.deleteAll();
    }

    @Test
    @Order(0)
    public void testCreateValidReview() {
        //Arrange
        //Date date = Date.valueOf(LocalDate.now());
        ReviewCreationDto dto = new ReviewCreationDto(VALID_RATING, VALID_COMMENT, VALID_PLAYER.getId(), VALID_GAME.getName());

        //Act
        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/reviews", dto, ReviewResponseDto.class);

        //Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().getId() >= 0, "the ID should be a positive int");
		this.createdReviewId = response.getBody().getId();
		assertEquals(dto.getRating(), response.getBody().getRating());
		assertEquals(dto.getComment(), response.getBody().getComment());
        assertEquals(dto.getReviewerId(), response.getBody().getReviewerId());
        assertEquals(dto.getGameName(), response.getBody().getGameName());
		//assertEquals(date, response.getBody().getReviewDate());
        //No real way to get the date object for assertions since it is created in service... Will ask TA about this.
    }

    @Test
    @Order(1)
    public void testCreateInvalidReview() {
        //Arrange
        int invalidId = VALID_PLAYER.getId() + 1;
        ReviewCreationDto dto = new ReviewCreationDto(VALID_RATING, VALID_COMMENT, invalidId, VALID_GAME.getName());

        //Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/reviews", dto, ErrorDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(
		    List.of(String.format("There is no person with ID %d.", invalidId)),
		    response.getBody().getErrors());
    }

    @Test
    @Order(2)
    public void testGetValidReviewById() {
        //Arrange

        //Act
        String queryString = String.format("/reviews/%d", createdReviewId);
        ResponseEntity<ReviewResponseDto> response = client.getForEntity(queryString, ReviewResponseDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
		assertNotNull(response.getBody());
		assertTrue(response.getBody().getId() >= 0, "the ID should be a positive int");
		this.createdReviewId = response.getBody().getId();
		assertEquals(VALID_RATING, response.getBody().getRating());
		assertEquals(VALID_COMMENT, response.getBody().getComment());
        assertEquals(VALID_PLAYER.getId(), response.getBody().getReviewerId());
        assertEquals(VALID_GAME.getName(), response.getBody().getGameName());
        //assertEquals(Date.valueOf(LocalDate.now()), response.getBody().getReviewDate());
		//No real way to get the date for assertions since it is created in service... Will ask TA about this.
    }

    @Test
    @Order(3)
    public void testGetInvalidReviewById() {
        //Arrange
        int invalidId = createdReviewId + 1;

        //Act
        ResponseEntity<ErrorDto> response = client.getForEntity(String.format("/reviews/%d", invalidId), ErrorDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(
		    List.of(String.format("No review has Id %d", invalidId)),
		    response.getBody().getErrors());
    }

    @Test
    @Order(4)
    public void testGetValidReviewsByGame() {
        //Arrange

        //Act
        String queryString = String.format("/reviews/game/%s", VALID_GAME.getName());
        ResponseEntity<ReviewResponseDto[]> response = client.getForEntity(queryString, ReviewResponseDto[].class);
        ReviewResponseDto responseItem = response.getBody()[0];
        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
		assertNotNull(response.getBody());

		assertTrue(responseItem.getId() >= 0, "the ID should be a positive int");
		this.createdReviewId = responseItem.getId();
		assertEquals(VALID_RATING, responseItem.getRating());
		assertEquals(VALID_COMMENT, responseItem.getComment());
        assertEquals(VALID_PLAYER.getId(), responseItem.getReviewerId());
        assertEquals(VALID_GAME.getName(), responseItem.getGameName());
        //assertEquals(Date.valueOf(LocalDate.now()), response.getBody().getReviewDate());
		//No real way to get the date for assertions since it is created in service... Will ask TA about this.
    }

    @Test
    @Order(5)
    public void testGetInvalidReviewByGame() {
        //Act
        ResponseEntity<ErrorDto> response = client.getForEntity("/reviews/game/Risk", ErrorDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(
		    List.of("There is no game with name Risk."),
		    response.getBody().getErrors());
    }

    @Test
    @Order(6)
    public void testGetValidReviewByReviewerAndGame() {
        //Arrange

        //Act
        String queryString = String.format("http://localhost:" + port + "/reviews/game/%s/%d", VALID_GAME.getName(), VALID_PLAYER.getId());
        ResponseEntity<ReviewResponseDto[]> response = client.exchange(
            queryString,
            HttpMethod.GET,
            null,
            ReviewResponseDto[].class
        );
        ReviewResponseDto responseItem = response.getBody()[0];

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
		assertNotNull(response.getBody());

		assertTrue(responseItem.getId() >= 0, "the ID should be a positive int");
		this.createdReviewId = responseItem.getId();
		assertEquals(VALID_RATING, responseItem.getRating());
		assertEquals(VALID_COMMENT, responseItem.getComment());
        assertEquals(VALID_PLAYER.getId(), responseItem.getReviewerId());
        assertEquals(VALID_GAME.getName(), responseItem.getGameName());
        //assertEquals(Date.valueOf(LocalDate.now()), response.getBody().getReviewDate());
		//No real way to get the date for assertions since it is created in service... Will ask TA about this.
    }

    @Test
    @Order(7)
    public void testGetInvalidReviewByGameAndInvalidReviewer() {
        //Act
        ResponseEntity<ErrorDto> response = client.getForEntity(String.format("/reviews/game/%s/%d", VALID_GAME.getName(), VALID_PLAYER.getId()+1), ErrorDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(
		    List.of(String.format("There is no person with ID %d.",VALID_PLAYER.getId()+1)),
		    response.getBody().getErrors());
    }

    @Test
    @Order(8)
    public void testGetInvalidReviewByInvalidGameAndReviewer() {
        //Act
        ResponseEntity<ErrorDto> response = client.getForEntity(String.format("/reviews/game/%s/%d", "Risk", VALID_PLAYER.getId()), ErrorDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(
		    List.of("There is no game with name Risk."),
		    response.getBody().getErrors());
    }

    @Test
    @Order(9)
    public void testUpdateValidReview() {
        //Arrange
        ReviewUpdateDto dto = new ReviewUpdateDto(createdReviewId, 4, "NVM, I don't like the game now");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");  // This is just an example; adjust as needed.
        
        // Wrap the body (dto) and headers in HttpEntity
        HttpEntity<ReviewUpdateDto> httpEntity = new HttpEntity<>(dto, headers);
        
        ResponseEntity<ReviewResponseDto> response = client.exchange(
            "http://localhost:" + port + "/reviews",
            HttpMethod.PUT,
            httpEntity,
            ReviewResponseDto.class
        );

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
		assertNotNull(response.getBody());
		assertTrue(response.getBody().getId() >= 0, "the ID should be a positive int");
		this.createdReviewId = response.getBody().getId();
		assertEquals(4, response.getBody().getRating());
		assertEquals("NVM, I don't like the game now", response.getBody().getComment());
        assertEquals(VALID_PLAYER.getId(), response.getBody().getReviewerId());
        assertEquals(VALID_GAME.getName(), response.getBody().getGameName());
    }

    @Test
    @Order(10)
    public void testDeleteInvalidReview() {
        int invalidId = createdReviewId+1;
        ResponseEntity<ErrorDto> response = client.exchange(
            "http://localhost:"+port+"/reviews/"+invalidId,
            HttpMethod.DELETE,
            null,
            ErrorDto.class
        );

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(
		    List.of(String.format("No review has Id %d", invalidId)),
		    response.getBody().getErrors());
    }

    @Test
    @Order(11)
    public void testDeleteValidReview() {
        // Remove the review copy
        ResponseEntity<Void> removeResponse = client.exchange(
            "http://localhost:"+port+"/reviews/"+createdReviewId,
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertEquals(HttpStatus.OK, removeResponse.getStatusCode());
        
        ResponseEntity<ErrorDto> response = client.getForEntity(String.format("/reviews/%d", createdReviewId), ErrorDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(
		    List.of(String.format("No review has Id %d", createdReviewId)),
		    response.getBody().getErrors());
    }
}
