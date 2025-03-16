package ca.mcgill.ecse321.boardgamehub.integration;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamehub.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamehub.dto.EventCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.EventResponseDto;
import ca.mcgill.ecse321.boardgamehub.dto.EventUpdateDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.ReviewResponseDto;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.EventRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class EventIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameCopyRepository gameCopyRepo;

    @Autowired
    private EventRepository eventRepo;

    private static final Player VALID_ORGANIZER = new Player("John", "john@mail.com", "123", true);
    private static final Game VALID_GAME = new Game("Monopoly", 4, 2, "Fun game.");
    private static final GameCopy VALID_GAMECOPY = new GameCopy(true, VALID_GAME, VALID_ORGANIZER);

    private static final String NAME = "Monopoly Event";
    private static final String LOCATION = "McGill";
    private static final String DESCRIPTION = "Fun event";
    private static final LocalDate DATE = LocalDate.of(2025, 3, 16);
    private static final LocalTime START_TIME = LocalTime.of(13, 0);
    private static final LocalTime END_TIME = LocalTime.of(14, 0);
    private static final int MAX_PARTICIPANTS = 4;
    //private static int ORGANIZER_ID = VALID_ORGANIZER.getId();
    //private static int GAME_ID = VALID_GAMECOPY.getId();

    private int createdEventId;

    @BeforeAll
    public void setup() {
        playerRepo.save(VALID_ORGANIZER);
        gameRepo.save(VALID_GAME);
        gameCopyRepo.save(VALID_GAMECOPY);
    }

    @AfterAll
    public void clearDataBase() {
        eventRepo.deleteAll();
        gameCopyRepo.deleteAll();
        playerRepo.deleteAll();
        gameRepo.deleteAll();
    }

    @Test
    @Order(0)
    public void testCreateValidEvent() {
        System.out.println(VALID_ORGANIZER.getId());
        
        EventCreationDto dto = new EventCreationDto(NAME, LOCATION, DESCRIPTION, DATE, START_TIME, END_TIME,
                MAX_PARTICIPANTS, VALID_ORGANIZER.getId(), VALID_GAMECOPY.getId());

        ResponseEntity<EventResponseDto> response = client.postForEntity("/events", dto, EventResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        EventResponseDto createdEvent = response.getBody();
        assertNotNull(createdEvent);
        
        assertNotNull(createdEvent.getId());
        assertTrue(createdEvent.getId() > 0, "Response should have a positive ID.");
        assertEquals(NAME, createdEvent.getName());
        assertEquals(LOCATION, createdEvent.getLocation());
        assertEquals(DESCRIPTION, createdEvent.getDescription());
        assertEquals(DATE, createdEvent.getDate());
        assertEquals(START_TIME, createdEvent.getStartTime());
        assertEquals(END_TIME, createdEvent.getEndTime());
        assertEquals(MAX_PARTICIPANTS, createdEvent.getMaxParticipants());
        assertEquals(0, createdEvent.getParticipantsCount());

        this.createdEventId = createdEvent.getId(); // For other tests
        
    }

    @Test
    @Order(1)
    public void testCreateEvent_Failure_InvalidData() {
        EventCreationDto request = new EventCreationDto(
                NAME,
                LOCATION,
                DESCRIPTION,
                DATE,
                START_TIME,
                END_TIME,
                -5,
                VALID_ORGANIZER.getId(),
                VALID_GAMECOPY.getId());

        ResponseEntity<String> response = client.postForEntity("/events", request, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Maximum participants must be greater than zero."),
                "Error message for negative participants is missing.");
        
    }

}
