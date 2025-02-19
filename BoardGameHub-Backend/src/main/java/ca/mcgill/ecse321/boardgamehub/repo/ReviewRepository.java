package ca.mcgill.ecse321.boardgamehub.repo;

import ca.mcgill.ecse321.boardgamehub.model.Review;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.Game;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ReviewRepository extends CrudRepository<Review, Integer> {
    public Review findReviewById(int id);
    public List<Review> findByReviewer(Player reviewer);
    public List<Review> findByGame(Game game);
    public List<Review> findByReviewerAndGame(Player reviewer, Game game);
}
