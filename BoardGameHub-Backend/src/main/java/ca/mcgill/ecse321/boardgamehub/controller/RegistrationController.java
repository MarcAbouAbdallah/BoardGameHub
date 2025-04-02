package ca.mcgill.ecse321.boardgamehub.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamehub.model.Registration;
import ca.mcgill.ecse321.boardgamehub.dto.RegistrationDto;
import ca.mcgill.ecse321.boardgamehub.service.EventService;

@CrossOrigin(origins = "*")
@RestController
public class RegistrationController {
    @Autowired
    private EventService registrationService;

    /**
     * Register a player to an event
     * 
     * @param eventId the ID of the event to register for
     * @param playerId the ID of the player registering for the event
     * @return A Response Entity
     */
    @PostMapping("registrations/{eventId}/{playerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Registration> registerToEvent(@PathVariable int eventId, @PathVariable int playerId) {
        Registration registration = registrationService.registerToEvent(eventId, playerId);
        return new ResponseEntity<>(registration, HttpStatus.CREATED);
    }
    
    /**
     * Get all registrations.
     *
     * @return A list of all registrations as DTO
     */
    @GetMapping("registrations")
    public List<RegistrationDto> findAllRegistrations() {
        return registrationService.findAllRegistrations().stream()
                           .map(RegistrationDto::new)
                           .collect(Collectors.toList());
    
    }

    /**
     * Get all registrations for a specific event.
     *
     * @param eventId the ID of the event to get registrations for
     * @return A list of registrations for the specified event, each as a DTO
     */
    @GetMapping("registrations/event/{eventId}")
    public List<RegistrationDto> findRegistrationsByEvent(@PathVariable int eventId) {
        return registrationService.findRegistrationsByEvent(eventId).stream()
                           .map(RegistrationDto::new)
                           .collect(Collectors.toList());
    }

    /**
     * Get all registrations for a specific player.
     *
     * @param playerId the ID of the player to get registrations for
     * @return A list of registrations for the specified player, each as a DTO
     */
    @GetMapping("registrations/player/{playerId}")
    public List<RegistrationDto> findRegistrationsByPlayer(@PathVariable int playerId) {
        return registrationService.findRegistrationsByPlayer(playerId).stream()
                           .map(RegistrationDto::new)
                           .collect(Collectors.toList());
    }

    /**
     * Get a specific registration by event and player IDs.
     *
     * @param eventId the ID of the event
     * @param playerId the ID of the player
     * @return The registration for the specified player and event, as a DTO
     */
    @GetMapping("registrations/{eventId}/{playerId}")
    public RegistrationDto findRegistration(@PathVariable int eventId, @PathVariable int playerId) {
        Registration registration = registrationService.findRegistration(eventId, playerId);
        return new RegistrationDto(registration);
    }

    /**
     * Unregister a player from a specific event.
     *
     * @param eventId the ID of the event
     * @param playerId the ID of the player
     */
    @DeleteMapping("registrations/{eventId}/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterFromEvent(@PathVariable int eventId, @PathVariable int playerId) {
        registrationService.unregisterFromEvent(eventId, playerId);
    }
}
