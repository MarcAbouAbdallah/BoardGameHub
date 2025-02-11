package ca.mcgill.ecse321.boardgamehub.repo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.Game;
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
        monopoly = new Game(); //This will have to be edited when game gets implemented
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
}
