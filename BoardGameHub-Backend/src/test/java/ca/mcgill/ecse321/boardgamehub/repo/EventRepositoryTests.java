package ca.mcgill.ecse321.boardgamehub.repo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;

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
        Date today = Date.valueOf("2025-02-13");
        String name = "Monopoly Event";
        String location = "McGill";
        String description = "Playing Monopoly";
        int maxParticipants = 9;

        Event monopolyEvent = new Event(name,location,description,today,maxParticipants,john,monopolyCopy);
        
        monopolyEvent = EventRepo.save(monopolyEvent);

        //Act
        Event monopolyEventFromDb = EventRepo.findEventById(monopolyEvent.getId());

        //Assert
        assertNotNull(monopolyEventFromDb);

        assertEquals(monopolyEvent.getId(), monopolyEventFromDb.getId());
		assertEquals(name, monopolyEventFromDb.getName());
        assertEquals(location, monopolyEventFromDb.getLocation());
        assertEquals(description, monopolyEventFromDb.getDescription());
        assertEquals(today, monopolyEventFromDb.getDate());
        assertEquals(maxParticipants, monopolyEventFromDb.getMaxParticipants());
        
        assertEquals(john.getId(), monopolyEventFromDb.getOrganizer().getId());
        assertEquals(monopolyCopy.getId(), monopolyEventFromDb.getGame().getId());
    }
}