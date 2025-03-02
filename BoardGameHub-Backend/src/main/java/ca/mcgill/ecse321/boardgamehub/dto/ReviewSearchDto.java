package ca.mcgill.ecse321.boardgamehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class ReviewSearchDto {
    @PositiveOrZero(message = "Invalid reviewer Id")
    private int reviewerId;
    @NotBlank(message = "Game name must be entered")
    private String gameName;

    protected ReviewSearchDto() {

    }

    public ReviewSearchDto(int reviewerId, String gameName) {
        this.reviewerId = reviewerId;
        this.gameName = gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getGameName() {
        return gameName;
    }

    public int getReviewerId() {
        return reviewerId;
    }
}
