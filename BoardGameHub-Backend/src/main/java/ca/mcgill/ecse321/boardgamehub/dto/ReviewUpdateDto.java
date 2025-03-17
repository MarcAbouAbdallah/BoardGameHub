package ca.mcgill.ecse321.boardgamehub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class ReviewUpdateDto {
    @PositiveOrZero(message = "Rating must be between 0 and 10")
    @Max(value = 10, message = "Rating must be between 0 and 10")
    private int rating;
    @NotBlank(message = "Comment for review must be entered")
    private String comment;

    protected ReviewUpdateDto() {   
    }

    public ReviewUpdateDto(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }

}
