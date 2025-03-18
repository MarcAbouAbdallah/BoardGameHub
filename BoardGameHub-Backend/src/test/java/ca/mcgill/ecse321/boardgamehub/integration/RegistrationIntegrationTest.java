package ca.mcgill.ecse321.boardgamehub.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamehub.dto.RegistrationDto;
import ca.mcgill.ecse321.boardgamehub.model.Event;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.Registration;
import ca.mcgill.ecse321.boardgamehub.repo.EventRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.RegistrationRepository;

import org.springframework.http.HttpMethod;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class RegistrationIntegrationTest {
    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameCopyRepository gameCopyRepo;

    @Autowired
    private RegistrationRepository registrationRepo;

    private static final String VALID_DESCRIPTION_STRING = "Going to play monopoly";
    private static final LocalDate VALID_DATE = LocalDate.of(2026, 5, 1);
    private static final LocalTime VALID_START_TIME = LocalTime.of(18, 0);
    private static final LocalTime VALID_END_TIME = LocalTime.of(22, 0);
    private static final int MAX_PARTICIPANTS = 10;

    private static final Player VALID_PLAYER = new Player("John", "john@mail.com", "jhKLEHGH75*(#$&", true);
    private static final Game VALID_GAME = new Game("Monopoly", 4, 2, "Fun game.");
    private static final GameCopy VALID_GAMECOPY = new GameCopy(true, VALID_GAME, VALID_PLAYER);
    private static final Event VALID_EVENT = new Event("Monopoly Club", "McGill Library", VALID_DESCRIPTION_STRING, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_PLAYER, VALID_GAMECOPY);

    @BeforeAll
    public void setup() {
        gameRepo.save(VALID_GAME);
        VALID_PLAYER.setId(playerRepo.save(VALID_PLAYER).getId());
        gameCopyRepo.save(VALID_GAMECOPY);
        VALID_EVENT.setId(eventRepo.save(VALID_EVENT).getId());
    }

    @AfterAll
    public void clearDatabase() {
        registrationRepo.deleteAll();
        eventRepo.deleteAll();
        gameCopyRepo.deleteAll();
        playerRepo.deleteAll();
        gameRepo.deleteAll();

    }

    @Test
    @Order(1)
    public void testRegisterValidPlayerToValidEvent() {
        int VALID_EVENT_ID = VALID_EVENT.getId();
        int VALID_PLAYER_ID = VALID_PLAYER.getId();
        String url = "/registrations/" + VALID_EVENT_ID + "/" + VALID_PLAYER_ID;
        
        ResponseEntity<Registration> response = client.postForEntity(url, null, Registration.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Registration registration = response.getBody();
        assertNotNull(registration);
        assertEquals(VALID_EVENT_ID, registration.getKey().getRegisteredEvent().getId());
        assertEquals(VALID_PLAYER_ID, registration.getKey().getRegistrant().getId());
    }

    @Test
    @Order(2)
    public void testRegisterPlayerToNonExistentEvent() {
        int VALID_PLAYER_ID = VALID_PLAYER.getId();
        int INVALID_EVENT_ID = 99999;
        String url = "/registrations/" + INVALID_EVENT_ID + "/" + VALID_PLAYER_ID;
        
        ResponseEntity<String> response = client.postForEntity(url, null, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("No event has Id " + INVALID_EVENT_ID));
    }

    @Test
    @Order(3)
    public void testFindRegistrationsByPlayer() {
        int VALID_PLAYER_ID = VALID_PLAYER.getId();
        String url = "/registrations/player/" + VALID_PLAYER_ID;
        
        ResponseEntity<RegistrationDto[]> response = client.getForEntity(url, RegistrationDto[].class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        RegistrationDto[] registrations = response.getBody();
        assertNotNull(registrations);
        assertTrue(registrations.length > 0);

        boolean found = false;
        for (RegistrationDto registrationDto : registrations) {
            if (registrationDto.getRegisteredEventId() == VALID_EVENT.getId() &&
                registrationDto.getRegistrantId() == VALID_PLAYER_ID) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    @Order(4)
    public void testFindRegistrationsByNonExistentPlayer() {
        int VALID_EVENT_ID = VALID_EVENT.getId();
        int INVALID_PLAYER_ID = 99999;
        String url = "/registrations/" + VALID_EVENT_ID + "/" + INVALID_PLAYER_ID;
        
        ResponseEntity<Void> response = client.getForEntity(url, Void.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(5)
    public void testDeleteValidRegistration() {
        int VALID_EVENT_ID = VALID_EVENT.getId();
        int VALID_PLAYER_ID = VALID_PLAYER.getId();
        String url = "/registrations/" + VALID_EVENT_ID + "/" + VALID_PLAYER_ID;
        
        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(6)
    public void testDeleteNonExistentRegistration() {
        int INVALID_EVENT_ID = 99999;
        int INVALID_PLAYER_ID = 99999;
        String url = "/registrations/" + INVALID_EVENT_ID + "/" + INVALID_PLAYER_ID;
        
        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
