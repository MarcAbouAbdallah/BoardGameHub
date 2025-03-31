package ca.mcgill.ecse321.boardgamehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public class PlayerCreationDto {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    @Size(min = 8, message = "Password length must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String password;
    private boolean isGameOwner; 

    protected PlayerCreationDto() {}

    public PlayerCreationDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isGameOwner = false;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean getIsGameOwner() {
        return isGameOwner;
    }
}