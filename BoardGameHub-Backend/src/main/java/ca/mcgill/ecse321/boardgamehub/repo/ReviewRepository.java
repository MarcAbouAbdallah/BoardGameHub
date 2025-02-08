package ca.mcgill.ecse321.boardgamehub.repo;

import org.springframework.data.repository.CrudRepository;
import ca.mcgill.ecse321.boardgamehub.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Integer> {
    public Review findReviewById(int id);
}
