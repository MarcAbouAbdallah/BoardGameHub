package ca.mcgill.ecse321.boardgamehub.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Date;
import java.sql.Time;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.Event;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Registration;

@SpringBootTest
public class RegistrationRepositoryTests {
    @Autowired
    private RegistrationRepository registrationRepo;
    @Autowired
    private PlayerRepository personRepo;
    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GameCopyRepository gameCopyRepo;

    private Player john;
    private Game hangman;
    private GameCopy hangmanCopy;
    private Event hanging;
    private Registration registration;
    private Registration.Key key;

    @BeforeEach
    public void setup() {
        john = new Player("John",
                          "john@gmail.com", 
                          "John@123",
                          true);
        john = personRepo.save(john);

        hangman = new Game("Hangman",
                           4, 
                           2, 
                           "A word game",
                           "https://images.unsplash.com/photo-1567589967685-d431540f735e?q=80&w=1069&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
        hangman = gameRepo.save(hangman);

        hangmanCopy = new GameCopy(hangman, john);
        hangmanCopy = gameCopyRepo.save(hangmanCopy);

        Date eventDate = Date.valueOf("2025-02-20");
        Time startTime = Time.valueOf("12:00:00");
        Time endTime = Time.valueOf("14:00:00");
        hanging = new Event("hanging",
                            "McGill", 
                            "spend some time", 
                            eventDate,
                            startTime,
                            endTime,
                            4, 
                            john, 
                            hangmanCopy);
        hanging = eventRepo.save(hanging);
    }

    @AfterEach
    public void clearDatabase() {
        registrationRepo.deleteAll();
        eventRepo.deleteAll();
        gameCopyRepo.deleteAll();
        personRepo.deleteAll();
        gameRepo.deleteAll();

    }

    @Test
    public void testCreateAndReadRegistration() {
        //Arrange
        key = new Registration.Key(john, hanging);
        registration = new Registration(key);
        registration = registrationRepo.save(registration);
      
        //Act
        Registration RegistrationFromDb = registrationRepo.findRegistrationByKey(key);

        //Assert
        assertNotNull(RegistrationFromDb);
        assertNotNull(RegistrationFromDb.getKey());
        assertNotNull(RegistrationFromDb.getKey().getRegistrant());
        assertEquals(john.getId(), RegistrationFromDb.getKey().getRegistrant().getId());
        assertNotNull(RegistrationFromDb.getKey().getRegisteredEvent());
        assertEquals(hanging.getId(), RegistrationFromDb.getKey().getRegisteredEvent().getId());

    }

    @Test
    public void testDeleteRegistration() {
        key = new Registration.Key(john, hanging);
        registration = new Registration(key);
        registration = registrationRepo.save(registration);
      
        registrationRepo.delete(registration);
        Registration RegistrationFromDb = registrationRepo.findRegistrationByKey(registration.getKey());

        assertNull(RegistrationFromDb);
    }

    @Test
    public void testNonExistingRegistration() {
        //Arrange
        Player avery = new Player("Avery",
                          "avery@gmail.com", 
                          "Avery@123",
                          true);
        avery = personRepo.save(avery);
        
        Registration.Key testKey = new Registration.Key(avery, hanging);

        //Act
        Registration RegistrationFromDb = registrationRepo.findRegistrationByKey(testKey);

        //Assert
        assertNull(RegistrationFromDb);
    }
}