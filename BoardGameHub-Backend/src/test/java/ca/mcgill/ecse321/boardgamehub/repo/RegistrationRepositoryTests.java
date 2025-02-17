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

    @BeforeEach
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
        Player john = new Player("John",
                                 "john@gmail.com",
                                 "John@123",
                                 true);
        john = personRepo.save(john);

        Game hangman = new Game("Hangman", 
                                 4, 
                                 2, 
                                 "A word game");
        hangman = gameRepo.save(hangman);

        GameCopy hangmanCopy = new GameCopy(true, hangman, john);
        hangmanCopy = gameCopyRepo.save(hangmanCopy);

        Date eventDate = Date.valueOf("2025-02-20");
        Event hanging = new Event("hanging",
                                  "McGill", 
                                  "spend some time", 
                                  eventDate, 
                                  4, 
                                  john, 
                                  hangmanCopy);
        hanging = eventRepo.save(hanging);

        Registration.Key key = new Registration.Key(john, hanging);
        Registration registration = new Registration(key);
        registration = registrationRepo.save(registration);

        System.out.println("Saved Registration: " + registrationRepo.findAll());

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
}