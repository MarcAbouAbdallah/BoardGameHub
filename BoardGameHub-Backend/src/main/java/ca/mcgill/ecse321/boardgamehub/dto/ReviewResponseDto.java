package ca.mcgill.ecse321.boardgamehub.dto;

import java.sql.Date;

import ca.mcgill.ecse321.boardgamehub.model.Review;

public class ReviewResponseDto {
    private int id;
    private int rating;
    private String comment;
    private int reviewerId;
    private String gameName;
    private Date reviewDate;

    protected ReviewResponseDto() {

    }

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.reviewerId = review.getReviewer().getId();
        this.gameName = review.getGame().getName();
        this.reviewDate = review.getDate();
    }

    public static ReviewResponseDto fromReview(Review review) {
        ReviewResponseDto response = new ReviewResponseDto();
        response.id = review.getId();
        response.rating = review.getRating();
        response.comment = review.getComment();
        response.reviewerId = review.getReviewer().getId();
        response.gameName = review.getGame().getName();
        response.reviewDate = review.getDate();
        return response;
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

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }
}
