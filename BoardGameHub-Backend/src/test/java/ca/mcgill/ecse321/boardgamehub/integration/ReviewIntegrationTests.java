package ca.mcgill.ecse321.boardgamehub.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamehub.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewResponseDto;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.ReviewRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ReviewIntegrationTests {

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
        ResponseEntity<ReviewResponseDto> response = client.getForEntity(String.format("/reviews/%d", createdReviewId), ReviewResponseDto.class);

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
}
