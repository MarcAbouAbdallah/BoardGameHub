package ca.mcgill.ecse321.boardgamehub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class PlayerLoginDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    @Size(min = 8, message = "Password length must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String password;

    public PlayerLoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    
}
