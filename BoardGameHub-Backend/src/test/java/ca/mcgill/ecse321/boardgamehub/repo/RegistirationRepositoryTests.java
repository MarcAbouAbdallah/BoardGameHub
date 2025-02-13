package ca.mcgill.ecse321.boardgamehub.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.Event;
import ca.mcgill.ecse321.boardgamehub.model.Registration;

@SpringBootTest
public class RegistirationRepositoryTests {
    @Autowired
    private RegistrationRepository registrationRepo;
    @Autowired
    private PlayerRepository personRepo;
    @Autowired
    private EventRepository eventRepo;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        registrationRepo.deleteAll();
        personRepo.deleteAll();
        eventRepo.deleteAll();
    }

    @Test
    public void testCreateAndReadRegistration() {
        //Arrange
        Player john = new Player("John",
                                 "john@gmail.com",
                                 "John@123",
                                 true);
        john = personRepo.save(john);

        Event hanging = new Event("hanging",
                                  null, 
                                  null, 
                                  null, 
                                  1, 
                                  john, 
                                  null);
        hanging = eventRepo.save(hanging);

        Registration.Key key = new Registration.Key(john, hanging);
        Registration registration = new Registration(key);
        registration = registrationRepo.save(registration);

        //Act
        Registration RegistrationFromDb = registrationRepo.findRegistrationByKey(key);

        //Assert
        assertNotNull(RegistrationFromDb);
        assertNotNull(RegistrationFromDb.getKey());
        assertNotNull(RegistrationFromDb.getKey().getRegistrant().getId());
        assertEquals(john.getId(), RegistrationFromDb.getKey().getRegistrant().getId());
        assertNotNull(RegistrationFromDb.getKey().getRegisteredEvent());
        assertEquals(hanging.getId(), RegistrationFromDb.getKey().getRegisteredEvent().getId());
}
}