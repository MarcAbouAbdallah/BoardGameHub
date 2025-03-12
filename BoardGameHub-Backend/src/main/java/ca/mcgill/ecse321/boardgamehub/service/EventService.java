package ca.mcgill.ecse321.boardgamehub.service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;

import ca.mcgill.ecse321.boardgamehub.repo.EventRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.RegistrationRepository;

import ca.mcgill.ecse321.boardgamehub.model.Event;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Registration;
import ca.mcgill.ecse321.boardgamehub.dto.EventCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.EventUpdateDto;
import ca.mcgill.ecse321.boardgamehub.dto.RegistrationDto;
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
    private GameCopyRepository gameRepo;
    @Autowired
    private RegistrationRepository registrationRepo;


    @Transactional
    public Event createEvent(@Valid EventCreationDto eventToCreate) {

        Player organizer = playerRepo.findPlayerById(eventToCreate.getOrganizer());
        if (organizer == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format(
                                    "There is no player with ID %d.",
                                            eventToCreate.getOrganizer()));
        }

        GameCopy game = gameRepo.findGameCopyById(eventToCreate.getGame());
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

    public Event findEventById(int id) {
        Event event = eventRepo.findEventById(id);
        if (event == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No event has Id %d", id));
        }
        return event;
    }

    @Transactional
    public void deleteEvent(int evenId, int playerId) {
        Event event = findEventById(evenId);
        Player player = playerRepo.findPlayerById(playerId);
        if (player == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No player has Id %d", playerId));
        }

        if (!event.getOrganizer().equals(player)) {
            throw new BoardGameHubException(HttpStatus.FORBIDDEN, 
                                            "You are not the organizer of this event.");
        }

        eventRepo.delete(event);
    }

    @Transactional
    public Event updateEvent(@Valid EventUpdateDto eventDTO, int eventId, int organizerId) {
        Event event = findEventById(eventId);

        Player player = playerRepo.findPlayerById(organizerId);
        if (player == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No player has Id %d", organizerId));
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
            GameCopy newGame = gameRepo.findGameCopyById(eventDTO.getGame());
            if (newGame == null) {
                throw new BoardGameHubException(HttpStatus.NOT_FOUND, "No game found with ID " + eventDTO.getGame());
            }
            event.setGame(newGame);
        }

        return eventRepo.save(event);
    }


    @Transactional
    public Registration registerToEvent(int eventId, int playerId) {
        Event event = findEventById(eventId);
        Player player = playerRepo.findPlayerById(playerId);
        if (player == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No player has Id %d", playerId));
        }

        int participants = (int) registrationRepo.countByKey_RegisteredEvent(event);

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
    public Registration registerToEvent(@Valid RegistrationDto registrationDto) {
        int registeredEventId = registrationDto.getRegisteredEventId();
        int registrantId = registrationDto.getRegistrantId();

        Event registeredEvent = findEventById(registeredEventId);
        Player registrant = playerRepo.findPlayerById(registrantId);

        if (registrant == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, 
                                            String.format("No player has Id %d", registrantId));
        }

        int participants = (int) registrationRepo.countByKey_RegisteredEvent(registeredEvent);

        if (participants >= registeredEvent.getMaxParticipants()) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, 
                                            "This event is full.");
        }

        Registration.Key key = new Registration.Key(registrant, registeredEvent);
        if (registrationRepo.findRegistrationByKey(key) != null) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, 
                                            "You are already registered to this event.");
        }

        Registration registration = new Registration(key);
        return registrationRepo.save(registration);
    }


}
