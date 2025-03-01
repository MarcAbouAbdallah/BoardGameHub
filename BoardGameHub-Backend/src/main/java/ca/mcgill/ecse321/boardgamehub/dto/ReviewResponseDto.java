package ca.mcgill.ecse321.boardgamehub.dto;

import ca.mcgill.ecse321.boardgamehub.model.Review;

public class ReviewResponseDto {
    private int id;
    private int rating;
    private String comment;
    private int reviewerId;
    private String gameName;

    protected ReviewResponseDto() {

    }

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.reviewerId = review.getReviewer().getId();
        this.gameName = review.getGame().getName();
    }

    public int getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public String getGameName() {
        return gameName;
    }
}
