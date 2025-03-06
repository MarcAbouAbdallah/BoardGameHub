package ca.mcgill.ecse321.boardgamehub.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.boardgamehub.dto.ReviewCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewSearchDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewUpdateDto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.ReviewRepository;
import ca.mcgill.ecse321.boardgamehub.model.Review;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired
    private PlayerRepository playerRepo;
    @Autowired
    private GameRepository gameRepo;

    @Transactional//Is this really necessary in this case?(Tut reference)
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

    public Review findReviewById(int id) {
        Review review = reviewRepo.findReviewById(id);
        if (review == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No review has Id %d", id));
        }
        return review;
    }

    public List<Review> findByReviewerAndGame(@Valid ReviewSearchDto dto) {
        Player reviewer = playerRepo.findPlayerById(dto.getReviewerId());
        if (reviewer == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no person with ID %d.",
                                    dto.getReviewerId()));
        }

        Game game = gameRepo.findGameByName(dto.getGameName());
        if (game == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no game with name %s.",
                                    dto.getGameName()));
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

    public Review editReview(@Valid ReviewUpdateDto editedReview) {

        Date today = Date.valueOf(LocalDate.now());

        Review review = reviewRepo.findReviewById(editedReview.getId());

        if (review == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No review has Id %d", editedReview.getId()));
        }

        review.setRating(editedReview.getRating());
        review.setComment(editedReview.getComment());
        review.setDate(today);

        return reviewRepo.save(review);
    }
}
