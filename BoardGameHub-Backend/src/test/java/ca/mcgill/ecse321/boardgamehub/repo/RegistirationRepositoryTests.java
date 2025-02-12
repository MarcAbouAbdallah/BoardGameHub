package ca.mcgill.ecse321.boardgamehub.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    public void testCreateAndReadRegistration() {
        //Arrange
        Player john = new Player("John",
                                 "john@gmail.com",
                                 "John@123",
                                 true);

        Registration registration = new Registration(john,);
        registration = registrationRepo.save(registration);

        //Act
        Registration RegistrationFromDb = registrationRepo.findRegistrationByKey(registration.getKey());
}
}