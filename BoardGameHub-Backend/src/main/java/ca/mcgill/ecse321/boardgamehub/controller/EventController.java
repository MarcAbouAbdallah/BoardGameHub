package ca.mcgill.ecse321.boardgamehub.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventCreationDto eventToCreate) {
        Event createdEvent = eventService.createEvent(eventToCreate);
        return ResponseEntity.ok(new EventResponseDto(createdEvent, 0));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable int eventId) {
        Event event = eventService.findEventById(eventId);
        int participantsCount = eventService.getParticipantsCount(eventId);
        return ResponseEntity.ok(new EventResponseDto(event, participantsCount));
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        List<Event> events = eventService.getallEvents();
        List<EventResponseDto> eventDTOs = new ArrayList<>();
        for (Event event : events) {
            int participantsCount = eventService.getParticipantsCount(event.getId());
            eventDTOs.add(new EventResponseDto(event, participantsCount));
        }
        return ResponseEntity.ok(eventDTOs);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable int eventId, @RequestBody EventUpdateDto eventToUpdate, @RequestParam int organizerId) {
        Event updatedEvent = eventService.updateEvent(eventToUpdate, eventId, organizerId);
        int participantsCount = eventService.getParticipantsCount(eventId);
        return ResponseEntity.ok(new EventResponseDto(updatedEvent, participantsCount));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable int eventId, @RequestParam int organizerId) {
        eventService.deleteEvent(eventId, organizerId);
        return ResponseEntity.noContent().build();
    }


    
}
