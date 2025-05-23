package ca.mcgill.ecse321.boardgamehub.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import ca.mcgill.ecse321.boardgamehub.dto.ReviewCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewResponseDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewUpdateDto;
import ca.mcgill.ecse321.boardgamehub.model.Review;
import ca.mcgill.ecse321.boardgamehub.service.ReviewService;

@CrossOrigin(origins = "*")
@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * Find a specific review by its unique Id
     * 
     * @param id The primary key of the review to find
     * @return The review DTO for the review with that id
     */
    @GetMapping("reviews/{id}")
    public ReviewResponseDto findReviewById(@PathVariable int id) {
        Review review = reviewService.findReviewById(id);
        return new ReviewResponseDto(review);
    }

    /**
     * Find all reviews for a particular game
     * 
     * @param gameName The name of the game whose reviews are wanted
     * @return A list of review response DTO's containing the reviews for that game
     */
    @GetMapping("reviews/game/{gameName}")
    public List<ReviewResponseDto> findByGame(@PathVariable String gameName) {
        List<Review> reviewList = reviewService.findByGame(gameName);
        List<ReviewResponseDto> dtoList = new ArrayList<>();
        for (Review review: reviewList) {
            dtoList.add(new ReviewResponseDto(review));
        }
        return dtoList;
    }

    /**
     * Find all reviews by a particular player
     * 
     * @param reviewerId The id of the player whose reviews we are looking for
     * @return A list of review response DTO's containing the reviews created by that player
     */
    @GetMapping("reviews/player/{reviewerId}")
    public List<ReviewResponseDto> findByReviewer(@PathVariable int reviewerId) {
        List<Review> reviewList = reviewService.findByReviewer(reviewerId);
        List<ReviewResponseDto> dtoList = new ArrayList<>();
        for (Review review: reviewList) {
            dtoList.add(new ReviewResponseDto(review));
        }
        return dtoList;
    }

    /**
     * Find all reviews for a particular game by a particular person
     * 
     * @param gameName The name of the game whose reviews we are looking for
     * @param reviewerId The id of the reviewer whose reviews we want
     * @return A list of review response DTO's containing the reviews for that game made by the person with that id
     */
    //Somehow Returning a list in the same way the other endpoints do errors out.
    //This is why it is coded like this.
    @GetMapping("reviews/game/{gameName}/{reviewerId}")
    public ResponseEntity<List<ReviewResponseDto>> findByGameAndReviewer(@PathVariable String gameName, @PathVariable int reviewerId) {
        List<Review> reviews = reviewService.findByReviewerAndGame(gameName, reviewerId);
        List<ReviewResponseDto> responses = reviews.stream()
            .map(ReviewResponseDto::fromReview)
            .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * Create a review for a particular game
     *
     * @return The created review in a response DTO, including their autogenerated Id
     */
    
    @PostMapping("reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponseDto createReview(@RequestBody ReviewCreationDto reviewToCreate) {
        Review createdReview = reviewService.createReview(reviewToCreate);
        return new ReviewResponseDto(createdReview);
    }

    /**
     * Edit a particular review
     * 
     * @param reviewToEdit The unique primary key id of the review to edit
     * @return The edited review in a DTO, including their autogenerated id
     */
    @PutMapping("reviews/{reviewId}")
    public ReviewResponseDto editReview(@PathVariable int reviewId, @RequestParam int userId, @RequestBody ReviewUpdateDto editedReview) {
        Review changedReview = reviewService.editReview(reviewId, userId, editedReview);
        return new ReviewResponseDto(changedReview);
    }

    /**
     * Delete a particular review
     * 
     * @param id The primary key id of the review to delete
     * @return void
     */
    @DeleteMapping("reviews/{reviewId}")
    public void deleteReview(@PathVariable int reviewId, @RequestParam int userId) {
        reviewService.deleteReview(reviewId, userId);
    }
}
