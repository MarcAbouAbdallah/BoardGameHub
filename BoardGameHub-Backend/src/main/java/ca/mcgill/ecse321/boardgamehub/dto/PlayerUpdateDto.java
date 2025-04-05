package ca.mcgill.ecse321.boardgamehub.dto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import org.springframework.http.HttpStatus;

public class PlayerUpdateDto {
    private String name;
    private String email;
    private String password;
    private Boolean isGameOwner;

    protected PlayerUpdateDto() {}

    public PlayerUpdateDto(String name, String email, String password, Boolean isGameOwner) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isGameOwner = isGameOwner;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public Boolean getIsGameOwner() {
        return isGameOwner;
    }
    public void setIsGameOwner(Boolean isGameOwner) {
        this.isGameOwner = isGameOwner;
    }

    public void validate(){
        if (name != null && name.isBlank()) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST,"Player name cannot be blank.");
        }
        if (email != null && (email.isBlank() || !email.contains("@"))) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST,"Email is invalid.");
        }
        if (password != null && (password.isBlank() || password.length() < 8)) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST,"Password must be at least 8 characters long.");
        }

    }

}
