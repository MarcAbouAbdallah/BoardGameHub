package ca.mcgill.ecse321.boardgamehub.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamehub.dto.ReviewCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewUpdateDto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.Review;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.ReviewRepository;


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
        Date date = Date.valueOf(LocalDate.now());
        
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
        assertEquals(date, createdReview.getDate());
        //Should there be an assertion for the date as it is created autonomously in the service...??
    }

    @Test
    public void testCreateReviewInvalidGame() {
        
        when(mockPlayerRepo.findPlayerById(0))
        .thenReturn(VALID_PLAYER);

        ReviewCreationDto dto = new ReviewCreationDto(VALID_RATING, VALID_COMMENT, VALID_PLAYER_ID, "Risk");

        //Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class,
                                               () -> reviewService.createReview(dto));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("There is no game with name Risk.", e.getMessage());
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
        //Arrange
        Date date = Date.valueOf(LocalDate.now());
        List<Review> reviewList = new ArrayList<>();
        reviewList.add(new Review(VALID_RATING, VALID_COMMENT, date, VALID_PLAYER, VALID_GAME));

        when(mockPlayerRepo.findPlayerById(0))
        .thenReturn(VALID_PLAYER);

        when(mockGameRepo.findGameByName("Monopoly"))
        .thenReturn(VALID_GAME);

        when(mockReviewRepo.findByReviewerAndGame(VALID_PLAYER, VALID_GAME))
        .thenReturn(reviewList);

        //ReviewSearchDto dto = new ReviewSearchDto(VALID_PLAYER_ID, VALID_GAME_NAME);

        //Act
        List<Review> foundReviewList = reviewService.findByReviewerAndGame(VALID_GAME_NAME, VALID_PLAYER_ID);
        Review foundReview = foundReviewList.get(0);

        //Assert
        assertNotNull(foundReviewList);
        assertEquals(1, foundReviewList.size());
        assertNotNull(foundReview);
        assertEquals(VALID_RATING, foundReview.getRating());
        assertEquals(VALID_COMMENT, foundReview.getComment());
        assertEquals(VALID_PLAYER_ID, foundReview.getReviewer().getId());
        assertEquals(VALID_GAME_NAME, foundReview.getGame().getName());
        assertEquals(date, foundReview.getDate());
    }

    @Test
    public void testFindReviewByInvalidReviewerAndValidGame() {
        //Arrange
        /**
         * By default repo returns null on invalid search.
         * Game repo mock is not necessary to be implemented
         * as exception will be thrown when player is not found and
         * gameRepo mock will neve be reached. (Review service looks
         * for player first and then game)
        */

        //ReviewSearchDto dto = new ReviewSearchDto(1588809,VALID_GAME_NAME);

        //Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class,
                                               () -> reviewService.findByReviewerAndGame(VALID_GAME_NAME, 1588809));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("There is no person with ID 1588809.", e.getMessage());
    }

    @Test
    public void testFindReviewByValidReviewerAndInvalidGame() {
        //Arrange
        //By default repo returns null on invalid search.
        when(mockPlayerRepo.findPlayerById(0))
        .thenReturn(VALID_PLAYER);

        //ReviewSearchDto dto = new ReviewSearchDto(VALID_PLAYER.getId(),"Risk");

        //Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class,
                                               () -> reviewService.findByReviewerAndGame("Risk", VALID_PLAYER.getId()));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("There is no game with name Risk.", e.getMessage());
    }

    @Test
    public void testFindReviewByValidGame() {
        //Arrange
        Date date = Date.valueOf(LocalDate.now());

        List<Review> reviewList = new ArrayList<>();
        reviewList.add(new Review(VALID_RATING, VALID_COMMENT, date, VALID_PLAYER, VALID_GAME));

        when(mockGameRepo.findGameByName("Monopoly"))
        .thenReturn(VALID_GAME);

        when(mockReviewRepo.findByGame(VALID_GAME))
        .thenReturn(reviewList);

        //Act
        List<Review> foundReviewList = reviewService.findByGame(VALID_GAME.getName());
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
    public void testFindByInvalidGame() {
        //Arrange
        /**
         * By default repo returns null on invalid search.
        */

        //Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class,
                                               () -> reviewService.findByGame("Risk"));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("There is no game with name Risk.", e.getMessage());
    }

    @Test
    public void testUpdateReview() {
        //Arrange
        Date date = Date.valueOf(LocalDate.now());
        ReviewUpdateDto dto = new ReviewUpdateDto(4, "NVM, I don't like the game now");

        when(mockReviewRepo.findReviewById(0))
        .thenReturn(new Review(VALID_RATING, VALID_COMMENT, date, VALID_PLAYER, VALID_GAME));

        when(mockReviewRepo.save(any(Review.class)))
        .thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

        //Act
        Review editedReview = reviewService.editReview(0, dto);

        //Assert
        assertNotNull(editedReview);
        assertEquals(4, editedReview.getRating());
        assertEquals("NVM, I don't like the game now", editedReview.getComment());
        assertEquals(VALID_PLAYER_ID, editedReview.getReviewer().getId());
        assertEquals(VALID_GAME_NAME, editedReview.getGame().getName());
        assertEquals(date, editedReview.getDate());
    }

    @Test
    public void testUpdateInvalidReview() {
        //Arrange
        //By default repo returns null on invalid search
        ReviewUpdateDto dto = new ReviewUpdateDto(4, "NVM, I don't like the game now");

        //Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class,
                                               () -> reviewService.editReview(156678, dto));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("No review has Id 156678", e.getMessage());
    }

    @Test
    public void testDeleteReview() {
        //Arrange
        Date date = Date.valueOf(LocalDate.now());
        when(mockReviewRepo.findReviewById(0))
        .thenReturn(new Review(VALID_RATING, VALID_COMMENT, date, VALID_PLAYER, VALID_GAME));

        //Act & Assert
        assertDoesNotThrow(() -> reviewService.deleteReview(0));
        verify(mockReviewRepo, times(1)).delete(any());
    }

    @Test
    public void testDeleteInvalidReview() {
        BoardGameHubException e = assertThrows(BoardGameHubException.class,
                                               () -> reviewService.deleteReview(15));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("No review has Id 15", e.getMessage());
    }
}
