package ca.mcgill.ecse321.boardgamehub.repo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Event;

@SpringBootTest
public class EventRepositoryTests {

    @Autowired
    private EventRepository EventRepo;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GameCopyRepository gameCopyRepo;

    @Autowired
    private GameRepository gameRepo;

    private Player john;
    
    private Game monopoly;

    private GameCopy monopolyCopy;

    @BeforeEach
    public void setup() {
        john = new Player(
                            "John",
                            "john@email.com",
                            "johnnyboy123",
                            true);
        playerRepo.save(john);
        monopoly = new Game(                    
                            "Monopoly", 
                            8, 
                            2, 
                            "multiplayer economics-themed board game");
        gameRepo.save(monopoly);

        monopolyCopy = new GameCopy(true, monopoly, john);
        monopolyCopy = gameCopyRepo.save(monopolyCopy);
    }

    @AfterEach
    public void clearDataBase() {
        EventRepo.deleteAll();
        gameCopyRepo.deleteAll();
        playerRepo.deleteAll();
        gameRepo.deleteAll();
    }

    @Test
    public void testCreateAndReadEvent() {
        //Arrange
        Date date = Date.valueOf("2025-02-13");
        Time startTime = Time.valueOf("10:00:00");
        Time endTime = Time.valueOf("12:00:00");
        String name = "Monopoly Event";
        String location = "McGill";
        String description = "Playing Monopoly";
        int maxParticipants = 9;

        Event monopolyEvent = new Event(name,location,description,date,startTime,endTime,maxParticipants,john,monopolyCopy);
        
        monopolyEvent = EventRepo.save(monopolyEvent);

        //Act
        Event monopolyEventFromDb = EventRepo.findEventById(monopolyEvent.getId());

        //Assert
        assertNotNull(monopolyEventFromDb);

        assertEquals(monopolyEvent.getId(), monopolyEventFromDb.getId());
		assertEquals(name, monopolyEventFromDb.getName());
        assertEquals(location, monopolyEventFromDb.getLocation());
        assertEquals(description, monopolyEventFromDb.getDescription());
        assertEquals(date, monopolyEventFromDb.getDate());
        assertEquals(startTime, monopolyEventFromDb.getStartTime());
        assertEquals(endTime, monopolyEventFromDb.getEndTime());
        assertEquals(maxParticipants, monopolyEventFromDb.getMaxParticipants());
        
        assertEquals(john.getId(), monopolyEventFromDb.getOrganizer().getId());
        assertEquals(monopolyCopy.getId(), monopolyEventFromDb.getGame().getId());
    }

    @Test
    public void testUpdateEvent() {
        Date today = Date.valueOf("2025-02-13");
        Time startTime = Time.valueOf("10:00:00");
        Time endTime = Time.valueOf("12:00:00");
        String name = "Monopoly Event";
        String location = "McGill";
        String description = "Playing Monopoly";
        int maxParticipants = 9;

        Event monopolyEvent = new Event(name,location,description,today,startTime,endTime,maxParticipants,john,monopolyCopy);
        
        monopolyEvent = EventRepo.save(monopolyEvent);

        String newName = "Monopoly Event 2";
        String newLocation = "Concordia";
        String newDescription = "Playing Monopoly 2";
        Date newDate = Date.valueOf("2025-02-14");
        Time newStartTime = Time.valueOf("11:00:00");
        Time newEndTime = Time.valueOf("13:00:00");
        int newMaxParticipants = 10;
    
        monopolyEvent.setName(newName);
        monopolyEvent.setLocation(newLocation);
        monopolyEvent.setDescription(newDescription);
        monopolyEvent.setDate(newDate);
        monopolyEvent.setStartTime(newStartTime);
        monopolyEvent.setEndTime(newEndTime);
        monopolyEvent.setMaxParticipants(newMaxParticipants);
        monopolyEvent = EventRepo.save(monopolyEvent);

        Event monopolyEventFromDb = EventRepo.findEventById(monopolyEvent.getId());

        assertNotNull(monopolyEventFromDb);

        assertEquals(monopolyEvent.getId(), monopolyEventFromDb.getId());
		assertEquals(newName, monopolyEventFromDb.getName());
        assertEquals(newLocation, monopolyEventFromDb.getLocation());
        assertEquals(newDescription, monopolyEventFromDb.getDescription());
        assertEquals(newDate, monopolyEventFromDb.getDate());
        assertEquals(newStartTime, monopolyEventFromDb.getStartTime());
        assertEquals(newEndTime, monopolyEventFromDb.getEndTime());
        assertEquals(newMaxParticipants, monopolyEventFromDb.getMaxParticipants());
    }

    @Test
    public void testDeleteEvent() {
        Date today = Date.valueOf("2025-02-13");
        Time startTime = Time.valueOf("10:00:00");
        Time endTime = Time.valueOf("12:00:00");
        String name = "Monopoly Event";
        String location = "McGill";
        String description = "Playing Monopoly";
        int maxParticipants = 9;

        Event monopolyEvent = new Event(name,location,description,today,startTime,endTime,maxParticipants,john,monopolyCopy);
        
        monopolyEvent = EventRepo.save(monopolyEvent);

        EventRepo.delete(monopolyEvent);

        //Assert
        Event monopolyEventFromDb = EventRepo.findEventById(monopolyEvent.getId());
        assertNull(monopolyEventFromDb);
    }

    @Test
    public void TestFindEventByOrganizer() {
        Date today = Date.valueOf("2025-02-13");
        Time startTime = Time.valueOf("10:00:00");
        Time endTime = Time.valueOf("12:00:00");
        String name = "Monopoly Event";
        String location = "McGill";
        String description = "Playing Monopoly";
        int maxParticipants = 9;

        Event monopolyEvent = new Event(name,location,description,today,startTime,endTime,maxParticipants,john,monopolyCopy);
        
        monopolyEvent = EventRepo.save(monopolyEvent);

        List<Event> playerEvent = EventRepo.findByOrganizer(john);

        assertFalse(playerEvent.isEmpty());
        assertEquals(1, playerEvent.size());

        Event monopolyEventFromDb = playerEvent.get(0);

        assertNotNull(monopolyEventFromDb);
        assertEquals(monopolyEvent.getOrganizer().getId(), monopolyEventFromDb.getOrganizer().getId());
        assertEquals(monopolyEvent.getGame().getId(), monopolyEventFromDb.getGame().getId());
    }
}