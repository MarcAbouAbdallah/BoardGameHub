package ca.mcgill.ecse321.boardgamehub.service;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamehub.dto.EventCreationdto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.Event;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Registration;
import ca.mcgill.ecse321.boardgamehub.repo.EventRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.RegistrationRepository;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class EventServiceTests {
    @Mock
    private EventRepository mockEventRepo;
    @Mock
    private PlayerRepository mockPlayerRepo;
    @Mock
    private GameCopyRepository mockGameRepo;
    @Mock
    private RegistrationRepository mockRegistrationRepo;
    @InjectMocks
    private EventService eventService;

    private static final int VALID_EVENT_ID = 3;
    private static final int VALID_ORGANIZER_ID = 1;
    private static final int VALID_GAME_ID = 2;
    private static final int VALID_PLAYER_ID = 4;
    private static final int MAX_PARTICIPANTS = 10;
    
    private static final String VALID_EVENT_NAME = "Board Game Night";
    private static final String VALID_EVENT_DESCRIPTION = "A fun board game event!";
    private static final String VALID_EVENT_LOCATION = "Community Center";
    
    private static final LocalDate VALID_DATE = LocalDate.of(2025, 5, 1);
    private static final LocalTime VALID_START_TIME = LocalTime.of(18, 0);
    private static final LocalTime VALID_END_TIME = LocalTime.of(22, 0);
    
    private static final Player VALID_ORGANIZER = new Player("John", "john@gmail.com", "John@123", true);
    private static final Player VALID_PLAYER = new Player("Jane", "jane@gmail.com", "Jane@123", false);
    private static final Game GAME = new Game("Monopoly", 4, 2, "A game");
    private static final GameCopy VALID_GAME = new GameCopy(true, GAME, VALID_ORGANIZER);

    @Test
    public void testCreateValidEvent() {
        
        EventCreationdto eventToCreate = new EventCreationdto(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, VALID_DATE, VALID_START_TIME, VALID_END_TIME, MAX_PARTICIPANTS, VALID_ORGANIZER_ID, VALID_GAME_ID);
        when(mockPlayerRepo.findPlayerById(VALID_ORGANIZER_ID)).thenReturn(VALID_ORGANIZER);
        when(mockGameRepo.findGameCopyById(VALID_GAME_ID)).thenReturn(VALID_GAME);
        when(mockEventRepo.save(any(Event.class))).thenAnswer((InvocationOnMock invocation) -> {
            Event event = invocation.getArgument(0);
            event.setId(VALID_EVENT_ID);
            return event;
        });

        Event event = null;
        try {
            event = eventService.createEvent(eventToCreate);
        } catch (BoardGameHubException e) {
            fail();
        }

        assertNotNull(event);
        assertEquals(VALID_EVENT_ID, event.getId());
        assertEquals(VALID_EVENT_NAME, event.getName());
        assertEquals(VALID_EVENT_DESCRIPTION, event.getDescription());
        assertEquals(VALID_EVENT_LOCATION, event.getLocation());
        assertEquals(Date.valueOf(VALID_DATE), event.getDate());
        assertEquals(Time.valueOf(VALID_START_TIME), event.getStartTime());
        assertEquals(Time.valueOf(VALID_END_TIME), event.getEndTime());
        assertEquals(VALID_ORGANIZER, event.getOrganizer());
        assertEquals(VALID_GAME, event.getGame());
        assertEquals(MAX_PARTICIPANTS, event.getMaxParticipants());
    }

    @Test
    public void testCreateEventWithInvalidOrganizer() {
        
        EventCreationdto eventToCreate = new EventCreationdto(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, VALID_DATE, VALID_START_TIME, VALID_END_TIME, MAX_PARTICIPANTS, VALID_ORGANIZER_ID, VALID_GAME_ID);
        when(mockPlayerRepo.findPlayerById(VALID_ORGANIZER_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            eventService.createEvent(eventToCreate);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no player with ID " + VALID_ORGANIZER_ID + ".", exception.getMessage());
    }

    @Test
    public void testCreateEventWithInvalidGame() {

        EventCreationdto eventToCreate = new EventCreationdto(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, VALID_DATE, VALID_START_TIME, VALID_END_TIME, MAX_PARTICIPANTS, VALID_ORGANIZER_ID, VALID_GAME_ID);
        when(mockPlayerRepo.findPlayerById(VALID_ORGANIZER_ID)).thenReturn(VALID_ORGANIZER);
        when(mockGameRepo.findGameCopyById(VALID_GAME_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            eventService.createEvent(eventToCreate);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no game with ID " + VALID_GAME_ID + ".", exception.getMessage());   
    }

    @Test
    public void testFindEventByValidId(){
        Event event = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        event.setId(VALID_EVENT_ID);
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(event);

        Event foundEvent = null;
        try {
            foundEvent = eventService.findEventById(VALID_EVENT_ID);
        } catch (BoardGameHubException e) {
            fail();
        }

        assertNotNull(foundEvent);
        assertEquals(VALID_EVENT_ID, foundEvent.getId());
        assertEquals(VALID_EVENT_NAME, foundEvent.getName());
        assertEquals(VALID_EVENT_DESCRIPTION, foundEvent.getDescription());
        assertEquals(VALID_EVENT_LOCATION, foundEvent.getLocation());
        assertEquals(Date.valueOf(VALID_DATE), foundEvent.getDate());
        assertEquals(Time.valueOf(VALID_START_TIME), foundEvent.getStartTime());
        assertEquals(Time.valueOf(VALID_END_TIME), foundEvent.getEndTime());
        assertEquals(VALID_ORGANIZER, foundEvent.getOrganizer());
        assertEquals(VALID_GAME, foundEvent.getGame());
        assertEquals(MAX_PARTICIPANTS, foundEvent.getMaxParticipants());
    }

    @Test
    public void testFindEventByInvalidId(){
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            eventService.findEventById(VALID_EVENT_ID);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No event has Id " + VALID_EVENT_ID, exception.getMessage());
    }

    @Test
    public void testDeleteEvent_withPermission(){
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        VALID_EVENT.setId(VALID_EVENT_ID);
    
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_ORGANIZER_ID)).thenReturn(VALID_ORGANIZER);

        eventService.deleteEvent(VALID_EVENT_ID, VALID_ORGANIZER_ID);

        assertDoesNotThrow(() -> mockEventRepo.delete(VALID_EVENT));
    }

    @Test
    public void testDeleteEvent_withoutPermission(){
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        VALID_EVENT.setId(VALID_EVENT_ID);
    
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_ORGANIZER_ID)).thenReturn(VALID_PLAYER);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            eventService.deleteEvent(VALID_EVENT_ID, VALID_ORGANIZER_ID);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You are not the organizer of this event.", exception.getMessage());
    }


}
