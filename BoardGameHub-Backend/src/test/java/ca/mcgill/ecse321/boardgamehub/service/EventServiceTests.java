package ca.mcgill.ecse321.boardgamehub.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamehub.dto.EventCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.EventUpdateDto;
import ca.mcgill.ecse321.boardgamehub.dto.RegistrationDto;
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
    
    private static final LocalDate VALID_DATE = LocalDate.of(2026, 5, 1);
    private static final LocalTime VALID_START_TIME = LocalTime.of(18, 0);
    private static final LocalTime VALID_END_TIME = LocalTime.of(22, 0);
    
    private static final Player VALID_ORGANIZER = new Player("John", "john@gmail.com", "John@123", true);
    private static final Player VALID_PLAYER = new Player("Jane", "jane@gmail.com", "Jane@123", false);
    private static final Game GAME = new Game("Monopoly", 4, 2, "A game");
    private static final GameCopy VALID_GAME = new GameCopy(true, GAME, VALID_ORGANIZER);

    @Test
    public void testCreateValidEvent() {
        EventCreationDto eventToCreate = new EventCreationDto(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, VALID_DATE, VALID_START_TIME, VALID_END_TIME, MAX_PARTICIPANTS, VALID_ORGANIZER_ID, VALID_GAME_ID);
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
        EventCreationDto eventToCreate = new EventCreationDto(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, VALID_DATE, VALID_START_TIME, VALID_END_TIME, MAX_PARTICIPANTS, VALID_ORGANIZER_ID, VALID_GAME_ID);
        when(mockPlayerRepo.findPlayerById(VALID_ORGANIZER_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            eventService.createEvent(eventToCreate);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no player with ID " + VALID_ORGANIZER_ID + ".", exception.getMessage());
    }

    @Test
    public void testCreateEventWithInvalidGame() {
        EventCreationDto eventToCreate = new EventCreationDto(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, VALID_DATE, VALID_START_TIME, VALID_END_TIME, MAX_PARTICIPANTS, VALID_ORGANIZER_ID, VALID_GAME_ID);
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

        Event foundEvent = eventService.findEventById(VALID_EVENT_ID);

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
    
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_ORGANIZER_ID)).thenReturn(VALID_ORGANIZER);

        eventService.deleteEvent(VALID_EVENT_ID, VALID_ORGANIZER_ID);

        assertDoesNotThrow(() -> mockEventRepo.delete(VALID_EVENT));
    }

    @Test
    public void testDeleteEvent_withoutPermission(){
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
    
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_ORGANIZER_ID)).thenReturn(VALID_PLAYER);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            eventService.deleteEvent(VALID_EVENT_ID, VALID_ORGANIZER_ID);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You are not the organizer of this event.", exception.getMessage());
    }

    @Test
    public void testRegisterToEvent_Success(){
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_PLAYER_ID)).thenReturn(VALID_PLAYER);
        when(mockRegistrationRepo.countByKey_RegisteredEvent(VALID_EVENT)).thenReturn(5);
        when(mockRegistrationRepo.findRegistrationByKey(any())).thenReturn(null);
        when(mockRegistrationRepo.save(any(Registration.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

        Registration registration = eventService.registerToEvent(VALID_EVENT_ID, VALID_PLAYER_ID);

        assertNotNull(registration);
        verify(mockRegistrationRepo).save(any(Registration.class));
    }

    @Test
    public void testRegisterToEvent_Fail_EventFull(){
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_PLAYER_ID)).thenReturn(VALID_PLAYER);
        when(mockRegistrationRepo.countByKey_RegisteredEvent(VALID_EVENT)).thenReturn(MAX_PARTICIPANTS);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            eventService.registerToEvent(VALID_EVENT_ID, VALID_PLAYER_ID);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("This event is full.", exception.getMessage());
    }

    @Test
    public void testRegisterToEvent_Fail_AlreadyRegistered(){
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        Registration registration = new Registration(new Registration.Key(VALID_PLAYER, VALID_EVENT));
        
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_PLAYER_ID)).thenReturn(VALID_PLAYER);
        when(mockRegistrationRepo.countByKey_RegisteredEvent(VALID_EVENT)).thenReturn(5);
        when(mockRegistrationRepo.findRegistrationByKey(any())).thenReturn(registration);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            eventService.registerToEvent(VALID_EVENT_ID, VALID_PLAYER_ID);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("You are already registered to this event.", exception.getMessage());
    }

    @Test void testUnregisterFromEvent_Success(){
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        Registration registration = new Registration(new Registration.Key(VALID_PLAYER, VALID_EVENT));
        
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_PLAYER_ID)).thenReturn(VALID_PLAYER);
        when(mockRegistrationRepo.findRegistrationByKey(any())).thenReturn(registration);

        // No errors and deletion happens exaclty once
        assertDoesNotThrow(() -> eventService.unregisterFromEvent(VALID_EVENT_ID, VALID_PLAYER_ID));
        verify(mockRegistrationRepo, times(1)).delete(registration);
    }

    @Test
    public void testUnregisterFromEvent_Fail_PlayerNotRegistered() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);

        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_PLAYER_ID)).thenReturn(VALID_PLAYER);
        when(mockRegistrationRepo.findRegistrationByKey(any())).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
                eventService.unregisterFromEvent(VALID_EVENT_ID, VALID_PLAYER_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("You are not registered to this event.", exception.getMessage());

        // No deletion happened
        verify(mockRegistrationRepo, never()).delete(any(Registration.class));
    } 

    @Test
    public void testUnregisterFromEvent_Fail_PlayerNotFound() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);

        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_PLAYER_ID)).thenReturn(null);
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
                eventService.unregisterFromEvent(VALID_EVENT_ID, VALID_PLAYER_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No player has Id " + VALID_PLAYER_ID, exception.getMessage());

        // No deletion happened
        verify(mockRegistrationRepo, never()).delete(any(Registration.class));
    }

    @Test
    public void testUnregisterFromEvent_Fail_EventNotFound() {
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
                eventService.unregisterFromEvent(VALID_EVENT_ID, VALID_PLAYER_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No event has Id " + VALID_EVENT_ID, exception.getMessage());

        // No deletion happened
        verify(mockRegistrationRepo, never()).delete(any(Registration.class));
    }

    @Test
    public void testGetAllEvents_Success() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);

        List<Event> eventList = new ArrayList<>();
        eventList.add(VALID_EVENT);

        when(mockEventRepo.findAll()).thenReturn(eventList);

        List<Event> retrievedEvents = eventService.getallEvents();

        assertNotNull(retrievedEvents);
        assertEquals(1, retrievedEvents.size());
        assertEquals(VALID_EVENT_NAME, retrievedEvents.get(0).getName());
        assertEquals(VALID_EVENT_LOCATION, retrievedEvents.get(0).getLocation());
    }

    @Test
    public void testUpdateEvent_Success() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);

        EventUpdateDto updateDTO = new EventUpdateDto("Updated Game Night", null, "New Venue", null, null, null, null, null);
        
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_ORGANIZER_ID)).thenReturn(VALID_ORGANIZER);
        when(mockEventRepo.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event updatedEvent = eventService.updateEvent(updateDTO, VALID_EVENT_ID, VALID_ORGANIZER_ID);

        assertNotNull(updatedEvent);
        assertEquals("Updated Game Night", updatedEvent.getName());
        assertEquals("New Venue", updatedEvent.getLocation());
        // The rest is unchanged
        assertEquals(VALID_EVENT_DESCRIPTION, updatedEvent.getDescription());
        assertEquals(VALID_GAME, updatedEvent.getGame());
        assertEquals(Date.valueOf(VALID_DATE), updatedEvent.getDate());
        assertEquals(Time.valueOf(VALID_START_TIME), updatedEvent.getStartTime());
        assertEquals(Time.valueOf(VALID_END_TIME), updatedEvent.getEndTime());
        assertEquals(MAX_PARTICIPANTS, updatedEvent.getMaxParticipants());
    }

    @Test
    public void testUpdate_withoutPermission(){
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);

        EventUpdateDto updateDTO = new EventUpdateDto("Updated Game Night", null, "New Venue", null, null, null, null, null);
        
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_PLAYER_ID)).thenReturn(VALID_PLAYER);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            eventService.updateEvent(updateDTO, VALID_EVENT_ID, VALID_PLAYER_ID); // Player is not the organizer (can't update)
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You are not the organizer of this event.", exception.getMessage());
    }

    @Test
    public void testUpdateEvent_Fail_EventNotFound() {
        EventUpdateDto updateDTO = new EventUpdateDto("Updated Game Night", null, "New Venue", null, null, null, null, null);
        
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            eventService.updateEvent(updateDTO, VALID_EVENT_ID, VALID_ORGANIZER_ID);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No event has Id " + VALID_EVENT_ID, exception.getMessage());
    }

    @Test
    public void testUpdateEvent_Fail_InvalidGame() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);

        EventUpdateDto updateDTO = new EventUpdateDto(null, null, null, null, null, null, null, VALID_GAME_ID);
        
        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockGameRepo.findGameCopyById(VALID_GAME_ID)).thenReturn(null);
        when(mockPlayerRepo.findPlayerById(VALID_ORGANIZER_ID)).thenReturn(VALID_ORGANIZER);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            eventService.updateEvent(updateDTO, VALID_EVENT_ID, VALID_ORGANIZER_ID);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No game found with ID " + VALID_GAME_ID, exception.getMessage());
    }

    @Test
    public void testGetAllEvents_EmptyList() {
        when(mockEventRepo.findAll()).thenReturn(new ArrayList<>());

        List<Event> retrievedEvents = eventService.getallEvents();

        assertNotNull(retrievedEvents);
        assertEquals(0, retrievedEvents.size());
    }

    @Test
    public void testFindRegistration_Success() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        Registration registration = new Registration(new Registration.Key(VALID_PLAYER, VALID_EVENT));
        VALID_PLAYER.setId(4);
        VALID_EVENT.setId(3);

        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_PLAYER_ID)).thenReturn(VALID_PLAYER);
        when(mockRegistrationRepo.findRegistrationByKey(any())).thenReturn(registration);

        Registration result = eventService.findRegistration(VALID_EVENT_ID, VALID_PLAYER_ID);

        assertNotNull(result);
        assertEquals(VALID_PLAYER_ID, result.getKey().getRegistrant().getId());
        assertEquals(VALID_EVENT_ID, result.getKey().getRegisteredEvent().getId());

        RegistrationDto registrationDto = new RegistrationDto(result);
        assertEquals(VALID_PLAYER_ID, registrationDto.getRegistrantId());
        assertEquals(VALID_EVENT_ID, registrationDto.getRegisteredEventId());
    }

    @Test
    public void testFindRegistration_Fail_RegistrationNotFound() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);

        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(VALID_PLAYER_ID)).thenReturn(VALID_PLAYER);
        when(mockRegistrationRepo.findRegistrationByKey(any())).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
                eventService.findRegistration(VALID_EVENT_ID, VALID_PLAYER_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No registration found for the given player and event.", exception.getMessage());
    }

    @Test
    public void testFindRegistration_Fail_EventNotFound() {
        int INVALID_EVENT_ID = 99999999;

        when(mockEventRepo.findEventById(INVALID_EVENT_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
                eventService.findRegistration(INVALID_EVENT_ID, VALID_PLAYER_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No event has Id " + INVALID_EVENT_ID, exception.getMessage());
    }

    @Test
    public void testFindRegistration_Fail_RegistrantNotFound() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        int INVALID_PLAYER_ID = 99999999;

        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockPlayerRepo.findPlayerById(INVALID_PLAYER_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
                eventService.findRegistration(VALID_EVENT_ID, INVALID_PLAYER_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no registrant with ID " + INVALID_PLAYER_ID + ".", exception.getMessage());
    }

    @Test
    public void testFindRegistrationsByRegistrant_Success() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        VALID_PLAYER.setId(4);
        VALID_EVENT.setId(3);

        Registration registration = new Registration(new Registration.Key(VALID_PLAYER, VALID_EVENT));

        when(mockPlayerRepo.findPlayerById(VALID_PLAYER_ID)).thenReturn(VALID_PLAYER);
        when(mockRegistrationRepo.findByKey_Registrant(VALID_PLAYER)).thenReturn(List.of(registration));

        List<Registration> result = eventService.findRegistrationsByPlayer(VALID_PLAYER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(VALID_PLAYER_ID, result.get(0).getKey().getRegistrant().getId());
        assertEquals(VALID_EVENT_ID, result.get(0).getKey().getRegisteredEvent().getId());
    }

    @Test
    public void testFindRegistrationsByRegistrant_Fail_RegistrationNotFound() {
        when(mockPlayerRepo.findPlayerById(VALID_PLAYER_ID)).thenReturn(VALID_PLAYER);
        when(mockRegistrationRepo.findByKey_Registrant(VALID_PLAYER)).thenReturn(Collections.emptyList());

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
                eventService.findRegistrationsByPlayer(VALID_PLAYER_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No registration found for player ID " + VALID_PLAYER_ID + ".", exception.getMessage());
    }

    @Test
    public void testFindRegistrationsByRegistrant_Fail_RegistrantNotFound() {
        int INVALID_PLAYER_ID = 99999999;

        when(mockPlayerRepo.findPlayerById(INVALID_PLAYER_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
                eventService.findRegistrationsByPlayer(INVALID_PLAYER_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no registrant with ID " + INVALID_PLAYER_ID + ".", exception.getMessage());
    }

    @Test
    public void testFindRegistrationsByEvent_Success() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        VALID_EVENT.setId(3);
        Registration registration = new Registration(new Registration.Key(VALID_PLAYER, VALID_EVENT));

        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockRegistrationRepo.findByKey_RegisteredEvent(VALID_EVENT)).thenReturn(List.of(registration));

        List<Registration> result = eventService.findRegistrationsByEvent(VALID_EVENT_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(VALID_PLAYER_ID, result.get(0).getKey().getRegistrant().getId());
        assertEquals(VALID_EVENT_ID, result.get(0).getKey().getRegisteredEvent().getId());
    }

    @Test
    public void testFindRegistrationsByEvent_Fail_RegistrationNotFound() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);

        when(mockEventRepo.findEventById(VALID_EVENT_ID)).thenReturn(VALID_EVENT);
        when(mockRegistrationRepo.findByKey_RegisteredEvent(VALID_EVENT)).thenReturn(Collections.emptyList());

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
                eventService.findRegistrationsByEvent(VALID_EVENT_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No registration found for event ID " + VALID_EVENT_ID + ".", exception.getMessage());
    }

    @Test
    public void testFindRegistrationsByEvent_Fail_EventNotFound() {
        int INVALID_EVENT_ID = 99999999;

        when(mockEventRepo.findEventById(INVALID_EVENT_ID)).thenReturn(null);

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () ->
                eventService.findRegistrationsByEvent(INVALID_EVENT_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no registered event with ID " + INVALID_EVENT_ID + ".", exception.getMessage());
    }   

    @Test
    public void testFindAllRegistrations_Success() {
        Event VALID_EVENT = new Event(VALID_EVENT_NAME, VALID_EVENT_LOCATION, VALID_EVENT_DESCRIPTION, Date.valueOf(VALID_DATE), Time.valueOf(VALID_START_TIME), Time.valueOf(VALID_END_TIME), MAX_PARTICIPANTS, VALID_ORGANIZER, VALID_GAME);
        Registration registration = new Registration(new Registration.Key(VALID_PLAYER, VALID_EVENT));
        VALID_PLAYER.setId(4);
        VALID_EVENT.setId(3);
        
        when(mockRegistrationRepo.findAll()).thenReturn(List.of(registration));

        List<Registration> result = eventService.findAllRegistrations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(VALID_PLAYER_ID, result.get(0).getKey().getRegistrant().getId());
        assertEquals(VALID_EVENT_ID, result.get(0).getKey().getRegisteredEvent().getId());
    }

    @Test
    public void testFindAllRegistrations_EmptyList() {
        when(mockRegistrationRepo.findAll()).thenReturn(Collections.emptyList());

        List<Registration> result = eventService.findAllRegistrations();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
