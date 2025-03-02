package ca.mcgill.ecse321.boardgamehub.service;

import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import org.mockito.quality.Strictness;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import ca.mcgill.ecse321.boardgamehub.repo.ReviewRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.model.Review;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewCreationDto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.Game;


@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ReviewServiceTests {
    @Mock
    private ReviewRepository mockReviewRepo;
    @Mock
    private PlayerRepository mockPlayerRepo;
    @Mock
    private GameRepository mockGameRepo;
    @InjectMocks
    private ReviewService reviewService;

    private static final int VALID_RATING = 9;
    private static final String VALID_COMMENT = "I have ruined my friend's day and couldn't be happier about it!";
    private static final int VALID_PLAYER_ID = 0;
    private static final String VALID_GAME_NAME = "Monopoly";

    private static final Player VALID_PLAYER = new Player("John", "john@mail.com", "jhKLEHGH75*(#$&", true);
    private static final Game VALID_GAME = new Game("Monopoly", 4, 2, "A board game about capitalism basically.");

    @Test
    public void testCreateValidReview() {
        //Arrange
        when(mockPlayerRepo.findPlayerById(0))
        .thenReturn(VALID_PLAYER);

        when(mockGameRepo.findGameByName("Monopoly"))
        .thenReturn(VALID_GAME);

        when(mockReviewRepo.save(any(Review.class)))
        .thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

        ReviewCreationDto dto = new ReviewCreationDto(VALID_RATING, VALID_COMMENT, VALID_PLAYER_ID, VALID_GAME_NAME);

        //Act
        Review createdReview = reviewService.createReview(dto);

        //Assert
        assertNotNull(createdReview);
        assertEquals(VALID_RATING, createdReview.getRating());
        assertEquals(VALID_COMMENT, createdReview.getComment());
        assertEquals(VALID_PLAYER_ID, createdReview.getReviewer().getId());
        assertEquals(VALID_GAME_NAME, createdReview.getGame().getName());
        //Should there be an asertion for the date as it is created autonomously in the service...??
    }

    @Test
    public void testFindReviewByValidId() {
        //Arrange
        Date date = Date.valueOf(LocalDate.now());
        when(mockReviewRepo.findReviewById(0))
        .thenReturn(new Review(VALID_RATING, VALID_COMMENT, date, VALID_PLAYER, VALID_GAME));

        //Act
        Review foundReview = mockReviewRepo.findReviewById(0);

        //Assert
        assertNotNull(foundReview);
        assertEquals(VALID_RATING, foundReview.getRating());
        assertEquals(VALID_COMMENT, foundReview.getComment());
        assertEquals(VALID_PLAYER_ID, foundReview.getReviewer().getId());
        assertEquals(VALID_GAME_NAME, foundReview.getGame().getName());
        assertEquals(date, foundReview.getDate());
    }

    @Test
    public void testFindReviewByInvalidId() {
        //Arrange
        //By default repo returns null on invalid search

        //Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class,
                                               () -> reviewService.findReviewById(1234));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("No review has Id 1234", e.getMessage());
    }

    @Test
    public void testFindReviewByValidReviewerAndGame() {
        //TODO IMPLEMENT THIS PROPERLY (NOT USING RIGHT SERVICE METHOD ATM)
        fail();
        //Arrange
        Date date = Date.valueOf(LocalDate.now());
        List<Review> reviewList = new ArrayList<Review>();
        reviewList.add(new Review(VALID_RATING, VALID_COMMENT, date, VALID_PLAYER, VALID_GAME));

        when(mockReviewRepo.findByReviewerAndGame(VALID_PLAYER, VALID_GAME))
        .thenReturn(reviewList);

        //Act
        List<Review> foundReviewList = mockReviewRepo.findByReviewerAndGame(VALID_PLAYER, VALID_GAME);
        Review foundReview = foundReviewList.get(0);

        //Assert
        assertNotNull(foundReview);
        assertEquals(VALID_RATING, foundReview.getRating());
        assertEquals(VALID_COMMENT, foundReview.getComment());
        assertEquals(VALID_PLAYER_ID, foundReview.getReviewer().getId());
        assertEquals(VALID_GAME_NAME, foundReview.getGame().getName());
        assertEquals(date, foundReview.getDate());
    }

    @Test
    public void findReviewByInvalidReviewerAndValidGame() {
        fail();
    }
}
