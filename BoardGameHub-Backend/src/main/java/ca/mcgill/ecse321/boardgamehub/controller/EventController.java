package ca.mcgill.ecse321.boardgamehub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

import org.springframework.validation.annotation.Validated;

import ca.mcgill.ecse321.boardgamehub.dto.EventRequestDto;
import ca.mcgill.ecse321.boardgamehub.dto.EventResponseDto;
import ca.mcgill.ecse321.boardgamehub.dto.EventUpdateDto;

import ca.mcgill.ecse321.boardgamehub.model.Event;
import ca.mcgill.ecse321.boardgamehub.service.EventService;

@RestController
@RequestMapping("/events")
@Validated
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * Create an event
     * 
     * @param eventToCreate an EventRequestDto containing the event to be created
     * @return An eventResponseDto containing the created event with its id
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto createEvent(@RequestBody EventRequestDto eventToCreate) {
        Event createdEvent = eventService.createEvent(eventToCreate);
        return new EventResponseDto(createdEvent, 0);
    }

    /**
     * Get a particular event by its id
     * 
     * @param eventId The id of the event to get
     * @return An eventResponseDto containing the event and its current participants count
     */
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto getEventById(@PathVariable int eventId) {
        Event event = eventService.findEventById(eventId);
        int participantsCount = eventService.getParticipantsCount(eventId);
        return new EventResponseDto(event, participantsCount);
    }

    /**
     * Get all events
     * 
     * @return A List of eventResponseDto containing all events with their participants count
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponseDto> getAllEvents() {
        return eventService.getallEvents().stream()
                .map(event -> new EventResponseDto(event, eventService.getParticipantsCount(event.getId())))
                .toList();
    }
    
    /**
     * Updating an event
     * 
     * @param eventId The id of the event to update
     * @param eventToUpdate An EventUpdateDto containing the updated event
     * @param userId The id of the user making the update (to check if they are the event organizer)
     * @return An eventResponseDto containing the updated event
     */
    @PutMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto updateEvent(@PathVariable int eventId, @RequestBody EventUpdateDto eventToUpdate, @RequestParam int userId) {
        Event updatedEvent = eventService.updateEvent(eventToUpdate, eventId, userId);
        int participantsCount = eventService.getParticipantsCount(eventId);
        return new EventResponseDto(updatedEvent, participantsCount);
    }

    /**
     * Delete an event
     * 
     * @param eventId The id of the event to delete
     * @param userId The id of the user making the delete request (to check if they are the organizer)
     * @return void
     */
    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable int eventId, @RequestParam int userId) {
        eventService.deleteEvent(eventId, userId);
    }
}
