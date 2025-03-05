package ca.mcgill.ecse321.boardgamehub.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public class ReviewCreationDto {
    @PositiveOrZero(message = "Rating must be between 0 and 10")
    @Max(value = 10, message = "Rating must be between 0 and 10")
    private int rating;
    @NotBlank(message = "Comment for review must be entered")
    private String comment;
    @PositiveOrZero(message = "Invalid reviewer Id")
    private int reviewerId;
    @NotBlank(message = "Game name must be entered")
    private String gameName;

    protected ReviewCreationDto() {   
    }

    public ReviewCreationDto(int rating, String comment, int reviewerId, String gameName) {
        this.rating = rating;
        this.comment = comment;
        this.reviewerId = reviewerId;
        this.gameName = gameName;
    }
    
    public String getComment() {
        return comment;
    }
    
    public String getGameName() {
        return gameName;
    }
    
    public int getRating() {
        return rating;
    }
    
    public int getReviewerId() {
        return reviewerId;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }
}
