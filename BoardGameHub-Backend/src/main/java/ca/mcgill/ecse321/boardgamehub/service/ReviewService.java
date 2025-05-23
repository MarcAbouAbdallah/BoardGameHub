package ca.mcgill.ecse321.boardgamehub.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import ca.mcgill.ecse321.boardgamehub.dto.ReviewCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewUpdateDto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.Review;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.ReviewRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Validated
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired
    private PlayerRepository playerRepo;
    @Autowired
    private GameRepository gameRepo;

    @Transactional
    public Review createReview(@Valid ReviewCreationDto reviewToCreate) {

        Date today = Date.valueOf(LocalDate.now());

        Player reviewer = playerRepo.findPlayerById(reviewToCreate.getReviewerId());
        if (reviewer == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no person with ID %d.",
                                            reviewToCreate.getReviewerId()));
        }

        Game game = gameRepo.findGameByName(reviewToCreate.getGameName());
        if (game == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no game with name %s.",
                                            reviewToCreate.getGameName()));
        }

        Review review = new Review(
                                    reviewToCreate.getRating(),
                                    reviewToCreate.getComment(),
                                    today,
                                    reviewer,
                                    game);

        return reviewRepo.save(review);
    }
    
    @Transactional
    public Review editReview(int reviewId, int userId, @Valid ReviewUpdateDto editedReview) {

        Date today = Date.valueOf(LocalDate.now());

        Review review = reviewRepo.findReviewById(reviewId);

        if (review == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No review has Id %d", reviewId));
        }

        if (review.getReviewer().getId() != userId) {
            throw new BoardGameHubException(HttpStatus.UNAUTHORIZED, 
                                            "Request sender is not the creator of this review.");
        }

        review.setRating(editedReview.getRating());
        review.setComment(editedReview.getComment());
        review.setDate(today);

        return reviewRepo.save(review);
    }

    @Transactional
    public void deleteReview(int reviewId, int userId) {
        Review review = reviewRepo.findReviewById(reviewId);

        if (review == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No review has Id %d", reviewId));
        }

        if (review.getReviewer().getId() != userId) {
            throw new BoardGameHubException(HttpStatus.UNAUTHORIZED, 
                                            "Request sender is not the creator of this review.");
        }

        reviewRepo.delete(review);
    }

    public Review findReviewById(int id) {
        Review review = reviewRepo.findReviewById(id);
        if (review == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No review has Id %d", id));
        }
        return review;
    }

    public List<Review> findByReviewerAndGame(String gameName, int reviewerId) {
        Player reviewer = playerRepo.findPlayerById(reviewerId);
        if (reviewer == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no person with ID %d.",
                                    reviewerId));
        }

        Game game = gameRepo.findGameByName(gameName);
        if (game == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no game with name %s.",
                                    gameName));
        }
        return reviewRepo.findByReviewerAndGame(reviewer, game);
    }

    public List<Review> findByGame(String gameName) {
        Game game = gameRepo.findGameByName(gameName);
        if (game == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no game with name %s.",
                                    gameName));
        }
        return reviewRepo.findByGame(game);
    }

    public List<Review> findByReviewer(int reviewerId) {
        Player reviewer = playerRepo.findPlayerById(reviewerId);
        if (reviewer == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no person with ID %d.",
                                    reviewerId));
        }
        return reviewRepo.findByReviewer(reviewer);
    }
}
