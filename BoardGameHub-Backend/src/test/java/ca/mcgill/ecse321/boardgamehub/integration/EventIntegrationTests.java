package ca.mcgill.ecse321.boardgamehub.integration;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamehub.dto.EventRequestDto;
import ca.mcgill.ecse321.boardgamehub.dto.EventResponseDto;
import ca.mcgill.ecse321.boardgamehub.dto.EventUpdateDto;
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
    private static final LocalDate DATE = LocalDate.of(2026, 5, 20);
    private static final LocalTime START_TIME = LocalTime.of(13, 0);
    private static final LocalTime END_TIME = LocalTime.of(14, 0);
    private static final int MAX_PARTICIPANTS = 4;

    private static int createdEventId;

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
        EventRequestDto dto = new EventRequestDto(NAME, LOCATION, DESCRIPTION, DATE, START_TIME, END_TIME,
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

        createdEventId = createdEvent.getId(); // Will be used in later tests as created event
        
    }

    @Test
    @Order(1)
    public void testCreateEvent_Failure_InvalidData() {
        EventRequestDto request = new EventRequestDto(
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

    @Test
    @Order(2)
    public void testGetEventByValidId() {
        ResponseEntity<EventResponseDto> response = client.getForEntity("/events/" + createdEventId, EventResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        EventResponseDto event = response.getBody();
        assertNotNull(event);
        assertEquals(createdEventId, event.getId());
        assertEquals(NAME, event.getName());
        assertEquals(LOCATION, event.getLocation());
        assertEquals(DESCRIPTION, event.getDescription());
        assertEquals(DATE, event.getDate());
        assertEquals(START_TIME, event.getStartTime());
        assertEquals(END_TIME, event.getEndTime());
        assertEquals(MAX_PARTICIPANTS, event.getMaxParticipants());
        assertEquals(0, event.getParticipantsCount());
    }

    @Test
    @Order(3)
    public void testGetEventById_NotFound() {
        int wrongId = createdEventId + 1;
        ResponseEntity<String> response = client.getForEntity("/events/" + wrongId, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        String error = response.getBody();
        assertNotNull(error);
        assertTrue(error.contains("No event has Id " + wrongId),
                "Error message for invalid event id.");
    }

    @Test
    @Order(4)
    public void testGetAllEvents() {
        ResponseEntity<EventResponseDto[]> response = client.getForEntity("/events", EventResponseDto[].class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        EventResponseDto[] events = response.getBody();
        assertNotNull(events);
        assertEquals(1, events.length);

        EventResponseDto event = events[0];
        assertNotNull(event);
        assertEquals(createdEventId, event.getId());
        assertEquals(NAME, event.getName());
        assertEquals(LOCATION, event.getLocation());
        assertEquals(DESCRIPTION, event.getDescription());
        assertEquals(DATE, event.getDate());
        assertEquals(START_TIME, event.getStartTime());
        assertEquals(END_TIME, event.getEndTime());
        assertEquals(MAX_PARTICIPANTS, event.getMaxParticipants());
        assertEquals(0, event.getParticipantsCount());
    }

    @Test
    @Order(5)
    public void testUpdateEvent() {
        String newLocation = "New Location";
        String newDescription = "New Description";
        LocalDate newDate = LocalDate.of(2027, 3, 17);
        LocalTime newStartTime = LocalTime.of(14, 0);
        LocalTime newEndTime = LocalTime.of(15, 0);

        EventUpdateDto dto = new EventUpdateDto(null, newDescription, newLocation, newDate, newStartTime, newEndTime, null, null);

        String url = "/events/" + createdEventId + "?userId=" + VALID_ORGANIZER.getId();

        ResponseEntity<EventResponseDto> response = client.exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), EventResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        EventResponseDto updatedEvent = response.getBody();
        assertNotNull(updatedEvent);
        assertEquals(createdEventId, updatedEvent.getId());
        assertEquals(NAME, updatedEvent.getName()); // Not updated (still original value)
        assertEquals(newLocation, updatedEvent.getLocation());
        assertEquals(newDescription, updatedEvent.getDescription());
        assertEquals(newDate, updatedEvent.getDate());
        assertEquals(newStartTime, updatedEvent.getStartTime());
        assertEquals(newEndTime, updatedEvent.getEndTime());
        assertEquals(MAX_PARTICIPANTS, updatedEvent.getMaxParticipants()); // Not updated
        assertEquals(0, updatedEvent.getParticipantsCount());
    }

    @Test
    @Order(6)
    public void testUpdateEvent_Unauthorized(){
        String newLocation = "New Location";

        EventUpdateDto dto = new EventUpdateDto(null, null, newLocation, null, null, null, null, null);

        // Only the organizer can update the event
        int unauthorizedId = VALID_ORGANIZER.getId() + 1;
        Player unauthorizedPlayer = new Player("Unauthorized", "", "123", true);
        playerRepo.save(unauthorizedPlayer);

        String url = "/events/" + createdEventId + "?userId=" + unauthorizedId;

        ResponseEntity<String> response = client.exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        String error = response.getBody();
        assertNotNull(error);
        assertTrue(error.contains("You are not the organizer of this event."),
                "Error message for unauthorized event update.");
    }

    @Test
    @Order(7)
    public void testUpdateEvent_NotFound() {
        String newLocation = "New Location";

        EventUpdateDto dto = new EventUpdateDto(null, null, newLocation, null, null, null, null, null);

        int wrongId = createdEventId + 1;
        String url = "/events/" + wrongId + "?userId=" + VALID_ORGANIZER.getId();

        ResponseEntity<String> response = client.exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        String error = response.getBody();
        assertNotNull(error);
        assertTrue(error.contains("No event has Id " + wrongId),
                "Error message for invalid event id.");
    }

    @Test
    @Order(8)
    public void testDeleteEvent_NotFound() {
        int wrongId = createdEventId + 1;
        String url = "/events/" + wrongId + "?userId=" + VALID_ORGANIZER.getId();

        ResponseEntity<String> response = client.exchange(url, HttpMethod.DELETE, null, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        String error = response.getBody();
        assertNotNull(error);
        assertTrue(error.contains("No event has Id " + wrongId),
                "Error message for invalid event id.");
    }

    @Test
    @Order(9)
    public void testDeleteEvent_Unauthorized() {
        // Only the organizer can delete the event
        int unauthorizedId = VALID_ORGANIZER.getId() + 1;
        Player unauthorizedPlayer = new Player("Unauthorized", "", "123", true);
        playerRepo.save(unauthorizedPlayer);

        String url = "/events/" + createdEventId + "?userId=" + unauthorizedId;

        ResponseEntity<String> response = client.exchange(url, HttpMethod.DELETE, null, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        String error = response.getBody();
        assertNotNull(error);
        assertTrue(error.contains("You are not the organizer of this event."),
                "Error message for unauthorized event deletion.");
    }

    @Test
    @Order(10)
    public void testDeleteEvent() {
        String url = "/events/" + createdEventId + "?userId=" + VALID_ORGANIZER.getId();

        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
