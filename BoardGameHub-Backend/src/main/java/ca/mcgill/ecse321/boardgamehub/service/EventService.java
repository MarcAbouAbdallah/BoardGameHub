package ca.mcgill.ecse321.boardgamehub.service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;

import ca.mcgill.ecse321.boardgamehub.repo.EventRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.RegistrationRepository;

import ca.mcgill.ecse321.boardgamehub.model.Event;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Registration;
import ca.mcgill.ecse321.boardgamehub.dto.EventRequestDto;
import ca.mcgill.ecse321.boardgamehub.dto.EventUpdateDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Validated
public class EventService {
    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private PlayerRepository playerRepo;
    @Autowired
    private GameCopyRepository gameCopyRepo;
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private RegistrationRepository registrationRepo;


    @Transactional
    public Event createEvent(@Valid EventRequestDto eventToCreate) {
        validateDto(eventToCreate);

        Player organizer = playerRepo.findPlayerById(eventToCreate.getOrganizer());
        if (organizer == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no player with ID %d.",
                                            eventToCreate.getOrganizer()));
        }

        GameCopy game = gameCopyRepo.findGameCopyById(eventToCreate.getGame());
        if (game == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no game with ID %d.",
                                            eventToCreate.getGame()));
        }

        Date eventDate = Date.valueOf(eventToCreate.getDate());
        Time startTime = Time.valueOf(eventToCreate.getStartTime());
        Time endTime = Time.valueOf(eventToCreate.getEndTime());

        Event event = new Event(
                                eventToCreate.getName(),
                                eventToCreate.getLocation(),
                                eventToCreate.getDescription(),
                                eventDate,
                                startTime,
                                endTime,
                                eventToCreate.getMaxParticipants(),
                                organizer,
                                game);

        return eventRepo.save(event);
    }

    public List<Event> getallEvents() {
        return (List<Event>) eventRepo.findAll();
    }

    public List<Event> getEventsForCreator(int creatorId) {
        Player creator = playerRepo.findPlayerById(creatorId);
        if (creator == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no person with ID %d.",
                                            creatorId));
        }
        return eventRepo.findByOrganizer(creator);
        
    }

    public List<Event> getEventsForGame(String gameName) {
        Game game = gameRepo.findGameByName(gameName);
        if (game == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no game with name %s.",
                                    gameName));
        }
        List<GameCopy> copies = gameCopyRepo.findByGame(game);
        if (copies == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There are no copies of the game with name %s.",
                                    gameName));
        }

        List<Event> eventList = new ArrayList<>();

        for (GameCopy copy: copies) {
            List<Event> tmpList = eventRepo.findByGame(copy) ;
            if (tmpList != null) eventList.addAll(tmpList);
        }

        return eventList;

    }

    public Event findEventById(int id) {
        Event event = eventRepo.findEventById(id);
        if (event == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No event has Id %d", id));
        }
        return event;
    }

    @Transactional
    public void deleteEvent(int eventId, int userId) {
        Event event = findEventById(eventId);
        Player player = playerRepo.findPlayerById(userId);
        if (player == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No player has Id %d", userId));
        }

        // Check that the user is the event organizer
        if (!event.getOrganizer().equals(player)) {
            throw new BoardGameHubException(HttpStatus.FORBIDDEN, 
                                            "You are not the organizer of this event.");
        }

        eventRepo.delete(event);
    }

    @Transactional
    public Event updateEvent(@Valid EventUpdateDto eventDTO, int eventId, int userId) {
        Event event = findEventById(eventId);

        Player player = playerRepo.findPlayerById(userId);
        if (player == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No player has Id %d", userId));
        }

        if (!event.getOrganizer().equals(player)) {
            throw new BoardGameHubException(HttpStatus.FORBIDDEN, 
                                            "You are not the organizer of this event.");
        }

        eventDTO.validate();

        if (eventDTO.getName() != null) event.setName(eventDTO.getName());
        if (eventDTO.getDescription() != null) event.setDescription(eventDTO.getDescription());
        if (eventDTO.getLocation() != null) event.setLocation(eventDTO.getLocation());
        if (eventDTO.getDate() != null) event.setDate(Date.valueOf(eventDTO.getDate()));
        if (eventDTO.getStartTime() != null) event.setStartTime(Time.valueOf(eventDTO.getStartTime()));
        if (eventDTO.getEndTime() != null) event.setEndTime(Time.valueOf(eventDTO.getEndTime()));
        if (eventDTO.getMaxParticipants() != null) event.setMaxParticipants(eventDTO.getMaxParticipants());

        if (eventDTO.getGame() != null) {
            GameCopy newGame = gameCopyRepo.findGameCopyById(eventDTO.getGame());
            if (newGame == null) {
                throw new BoardGameHubException(HttpStatus.NOT_FOUND, "No game found with ID " + eventDTO.getGame());
            }
            event.setGame(newGame);
        }

        return eventRepo.save(event);
    }

    public int getParticipantsCount(int eventId) {
        Event event = findEventById(eventId);
        return (int) registrationRepo.countByKey_RegisteredEvent(event);
    }

    public void validateDto(EventRequestDto eventToCreate) {
        if (eventToCreate.getName().isBlank()) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Event name cannot be blank.");
        }

        if (eventToCreate.getLocation().isBlank()) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Event location cannot be blank.");
        }

        if (eventToCreate.getDescription().isBlank()) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Event description cannot be blank.");
        }

        if (eventToCreate.getMaxParticipants() <= 0) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Maximum participants must be greater than zero.");
        }

        if (eventToCreate.getDate().isBefore(LocalDate.now())) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Event date must be in the future.");
        }

        if (eventToCreate.getStartTime().isAfter(eventToCreate.getEndTime())) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "End time must be after start time.");
        }
    }
  
    @Transactional
    public Registration registerToEvent(int eventId, int playerId) {
        Event event = findEventById(eventId);
        Player player = playerRepo.findPlayerById(playerId);
        if (player == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No player has Id %d", playerId));
        }

        int participants = getParticipantsCount(eventId);

        if (participants >= event.getMaxParticipants()) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, 
                                            "This event is full.");
        }

        Registration.Key key = new Registration.Key(player, event);
        if (registrationRepo.findRegistrationByKey(key) != null) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, 
                                            "You are already registered to this event.");
        }

        Registration registration = new Registration(key);
        return registrationRepo.save(registration);
    }

    @Transactional
    public void unregisterFromEvent(int eventId, int playerId) {
        Event event = findEventById(eventId);
        Player player = playerRepo.findPlayerById(playerId);
        if (player == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No player has Id %d", playerId));
        }

        Registration.Key key = new Registration.Key(player, event);
        Registration registration = registrationRepo.findRegistrationByKey(key);
        if (registration == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            "You are not registered to this event.");
        }

        registrationRepo.delete(registration);
    }

    @Transactional
    public List<Registration> findRegistrationsByPlayer(int registrantId) {
        Player registrant = playerRepo.findPlayerById(registrantId);
        if (registrant == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no registrant with ID %s.",
                                    registrantId));
        }
        List<Registration> registrations = registrationRepo.findByKey_Registrant(registrant);
        if (registrations.isEmpty()) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
            "No registration found for player ID " + registrantId + ".");
        }
        return registrationRepo.findByKey_Registrant(registrant);
    }

    @Transactional
    public List<Registration> findRegistrationsByEvent(int eventId) {
        Event registeredEvent = eventRepo.findEventById(eventId);
        if (registeredEvent == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no registered event with ID %s.",
                                    eventId));
        }
        List<Registration> registrations = registrationRepo.findByKey_RegisteredEvent(registeredEvent);
        if (registrations.isEmpty()) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
            "No registration found for event ID " + eventId + ".");
        }
        return registrationRepo.findByKey_RegisteredEvent(registeredEvent);
    }

    @Transactional
    public Registration findRegistration(int eventId, int registrantId) {
        Event registeredEvent = findEventById(eventId);
        Player registrant = playerRepo.findPlayerById(registrantId);
        if (registrant == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no registrant with ID %s.",
                                    registrantId));
        }
        if (registeredEvent == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no registered event with ID %s.",
                                    eventId));
        }
        Registration.Key key = new Registration.Key(registrant, registeredEvent);
        Registration registration = registrationRepo.findRegistrationByKey(key);
        if (registration == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            "No registration found for the given player and event.");
        }
        return registration;
    }

    @Transactional
    public List<Registration> findAllRegistrations() {
        return (List<Registration>) registrationRepo.findAll();
    }

}
