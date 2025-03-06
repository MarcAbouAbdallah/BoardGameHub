package ca.mcgill.ecse321.boardgamehub.dto;

import ca.mcgill.ecse321.boardgamehub.model.Player;


public class PlayerResponseDto {
    private int id;
    private String name;
    private String email;
    private boolean isGameOwner;

    @SuppressWarnings("unused")
    private PlayerResponseDto() {
    }

    public PlayerResponseDto(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.email = player.getEmail();
        this.isGameOwner = player.getIsGameOwner();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean getIsGameOwner() {
        return isGameOwner;
    }
}
