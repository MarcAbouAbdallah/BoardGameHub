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

import ca.mcgill.ecse321.boardgamehub.dto.EventCreationDto;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto createEvent(@RequestBody EventCreationDto eventToCreate) {
        Event createdEvent = eventService.createEvent(eventToCreate);
        return new EventResponseDto(createdEvent, 0);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto getEventById(@PathVariable int eventId) {
        Event event = eventService.findEventById(eventId);
        int participantsCount = eventService.getParticipantsCount(eventId);
        return new EventResponseDto(event, participantsCount);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponseDto> getAllEvents() {
        return eventService.getallEvents().stream()
                .map(event -> new EventResponseDto(event, eventService.getParticipantsCount(event.getId())))
                .toList();
    }

    @PutMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto updateEvent(@PathVariable int eventId, @RequestBody EventUpdateDto eventToUpdate, @RequestParam int organizerId) {
        Event updatedEvent = eventService.updateEvent(eventToUpdate, eventId, organizerId);
        int participantsCount = eventService.getParticipantsCount(eventId);
        return new EventResponseDto(updatedEvent, participantsCount);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable int eventId, @RequestParam int organizerId) {
        eventService.deleteEvent(eventId, organizerId);
    }
}
