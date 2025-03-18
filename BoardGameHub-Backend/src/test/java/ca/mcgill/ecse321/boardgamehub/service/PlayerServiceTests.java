package ca.mcgill.ecse321.boardgamehub.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;

import ca.mcgill.ecse321.boardgamehub.dto.PlayerCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.PlayerLoginDto;
import ca.mcgill.ecse321.boardgamehub.dto.PlayerUpdateDto;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;

import ca.mcgill.ecse321.boardgamehub.model.Player;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PlayerServiceTests {
    @Mock
    private PlayerRepository mockPlayerRepository;

    @InjectMocks
    private PlayerService playerService;

    private static final String VALID_NAME = "John Doe";
    private static final String VALID_EMAIL = "john.doe@mail.mcgill.ca";
    private static final String VALID_PASSWORD = "password123";

   
    @Test
    public void testRegisterValidPlayer() {
        // Arrange
        PlayerCreationDto John = new PlayerCreationDto(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        when(mockPlayerRepository.save(any(Player.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

        // Act
        Player createdPlayer = playerService.registerPlayer(John);

        // Assert
        assertNotNull(createdPlayer);
        assertEquals(VALID_NAME, createdPlayer.getName());
        assertEquals(VALID_EMAIL, createdPlayer.getEmail());
        assertEquals(VALID_PASSWORD, createdPlayer.getPassword());

        verify(mockPlayerRepository, times(1)).save(any(Player.class));
    }

    @Test
    public void testRegisterAlreadyExistingEmailPlayer() {
        // Arrange
        PlayerCreationDto John = new PlayerCreationDto(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
        Player existingPlayer = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        when(mockPlayerRepository.findAll()).thenReturn(java.util.List.of(existingPlayer));

        // Act and Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            playerService.registerPlayer(John);
        });
        assertEquals(HttpStatus.CONFLICT, e.getStatus());
        assertEquals("Email already in use", e.getMessage());
    }



    @Test
    public void testLogin() {
        // Arrange
        Player John = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        PlayerLoginDto dto = new PlayerLoginDto(VALID_EMAIL, VALID_PASSWORD);
        when(mockPlayerRepository.findPlayerByEmail(VALID_EMAIL)).thenReturn(John);


        // Act
        Player loggedInPlayer = playerService.login(dto);

        // Assert
        assertNotNull(loggedInPlayer);
        assertEquals(VALID_NAME, loggedInPlayer.getName());
        assertEquals(VALID_EMAIL, loggedInPlayer.getEmail());
        assertEquals(VALID_PASSWORD, loggedInPlayer.getPassword());
        
    }

    @Test
    public void testLoginInvalidEmail() {
        // Arrange
        PlayerLoginDto dto = new PlayerLoginDto(VALID_EMAIL, VALID_PASSWORD);
        when(mockPlayerRepository.findPlayerByEmail(VALID_EMAIL)).thenReturn(null);

        // Act and Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            playerService.login(dto);
        });
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Incorrect email or account does not exist", e.getMessage());
    }

    @Test
    public void testLoginInvalidPassword() {
        // Arrange
        Player John = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        PlayerLoginDto dto = new PlayerLoginDto(VALID_EMAIL, "wrongpassword");
        when(mockPlayerRepository.findPlayerByEmail(VALID_EMAIL)).thenReturn(John);

        // Act and Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            playerService.login(dto);
        });
        assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        assertEquals("Invalid password", e.getMessage());
    }



    @Test
    public void testDeletePlayer() {
        //Arrange
        int id = 0;
        Player John = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        when(mockPlayerRepository.findPlayerById(id)).thenReturn(John);

        //Act
        playerService.deletePlayer(id);

        //Assert
        verify(mockPlayerRepository, times(1)).delete(John);
        
    }

    @Test
    public void testDeleteInvalidPlayer() {
        //Arrange
        int id = 0;
        when(mockPlayerRepository.findPlayerById(id)).thenReturn(null);

        //Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            playerService.deletePlayer(id);
        });
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Player does not exist", e.getMessage());
    }



    @Test
    public void testRetrievePlayer() {
        //Arrange
        int id = 0;
        Player John = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        when(mockPlayerRepository.findPlayerById(id)).thenReturn(John);

        //Act
        Player retrievedPlayer = playerService.getPlayerById(id);

        //Assert
        assertNotNull(retrievedPlayer);
        assertEquals(VALID_NAME, retrievedPlayer.getName());
        assertEquals(VALID_EMAIL, retrievedPlayer.getEmail());
        assertEquals(VALID_PASSWORD, retrievedPlayer.getPassword());
        
    }

    @Test
    public void testRetrieveInvalidPlayer() {
        //Arrange
        int id = 0;
        when(mockPlayerRepository.findPlayerById(id)).thenReturn(null);

        //Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            playerService.getPlayerById(id);
        });
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Player does not exist", e.getMessage());
    }

    @Test
    public void testUpdatePlayer() {
        //Arrange
        int id = 0;
        Player John = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        when(mockPlayerRepository.findPlayerById(id)).thenReturn(John);
        when(mockPlayerRepository.save(any(Player.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

        PlayerUpdateDto updatedJohn = new PlayerUpdateDto("Jane Doe", "jane.doe@mail.mcgill.ca", "password456", true);

        //Act
        Player updatedPlayer = playerService.updatePlayer(id, updatedJohn);

        //Assert
        assertNotNull(updatedPlayer);
        assertEquals("Jane Doe", updatedPlayer.getName());
        assertEquals("jane.doe@mail.mcgill.ca", updatedPlayer.getEmail());
        assertEquals("password456", updatedPlayer.getPassword());

    }

    @Test
    public void testUpdateInvalidPlayer() {
        //Arrange
        int id = 0;
        when(mockPlayerRepository.findPlayerById(id)).thenReturn(null);

        PlayerUpdateDto updatedJohn = new PlayerUpdateDto("Jane Doe", "jane.doe@mail.mcgill.ca", "password456", true);

        //Act & Assert
        BoardGameHubException e = assertThrows(BoardGameHubException.class, () -> {
            playerService.updatePlayer(id, updatedJohn);
        });
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Player does not exist", e.getMessage());
    }
}
