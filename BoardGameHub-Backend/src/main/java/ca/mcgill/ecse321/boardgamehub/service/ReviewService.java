package ca.mcgill.ecse321.boardgamehub.service;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.boardgamehub.dto.ReviewCreationDto;
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


}
