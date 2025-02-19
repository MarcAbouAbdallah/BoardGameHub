package ca.mcgill.ecse321.boardgamehub.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.Review;

@SpringBootTest
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GameRepository gameRepo;

    private Player john;
    
    private Game monopoly;

    @BeforeEach
    public void setup() {
        john = new Player(
                            "John",
                            "john@email.com",
                            "johnnyboy123",
                            true);
        playerRepo.save(john);
        monopoly = new Game("Monopoly", 8, 2, "multiplayer economics-themed board game");
        gameRepo.save(monopoly);
    }

    @AfterEach
    public void clearDataBase() {
        reviewRepo.deleteAll();
        playerRepo.deleteAll();
        gameRepo.deleteAll();
    }

    @Test
    public void testCreateAndReadReview() {
        //Arrange
        Date today = Date.valueOf("2025-02-07");
        
        Review monopolyReview = new Review(
                                    9,
                                    "I won, but at what cost?",
                                    today,
                                    john,
                                    monopoly);
        monopolyReview = reviewRepo.save(monopolyReview);

        //Act
        Review monopolyReviewFromDb = reviewRepo.findReviewById(monopolyReview.getId());

        //Assert
        assertNotNull(monopolyReviewFromDb);
        assertEquals(monopolyReview.getId(), monopolyReviewFromDb.getId());
		assertEquals(monopolyReview.getRating(), monopolyReviewFromDb.getRating());
		assertEquals(monopolyReview.getComment(), monopolyReviewFromDb.getComment());
		assertEquals(monopolyReview.getDate(), monopolyReviewFromDb.getDate());
		assertEquals(monopolyReview.getReviewer().getId(), monopolyReviewFromDb.getReviewer().getId()); // For this and next test, unpredictable behaviour of assertequals on objects,
        assertEquals(monopolyReview.getGame().getId(), monopolyReviewFromDb.getGame().getId());     // asserted Id instead. What do?
    }

    @Test
    public void testUpdateReview() {
        //Arrange
        Date today = Date.valueOf("2025-02-07");
        
        Review monopolyReview = new Review(
                                    9,
                                    "I won, but at what cost?",
                                    today,
                                    john,
                                    monopoly);
        monopolyReview = reviewRepo.save(monopolyReview);

        monopolyReview.setComment("My friends hate me now");
        monopolyReview.setRating(1);
        monopolyReview.setDate(Date.valueOf("2025-02-15")); 
        monopolyReview = reviewRepo.save(monopolyReview);

        //Act
        Review monopolyReviewFromDb = reviewRepo.findReviewById(monopolyReview.getId());

        //Assert
        assertNotNull(monopolyReviewFromDb);
        assertEquals(monopolyReview.getId(), monopolyReviewFromDb.getId());
		assertEquals(monopolyReview.getRating(), monopolyReviewFromDb.getRating());
		assertEquals(monopolyReview.getComment(), monopolyReviewFromDb.getComment());
		assertEquals(monopolyReview.getDate(), monopolyReviewFromDb.getDate());
		assertEquals(monopolyReview.getReviewer().getId(), monopolyReviewFromDb.getReviewer().getId());
        assertEquals(monopolyReview.getGame().getId(), monopolyReviewFromDb.getGame().getId());
    }

    @Test
    public void testDeleteReview() {
        //Arrange
        Date today = Date.valueOf("2025-02-07");
        
        Review monopolyReview = new Review(
                                    9,
                                    "I won, but at what cost?",
                                    today,
                                    john,
                                    monopoly);
        monopolyReview = reviewRepo.save(monopolyReview);

        //Act
        reviewRepo.delete(monopolyReview);

        //Assert
        Review monopolyReviewFromDb = reviewRepo.findReviewById(monopolyReview.getId());
        assertNull(monopolyReviewFromDb);
    }

    @Test
    public void testNonExistentReview() {
        //Arrange
        //(done in before each)

        //Act
        List<Review> fakeMonopolyReviewFromDb = reviewRepo.findByReviewerAndGame(john, monopoly);

        //Assert
        assertEquals(0, fakeMonopolyReviewFromDb.size());
    }

    @Test
    public void testFindReviewByPlayer() {
        //Arrange
        Date today = Date.valueOf("2025-02-07");
        
        Review monopolyReview = new Review(
                                    9,
                                    "I won, but at what cost?",
                                    today,
                                    john,
                                    monopoly);
        monopolyReview = reviewRepo.save(monopolyReview);

        //Act
        List<Review> playerReviews = reviewRepo.findByReviewer(john);

        //Assert
        assertFalse(playerReviews.isEmpty());
        assertEquals(1, playerReviews.size());

        Review monopolyReviewFromDb = playerReviews.get(0);

        assertNotNull(monopolyReviewFromDb);
        assertEquals(monopolyReview.getId(), monopolyReviewFromDb.getId());
		assertEquals(monopolyReview.getRating(), monopolyReviewFromDb.getRating());
		assertEquals(monopolyReview.getComment(), monopolyReviewFromDb.getComment());
		assertEquals(monopolyReview.getDate(), monopolyReviewFromDb.getDate());
		assertEquals(monopolyReview.getReviewer().getId(), monopolyReviewFromDb.getReviewer().getId());
        assertEquals(monopolyReview.getGame().getId(), monopolyReviewFromDb.getGame().getId());
    }

    @Test
    public void testFindReviewByGame() {
        //Arrange
        Date today = Date.valueOf("2025-02-07");
        
        Review monopolyReview = new Review(
                                    9,
                                    "I won, but at what cost?",
                                    today,
                                    john,
                                    monopoly);
        monopolyReview = reviewRepo.save(monopolyReview);

        //Act
        List<Review> gameReviews = reviewRepo.findByGame(monopoly);

        //Assert
        assertFalse(gameReviews.isEmpty());
        assertEquals(1, gameReviews.size());

        Review monopolyReviewFromDb = gameReviews.get(0);

        assertNotNull(monopolyReviewFromDb);
        assertEquals(monopolyReview.getId(), monopolyReviewFromDb.getId());
		assertEquals(monopolyReview.getRating(), monopolyReviewFromDb.getRating());
		assertEquals(monopolyReview.getComment(), monopolyReviewFromDb.getComment());
		assertEquals(monopolyReview.getDate(), monopolyReviewFromDb.getDate());
		assertEquals(monopolyReview.getReviewer().getId(), monopolyReviewFromDb.getReviewer().getId());
        assertEquals(monopolyReview.getGame().getId(), monopolyReviewFromDb.getGame().getId());
    }

    @Test
    public void testFindReviewByPlayerAndGame() {
        //Arrange
        Date today = Date.valueOf("2025-02-07");
        
        Review monopolyReview = new Review(
                                    9,
                                    "I won, but at what cost?",
                                    today,
                                    john,
                                    monopoly);
        monopolyReview = reviewRepo.save(monopolyReview);

        //Act
        List<Review> playerGameReviews = reviewRepo.findByReviewerAndGame(john, monopoly);

        //Assert
        assertFalse(playerGameReviews.isEmpty());
        assertEquals(1, playerGameReviews.size());

        Review monopolyReviewFromDb = playerGameReviews.get(0);

        assertNotNull(monopolyReviewFromDb);
        assertEquals(monopolyReview.getId(), monopolyReviewFromDb.getId());
		assertEquals(monopolyReview.getRating(), monopolyReviewFromDb.getRating());
		assertEquals(monopolyReview.getComment(), monopolyReviewFromDb.getComment());
		assertEquals(monopolyReview.getDate(), monopolyReviewFromDb.getDate());
		assertEquals(monopolyReview.getReviewer().getId(), monopolyReviewFromDb.getReviewer().getId());
        assertEquals(monopolyReview.getGame().getId(), monopolyReviewFromDb.getGame().getId());
    }

    @Test
    public void testMultipleReviews() {
        //Arrange
        Date today = Date.valueOf("2025-02-07");
        Review monopolyReview = new Review(
                                    9,
                                    "I won, but at what cost?",
                                    today,
                                    john,
                                    monopoly);
        monopolyReview = reviewRepo.save(monopolyReview);

        Date yesterday = Date.valueOf("2025-02-06");
        Review monopolyReviewSecond = new Review(
                                    2,
                                    "I lost this time, which definitely means it's a bad game...",
                                    yesterday,
                                    john,
                                    monopoly);
        monopolyReviewSecond = reviewRepo.save(monopolyReviewSecond);

        //Act
        List<Review> reviewsFromDb = reviewRepo.findByReviewerAndGame(john, monopoly);

        //Assert
        assertEquals(2, reviewsFromDb.size());
    }
}
